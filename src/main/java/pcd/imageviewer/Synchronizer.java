package pcd.imageviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Manages the synchronization of a collection of viewers.
 * <h2>Implementation notes</h2>
 * This class is necessary mainly due to the complicated operations that are performed for some property changes.
 * When e.g. a resize strategy is changed, that triggers a change in the component's size, leading to an update to the
 * scroll bars. But after that, the viewer performs rescrolling to try to restore the original image center. These
 * multiple scroll operations do not work very well together when two synchrnoized viewers have images of different sizes.
 * 
 * This scenario is managed in the following way. While the resize strategy property is synchronized, all scroll
 * synchronizations are disabled, so size changes do not rewrite the scroll positions of other viewers. Rescrolling is
 * only allowed for the viewer in which the property change was initiated, and after it finished rescrolling, this
 * synchronizer matches the scroll positions of the other viewers.
 * 
 * All this cannot be implemented using property change listeners, because then by the time we'd learn that the
 * resize strategy has changed, a bunch of scrolls would have happened, and it is entirely possible that the rescroll
 * operation of the originating viewer is overwritten by a subsequent scroll synchronization from one of the other viewers.
 * 
 * So the viewer needs a reference the the synchronizer. It announces to the synchronizer that a complex operation
 * begins, and asks permission to perform rescrolling
 * ({@link #resizeStrategyChangedCanIRescroll(ImageViewer)}). The synchronizer uses this
 * mechanism to make note of the viewer in which the change originated, storing it in the variable {@link #leader}, and using
 * it to deny rescrolling to all the other viewers, and to disable scroll synchronization. Then, when the originator finishes the rescroll, it
 * notifies the synchronizer that the operation is complete using the {@link #doneRescrolling(ImageViewer)} function. At this point the
 * synchronizer adjusts the scroll panes of the other viewers to match the originator.
 * @author Kazó Csaba
 */
class Synchronizer {
	private final WeakHashMap<ImageViewer, Void> viewers=new WeakHashMap<ImageViewer, Void>(4);
	
	/*
	 * If there is currently a synchronization update taking place, the viewer which initiated the change is stored
	 * in this variable.
	 */
	private ImageViewer leader=null;
	
	private final ChangeListener scrollChangeListener=new ChangeListener() {
		boolean adjusting=false;
		@Override
		public void stateChanged(ChangeEvent e) {
			if (leader!=null) {
				// ignore every scrolling event during synchronization, it will all be adjusted after the final rescroll
				return;
			}
			if (adjusting) {
				// also ignore changes that our adjustments cause
				return;
			}
			ImageViewer source=null;
			for (ImageViewer viewer: viewers.keySet()) {
				if (viewer.getScrollPane().getHorizontalScrollBar().getModel()==e.getSource() || viewer.getScrollPane().getVerticalScrollBar().getModel()==e.getSource()) {
					source=viewer;
					break;
				}
			}
			if (source==null) throw new AssertionError("Couldn't find the source of the scroll bar change event");
			adjusting=true;
			for (ImageViewer viewer: viewers.keySet()) {
				updateScroll(viewer, source);
			}
			adjusting=false;
		}
	};
	
	public Synchronizer(ImageViewer viewer) {
		viewers.put(viewer, null);
		viewer.getScrollPane().getHorizontalScrollBar().getModel().addChangeListener(scrollChangeListener);
		viewer.getScrollPane().getVerticalScrollBar().getModel().addChangeListener(scrollChangeListener);
	}
	private void updateScroll(ImageViewer viewer, ImageViewer reference) {
		if (reference==viewer) return;
		/*
		 * Note that this method may be called during a resize, before the viewport has had a chance to reshape itself
		 * so we cannot rely on the view rectangle.
		 */
		viewer.getScrollPane().getHorizontalScrollBar().getModel().setValue(reference.getScrollPane().getHorizontalScrollBar().getModel().getValue());
		viewer.getScrollPane().getVerticalScrollBar().getModel().setValue(reference.getScrollPane().getVerticalScrollBar().getModel().getValue());
	}
	
	public void add(ImageViewer viewer) {
		if (viewer.getSynchronizer()==this) return;
		ImageViewer referenceViewer=viewers.keySet().iterator().next();
		
		List<ImageViewer> otherViewers=new ArrayList<ImageViewer>(viewer.getSynchronizer().viewers.keySet());
		for (ImageViewer otherViewer: otherViewers) {
			otherViewer.getSynchronizer().remove(otherViewer);
			otherViewer.setSynchronizer(this);
			viewers.put(otherViewer, null);
			otherViewer.setStatusBarVisible(referenceViewer.isStatusBarVisible());
			otherViewer.setResizeStrategy(referenceViewer.getResizeStrategy());
			otherViewer.setZoomFactor(referenceViewer.getZoomFactor());
			otherViewer.setPixelatedZoom(referenceViewer.isPixelatedZoom());
			otherViewer.setInterpolationType(referenceViewer.getInterpolationType());
			
			updateScroll(otherViewer, referenceViewer);
			otherViewer.getScrollPane().getHorizontalScrollBar().getModel().addChangeListener(scrollChangeListener);
			otherViewer.getScrollPane().getVerticalScrollBar().getModel().addChangeListener(scrollChangeListener);
		}
	}
	
	public void remove(ImageViewer viewer) {
		viewers.remove(viewer);
		viewer.getScrollPane().getHorizontalScrollBar().getModel().removeChangeListener(scrollChangeListener);
		viewer.getScrollPane().getVerticalScrollBar().getModel().removeChangeListener(scrollChangeListener);
	}
	
	
	boolean resizeStrategyChangedCanIRescroll(ImageViewer source) {
		if (leader!=null) {
			// leader is leading an adjustment operation; wait for it to rescroll, and then adjust everything else
			return false;
		}
		leader=source;
		for (ImageViewer viewer: viewers.keySet())
			viewer.setResizeStrategy(source.getResizeStrategy());
		return true;
	}
	boolean zoomFactorChangedCanIRescroll(ImageViewer source) {
		if (leader!=null) {
			// leader is leading an adjustment operation; wait for it to rescroll, and then adjust everything else
			return false;
		}
		leader=source;
		for (ImageViewer viewer: viewers.keySet())
			viewer.setZoomFactor(source.getZoomFactor());
		return true;
	}
	void doneRescrolling(ImageViewer source) {
		if (leader!=source) throw new AssertionError();
		for (ImageViewer otherViewer: viewers.keySet()) {
			if (otherViewer!=leader) {
				((JComponent)otherViewer.getScrollPane().getViewport().getView()).scrollRectToVisible(leader.getScrollPane().getViewport().getViewRect());
				updateScroll(otherViewer, leader);
			}
		}
		leader=null;
	}
	void interpolationTypeChanged(ImageViewer source) {
		for (ImageViewer viewer: viewers.keySet())
			viewer.setInterpolationType(source.getInterpolationType());
	}
	void statusBarVisibilityChanged(ImageViewer source) {
		for (ImageViewer viewer: viewers.keySet())
			viewer.setStatusBarVisible(source.isStatusBarVisible());
	}
	void pixelatedZoomChanged(ImageViewer source) {
		for (ImageViewer viewer: viewers.keySet())
			viewer.setPixelatedZoom(source.isPixelatedZoom());
	}
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import pcd.imageviewer.ImageMouseEvent;
import pcd.imageviewer.ImageMouseMotionListener;
import pcd.data.ImageDataStorage;
import pcd.data.PcdPoint;
import pcd.gui.MainFrame;
import pcd.utils.Constant;

/**
 *
 * @author Noemi Farkas
 * This Listener class is responsible for moving the point inside the overlay.
 */
public class PCDMoveListener implements ImageMouseMotionListener {

    private final MainFrame parentFrame;

    private final ImageDataStorage imgDataStorage;
    private PcdPoint draggedPoint = null;
/**
 * Accepts 2 parameters one MainFram object and one ImageDataStorage object
 * @param parentFrame MainFrame
 * @param imgDataStorage ImageDataStorage
 */
    public PCDMoveListener(MainFrame parentFrame, ImageDataStorage imgDataStorage) {
        this.parentFrame = parentFrame;
        this.imgDataStorage = imgDataStorage;
    }

    @Override
    public void mouseMoved(ImageMouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(ImageMouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(ImageMouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
/**
 * This method changes the points location when dragged, and saves it and updates it in the tables.
 * <p>
 * When dragging on the overlay the method chooses the PCD point closest to the mouse drag, however if the distance is bigger than a certain 
 * threshold then dragged point will be null, and the method will return.
 * @param e ImageMouseEvent done on the GUI
 */
    @Override
    public void mouseDragged(ImageMouseEvent e) {
        
        if (imgDataStorage.getCurrent().isInitialized()) {
            double distance = 100;
            if(draggedPoint != null)
                distance = draggedPoint.distanceToPoint(new PcdPoint(e.getX(), e.getY()));
            
            if (draggedPoint == null) {
                draggedPoint = imgDataStorage.getCurrent().getClosestPoint(e.getX(), e.getY());
            } else if ((SwingUtilities.isLeftMouseButton(e.getOriginalEvent()) && distance > 20) ||
                    (SwingUtilities.isRightMouseButton(e.getOriginalEvent()) && distance > 80)) {
                draggedPoint = null;
                return;
            }
            
            if (SwingUtilities.isLeftMouseButton(e.getOriginalEvent())) {
                draggedPoint.move(e.getX(), e.getY());
            } else if (SwingUtilities.isRightMouseButton(e.getOriginalEvent())) {
                boolean positive = true;
                double angle = 90 - Math.toDegrees(
                        Math.atan((double)
                                   (e.getX() - draggedPoint.x)
                                /  (draggedPoint.y - e.getY())
                        )
                );
                

                if (angle < 0) {
                    positive = false;
                    angle = Math.abs(angle);
                }
                
                if(angle > 90){
                    angle = 180 - angle;
                    positive = false;
                }

                draggedPoint.setAngle(angle);
                draggedPoint.setAnglePositive(positive);

                imgDataStorage.getCurrent().updateAvgStdAngle();
                parentFrame.loadTables();
            }

            imgDataStorage.getCurrent().getOverlay().repaint();
        }
    }

}

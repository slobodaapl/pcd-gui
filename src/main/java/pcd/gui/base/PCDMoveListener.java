/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import javax.swing.SwingUtilities;
import pcd.imageviewer.ImageMouseEvent;
import pcd.imageviewer.ImageMouseMotionListener;
import pcd.data.ImageDataStorage;
import pcd.data.PcdPoint;
import pcd.gui.MainFrame;

/**
 *
 * @author Tibor Sloboda
 * @author Noemi Farkas
 *
 * Listens for dragging mouse events (click and hold + move mouse) over image
 * viewport
 */
public class PCDMoveListener implements ImageMouseMotionListener {

    /**
     * The parent frame from which to listen for clicks. Manipulates tables
     * based on changes.
     */
    private final MainFrame parentFrame;
    /**
     * A reference to instantiated {@link ImageDataStorage}. Used to update and
     * manipulate points in response to clicks.
     */
    private final ImageDataStorage imgDataStorage;
    /**
     * The point currently being dragged
     */
    private PcdPoint draggedPoint = null;

    /**
     * Initializes the listener with the parent frame and
     * {@link ImageDataStorage}
     *
     * @param parentFrame the parent frame, specifically {@link MainFrame}
     * @param imgDataStorage the instantiated image object storage
     */
    public PCDMoveListener(MainFrame parentFrame, ImageDataStorage imgDataStorage) {
        this.parentFrame = parentFrame;
        this.imgDataStorage = imgDataStorage;
    }

    /**
     * Not implemented, currently does nothing.
     *
     * @param e the mouse event
     */
    @Override
    public void mouseMoved(ImageMouseEvent e) {
    }

    /**
     * Not implemented, currently does nothing.
     *
     * @param e the mouse event
     */
    @Override
    public void mouseEntered(ImageMouseEvent e) {
    }

    /**
     * Not implemented, currently does nothing.
     *
     * @param e the mouse event
     */
    @Override
    public void mouseExited(ImageMouseEvent e) {
    }

    /**
     * If left mouse button is used, it drags a point around, changing its
     * coordinates with movement.
     * <p>
     * If right mouse button is used, it adjusts the angle as long as mouse is
     * kept within close proximity of the point. The angle will always be in the -90 to 90 degree range.
     * <p>
     * Reloads the main GUI tables with every change of angles.
     *
     * @param e the mouse event
     */
    @Override
    public void mouseDragged(ImageMouseEvent e) {

        if (imgDataStorage.getCurrent().isInitialized()) {
            double distance = 100;
            if (draggedPoint != null) {
                distance = draggedPoint.distanceToPoint(new PcdPoint(e.getX(), e.getY()));
            }

            if (draggedPoint == null) {
                draggedPoint = imgDataStorage.getCurrent().getClosestPoint(e.getX(), e.getY());
            } else if ((SwingUtilities.isLeftMouseButton(e.getOriginalEvent()) && distance > 20)
                    || (SwingUtilities.isRightMouseButton(e.getOriginalEvent()) && distance > 80)) {
                draggedPoint = null;
                return;
            }

            if (SwingUtilities.isLeftMouseButton(e.getOriginalEvent())) {
                draggedPoint.move(e.getX(), e.getY());
            } else if (SwingUtilities.isRightMouseButton(e.getOriginalEvent())) {
                boolean positive = true;
                double angle = 90 - Math.toDegrees(
                        Math.atan((double) (e.getX() - draggedPoint.x)
                                / (draggedPoint.y - e.getY())
                        )
                );

                if (angle < 0) {
                    positive = false;
                    angle = Math.abs(angle);
                }

                if (angle > 90) {
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

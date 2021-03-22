/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import hu.kazocsaba.imageviewer.ImageMouseEvent;
import hu.kazocsaba.imageviewer.ImageMouseMotionListener;
import pcd.data.ImageDataStorage;
import pcd.data.PcdPoint;
import pcd.gui.MainFrame;

/**
 *
 * @author ixenr
 */
public class PCDMoveListener implements ImageMouseMotionListener {

    private final ImageDataStorage imgDataStorage;
    private final MainFrame parentFrame;
    private PcdPoint draggedPoint = null;

    public PCDMoveListener(MainFrame frame, ImageDataStorage imgDataStorage) {
        parentFrame = frame;
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

    @Override
    public void mouseDragged(ImageMouseEvent e) {
        if (imgDataStorage.getCurrent().isInitialized()) {
            if (draggedPoint == null) {
                draggedPoint = imgDataStorage.getCurrent().getClosestPoint(e.getX(), e.getY());
            } else if (draggedPoint.distanceToPoint(new PcdPoint(e.getX(), e.getY())) > 20) {
                draggedPoint = null;
                return;
            }

            draggedPoint.move(e.getX(), e.getY());
            imgDataStorage.getCurrent().getOverlay().repaint();
        }
    }

}

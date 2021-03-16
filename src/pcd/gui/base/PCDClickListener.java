/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import hu.kazocsaba.imageviewer.ImageMouseClickListener;
import hu.kazocsaba.imageviewer.ImageMouseEvent;
import java.awt.event.MouseEvent;
import pcd.data.ImageProcess;
import pcd.data.PcdPoint;
import pcd.gui.MainFrame;

/**
 *
 * @author ixenr
 */
public class PCDClickListener implements ImageMouseClickListener {

    private final ImageProcess imgProc;
    private final MainFrame parentFrame;
    private PcdPoint selectedPoint = null;

    public PCDClickListener(MainFrame frame, ImageProcess imgProc) {
        parentFrame = frame;
        this.imgProc = imgProc;
    }

    @Override
    public void mouseClicked(ImageMouseEvent e) {
        int button = e.getOriginalEvent().getButton();
        if (imgProc.getCurrentImage().isInitialized()) {
            PcdPoint p = imgProc.getCurrentImage().getClosestPoint(e.getX(), e.getY());
            double distance = p.distanceToPoint(new PcdPoint(e.getX(), e.getY()));
            if (button == MouseEvent.BUTTON1) {
                if (distance >= 50 || p.getType() == -1) {
                    selectedPoint = new PcdPoint(e.getX(), e.getY());
                    selectedPoint.select();
                    imgProc.addPoint(selectedPoint, parentFrame.getNewClickType());
                    parentFrame.loadTables();
                } else if(p.getType() != -1){
                    if(selectedPoint != null)
                        selectedPoint.deselect();
                    selectedPoint = p;
                    selectedPoint.select();
                    imgProc.getCurrentImage().getOverlay().repaint();
                }
            } else if(button == MouseEvent.BUTTON3){
                if(distance <= 50 && p.getType() != -1){
                    imgProc.remPoint(p);
                    parentFrame.loadTables();
                }
            }
        }
    }

}

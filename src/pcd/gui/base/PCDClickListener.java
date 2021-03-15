/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import hu.kazocsaba.imageviewer.ImageMouseClickListener;
import hu.kazocsaba.imageviewer.ImageMouseEvent;
import pcd.data.ImageProcess;
import pcd.data.PcdPoint;

/**
 *
 * @author ixenr
 */
public class PCDClickListener implements ImageMouseClickListener {

    private final ImageProcess imgProc;

    public PCDClickListener(ImageProcess imgProc) {
        this.imgProc = imgProc;
    }

    @Override
    public void mouseClicked(ImageMouseEvent e) {
        PcdPoint p = imgProc.getCurrentImage().getClosestPoint(e.getX(), e.getY());
        double distance = p.distanceToPoint(new PcdPoint(e.getX(), e.getY()));
    }
    
}

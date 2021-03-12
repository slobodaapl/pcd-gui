/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import hu.kazocsaba.imageviewer.ImageMouseClickListener;
import hu.kazocsaba.imageviewer.ImageMouseEvent;

/**
 *
 * @author ixenr
 */
public class PCDClickListener implements ImageMouseClickListener {

    @Override
    public void mouseClicked(ImageMouseEvent e) {
        System.out.println(Integer.toString(e.getX()) + ", " + Integer.toString(e.getY()));
    }
    
}

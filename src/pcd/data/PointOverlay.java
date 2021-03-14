/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import hu.kazocsaba.imageviewer.Overlay;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class PointOverlay extends Overlay {
    
    private ArrayList<Point> points;
    
    PointOverlay(ArrayList<Point> points){
        this.points = points;
    }

    @Override
    public void paint(Graphics2D g, BufferedImage image, AffineTransform transform) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}

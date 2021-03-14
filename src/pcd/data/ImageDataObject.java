/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import pcd.python.PythonProcess;


public class ImageDataObject {
    
    private ArrayList<Point> arrayList;
    private final String imgPath;
    private final PointOverlay layer = new PointOverlay(arrayList);
    private final PythonProcess py;
    
    public ImageDataObject(String path, PythonProcess py){
        this.py = py;
        imgPath = path;
        try{
            arrayList = py.getPoints(imgPath);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public BufferedImage loadImage(){
        try {
            return ImageIO.read(new File(imgPath));
        } catch (IOException e) {
            return null;
        }
    }
    
    public PointOverlay getOverlay(){
        return layer;
    }
    
    //TODO implement this plz
    public Point getClosestPoint(int x, int y){
        return null;
    }
    
}

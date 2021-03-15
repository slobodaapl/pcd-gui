/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import pcd.python.PythonProcess;


public class ImageDataObject {
    
    private ArrayList<Point> arrayList;
    private final String imgPath;
    private final PointOverlay layer = new PointOverlay(arrayList);
    private final PythonProcess py;
    private boolean initialized = false;
    
    public ImageDataObject(String path, PythonProcess py){
        this.py = py;
        imgPath = path;
    }
    
    public void initialize(){
        try{
            arrayList = py.getPoints(imgPath);
        } catch(IOException e){
            e.printStackTrace();
            return;
        }
        
        initialized = true;
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
    
    public boolean fileMatch(String path) throws IOException {
        try{
            return Files.isSameFile(Paths.get(path), Paths.get(imgPath));
        } catch(IOException e){
            throw e;
        }
    }
    
    //TODO implement this plz
    public Point getClosestPoint(int x, int y){
        return null;
    }

    public boolean isInitialized() {
        return initialized;
    }
    
    
    
}

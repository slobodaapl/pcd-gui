/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import pcd.python.PythonProcess;
import pcd.utils.PointUtils;

public class ImageDataObject {

    private final static int WIDTH = 3406;
    private final static int HEIGHT = 2672;

    private ArrayList<PcdPoint> pointList;
    private final String imgPath;
    private PointOverlay layer;
    private final PythonProcess py;
    private boolean initialized = false;
    private final ArrayList<Integer> typeIdentifierList;
    private final ArrayList<String> typeIconList;

    public ImageDataObject(String path, PythonProcess py, ArrayList<Integer> typeIdentifierList, ArrayList<String> typeIconList) {
        this.py = py;
        imgPath = path;
        this.typeIdentifierList = typeIdentifierList;
        this.typeIconList = typeIconList;
    }

    public void initialize() {
        if(initialized)
            return;
        
        try {
            pointList = py.getPoints(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        PointUtils.removeClosestPoints(pointList, 50);

        initialized = true;
        layer = new PointOverlay(pointList, typeIconList, typeIdentifierList);
    }

    public BufferedImage loadImage() {
        try {
            BufferedImage img = ImageIO.read(new File(imgPath));
            if(img.getWidth() != WIDTH || img.getHeight() != HEIGHT)
                return resizeImage(img);
            else
                return img;
        } catch (IOException e) {
            return null;
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage) throws IOException {
        Image resultingImage = originalImage.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public PointOverlay getOverlay() {
        return layer;
    }

    public boolean fileMatch(String path) throws IOException {
        try {
            return Files.isSameFile(Paths.get(path), Paths.get(imgPath));
        } catch (IOException e) {
            throw e;
        }
    }

    //TODO implement this plz
    public PcdPoint getClosestPoint(int x, int y) {
        return PointUtils.getSimpleClosestPoint(x, y, pointList);
    }

    public boolean isInitialized() {
        return initialized;
    }
    
    public void setPointsOpacity(float f){
        layer.setOpacity(f);
        layer.repaint();
    }

    void addPoint(PcdPoint pcdPoint) {
        pointList.add(pcdPoint);
        layer.repaint();
    }

    void remPoint(PcdPoint p) {
        pointList.remove(p);
        layer.repaint();
    }

    public ArrayList<PcdPoint> getPointList() {
        return pointList;
    }

}

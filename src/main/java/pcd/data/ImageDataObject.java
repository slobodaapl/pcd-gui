/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import pcd.utils.AngleWrapper;
import pcd.utils.Constant;
import pcd.utils.PointUtils;

public class ImageDataObject implements Serializable {

    private final static int WIDTH = 3406;
    private final static int HEIGHT = 2672;

    private ArrayList<PcdPoint> pointList;
    private final String imgPath;
    private PointOverlay layer = null;
    private boolean initialized = false;
    private boolean angleInitialized = false;
    private double avgAngle = -1.;
    private double stdAngle = -1.;


    public ImageDataObject(String path) {
        imgPath = path;
    }

    public ImageDataObject(ImageDataObject obj){
        this.pointList = obj.getPointList();
        this.imgPath = obj.getImgPath();
        this.initialized = obj.initialized;
        this.layer = obj.getOverlay();
    }

    public boolean isAngleInitialized() {
        return angleInitialized;
    }

    public void angleInitialize(double angle) {
        if(isAngleInitialized() || angle == -1.)
            return;

        setAvgAngle(angle);
        angleInitialized = true;
    }

    public double getAvgAngle() {
        return avgAngle;
    }

    public double getStdAngle(){
        return stdAngle;
    }

    public void setAvgAngle(double avgAngle) {
        this.avgAngle = avgAngle;
        double sum = 0;

        for (PcdPoint pcdPoint : pointList) {
            sum += Math.pow(pcdPoint.getAngle() - avgAngle, 2);
        }

        sum /= pointList.size();
        this.stdAngle = Math.sqrt(sum);
    }

    public void initialize(ArrayList<PcdPoint> pointlist, ArrayList<Integer> typeIdentifierList, ArrayList<String> typeIconList, ArrayList<String> typeConfigList) {
        if (initialized || pointlist == null) {
            return;
        }

        pointList = pointlist;

        PointUtils.removeClosestPointsSimple(pointList, Constant.FILTER_THRESHOLD_DISTANCE);

        pointList.forEach(pcdPoint -> {
            pcdPoint.setTypeName(typeConfigList.get(typeIdentifierList.indexOf(pcdPoint.getType())));
        });

        initialized = true;
        layer = new PointOverlay(pointList, typeIconList, typeIdentifierList);
        
        if(Constant.DEBUG_MSG)
            System.out.println("Done");
    }

    public void initializeOverlay(ArrayList<Integer> typeIdentifierList, ArrayList<String> typeIconList){
        if(pointList != null){
            layer = new PointOverlay(pointList, typeIconList, typeIdentifierList);
            initialized = true;
        }
    }

    public BufferedImage loadImage() {
        try {
            BufferedImage img = ImageIO.read(new File(imgPath));
            if (img.getWidth() != WIDTH || img.getHeight() != HEIGHT) {
                return resizeImage(img);
            } else {
                return img;
            }
        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("Image loading failed!", e);
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
            ImageDataStorage.getLOGGER().error("File compare failed!", e);
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

    public void setPointsOpacity(float f) {
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

    public ArrayList<Point> getRawPointList(){
        return (ArrayList<Point>) pointList.clone();
    }

    public void setPointList(ArrayList<PcdPoint> points){
        this.pointList = points;
    }

    public String getImageName() {
        return Paths.get(imgPath).getFileName().toString();
    }

    public String getImgPath() {
        return imgPath;
    }

    void mapAngles(AngleWrapper wrapper) {
        for (int i = 0; i < pointList.size(); i++) {
            pointList.get(i).setAngle(wrapper.getAngles().get(i));
            pointList.get(i).setAnglePositive(wrapper.getPositivenessBools().get(i));
            pointList.get(i).x += wrapper.getXoffset().get(i);
            pointList.get(i).y += wrapper.getYoffset().get(i);
        }
    }

    public ArrayList<Integer> getPointTypes() {
        ArrayList<Integer> typeList = new ArrayList<>();
        pointList.forEach(pcdPoint -> {
            typeList.add(pcdPoint.getType());
        });
        return typeList;
    }

    public ArrayList<PcdPoint> getPointList() {
        if(pointList == null)
            return null;

        ArrayList<PcdPoint> newList = new ArrayList<>();
        pointList.forEach(pcdPoint -> {
            newList.add(new PcdPoint(pcdPoint));
        });

        return newList;

    }

    protected PcdPoint getActualPoint(PcdPoint p) {
        for (PcdPoint pcdPoint : pointList) {
            if(pcdPoint.equals(p))
                return pcdPoint;
        }

        return null;
    }

}

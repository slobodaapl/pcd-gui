/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import hu.kazocsaba.imageviewer.Overlay;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class PointOverlay extends Overlay {

    private final ArrayList<PcdPoint> points;
    private final ArrayList<String> typeIconList;
    private final ArrayList<Integer> typeIdentifierList;
    private final ArrayList<Boolean> isIcon = new ArrayList<>();
    private final ArrayList<BufferedImage> imageList = new ArrayList<>();
    private final ArrayList<Color> colorList = new ArrayList<>();
    
    private static final Path ICO_PATH = Paths.get(System.getProperty("user.dir") + "/icons/");

    PointOverlay(ArrayList<PcdPoint> points, ArrayList<String> typeIconList, ArrayList<Integer> typeIdentifierList) {
        this.points = points;
        this.typeIconList = typeIconList;
        this.typeIdentifierList = typeIdentifierList;
        
        for (String string : typeIconList) {
            isIcon.add(!string.contains(".rgb"));
        }
        
        for (int i = 0; i < isIcon.size(); i++) {
            if(isIcon.get(i)){
                try{
                    imageList.add(ImageIO.read(new File(Paths.get(ICO_PATH.toString(), typeIconList.get(i)).toString())));
                    colorList.add(null);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
            else {
                imageList.add(null);
                int r = Integer.parseInt(typeIconList.get(i).substring(0, 2), 16);
                int g = Integer.parseInt(typeIconList.get(i).substring(2, 4), 16);
                int b = Integer.parseInt(typeIconList.get(i).substring(4, 6), 16);
                colorList.add(new Color(r, g, b));
            }
                
        }
    }

    @Override
    public void paint(Graphics2D g, BufferedImage image, AffineTransform transform) {
        double[] bounds = {
            0, 0,
            image.getWidth(), 0,
            image.getWidth(), image.getHeight(),
            0, image.getHeight()};
        
        transform.transform(bounds, 0, bounds, 0, 4);
        
        double topleftX = bounds[0];
        double toprightX = bounds[2];
        double topleftY = bounds[1];
        double bottomleftY = bounds[5];
        
        double scaleX = toprightX / image.getWidth();
        double scaleY = bottomleftY / image.getHeight();
        
        for (PcdPoint point : points) {
            int idx = typeIdentifierList.indexOf((int) point.getType());
            if(idx == -1)
                continue;
            
            Point pt = new Point(point.getX(), point.getY());
            transform.transform(pt, pt);
            
            if(imageList.get(idx) != null){
                g.drawImage(imageList.get(idx), null, (int) pt.getX(), (int) pt.getY());
            }
            else {
                g.setColor(colorList.get(point.getType()));
                Rectangle r = new Rectangle((int) pt.getX(), (int) pt.getY(), (int) (25 * scaleX), (int) (25 * scaleY));
                g.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
            }
        }
        
    }

}

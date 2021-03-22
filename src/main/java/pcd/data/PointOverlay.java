/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import hu.kazocsaba.imageviewer.Overlay;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import pcd.utils.PcdColor;

public class PointOverlay extends Overlay implements Serializable {

    private final ArrayList<PcdPoint> points;
    private final ArrayList<String> typeIconList;
    private final ArrayList<Integer> typeIdentifierList;
    private final ArrayList<Boolean> isIcon = new ArrayList<>();
    private final ArrayList<BufferedImage> imageList = new ArrayList<>();
    private final ArrayList<PcdColor> colorList = new ArrayList<>();
    private float opacity = 1.0f;

    private static final Path ICO_PATH = Paths.get(System.getProperty("user.dir") + "/icons/");

    PointOverlay(ArrayList<PcdPoint> points, ArrayList<String> typeIconList, ArrayList<Integer> typeIdentifierList) {
        this.points = points;
        this.typeIconList = typeIconList;
        this.typeIdentifierList = typeIdentifierList;

        typeIconList.forEach(string -> {
            isIcon.add(!string.contains(".rgb"));
        });

        for (int i = 0; i < isIcon.size(); i++) {
            if (isIcon.get(i)) {
                try {
                    imageList.add(ImageIO.read(new File(Paths.get(ICO_PATH.toString(), typeIconList.get(i)).toString())));
                    colorList.add(null);
                } catch (IOException e) {
                    ImageDataStorage.getLOGGER().error("Adding image failed!", e);
                }
            } else {
                imageList.add(null);
                int r = Integer.parseInt(typeIconList.get(i).substring(0, 2), 16);
                int g = Integer.parseInt(typeIconList.get(i).substring(2, 4), 16);
                int b = Integer.parseInt(typeIconList.get(i).substring(4, 6), 16);
                colorList.add(new PcdColor(r, g, b));
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

        points.forEach(point -> {
            int size = 25;
            if (point.isSelected()) {
                size += 25;
            }
            int idx = typeIdentifierList.indexOf((int) point.getType());
            if (!(idx == -1)) {
                PcdPoint tp = new PcdPoint(point);
                transform.transform(tp, tp);
                
                if (imageList.get(idx) != null) {
                    g.drawImage(imageList.get(idx), null, (int) tp.getX(), (int) tp.getY());
                } else {
                    g.setColor(new PcdColor(colorList.get(point.getType()), opacity));
                    Rectangle r = new Rectangle((int) tp.getX() - (int) (size * scaleX / 2), (int) tp.getY() - (int) (size * scaleX / 2), (int) (size * scaleX), (int) (size * scaleX));
                    g.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
                }
            }
        });

    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

}

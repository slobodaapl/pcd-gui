package pcd.data;

import java.awt.BasicStroke;
import java.awt.Color;
import pcd.imageviewer.Overlay;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pcd.utils.PcdColor;

/**
 *
 * @author Tibor Sloboda
 *
 * The Graphics object responsible for drawing points and angles on the image
 */
public class PointOverlay extends Overlay implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(PointOverlay.class);
    /**
     * The maximum radius to which to draw angle indicator
     */
    private final int CIRCLE_RADIUS = 70;
    /**
     * Reference to list of points from associated {@link ImageDataObject}
     */
    private final ArrayList<PcdPoint> points;
    /**
     * @see ImageDataStorage#typeIdentifierList
     */
    private final ArrayList<Integer> typeIdentifierList;
    /**
     * A list matching to {@link PointOverlay#typeIdentifierList} to determine if id has color or icon
     */
    private final ArrayList<Boolean> isIcon = new ArrayList<>();
    /**
     * List of icon buffered images to display in place of rectangle markers
     */
    private final ArrayList<BufferedImage> imageList = new ArrayList<>();
    /**
     * List of associated colors for markers
     */
    private final ArrayList<PcdColor> colorList = new ArrayList<>();
    /**
     * Opacity of markers
     */
    private float opacity = 1.0f;

    /**
     * Path to icon folder
     */
    private static final Path ICO_PATH = Paths.get("./icons/");

    /**
     * Initializes the PointOverlay with points and icons/colors.
     * Adds RGB colors via {@link PcdColor} where applicable otherwise icon
     * @param points the reference to list of {@link PcdPoint} to draw
     * @param typeIconList the list of icon files or RGB strings
     * @param typeIdentifierList the list of point identifiers to match colors
     */
    PointOverlay(ArrayList<PcdPoint> points, ArrayList<String> typeIconList, ArrayList<Integer> typeIdentifierList) {
        this.points = points;
        this.typeIdentifierList = typeIdentifierList;

        typeIconList.forEach(string -> isIcon.add(!string.contains(".rgb")));

        for (int i = 0; i < isIcon.size(); i++) {
            if (isIcon.get(i)) {
                try {
                    imageList.add(ImageIO.read(new File(Paths.get(ICO_PATH.toString(), typeIconList.get(i)).toString())));
                    colorList.add(null);
                } catch (IOException e) {
                    LOGGER.error("Adding image failed!", e);
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
    
    private void drawInnerDynein(PcdPoint tp, Rectangle r, Graphics2D g){
        
        double stroke_width = r.getWidth() * 0.1;
        
        g.setStroke(new BasicStroke((int) stroke_width));
        g.setColor(Color.black);
        
        g.drawLine(
                (int) (tp.getX() - r.getWidth()/2),
                (int) (tp.getY()),
                (int) (tp.getX() + r.getWidth()/2),
                (int) (tp.getY())
        );
        
        g.setStroke(new BasicStroke(1));
    }
    
    private void drawNoDynein(PcdPoint tp, Rectangle r, Graphics2D g){
        
        double stroke_width = r.getWidth() * 0.1;
        
        g.setStroke(new BasicStroke((int) stroke_width));
        g.setColor(Color.black);
        
        g.drawLine(
                (int) (tp.getX() - r.getWidth()*0.45),
                (int) (tp.getY() - r.getHeight()*0.45),
                (int) (tp.getX() + r.getWidth()*0.45),
                (int) (tp.getY() + r.getHeight()*0.45)
        );
        
        g.drawLine(
                (int) (tp.getX() - r.getWidth()*0.45),
                (int) (tp.getY() + r.getHeight()*0.45),
                (int) (tp.getX() + r.getWidth()*0.45),
                (int) (tp.getY() - r.getHeight()*0.45)
        );
        
        g.setStroke(new BasicStroke(1));
    }
    
    private void drawOuterDynein(PcdPoint tp, Rectangle r, Graphics2D g){
        
        double stroke_width = r.getWidth() * 0.1;
        
        g.setStroke(new BasicStroke((int) stroke_width));
        g.setColor(Color.black);
        
        g.drawLine(
                (int) (tp.getX() - r.getWidth()/2),
                (int) (tp.getY()),
                (int) (tp.getX() - r.getWidth() * 0.75),
                (int) (tp.getY())
        );
        
        g.drawLine(
                (int) (tp.getX() - r.getWidth() * 0.75),
                (int) (tp.getY()),
                (int) (tp.getX() - r.getWidth() * 0.75),
                (int) (tp.getY() + r.getHeight()/2)
        );
        
        g.drawLine(
                (int) (tp.getX() + r.getWidth()/2),
                (int) (tp.getY()),
                (int) (tp.getX() + r.getWidth() * 0.75),
                (int) (tp.getY())
        );
        
        g.drawLine(
                (int) (tp.getX() + r.getWidth() * 0.75),
                (int) (tp.getY()),
                (int) (tp.getX() + r.getWidth() * 0.75),
                (int) (tp.getY() - r.getHeight()/2)
        );
       
        
        g.setStroke(new BasicStroke(1));
    }

    /**
     * Draws a 25 by 25 rectangle marker for every point with respective color based on type.
     * This size expands to 75 by 75 when point is selected.
     * <p>
     * Contains affine transformation to scale points based on zoom and position on the image.
     * Also draws angle in respect to horizontal plane.
     * @param g the {@link Graphics2D} plane to draw on
     * @param image the {@link BufferedImage} based on which transformations happen
     * @param transform affine transformations to scale and adjust point coordinates based on viewport
     */
    @Override
    public void paint(Graphics2D g, BufferedImage image, AffineTransform transform) {
        double[] bounds = {
            0, 0,
            image.getWidth(), 0,
            image.getWidth(), image.getHeight(),
            0, image.getHeight()};

        transform.transform(bounds, 0, bounds, 0, 4);

        double toprightX = bounds[2];

        double scaleX = toprightX / image.getWidth();

        points.forEach(point -> {
            int size = 25;
            if (point.isSelected()) {
                size += 50;
            }
            
            int idx = typeIdentifierList.indexOf(point.getType());
            int dyn = point.getDynein();
            
            if (!(idx == -1)) {
                PcdPoint tp = new PcdPoint(point);
                Rectangle rc = new Rectangle((int) tp.getX() - (int) (size * scaleX / 2), (int) tp.getY() - (int) (size * scaleX / 2), (int) (size * scaleX), (int) (size * scaleX));
                transform.transform(tp, tp);

                if (imageList.get(idx) != null) {
                    g.drawImage(imageList.get(idx), null, (int) tp.getX(), (int) tp.getY());
                } else {
                    g.setColor(new PcdColor(colorList.get(point.getType()), opacity));
                    Rectangle r = new Rectangle((int) tp.getX() - (int) (size * scaleX / 2), (int) tp.getY() - (int) (size * scaleX / 2), (int) (size * scaleX), (int) (size * scaleX));
                    g.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
                }

                if (tp.getAngle() >= 0) {
                    g.setColor(Color.cyan);
                    g.drawLine(tp.x, tp.y, tp.x + (int) (CIRCLE_RADIUS * (scaleX)), tp.y);
                    g.setColor(Color.yellow);
                    g.drawLine(tp.x, tp.y, tp.x + (int) (CIRCLE_RADIUS * (scaleX) * Math.cos(tp.getAngle() * 0.0174532925)), tp.y + (tp.isAnglePositive() ? -1 : 1) * ((int) (CIRCLE_RADIUS * (scaleX) * Math.sin(tp.getAngle() * 0.0174532925))));
                }
                
                
                switch(dyn){
                    case 0:
                        break;
                    case 1:
                        drawInnerDynein(tp, rc, g);
                        drawOuterDynein(tp, rc, g);
                        break;
                    case 2:
                        drawInnerDynein(tp, rc, g);
                        break;
                    case 3:
                        drawOuterDynein(tp, rc, g);
                        break;
                    case 4:
                        drawNoDynein(tp, rc, g);
                        break;
                }
                
            }
        });

    }

    /**
     * Changes the opacity of markers by setting opacity.
     * @param opacity the opacity to set markers to, from 0 to 1.0
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

}

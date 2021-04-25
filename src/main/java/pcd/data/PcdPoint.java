package pcd.data;

import java.awt.Point;
import java.io.Serializable;

/**
 *
 * @author Tibor Sloboda
 *
 * Based on {@link Point} with extensions, but used similarly. It is displayed
 * over an image and can be updated, and then saved to be used as annotations
 * for machine learning.
 *
 */
public final class PcdPoint extends Point implements Serializable {

    /**
     * The type of the cilia, matching ID from config.
     */
    private int cilia_type = 0;
    /**
     * Whether or not the point should appear larger when drawn, due to
     * selection.
     */
    private boolean selected = false;
    /**
     * The confidence score, as returned by the Python process. It is by default
     * 1.0 in the case it is added by a doctor.
     */
    private double score = 1.0;
    /**
     * The angle of the central pair of microtubules in a cilium, important for
     * establishing a diagnosis based on quantitative analysis by the software.
     * The angle is always only in the range 0 to 90.
     */
    private double angle = -1.0;
    /**
     * Whether the angle is in the 4th of 1st quadrant. 1st if true.
     */
    private boolean anglePositive = false;
    /**
     * The name of the cilium type associated with its ID based on config.
     */
    private String typeName = "";

    /**
     * Initializes the point with a type and its coordinates
     *
     * @param x the x coordinate, starting from left
     * @param y the y coordinate, starting from the top
     * @param type the cilium denoted in config
     */
    public PcdPoint(int x, int y, int type) {
        this(x, y);
        cilia_type = type;
    }

    /**
     * Uses the basic {@link Point} initializer
     */
    public PcdPoint() {
        super();
    }

    /**
     * Basic {@link Point} initializer with coordinates
     *
     * @param x the x coordinate, starting from left
     * @param y the y coordinate, starting from the top
     */
    public PcdPoint(int x, int y) {
        super(x, y);
    }

    /**
     * A copy initializer
     *
     * @param p the point used to initialize new point
     */
    public PcdPoint(PcdPoint p) {
        this.x = (int) p.getX();
        this.y = (int) p.getY();
        this.cilia_type = p.getType();
        this.typeName = p.getTypeName();
        this.angle = p.getAngle();
        this.score = p.getScore();
        this.anglePositive = p.isAnglePositive();
        this.selected = p.isSelected();
    }

    /**
     * Calculates distance to another {@link PcdPoint}
     *
     * @param p the to calculate distance to from this one
     * @return the distance to the point
     */
    public double distanceToPoint(PcdPoint p) {
        return Math.sqrt(Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2));
    }

    /**
     * Checks whether point is selected
     *
     * @return true if selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set {@link PcdPoint#selected} to true
     */
    public void select() {
        selected = true;
    }

    /**
     * Set {@link PcdPoint#selected} to false
     */
    public void deselect() {
        selected = false;
    }

    /**
     * Retrieves the type id of the point corresponding to the cilia
     *
     * @return the id of the type
     */
    public int getType() {
        return cilia_type;
    }

    /**
     * Sets point corresponding to cilia to a new one
     *
     * @param cilia_type the id to update the type to
     */
    public void setType(int cilia_type) {
        this.cilia_type = cilia_type;
    }

    /**
     * Sets x coordinate to new value
     *
     * @param x the value with which to update the x coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets y coordinate to new value
     *
     * @param y the value with which to update the y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Checks whether an object is a point and if the points are the same by
     * comparing their x coordinate, y coordinate and type id.
     *
     * @param obj the object to compare point to
     * @return true if object is a point and matches this point's coordinates
     * and type, otherwise lets Object class compare references
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PcdPoint) {
            PcdPoint pt = (PcdPoint) obj;
            return (x == pt.x) && (y == pt.y) && (cilia_type == pt.cilia_type);
        }
        return super.equals(obj);
    }

    /**
     * Custom hash-code that has a lower collision rate than default hash-code
     * method
     *
     * @return the hash-code
     */
    @Override
    public int hashCode() {
        int hash = y * 3800 + x;
        hash = 73 * (hash + this.cilia_type);
        return hash;
    }

    /**
     * Sets the score of the point.
     *
     * @param score the score to set the point to
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Retrieves the score of the point
     *
     * @return the score of the point
     */
    public double getScore() {
        return score;
    }

    /**
     * Retrieves the name of the type of the point
     *
     * @return the name of the type of the point or empty string if not set
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Sets the point type name
     *
     * @param typeName the type name to set the point to
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Retrieves the angle of the point
     *
     * @return the angle of the point, or -1. by default if never set
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Sets the angle of the point
     *
     * @param angle the angle to set the point to
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Determined whether the angle is in 1st quadrant.
     *
     * @return true if in 1st quadrant, false if in 4th quadrant
     */
    public boolean isAnglePositive() {
        return anglePositive;
    }

    /**
     * Sets the quadrant of the angle.
     *
     * @param anglePos the 'positiveness' of the angle where true corresponds to
     * 1st quadrant and false to 4th quadrant
     */
    public void setAnglePositive(boolean anglePos) {
        anglePositive = anglePos;
    }

}

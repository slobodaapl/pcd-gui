package pcd.data;

import java.awt.Point;
import java.io.Serializable;

public final class PcdPoint extends Point implements Serializable {

    private int cilia_type = 0;
    private boolean selected = false;

    public PcdPoint(int x, int y, int type) {
        super(x, y);
        cilia_type = type;
    }

    public PcdPoint() {
        super();
    }

    public PcdPoint(int x, int y) {
        super(x, y);
    }

    public PcdPoint(PcdPoint p) {
        this.x = (int) p.getX();
        this.y = (int) p.getY();
        this.cilia_type = p.getType();
    }

    public double distanceToPoint(PcdPoint p) {
        return Math.sqrt(Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2));
    }

    public boolean isSelected() {
        return selected;
    }

    public void select() {
        selected = true;
    }

    public void deselect() {
        selected = false;
    }

    public int getType() {
        return cilia_type;
    }

    public void setType(int cilia_type) {
        this.cilia_type = cilia_type;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PcdPoint) {
            PcdPoint pt = (PcdPoint) obj;
            return (x == pt.x) && (y == pt.y) && (cilia_type == pt.cilia_type);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.cilia_type;
        return hash;
    }

}

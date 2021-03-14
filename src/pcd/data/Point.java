package pcd.data;


public final class Point {
    
    private short cilia_type = 0;
    private int X = 0;
    private int Y = 0;
    
    public double distanceToPoint(Point p){
        return Math.sqrt(Math.pow((double) p.getX() - (double) X, 2) + Math.pow((double) p.getY() - (double) Y, 2));
    }

    public short getType() {
        return cilia_type;
    }

    public void setType(short cilia_type) {
        this.cilia_type = cilia_type;
    }

    public int getX() {
        return X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public int getY() {
        return Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }
    
}

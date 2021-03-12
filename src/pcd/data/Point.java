package pcd.data;


public final class Point {
    
    private short cilia_type = 0;
    private double X = 0.;
    private double Y = 0.;
    
    public double distanceToPoint(Point p){
        return Math.sqrt(Math.pow(p.getX() - X, 2) + Math.pow(p.getY() - Y, 2));
    }

    public short getType() {
        return cilia_type;
    }

    public void setType(short cilia_type) {
        this.cilia_type = cilia_type;
    }

    public double getX() {
        return X;
    }

    public void setX(double X) {
        this.X = X;
    }

    public double getY() {
        return Y;
    }

    public void setY(double Y) {
        this.Y = Y;
    }
    
}

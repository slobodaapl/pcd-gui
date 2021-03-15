package pcd.data;


public final class PcdPoint {
    
    private short cilia_type = 0;
    private int X = 0;
    private int Y = 0;
    
    public PcdPoint(int x, int y){
        X = x;
        Y = y;
    }
    
    public PcdPoint(int x, int y, short type){
        X = x;
        Y = y;
        cilia_type = type;
    }
    
    public PcdPoint(){}
    
    public double distanceToPoint(PcdPoint p){
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

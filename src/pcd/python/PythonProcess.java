package pcd.python;

// Python process not yet implemented for testing purposes
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import pcd.data.Point;

public class PythonProcess {
    
    TCPServer server;
    ProcessBuilder pb;
    boolean debug;
    
    public PythonProcess(int port, boolean debug) {
        this.debug = debug;
        if (!debug) {
            initProcess();
            server = new TCPServer(port, pb);
        }
    }
    
    public PythonProcess(boolean debug) {
        this.debug = debug;
        if (!debug) {
            initProcess();
            server = new TCPServer(5000, pb);
        }
    }
    
    private void initProcess(){
        pb = new ProcessBuilder("python/main.exe"); 
        pb.directory(new File(System.getProperty("user.dir")  + "/python"));
    }
    
    public ArrayList<Point> getPoints(String imgPath) throws IOException {
        if(debug){
            return getPoints_debug();
        }
        
        String t;
        ArrayList<Point> pointList = new ArrayList<>();
        
        try {
            server.send(imgPath);
            t = server.receive();
        } catch (IOException e) {
            throw e;
        }
        
        String[] points = t.split(";");
        
        for (String point : points) {
            Point point1 = new Point();
            String[] data = point.split(",");
            point1.setType(Short.parseShort(data[2]));
            point1.setX(Integer.parseInt(data[0]));
            point1.setY(Integer.parseInt(data[1]));
            pointList.add(point1);
        }
        
        return pointList;
    }
    
    private ArrayList<Point> getPoints_debug() {
        ArrayList<Point> debugPoints = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            Point p = new Point();
            int randtype = ThreadLocalRandom.current().nextInt(0, 2 + 1);
            int randx = ThreadLocalRandom.current().nextInt(100, 3000 + 1);
            int randy = ThreadLocalRandom.current().nextInt(100, 2000 + 1);
            p.setType((short) randtype);
            p.setX(randx);
            p.setY(randy);
            debugPoints.add(p);
        }
        
        return debugPoints;
    }
}

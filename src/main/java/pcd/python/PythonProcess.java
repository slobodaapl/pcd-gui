package pcd.python;

// Python process not yet implemented for testing purposes
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import pcd.data.ImageDataStorage;
import pcd.data.PcdPoint;

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

    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    public PythonProcess(boolean debug) {
        this.debug = debug;
        if (!debug) {
            initProcess();
            server = new TCPServer(5000, pb);
        }
    }

    private void initProcess() {
        pb = new ProcessBuilder("python/main.exe");
        pb.directory(new File(System.getProperty("user.dir") + "/python"));
    }

    synchronized public ArrayList<PcdPoint> getPoints(String imgPath, javax.swing.JProgressBar progressBar, int count) throws IOException {
        ArrayList<PcdPoint> points;
        if (debug) {
            points = _getPoints_debug();
        } else {
            points = _getPoints(imgPath);
        }
        
        int progress = progressBar.getValue();
        int max = progressBar.getMaximum();
        int increment = max / count;
        progressBar.setValue(progress + increment);
        
        return points;
    }

    synchronized public ArrayList<PcdPoint> getPoints(String imgPath) throws IOException {
        ArrayList<PcdPoint> points;
        if (debug) {
            points = _getPoints_debug();
        } else {
            points = _getPoints(imgPath);
        }

        return points;
    }

    synchronized private ArrayList<PcdPoint> _getPoints(String imgPath) throws IOException {
        String t;
        ArrayList<PcdPoint> pointList = new ArrayList<>();

        try {
            server.send(imgPath);
            t = server.receive();
        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("Getting points failed!", e);
            throw e;
        }

        String[] points = t.substring(1).split(";");

        for (String point : points) {
            PcdPoint point1 = new PcdPoint();
            String[] data = point.split(",");
            point1.setType(Short.parseShort(data[2]));
            point1.setX(Integer.parseInt(data[0]));
            point1.setY(Integer.parseInt(data[1]));
            point1.setScore(Double.parseDouble(data[3]));
            pointList.add(point1);
        }

        return pointList;
    }

    synchronized private ArrayList<PcdPoint> _getPoints_debug() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            ImageDataStorage.getLOGGER().error("Thread interrupted", ex);
        }
        ArrayList<PcdPoint> debugPoints = new ArrayList<>();

        for (int i = 0; i < 300; i++) {
            int randtype = ThreadLocalRandom.current().nextInt(0, 2 + 1);
            int randx = ThreadLocalRandom.current().nextInt(100, 3000 + 1);
            int randy = ThreadLocalRandom.current().nextInt(100, 2000 + 1);
            double rands = ThreadLocalRandom.current().nextDouble(0., 1.);
            PcdPoint p = new PcdPoint(randx, randy);
            p.setType((short) randtype);
            p.setScore(rands);
            debugPoints.add(p);
        }

        return debugPoints;
    }
}

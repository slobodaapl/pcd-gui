package pcd.utils;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.lang3.ArrayUtils;
import pcd.data.PcdPoint;

// Closest point algorithm from https://www.geeksforgeeks.org/closest-pair-of-points-onlogn-implementation/
public final class PointUtils {

    private PointUtils() {
    }

    public static PcdPoint getSimpleClosestPoint(int x, int y, ArrayList<PcdPoint> points) {
        double min = Double.MAX_VALUE;
        PcdPoint clickPt = new PcdPoint(x, y, -1);
        PcdPoint minPt = new PcdPoint(x, y, -1);

        for (PcdPoint point : points) {
            double dist = point.distanceToPoint(clickPt);
            if (dist < min) {
                minPt = point;
                min = dist;
            }
        }

        if (clickPt.distanceToPoint(minPt) <= 50) {
            return minPt;
        }

        return clickPt;
    }

    public static PcdPoint getClosestPoint(int x, int y, ArrayList<PcdPoint> points) {

        if (points.size() <= 1) {
            return points.get(0);
        }

        ArrayList<PcdPoint> pointsCopy = new ArrayList<>(points);
        pointsCopy.add(new PcdPoint(x, y, (short) -1));
        PcdPoint[] Px = sortByX(pointsCopy);
        PcdPoint[] Py = sortByY(pointsCopy);
        PcdPoint[] result = new PcdPoint[2];

        try {
            result = PointUtils.closestUtil(Px, Py, pointsCopy.size());
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        if (!(result[0].getType() == -2 || result[1].getType() == -2)) {
            if (result[0].getType() == -1) {
                return result[1];
            } else if (result[1].getType() == -1) {
                return result[0];
            }
        }

        return new PcdPoint(x, y, (short) -1);
    }

    public static void removeClosestPoints(ArrayList<PcdPoint> points, double threshold) {
        if (points.size() <= 1) {
            return;
        }

        boolean repeat = true;

        while (repeat) {
            ArrayList<PcdPoint> pointsCopy = new ArrayList<>(points);
            PcdPoint[] Px = sortByX(pointsCopy);
            PcdPoint[] Py = sortByY(pointsCopy);

            PcdPoint[] result = PointUtils.closestUtil(Px, Py, points.size());

            if (result[1].distanceToPoint(result[0]) < threshold) {
                points.remove(result[1]);
            } else {
                repeat = false;
            }
        }
    }

    public static void removeClosestPointsSimple(ArrayList<PcdPoint> points, double threshold) {
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                if (points.get(i).distanceToPoint(points.get(j)) < threshold) {
                    points.remove(j);
                }
            }
        }
    }

    private static PcdPoint[] closestUtil(PcdPoint[] Px, PcdPoint[] Py, int n) {

        if (n <= 3) {
            return PointUtils.bruteForce(Px, n);
        }

        int mid = n / 2;
        PcdPoint midPoint = Px[mid];

        PcdPoint[] Pyl = new PcdPoint[mid];
        PcdPoint[] Pyr = new PcdPoint[n - mid];

        int li = 0, ri = 0;
        for (int i = 0; i < n; i++) {
            if (Py[i].getX() <= midPoint.getX() && li < mid) {
                Pyl[li++] = Py[i];
            } else {
                Pyr[ri++] = Py[i];
            }
        }

        PcdPoint[] dla = closestUtil(Px, Pyl, mid);
        PcdPoint[] dra = closestUtil(ArrayUtils.subarray(Px, mid, n), Pyr, n - mid);

        double dl = dla[0].distanceToPoint(dla[1]);
        double dr = dra[0].distanceToPoint(dra[1]);

        double d = min(dl, dr);

        PcdPoint[] strip = new PcdPoint[n];
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (abs(Py[i].getX() - midPoint.getX()) < d) {
                strip[j] = Py[i];
                j++;
            }
        }

        if (j <= 1) {
            if (dl < dr) {
                return dla;
            } else {
                return dra;
            }
        }

        return PointUtils.stripClosest(strip, j, d);
    }

    private static PcdPoint[] bruteForce(PcdPoint P[], int n) {
        double min = Float.MAX_VALUE;
        PcdPoint one = P[0];
        PcdPoint two = P[1];

        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                double dist = P[i].distanceToPoint(P[j]);
                if (dist < min) {
                    min = dist;
                    one = P[i];
                    two = P[j];
                }
            }
        }

        return new PcdPoint[]{one, two};
    }

    private static PcdPoint[] stripClosest(PcdPoint strip[], int size, double d) {
        double min = d;
        PcdPoint one = strip[0];
        PcdPoint two = strip[1];

        for (int i = 0; i < size; ++i) {
            for (int j = i + 1; (j < size) && ((strip[j].getY() - strip[i].getY()) < min); ++j) {
                double dist = strip[i].distanceToPoint(strip[j]);
                if (dist < min) {
                    min = dist;
                    one = strip[i];
                    two = strip[j];
                }
            }
        }

        return new PcdPoint[]{one, two};
    }

    private static PcdPoint[] sortByX(ArrayList<PcdPoint> points) {
        Collections.sort(points, (PcdPoint p1, PcdPoint p2) -> (int) (p1.getX() - p2.getX()));
        return points.toArray(new PcdPoint[points.size()]);
    }

    private static PcdPoint[] sortByY(ArrayList<PcdPoint> points) {
        Collections.sort(points, (PcdPoint p1, PcdPoint p2) -> (int) (p1.getY() - p2.getY()));
        return points.toArray(new PcdPoint[points.size()]);
    }

//    public static void main(String[] args) {
//        ArrayList<PcdPoint> pointList = new ArrayList<>();
//
//        pointList.add(new PcdPoint(100, 100, 0));
//        pointList.add(new PcdPoint(200, 200, 0));
//        PcdPoint p = PointUtils.getClosestPoint(180, 180, pointList);
//
//        p.setX(999);
//        
//        System.out.println(pointList.toString());
//    }
}

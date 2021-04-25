package pcd.utils;

import pcd.data.PcdPoint;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility to find the closest points and filter points that are too close to
 * each other when first retrieving them from Python.
 * @author Tibor Sloboda
 */
public final class PointUtils {

    private PointUtils() {
    }

    /**
     * Simple brute-force algorithm to find the closest point in the array of points.
     * Point size doesn't exceed 400 so using brute force here isn't slow.
     * @param x the x coordinate for which to find closest point
     * @param y the y coordinate for which to find closest point
     * @param points the list of {@link PcdPoint}
     * @return the closest point
     */
    public static PcdPoint getSimpleClosestPoint(int x, int y, List<PcdPoint> points) {
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

    /**
     * Removes all points that are within a certain threshold, in order of occurrence
     * @param points the points to filter
     * @param threshold the maximum distance two points can be apart for one of them to be filtered out
     */
    public static void removeClosestPointsSimple(ArrayList<PcdPoint> points, double threshold) {
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                if (points.get(i).distanceToPoint(points.get(j)) < threshold) {
                    points.remove(j);
                }
            }
        }
    }

}

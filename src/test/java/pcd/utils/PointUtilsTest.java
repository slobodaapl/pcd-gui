/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import pcd.data.PcdPoint;

/**
 *
 * @author ixenr
 */
public class PointUtilsTest {
    
    ArrayList<PcdPoint> pcdList = new ArrayList<>();
    
    public PointUtilsTest() {
    }
    
    @BeforeEach
    public void setUp() {
        pcdList.clear();
        
        int firstloop = ThreadLocalRandom.current().nextInt(40, 250 + 1);
        
        for (int i = 0; i < firstloop; i++) {
            int x = ThreadLocalRandom.current().nextInt(0, 3406 + 1);
            int y = ThreadLocalRandom.current().nextInt(0, 2604 + 1);
            pcdList.add(new PcdPoint(x, y));
        }
        
        pcdList.add(new PcdPoint(1000, 1001));
        
        for (int i = 0; i < 299 - firstloop; i++) {
            int x = ThreadLocalRandom.current().nextInt(0, 3406 + 1);
            int y = ThreadLocalRandom.current().nextInt(0, 2604 + 1);
            pcdList.add(new PcdPoint(x, y));
        }
        
        
    }

    @org.junit.jupiter.api.RepeatedTest(10)
    public void testGetClosestPoint() {
        PcdPoint p = new PcdPoint(1000, 1000);
        double d = Double.MAX_VALUE;
        
        try{
            d = p.distanceToPoint(PointUtils.getClosestPoint(1000, 1000, pcdList));
        } catch(IndexOutOfBoundsException e){
            fail("Index out of bounds");
        } catch (NullPointerException e){
            fail("Agorithm didn't return valid PcdPoint");
        }
        assertTrue(d <= 1.0);
    }

    @org.junit.jupiter.api.Test
    public void testRemoveClosestPoints() {
    }
    
}

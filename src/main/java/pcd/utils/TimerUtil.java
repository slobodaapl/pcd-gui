/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;


/**
 *
 * @author ixenr
 */
public final class TimerUtil {
    
    private static long starttime;
    private static long endtime;
    private static long interval;
    
    static {
        starttime = System.nanoTime();
        endtime = System.nanoTime();
        interval = endtime - starttime;
    }
    
    private TimerUtil(){
        
    }
    
    public static void start(){
        starttime = System.nanoTime();
    }
    
    public static void end(){
        endtime = System.nanoTime();
        interval = endtime - starttime;
    }
    
    public static double elapsedSeconds(){
        return interval / 1000000000.;
    }
    
    public static long elapsedNano(){
        return interval;
    }
    
    
}

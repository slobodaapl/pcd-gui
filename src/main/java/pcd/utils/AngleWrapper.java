/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import java.util.ArrayList;

/**
 *
 * @author Tibor Sloboda
 */
public class AngleWrapper {
    
    /**
     * A list of found angles by python
     */
    private final ArrayList<Double> angles;
    /**
     * A list of booleans denoting whether the angle is in the 1st quadrant(true) or 4th quadrant(false)
     */
    private final ArrayList<Boolean> positivenessBools;
    /**
     * A list of x coordinate offsets, produced by Python, which helps center points
     */
    private final ArrayList<Integer> xoffset;
    /**
     * A list of y coordinate offsets, produced by Python, which helps center points
     */
    private final ArrayList<Integer> yoffset;

    /**
     * Initializes the wrapper to pass wrapped values in functions
     * @param angles a list of found angles by python
     * @param positivenessBools a list of booleans denoting 'positiveness' as described in {@link AngleWrapper#positivenessBools}
     * @param xoffset a list of x coordinate offsets, produced by Python, which helps center points
     * @param yoffset a list of y coordinate offsets, produced by Python, which helps center points
     */
    public AngleWrapper(ArrayList<Double> angles, ArrayList<Boolean> positivenessBools, ArrayList<Integer> xoffset, ArrayList<Integer> yoffset) {
        this.angles = angles;
        this.positivenessBools = positivenessBools;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
    }

    /**
     * 
     * @return the angles in the wrapper
     */
    public ArrayList<Double> getAngles() {
        return angles;
    }

    /**
     * 
     * @return the positiveness boolean values in the wrapper
     */
    public ArrayList<Boolean> getPositivenessBools() {
        return positivenessBools;
    }

    /**
     * 
     * @return the list of x coordinate offsets to help center points
     */
    public ArrayList<Integer> getXoffset() {
        return xoffset;
    }

    /**
     * 
     * @return the list of y coordinate offsets to help center points
     */
    public ArrayList<Integer> getYoffset() {
        return yoffset;
    }
    
}

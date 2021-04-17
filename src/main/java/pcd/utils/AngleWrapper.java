/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import java.util.ArrayList;

/**
 *
 * @author ixenr
 */
public class AngleWrapper {
    
    private final ArrayList<Double> angles;
    private final ArrayList<Boolean> positivenessBools;
    private final ArrayList<Integer> xoffset;
    private final ArrayList<Integer> yoffset;

    public AngleWrapper(ArrayList<Double> angles, ArrayList<Boolean> positivenessBools, ArrayList<Integer> xoffset, ArrayList<Integer> yoffset) {
        this.angles = angles;
        this.positivenessBools = positivenessBools;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
    }

    public ArrayList<Double> getAngles() {
        return angles;
    }

    public ArrayList<Boolean> getPositivenessBools() {
        return positivenessBools;
    }

    public ArrayList<Integer> getXoffset() {
        return xoffset;
    }

    public ArrayList<Integer> getYoffset() {
        return yoffset;
    }
    
}

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

    public AngleWrapper(ArrayList<Double> angles, ArrayList<Boolean> positivenessBools) {
        this.angles = angles;
        this.positivenessBools = positivenessBools;
    }

    public ArrayList<Double> getAngles() {
        return angles;
    }

    public ArrayList<Boolean> getPositivenessBools() {
        return positivenessBools;
    }
    
}

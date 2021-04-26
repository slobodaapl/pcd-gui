/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.io.Serializable;

/**
 * This class is responsible for providing colours for different type of PCD points in the GUI.
 * @author Noemi Farkas
 */
public class PcdColor extends Color implements Serializable {

    
    public PcdColor(int i, int i1, int i2) {
        super(i, i1, i2);
    }

    public PcdColor(int i, int i1, int i2, int i3) {
        super(i, i1, i2, i3);
    }

    public PcdColor(int i) {
        super(i);
    }

    public PcdColor(int i, boolean bln) {
        super(i, bln);
    }

    public PcdColor(float f, float f1, float f2) {
        super(f, f1, f2);
    }

    public PcdColor(float f, float f1, float f2, float f3) {
        super(f, f1, f2, f3);
    }

    public PcdColor(ColorSpace cs, float[] floats, float f) {
        super(cs, floats, f);
    }

    public PcdColor(PcdColor pcc, float alpha) {
        super(pcc.getRed(), pcc.getGreen(), pcc.getBlue(), (int) (alpha * 255));
    }
    /**
     * This method is responsible for providing Luminance for part in the GUI.
     * @return double value containing the luminance
     */
    public double getLuminance(){
        return (getRed() * 0.2126 + getBlue() * 0.0722 + getGreen() * 0.7152) / 255.;
    }

}

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
 *
 * @author ixenr
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
    
    public PcdColor(PcdColor pcc, float alpha){
        super(pcc.getRed(), pcc.getGreen(), pcc.getBlue(), (int) (alpha * 255));
    }
    
}

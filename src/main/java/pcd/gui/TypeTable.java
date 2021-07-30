/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import pcd.data.ImageDataStorage;
import pcd.data.PcdPoint;
import pcd.utils.Constant;
import pcd.utils.PcdColor;

/**
 *
 * @author Noemi Farkas
 * 
 * TypeTable class extends the JTable  and makes a table where the first column depends on the ImageDataStorage's 
 */
public class TypeTable extends JTable {

    private final ImageDataStorage imgProc;
   /**
    * The constructor has a super method which is extended by Jtable's constructor
    * 
    * @param p ImageDataStorage object contains all the information about the image data.
    */
    public TypeTable(ImageDataStorage p) {
        super();
        imgProc = p;
    }

    /**
     * prepareRenderer method gives different colours to a specific rows and columns in the table 
     * <p>
     * @param renderer TableCellRenderer is object that allows modification of the table cells.
     * @param row specific row in the table
     * @param col specific column in the table.
     * @return  Component variable is where the changes are saved in the Jtable.
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        Component comp = super.prepareRenderer(renderer, row, col);
        PcdPoint p = (PcdPoint) getModel().getValueAt(row, 0);
       
        if (col != 1) {
            if (this.isRowSelected(row)) {
                comp.setBackground(new Color(255 - 35, 255 - 35, 255 - 45));
                comp.setForeground(Color.BLACK);
            } else if (p.getScore() <= Constant.SCORE_THRESHOLD) {
                comp.setForeground(Color.BLACK);
                comp.setBackground(Color.YELLOW);
            } else {
                comp.setForeground(Color.BLACK);
                comp.setBackground(Color.WHITE);
            }
            return comp;
        }

        PcdColor clr = imgProc.getColor(p);

        if (clr != null) {
            comp.setBackground(clr);
        }

        return comp;
    }

}

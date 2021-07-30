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
import pcd.utils.PcdColor;

/**
 *
 * @author Noemi Farkas
 * 
 * TypeCountTable class extends the JTable and sets the table background according to data. 
 */
public final class TypeCountTable extends JTable {

    private final ImageDataStorage imgProc;
  /**
    * The constructor has a super method which is extended by Jtable's constructor
    * <p>
    * @param p ImageDataStorage object contains all the information about the image data.
    */
    public TypeCountTable(ImageDataStorage p) {
        super();
        imgProc = p;
    }
    
    /**
    *prepareRenderer method gives different colours to a specific rows and columns in the table 
    *<p>
    * It sets the first column's cells colour according to the given row's second
    * column and checks sets the foreground of the colour according to the colour's 
    * luminance  
     *This method always returns immediately, wether the cell gets colour or not.
     * 
     * @param renderer TableCellRenderer is object that allows modification of the table cells.
     * @param row specific row in the table
     * @param col specific column in the table.
     * @return  Component variable is where the changes are saved in the Jtable.
    */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        Component comp = super.prepareRenderer(renderer, row, col);

        if (col == 1) {
            comp.setBackground(Color.WHITE);
            comp.setForeground(Color.BLACK);
            return comp;
        }

        String value = (String) getModel().getValueAt(row, col + 1);
        PcdColor clr = imgProc.getColor(value);

        if (clr != null) {
            comp.setBackground(clr);
            double L = clr.getLuminance();
            if(L >= 0.1791)
                comp.setForeground(Color.BLACK);
            else
                comp.setForeground(Color.WHITE);
        } 

        return comp;
    }
   /**
    * This method returns the class of a specific column.
    * @param column specific column in the table.
    * @return the value of the column.
    */
    @Override
    public Class<?> getColumnClass(int column){
        return getValueAt(0, column).getClass();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui;

import java.awt.Color;
import static java.awt.Color.GREEN;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import pcd.data.ImageDataStorage;

/**
 * 
 * @author Noemi Farkas
 * This class creates a table where are shown all the loaded images
 */
public class FileTable extends JTable {

    private final ImageDataStorage imgProc;
  /**
    * The constructor has a super method which is extended by Jtable's constructor
    * <p>
    * @param p ImageDataStorage object contains all the information about the image data.
    */
    public FileTable(ImageDataStorage p) {
        super();
        imgProc = p;
    }
    /**
     * This method sets the background of the listed files depending on the selection and makes the 3rd column show with colour whether the image was initialized
     * @param renderer 
     * @param row specific row in the table 
     * @param col specific column in the table
     * @return Component 
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        Component comp = super.prepareRenderer(renderer, row, col);

        if (col == 0 || col == 1) {
            if (this.getSelectedRow() == row) {
                comp.setBackground(new Color(216, 205, 150));//selcted colour
                comp.setForeground(Color.BLACK);
            } else {
                comp.setBackground(Color.WHITE);
                comp.setForeground(Color.BLACK);
            }
            return comp;
        } else if (!imgProc.isInitialized(row)) {
            comp.setBackground(Color.GRAY);
        } else {
            comp.setBackground(GREEN);
        }
        return comp;
    }
    /**
     * Modifies the isCellEditable method enabling just the first column to be editable.
     * 
     * @param row
     * @param column
     * @return 
     */
    @Override
    public boolean isCellEditable(int row, int column) {
  
        if (column == 0) {
            return true;

        } else {
            return false;
        }
    }
}

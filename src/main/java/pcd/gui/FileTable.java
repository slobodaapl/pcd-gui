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
 * @author Nao
 */
public class FileTable extends JTable {

    private final ImageDataStorage imgProc;

    public FileTable(ImageDataStorage p) {
        super();
        imgProc = p;
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        Component comp = super.prepareRenderer(renderer, row, col);

        if (col == 0 || col == 1) {
            if (this.getSelectedRow() == row) {
                comp.setBackground(new Color(216, 205, 150));
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
    
    @Override
    public boolean isCellEditable(int row, int column){
        if(column == 0 && !imgProc.getImage(row).isInitialized())
            return true;
        else if(column == 0 && imgProc.getImage(row).isInitialized())
            return false;
        else
            return false;
    }
}

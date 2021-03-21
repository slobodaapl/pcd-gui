/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui;

import java.awt.Color;
import static java.awt.Color.BLACK;
import static java.awt.Color.GRAY;
import static java.awt.Color.GREEN;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import pcd.data.ImageDataStorage;
import pcd.utils.PcdColor;

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

        if (col == 0 || col==1 ) {
            comp.setBackground(Color.WHITE);
            return comp;
        }
        else if(imgProc.getImage(row)==null || !imgProc.isInitialized()){
        comp.setBackground(Color.BLACK);
        }
        else{
        comp.setBackground(GREEN);
        }
        return comp;
    }
}

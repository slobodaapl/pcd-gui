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
 * @author ixenr
 */
public final class TypeCountTable extends JTable {

    private final ImageDataStorage imgProc;

    public TypeCountTable(ImageDataStorage p) {
        super();
        imgProc = p;
    }

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
        } else {
            getModel().setValueAt(imgProc.getIcon(value), row, col);
        }

        return comp;
    }
    
    @Override
    public Class<?> getColumnClass(int column){
        return getValueAt(0, column).getClass();
    }

}

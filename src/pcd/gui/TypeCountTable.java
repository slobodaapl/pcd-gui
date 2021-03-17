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
import pcd.data.ImageProcess;
import pcd.utils.PcdColor;

/**
 *
 * @author ixenr
 */
public class TypeCountTable extends JTable {
    
    private final ImageProcess imgProc;

    public TypeCountTable(ImageProcess p) {
        super();
        imgProc = p;
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        Component comp = super.prepareRenderer(renderer, row, col);

        if(col == 1){
            comp.setBackground(Color.WHITE);
            return comp;
        }
        
        String value = (String) getModel().getValueAt(row, col + 1);
        PcdColor clr = imgProc.getColor(value);
        
        if (clr != null) {
            comp.setBackground(clr);
        } else {
            getModel().setValueAt(imgProc.getIcon(value), row, col);
        }

        return comp;
    }

}

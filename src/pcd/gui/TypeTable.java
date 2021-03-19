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
import pcd.utils.PcdColor;

/**
 *
 * @author ixenr
 */
public class TypeTable extends JTable {

    private final ImageDataStorage imgProc;

    public TypeTable(ImageDataStorage p) {
        super();
        imgProc = p;
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        Component comp = super.prepareRenderer(renderer, row, col);

        if (col != 1) {
            if (this.isRowSelected(row)) {
                comp.setBackground(new Color(255 - 35, 255 - 35, 255 - 45));
            } else {
                comp.setBackground(Color.WHITE);
            }
            return comp;
        }

        PcdPoint value = (PcdPoint) getModel().getValueAt(row, col - 1);
        PcdColor clr = imgProc.getColor(value);

        if (clr != null) {
            comp.setBackground(clr);
        } else {
            getModel().setValueAt(imgProc.getIcon(value), row, col);
        }

        return comp;
    }

}

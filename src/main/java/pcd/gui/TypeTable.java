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
        PcdPoint p = (PcdPoint) getModel().getValueAt(row, 0);

        if (col != 1) {
            if (this.isRowSelected(row)) {
                comp.setBackground(new Color(255 - 35, 255 - 35, 255 - 45));
            } else if (p.getScore() <= Constant.SCORE_THRESHOLD) {
                comp.setBackground(Color.YELLOW);
            } else {
                comp.setBackground(Color.WHITE);
            }
            return comp;
        }

        PcdColor clr = imgProc.getColor(p);

        if (clr != null) {
            comp.setBackground(clr);
        } else {
            getModel().setValueAt(imgProc.getIcon(p), row, col);
        }

        return comp;
    }

}

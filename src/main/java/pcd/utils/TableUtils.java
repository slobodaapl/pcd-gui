/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.JViewport;
import pcd.data.PcdPoint;

/**
 * This class is mainly responsible for synchronizing the  PCD point in the table according to the  selected PCD point.
 * @author Noemi Farkas
 */
public class TableUtils {

    private TableUtils() {
    }

    /**
     * This method is responsible for searching for the selected PCD point in the table, and
     * calling the scrollToPoint method on it.
     * @param p selected PcdPoint 
     * @param table JTable storing PCD point data.
     */
    public static void updateSelect(PcdPoint p, JTable table) {

        int idx = -1;

        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0).equals(p)) {
                idx = i;
                break;
            }
        }

        if (idx == -1) {
            System.out.println("Point not in table???");
        }

        table.setRowSelectionInterval(idx, idx);

        TableUtils.scrollToPoint(table, idx);

    }

    /**
     * This method is responsible for scrolling in the Jtable to a certain row.
     * @param table JTable 
     * @param row int specific row in the JTable
     */
    private static void scrollToPoint(JTable table, int row) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }

        Rectangle rect = table.getCellRect(row, 1, true);

        table.scrollRectToVisible(rect);

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import java.awt.Point;
import java.awt.Rectangle;
import javafx.scene.control.TableSelectionModel;
import javax.swing.JTable;
import javax.swing.JViewport;
import pcd.data.PcdPoint;

/**
 *
 * @author ixenr
 */
public class TableUtils {
    
    private TableUtils() {}
    
    public static void updateSelect(PcdPoint p, JTable table) {
        
        int idx = -1;
        
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0) == p) {
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
    
    private static void scrollToPoint(JTable table, int row) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport) table.getParent();
        
        Rectangle rect = table.getCellRect(row, 1, true);
        
        Point pt = viewport.getViewPosition();
        
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        
        table.scrollRectToVisible(rect);
        
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.dialog;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import pcd.data.ImageDataStorage;
import pcd.gui.MainFrame;

/**
 *
 * @author ixenr
 */
public final class FileListPopup extends JPopupMenu {

    private final JMenuItem deleter;
    private final MainFrame frame;
    private final JTable parentTable;
    private final ImageDataStorage imgDataStorage;
    private final int row;

    public FileListPopup(MainFrame frame, JTable parentTable, ImageDataStorage imgDataStorage, int row) {
        this.parentTable = parentTable;
        this.frame = frame;
        this.imgDataStorage = imgDataStorage;
        this.row = row;
        String close = "Close";
        deleter = new JMenuItem(close);

        addCloseListener();

        super.add(deleter);
    }

    private void addCloseListener() {
        deleter.addActionListener(e -> closeImageFile());
    }

    private void closeImageFile() {
        if (frame.hasOverlay()) {
            frame.getImagePane().removeOverlay(imgDataStorage.getOverlay());
            frame.setHasOverlay(false);
        }

        imgDataStorage.dispose();
        ((DefaultTableModel) parentTable.getModel()).removeRow(row);
        frame.getImagePane().setImage(null);
        frame.loadTables();
    }

}

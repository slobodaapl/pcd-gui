/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.dialog;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import pcd.data.ImageDataStorage;
import pcd.gui.MainFrame;

/**
 *
 * @author ixenr
 */
public final class FileListPopup extends JPopupMenu {

    private final JMenuItem deleter;
    private final MainFrame frame;
    private final JList parentList;
    private final ImageDataStorage imgDataStorage;
    private final int row;

    public FileListPopup(MainFrame frame, JList parentList, ImageDataStorage imgDataStorage, int row) {
        this.frame = frame;
        this.parentList = parentList;
        this.imgDataStorage = imgDataStorage;
        this.row = row;
        deleter = new JMenuItem("Zavrit");

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
        ((DefaultListModel) parentList.getModel()).remove(row);
        frame.getImagePane().setImage(null);
        frame.loadTables();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import pcd.imageviewer.ImageMouseClickListener;
import pcd.imageviewer.ImageMouseEvent;
import java.awt.event.MouseEvent;
import javax.swing.CellEditor;
import pcd.data.ImageDataStorage;
import pcd.data.PcdPoint;
import pcd.gui.MainFrame;
import pcd.utils.TableUtils;

/**
 *
 * @author ixenr
 */
public class PCDClickListener implements ImageMouseClickListener {

    private final ImageDataStorage imgDataStorage;
    private final MainFrame parentFrame;
    private PcdPoint selectedPoint = null;

    public PCDClickListener(MainFrame frame, ImageDataStorage imgDataStorage) {
        parentFrame = frame;
        this.imgDataStorage = imgDataStorage;
    }

    public void setSelection(PcdPoint p) {
        if (selectedPoint == null) {
            selectedPoint = p;
            p.select();
        } else {
            selectedPoint.deselect();
            selectedPoint = p;
            p.select();
        }

        p.setScore(1.0);
        imgDataStorage.getCurrent().getOverlay().repaint();
    }

    @Override
    public void mouseClicked(ImageMouseEvent e) {
        int button = e.getOriginalEvent().getButton();
        if (imgDataStorage.getCurrent().isInitialized()) {
            PcdPoint p = imgDataStorage.getCurrent().getClosestPoint(e.getX(), e.getY());
            double distance = p.distanceToPoint(new PcdPoint(e.getX(), e.getY()));
            if (button == MouseEvent.BUTTON1) {
                if (distance >= 50 || p.getType() == -1) {
                    if (selectedPoint != null) {
                        selectedPoint.deselect();
                    }
                    selectedPoint = new PcdPoint(e.getX(), e.getY());
                    selectedPoint.select();
                    imgDataStorage.addPoint(selectedPoint, parentFrame.getNewClickType());
                    parentFrame.saveProjectTemp();
                    parentFrame.loadTables();
                    TableUtils.updateSelect(selectedPoint, parentFrame.getTagTable());
                    parentFrame.getTagTable().setValueAt(selectedPoint.getTypeName(), parentFrame.getTagTable().getSelectedRow(), 2);
                } else if (p.getType() != -1) {
                    if (selectedPoint != null) {
                        selectedPoint.deselect();
                    }
                    selectedPoint = p;
                    selectedPoint.select();
                    TableUtils.updateSelect(p, parentFrame.getTagTable());
                    imgDataStorage.getCurrent().getOverlay().repaint();
                }
            } else if (button == MouseEvent.BUTTON3) {
                if (distance <= 50 && p.getType() != -1) {
                    remPoint(p);
                }
            }
        }
    }

    public void remPoint(PcdPoint p) {
        CellEditor cellEditor = parentFrame.getTagTable().getCellEditor();
        if (cellEditor != null) {
            if (cellEditor.getCellEditorValue() != null) {
                cellEditor.stopCellEditing();
            } else {
                cellEditor.cancelCellEditing();
            }
        }
        imgDataStorage.remPoint(p);
        parentFrame.loadTables();
    }
    
    public PcdPoint getSelection(){
        return selectedPoint;
    }

}

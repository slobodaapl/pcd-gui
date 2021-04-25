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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pcd.data.ImageDataStorage;
import pcd.data.PcdPoint;
import pcd.gui.MainFrame;
import pcd.utils.Constant;
import pcd.utils.TableUtils;

/**
 *
 * @author Tibor Sloboda
 * @author Noemi Farkas
 *
 * A mouse listener for tap-clicking on the image viewport
 */
public class PCDClickListener implements ImageMouseClickListener {
    private static final Logger LOGGER = LogManager.getLogger(PCDClickListener.class);

    /**
     * A reference to instantiated {@link ImageDataStorage}.
     * Used to update and manipulate points in response to clicks.
     */
    private final ImageDataStorage imgDataStorage;
    /**
     * The parent frame from which to listen for clicks.
     * Manipulates tables based on changes.
     */
    private final MainFrame parentFrame;
    /**
     * Keeps the reference to the last selected point, in
     * order to set it as selected or deselect it, so that two
     * points can't end up being selected at once.
     */
    private PcdPoint selectedPoint = null;

    /**
     * Initializes the listener with the parent frame and {@link ImageDataStorage}
     * @param frame the parent frame, specifically {@link MainFrame}
     * @param imgDataStorage the instantiated image object storage
     */
    public PCDClickListener(MainFrame frame, ImageDataStorage imgDataStorage) {
        parentFrame = frame;
        this.imgDataStorage = imgDataStorage;
    }

    /**
     * Programatically selects a point based on selection of table row.
     * Also updates the score to 1.0, since the point is now considered 'verified'.
     * Repaints the overlay after it is done.
     * @param p the point to select
     */
    public void setSelection(PcdPoint p) {
        //TODO Make safe
        //p = imgDataStorage.getActualPoint(p);
        
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

    /**
     * If left click is used, it adds a new point based on selected type if distance from closest
     * point exceeds 50. Otherwise, select the closes point to the click location.
     * <p>
     * If right click is used, the closest point within 50 pixels is removed.
     * <p>
     * After any point modifications are made, the tables in the main GUI are updated,
     * and any selections cause highlighting of the point that is selected.
     *
     * @see PcdPoint#select()
     * @param e the mouse event
     */
    @Override
    public void mouseClicked(ImageMouseEvent e) {
        int button = e.getOriginalEvent().getButton();
        LOGGER.info("Clicked (x,y): " + e.getX() + "," + e.getY());
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
                } else {
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

    /**
     * Updates tables of the main GUI and removes the point from the image object
     * The angles are re-calculated if changes were made to an angle
     * @param p the point to be removed
     */
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
        imgDataStorage.getCurrent().updateAvgStdAngle();
        parentFrame.loadTables();
    }

    /**
     * Retrieves the currently selected point.
     * @return the currently selected point
     */
    public PcdPoint getSelection(){
        return selectedPoint;
    }

}

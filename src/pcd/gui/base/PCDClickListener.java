/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import hu.kazocsaba.imageviewer.ImageMouseClickListener;
import hu.kazocsaba.imageviewer.ImageMouseEvent;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;
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
        imgDataStorage.getCurrentImage().getOverlay().repaint();
    }

    @Override
    public void mouseClicked(ImageMouseEvent e) {
        int button = e.getOriginalEvent().getButton();
        if (imgDataStorage.getCurrentImage().isInitialized()) {
            PcdPoint p = imgDataStorage.getCurrentImage().getClosestPoint(e.getX(), e.getY());
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
                } else if (p.getType() != -1) {
                    if (selectedPoint != null) {
                        selectedPoint.deselect();
                    }
                    selectedPoint = p;
                    selectedPoint.select();
                    TableUtils.updateSelect(p, parentFrame.getTagTable());
                    imgDataStorage.getCurrentImage().getOverlay().repaint();
                }
            } else if (button == MouseEvent.BUTTON3) {
                if (distance <= 50 && p.getType() != -1) {
                    imgDataStorage.remPoint(p);
                    parentFrame.loadTables();
                }
            }
        }
    }

}

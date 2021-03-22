/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.python;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import pcd.data.ImageDataStorage;
import pcd.gui.dialog.LoadingMultipleDialogGUI;

/**
 *
 * @author ixenr
 */
public class LoadingMultipleDialogProcess extends SwingWorker<List<Boolean>, Boolean> {

    private final ArrayList<Integer> idxList;
    private final ImageDataStorage imgStore;
    private int current = 0;
    private final List<Boolean> out = new ArrayList<>();
    private final LoadingMultipleDialogGUI gui;

    public LoadingMultipleDialogProcess(ArrayList<Integer> idxList, ImageDataStorage imgStore, LoadingMultipleDialogGUI gui) {
        this.idxList = idxList;
        this.imgStore = imgStore;
        this.gui = gui;
    }

    @Override
    public List<Boolean> doInBackground() throws Exception {
        for (int i = 0; i < idxList.size() && !isCancelled(); i++) {
            boolean res = imgStore.inferImage(idxList.get(i));
            out.add(res);
            publish(res);
            gui.inferProgressBar.setValue((i / idxList.size()) * 100);
            gui.repaint();
            setProgress((i / idxList.size()) * 100);
        }
        gui.dispose();
        return out;
    }

    @Override
    protected void process(List<Boolean> chunks) {
        chunks.forEach(_item -> {
            current += 1;
        });
    }

}

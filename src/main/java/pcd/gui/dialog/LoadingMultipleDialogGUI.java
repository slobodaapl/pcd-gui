/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.dialog;

import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import pcd.data.ImageDataObject;
import pcd.data.PcdPoint;
import pcd.gui.MainFrame;
import pcd.python.PythonProcess;
import pcd.utils.Constant;

/**
 *
 * @author ixenr
 */
public class LoadingMultipleDialogGUI extends JDialog {

    private final PythonProcess pyproc;
    private final ArrayList<Integer> idxList;
    private final ArrayList<ImageDataObject> imageList;
    private final ArrayList<ArrayList<PcdPoint>> pointlistList = new ArrayList<>();
    private ImgTask imgTask;
    private final JDialog thisDialog;

    public LoadingMultipleDialogGUI(MainFrame parentFrame, PythonProcess pyproc, ArrayList<Integer> idxList, ArrayList<ImageDataObject> imageList) {
        super(parentFrame, true);
        initComponents();
        this.pyproc = pyproc;
        this.idxList = idxList;
        this.imageList = imageList;
        this.thisDialog = this;
    }

    public ArrayList<ArrayList<PcdPoint>> showDialog(){
        (imgTask = new ImgTask()).execute();
        setVisible(true);
        return pointlistList;
    }
    
    private class ImgTask extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() throws Exception {
            for (Integer idx : idxList) {
                if(Constant.DEBUG_MSG)
                    System.out.println("\nSending image index " + idx.toString() + ": " + imageList.get(idx).getImgPath());
                pointlistList.add(pyproc.getPoints(imageList.get(idx).getImgPath(), inferProgressBar, idxList.size()));
            }
            if(Constant.DEBUG_MSG)
                System.out.println("\nFinished queue\n");
            thisDialog.setVisible(false);
            thisDialog.dispose();
            return null;    
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inferProgressBar = new javax.swing.JProgressBar();
        loadLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Bundle"); // NOI18N
        setTitle(bundle.getString("General.loading")); // NOI18N
        setAlwaysOnTop(true);
        setModal(true);

        inferProgressBar.setDoubleBuffered(true);

        loadLabel.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        loadLabel.setText(bundle.getString("General.loading")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(loadLabel)
                    .addComponent(inferProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(loadLabel)
                .addGap(18, 18, 18)
                .addComponent(inferProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JProgressBar inferProgressBar;
    public javax.swing.JLabel loadLabel;
    // End of variables declaration//GEN-END:variables
}

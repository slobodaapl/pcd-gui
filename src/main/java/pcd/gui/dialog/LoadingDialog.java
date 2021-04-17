/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.dialog;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import pcd.data.PcdPoint;
import pcd.python.PythonProcess;
import pcd.utils.Constant;

/**
 *
 * @author ixenr
 */
public class LoadingDialog extends JDialog {

    private final PythonProcess pyproc;
    private ArrayList<PcdPoint> pointlist = null;
    private final String imgPath;
    private final JDialog thisDialog;

    public LoadingDialog(java.awt.Frame parent, PythonProcess pyproc, String imgPath) {
        super(parent, true);
        initComponents();
        this.pyproc = pyproc;
        this.imgPath = imgPath;
        this.thisDialog = this;
    }

    public ArrayList<PcdPoint> showDialog() {
        (new ImgTask()).execute();
        setVisible(true);
        return pointlist;
    }

    private class ImgTask extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            try {
                if (Constant.DEBUG_MSG) {
                    System.out.println("SwingWorker started");
                }
                pointlist = pyproc.getPoints(imgPath);
                if (Constant.DEBUG_MSG) {
                    System.out.println("Function returned in SwingWorker");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (Constant.DEBUG_MSG) {
                System.out.println("SwingWorker finishing");
            }
            thisDialog.setVisible(false);
            thisDialog.dispose();
            return null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Loading");
        setAlwaysOnTop(true);
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Loading, please wait.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}

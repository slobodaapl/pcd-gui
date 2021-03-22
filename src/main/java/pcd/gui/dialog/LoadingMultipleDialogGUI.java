/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.dialog;

import pcd.python.LoadingMultipleDialogProcess;

/**
 *
 * @author ixenr
 */
public class LoadingMultipleDialogGUI extends javax.swing.JDialog {

    /**
     * Creates new form LoadingMultipleDialog
     */
    public LoadingMultipleDialogGUI(java.awt.Frame parent) {
        super(parent, false);
        initComponents();
    }
    
    @Override
    public void setVisible(boolean b){
        super.setVisible(b);
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
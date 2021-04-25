/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.dialog;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pcd.gui.MainFrame;
import pcd.python.PythonProcess;
import pcd.utils.AngleWrapper;

/**
 *
 * @author Tibor Sloboda
 * 
 * The dialog popup shown during loading of angles from Python
 */
public class AngleLoadingDialog extends JDialog {
    
    /**
     * The parent frame for modality
     */
    MainFrame parentFrame;
    /**
     * Reference to the python process
     */
    private final PythonProcess pyproc;
    /**
     * Path to the image for which to retrieve angles
     */
    private final String imgPath;
    /**
     * Points whose coordinates tell Python where to look for angles
     */
    private final ArrayList<Point> pointList;
    /**
     * Reference this dialog
     */
    private final JDialog thisDialog = this;
    /**
     * The resulting {@link AngleWrapper} used to return the results. Packs
     * together a list of angles and 'positiveness' boolean values
     */
    private AngleWrapper result = null;

    /**
     * Initializes the dialog 
     * @param parentFrame the parent frame, namely {@link MainFrame}, for modality
     * @param imgPath the path to the image to send to python
     * @param pointList the list of points to send to python
     */
    public AngleLoadingDialog(MainFrame parentFrame, String imgPath, ArrayList<Point> pointList) {
        super(parentFrame, true);
        initComponents();
        this.parentFrame = parentFrame;
        this.pyproc = PythonProcess.getInstance();
        this.imgPath = imgPath;
        this.pointList = pointList;
    }
    
    /**
     * Runs the worker thread and displays the dialog
     * @return an {@link AngleWrapper} with results from inference or null if failed
     */
    public AngleWrapper showDialog(){
        (new ImgTask()).execute();
        setVisible(true);
        return result;  
    }
    
    private class ImgTask extends SwingWorker<Void, String> {
        
        private final Logger LOGGER = LogManager.getLogger(ImgTask.class);

        @Override
        protected Void doInBackground() {
            try{
                result = pyproc.getAngles(imgPath, pointList);
            }catch(IOException e){
                LOGGER.error("Something went wrong", e);
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Loading");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Loading, please wait.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
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

package pcd.gui.dialog;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pcd.data.ImageDataObject;
import pcd.data.PcdPoint;
import pcd.python.PythonProcess;
import pcd.utils.TimerUtil;

/**
 *
 * @author Tibor Sloboda Dialog for inferring multiple images, also showing a
 * progress bar
 */
public class LoadingMultipleDialogGUI extends JDialog {

    private final PythonProcess pyproc;
    private final ArrayList<Integer> idxList;
    private final List<String> imagePathList;
    private final ArrayList<ArrayList<PcdPoint>> pointlistList = new ArrayList<>();
    private final JDialog thisDialog;

    /**
     * Initializes the dialog
     *
     * @param parentFrame the parent frame for modality
     * @param idxList the list of indexes for which to infer points, matching
     * imagePathList
     * @param imagePathList the list of image paths from {@link ImageDataObject}
     */
    public LoadingMultipleDialogGUI(Frame parentFrame, ArrayList<Integer> idxList, List<String> imagePathList) {
        super(parentFrame, true);
        initComponents();
        this.pyproc = PythonProcess.getInstance();
        this.idxList = idxList;
        this.imagePathList = imagePathList;
        this.thisDialog = this;
        this.progressLabel.setText(String.format("%d / %d", 0, idxList.size()));
    }

    /**
     * Shows the dialog and run the worker thread
     *
     * @return the inferred list of point lists for every index, or null if
     * failed
     */
    public ArrayList<ArrayList<PcdPoint>> showDialog() {
        (new ImgTask()).execute();
        setVisible(true);
        return pointlistList;
    }

    private class ImgTask extends SwingWorker<Void, String> {

        private final Logger LOGGER = LogManager.getLogger(ImgTask.class);

        @Override
        protected Void doInBackground() throws Exception {
            LOGGER.info("SwingWorker started");
            int iterator = 0;
            for (Integer idx : idxList) {
                LOGGER.info("\nSending image index " + idx.toString() + ": " + imagePathList.get(idx));
                
                TimerUtil.start();
                pointlistList.add(pyproc.getPoints(imagePathList.get(idx)));
                TimerUtil.end();
                System.out.println("Inference points: " + TimerUtil.elapsedSeconds());
                
                int progress = inferProgressBar.getValue();
                int max = inferProgressBar.getMaximum();
                int increment = max / (idxList.size());
                inferProgressBar.setValue(progress + increment);
                
                progressLabel.setText(String.format("%d / %d", ++iterator, idxList.size()));
                LOGGER.info("Done: " + iterator);
            }
            LOGGER.info("\nFinished queue\n");
            thisDialog.setVisible(false);
            thisDialog.dispose();
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inferProgressBar = new javax.swing.JProgressBar();
        loadLabel = new javax.swing.JLabel();
        progressLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Bundle"); // NOI18N
        setTitle(bundle.getString("General.loading")); // NOI18N
        setModal(true);

        inferProgressBar.setDoubleBuffered(true);

        loadLabel.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        loadLabel.setText(bundle.getString("General.loading")); // NOI18N

        progressLabel.setText("x");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(loadLabel)
                    .addComponent(inferProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressLabel))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(loadLabel)
                .addGap(18, 18, 18)
                .addComponent(inferProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressLabel)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JProgressBar inferProgressBar;
    public javax.swing.JLabel loadLabel;
    public javax.swing.JLabel progressLabel;
    // End of variables declaration//GEN-END:variables
}

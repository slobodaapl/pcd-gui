/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import hu.kazocsaba.imageviewer.Overlay;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import pcd.gui.MainFrame;
import pcd.gui.dialog.LoadingDialog;
import pcd.python.PythonProcess;
import pcd.utils.Constant;
import pcd.utils.FileUtils;
import pcd.utils.PcdColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ixenr
 */
public class ImageDataStorage {

    public static Logger getLOGGER() {
        return LOGGER;
    }

    private ArrayList<ImageDataObject> imageList = new ArrayList<>();
    private ImageDataObject current;
    private static final Logger LOGGER = LogManager.getLogger(ImageDataStorage.class);
    private final ArrayList<String> typeConfigList;
    private final ArrayList<Integer> typeIdentifierList;

    private final ArrayList<String> typeIconList;
    private final PythonProcess pyproc;
    private final ImageDataObjectFactory imgFactory;
    private MainFrame parentFrame;

    public ImageDataStorage(ArrayList<String> typeConfigList, ArrayList<Integer> typeIdentifierList, ArrayList<String> typeIconList) {
        this.typeConfigList = typeConfigList;
        this.typeIdentifierList = typeIdentifierList;
        this.typeIconList = typeIconList;
        pyproc = new PythonProcess(5000, Constant.DEBUG);
        imgFactory = new ImageDataObjectFactory(pyproc, this);
    }

    public ArrayList<String> getTypeConfigList() {
        return typeConfigList;
    }

    public ArrayList<Integer> getTypeIdentifierList() {
        return typeIdentifierList;
    }

    public void stopProcess() {
        pyproc.stop();
    }

    public void addImage(String path) {
        imgFactory.addImage(path);
    }

    public BufferedImage getImageObject(int index) {
        return this.getAndUpdateCurrentImage(index).loadImage();
    }

    public BufferedImage getImageObject() {
        return current.loadImage();
    }

    public ImageDataObject getAndUpdateCurrentImage(int index) {
        current = imageList.get(index);
        return current;
    }

    public ImageDataObject getImage(int index) {
        return imageList.get(index);
    }

    public ImageDataObject getCurrent() {
        return current;
    }

    public ImageDataObject getLastImage() {
        current = imageList.get(imageList.size() - 1);
        return current;
    }

    public Overlay getOverlay() {
        return current.getOverlay();
    }

    public void deleteImage(int index) {
        imageList.remove(index);
    }

    public void addImage(ImageDataObject img) {
        imageList.add(img);
    }

    public boolean checkOpened(File f) throws IOException {
        boolean opened = false;

        try {
            for (ImageDataObject imageDataObject : imageList) {
                opened = opened | imageDataObject.fileMatch(f.getPath());
            }
        } catch (IOException e) {
            LOGGER.info("File is not opend.", e);
        }

        return opened;
    }

    public void setFrame(MainFrame aThis) {
        this.parentFrame = aThis;
    }

    public void addPoint(PcdPoint pcdPoint, String newClickType) {
        pcdPoint.setType(typeIdentifierList.get(typeConfigList.indexOf(newClickType)));
        pcdPoint.setTypeName(newClickType);
        this.addPoint(pcdPoint);
    }

    public PcdColor getColor(PcdPoint p) {
        int idx = typeIdentifierList.indexOf(p.getType());
        String s = typeIconList.get(idx);
        return parseColor(s);
    }

    public PcdColor getColor(String typeName) {
        return parseColor(typeIconList.get(typeConfigList.indexOf(typeName)));
    }

    private PcdColor parseColor(String s) {
        if (s.contains(".rgb")) {
            int r = Integer.parseInt(s.substring(0, 2), 16);
            int g = Integer.parseInt(s.substring(2, 4), 16);
            int b = Integer.parseInt(s.substring(4, 6), 16);
            return new PcdColor(r, g, b);
        }

        return null;
    }

    public BufferedImage getIcon(PcdPoint value) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("./icons/" + typeIconList.get(typeIdentifierList.indexOf(value.getType()))));
        } catch (IOException e) {
            LOGGER.error("Unable to load icon", e);
            JOptionPane.showMessageDialog(parentFrame, "Nepodarilo se najit nebo nacist ikonu", "Chyba", JOptionPane.ERROR_MESSAGE);
        }

        return img;
    }

    public BufferedImage getIcon(String identifier) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("./icons/" + typeIconList.get(typeConfigList.indexOf(identifier))));
        } catch (IOException e) {
            LOGGER.error("", e);
            JOptionPane.showMessageDialog(parentFrame, "Nepodarilo se najit nebo nacist ikonu", "Chyba", JOptionPane.ERROR_MESSAGE);
        }

        return img;
    }

    public String getPointTypeName(PcdPoint p) {
        int idx = typeIdentifierList.indexOf(p.getType());
        return typeConfigList.get(idx);
    }

    public int getPointIdentifier(String s) {
        int idx = typeConfigList.indexOf(s);
        return typeIdentifierList.get(idx);
    }

    public ArrayList<AtomicInteger> getCounts() {
        ArrayList<PcdPoint> ptList = current.getPointList();
        ArrayList<AtomicInteger> counts = new ArrayList<>();
        typeConfigList.forEach(_item -> {
            counts.add(new AtomicInteger(0));
        });

        ptList.forEach(pcdPoint -> {
            counts.get(typeIdentifierList.indexOf(pcdPoint.getType())).incrementAndGet();
        });

        return counts;
    }

    public boolean isInitialized() {
        if (current == null) {
            return false;
        }
        return current.isInitialized();
    }

    public void addPoint(PcdPoint pcdPoint) {
        current.addPoint(pcdPoint);
    }

    public void remPoint(PcdPoint p) {
        current.remPoint(p);
    }

    public void dispose() {
        imageList.remove(current);
        current = null;
    }

    public ArrayList<ImageDataObject> getImageObjectList() {
        return imageList;
    }

    public void setImageObjectList(ArrayList<ImageDataObject> list) {
        imageList = list;
        current = null;
    }

    public void saveProject(Path savePath, ArrayList<ImageDataObject> imgObjectList) {
        FileUtils.saveProject(savePath, imgObjectList);
    }

    public boolean isInitialized(int row) {
        return imageList.get(row).isInitialized();
    }

    public boolean inferImage() {
        LoadingDialog loading = new LoadingDialog(parentFrame);
        loading.setLocationRelativeTo(parentFrame);
        loading.setVisible(true);
        current.initialize(pyproc, typeIdentifierList, typeIconList, typeConfigList);
        boolean result = current.isInitialized();
        loading.dispose();

        if (!result) {
            JOptionPane.showMessageDialog(parentFrame, "Nepodarilo se nacitat anotace, ulozte prosim svou praci a restartujte program", "Chyba", JOptionPane.ERROR);
        }

        return result;
    }

    public boolean inferImage(int i) {
        imageList.get(i).initialize(pyproc, typeIdentifierList, typeIconList, typeConfigList);
        return imageList.get(i).isInitialized();
    }

    public void inferImages(ArrayList<Integer> idxList) {
        if (idxList.isEmpty()) {
            return;
        }

//        LoadingMultipleDialogGUI dialogProcess = new LoadingMultipleDialogGUI(aThis);
//        LoadingMultipleDialogProcess task = new LoadingMultipleDialogProcess(idxList, this, dialogProcess);
//        dialogProcess.setLocationRelativeTo(aThis);
//        
//        task.addPropertyChangeListener((PropertyChangeEvent evt) -> {
//            if ("progress".equals(evt.getPropertyName())) {
//                dialogProcess.inferProgressBar.setValue(((Integer)evt.getNewValue() / idxList.size()) * dialogProcess.inferProgressBar.getMaximum());
//                if(dialogProcess.inferProgressBar.getValue() == dialogProcess.inferProgressBar.getMaximum())
//                    dialogProcess.dispose();
//            }
//        });
//        
//        task.execute();
//        dialogProcess.setVisible(true);
//        try {
//            task.wait();
//            dialogProcess.dispose();
//        } catch (InterruptedException ex) {
//            java.util.logging.Logger.getLogger(ImageDataStorage.class.getName()).log(Level.SEVERE, null, ex);
//        }
        LoadingDialog loading = new LoadingDialog(parentFrame);
        loading.setLocationRelativeTo(parentFrame);
        loading.setVisible(true);

        for (int i = 0; i < idxList.size(); i++) {
            inferImage(idxList.get(i));
        }

        parentFrame.getFileListTable().setRowSelectionInterval(idxList.get(0), idxList.get(0));
        ((DefaultTableModel) parentFrame.getFileListTable().getModel()).fireTableDataChanged();
        parentFrame.loadTables();

        loading.dispose();

    }
}

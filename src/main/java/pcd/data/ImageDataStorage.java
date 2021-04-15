/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import pcd.imageviewer.Overlay;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
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
import pcd.gui.dialog.LoadingMultipleDialogGUI;

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
    private final ArrayList<String> typeTypeList;

    public ImageDataStorage(ArrayList<String> typeConfigList, ArrayList<Integer> typeIdentifierList, ArrayList<String> typeIconList, ArrayList<String> typeTypeList) {
        this.typeConfigList = typeConfigList;
        this.typeIdentifierList = typeIdentifierList;
        this.typeIconList = typeIconList;
        this.typeTypeList = typeTypeList;
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
        if(idx == -1)
            return idx;
        return typeIdentifierList.get(idx);
    }

    public ArrayList<AtomicInteger> getCounts() {
        ArrayList<AtomicInteger> counts = new ArrayList<>();
        typeConfigList.forEach(_item -> {
            counts.add(new AtomicInteger(0));
        });

        current.getPointList().forEach(pcdPoint -> {
            counts.get(typeIdentifierList.indexOf(pcdPoint.getType())).incrementAndGet();
        });

        return counts;
    }
    
    public String getPcdRate(ArrayList<AtomicInteger> counts){
        DecimalFormat df = new DecimalFormat("#.##");
        double primary = Math.ulp(1.0);
        double normal = Math.ulp(1.0);
        
        for (int i = 0; i < counts.size(); i++) {
            int num = counts.get(i).get();
            switch(typeTypeList.get(i)){
                default:
                    break;
                case "n":
                    normal += num;
                    break;
                case "p":
                    primary += num;
                    break;
            }
        }
        
        return df.format(primary * 100 / (normal + primary));
    }
    
    public String getSecRate(ArrayList<AtomicInteger> counts){
        DecimalFormat df = new DecimalFormat("#.##");
        double secondary = Math.ulp(1.0);
        double normal = Math.ulp(1.0);
        
        for (int i = 0; i < counts.size(); i++) {
            int num = counts.get(i).get();
            switch(typeTypeList.get(i)){
                default:
                    break;
                case "n":
                    normal += num;
                    break;
                case "s":
                    secondary += num;
                    break;
            }
        }
        
        return df.format(secondary * 100 / (normal + secondary));
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
        if(current != null)
            imageList.remove(current);
        current = null;
    }

    public ArrayList<ImageDataObject> getImageObjectList() {
        ArrayList<ImageDataObject> imgData = new ArrayList<>();
        
        imageList.forEach(imageDataObject -> {
            imgData.add(new ImageDataObject(imageDataObject));
        });
        
        return imgData;
    }
    
    public ArrayList<String> getImageNames(){
        ArrayList<String> strList = new ArrayList<>();
        
        imageList.forEach(imageDataObject -> {
            strList.add(imageDataObject.getImageName());
        });
        
        return strList;
    }

    private void setImageObjectList(ArrayList<ImageDataObject> list) {
        imageList = list;
        current = null;
    }

    public boolean isInitialized(int row) {
        return imageList.get(row).isInitialized();
    }

    public boolean inferImage() {
        LoadingDialog loading = new LoadingDialog(parentFrame, pyproc, current.getImgPath());
        loading.setLocationRelativeTo(parentFrame);
        
        ArrayList<PcdPoint> pointlist = loading.showDialog();
        
        current.initialize(pointlist, typeIdentifierList, typeIconList, typeConfigList);
        boolean result = current.isInitialized();

        if (!result) {
            JOptionPane.showMessageDialog(parentFrame, "Nepodarilo se nacitat anotace, ulozte prosim svou praci a restartujte program", "Chyba", JOptionPane.ERROR_MESSAGE);
        }

        return result;
    }

    public boolean inferImage(int i, ArrayList<PcdPoint> pointlist) {
        imageList.get(i).initialize(pointlist, typeIdentifierList, typeIconList, typeConfigList);
        return imageList.get(i).isInitialized();
    }

    public void inferImages(ArrayList<Integer> idxList) {
        if (idxList.isEmpty()) {
            return;
        } else if(Constant.DEBUG_MSG)
            System.out.println("Marked images found, submitting for inference: " + String.valueOf(idxList.size()));
        
        LoadingMultipleDialogGUI inferGui = new LoadingMultipleDialogGUI(parentFrame, pyproc, idxList, imageList);
        inferGui.setLocationRelativeTo(parentFrame);
        ArrayList<ArrayList<PcdPoint>> pointlistList = inferGui.showDialog();
        
        if(Constant.DEBUG_MSG)
            System.out.println("Starting post-processing..");
        
        for (int i = 0; i < pointlistList.size(); i++) {
            if(Constant.DEBUG_MSG)
                System.out.println("Progress: " + String.valueOf(i+1) + "/" + String.valueOf(pointlistList.size()));
            inferImage(idxList.get(i), pointlistList.get(i));
        }

        parentFrame.getFileListTable().setRowSelectionInterval(idxList.get(0), idxList.get(0));
        ((DefaultTableModel) parentFrame.getFileListTable().getModel()).fireTableDataChanged();
        parentFrame.loadTables();

    }

    public void clear() {
        imageList.clear();
    }

    public void setPointType(PcdPoint p, String string) {
        int id = getPointIdentifier(string);
        if(id == -1)
            return;
        
        p.setType(id);
        p.setTypeName(string);
    }

    public void loadProject(File file) {
        ArrayList<ImageDataObject> objList = FileUtils.loadProject(file);
        
        if(objList == null)
            return;
        
        objList.forEach(imageDataObject -> {
            imageDataObject.initializeOverlay(typeIdentifierList, typeIconList);
        });
        
        setImageObjectList(objList);
    }
}

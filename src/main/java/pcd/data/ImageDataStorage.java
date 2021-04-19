/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pcd.gui.MainFrame;
import pcd.gui.dialog.AngleLoadingDialog;
import pcd.gui.dialog.LoadingDialog;
import pcd.gui.dialog.LoadingMultipleDialogGUI;
import pcd.imageviewer.Overlay;
import pcd.python.PythonProcess;
import pcd.utils.AngleWrapper;
import pcd.utils.Constant;
import pcd.utils.FileUtils;
import pcd.utils.PcdColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import pcd.gui.base.ImgFileFilter;

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
        pyproc = new PythonProcess();
        imgFactory = new ImageDataObjectFactory(this);
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

    public BufferedImage getBufferedImage(int index) {
        String path = getAndUpdateCurrentImage(index).getImgPath();
        
        File imgFile = new File(path);
        if(!imgFile.exists() || !imgFile.canRead()){
            int returnValue = JOptionPane.showConfirmDialog(parentFrame, "Image associated with project not found. Would you like to select a replacement?", "Warning", JOptionPane.YES_NO_OPTION);
            
            if(returnValue == JOptionPane.YES_OPTION){
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new ImgFileFilter());
                int returnVal = chooser.showOpenDialog(parentFrame);
                
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    File chosen = chooser.getSelectedFile();
                    
                    return current.loadImage(chosen.getAbsolutePath());
                }
            } else {
                return null;
            }
        }
        
        return current.loadImage();
    }

    public BufferedImage getBufferedImage() {
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
        addPoint(pcdPoint);
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
        typeConfigList.forEach(_item -> counts.add(new AtomicInteger(0)));

        current.getPointTypes().forEach(type -> counts.get(typeIdentifierList.indexOf(type)).incrementAndGet());

        return counts;
    }
    
    strictfp public String getPcdRate(ArrayList<AtomicInteger> counts){
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
    
    strictfp public String getSecRate(ArrayList<AtomicInteger> counts){
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

    public boolean isAngleInitialized(){
        return current.isAngleInitialized();
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

    public ArrayList<ImageDataObject> getImageObjectListTODO() {
        ArrayList<ImageDataObject> imgData = new ArrayList<>();

        imageList.forEach(imageDataObject -> imgData.add(new ImageDataObject(imageDataObject)));

        return imgData;
    }
    
    public ArrayList<ImageDataObject> getImageObjectList(){
        return imageList;
    }

    public ArrayList<String> getImageNames(){
        ArrayList<String> strList = new ArrayList<>();

        imageList.forEach(imageDataObject -> strList.add(imageDataObject.getImageName()));

        return strList;
    }

    public void setImageObjectList(ArrayList<ImageDataObject> list) {
        imageList = list;
        current = null;
    }

    public boolean isInitialized(int row) {
        return imageList.get(row).isInitialized();
    }

    public boolean initializeAngles(){
        if(current.isAngleInitialized())
            return true;

        AngleLoadingDialog loading = new AngleLoadingDialog(parentFrame, pyproc, current.getImgPath(), current.getRawPointList());
        loading.setLocationRelativeTo(parentFrame);

        AngleWrapper angleWrapper = loading.showDialog();
        if(angleWrapper == null)
            return false;

        ArrayList<Double> angles = angleWrapper.getAngles();

        double avg = angles.stream().mapToDouble(a -> a).sum() / angles.size();
        current.mapAngles(angleWrapper);
        current.angleInitialize(avg);

        boolean result  = current.isAngleInitialized();

        if(!result)
            JOptionPane.showMessageDialog(parentFrame, "Unable to load angles, please save your work and restart the program", "Error", JOptionPane.ERROR_MESSAGE);

        return result;

    }

    public boolean inferImage() {
        if(current.isInitialized())
            return true;

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
            System.out.println("Marked images found, submitting for inference: " + idxList.size());
        
        LoadingMultipleDialogGUI inferGui = new LoadingMultipleDialogGUI(parentFrame, pyproc, idxList, imageList);
        inferGui.setLocationRelativeTo(parentFrame);
        ArrayList<ArrayList<PcdPoint>> pointlistList = inferGui.showDialog();
        
        if(Constant.DEBUG_MSG)
            System.out.println("Starting post-processing..");
        
        for (int i = 0; i < pointlistList.size(); i++) {
            if(Constant.DEBUG_MSG)
                System.out.println("Progress: " + (i + 1) + "/" + pointlistList.size());
            inferImage(idxList.get(i), pointlistList.get(i));
        }

        parentFrame.getFileListTable().setRowSelectionInterval(idxList.get(0), idxList.get(0));
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

    public PcdPoint getActualPointTODO(PcdPoint p) {
        return current.getActualPoint(p);
    }

    public void loadProject(File file) {
        ArrayList<ImageDataObject> objList = FileUtils.loadProject(file);

        if(objList == null)
            return;

        objList.forEach(imageDataObject -> imageDataObject.initializeOverlay(typeIdentifierList, typeIconList));

        setImageObjectList(objList);
    }
}

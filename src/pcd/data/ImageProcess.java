/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import hu.kazocsaba.imageviewer.Overlay;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import pcd.gui.MainFrame;
import pcd.gui.dialog.LoadingDialog;
import pcd.python.PythonProcess;
import pcd.utils.PcdColor;

/**
 *
 * @author ixenr
 */
public class ImageProcess {

    private final ArrayList<String> typeConfigList;
    private final ArrayList<Integer> typeIdentifierList;
    private final ArrayList<String> typeIconList;
    private final PythonProcess pyproc;
    private final ImageDataStorage imgStore = new ImageDataStorage();
    private final ImageDataObjectFactory imgFactory;
    private Frame parentFrame;

    public ImageProcess(ArrayList<String> typeConfigList, ArrayList<Integer> typeIdentifierList, ArrayList<String> typeIconList) {
        this.typeConfigList = typeConfigList;
        this.typeIdentifierList = typeIdentifierList;
        this.typeIconList = typeIconList;
        pyproc = new PythonProcess(5000, true);
        imgFactory = new ImageDataObjectFactory(pyproc, imgStore, typeIdentifierList, typeIconList);
    }

    public ArrayList<String> getTypeConfigList() {
        return typeConfigList;
    }

    public ArrayList<Integer> getTypeIdentifierList() {
        return typeIdentifierList;
    }

    public void addImage(String path) {
        imgFactory.addImage(path);
    }

    public BufferedImage getImageObject(int index) {
        return imgStore.getImage(index).loadImage();
    }

    public BufferedImage getImageObject() {
        return imgStore.getLastImage().loadImage();
    }

    public ImageDataObject getCurrentImage() {
        return imgStore.getCurrentImage();
    }

    public Overlay getOverlay() {
        return imgStore.getOverlay();
    }

    public boolean isInitialized() {
        return imgStore.isInitialized();
    }

    public boolean checkOpened(File file) throws IOException {
        try {
            return imgStore.checkOpened(file);
        } catch (IOException e) {
            throw e;
        }
    }

    public boolean inferImage() {
        LoadingDialog loading = new LoadingDialog(parentFrame);
        loading.setVisible(true);
        boolean result = imgStore.inferImage();
        loading.dispose();

        if (!result) {
            JOptionPane.showMessageDialog(parentFrame, "Nepodarilo se nacitat anotace, ulozte prosim svou praci a restartujte program", "Chyba", JOptionPane.ERROR);
        }

        return result;

    }

    public void setFrame(MainFrame aThis) {
        this.parentFrame = aThis;
    }

    public void addPoint(PcdPoint pcdPoint, String newClickType) {
        pcdPoint.setType(typeIdentifierList.get(typeConfigList.indexOf(newClickType)));
        imgStore.addPoint(pcdPoint);
    }

    public void remPoint(PcdPoint p) {
        imgStore.remPoint(p);
    }

    public PcdColor getColor(PcdPoint p) {
        int idx = typeIdentifierList.indexOf(p.getType());
        String s = typeIconList.get(idx);
        return parseColor(s);
    }
    
    public PcdColor getColor(String identifier){
        return parseColor(typeIconList.get(typeConfigList.indexOf(identifier)));
    }
    
    private PcdColor parseColor(String s){
        if(s.contains(".rgb")){
            int r = Integer.parseInt(s.substring(0, 2), 16);
            int g = Integer.parseInt(s.substring(2, 4), 16);
            int b = Integer.parseInt(s.substring(4, 6), 16);
            return new PcdColor(r, g, b);
        }
        
        return null;
    }

    public BufferedImage getIcon(PcdPoint value) {
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File("./icons/" + typeIconList.get(typeIdentifierList.indexOf(value.getType()))));
        } catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Nepodarilo se najit nebo nacist ikonu", "Chyba", JOptionPane.ERROR_MESSAGE);
        }
        
        return img;
    }
    
    public BufferedImage getIcon(String identifier) {
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File("./icons/" + typeIconList.get(typeConfigList.indexOf(identifier))));
        } catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Nepodarilo se najit nebo nacist ikonu", "Chyba", JOptionPane.ERROR_MESSAGE);
        }
        
        return img;
    }
    
    public String getPointTypeName(PcdPoint p){
        int idx = typeIdentifierList.indexOf(p.getType());
        return typeConfigList.get(idx);
    }
    
    public int getPointIdentifier(String s){
        int idx = typeConfigList.indexOf(s);
        return typeIdentifierList.get(idx);
    }

    public ArrayList<AtomicInteger> getCounts() {
        ArrayList<PcdPoint> ptList = imgStore.getCurrentImage().getPointList();
        ArrayList<AtomicInteger> counts = new ArrayList<>();
        typeConfigList.forEach(_item -> {
            counts.add(new AtomicInteger(0));
        });
        
        ptList.forEach(pcdPoint -> {
            counts.get(typeIdentifierList.indexOf(pcdPoint.getType())).incrementAndGet();
        });
        
        return counts;
    }

    public void dispose() {
        imgStore.dispose();
    }

}

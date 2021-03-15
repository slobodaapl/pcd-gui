/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import pcd.python.PythonProcess;

/**
 *
 * @author ixenr
 */
public class ImageProcess {
    
    private final ArrayList<String> typeConfigList;
    private final ArrayList<String> typeIconList;
    private final PythonProcess pyproc;
    private final ImageDataStorage imgStore = new ImageDataStorage();
    private final ImageDataObjectFactory imgFactory;

    public ImageProcess(ArrayList<String> typeConfigList, ArrayList<String> typeIconList) {
        this.typeConfigList = typeConfigList;
        this.typeIconList = typeIconList;
        pyproc = new PythonProcess(5000, true);
        imgFactory = new ImageDataObjectFactory(pyproc, imgStore);
    }

    public ArrayList<String> getTypeConfigList() {
        return typeConfigList;
    }
    
    public void addImage(String path){
        imgFactory.addImage(path);
    }
    
    public BufferedImage getImageObject(int index){
        return imgStore.getImage(index).loadImage();
    }

    public boolean checkOpened(File file) throws IOException {
        try{
            return imgStore.checkOpened(file);
        } catch(IOException e){
            throw e;
        }
    }
    
    
    
}

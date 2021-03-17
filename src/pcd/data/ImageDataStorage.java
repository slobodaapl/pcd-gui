/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import hu.kazocsaba.imageviewer.Overlay;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import pcd.python.PythonProcess;

/**
 *
 * @author ixenr
 */
public class ImageDataStorage {
    
    private ArrayList<ImageDataObject> imageList = new ArrayList<>();
    private ImageDataObject current;
    
    ImageDataObject getImage(int index){
        current = imageList.get(index);
        return current;
    }
    
    ImageDataObject getLastImage(){
        current = imageList.get(imageList.size() - 1);
        return current;
    }
    
    ImageDataObject getCurrentImage(){
        return current;
    }
    
    Overlay getOverlay(){
        return current.getOverlay();
    }
    
    void deleteImage(int index){
        imageList.remove(index);
    }
    
   void addImage(ImageDataObject img){
        imageList.add(img);
    }
    
    boolean checkOpened(File f) throws IOException {
        boolean opened = false;
        
        try{
            for (ImageDataObject imageDataObject : imageList) {
                opened = opened | imageDataObject.fileMatch(f.getPath());
            }
        }catch(IOException e){
                throw e;
        }
        
        return opened;
    }

    boolean isInitialized() {
        return current.isInitialized();
    }
    
    boolean inferImage(PythonProcess py, ArrayList<Integer> typeIdentifierList, ArrayList<String> typeIconList){
        current.initialize(py, typeIdentifierList, typeIconList);
        return current.isInitialized();
    }

    void addPoint(PcdPoint pcdPoint) {
        current.addPoint(pcdPoint);
    }

    void remPoint(PcdPoint p) {
        current.remPoint(p);
    }

    void dispose() {
        imageList.remove(current);
        current = null;
    }

    ArrayList<ImageDataObject> getImageObjectList() {
        return imageList;
    }

    void setImageObjectList(ArrayList<ImageDataObject> list) {
        imageList = list;
        current = null;
    }
    
}

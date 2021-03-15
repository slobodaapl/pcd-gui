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

/**
 *
 * @author ixenr
 */
public class ImageDataStorage {
    
    private final ArrayList<ImageDataObject> imageList = new ArrayList<>();
    private ImageDataObject current;
    
    public ImageDataObject getImage(int index){
        current = imageList.get(index);
        return current;
    }
    
    public ImageDataObject getLastImage(){
        current = imageList.get(imageList.size() - 1);
        return current;
    }
    
    public ImageDataObject getCurrentImage(){
        return current;
    }
    
    public Overlay getOverlay(){
        return current.getOverlay();
    }
    
    public void deleteImage(int index){
        imageList.remove(index);
    }
    
    public void addImage(ImageDataObject img){
        imageList.add(img);
    }
    
    public boolean checkOpened(File f) throws IOException {
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

    public boolean isInitialized() {
        return current.isInitialized();
    }
    
    public boolean inferImage(){
        current.initialize();
        return current.isInitialized();
    }
    
}

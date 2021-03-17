/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import java.util.ArrayList;
import pcd.python.PythonProcess;

/**
 *
 * @author ixenr
 */
public class ImageDataObjectFactory {
    
    private final PythonProcess py;
    private final ImageDataStorage store;

    public ImageDataObjectFactory(PythonProcess py, ImageDataStorage store) {
        this.store = store;
        this.py = py;
    }
    
    private ImageDataObject makeImage(String path){
        return new ImageDataObject(path);
    }
    
    public void addImage(String path){
        store.addImage(makeImage(path));
    }
    
}

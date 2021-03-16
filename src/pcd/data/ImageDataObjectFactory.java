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
    private final ArrayList<Integer> typeIdentifierList;
    private final ArrayList<String> typeIconList;

    public ImageDataObjectFactory(PythonProcess py, ImageDataStorage store, ArrayList<Integer> typeIdentifierList, ArrayList<String> typeIconList) {
        this.store = store;
        this.py = py;
        this.typeIdentifierList = typeIdentifierList;
        this.typeIconList = typeIconList;
    }
    
    private ImageDataObject makeImage(String path){
        return new ImageDataObject(path, py, typeIdentifierList, typeIconList);
    }
    
    public void addImage(String path){
        store.addImage(makeImage(path));
    }
    
}

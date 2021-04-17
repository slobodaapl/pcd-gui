/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import pcd.python.PythonProcess;

/**
 *
 * @author ixenr
 */
public class ImageDataObjectFactory {

    private final ImageDataStorage store;

    public ImageDataObjectFactory(ImageDataStorage store) {
        this.store = store;
    }

    private ImageDataObject makeImage(String path) {
        return new ImageDataObject(path);
    }

    public void addImage(String path) {
        store.addImage(makeImage(path));
    }

}

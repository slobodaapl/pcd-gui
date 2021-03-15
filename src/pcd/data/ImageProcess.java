/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.data;

import java.util.ArrayList;

/**
 *
 * @author ixenr
 */
public class ImageProcess {
    
    private final ArrayList<String> typeConfigList;
    private final ArrayList<String> typeIconList;

    public ImageProcess(ArrayList<String> typeConfigList, ArrayList<String> typeIconList) {
        this.typeConfigList = typeConfigList;
        this.typeIconList = typeIconList;
    }

    public ArrayList<String> getTypeConfigList() {
        return typeConfigList;
    }
    
    
    
    
    
}

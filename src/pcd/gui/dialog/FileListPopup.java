/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.dialog;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author ixenr
 */
public class FileListPopup extends JPopupMenu {
    
    JMenuItem deleter;
    
    public FileListPopup(){
        deleter = new JMenuItem("Vymazat");
        super.add(deleter);
    }
    
}

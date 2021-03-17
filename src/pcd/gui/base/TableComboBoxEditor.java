/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 *
 * @author ixenr
 */
public class TableComboBoxEditor extends DefaultCellEditor {

    public TableComboBoxEditor(String[] items) {
        super(new JComboBox(items));
    }
}

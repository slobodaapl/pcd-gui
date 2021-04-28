/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pcd.data.ImageDataStorage;

/**
 *
 * @author Noemi Farkas This class is for filtering through files to find a
 * projects that corresponding to the type used by the program. Extends
 * FilFilter class
 */
public class ProjectFileFilter extends FileFilter {

    private static final Logger LOGGER = LogManager.getLogger(FileFilter.class);
    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Bundle");
    private final String[] accepted = {"pcd"};

    Set<String> acceptedSet = new HashSet<>(Arrays.asList(accepted));

    /**
     * It checks whether the choosen file is a project file that the program can
     * accept or not.
     *
     * @param f choosen file
     * @return boolean true if the file is a project file, false if not
     */
    @Override
    public boolean accept(File f) {
        String path = f.getName();
        String ext;
        try {
            ext = FilenameUtils.getExtension(path).toLowerCase();
        } catch (IllegalArgumentException e) {
            LOGGER.error("Cannor accept file.", e);
            return false;
        }
        return acceptedSet.contains(ext) | f.isDirectory();
    }

    /**
     * returns the description of the file.
     *
     * @return String "Project files"
     */
    @Override
    public String getDescription() {
        String pf = bundle.getString("ProjectFileFilter.pf");
        return pf;
    }

}

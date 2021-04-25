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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pcd.data.ImageDataStorage;

/**
 *
 * @author ixenr
 */
public class ProjectFileFilter extends FileFilter {
     private static final Logger LOGGER = LogManager.getLogger(FileFilter.class);
    private final String[] accepted = {"pcd"};

    Set<String> acceptedSet = new HashSet<>(Arrays.asList(accepted));

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

    @Override
    public String getDescription() {
        String pf ="Project files";
        return pf;
    }

}

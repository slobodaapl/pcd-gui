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
 * @author Noemi Farkas
 * This class is responsible for filtering image files that will be shown in the JFile Chooser
 */
public class ImgFileFilter extends FileFilter {
     private static final Logger LOGGER = LogManager.getLogger(FileFilter.class);
    private final String[] accepted = {"jpg", "tiff", "tif", "png", "bmp",
        "webmp", "gif", "hdr", "jpeg"};

    Set<String> acceptedSet = new HashSet<>(Arrays.asList(accepted));

  /**
 * It checks whether the choosen file is a img file that the program can accept or not.
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
            String fcba = "File cannot be accepted!";
            LOGGER.info(fcba, e);
            return false;
        }
        return acceptedSet.contains(ext) | f.isDirectory();
    }
/**
 * returns the description of the file.
 * @return String "Image files"
 */
    @Override
    public String getDescription() {
        String imgf = "Image files";
        return imgf;
    }

}

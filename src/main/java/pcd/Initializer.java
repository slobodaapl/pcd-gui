package pcd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import static javafx.application.Platform.exit;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pcd.data.ImageDataStorage;
import pcd.gui.MainFrame;
import pcd.utils.Constant;
import pcd.utils.FileUtils;

/**
 * @author Tibor Sloboda
 * 
 * Prepares missing directories and parses the configuration files, adding
 * missing colors where necessary for cilia types.
 *<p>
 * Finally it instantiates {@link pcd.data.ImageDataStorage}, which in turn
 * runs the Python process and starts the server. The GUI only opens once
 * Python is connected and loaded.
 * 
 */
public class Initializer {
    private static final Logger LOGGER = LogManager.getLogger(Initializer.class);
    
    /**
     * List of cilia types
     */
    private final ArrayList<String> typeConfigList = new ArrayList<>();
    /**
     * List of type IDs
     */
    private final ArrayList<Integer> typeIdentifierList = new ArrayList<>();
    /**
     * List of color strings or icon paths
     */
    private final ArrayList<String> typeIconList = new ArrayList<>();
    /**
     * List of the 'types' of the types of cilia (healthy/unhealthy)
     */
    private final ArrayList<String> typeTypeList = new ArrayList<>();

    /**
     * Parses lines from the loaded configuration file.
     * It splits the fields of the configuration into respective lists
     * and adds random colors to those types that are missing it.
     * 
     * @param list The list of lines from the configuration file
     */
    private void splitConfig(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String string = list.get(i);
            String[] parts = string.split(",");

            if (parts.length < 3) {
                continue;
            }

            typeConfigList.add(parts[0]);
            typeIdentifierList.add(Integer.parseInt(parts[1]));
            typeTypeList.add(parts[2]);

            if (parts.length == 3) {
                int r = ThreadLocalRandom.current().nextInt(0, 256);
                int g = ThreadLocalRandom.current().nextInt(0, 256);
                int b = ThreadLocalRandom.current().nextInt(0, 256);

                String rs = "";
                String gs = "";
                String bs = "";

                if (r <= 15) {
                    rs += "0";
                }
                if (g <= 15) {
                    gs += "0";
                }
                if (b <= 15) {
                    bs += "0";
                }

                String hexColor = rs + Integer.toHexString(r);
                hexColor += gs + Integer.toHexString(g);
                hexColor += bs + Integer.toHexString(b);
                hexColor += ".rgb";

                typeIconList.add(hexColor);
                try {
                    FileUtils.updateRGB(Constant.CONFIG_PATH, i, hexColor);
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            } else {
                typeIconList.add(parts[3]);
            }
        }
    }
    
    /**
     * Creates missing folders and reads the config file using {@link pcd.utils.FileUtils}
     * before passing the contents into {@link pcd.Initializer#splitConfig(java.util.ArrayList)}
     */
    void setup(){
        File f = new File("Logs");
        if(!f.exists())
            f.mkdir();

        if (FileUtils.checkConfigFile(Constant.CONFIG_PATH)) {
            try {
                splitConfig(Objects.requireNonNull(FileUtils.readConfigFile(Constant.CONFIG_PATH)));
            } catch (IOException e) {
                String split = "SplitConfig failed!";
                LOGGER.error(split, e);
                exit();
            }
        }

        FileUtils.prepCache();
    }

    /**
     * Initializes the {@link pcd.data.ImageDataStorage} and runs the primary GUI.
     * It decides which initializer to use for the GUI based on whether or not
     * a project file was present in passed arguments to main.
     * 
     * Finally it adds a listener to the main GUI window to stop the process and exit the program successfully.
     * A {@link System#exit(int)} is used to make the program exit once the process is terminated. This is the
     * last line of code in the project, so it is safe from unhandled exceptions.
     * 
     * @see MainFrame
     * 
     * @param projectFile The path in {@link String} format to any passed project files
     */
    void run(String projectFile) {
        
        setup();

        ImageDataStorage imgDataStorage = new ImageDataStorage(typeConfigList, typeIdentifierList, typeIconList, typeTypeList);
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame;
            if(projectFile.isEmpty())
                mainFrame = new MainFrame(imgDataStorage);
            else
                mainFrame = new MainFrame(imgDataStorage, projectFile);
            mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    mainFrame.saveProjectTemp();
                    imgDataStorage.stopProcess();
                    System.exit(0);
                }
            });
            mainFrame.setVisible(true);
        });

    }

}

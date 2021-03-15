package pcd;

import java.io.IOException;
import java.util.ArrayList;
import static javafx.application.Platform.exit;
import pcd.data.ImageProcess;
import pcd.gui.MainFrame;
import pcd.utils.FileUtils;

public class Initializer {

    private static final String CONFIG_PATH = "celltypes_config.conf";

    private final ArrayList<String> typeConfigList = new ArrayList<>();
    private final ArrayList<String> typeIconList = new ArrayList<>();

    private void splitConfig(ArrayList<String> list) {
        for (String string : list) {
            String[] parts = string.split(",");
            typeConfigList.add(parts[0]);
            typeIconList.add(parts[1]);
        }
    }

    void run() {

        if (FileUtils.checkConfigFile(CONFIG_PATH))
            try {
            splitConfig(FileUtils.readConfigFile(CONFIG_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            exit();
        }
        
        ImageProcess imgProc = new ImageProcess(typeConfigList, typeIconList);
        MainFrame mainFrame = new MainFrame(imgProc);
        mainFrame.setVisible(true);

    }

}

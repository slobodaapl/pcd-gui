package pcd;

import java.io.IOException;
import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import static javafx.application.Platform.exit;
import pcd.data.ImageDataStorage;
import pcd.gui.MainFrame;
import pcd.utils.FileUtils;

public class Initializer {

    private static final String CONFIG_PATH = "celltypes_config.conf";

    private final ArrayList<String> typeConfigList = new ArrayList<>();
    private final ArrayList<Integer> typeIdentifierList = new ArrayList<>();
    private final ArrayList<String> typeIconList = new ArrayList<>();

    private void splitConfig(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String string = list.get(i);
            String[] parts = string.split(",");

            if (parts.length < 2) {
                continue;
            }

            typeConfigList.add(parts[0]);
            typeIdentifierList.add(Integer.parseInt(parts[1]));

            if (parts.length == 2) {
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
                    FileUtils.updateRGB(CONFIG_PATH, i, hexColor);
                } catch (IOException e) {
                   ImageDataStorage.getLOGGER().error("",e);
                }
            } else {
                typeIconList.add(parts[2]);
            }
        }
    }

    void run() {

        if (FileUtils.checkConfigFile(CONFIG_PATH)) {
            try {
                splitConfig(FileUtils.readConfigFile(CONFIG_PATH));
            } catch (IOException e) {
                ImageDataStorage.getLOGGER().error("SplitConfig failed!",e);
                exit();
            }
        }

        FileUtils.prepCache();

        ImageDataStorage imgDataStorage = new ImageDataStorage(typeConfigList, typeIdentifierList, typeIconList);
        MainFrame mainFrame = new MainFrame(imgDataStorage);
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                mainFrame.saveProjectTemp();
                imgDataStorage.stopProcess();
                System.exit(0);
            }
        });
        mainFrame.setVisible(true);

    }

}

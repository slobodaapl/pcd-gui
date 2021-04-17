package pcd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import static javafx.application.Platform.exit;
import javax.swing.SwingUtilities;
import pcd.data.ImageDataStorage;
import pcd.gui.MainFrame;
import pcd.utils.Constant;
import pcd.utils.FileUtils;

public class Initializer {

    private final ArrayList<String> typeConfigList = new ArrayList<>();
    private final ArrayList<Integer> typeIdentifierList = new ArrayList<>();
    private final ArrayList<String> typeIconList = new ArrayList<>();
    private final ArrayList<String> typeTypeList = new ArrayList<>();

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
                    ImageDataStorage.getLOGGER().error("", e);
                }
            } else {
                typeIconList.add(parts[3]);
            }
        }
    }
    
    void setup(){
        File f = new File("Logs");
        if(!f.exists())
            f.mkdir();

        if (FileUtils.checkConfigFile(Constant.CONFIG_PATH)) {
            try {
                splitConfig(Objects.requireNonNull(FileUtils.readConfigFile(Constant.CONFIG_PATH)));
            } catch (IOException e) {
                ImageDataStorage.getLOGGER().error("SplitConfig failed!", e);
                exit();
            }
        }

        FileUtils.prepCache();
    }

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

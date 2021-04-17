/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pcd.data.ImageDataObject;
import pcd.data.ImageDataStorage;
import pcd.data.PcdPoint;
import pcd.gui.MainFrame;

/**
 *
 * @author ixenr
 */
public final class FileUtils {

    private final static String[] HEADERS = {"tag", "count"};

    public static void updateRGB(String CONFIG_PATH, int i, String hexColor) throws IOException {
        Path p = Paths.get(CONFIG_PATH);
        List<String> fileContent = new ArrayList<>(Files.readAllLines(p, StandardCharsets.UTF_8));
        int start_i = 0;

        while (true) {
            if (isComment(fileContent.get(start_i))) {
                start_i++;
            } else {
                break;
            }
        }

        fileContent.set(start_i + i, fileContent.get(start_i + i) + "," + hexColor);

        Files.write(p, fileContent, StandardCharsets.UTF_8);
    }

    public static void prepCache() {
        File f = new File("cache");
        f.mkdir();
    }

    public static boolean isComment(String line) {
        int i = StringUtils.indexNonWhitespace(line);

        if (i == -1 || line.isEmpty()) {
            return true;
        }

        return line.charAt(i) == '#';
    }

    public static ArrayList<String> readConfigFile(String path) throws IOException {
        ArrayList<String> arrayList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                if (!isComment(line)) {
                    arrayList.add(line);
                }
                line = br.readLine();
            }
        }

        if (!arrayList.isEmpty()) {
            return arrayList;
        }

        return null;
    }

    public static boolean loadImageFile(File f, DefaultTableModel t, ImageDataStorage stor) {
        try {
            if (stor.checkOpened(f)) {
                return false;
            }

            stor.addImage(f.getAbsolutePath());
            t.addRow(new Object[]{false, f.getName(), ""});

        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("Adding image failed!", e);
            return false;
        }
        return true;
    }

    public static boolean checkConfigFile(String path) {
        File file = new File(path);

        if (file.exists() && file.canRead()) {
            return true;
        } else if (file.exists()) {
            return false;
        } else {
            try {
                try (FileOutputStream fos = new FileOutputStream(file); BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
                    bw.write("# Na pridani novych typu pouzite format: \"nazev_bez_mezer,unikatne_cislo,typ_poskozeni\"");
                    bw.newLine();
                    
                    bw.write("# Typy poskozeni muzou byt n, p, nebo s (normalni, primarni, sekondarni)");
                    bw.newLine();

                    bw.write("# Muzete taky priad vlastni ikonu pro typ cilie. Dejte soubor ikony do zlozky 'icons' o velkosti 25x25");
                    bw.newLine();

                    bw.write("# a pridejte nazev ikony za unikatne cislo, oddelene carkou. Nazev ikony musi byt bez mezer.");
                    bw.newLine();

                    bw.newLine();

                    bw.write("# Priklad noveho typu: \"sekondarni_bez_steny,p,3\" ... nebo \"sekondarni_bez_steny,p,3,moje_ikona.ico\"");
                    bw.newLine();

                    bw.newLine();

                    bw.write("normal,0,n,00ff00.rgb");
                    bw.newLine();

                    bw.write("disorg.MT,1,p,ff0000.rgb");
                    bw.newLine();

                    bw.write("sec.defects,2,s,0000ff.rgb");
                    bw.newLine();
                    
                    bw.write("compound,3,s,00ffff.rgb");
                    bw.newLine();
                    
                    bw.write("free_axonema,4,s,ffff00.rgb");
                    bw.newLine();
                    
                    bw.write("CCD,5,p,ff8000.rgb");
                    bw.newLine();
                    
                    bw.write("periph.MT-,6,p,ff00ff.rgb");
                }
            } catch (IOException e) {
                ImageDataStorage.getLOGGER().error("", e);
                return false;
            }

            return true;
        }

    }

    public static void saveCSVSingle(Path savePath, ArrayList<AtomicInteger> counts, ArrayList<String> typeConfigList) throws IOException {
        try {
            try (FileWriter out = new FileWriter(savePath.toString())) {
                CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS));

                for (int i = 0; i < counts.size(); i++) {
                    printer.printRecord(typeConfigList.get(i), counts.get(i));
                }

                printer.close(true);
            }

        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("", e);
            throw e;
        }
    }

    public static void saveProject(Path savePath, ArrayList<ImageDataObject> imgObjectList) {
        File saveFile = new File(savePath.toString());
        if (saveFile.exists() && saveFile.isFile()) {
            saveFile.delete();
        }

        try (FileOutputStream fos = new FileOutputStream(saveFile); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            try {
                oos.writeObject(imgObjectList);
            } catch (NotSerializableException e) {
                ImageDataStorage.getLOGGER().error("", e);
            }
        } catch (IOException e) {
            ImageDataStorage.getLOGGER().error("", e);
        }
    }
    
    public static void saveCacheAll(ArrayList<ImageDataObject> imgs, String path) throws IOException {
        File zipFile = new File(path);
        boolean result = zipFile.createNewFile();
        
        if(!result)
            return;
        
        String[] pointheader = new String[]{"x", "y", "class"};
        
        if(!result)
            return;
        
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipFile));
        
        try (ZipOutputStream out = new ZipOutputStream(bos)){
            for (ImageDataObject img : imgs) {
                String imgName = img.getImageName();
                File f = new File(img.getImgPath());
                
                if(!f.exists())
                    continue;
                
                out.putNextEntry(new ZipEntry(imgName));
                Files.copy(f.toPath(), out);
                out.closeEntry();
                
                out.putNextEntry(new ZipEntry(imgName + ".csv"));
                CSVPrinter writer = new CSVPrinter(new PrintStream(out), CSVFormat.DEFAULT.withHeader(pointheader));
                
                for (PcdPoint pcdPoint : img.getPointList()) {
                    writer.printRecord(pcdPoint.x, pcdPoint.y, pcdPoint.getType());
                }
                
                out.closeEntry();
            }
        } catch (IOException e){
            throw e;
        }
        
        bos.close();
    }
    
    public static void saveCacheItem(ImageDataObject imgObj) throws IOException {
        
        File saveFile = new File(imgObj.getImgPath() + ".annot");

        if (!imgObj.isInitialized()) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(saveFile); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            try {
                oos.writeObject(imgObj.getPointList());
            } catch (NotSerializableException e) {
                throw e;
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public static Path getCSVSaveLocation(MainFrame parentFrame) {
        JFileChooser chooser = new JFileChooser();

        chooser.setSelectedFile(new File("data.csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("Comma-Separated Values File", "csv"));

        int userSelection = chooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File saveFile = chooser.getSelectedFile();
            if (saveFile != null) {
                return Paths.get(saveFile.getAbsolutePath());
            }
        }

        return null;
    }

    public static void saveCSVMultiple(Path csvSaveLocation, ArrayList<ImageDataObject> imageObjectList, ArrayList<String> typeConfigList) throws IOException {
        try {
            try (FileWriter out = new FileWriter(csvSaveLocation.toString())) {
                ArrayList<String> conf = (ArrayList<String>) typeConfigList.clone();
                conf.add(0, "");
                CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(conf.toArray(new String[conf.size()])));

                ArrayList<Object> results = getAtomicArrayCSV(typeConfigList.size());

                for (ImageDataObject imageDataObject : imageObjectList) {
                    ArrayList<Object> record = (ArrayList<Object>) getAtomicArrayCSV(typeConfigList.size());
                    record.set(0, Paths.get(imageDataObject.getImgPath()).getFileName());

                    imageDataObject.getPointList().forEach(pcdPoint -> {
                        ((AtomicInteger) record.get(typeConfigList.indexOf(pcdPoint.getTypeName()) + 1)).addAndGet(1);
                    });

                    for (int i = 1; i < record.size(); i++) {
                        ((AtomicInteger) results.get(i)).addAndGet(((AtomicInteger) record.get(i)).get());
                    }

                    printer.printRecord(record);
                }

                printer.printRecord(results);

                printer.close(true);
            }

        } catch (IOException e) {
            throw e;
        }
    }

    public static ArrayList<Object> getAtomicArrayCSV(int size) {
        ArrayList<Object> list = new ArrayList<>();
        list.add("");

        for (int i = 0; i < size; i++) {
            list.add(new AtomicInteger(0));
        }

        return list;
    }

    private FileUtils() {
    }
}

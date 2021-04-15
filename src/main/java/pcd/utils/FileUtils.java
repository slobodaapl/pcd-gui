/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import com.sun.xml.internal.stream.writers.UTF8OutputStreamWriter;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
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
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

        Document doc = dBuilder.newDocument();
        Element project = doc.createElement("project");
        doc.appendChild(project);

        for (ImageDataObject imageDataObject : imgObjectList) {

            Element imageDataElement = doc.createElement("imagedata");
            Attr idAttribute = doc.createAttribute("path");
            idAttribute.setValue(imageDataObject.getImgPath());
            imageDataElement.setAttributeNode(idAttribute);

            if (imageDataObject.getPointList() != null) {
                for (PcdPoint pcdPoint : imageDataObject.getPointList()) {
                    Element point = doc.createElement("point");

                    Element xcoord = doc.createElement("x");
                    xcoord.appendChild(doc.createTextNode(Integer.toString(pcdPoint.x)));
                    point.appendChild(xcoord);

                    Element ycoord = doc.createElement("y");
                    ycoord.appendChild(doc.createTextNode(Integer.toString(pcdPoint.y)));
                    point.appendChild(ycoord);

                    Element typeid = doc.createElement("typeid");
                    typeid.appendChild(doc.createTextNode(Integer.toString(pcdPoint.getType())));
                    point.appendChild(typeid);

                    Element typename = doc.createElement("typename");
                    typename.appendChild(doc.createTextNode(pcdPoint.getTypeName()));
                    point.appendChild(typename);

                    Element score = doc.createElement("score");
                    score.appendChild(doc.createTextNode(Double.toString(pcdPoint.getScore())));
                    point.appendChild(score);

                    Element angle = doc.createElement("angle");
                    angle.appendChild(doc.createTextNode(Double.toString(-0.)));
                    point.appendChild(angle);

                    imageDataElement.appendChild(point);
                }
            }

            project.appendChild(imageDataElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transform;

        try {
            transform = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return;
        }

        DOMSource source = new DOMSource(doc);
        
        UTF8OutputStreamWriter writer;
        try{
            writer = new UTF8OutputStreamWriter(new FileOutputStream(new File(savePath.toString())));
        } catch(FileNotFoundException e){
            e.printStackTrace();
            return;
        }
        
        StreamResult result = new StreamResult(writer);

        try {
            transform.transform(source, result);
            writer.close();
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
            return;
        }

    }

    public static ArrayList<ImageDataObject> loadProject(File file) {
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringComments(true);
        dbFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder dBuilder;
        ArrayList<ImageDataObject> imgList = new ArrayList<>();
        Document doc;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
        
        doc.getDocumentElement().normalize();
        System.out.println(doc.getDocumentElement().getNodeName());

        NodeList rootNodes = doc.getChildNodes();
        NodeList imageNodes = rootNodes.item(0).getChildNodes();
        
        for (int i = 0; i < imageNodes.getLength(); i++) {
            imgList.add(new ImageDataObject(((Element) imageNodes.item(i)).getAttribute("path")));

            NodeList pointNodeList = imageNodes.item(i).getChildNodes();
            int pointNodeCount = pointNodeList.getLength();

            ArrayList<PcdPoint> pointList = new ArrayList<>();

            for (int j = 0; j < pointNodeCount; j++) {
                Element point = (Element) pointNodeList.item(j);

                NodeList attributeList = point.getChildNodes();

                Element xcoord = (Element) attributeList.item(0);
                Element ycoord = (Element) attributeList.item(1);
                Element typeId = (Element) attributeList.item(2);
                Element typeName = (Element) attributeList.item(3);
                Element score = (Element) attributeList.item(4);
                Element angle = (Element) attributeList.item(5);

                PcdPoint current = new PcdPoint();

                current.setX(Integer.parseInt(xcoord.getTextContent()));
                current.setY(Integer.parseInt(ycoord.getTextContent()));
                current.setType(Integer.parseInt(typeId.getTextContent()));
                current.setTypeName(typeName.getTextContent());
                current.setScore(Double.parseDouble(score.getTextContent()));
                
                pointList.add(current);

            }

            if (pointNodeCount != 0) {
                imgList.get(i).setPointList(pointList);
            }

        }

        return imgList;
    }

    public static void saveCacheAll(ArrayList<ImageDataObject> imgs, String path) throws IOException {
        File zipFile = new File(path);
        boolean result = zipFile.createNewFile();

        if (!result) {
            return;
        }

        String[] pointheader = new String[]{"x", "y", "class"};

        if (!result) {
            return;
        }

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipFile));

        try (ZipOutputStream out = new ZipOutputStream(bos)) {
            for (ImageDataObject img : imgs) {
                String imgName = img.getImageName();
                File f = new File(img.getImgPath());

                if (!f.exists()) {
                    continue;
                }

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
        } catch (IOException e) {
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

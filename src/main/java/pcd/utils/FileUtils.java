/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pcd.data.ImageDataObject;
import pcd.data.ImageDataStorage;
import pcd.data.PcdPoint;
import pcd.gui.MainFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.*;

/**
 *
 * @author ixenr
 */
public final class FileUtils {

    private final static String[] HEADERS = {"tag", "count"};
    private static final Logger LOGGER = LogManager.getLogger(FileUtils.class);

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

    public static boolean prepCache() {
        File f = new File("cache");
        return f.mkdir();
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
        if (stor.checkOpened(f)) {
            return false;
        }

        stor.addImage(f.getAbsolutePath());
        t.addRow(new Object[]{false, f.getName(), ""});

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
                    String tant = "To add new type, use the following format: \"name_without_spaces,unique_number,corruption_type";
                    String toc = "Type of corruptions are the following: n,p or s (normal, primary, secondary)";
                    String wtf = "No idea how to translate this";
                    String icnm = " give the name of the icon after the unique number,separated by a comma. Name of the icon must be without spaces.";
                    String nty = "Sample for new type: \"secondary_wihout_walls,p,3\" ...or \"secondary_without_walls,p,3,my_icon.ico\"";
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
                LOGGER.error("", e);
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
            LOGGER.error("", e);
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
        String pro = "project";
        String imd = "imagedata";
        String pat = "path";
        String poi = "points";
        String ang = "angle";
        String av = "avg";
        String st = "std";

        Element project = doc.createElement(pro);
        doc.appendChild(project);

        for (ImageDataObject imageDataObject : imgObjectList) {

            Element imageDataElement = doc.createElement(imd);
            Attr idAttribute = doc.createAttribute(pat);
            Attr pointInitAttribute = doc.createAttribute(poi);
            Attr angleInitAttribute = doc.createAttribute(ang);
            Attr avgAngleAttribute = doc.createAttribute(av);
            Attr stdAngleAttribute = doc.createAttribute(st);

            idAttribute.setValue(imageDataObject.getImgPath());
            pointInitAttribute.setValue(Boolean.toString(imageDataObject.isInitialized()));
            angleInitAttribute.setValue(Boolean.toString(imageDataObject.isAngleInitialized()));
            avgAngleAttribute.setValue(Double.toString(imageDataObject.getAvgAngle()));
            stdAngleAttribute.setValue(Double.toString(imageDataObject.getStdAngle()));

            imageDataElement.setAttributeNode(idAttribute);
            imageDataElement.setAttributeNode(pointInitAttribute);
            imageDataElement.setAttributeNode(angleInitAttribute);
            imageDataElement.setAttributeNode(avgAngleAttribute);
            imageDataElement.setAttributeNode(stdAngleAttribute);

            if (imageDataObject.getPointList() != null) {
                String pon = "point";
                String ti = "typeid";
                String tyn = "typename";
                String sc = "score";

                for (PcdPoint pcdPoint : imageDataObject.getPointList()) {
                    Element point = doc.createElement(pon);

                    Element xcoord = doc.createElement("x");
                    xcoord.appendChild(doc.createTextNode(Integer.toString(pcdPoint.x)));
                    point.appendChild(xcoord);

                    Element ycoord = doc.createElement("y");
                    ycoord.appendChild(doc.createTextNode(Integer.toString(pcdPoint.y)));
                    point.appendChild(ycoord);

                    Element typeid = doc.createElement(ti);
                    typeid.appendChild(doc.createTextNode(Integer.toString(pcdPoint.getType())));
                    point.appendChild(typeid);

                    Element typename = doc.createElement(tyn);
                    typename.appendChild(doc.createTextNode(pcdPoint.getTypeName()));
                    point.appendChild(typename);

                    Element score = doc.createElement(sc);
                    score.appendChild(doc.createTextNode(Double.toString(pcdPoint.getScore())));
                    point.appendChild(score);

                    Element angle = doc.createElement(ang);
                    angle.appendChild(doc.createTextNode(Double.toString(pcdPoint.getAngle())));
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

        FileOutputStream writer;
        try {
            writer = new FileOutputStream(savePath.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        StreamResult result = new StreamResult(writer);

        try {
            transform.transform(source, result);
            writer.close();
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
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
        
        if(imageNodes.getLength() >= 100)
            return null;

        for (int i = 0; i < imageNodes.getLength(); i++) {

            ImageDataObject newImgObj = new ImageDataObject(((Element) imageNodes.item(i)).getAttribute("path"));
            newImgObj.setAngleInitialized(Boolean.parseBoolean(((Element) imageNodes.item(i)).getAttribute("angle")));
            newImgObj.setAvgAngle(Double.parseDouble(((Element) imageNodes.item(i)).getAttribute("avg")));
            newImgObj.setStdAngle(Double.parseDouble(((Element) imageNodes.item(i)).getAttribute("std")));

            NodeList pointNodeList = imageNodes.item(i).getChildNodes();
            int pointNodeCount = pointNodeList.getLength();
            
            if(pointNodeCount > 400)
                continue;
            
            imgList.add(newImgObj);

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
                current.setAngle(Double.parseDouble(angle.getTextContent()));

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
        String cls = "class";
        String[] pointheader = new String[]{"x", "y", cls};

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
        }

        bos.close();
    }

    public static Path getCSVSaveLocation(MainFrame parentFrame) {
        JFileChooser chooser = new JFileChooser();

        chooser.setSelectedFile(new File("data.csv"));
        String csvf = "Comma-Separated Values File";
        chooser.setFileFilter(new FileNameExtensionFilter(csvf, "csv"));

        int userSelection = chooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File saveFile = chooser.getSelectedFile();
            if (saveFile != null) {
                return Paths.get(saveFile.getAbsolutePath());
            }
        }

        return null;
    }

    public static void saveCSVMultiple(Path csvSaveLocation, ImageDataStorage imageStore) throws IOException {
        if (csvSaveLocation == null) {
            return;
        }

        try (FileWriter out = new FileWriter(csvSaveLocation.toString())) {
            ArrayList<String> conf = (ArrayList<String>) imageStore.getTypeConfigList().clone();
            String mangle = "mean angle";
            String stdangle = "std angle";
            conf.add(0, "");
            conf.add("pdr");
            conf.add("sdr");
            conf.add(mangle);
            conf.add(stdangle);
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(conf.toArray(new String[0])));

            ArrayList<AtomicInteger> results = new ArrayList<>();
            imageStore.getTypeConfigList().forEach(_item -> {
                results.add(new AtomicInteger(0));
            });

            for (ImageDataObject imageDataObject : imageStore.getImageObjectList()) {
                ArrayList<Object> record = new ArrayList<>();
                ArrayList<AtomicInteger> counts = imageStore.getCounts(imageDataObject);

                record.add(Paths.get(imageDataObject.getImgPath()).getFileName());
                record.addAll(counts);

                record.add(imageStore.getPcdRate(counts));
                record.add(imageStore.getSecRate(counts));
                record.add(imageDataObject.getAvgAngle());
                record.add(imageDataObject.getStdAngle());

                for (int i = 0; i < results.size(); i++) {
                    ((AtomicInteger) results.get(i)).addAndGet(((AtomicInteger) record.get(i + 1)).get());
                }

                printer.printRecord(record);
            }

            String pdr = imageStore.getPcdRate(results);
            String sdr = imageStore.getSecRate(results);

            ArrayList<Object> resultsRecord = new ArrayList<>();
            resultsRecord.addAll(results);
            resultsRecord.add(pdr);
            resultsRecord.add(sdr);
            resultsRecord.add("");
            resultsRecord.add("");
            resultsRecord.add(0, "");

            printer.printRecord(resultsRecord);

            printer.close(true);
        }

    }

    private FileUtils() {
    }
}

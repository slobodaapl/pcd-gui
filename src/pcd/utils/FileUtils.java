/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ixenr
 */
public final class FileUtils {

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
        File f = new File("./cache");
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

    public static boolean checkConfigFile(String path) {
        File file = new File(path);

        if (file.exists() && file.canRead()) {
            return true;
        } else if (file.exists()) {
            return false;
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
                    bw.write("# Na pridani novych typu pouzite format: \"nazev_bez_mezer,unikatne_cislo\"");
                    bw.newLine();
                    
                    bw.write("# Muzete taky priad vlastni ikonu pro typ. Dejte soubor ikony do zlozky 'icons' o velkosti 25x25");
                    bw.newLine();
                    
                    bw.write("# a pridejte nazev ikony za unikatne cislo, oddelene carkou. Nazev ikony musi byt bez mezer.");
                    bw.newLine();
                    
                    bw.newLine();
                    
                    bw.write("# Priklad noveho typu: \"sekondarni_bez_steny,3\" ... nebo \"sekondarni_bez_steny,3,moje_ikona.ico\"");
                    bw.newLine();
                    
                    bw.newLine();
                    
                    bw.write("normal,0");
                    bw.newLine();

                    bw.write("primary_pcd,1");
                    bw.newLine();

                    bw.write("secondary_pcd,2");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

    }

    private FileUtils() {
    }

}

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
import java.util.ArrayList;

/**
 *
 * @author ixenr
 */
public final class FileUtils {
    
    private FileUtils(){}
    
    public static ArrayList<String> readConfigFile(String path) throws IOException {
        ArrayList<String> arrayList = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while(line != null){
                int i = StringUtils.indexNonWhitespace(line);
                if(!(i == -1) && !(line.charAt(i) == '#'))
                    arrayList.add(line);
                line = br.readLine();
            }
        }
        
        if(!arrayList.isEmpty())
            return arrayList;
        
        return null;
    }
    
    public static boolean checkConfigFile(String path){
        File file = new File(path);
        
        if(file.exists() && file.canRead())
            return true;
        else if(file.exists())
            return false;
        else {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                
                bw.write("normal,normal.ico");
                bw.newLine();
                
                bw.write("primary_pcd,pcd.ico");
                bw.newLine();
                
                bw.write("secondary_pcd,pcds.ico");
                
                bw.close();
            } catch(IOException e){
                e.printStackTrace();
                return false;
            }
            
            return true;
        }
            
    }
    
}

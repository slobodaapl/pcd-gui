/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.gui.base.FileChooserAccesory;

import java.io.File;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 *
 * @author Nao
 */
public class Search implements Runnable{
FileSearchAccessory acc;
    
public Search(FileSearchAccessory acc){
this.acc=acc;
}

    @Override
    public void run() {
        SearchDirectories(acc.getDir(), acc.getText(), acc.getDepth());
    }
     
       
        
    public void SearchDirectories(File root,String pattern, int depth){
     
    RegexFileFilter  regfilter = new RegexFileFilter(pattern);
    if(root == null)return;
    File directory ;
    if(root.isDirectory())
    {directory = root;
    }
    else return;
    
     File[] files = directory.listFiles();
     
    
     for(int i=0; i<files.length;i++){
      acc.setCheckedCount(acc.getCheckedCount() + 1);
       acc.getCounter().setText("found:"+acc.getFoundFilesCount()+"/"+acc.getCheckedCount());
         
       if(acc.getFilter()!= null && acc.getFilter().accept(files[i]) && !files[i].isDirectory()){   
            if(regfilter.accept(files[i], files[i].getName())){
                acc.getModel().addElement(files[i]);
                acc.setFoundFilesCount(acc.getFoundFilesCount() + 1);
                acc.getCounter().setText("found:"+acc.getFoundFilesCount()+"/"+acc.getCheckedCount());
            }
         }
         else if (acc.getFilter()==null && regfilter.accept(files[i], files[i].getName())){
            acc.getModel().addElement(files[i]);
            acc.setFoundFilesCount(acc.getFoundFilesCount() + 1);
            acc.getCounter().setText("found:"+acc.getFoundFilesCount()+"/"+acc.getCheckedCount());
         }
         if(acc.isStop()){
         return;
         }
        
         if(files[i].isDirectory() && depth==0){
         SearchDirectories(files[i],pattern,0);
         }
         
     }
    }
    

    
  
    
    
    
}

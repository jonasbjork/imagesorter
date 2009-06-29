/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imagesorter;
import com.drew.metadata.Metadata;
import com.drew.metadata.Directory;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.imaging.jpeg.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
/**
 *
 * @author jonas
 */
public class SortImage {
    
    private final boolean DEBUG = true;
    
    private String destDir;
    private String searchDir;
    private FileHandling fh;
    private boolean recursiveSearch;
    private boolean moveFiles; 
    
    // Constructor
    SortImage() {
        this.destDir = "";
        this.searchDir = "";
        this.fh = new FileHandling();
        this.moveFiles = false;         // As default we want to copy files, not move them.
    }
    
    public void setMoveFiles(boolean move) {
        this.moveFiles = move;
    }
    
    public boolean getMoveFiles() {
        return this.moveFiles;
    }
    
    public void setRecursiveSearch(boolean rsearch) {
        this.recursiveSearch = rsearch;
    }
    
    public boolean getRecursiveSearch() {
        return this.recursiveSearch;
    }
    
    
    public void setDestinationDir(String ddir) {
        this.destDir = ddir;
    }
    
    public String getDestinationDir() {
        return this.destDir;
    }
    
    public void setSearchDir(String sdir) {
        this.searchDir = sdir;
    }
  
    public String getSearchDir() {
        return this.searchDir;
    }
    
    public void sortImages() {
        
        // Hitta alla underkataloger i en katalog
        fh.setRecursiveSearch(this.getRecursiveSearch());
        ArrayList<String> directories = fh.findDirs( this.searchDir );

        if(DEBUG) debugMsg("=== VI HAR EN ARRAYLIST ===");
        
        Iterator it = directories.iterator();
        while(it.hasNext()) {
            Object o = it.next();
            if(DEBUG) debugMsg("Hello: " + o);
            // Hitta alla filer i katalogen
            String d = o.toString();
            //System.out.println("d = " + d);
            ArrayList<String> fileArray = fh.searchFiles( d );
            Iterator fileIt = fileArray.iterator();
            while(fileIt.hasNext()) {
                Object fileObject = fileIt.next();
                if(DEBUG) debugMsg("Processing image :" + fileObject.toString());
                processImage( fileObject.toString() );
            }
        }        
    }
    
    private void processImage(String fileName) {
        
        if(DEBUG) debugMsg("[processImage] Got filename " + fileName);
        File jpegFile = new File( fileName );
        
        try {
            Metadata mdata = JpegMetadataReader.readMetadata(jpegFile);
            Directory exifDirectory = mdata.getDirectory(ExifDirectory.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            try {
                String eTime = exifDirectory.getString(ExifDirectory.TAG_DATETIME_DIGITIZED);
                Date d = sdf.parse(eTime);
                if(DEBUG) debugMsg("Date = " + d);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH)+1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                if(DEBUG) debugMsg("Y: " + year);
                if(DEBUG) debugMsg(" M: " + month);
                if(DEBUG) debugMsg(" D: " + day);
                
                checkDestDir();
                String destDate = destDir + "/" + year + "/" + padNumber(month) + "/" + padNumber(day);
                if(DEBUG) debugMsg("Destination dir: " + destDate);
                checkDestDateDir(destDate);
                
                String sourceFile = jpegFile.getAbsolutePath();
                String destFile = destDate + "/" + jpegFile.getName();
                copyFile(sourceFile, destFile);
                
            } catch (Exception e) {
                if(DEBUG) debugMsg("Error: " + e.getLocalizedMessage());
            }
            
        } catch (JpegProcessingException e) {
            if(DEBUG) debugMsg("Error: " + e.getLocalizedMessage());
        }
    }
     

    // Debugkod från exempel för metadata-extractor 
    
    private String padNumber(int n) {
        String num = null;
        if(n < 10) {
            num = "0" + n;
        } else {
            num = "" + n;
        }
        return num;
    }
    
    private void checkDestDateDir(String dateDir) {
        File d = new File(dateDir);
        if(!d.exists()) {
            if(DEBUG) debugMsg("Info: Destination directory " + dateDir + " does not exist");
            if(!d.mkdirs()) {
                if(DEBUG) debugMsg("Error: Could not create destinationdir " + dateDir);
            }
        }
    }
    
    private void checkDestDir() {
        // kolla kataloger
        File dest = new File( this.destDir );
        if (!dest.exists()) {
            if(DEBUG) System.out.println("[DEBUG]: Directory does not exist!");
            if (dest.mkdir()) {
                if(DEBUG) System.out.println("Created directory!");
            }
        } else {
            System.out.println("Directory does exist!");
        }
    }
    
    private void copyFile(String sFile, String dFile) {
        File dTest = new File(dFile);
        
        // Check if destination filename already exists.
        if(dTest.exists()) {
            int posPath = dFile.lastIndexOf("/");
            int posExt = dFile.lastIndexOf(".");
            String dpath = dFile.substring(0,posPath+1);
            String dfile = dFile.substring(posPath+1, posExt);
            String extension = dFile.substring(posExt);
            
            String tmpName = null;
            String num = null;
            for(int i=1; i < 1000; i++) {
                if(i < 10) {
                    num = "00" + i;
                } else if(i < 100) {
                    num = "0" + i;
                } else {
                    num = "" + i;
                }
                tmpName = dpath + dfile + "_" + num + extension;
                
                File tmpFile = new File(tmpName);
                if(!tmpFile.exists()) {
                    dFile = tmpName;
                    break;
                }
            }
        }
        
        // TODO: Should check if there is space left on device.
        
        // Set source file and destination file        
        File s = new File(sFile);
        File d = new File(dFile);
        if(DEBUG) debugMsg("Destination file: " + dFile);
        
        try {
            InputStream in = new FileInputStream(s);
            OutputStream out = new FileOutputStream(d);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            if(DEBUG) debugMsg("File copied.");
        } catch (Exception e) {
            if(DEBUG) debugMsg("Error: " + e.getLocalizedMessage());
        }
        
        // If moving files, delete source file.
        if(this.moveFiles) {
            s.delete();
            if(DEBUG) debugMsg("File deleted.");
        }
        
    }
    
    private void debugMsg(String dbg) {
        System.out.println("[DEBUG]: " + dbg);
    }
    
}

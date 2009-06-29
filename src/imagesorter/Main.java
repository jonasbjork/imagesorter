/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imagesorter;

import com.drew.metadata.Metadata;
import com.drew.metadata.Directory;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.Tag;
import com.drew.imaging.jpeg.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author jonas
 */
public class Main {

    private static String destDir = "d:/TestPhotos";
    /**
     * @param args the command line arguments
     */
    private static List<String> dirs;
    
    private static void findDirs(File dir) {
        if(dir.isDirectory()) {
            String[] children = dir.list();
            for(int i=0; i<children.length; i++) {
                System.out.println(children[i]);
                dirs.add(dir.getAbsolutePath() + children[i]);
                findDirs(new File(dir, children[i]));
            }
            //for(int i=0; i<children.length; i++) {
                //String tmp = children[i];
              //  dirs.add();
                //System.out.println("Katalog: " + children[i]);
            //}
        }
        
    }

    public static void main(String[] args) {

        dirs = new ArrayList<String>();
        
        String SearchDir = "c:/test";
        findDirs(new File(SearchDir));
 
        System.out.println("==========================================");
        for(Iterator i = dirs.iterator(); i.hasNext(); ) {
            System.out.println("Katalog: " + i.next());
        }
       
        /*
        for(int i=0; i<children.length; i++) {
            System.out.println("Katalog: " + children[i]);
        }
         */
        System.exit(0);
        
        File jpegFile = new File("c:/test.JPG");

        try {
            Metadata mdata = JpegMetadataReader.readMetadata(jpegFile);
            //printImageTags(1, mdata); // DEBUG shit
            Directory exifDirectory = mdata.getDirectory(ExifDirectory.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            try {
                String eTime = exifDirectory.getString(ExifDirectory.TAG_DATETIME_DIGITIZED);
                Date d = sdf.parse(eTime);
                System.out.println("Date = " + d);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH)+1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                System.out.print("Y: " + year);
                System.out.print(" M: " + month);
                System.out.println(" D: " + day);
                
                checkDestDir();
                String destDate = destDir + "/" + year + "/" + padNumber(month) + "/" + padNumber(day);
                System.out.println("Destination dir: " + destDate);
                checkDestDateDir(destDate);
                
                String sourceFile = jpegFile.getAbsolutePath();
                String destFile = destDate + "/" + jpegFile.getName();
                copyFile(sourceFile, destFile);
                
            } catch (Exception e) {
                System.err.println("Error: " + e.getLocalizedMessage());
            }
            
        } catch (JpegProcessingException e) {
            System.err.println("Error: " + e.getLocalizedMessage());
        }
        
        

    }

    // Debugkod från exempel för metadata-extractor 
    
    private static String padNumber(int n) {
        String num = null;
        if(n < 10) {
            num = "0" + n;
        } else {
            num = "" + n;
        }
        return num;
    }
    
    private static void checkDestDateDir(String dateDir) {
        File d = new File(dateDir);
        if(!d.exists()) {
            System.err.println("Info: Destination directory " + dateDir + " does not exist");
            if(!d.mkdirs()) {
                System.err.println("Error: Could not create destinationdir " + dateDir);
            }
        }
    }
    
    private static void checkDestDir() {
        // kolla kataloger
        File dest = new File(destDir);
        if (!dest.exists()) {
            System.out.println("Katalogen finns inte!");
            if (dest.mkdir()) {
                System.out.println("Skapade katalogen");
            }
        } else {
            System.out.println("Katalogen finns!");
        }
    }
    
    private static void copyFile(String sFile, String dFile) {
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
        System.out.println("Destination file: " + dFile);
        
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
            System.out.println("File copied.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getLocalizedMessage());
        }
    }
    
    private static void printImageTags(int approachCount, Metadata metadata) {
        System.out.println();
        System.out.println("*** APPROACH " + approachCount + " ***");
        System.out.println();
        // iterate over the exif data and print to System.out
        Iterator directories = metadata.getDirectoryIterator();
        while (directories.hasNext()) {
            Directory directory = (Directory) directories.next();
            Iterator tags = directory.getTagIterator();
            while (tags.hasNext()) {
                Tag tag = (Tag) tags.next();
                System.out.println(tag);
            }
            if (directory.hasErrors()) {
                Iterator errors = directory.getErrors();
                while (errors.hasNext()) {
                    System.out.println("ERROR: " + errors.next());
                }
            }
        }
    }
}

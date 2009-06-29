/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imagesorter;

import java.io.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.*;

/**
 *
 * @author Jonas Björk, jonas.bjork@gmail.com
 */
public class FileHandling {

    // Vi behöver en lista att lagra katalogerna i.
    private ArrayList<String> al;
    private boolean recursiveSearch = true;

    
    public void setRecursiveSearch(Boolean rsearch) {
        this.recursiveSearch = rsearch;
    }
    
    public boolean getRecursiveSearch() {
        return this.recursiveSearch;
    }
    
    public ArrayList<String> findDirs(String path) {
        
        al = new ArrayList();
        searchFolders(new File(path));
        System.out.println("Done.");
        System.out.println("=====================================");
        Iterator it = al.iterator();
        while(it.hasNext()) {
            Object o = it.next();
            System.out.println("[DEBUG:] al = " + o);
        }
        return al;
    }
    
    public void searchFolders(File fo) {
        if(fo.isDirectory()) {
            String abspath = fo.getAbsolutePath();
            al.add(abspath);
            if(getRecursiveSearch()) {
                System.out.println("[DEBUG]: Searching in... " + abspath);
                String internalNames[] = fo.list();
                for(int i=0; i<internalNames.length; i++) {
                    searchFolders(new File(fo.getAbsolutePath() + "/" + internalNames[i]));
                }
            }
        } else {
            System.out.println("[DEBUG]: Found a file... " + fo.getName());
        }
    }
    
    
    public ArrayList searchFiles( String dir ) {
        ArrayList<String> fList = new ArrayList();
        File fo = new File( dir );
        String fileList[] = fo.list();
        for(int i=0; i<fileList.length; i++) {
            if(Pattern.matches(".*\\.(jpg|jpeg|JPG|JPEG)", fileList[i])) {
                String fileName = fo.getAbsolutePath() + "\\" + fileList[i];
                fList.add( fileName );
                System.out.println( "Added file: " + fileName );
            }
        }
        return fList;
    }
}

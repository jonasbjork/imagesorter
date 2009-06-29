/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imagesorter;

/**
 *
 * @author jonas
 */
public class Config {
    private static java.util.prefs.Preferences prefs;
    
    Config() {
        prefs = java.util.prefs.Preferences.userNodeForPackage(getClass());
    }
    
    public static void main(String[] args) {
        System.out.println("Hello config!");
        System.out.println("Application name: " + prefs.get("app.name", "Sort Image"));
    }
    
    
}

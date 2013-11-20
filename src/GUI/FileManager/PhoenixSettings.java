/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.FileManager;

import Documents.Document;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcio Tadeu de Oliveira Jr
 */
public class PhoenixSettings {
    private static String settings="";
    
    public static String read(){
        String content="";
        try {
            BufferedReader br = new BufferedReader(new FileReader(".phoenix_settings"));
            try {
                br.readLine();
                br.readLine();
                while(br.ready()){ 
                  content += br.readLine()+"\n"; 
                }
                try { 
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content;
    }
    
    public static boolean hasChange(){
        String current = PhoenixSettings.read();
        if(settings.equals("")||settings.equals(current)){
            settings=current;
            return false;
        }else{
            settings=current;
            return true;
        }
    }
}

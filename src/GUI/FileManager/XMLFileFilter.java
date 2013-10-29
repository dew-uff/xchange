package GUI.FileManager;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author MARCIO
 * 
 * Cria um objeto que filtra para abertura de arquivos .XML
 */
public class XMLFileFilter extends FileFilter{//filtro para abertura de arquivos .XML


    @Override
    public String getDescription() {
        return "XML documents";
    }

    @Override
    public boolean accept(File f) {
        return f.getName ().toLowerCase ().endsWith (".xml") || f.isDirectory ();
    }
    
}
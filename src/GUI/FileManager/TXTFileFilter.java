package GUI.FileManager;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author MARCIO
 * 
 * Cria um objeto que filtra para abertura de arquivos .TXT
 */
public class TXTFileFilter extends FileFilter{//filtro para abertura de arquivos .TXT


    @Override
    public String getDescription() {
        return "TXT files";
    }

    @Override
    public boolean accept(File f) {
        return f.getName ().toLowerCase ().endsWith (".txt") || f.isDirectory ();
    }
    
}
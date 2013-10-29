package GUI.FileManager;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Marcio Tadeu de Oliveira Júnior
 * 
 * Cria um objeto para filtrar a abertura de arquivos .XCP 
 */
public class XCProjectFileFilter extends FileFilter{//filtro para salvamento e abertura de arquivos .XCP


    @Override
    public String getDescription() {
        return "XChange Project";
    }

    @Override
    public boolean accept(File f) {
        return f.getName ().toUpperCase ().endsWith (".XCP") || f.isDirectory ();
    }
    
}
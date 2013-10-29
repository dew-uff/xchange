package GUI.FileManager;

import java.io.*;
/**
 * Cria um objeto para filtrar a abertura de arquivos .PL
 * @author Marcio Tadeu de Oliveira Júnior
 */
public class PLFileFilter extends javax.swing.filechooser.FileFilter{
    
    @Override
    public boolean accept (File f) {
        return f.getName ().toLowerCase ().endsWith (".pl") || f.isDirectory ();
    }

    @Override
    public String getDescription () {
        return "PL Files(*.pl)";
    }
}

package GUI.FileManager;

import Documents.Documents;
import Documents.XMLFormatter;
import gems.ic.uff.br.modelo.LcsXML;
import gems.ic.uff.br.modelo.XML;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * 
 * @author Marcio Tadeu de Oliveira Junior
 * Cria um objeto para salvar um projeto .XCP
 */

public abstract class MultiDiffSaver{
    /**
     * Recebe um conjunto de documentos e os salva os diff's na ordem linear em uma pasta externa determinado pelo usuário.
     * @param documents(conjunto de documentos a ser salvo)
     * @throws NoSelectedFileException(problema na seleção de um arquivo)
     */
    public static void save(Documents documents) throws NoSelectedFileException{
        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("multidiff"));
        //abre o salvamento do arquivo de projetos
        String pathWay = "";
        int openedFile = chooser.showSaveDialog(null); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
        if (openedFile==JFileChooser.APPROVE_OPTION){
            pathWay = chooser.getSelectedFile().getAbsolutePath();  // o getSelectedFile pega o arquivo e o getAbsolutePath retorna uma string contendo o endereço.
            LastpathManager.savelastpath(pathWay, "multidiff");
            //salva o arquivo no local selecionado
            try {
                File folder = new File(pathWay);
                folder.mkdir();
                for(int i=0;i<documents.getSize()-1;i++){
                    FileWriter file = new FileWriter(new File(folder,"XMLDiff "+i+" ("+documents.getFile(i).getName().substring(0, documents.getFile(i).getName().indexOf(".")) +"_"+documents.getFile(i+1).getName().substring(0, documents.getFile(i+1).getName().indexOf("."))+").xml"));
                    BufferedWriter bwriter = new BufferedWriter(file);
                    XML xml1 = new XML(documents.getContent(i));
                    XML xml2 = new XML(documents.getContent(i+1));
                    LcsXML lcsxml = new LcsXML(xml1, xml2);
                    String[] lines = lcsxml.getDiffXML().toString().split("\n");
                    for (int k = 0; k < lines.length; k++) {
                        bwriter.write(lines[k]);
                        bwriter.newLine();
                    }
                    bwriter.close();
                } 
            }catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Error", JOptionPane.ERROR_MESSAGE); //caso ocorra algum erro na gravação do arquivo
            }
        }else{
           throw new NoSelectedFileException(); // isso é para quando você clicar em cancelar, ele nao vai ter selecionado nada e o pathWay será nulo.
        }
    }
}
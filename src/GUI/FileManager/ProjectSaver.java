package GUI.FileManager;

import Documents.Documents;
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

public abstract class ProjectSaver{
    /**
     * Recebe um conjunto de documentos e os salva em um arquivo externo determinado pelo usuário, contendo o caminho para cada arquivo XML que o contém.
     * @param documents(conjunto de documentos a ser salvo)
     * @throws NoSelectedFileException(problema na seleção de um arquivo)
     */
    public static void save(Documents documents) throws NoSelectedFileException{
        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("xcp")+".XCP");//caso exista um histórico com o ultimo caminho acessado, cria um JFileChooser com este caminho
        //abre o salvamento do arquivo de projetos
        String pathWay = "";
        File file = null;
        int openedFile = chooser.showSaveDialog(null); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
        if (openedFile==JFileChooser.APPROVE_OPTION){
           pathWay = chooser.getSelectedFile().getAbsolutePath();
            LastpathManager.savelastpath(pathWay, "xcp");       
            //salva o arquivo no local selecionado
            String name = pathWay;
            if(!name.toUpperCase().endsWith(".XCP")) {
                name+=".XCP";
            }
            file = new File(name); // o +".txt" é para ele salvar como txt . Para outro tipo de arquivo, mude a extensao final. se você nao mudar a extensao, ele vai criar como ".bin"
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                for(String doc:documents.getPathWays()){//para cada document, grava o caminho do arquivo XML associado
                    out.write(doc);
                    out.newLine();
                }
                out.close();
            }catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Erro", JOptionPane.ERROR_MESSAGE); //caso ocorra algum erro na gravação do arquivo
            }
        }else{
           throw new NoSelectedFileException(); // isso é para quando você clicar em cancelar, ele nao vai ter selecionado nada e o pathWay será nulo.
        }
    }
}
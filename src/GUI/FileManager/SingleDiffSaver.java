package GUI.FileManager;

import Exception.NoSelectedFileException;
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
 * @author Marcio Tadeu de Oliveira Junior Cria um objeto para salvar um projeto
 * .XCP
 */
public abstract class SingleDiffSaver {

    /**
     * Uma String que contém o resultado de um diff entr dois documentos e o salva.
     *
     * @param content Conteúdo a ser salvo
     * @throws NoSelectedFileException(problema na seleção de um arquivo)
     */
    public static void save(String content) throws NoSelectedFileException {
        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("singlediff")+".xml");//caso exista um histórico com o ultimo caminho acessado, cria um JFileChooser com este caminho
        String pathWay = "";
        File file = null;
        int openedFile = chooser.showSaveDialog(null); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
        if (openedFile == JFileChooser.APPROVE_OPTION) {
            pathWay = chooser.getSelectedFile().getAbsolutePath();  // o getSelectedFile pega o arquivo e o getAbsolutePath retorna uma string contendo o endereço.
            LastpathManager.savelastpath(pathWay, "singlediff");
            //salva o arquivo no local selecionado
            String name = pathWay;
            if (!name.toUpperCase().endsWith(".XML")) {
                name += ".xml";
            }
            file = new File(name); // o +".txt" é para ele salvar como txt . Para outro tipo de arquivo, mude a extensao final. se você nao mudar a extensao, ele vai criar como ".bin"
            try {
                String[] lines = content.split("\n");
                BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                for (int i = 0; i < lines.length; i++) {
                    out.write(lines[i]);
                    out.newLine();
                }
                out.newLine();

                out.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Erro", JOptionPane.ERROR_MESSAGE); //caso ocorra algum erro na gravação do arquivo
            }
        } else {
            throw new NoSelectedFileException(); // isso é para quando você clicar em cancelar, ele nao vai ter selecionado nada e o pathWay será nulo.
        }
    }
}
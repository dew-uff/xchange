package GUI.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Classe responsável por carregar um arquivo PL e retornar seu conteúdo.
 * @author Celio H. N. Larcher Jr.
 */
public class PLOpener {

    private String insertText;

    /**
     * Método que abre um arquivo PL contendo as regras e atribui o conteúdo a variável da classe insertText.
     * Este método abre o JFileChoose no ultimo caminho aberto pelo programa guardado no arquivo lastpathpl.txt
    */
    public PLOpener() {
        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("pl"));//passa o caminho até a pasta do ultimo arquivo aberto
        chooser.setFileFilter(new PLFileFilter()); //altera o filtro de arquivos para um personalizado por arquivo PL.
        int anwser = chooser.showOpenDialog(null); //abre a caixa de seleção de arquivo PL
        this.insertText = "";
        if (anwser == JFileChooser.APPROVE_OPTION) {//caso tenha sido selecionado um arquivo
            LastpathManager.savelastpath(chooser.getSelectedFile().getAbsolutePath(), "pl");
            readFile(chooser.getSelectedFile());//le o arquivo PL selecionado com a função readFile
        }
    }

    /**
     * Passado um arquivo texto qualquer a função lê seu conteúdo e o atribui a variável de classe insertText
     * @param fileOpen
     */
    private void readFile(File fileOpen) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileOpen));//cria um buffer do arquivo passado
            String line, concatenated = "";
            while ((line = br.readLine()) != null) {//enquanto a leitura do arquivo for válida
                concatenated = concatenated + line + "\n";//concatena as linhas do arquivo com um \n entre elas.
            }
            this.insertText = concatenated;//atribui a variável insertText
            br.close();
        } catch (FileNotFoundException error) {
            JOptionPane.showMessageDialog(null, "It was not possible read the file.", "Erro", JOptionPane.ERROR_MESSAGE);//caso não encontre o arquivo
        } catch (IOException error) {
            JOptionPane.showMessageDialog(null, "It was not possible read the file.", "Erro", JOptionPane.ERROR_MESSAGE);//caso não seja possível a leitura do arquivo
        }
    }

    /**
     * Retorna o conteúdo do arquivo selecionado que foi lido pela classe.
     * @return this.insertText
     */
    public String getText() {
        return this.insertText;
    }
}

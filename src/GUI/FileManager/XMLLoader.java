package GUI.FileManager;

import Exception.NoSelectedFileException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 * Classe responsável por abrir um arquivo XML
 * @author Marcio Tadeu de Oliveira Júnior
 */
public class XMLLoader {

    /**
     * Abre um arquivo .XML e o repassa a aplicação
     * @return new File(pathWay) //retorna o arquivo selecionado pelo usuário
     * @throws IOException(erro de leitura de arquivos)
     * @throws NoSelectedFileException(problema na seleção de um arquivo)
     */
    public static File open() throws NoSelectedFileException, IOException {
        //procura o ultimo caminho em que se abriu um arquivo XML
        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("xml"));//tenta criar um chooser para a ultima pasta onde um arquivo XML foi aberto
        chooser.setFileFilter(new XMLFileFilter());//abre o dialogo de abertura de arquivo XML
        String pathWay = "";
        int openedFile = chooser.showOpenDialog(null);
        if (openedFile == JFileChooser.APPROVE_OPTION) {//caso tenha sido selecionado um arquivo
            pathWay = chooser.getSelectedFile().getAbsolutePath();//pega o caminho do arquivo XML
            if (pathWay.toLowerCase().endsWith(".xml")) {//caso tenha extensão .xml
                LastpathManager.savelastpath(pathWay, "xml");
                return new File(pathWay);//retorna o arquivo XML
            } else{
                throw new IOException();//caso tenha sido selecionado um arquivo com extensão errada
            }
        } else {
            throw new NoSelectedFileException();//caso não tenha sido selecionado um arquivo
        }
    }
}
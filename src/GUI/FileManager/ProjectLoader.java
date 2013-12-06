package GUI.FileManager;

import Documents.Document;
import Documents.Documents;
import Exception.NoSelectedFileException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Marcio Tadeu de Oliveira Junior Cria um objeto para abrir um projeto
 * .XCP
 */
public abstract class ProjectLoader {

    /**
     * Abre um arquivo .XCP que contem os caminhos dos arquivos de um projeto
     * salvo e cria um novo projeto
     *
     * @return documents(elemento da classe Documents com conjunto de arquivos
     * carregados)
     * @throws IOException(erro de leitura de arquivos)
     * @throws NoSelectedFileException(problema na seleção de um arquivo)
     */
    public static Documents load() throws IOException, NoSelectedFileException {

        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("xcp")+".XCP");//caso exista um histórico com o ultimo caminho acessado, cria um JFileChooser com este caminho
        chooser.setFileFilter(new XCProjectFileFilter());//define o filtro de seleção para XCP
        String pathWay = "";
        int openedFile = chooser.showOpenDialog(null);
        if (openedFile == JFileChooser.APPROVE_OPTION) {//caso um arquivo tenha sido selecionado
            pathWay = chooser.getSelectedFile().getAbsolutePath();//atribui o caminho onde o arquivo foi selecionado a variável pathWay
            LastpathManager.savelastpath(pathWay, "xcp");

            //importa os arquivos do projeto salvo para o novo projeto caso um arquivo tenha sido selecionado e tenha extenção XCP
            if (pathWay.toUpperCase().endsWith(".XCP")) {
                File file = new File(pathWay);
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(file));//le o arquivo selecionado pelo usuário
                    try {
                        Document.differentProject();//reinicia o contador de documentos da classe
                        Documents documents = new Documents();//cria um novo projeto contendo um conjunto de documentos
                        while (br.ready()) {
                            String s = br.readLine();//para cada linha lida
                            documents.add(new File(s));//cria e adiciona um novo documento ao conjunto
                        }
                        try {
                            br.close();
                            return documents;//retorna o conjunto de documentos lidos
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "It was not possible read the file.", "Erro", JOptionPane.ERROR_MESSAGE);//caso não encontre o arquivo
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "It was not possible read the file.", "Erro", JOptionPane.ERROR_MESSAGE);//caso não encontre o arquivo
                    }
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "It was not possible read the file.", "Erro", JOptionPane.ERROR_MESSAGE);//caso não encontre o arquivo
                }
            }
        } else {
            throw new NoSelectedFileException(); //caso não tenha sido selecionado nenhum arquivo
        }
        return null;  //caso alguma etapa tenha tido problemas retorna o valor null
    }
}
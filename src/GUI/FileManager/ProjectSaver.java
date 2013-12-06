package GUI.FileManager;

import Documents.Documents;
import Exception.NoSelectedFileException;
import GUI.MainInterface.MainInterface;
import Manager.Manager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * 
 * @author Carlos Roberto Carvalho Oliveira
 * @author Marcio Tadeu de Oliveira Junior
 * Cria um objeto para salvar um projeto .XCP
 */

public abstract class ProjectSaver{  
    private static Manager manager;
    private static MainInterface mainInterface;
    /**
     * Recebe um conjunto de documentos e os salva em um arquivo externo determinado pelo usuário, contendo o caminho para cada arquivo XML que o contém.
     * @param documents(conjunto de documentos a ser salvo)
     * @param mInterface(identificar qual modulo foi ativo(ContextKey ou Similaridade))
     * @param mngr(necessário para obter os fatos prolog)
     * @param isEnabledFactsPrologFacts(verifica se foi inicializado algum modulo(ContextKey ou Similaridade), caso contrario so ira salvar os xml abertos)
     * @throws NoSelectedFileException(problema na seleção de um arquivo)
     */
    public static void save(Documents documents, MainInterface mInterface, Manager mngr, boolean isEnabledFactsPrologFacts) throws NoSelectedFileException, IOException{
        mainInterface = mInterface;
        manager = mngr;
        
        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("xcp")+".XCP");//caso exista um histórico com o ultimo caminho acessado, cria um JFileChooser com este caminho
        //abre o salvamento do arquivo de projetos
        String pathWay = "";
        File file = null;
        int openedFile = chooser.showSaveDialog(null); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
        if (openedFile==JFileChooser.APPROVE_OPTION){
            pathWay = chooser.getSelectedFile().getAbsolutePath();
            LastpathManager.savelastpath(pathWay, "xcp");
            file = new File(pathWay);
            file.mkdir();
            
            //Salva os XML abertos
            saveXMLVersions(documents,pathWay);
            
            //Se tiver iniciado contextkey ou similaridade, salva os fatos tambem
            if(isEnabledFactsPrologFacts)
                savePrologFacts(documents,pathWay);
        
        }else{
           throw new NoSelectedFileException(); // isso é para quando você clicar em cancelar, ele nao vai ter selecionado nada e o pathWay será nulo.
        }
    }
    
    /*
     * Responsável por salvar os XML Abertos
     */
    public static void saveXMLVersions(Documents documents, String pathWay){
        File file=null;
        String name = "XMLVersions.XCP";
        file = new File(new File(pathWay),name); // o +".txt" é para ele salvar como txt . Para outro tipo de arquivo, mude a extensao final. se você nao mudar a extensao, ele vai criar como ".bin"
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            for(String doc:documents.getPathWays()){//para cada document, grava o caminho do arquivo XML associado
                out.write(doc);
                out.newLine();
            }
            out.close();
        }catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Erro", JOptionPane.ERROR_MESSAGE); //caso ocorra algum erro na gravação do arquivo
        }
    }
    
    /*
     * Responsável por os fatos prolog
     */
    public static void savePrologFacts(Documents documents, String pathWay){
        //ContextKey
        if(!mainInterface.getSimilarity()){
            String contextKeyDirectory = "ContextKey";
            File directory = new File(new File(pathWay),contextKeyDirectory);
            directory.mkdir();
                        
            for(int i=0; i < documents.getSize(); i++){
                //Obtém o nome do documento sem o ".xml" no final
                String name = documents.getDocument(i+1).getFile().getName().substring(0,documents.getDocument(i+1).getFile().getName().lastIndexOf("."))+".pl";
                
                File file = new File(directory,name);
                  
                //Obtém o fato prolog no indice i
                String[] fact = manager.getContextKey().get(i).getFacts().split("\n");
                
                try{
                    BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                    for(int j=0; j< fact.length; j++){
                        out.write(fact[j]);
                        out.newLine();
                    }
                    out.close();
                }catch(IOException ex){
                    JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Erro", JOptionPane.ERROR_MESSAGE); //caso ocorra algum erro na gravação do arquivo
                }
            }
        }else{//Similarity
            String similarityDirectory = "Similarity";
            File directory = new File(new File(pathWay),similarityDirectory);
            directory.mkdir();
            
            for(int i=0; i<(documents.getSize()-1);i++){
                for(int j = (i+1); j<documents.getSize();j++){
                    //Define o nome dos arquivos Vi_Vj
                    String name = "V"+ Integer.toString(i+1)+"_V"+Integer.toString(j+1)+".pl";
                    
                    File file = new File(directory, name);
                    
                    String[] factVi;
                    factVi = (manager.getSimilarityFacts().get(i).get(documents.getSize()-j-1).get(0)).split("\n");
                                        
                    String[] factVj;
                    factVj = (manager.getSimilarityFacts().get(i).get(documents.getSize()-j-1).get(1)).split("\n");
                    
                    try{
                        BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                        for(int k=0; k< factVi.length;k++){
                            out.write(factVi[k]);
                            out.newLine();
                        }
                        out.newLine();
                        for(int w=0; w<factVj.length;w++){
                            out.write(factVj[w]);
                            out.newLine();
                        }
                        out.close();
                    }catch(IOException ex){
                        JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Erro", JOptionPane.ERROR_MESSAGE); //caso ocorra algum erro na gravação do arquivo
                    }
                }
            }
        }
    }
}
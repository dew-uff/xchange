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
     * @param isEnableXMLDiff (verifica se foi abilitada a aba de resultados, ou seja, caso positivo, salva as regras selecionadas ou criadas pelo usuario)
     * @throws NoSelectedFileException(problema na seleção de um arquivo)
     */
    public static void save(Documents documents, MainInterface mInterface, Manager mngr, boolean isEnabledFactsPrologFacts, boolean isEnableXMLDiff) throws NoSelectedFileException, IOException{
        mainInterface = mInterface;
        manager = mngr;
        
        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("xcp")+".XCP");//caso exista um histórico com o ultimo caminho acessado, cria um JFileChooser com este caminho
        chooser.setFileFilter(new XCProjectFileFilter()); // Coloca um filtro para ser visualizado somente os arquivos .XCP
        chooser.setAcceptAllFileFilterUsed(false); // desabilita a opção de mostrar todos os arquivos
        //abre o salvamento do arquivo de projetos
        String pathWay = "";
        File file = null;
        int openedFile = chooser.showSaveDialog(null); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
        if (openedFile==JFileChooser.APPROVE_OPTION){
            pathWay = chooser.getSelectedFile().getAbsolutePath();
            LastpathManager.savelastpath(pathWay, "xcp");
            file = new File(pathWay);
            File fileExists = new File(pathWay+".xcp");
            if(fileExists.exists()){ // Caso ja contenha um arquivo com o nome que o usuario digitou
                // Obtem se o usuario deseja sobreescrever o arquivo ou cancelar
                int resposta = JOptionPane.showConfirmDialog(null, fileExists.getName()+
                                                            " already exists!\n"+"Want to replace it?", 
                                                            "Confirm replace it", JOptionPane.OK_CANCEL_OPTION,
                                                            JOptionPane.QUESTION_MESSAGE);
                
                if(resposta ==  JOptionPane.CANCEL_OPTION){ //Caso o usuario nao deseja substituir o arquivo, abre a janela novamente para escolher outro nome
                    save(documents, mInterface, mngr, isEnabledFactsPrologFacts, isEnableXMLDiff);
                }else{ //Caso ele deseja substituir o arquivo
                    saveConfirm(documents, isEnabledFactsPrologFacts, isEnableXMLDiff, file, pathWay);
                }
            }else{ //Caso nao contenha o nome do arquivo
                saveConfirm(documents, isEnabledFactsPrologFacts, isEnableXMLDiff, file, pathWay);
            }            
        }else{
           throw new NoSelectedFileException(); // isso é para quando você clicar em cancelar, ele nao vai ter selecionado nada e o pathWay será nulo.
        }
    }
    
    /**
     * Salva os arquivos XML aberto, e caso exista os fatos prolog e as regras
     * @param documents(conjunto de documentos a ser salvo)
     * @param isEnabledFactsPrologFacts(verifica se foi inicializado algum modulo(ContextKey ou Similaridade), caso contrario so ira salvar os xml abertos)
     * @param isEnableXMLDiff (verifica se foi abilitada a aba de resultados, ou seja, caso positivo, salva as regras selecionadas ou criadas pelo usuario)
     * @param file (pasta temporario utilizada antes de zipar o arquivo em formato .XCP)
     * @param pathWay (caminho completo onde o arquivo sera salvo)
     */
    public static void saveConfirm(Documents documents, boolean isEnabledFactsPrologFacts, boolean isEnableXMLDiff, File file, String pathWay){
        //cria uma pasta com o nome que o usuario digitou
        file.mkdir();

        //Salva os XML abertos
        saveXMLVersions(documents,pathWay);

        //Se tiver iniciado contextkey ou similaridade, salva os fatos tambem
        if(isEnabledFactsPrologFacts)
            savePrologFacts(documents,pathWay, isEnableXMLDiff);

        //Zipa a pasta criada e altera sua extenção para .XCP
        XCPBuilder xcp = new XCPBuilder(pathWay);

        //Deleta a Pasta Criada inicialmente
        deleteTemp(file);

        JOptionPane.showMessageDialog(null, "Project Saved Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /*
     * Responsável por salvar os XML Abertos
     */
    public static void saveXMLVersions(Documents documents, String pathWay){
        File file = null;
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
    public static void savePrologFacts(Documents documents, String pathWay, boolean isEnableXMLDiff){
        File directory = null;
        //ContextKey
        if(!mainInterface.getSimilarity()){
            String contextKeyDirectory = "ContextKey";
            directory = new File(new File(pathWay),contextKeyDirectory);
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
            directory = new File(new File(pathWay),similarityDirectory);
            directory.mkdir();
            
            for(int i=0; i<(documents.getSize()-1);i++){
                for(int j = (i+1); j<documents.getSize();j++){
                    //Define o nome dos arquivos Vi_Vj
                    String name = "V"+ Integer.toString(i+1)+"_V"+Integer.toString(j+1)+".pl";
                    
                    File file = new File(directory, name);
                    
                    String[] factVi = (manager.getSimilarityFacts().get(i).get(documents.getSize()-j-1).get(0)).split("\n");
                                        
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
        
        /*
        if(isEnableXMLDiff){
            String rulesName = "rules"+".pl";
                
            File fileRules = new File(directory, rulesName);

            String[] rules = RuleMainInterface.formatSetTextPane(manager.getRulesModule().getRulesString()).split("\n");

            try{
                BufferedWriter out = new BufferedWriter(new FileWriter(fileRules.getAbsolutePath()));
                for(int i=0; i < rules.length; i++){
                    out.write(rules[i]);
                    out.newLine();
                }
                out.close();
            }catch(IOException ex){
                JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Erro", JOptionPane.ERROR_MESSAGE); //caso ocorra algum erro na gravação do arquivo
            }
        }*/
    }
    
    public static boolean deleteTemp(File dir){
        if(dir.isDirectory()){
            String[] children = dir.list();
            for(int i=0;i<children.length;i++){
                boolean success = deleteTemp(new File(dir,children[i]));
                if(!success){
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
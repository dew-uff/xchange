package Translate;
import GUI.Util.ProgressHandler;
import br.ufrj.ppgi.parser.XMLParser;
import gems.ic.uff.br.modelo.XML;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import javax.swing.JOptionPane;
/**
 *
 * @author Celio H. N. Larcher Junior e Guilherme Martins
*/
public abstract class TranslateModule {
    protected static boolean reset=true;//ao se iniciar o programa essa variável permite resetar o índice das ids e, após isso, recebe valor false, mantendo o crescimento das mesmas
    protected String facts=""; //Fatos de um determinado arquivo
    protected static String elementName; //Nome do fato que identifica a tag principal do XML
      
    /**
     * Inicia o reset das ID's.
     */
    public static void reset() {
        reset=true;
    }
    
    /**
     * Retorna os fatos armazenados.
     * @return facts
     */
    public String getFacts(){
        return this.facts;
    }
    
    /**
     * Atribui os fatos traduzidos.
     * @param fact 
     */
    public void setFacts(String fact){
        this.facts = fact;
    }
    
    /**
     * Retorna o nome do elemento que é a tag principal do XML.
     * @return elementName
     */
    public String getElementName(){
        return TranslateModule.elementName;
    }
    
    /**
     * Obtem o nome do elemento que é a tag principal do XML
     */
    protected void setElementName(File fileTranslate){
        XML xml = new XML(fileTranslate.getAbsolutePath());
        TranslateModule.elementName = xml.getDocument().getDocumentElement().getFirstChild().getNodeName();
    }

    /**
     * Função que converte os arquivos XML carregados em fatos Prolog adequados para a inferencia das regras. 
     * @param fileTranslate 
     * Documento XML que se deseja traduzir.
     */
    public void translateFacts(File fileTranslate) {
        ProgressHandler.setLabel("Translating "+ fileTranslate.getName() +" into Prolog facts");
        XMLParser parser = new XMLParser();
        HashMap file=new HashMap();
        file.put("0",fileTranslate);//adciona o arquivo a se traduzir no hashmap com chave "0", a biblioteca só aceita HashMap
        parser.setClearData(true);//limpa o arquivo externo onde ficam armazenada a tradução
        parser.setResetLastId(TranslateModule.reset);//caso passado true, reseta as ids, caso false, mantém a incrementação
        parser.executeParseSax(file);//Traduz os fatos usando o método SAX
        try{//le o resultado da tradução do arquivo criado BaseFatos.pl
            BufferedReader bf=new BufferedReader(new FileReader(new File("BaseFatos.pl")));
            String line=bf.readLine();
            this.facts = "";
            while(line!=null){
                this.facts +=line;
                line=bf.readLine();
            }
        }catch(Exception error){
            JOptionPane.showMessageDialog(null, "Error in reading file BaseFatos.pl.", "Error",JOptionPane.ERROR_MESSAGE);
        }

        this.facts=this.facts.replaceAll("\\. ", "\\.");//retira o espaço após o ponto final
        this.facts=this.facts.replaceAll("\\.", "\\.\n");//retira o \n do texto
        this.facts = this.facts.replaceAll(", ", ",");//retira o espaço após as virgulas
        this.facts = this.facts.replaceAll(" ", "_");//substitui os espaços entre as palavras por _
        this.facts = this.facts.replaceAll("\\._", "\\.");//retira o _ do final das sentenças
        this.facts = this.facts.replaceAll("'", "");//retira as aspas
        this.facts=this.facts.replaceAll(",\\)",",''\\)");//coloca aspas simples em campos vazios
        this.facts = this.facts.replaceAll("\n\n", "\n");
        this.facts=this.facts.toLowerCase();//coloca todos os fatos em minusculo

        if(TranslateModule.reset){//Na primeira execução, reseta as ids e inicializa elementName. Após, seta em false a variável reset.
            this.setElementName(fileTranslate);//Inicializa o valor da tag principal do xml
            TranslateModule.reset=!TranslateModule.reset;
        }
        ProgressHandler.increase();
    }

    /**
     * Operação para dividir os fatos concentrados em uma única String em um vetor de Strings onde cada posição guarda um fato isolado.
     * @param factsTranlate
     * String contendo todos os fatos.
     * @return partFacts
     * Vetor de Strings onde cada índice corresponde a um fato.
     */
    public String[] partFacts(String factsTranlate){
        
        factsTranlate = factsTranlate.replaceAll("(\n|\r)", ""); //Remove quebras de linha     
        String[] partFacts = factsTranlate.split("\\."); //Separa os fatos através do ponto final

        return partFacts;
    }
    
    /**
     * Retorna os argumentos dos fatos.
     * @param fact
     * String contendo todos os fatos.
     * @return arguments
     * Vetor de Strings onde cada índice corresponde ao argumento de um fato.
     */
    public String[] argumentFacts(String fact) {
        int idxAbreParenteses = fact.indexOf("(");
        int idxFechaParenteses = fact.indexOf(")");

        String[] arguments = fact.substring(idxAbreParenteses + 1, idxFechaParenteses).split(",");

        return arguments;
    }
    
    /**
     * Retorna o nome de um fato
     * @param fact
     * String contendo um fato.
     * @return nameFact
     * String contendo o nome do fato passado como parametro.
     */
    public String getNameFact(String fact) {
        String nameFact = "";

        int indiceParenteses = fact.indexOf("(");
        nameFact = fact.substring(0, indiceParenteses);

        return nameFact;
    }
    
    /**
     * Retorna o fato com "before" ou "after" como primeiro argumento.
     * @param partFacts
     * @param version
     * @return mainFact
     * Exemplo: (before, Fb) ou (after, Fm).
     */
    public String getMainFact(String[] partFacts, String version) {
        String mainFact = "";
        String secondArgument = "";
        if (version.equals("before")){
            secondArgument="Before";
        }else if(version.equals("after")){
            secondArgument="After";
        }
        
        //Fato com o primeiro e o segundo argumento sendo uma Variavel Prolog
        mainFact = getElementName()+"("+version+","+getElementName().toUpperCase()+secondArgument+")";
       
        return mainFact;
    }

    /**
     * Acrescenta as cláusulas "before" e "after" aos fatos referentes ao primeiro e ao segundo documento.
     * @param beforeFacts 
     * Fatos proveniente da tradução do documento XML "before".
     * @param afterFacts 
     * Fatos proveniente da tradução do documento XML "after".
     * @return closeFacts
     * Retorna a união destes dois conjuntos de fatos com os identificadores normalizados.
     */
    public static String setStandardFacts(String beforeFacts, String afterFacts) {

       int startId2 = beforeFacts.indexOf(TranslateModule.elementName+"(");//descobre o indice do inicio de uma ocorrencia do fato referente a tag principal
       int endId2 = beforeFacts.indexOf(",",startId2);//descobre o indice do fim de uma ocorrencia do fato referente a tag principal

       String part2 = beforeFacts.substring(startId2 +TranslateModule.elementName.length()+1, endId2+1);//captura a id referente a tag principal no documento
       String beforeFactsReconstruct=beforeFacts.replaceAll(part2, "before,");//substitui essa id por before em todo o documento
       
       startId2 = afterFacts.indexOf(TranslateModule.elementName+"(");//descobre o indice do inicio de uma ocorrencia do fato referente a tag principal
       endId2 = afterFacts.indexOf(",",startId2);//descobre o indice do fim de uma ocorrencia do fato referente a tag principal

       part2 = afterFacts.substring(startId2 +TranslateModule.elementName.length()+1, endId2+1);//captura a id referente a tag principal no documento
       String afterFactsReconstruct=afterFacts.replaceAll(part2, "after,");//substitui essa id por after em todo o documento

       String closeFacts = beforeFactsReconstruct + "\n" + afterFactsReconstruct;//retorna os documentos com before e after setados
       
       while (closeFacts.contains("\n\n")){
           closeFacts = closeFacts.replaceAll("\n\n", "\n");
       }
       
       return closeFacts;
   }
}
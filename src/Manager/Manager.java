package Manager;

import Documents.Documents;
import GUI.MainInterface.MainInterface;
import GUI.Util.ProgressHandler;
import Inference.InferenceModule;
import Rules.RulesModule;
import Translate.ContextKey;
import Translate.Similarity;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Guilherme Martins
 */
public class Manager {

    //Variaveis utilizadas
    private RulesModule rulesModule = new RulesModule();
    private InferenceModule inferenceModule = new InferenceModule();
    private ArrayList<ContextKey> contextKey = new ArrayList<ContextKey>();
    private ArrayList<Similarity> similarity = new ArrayList<Similarity>();
    private ArrayList<Results> resultsInference = new ArrayList<Results>();
    private float similarityRate;

    /**
     * @return contextKey
     */
    public ArrayList<ContextKey> getContextKey() {
        return contextKey;
    }

    /**
     * @return similarity
     */
    public ArrayList<Similarity> getSimilarity() {
        return similarity;
    }

    /**
     * @return rulesModule
     */
    public RulesModule getRulesModule() {
        return rulesModule;
    }

    /**
     * @return inferenceModule
     */
    public InferenceModule getInferenceModule() {
        return inferenceModule;
    }

    /**
     * @return resultsInference
     */
    public ArrayList<Results> getResultsInference() {
        return resultsInference;
    }

    /**
     * Apaga as informações contidas em todas as variáveis desta classe.
     */
    public void refreshFullManager() {
        rulesModule = new RulesModule();
        inferenceModule = new InferenceModule();
        contextKey = new ArrayList<ContextKey>();
        similarity = new ArrayList<Similarity>();
        resultsInference = new ArrayList<Results>();
    }

    /**
     * Inicia os Documentos XML como "ContextKey".
     *
     * @param documents
     */
    public void startContextKey(Documents documents) {

        if (getContextKey().size() > 0) { //Caso já existam documentos iniciados como "Context Key", estes serão removidos
            for (int i = (getContextKey().size() - 1); i >= 0; i--) {
                getContextKey().remove(i);
            }
        }
        ProgressHandler.makeNew(documents.getSize(), "Translating XML into Prolog Facts");
        for (int i = 0; i < documents.getSize(); i++) { //Adiciona todos os documentos passados ao conjunto "Context Key"          
            getContextKey().add(new ContextKey());
            getContextKey().get(i).translateFacts(documents.getDocuments().get(i).getFile());
            ProgressHandler.setLabel("Translating " + documents.getDocuments().get(i).getFile().getName() + " into Prolog Facts");
        }
        ProgressHandler.dispose();
    }

    /**
     * Inicia os Documentos XML como "Similarity".
     *
     * @param documents
     */
    public void startSimilarity(Documents documents) {

        if (getSimilarity().size() > 0) { //Caso já existam documentos iniciados como "Similarity", estes serão removidos
            for (int i = (getSimilarity().size() - 1); i >= 0; i--) {
                getSimilarity().remove(i);
            }
        }
        ProgressHandler.makeNew(documents.getSize()*2 + 7, "Calculating similarity between XML ");
        for (int i = 0; i < documents.getSize(); i++) { //Adiciona todos os documentos passados ao conjunto "Similarity" 
            getSimilarity().add(new Similarity(documents.getDocuments().get(i).getPathWay()));
            
            ProgressHandler.increase();
            
            getSimilarity().get(i).translateFacts(documents.getDocuments().get(i).getFile());
        }

        //Inicia os dois primeiros documentos para que eles possam ser utilizados na construção das regras
        ProgressHandler.setLabel("Getting Facts");
        getSimilarity().get(0).documentsWithIDs(documents.getDocuments().get(0).getPathWay(), documents.getDocuments().get(1).getPathWay(), ((float) 0.99));
        
        getSimilarity().get(0).translateFactsID(new File(getSimilarity().get(0).getPathID1()));
        ProgressHandler.dispose();
    }

    /**
     * Armazena os resultados das inferencias utilizando o método "Context Key".
     */
    public void startResultsInferenciaContextKey() {

        String factsInference; //Fatos dos dois documentos que serão utilizados na inferência
        ArrayList<String> finishResult; //Resultados da inferência

        //Limpa os resultados caso já exista algum
        resultsInference.clear();

        //Gera resultados para todos os pares de documentos
        for (int i = 0; i < (getContextKey().size() - 1); i++) {
            for (int j = i + 1; j < getContextKey().size(); j++) {
                if (i != j) {
                    //Concatena e normaliza os fatos pertencentes a dois documentos
                    factsInference = getContextKey().get(i).setStandardFacts(getContextKey().get(i).getFacts(), getContextKey().get(j).getFacts());
                    finishResult = getInferenceModule().performInference(factsInference, getRulesModule()); //Realiza a inferência
                    getResultsInference().add(new Results(i + 1, j + 1, finishResult)); //Armazena os resultados referentes aos dois documentos utilizados
                }
            }
        }
    }

    /**
     * Armazena os resultados das inferencias utilizando o método "Similarity".
     *
     * @param documents
     * @param similarityRate
     */
    public void startResultsInferenciaSimilarity(Documents documents, float similarityRate) {

        this.similarityRate = similarityRate;

        String factsInference; //Fatos dos dois documentos que serão utilizados na inferência
        ArrayList<String> finishResult; //Resultados da inferência
        String auxFact1, auxFact2; //Strings auxiliares para obter os fatos dos documentos

        //Limpa os resultados caso já exista algum
        resultsInference.clear();

        //comandos para cálculo do valor máximo da barra
        int iterations = 0;

        for (int i = 0; i < (getSimilarity().size() - 1); i++) {
            for (int j = i + 1; j < getSimilarity().size(); j++) {
                if (i != j) iterations++;
            }
        }
        iterations*=8;//multiplica-se por 7 pois a função documentsWithIDs() executa 6 acrécimos na barra de progresso, uma vez para gerar os fatos, e outra para realizar a inferência
        
        ProgressHandler.makeNew(iterations, "Getting inference");       
        
        //Gera resultados para todos os pares de documentos
        for (int i = 0; i < (getSimilarity().size() - 1); i++) {
            for (int j = i + 1; j < getSimilarity().size(); j++) {
                if (i != j) {
                    getSimilarity().get(i).documentsWithIDs(documents.getDocuments().get(i).getPathWay(), documents.getDocuments().get(j).getPathWay(), similarityRate);
                    
                    ProgressHandler.setLabel("Getting Prolog facts");
                    
                    getSimilarity().get(i).translateFactsID(new File(getSimilarity().get(i).getPathID1())); //Gera os fatos do primeiro documento
                    auxFact1 = getSimilarity().get(i).getFactsSimilarityID(); //Obtêm os fatos referentes ao primeiro documento
                    getSimilarity().get(i).translateFactsID(new File(getSimilarity().get(i).getPathID2())); //Gera os fatos do segundo documento
                    auxFact2 = getSimilarity().get(i).getFactsSimilarityID(); //Obtêm os fatos referentes ao segundo documento

                    ProgressHandler.setLabel("Getting inference results");
                    //Concatena e normaliza os fatos pertencentes a dois documentos
                    factsInference = getSimilarity().get(i).setStandardFacts(auxFact1, auxFact2);
                    finishResult = getInferenceModule().performInference(factsInference, getRulesModule()); //Realiza a inferência
                    getResultsInference().add(new Results(i + 1, j + 1, finishResult)); //Armazena os resultados referentes aos dois documentos utilizados
                }
            }
        }
        ProgressHandler.dispose();
    }

    public float getSimilarityRate() {
        return this.similarityRate;
    }

    /**
     * Principal.
     *
     * @param args
     */
    public static void main(String[] args) {
        Manager manager = new Manager();
        MainInterface main = new MainInterface(manager); //Invoca o construtor da interface principal
    }
}

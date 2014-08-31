package Manager;

import Documents.Documents;
import GUI.FileManager.PhoenixSettings;
import GUI.MainInterface.MainInterface;
import GUI.Util.ProgressHandler;
import Inference.InferenceModule;
import Rules.RulesModule;
import Translate.ContextKey;
import Translate.Similarity;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;

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
    private ArrayList<ArrayList<ArrayList<String>>> similarityFacts = new ArrayList<ArrayList<ArrayList<String>>>();
    private float similarityRate = 0;
    private static MainInterface main;
    private int documentsSize = 0;

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
        ProgressHandler.restart(documents.getSize(), "Translating XML into Prolog facts");
        for (int i = 0; i < documents.getSize(); i++) { //Adiciona todos os documentos passados ao conjunto "Context Key"          
            ProgressHandler.setLabel("Translating \"" + documents.getFile(i).getName() + "\" into Prolog facts");
            getContextKey().add(new ContextKey());
            getContextKey().get(i).translateFacts(documents.getDocuments().get(i).getFile());

            ProgressHandler.increase();
        }
        ProgressHandler.stop();
    }

    /**
     * Inicia os Documentos XML como "Similarity".
     *
     * @param documents
     */
    public void startSimilarity(Documents documents) {

        float currentSimilarityRate = main.getSimilarityRate();

        if (getSimilarityFacts() == null || getSimilarityFacts().isEmpty() || this.similarityRate != currentSimilarityRate || PhoenixSettings.hasChange()) {
            similarityFacts = new ArrayList<ArrayList<ArrayList<String>>>();
        } else if (documentsSize == documents.getSize()) {
            return;
        }

        documentsSize = documents.getSize();

        this.similarityRate = currentSimilarityRate;

        if (getSimilarity().size() > 0) { //Caso já existam documentos iniciados como "Similarity", estes serão removidos
            getSimilarity().clear();
        }

        int iterations = ((documents.getSize() * (documents.getSize() - 1)) / 2) * 9;

        for (int i = 0; i < documents.getSize(); i++) { //Adiciona todos os documentos passados ao conjunto "Similarity" 
            getSimilarity().add(new Similarity(documents.getDocuments().get(i).getPathWay()));

            getSimilarity().get(i).translateFacts(documents.getDocuments().get(i).getFile());
        }

        ProgressHandler.restart(iterations, "Translating XML files using Similarity");
        try {
            for (int i = 0; i < (getSimilarity().size() - 1); i++) {
                if (i > getSimilarityFacts().size() - 1) {
                    getSimilarityFacts().add(new ArrayList<ArrayList<String>>());
                }
                for (int j = i + 1; j < getSimilarity().size(); j++) {
                    if (i != j) {

                        ProgressHandler.setLabel("Getting the similarity between " + documents.getFile(i).getName() + " and " + documents.getFile(j).getName());

                        if (documents.getSize() - i - 1 > getSimilarityFacts().get(i).size()) {

                            getSimilarityFacts().get(i).add(new ArrayList<String>());

                            getSimilarity().get(i).documentsWithIDs(documents.getDocuments().get(i).getPathWay(), documents.getDocuments().get(j).getPathWay(), this.similarityRate);

                            ProgressHandler.setLabel("Translating Temporary Files into Prolog facts");
                            getSimilarity().get(i).translateFactsID(new File(getSimilarity().get(i).getPathID1())); //Gera os fatos do primeiro documento
                            getSimilarityFacts().get(i).get(j - i - 1).add(getSimilarity().get(i).getFactsSimilarityID());
                            ProgressHandler.increase();

                            getSimilarity().get(i).translateFactsID(new File(getSimilarity().get(i).getPathID2())); //Gera os fatos do segundo documento
                            getSimilarityFacts().get(i).get(j - i - 1).add(getSimilarity().get(i).getFactsSimilarityID());
                            ProgressHandler.increase();

                        } else {
                            for (int a = 0; a < 8; a++) {
                                ProgressHandler.increase();
                            }
                        }

                        ProgressHandler.increase();

                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error on translating the files", "Error", JOptionPane.ERROR_MESSAGE);
        }

        ProgressHandler.stop();

    }

    /**
     * Armazena os resultados das inferencias utilizando o método "Context Key".
     */
    public void startResultsInferenciaContextKey(boolean[] marked) {

        String factsInference; //Fatos dos dois documentos que serão utilizados na inferência
        ArrayList<String> finishResult; //Resultados da inferência

        //Limpa os resultados caso já exista algum
        resultsInference.clear();

        ProgressHandler.restart((getContextKey().size() * (getContextKey().size() - 1)) / 2, "Performing inference");

        //Gera resultados para todos os pares de documentos
        for (int i = 0; i < (getContextKey().size() - 1); i++) {
            for (int j = i + 1; j < getContextKey().size(); j++) {
                if (i != j) {
                    ProgressHandler.setLabel("Performing inference");

                    if (marked[i] && marked[j]) {
                        //Concatena e normaliza os fatos pertencentes a dois documentos
                        factsInference = ContextKey.setStandardFacts(getContextKey().get(i).getFacts(), getContextKey().get(j).getFacts());
                        finishResult = getInferenceModule().performInference(factsInference, getRulesModule()); //Realiza a inferência
                        getResultsInference().add(new Results(i + 1, j + 1, finishResult)); //Armazena os resultados referentes aos dois documentos utilizados
                    } else {
                        ArrayList<String> nullResult = new ArrayList<String>();
                        nullResult.add("");
                        getResultsInference().add(new Results(i + 1, j + 1, nullResult));
                    }

                    ProgressHandler.increase();
                }
            }
        }
        ProgressHandler.stop();
    }

    /**
     * Armazena os resultados das inferencias utilizando o método "Similarity".
     *
     * @param documents
     * @param similarityRate
     */
    public void startResultsInferenciaSimilarity(Documents documents, boolean[] marked) {

        if (getSimilarityFacts() == null || getSimilarityFacts().isEmpty() || this.similarityRate != main.getSimilarityRate()){// || PhoenixSettings.hasChange()) {
            similarityFacts = new ArrayList<ArrayList<ArrayList<String>>>();
        }

        this.similarityRate = main.getSimilarityRate();

        String factsInference; //Fatos dos dois documentos que serão utilizados na inferência
        ArrayList<String> finishResult; //Resultados da inferência

        //Limpa os resultados caso já exista algum
        resultsInference.clear();

        ProgressHandler.restart((getContextKey().size() * (getContextKey().size() - 1)) / 2, "Performing inference");

        //Gera resultados para todos os pares de documentos
        for (int i = 0; i < (getSimilarity().size() - 1); i++) {
            if (i > getSimilarityFacts().size() - 1) {
                getSimilarityFacts().add(new ArrayList<ArrayList<String>>());
            }
            for (int j = i + 1; j < getSimilarity().size(); j++) {
                if (i != j) {

                    ProgressHandler.setLabel("Performing inference");

                    if (marked[i] && marked[j]) {
                        //Concatena e normaliza os fatos pertencentes a dois documentos
                        factsInference = Similarity.setStandardFacts(getSimilarityFacts().get(i).get(j - i - 1).get(0), getSimilarityFacts().get(i).get(j - i - 1).get(1));
                        finishResult = getInferenceModule().performInference(factsInference, getRulesModule()); //Realiza a inferência
                        getResultsInference().add(new Results(i + 1, j + 1, finishResult)); //Armazena os resultados referentes aos dois documentos utilizados
                    } else {
                        ArrayList<String> nullResult = new ArrayList<String>();
                        nullResult.add("");
                        getResultsInference().add(new Results(i + 1, j + 1, nullResult));
                    }

                    ProgressHandler.increase();
                }
            }
        }
        ProgressHandler.stop();
    }

    public float getSimilarityRate() {
        return this.similarityRate;
    }

    public void reset() {
        getSimilarityFacts().clear();
        documentsSize = 0;
    }

    /**
     * Principal.
     *
     * @param args
     */
    public static void main(String[] args) {
        Manager manager = new Manager();
        main = new MainInterface(manager); //Invoca o construtor da interface principal
    }

    /**
     * @return Os fatos do módulo de similaridade
     */
    public ArrayList<ArrayList<ArrayList<String>>> getSimilarityFacts() {
        return similarityFacts;
    }
}

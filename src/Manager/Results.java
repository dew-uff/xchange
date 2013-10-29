package Manager;

import java.util.ArrayList;

/**
 * Classe que armazenará os resultados da inferência e os IDs dos documentos aos quais eles pertencem.
 * @author Guilherme Martins
 */
public class Results {
    
    private int document1, document2; //Representa os IDs dos documentos
    private ArrayList<String> resultInference = new ArrayList<String>(); //ArrayList com os resultados entre os dois documentos especificados

    /**
     * Construtor da classe "Results".
     * @param d1
     * @param d2
     * @param res 
     */
    public Results(int d1, int d2, ArrayList<String> res){
        document1 = d1;
        document2 = d2;
        resultInference = res;
    }

    /**
     * @return document1
     */
    public int getDocument1() {
        return document1;
    }

    /**
     * @return document2
     */
    public int getDocument2() {
        return document2;
    }

    /**
     * @return resultInference
     */
    public ArrayList<String> getResultInference() {
        return resultInference;
    }
}

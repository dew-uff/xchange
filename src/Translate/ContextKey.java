package Translate;

import java.util.HashSet;
/**
 *
 * @author Celio H. N. Larcher Junior e Guilherme Martins
 */
public class ContextKey extends TranslateModule {
    
    /**
     * Método construtor da classe.
     */
    public ContextKey(){
        super();
    }
    
    /**
     * Operação para dividir os fatos concentrados em uma única String em um vetor de Strings onde cada posição guarda o nome de fato isolado. 
     * A String é adquirida no processo de tradução.
     * @return arrayResult
     * Vetor de Strings com os nomes dos fatos separados por indice do vetor.
     */
    public String[] getNameFacts() {
        String translateFacts=this.getFacts().replaceAll("(\n|\r)", ""); //Retira as quebras de linha
        String separateFacts[]=translateFacts.split("\\."); //Divide os fatos traduzidos em um vetor através do ponto final
        HashSet<String> getFacts=new HashSet<String>(); //Cria uma tabela hashset(em uma tabela hashset não existem elementos repetidos)
        for (int i=0;i<separateFacts.length;i++) { //Varre todos os fatos
            if((separateFacts[i].indexOf(",")>0)&&(!separateFacts[i].substring(separateFacts[i].indexOf("(")+1,separateFacts[i].indexOf(",")).equals("base"))&&(!separateFacts[i].substring(separateFacts[i].indexOf("(")+1,separateFacts[i].indexOf(",")).equals("modified"))){
                if((!separateFacts[i].equals(" "))&&!(separateFacts[i]==null)){ //Caso seja um fato candidato a ser chave de contexto
                        getFacts.add(separateFacts[i].substring(0,separateFacts[i].indexOf("("))); //Ele é adcionado na tabela hash
                }
            }
        }
        String[] nameFacts=(String[]) getFacts.toArray(new String[0]); //Passa todos os fatos adcionados para um array
        String[] arrayResult = new String[nameFacts.length]; //Ao adciona-los eles ficam em ordem invertida

        for (int i=0; i<nameFacts.length;i++){ //Cria um vetor reordenado
             arrayResult[i]=nameFacts[nameFacts.length-i-1];
        }
        return arrayResult; //Retorna o array com o nomes dos fatos
    }
}


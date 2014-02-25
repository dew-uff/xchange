package Rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Celio H. N. Larcher Junior e Guilherme Martins
 */
public class RulesModule{
   private ArrayList<Rule> rules = new ArrayList<Rule>();
   private ArrayList<Rule> selectRules = new ArrayList<Rule>();

   /**
    * Adciona regra a lista de regras, verificando se a mesma não se repete.
    * @param rule 
    * String com a regra.
    */
   public void addRule(String rule){
       Iterator it = rules.iterator();
       boolean contain=false;
       while(it.hasNext()){ //Verifica se a regra não existe ainda
           Rule aux = (Rule)it.next();
           if(aux.getRule().compareTo(rule)==0)contain=true; //Caso exista, muda a variável contain para true
       }
       if(!contain){ //Caso não exista a regra
           Rule newrule = new Rule(rule); //Cria o objeto Rule com a string da regra
           this.rules.add(newrule); //Adciona a regra na lista de regras
       }
   }
   
   /**
    * Adiciona as regras selecionadas em sua respectiva variável.
    * @param ruleActive
    * ArrayList com as regras selecionadas pelo usuário.
    */
   public void addSelectRules(ArrayList<String> ruleActive){
       Iterator it = ruleActive.iterator();
       while(it.hasNext()){ //Percorre as regras selecionadas
           String aux = it.next().toString();
           for(int i = 0; i < rules.size(); i++){ //Percorre todas as regras criadas e/ou adicionadas
               if(rules.get(i).getRule().indexOf(aux) >= 0){ //Verifica se a regra selecionada realmente existe
                    getSelectRules().add(rules.get(i));
               }
           }         
       }     
   }
   
   /**
    * Adiciona as regras selecionadas em sua respectiva variável recebendo como parametro uma String.
    * @param rule
    * String com uma das regras selecionadas pelo usuário.
    */
   public void addSelectRules(String rule){
       Iterator it=selectRules.iterator();
       boolean contain=false;
       while(it.hasNext()){ //Verifica se a regra existe
           Rule aux=(Rule)it.next();
           if(aux.getRule().compareTo(rule)==0)contain=true; //Caso exista, muda a variável contain para true
       }
       if(!contain){ //Caso não exista a regra
           Rule newrule=new Rule(rule); //Cria o objeto Rule com a string da regra
           this.selectRules.add(newrule); //Adciona a regra na lista de regras
       }
   }

   /**
    * Transforma a lista contendo todas as regras em uma string com cada regra dividida por uma quebra de linha.
    * @return stringRules
    * Regras em forma de String.
    */
   public String getRulesString(){
       Iterator it=this.rules.iterator();
       String stringRules=new String();
       while(it.hasNext()){ //Percorre a lista de regras
           Rule aux=(Rule)it.next();
           stringRules=stringRules+aux.getRule()+"\n"; //Cada regra é adicionada a string com uma quebra de linha após.
       }
       return stringRules;
   }
   
   /**
    * Transforma a lista contendo todas as regras selecionadas em uma string com cada regra dividida por uma quebra de linha.
    * @return stringRules
    * Regras selecionadas em forma de String.
    */
   public String getSelectRulesString(){
       Iterator it=this.selectRules.iterator();
       String stringRules=new String();
       while(it.hasNext()){ //Percorre a lista de regras
           Rule aux=(Rule)it.next();
           stringRules=stringRules+aux.getRule()+"\n"; //Cada regra é adcionada a string com uma quebra de linha após.
       }
       return stringRules;
   }
   
   /**
    * Separa as regras contidas em uma única String em um vetor de Strings, onde cada posição deste vertor possuirá uma regra.
    * @param allRules
    * String com todas as regras juntas.
    * @return partRules
    * Regras separadas em um vetor de Strings onde cada posição comtêm uma regra.
    */
   public String[] partRules(String allRules) {
        //Remove quebras de linha
        allRules = allRules.replaceAll("(\n|\r)", "");
        //Separa as regras através do ponto final dos axiomas.
        String[] partRules = allRules.split("\\.");

        return partRules;
    }
   
    /**
     * Separa as regras do predicado.
     * @param rulesParam 
     * Um vetor de Strings com uma regra completa, nome + argumentos + predicado, em cada índice.
     * @return
     * Retorna um outro vetor de String com uma regra em cada indice, contendo nome + argumentos.
     */
    public String[] getNameAndArgumentsRules(String[] rulesParam) {
        String[] nameAndArgumentsRules = new String[rulesParam.length];
        for (int i = 0; i < rulesParam.length; i++) {
            int idxOpenP = rulesParam[i].indexOf(")");
            nameAndArgumentsRules[i] = rulesParam[i].substring(0, idxOpenP + 1).trim();
        }
        return nameAndArgumentsRules;
    }
    
    /**
     * Remove todas as regras contidas no arrayList "rules".
     */
    public void clearRules(){
        rules.clear();
    }
    
    /**
     * Remove todas as regras selecionadas contidas no arrayList "selectRules".
     */
    public void clearSelectRules(){
        selectRules.clear();
    }

    /**
     * Realiza ajustes tanto no arrayList "rules" quanto no "selectRules".
     * Estes ajustes são: remoção de regras em branco e de regras duplicadas.
     */
    public void adjustRules(){
        String insert = ""; //Variavel auxiliar para reinserir as regras em seus devidos lugares
        
        //Separa as regras em um ArrayList onde cada posicao representa uma regra
        String stringAux1 = this.getRulesString();
        String stringAux2 = this.getSelectRulesString();
        String[] stringVectorAux1 = stringAux1.split("(\n|\r)");
        String[] stringVectorAux2 = stringAux2.split("(\n|\r)");
        ArrayList<String> arrayAux1 = new ArrayList<String>(Arrays.asList(stringVectorAux1));
        ArrayList<String> arrayAux2 = new ArrayList<String>(Arrays.asList(stringVectorAux2));
        arrayAux1 = new ArrayList(new HashSet(arrayAux1));
        arrayAux2 = new ArrayList(new HashSet(arrayAux2));
        
        //Remove todas as regras antigas
        this.clearRules();
        this.clearSelectRules();
        
        //Readiciona as regras SEM conter regras repetidas ou em branco
        Iterator it1 = arrayAux1.iterator();
        while(it1.hasNext()){ //Reinsere as regras inseridas pelo usuário
            insert = (String) it1.next();
            if(!insert.equals("\n") && !insert.equals("\r") && !insert.equals("")){
                this.addRule(insert);
            }
        }
        Iterator it2 = arrayAux2.iterator();
        while(it2.hasNext()){ //Reinsere as regras selecionadas pelo usuário
            insert = (String) it2.next();
            if(!insert.equals("\n") && !insert.equals("\r") && !insert.equals("") && !insert.equals(" ")){
                this.addSelectRules(insert);
            } 
        }        
    }
    
    /**
     * Responsável por retornar as regras selecionadas.
     * @return selectRules
     * ArrayList com as regras selecionadas.
     */
    public ArrayList<Rule> getSelectRules() {
        return selectRules;
    }
    
    /**
     * Responsável por retornar o conjunto de regras que foram introduzidas pelo usuário.
     * @return rules
     * ArrayList com as regras introduzidas.
     */
    public ArrayList<Rule> getRules() {
        return rules;
    }
}

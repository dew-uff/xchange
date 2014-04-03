package Rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
   public void addRule(String rule, List<Condition> conditions){
       Iterator it = rules.iterator();
       boolean contain=false;
       while(it.hasNext()){ //Verifica se a regra não existe ainda
           Rule aux = (Rule)it.next();
           if(aux.getRule().compareTo(rule)==0)contain=true; //Caso exista, muda a variável contain para true
       }
       if(!contain){ //Caso não exista a regra
           Rule newrule = new Rule(rule, conditions); //Cria o objeto Rule com a string da regra
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
   public void addSelectRules(String rule, List<Condition> conditions){
       Iterator it=selectRules.iterator();
       boolean contain=false;
       while(it.hasNext()){ //Verifica se a regra existe
           Rule aux=(Rule)it.next();
           if(aux.getRule().compareTo(rule)==0)contain=true; //Caso exista, muda a variável contain para true
       }
       if(!contain){ //Caso não exista a regra
           Rule newrule=new Rule(rule, conditions); //Cria o objeto Rule com a string da regra
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

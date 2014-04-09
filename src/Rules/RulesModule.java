package Rules;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Celio H. N. Larcher Junior e Guilherme Martins
 */
public class RulesModule {

    private ArrayList<Rule> rules = new ArrayList<Rule>();
    private ArrayList<Rule> selectRules = new ArrayList<Rule>();

    /**
     * Adciona regra a lista de regras, verificando se a mesma não se repete.
     *
     * @param newRule String com a regra.
     */
    public void addRule(Rule newRule) {
        Iterator it = rules.iterator();
        boolean contain = false;
        while (it.hasNext()) { //Verifica se a regra não existe ainda
            Rule aux = (Rule) it.next();
            if (aux.getRule().compareTo(newRule.getRule()) == 0) {
                contain = true; //Caso exista, muda a variável contain para true
            }
        }
        if (!contain) { //Caso não exista a regra
            this.rules.add(newRule); //Adciona a regra na lista de regras
        }
    }

    /**
     * Adiciona as regras selecionadas em sua respectiva variável.
     *
     * @param ruleActive ArrayList com as regras selecionadas pelo usuário.
     */
    public void addSelectRules(ArrayList<String> ruleActive) {
        Iterator it = ruleActive.iterator();
        while (it.hasNext()) { //Percorre as regras selecionadas
            String aux = it.next().toString();
            for (int i = 0; i < rules.size(); i++) { //Percorre todas as regras criadas e/ou adicionadas
                if (rules.get(i).getRule().indexOf(aux) >= 0) { //Verifica se a regra selecionada realmente existe
                    getSelectRules().add(rules.get(i));
                }
            }
        }
    }
    
    public void removeRule(int index){
        selectRules.remove(rules.get(index));
        rules.remove(index);        
    }

    /**
     * Transforma a lista contendo todas as regras em uma string com cada regra
     * dividida por uma quebra de linha.
     *
     * @return stringRules Regras em forma de String.
     */
    public String getRulesString() {
        StringBuilder stringRules = new StringBuilder();
        for (Rule selectRule : this.rules) {
            stringRules.append(selectRule.getRule()).append("\n"); //Cada regra é adcionada a string com uma quebra de linha após.
        }

        return stringRules.toString();
    }

    /**
     * Transforma a lista contendo todas as regras selecionadas em uma string
     * com cada regra dividida por uma quebra de linha.
     *
     * @return stringRules Regras selecionadas em forma de String.
     */
    public String getSelectRulesString() {
        StringBuilder stringRules = new StringBuilder();
        for (Rule selectRule : this.selectRules) {
            stringRules.append(selectRule.getRule()).append("\n"); //Cada regra é adcionada a string com uma quebra de linha após.
        }

        return stringRules.toString();
    }

    /**
     * Separa as regras contidas em uma única String em um vetor de Strings,
     * onde cada posição deste vertor possuirá uma regra.
     *
     * @param allRules String com todas as regras juntas.
     * @return partRules Regras separadas em um vetor de Strings onde cada
     * posição comtêm uma regra.
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
     *
     * @param rulesParam Um vetor de Strings com uma regra completa, nome +
     * argumentos + predicado, em cada índice.
     * @return Retorna um outro vetor de String com uma regra em cada indice,
     * contendo nome + argumentos.
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
    public void clearRules() {
        rules.clear();
    }

    /**
     * Remove todas as regras selecionadas contidas no arrayList "selectRules".
     */
    public void clearSelectRules() {
        selectRules.clear();
    }

    /**
     * Responsável por retornar as regras selecionadas.
     *
     * @return selectRules ArrayList com as regras selecionadas.
     */
    public ArrayList<Rule> getSelectRules() {
        return selectRules;
    }

    /**
     * Responsável por retornar o conjunto de regras que foram introduzidas pelo
     * usuário.
     *
     * @return rules ArrayList com as regras introduzidas.
     */
    public ArrayList<Rule> getRules() {
        return rules;
    }
}

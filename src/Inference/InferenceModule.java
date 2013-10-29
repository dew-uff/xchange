package Inference;

import Rules.Rule;
import Rules.RulesModule;
import alice.tuprolog.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
/**
 *
 * @author Celio H. N. Larcher Junior e Guilherme Martins
 */
public class InferenceModule {
     /**
     * Retorna o resultado da aplicação das regras Prolog nos fatos traduzidos e normalizados.
     * @param normalizedFacts e listRules
     * Onde normalizedFacts é uma String que contêm os fatos traduzidos, e listRules contêm as regras inseridas pelo usuário.
     * @return inferenceResult
     * Resultado da inferência.
     */
    public ArrayList<String> performInference(String normalizedFacts, RulesModule listRules) {
        ArrayList<String> inferenceResult = new ArrayList<String>();
        Prolog inference=new Prolog(); //Cria um objeto do tipo Prolog que irá executar a inferencia
        Theory theory;
        try {
            theory=new Theory(normalizedFacts+"\n"+listRules.getRulesString()); //Cria uma variável do tipo "Theory" inicializada com os fatos norlaizados e a string das regras
            inference.setTheory(theory); //Inicializa o objeto inference com a teoria
            for (Rule rule:listRules.getRules()) { //Para cada regra, executa o método "inferenceRule", somando seu resultado
                inferenceResult.add(inferenceRule(inference,rule.structRule())+"\n"); //Adiciona os resultados no ArrayList "inferenceResult"
            }
        }catch(InvalidTheoryException ex) {
                JOptionPane.showMessageDialog(null, "Error on theory", "Error",JOptionPane.ERROR_MESSAGE);
        }
        return inferenceResult; //Retorna todos os resultados da inferencia
    }

     /**
     * Retorna o resultado da execução de uma regra sobre os fatos.
     * @param objeto 
     * Objeto Prolog inicializado com a teoria e a "struct" com a regra.
     * @return result
     * Resultado da inferencia de regra.
     */
    private String inferenceRule(Prolog inference,Struct structRule){
        SolveInfo solution=inference.solve(structRule); //Atribui a variável solution o resultado da primeira aplicação da regra
        String result = structRule.getName().toUpperCase()+":\n"; //Atribui o nome da regra a string para identificação da regra ao apresentar o resultado
        try{
            do{
                if(solution.isSuccess()){ //Se a inferência obter sucesso
                    for(int i=0;i<structRule.getArity();i++)
                        result+=solution.getVarValue(structRule.getArg(i).toString())+"\n"; //Soma ao resultado as soluções encontradas na variável Prolog ao aplicar a regra
                }
                if (inference.hasOpenAlternatives()){ //Se houver soluções a serem exploradas (caso seja primeira execução)
                    try{
                        solution=inference.solveNext(); //Tenta encontrar uma outra solução aplicando a inferência da regra
                    }catch(NoMoreSolutionException erro){
                            JOptionPane.showMessageDialog(null, "Error on theory", "Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }while(inference.hasOpenAlternatives()); //Enquanto houverem soluções a serem exploradas
            inference.solveEnd(); //Finaliza a inferência da regra passada
        }catch(NoSolutionException ex){
            JOptionPane.showMessageDialog(null, "Error on theory", "Error",JOptionPane.ERROR_MESSAGE);
        }
        return result; //Retorna o resultado da inferência referente a uma regra
    }
}

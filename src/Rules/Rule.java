package Rules;

import alice.tuprolog.*;
import java.util.List;

/**
 * @author Celio H. N. Larcher Junior e Guilherme Martins
 */
public class Rule {

    private String name;
    private String output;
    private List<Condition> conditions;
    private String rule;

    /**
     * Construtor da classe.
     *
     * @param name
     * @param output
     * @param conditions
     * @param rule
     */
    public Rule(String name, String output, List<Condition> conditions, String rule) {
        this.name = name;
        this.output = output;
        this.conditions = conditions;
        this.rule = rule;
    }
        
    /**
     * Função que retorna o nome da regra.
     *
     * @return Nome da regra.
     */
    public String getName() {
        return this.name;
    }
    
        
    /**
     * Função que retorna o a saída da regra.
     *
     * @return Saída da regra.
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * Responsável por retornar as condições de uma determinada regra.
     *
     * @return conditions List<Condition> contendo as condições.
     */
    public List<Condition> getConditions() {
        return this.conditions;
    }

    /**
     * Responsável por retornar uma determinada regra.
     *
     * @return rule String contendo a regra.
     */
    public String getRule() {
        return this.rule;
    }
    
    /**
     * Função que retorna um vetor com os argumentos da regra.
     *
     * @return arguments Vetor com cada posição correspondendo a um argumento.
     */
    public String[] getArguments() {
        String arguments = this.rule.substring(this.rule.indexOf("(") + 1, this.rule.indexOf(")"));
        return arguments.split(",");
    }

    /**
     * Função que cria uma variável do tipo Struct adequada ao uso na execução
     * da inferencia.
     *
     * @return struct Retorna Struct da regra
     */
    public Struct structRule() {
        String[] argumentsList = this.getArguments();//recebe a lista de argumentos da regra
        Var[] varList = new Var[argumentsList.length];//cria uma lista de Var, para representar cada argumento
        for (int i = 0; i < argumentsList.length; i++) {//a cada posição da lista de argumentos em string, o atribui a lista de Var
            varList[i] = new Var("Xid" + argumentsList[i]);//cria um objeto var inicializado com Xid+argumento
        }
        Struct struct = new Struct(this.getName().replaceAll(" ", ""), varList);//cria Struct com nome da regra e lista de Var
        return struct;//retorna Struct
    }

    public boolean isEquals(Object object, boolean checkName){
        boolean equals = false;

        if (object != null && object instanceof Rule)
        {
            if(checkName && this.name.equals(((Rule)object).getName()))
                equals = true;
            else if(this.conditions.size() == ((Rule)object).getConditions().size()){
                int countEquals = 0;
                for(Condition cond: ((Rule)object).getConditions()){
                    if(this.conditions.contains(cond))
                        countEquals++;
                }
                if(countEquals == this.conditions.size())
                    equals = true;
            }
        }

        return equals;
    }
    
    @Override
    public boolean equals(Object object)
    {
        return isEquals(object, true);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.conditions != null ? this.conditions.hashCode() : 0);
        return hash;
    }
    
}

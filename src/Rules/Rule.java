package Rules;
import alice.tuprolog.*;

/**
 * @author Celio H. N. Larcher Junior e Guilherme Martins
 */
public class Rule {
    private String rule;

    /**
     * Construtor da classe.
     * @param rule 
     */
    Rule(String rule){
        this.rule=rule;
    }
    
    /**
     * Responsável por retornar uma determinada regra.
     * @return rule
     * String contendo a regra.
     */
    public String getRule(){
        return this.rule;
    }
    
    /**
     * Função que retorna o nome da regra.
     * @return 
     * Nome da regra.
     */
    public String getName() {
        return this.rule.substring(0, this.rule.indexOf("("));
    }
    
    /**
     * Função que retorna um vetor com os argumentos da regra.
     * @return arguments
     * Vetor com cada posição correspondendo a um argumento.
     */
    public String[] getArguments() {
        String arguments=this.rule.substring(this.rule.indexOf("(")+1,this.rule.indexOf(")"));
        return arguments.split(",");
    }
    
    
    
    /**
     * Função que cria uma variável do tipo Struct adequada ao uso na execução da inferencia.
     * @return struct
     * Retorna Struct da regra
     */
    public Struct structRule(){
        String[] argumentsList = this.getArguments();//recebe a lista de argumentos da regra
        Var[] varList = new Var[argumentsList.length];//cria uma lista de Var, para representar cada argumento
        for (int i=0;i<argumentsList.length;i++) {//a cada posição da lista de argumentos em string, o atribui a lista de Var
            varList[i]=new Var("Xid" + argumentsList[i]);//cria um objeto var inicializado com Xid+argumento
        }
        Struct struct = new Struct(this.getName(), varList);//cria Struct com nome da regra e lista de Var
        return struct;//retorna Struct
    }
}
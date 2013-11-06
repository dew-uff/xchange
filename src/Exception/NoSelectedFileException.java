package Exception;

/**
 * Excessão associada a não seleção de arquivos durante a solicitação de abertura.
 * @author Marcio Tadeu de Oliveira Jr.
 */
public class NoSelectedFileException extends Exception{ 
    public String getMessage(){
        return "No file has been selected!";
    }
}
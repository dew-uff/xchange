package GUI.FileManager;

/**
 *
 * @author Carlos Roberto Carvalho Oliveira
 * 
 * Essa classe é utilizada para gerar o relatorio no iReport
 * Contem o conteudo que sera impresso no relatorio
 * iReport obtem o valor atraves do metodo get
 */
public class ContentReport {
    
    private String content;
    
    public void setContent(String content){
        this.content = content;
    }
    
    public String getContent(){
        return this.content;
    }
    
}

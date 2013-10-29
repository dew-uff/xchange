package Documents;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;
/**
 * Classe que representa o conjunto de documentos XML carregados no momento.
 * @author Marcio Tadeu de Oliveira Jr.
 */
public class Documents{
    private ArrayList<Document> documents;//lista de documentos XML 

    /**
     * Metodo construtor da classe. Cria um array contendo Document.
     */
    public Documents(){
        this.documents = new ArrayList<Document>();
        Document.differentProject();
    }

    /**
     * Adiciona o arquivo file caso ele não exista no projeto.
     * @param file
     * @return booleano representando sucesso ou fracasso na adição.
     */
    public boolean add(File file){
        if(file==null) {
            return false;
        }
        for(Document aux:getDocuments()){
            if(aux.equals(file)){
                JOptionPane.showMessageDialog(null, "This document have already been added to the project");
                return false;
            }
        }
        getDocuments().add(new Document(file));
        return true;
    }

    /**
     * Retorna os nomes pelos quais os arquivos XML são tratados pelos usuarios.
     * @return docs(array de getCode())
     */
    public ArrayList<String> docsIds(){//
        ArrayList<String> docs = new ArrayList<String>();
        for(Document f:getDocuments()){
            docs.add(f.getCode());
        }                
        return docs;
    }
    /**
     * Remove um arquivo XML através do nome passado.
     * @param s
     */
    public void remove(String s){
        for(Document f:getDocuments()){
            if(s.equals(f.toString())) {
                getDocuments().remove(f);
            }
        }
    }
    
    /**
     * Remove um arquivo XML pela ID passada.
     * @param id
     */
    public void remove(int id){
        for(Document f:getDocuments()){
            if(id==f.getId()) {
                getDocuments().remove(f);
            }
        }
    }
    
    /**
     * Retorna o conteúdo de um arquivo XML sendo buscado por seu nome ou vazio caso não haja.
     * @param string
     * @return getContent()
     */
    public String getContent(String string){
        for(Document f:getDocuments()){
            if(string.equals(f.toString())) {
                return f.getContent();
            }
        }
        return "";
    }

    /**
     * Retorna o conteúdo de um arquivo XML sendo buscado por sua ID.
     * @param id
     * @return getContent()
     */
    public String getContent(int id){
        if(id < this.getSize() && id >= 0){
            return this.getDocuments().get(id).getContent();
        }
        return "";
    }

    /**
     * Retorna o numero de arquivos XML presentes no projeto.
     * @return size()
     */
    public int getSize(){
        return this.getDocuments().size();
    }

    /**
     * Retorna os caminhos completos dos arquivos XML.
     * @return array(getPathWay())
     */
    public ArrayList<String> getPathWays(){
        ArrayList<String> pathWays = new ArrayList<String>();
        for(Document doc:getDocuments()){
            pathWays.add(doc.getPathWay());
        }
        return pathWays;
    }

    /**
     * Retorna o array de documentos.
     * @return documents
     */
    public ArrayList<Document> getDocuments() {
        return documents;
    }

    /**
     * Atribui um vetor de documentos a lista.
     * @param documents
     */
    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }

    /**
     * Retorna o documento associado a ID passada ou null caso não encontrado.
     * @param ID
     * @return document
     */
    public Document getDocument(int id){
        Document document=null;
        for(Document doc:getDocuments()){
            if(doc.getId()==id){
                document=doc;
                break;
            }
        }
        return document;
    }
    
    public File getFile(int id){
        return this.documents.get(id).getFile();
    }
}
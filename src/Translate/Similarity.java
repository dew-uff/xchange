package Translate;

import GUI.Util.ProgressHandler;
import Phoenix.PhoenixWrapper;
import br.uff.ic.gems.phoenix.SettingsHelper;
import br.uff.ic.gems.phoenix.XmlParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 *
 * @author Guilherme Martins
 */
public class Similarity extends ContextKey {

    private int ID = 0;
    private String factsSimilarityID; //Fatos de um determinado arquivo com ID
    private String pathWayDocument;
    private String pathID1, pathID2, documentID1, documentID2;

    public Similarity(String pathWayDocument) {
        super();
        this.pathWayDocument = pathWayDocument;
    }

    /**
     * @return factsSimilarityID
     */
    public String getFactsSimilarityID() {
        return factsSimilarityID;
    }

    /**
     * @return pathWayDocument
     */
    public String getPathWayDocument() {
        return pathWayDocument;
    }

    /**
     * @return pathID1
     */
    public String getPathID1() {
        return pathID1;
    }

    /**
     * @return pathID2
     */
    public String getPathID2() {
        return pathID2;
    }

    /**
     * Função que converte os arquivos XML carregados em fatos Prolog adequados
     * para a inferencia das regras.
     *
     * @param fileTranslate Documento XML que se deseja traduzir.
     */
    public void translateFactsID(File fileTranslate) {
        this.translateFacts(fileTranslate);
        this.factsSimilarityID = this.facts;
    }

    /**
     * Cria um documento para representar um documento XLM.
     *
     * @return doc Um documento.
     */
    public static Document createDomDocument() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            return doc;
        } catch (ParserConfigurationException e) {
        }
        return null;
    }

    /**
     * Cria os documentos com IDs de acordo com sua similaridade.
     *
     * @param pathDocument1 String contendo o caminho do primeiro documento XML
     * a ser comparado.
     * @param pathDocument2 String contendo o caminho do segundo documento XML a
     * ser comparado.
     */
    public void documentsWithIDs(String pathDocument1, String pathDocument2) {
        String file1 = new File(pathDocument1).getName();
        String file2 = new File(pathDocument2).getName();
        String preName = System.getProperty("user.dir")+System.getProperty("file.separator")+"temp"+System.getProperty("file.separator")+file1.substring(0, file1.length()-4)+file2.substring(0, file2.length()-4)+"_";
        
        pathID1 = preName+file1;
        pathID2 = preName+file2;
      
        ProgressHandler.setLabel("Creating DOM Documents");
        Document XMLwithID1 = createDomDocument();
        Document XMLwithID2 = createDomDocument();
        ProgressHandler.increase();

        ProgressHandler.setLabel("Calculating Similarity");
        String similarityDiff = PhoenixWrapper.doSimilarity(pathDocument1, pathDocument2);
        ProgressHandler.increase();

        ProgressHandler.setLabel("Working on similarity diff");
        Document xml3 = PhoenixWrapper.createDOMDocument(similarityDiff);
        Node rootXML = xml3.getDocumentElement().getFirstChild();
        ProgressHandler.increase();

        ProgressHandler.setLabel("Creating temporary XML objects");
        Element element1 = XMLwithID1.createElement(rootXML.getNodeName());
        XMLwithID1.appendChild(element1);

        Element element2 = XMLwithID2.createElement(rootXML.getNodeName());
        XMLwithID2.appendChild(element2);

        createXMLwithIDsNEWWWW(rootXML, XMLwithID1, XMLwithID2, element1, element2, true);
        ProgressHandler.increase();

        ProgressHandler.setLabel("Creating temporary XML files");
        //removeEmptyIDs(toString(XMLwithID1), toString(XMLwithID2)); //Para que serve isso?!
        salvaXmlComId(toString(XMLwithID1), toString(XMLwithID2));

        ProgressHandler.increase();

    }
    
    /**
     * Cria os documentos XML com IDs de acordo com a similaridade entre os
     * elementos.
     *
     * @param no
     * @param doc1
     * @param doc2
     * @param e1
     * @param e2
     */
    private void createXMLwithIDsNEWWWW(Node no, Document doc1, Document doc2, Element e1, Element e2, boolean first) {
        if (no.getFirstChild().getNodeName().equals("diff:value")) {
            NamedNodeMap attributes = (NamedNodeMap) no.getFirstChild().getAttributes();
            Attr version1 = (Attr) attributes.getNamedItem("diff:left");
            Attr version2 = (Attr) attributes.getNamedItem("diff:right");

            Element newNode1 = doc1.createElement(no.getNodeName());
            e1.appendChild(newNode1);
            if (version1 != null) {
                newNode1.appendChild(doc1.createTextNode(version1.getValue()));
            }

            Element newNode2 = doc2.createElement(no.getNodeName());
            e2.appendChild(newNode2);
            if (version2 != null) {
                newNode2.appendChild(doc2.createTextNode(version2.getValue()));
            }
        } else {
            NodeList listChildren = no.getChildNodes();
            for (int i = 0; i < no.getChildNodes().getLength(); i++) {
                Node node = listChildren.item(i);
                NamedNodeMap nodeAttr = node.getAttributes();

                double sim = Double.parseDouble(nodeAttr.getNamedItem("diff:similarity").getNodeValue());
                if(sim == 0.0){
                    if(first) this.ID++;

                    if(nodeAttr.getNamedItem("diff:side").getNodeValue().equalsIgnoreCase("left")) {
                        copyToSide(node, doc1, e1, first);
                    } else {
                        copyToSide(node, doc2, e2, first);
                    }
                } else if(sim == 1.0){
                    if(first) this.ID++;
                    copyToSide(node, doc1, e1, first);
                    copyToSide(node, doc2, e2, first);
                } else {
                    if(first) {
                        this.ID++;
                        Element newE1 = doc1.createElement(node.getNodeName());
                        e1.appendChild(newE1);
                        
                        Element newId1 = doc1.createElement("id");
                        e1.getLastChild().appendChild(newId1);
                        newId1.appendChild(doc1.createTextNode(Integer.toString(this.ID)));
                        
                        Element newE2 = doc2.createElement(node.getNodeName());
                        e2.appendChild(newE2);
                        
                        Element newId2 = doc2.createElement("id");
                        e2.getLastChild().appendChild(newId2);
                        newId2.appendChild(doc2.createTextNode(Integer.toString(this.ID)));
                        
                        createXMLwithIDsNEWWWW(node, doc1, doc2, newE1, newE2, false);
                    } else
                        createXMLwithIDsNEWWWW(node, doc1, doc2, e1, e2, false);
                }
            }
        }
    }
    private void copyToSide(Node no, Document doc, Element e, boolean first) {
        if (first){
            Element newE = doc.createElement(no.getNodeName());
            e.appendChild(newE);
        
            Element newId = doc.createElement("id");
            e.getLastChild().appendChild(newId);
            newId.appendChild(doc.createTextNode(Integer.toString(this.ID)));
            
            copyRecursive(no, doc, newE);
        } else
            copyRecursive(no, doc, e);
    }
    
    private void copyRecursive(Node no, Document doc, Element e) {
        if (!no.hasChildNodes() && no.getNodeType() == Node.TEXT_NODE) {
            Element novoNo = doc.createElement(no.getParentNode().getNodeName());
            e.appendChild(novoNo);
            novoNo.appendChild(doc.createTextNode(no.getNodeValue()));
        } else {
            
            NodeList listChildren = no.getChildNodes();
            for (int i = 0; i < no.getChildNodes().getLength(); i++) {
                if (listChildren.item(i).hasChildNodes()
                        && listChildren.item(i).getFirstChild().getNodeType() != Node.TEXT_NODE) {
                    
                    Element novoNo = doc.createElement(listChildren.item(i).getNodeName());
                    e.appendChild(novoNo);
                
                    copyRecursive(listChildren.item(i), doc, novoNo);
                } else
                    copyRecursive(listChildren.item(i), doc, e);
            }
        }
    }

    /**
     * Método responsvel por salvar as vers´ões dos XMLs com os IDs atribuídos
     * pelo Phoenix.
     *
     * @param txt1
     * @param txt2
     */
    private void salvaXmlComId(String txt1, String txt2) {

        if (!txt1.equals("") && !txt2.equals("")) {
            try {
                PrintWriter inArq1 = new PrintWriter(getPathID1());
                inArq1.println(txt1.toString());
                inArq1.close();
                PrintWriter inArq2 = new PrintWriter(getPathID2());
                inArq2.println(txt2.toString());
                inArq2.close();
                File xml1 = new File(pathID1);
                File xml2 = new File(pathID2);
                //xml1.deleteOnExit();
                //xml2.deleteOnExit();
            } catch (Exception erro) {
                JOptionPane.showMessageDialog(null, "ERROR!" + erro.toString());
            }
        } else { //Se nao tiver texto nos textPanes 1 e 2, entao um aviso serao¡ mostrado que nao foi gerado IDs.
            JOptionPane.showMessageDialog(null, "Were generated files with blank IDs. Check the original files!");
        }
    }

    /**
     * Responsável por limpar do documento XML com IDs, aqueles cujo ID é vazio.
     * Uma vez que para documentos de tamanhos diferentes, o phoenix cria Ids
     * vazios no XML de menor tamanho para igualar os tamanhos.
     *
     * @param XmlId1
     * @param XmlId2
     */
    private void removeEmptyIDs(String XmlId1, String XmlId2) {

        String aux = "", aux2 = "";
        int i = 0, j = 0, k = 0, l = 0;
        documentID1 = "";
        documentID2 = "";

        //PARA O ARQUIVO BASE
        while (i < XmlId1.length()) { //Enquanto não chegar no fim do arquivo
            if (XmlId1.charAt(i) == '<') {
                for (j = i; j < XmlId1.length(); j++) { //Verifica os caracteres seguintes
                    if (XmlId1.charAt(j) != '/') { //Enquanto for != '/' guarda os caracteres concatenados
                        aux = aux + XmlId1.charAt(j); //Guarda o caracter '/'
                    } else {
                        if (XmlId1.charAt(j + 1) != '>') { //Se o proximo nao for '>' significa que a tag nao esta vazia
                            aux = aux + XmlId1.charAt(j);
                            while (XmlId1.charAt(j + 1) != '>') { //Percorre a tag ate achar o seu fim
                                aux = aux + XmlId1.charAt(j + 1);
                                j++;
                            }
                            aux = aux + XmlId1.charAt(j + 1);
                            documentID1 = documentID1 + aux;
                            aux = "";
                            break;
                        } else { //Se tiver "/>" significa que a tag está vazia então não guarda seu conteudo
                            aux = "";
                            break;
                        }
                    }
                }
                i = j + 2;
            } else {
                //se nao for inicio de tag, ou seja, pode ser espaço em branco apenas guarda o espaço, para dar formatação
                documentID1 = documentID1 + XmlId1.charAt(i);
                i++;
            }
        }

        //PARA O ARQUIVO MODIFICADO
        while (k < XmlId2.length()) { //Enquanto não chegar no fim do arquivo
            if (XmlId2.charAt(k) == '<') {
                for (l = k; l < XmlId2.length(); l++) { //Verifica os caracteres seguintes
                    if (XmlId2.charAt(l) != '/') { //Enquanto for != '/' guarda os caracteres concatenados
                        aux2 = aux2 + XmlId2.charAt(l);
                    } else {
                        if (XmlId2.charAt(l + 1) != '>') {
                            aux2 = aux2 + XmlId2.charAt(l);
                            while (XmlId2.charAt(l + 1) != '>') {
                                aux2 = aux2 + XmlId2.charAt(l + 1);
                                l++;
                            }
                            aux2 = aux2 + XmlId2.charAt(l + 1);
                            documentID2 = documentID2 + aux2;
                            aux2 = "";
                            break;
                        } else {
                            aux2 = "";
                            break;
                        }
                    }
                }
                k = l + 2;
            } else {
                documentID2 = documentID2 + XmlId2.charAt(k);
                k++;
            }
        }
    }

    /**
     * Tranforma um documento em uma String.
     *
     * @param doc
     * @return writer.toString()
     */
    public String toString(Document doc) {
        Element root = doc.getDocumentElement();
        StringWriter writer = new StringWriter();

        try {
            Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            serializer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerException ex) {
            //Mensagem de Exceção
        }
        return writer.toString();
    }
}

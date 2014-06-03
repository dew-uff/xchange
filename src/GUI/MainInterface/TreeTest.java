/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.MainInterface;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import gems.ic.uff.br.modelo.LcsXML;
import gems.ic.uff.br.modelo.XML;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.collections15.Transformer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Jorge
 */
public class TreeTest{

    private VisualizationViewer<String, String> vv;
    private JSplitPane aux;

    public TreeTest(String documentsContent1, String documentsContent2, String documentsContentFather) {
        /*XML xml1 = new XML(documentsContent1);
        XML xml2 = new XML(documentsContent2);
        XML xmlFather = new XML(documentsContentFather);

        LcsXML lcsXML = new LcsXML(xmlFather, xml1, xml2, true);

        vvs = new ArrayList<VisualizationViewer<String, String>>();*/
        
        System.out.println("\n\nInicio\n\n");
        System.out.println(documentsContent1);
        System.out.println("\n\nFinal\n\n");/*
        

        Forest<String, String> tree = buildTree(lcsXML.getDiffXML().toString());
        VisualizationViewer<String, String> vv = buildView(tree);*/
        
        Forest<String, String> tree = buildTree(documentsContent1);
        vv = buildView(tree);
    }
    
    public VisualizationViewer<String, String> getTree(){
        return vv;
    }

    /**
     * Construção da estrutura da árvore
     */
    private Forest<String, String> buildTree(String documentContent) {

        try {
            org.w3c.dom.Document doc = loadXMLFromString(documentContent);
            doc.getDocumentElement().normalize();

            Forest<String, String> tree = new DelegateTree<String, String>() {
            };
            tree.addVertex("/" + doc.getDocumentElement().getNodeName());

            NodeList beansList = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < beansList.getLength(); i++) {
                if (beansList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element bean = (Element) beansList.item(i);
                    tree.addEdge("rootToBean" + i, "/" + doc.getDocumentElement().getNodeName(), i + "/" + bean.getNodeName());

                    for (int j = 0; j < bean.getChildNodes().getLength(); j++) {
                        if (bean.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element tag = (Element) bean.getChildNodes().item(j);
                            tree.addEdge("bean" + i + "ToTag" + j, i + "/" + bean.getNodeName(), i + j + "/" + tag.getNodeName());
                            tree.addEdge("tag" + i + "ToValue" + j, i + j + "/" + tag.getNodeName(), i + j + "/" + tag.getTextContent());
                        }
                    }
                }
            }

            return tree;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DOMException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Constroi a visualização do gráfico para ser colocada na GUI
     *
     * @return O BasicVisualizationServer responsável pela visualização
     */
    private VisualizationViewer<String, String> buildView(Forest<String, String> tree) {
        //Cria um layout posicionando os vértices do gráfico igualmente espaçados em uma área circular
        Layout<String, String> layout = new TreeLayout<String, String>(tree, 100, 100);
        //Sub-classe de JPanel que pode ser colocada na GUI do Swing
        VisualizationViewer<String, String> vv = new VisualizationViewer<String, String>(layout);

        //Retorna a cor verde para cada vértice
        Transformer<String, Paint> elementPaint = new Transformer<String, Paint>() {
            public Paint transform(String s) {
                return Color.WHITE;
            }
        };

        //Define os rótulos de cada vértice e aresta
        Transformer<String, String> labeller = new Transformer<String, String>() {
            public String transform(String s) {
                if (!s.contains("/")) {
                    return null;
                }
                return s.substring(s.indexOf("/") + 1);
            }
        };

        //Define a visualização dos vértices e arestas
        vv.getRenderContext().setVertexFillPaintTransformer(elementPaint);
        vv.getRenderContext().setVertexLabelTransformer(labeller);
        vv.getRenderContext().setEdgeLabelTransformer(labeller);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.AUTO);

        //Adiciona o comportamento do mouse sobre o gráfico
        PluggableGraphMouse gm = new PluggableGraphMouse();
        gm.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON1_MASK));
        gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 0.9f));
        vv.setGraphMouse(gm);

        return vv;
    }

    private org.w3c.dom.Document loadXMLFromString(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }
}

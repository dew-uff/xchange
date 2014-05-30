/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.MainInterface;

import Rules.Condition;
import Rules.Rule;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.collections15.Transformer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Jorge
 */
public class TreeTest extends JFrame {

    private Forest<String, String> tree;

    public TreeTest(final Color vertexColor, String... documentsContent) {
        super("Tree Test");

        for (String documentContent : documentsContent) {
            buildTree(documentContent);
            this.getContentPane().add(buildView(vertexColor));
        }

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
    }

    /**
     * Construção do gráfico em si
     */
    private void buildTree(String documentContent) {
        /*

         //Adiciona as arestas, cujos nomes representam os campos
         t.addEdge("employee", "company", "1");
         t.addEdge("name", "1", "John");
         t.addEdge("cpf", "1", "000000");*/

        System.out.println("\n\nInicio\n\n");
        System.out.println(documentContent);

        try {
            org.w3c.dom.Document doc = loadXMLFromString(documentContent);
            doc.getDocumentElement().normalize();

            tree = new DelegateTree<String, String>() {
            };
            tree.addVertex(doc.getDocumentElement().getNodeName().trim().replace("\n", "").replace("\r", ""));

            NodeList ruleNodeList = doc.getDocumentElement().getChildNodes();
            System.out.println(doc.getDocumentElement().getNodeName().trim().replace("\n", "").replace("\r", ""));

            for (int i = 0; i < 2; i++) {
                if (ruleNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element mainElement = (Element) ruleNodeList.item(i);
                    tree.addEdge("" + i, doc.getDocumentElement().getNodeName(), mainElement.getNodeName());
                    //System.out.println("\t" + " " + mainElement.getNodeName().trim().replace("\n", "").replace("\r", ""));

                    for (int j = 0; j < mainElement.getChildNodes().getLength(); j++) {
                        if (mainElement.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
                            tree.addEdge(mainElement.getChildNodes().item(j).getNodeName(), mainElement.getNodeName(), mainElement.getChildNodes().item(j).getTextContent().trim().replace("\n", "").replace("\r", ""));
                            //System.out.println("\t\t" + " " + mainElement.getChildNodes().item(j).getTextContent().trim().replace("\n", "").replace("\r", ""));
                        }
                    }
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DOMException e) {
            e.printStackTrace();
        }

        System.out.println("\n\nFinal\n\n");
    }

    /**
     * Constroi a visualização do gráfico para ser colocada na GUI
     *
     * @return O BasicVisualizationServer responsável pela visualização
     */
    private VisualizationViewer<String, String> buildView(final Color vertexColor) {
        //Cria um layout posicionando os vértices do gráfico igualmente espaçados em uma área circular
        Layout<String, String> layout = new TreeLayout<String, String>(tree, 100, 100);
        //Sub-classe de JPanel que pode ser colocada na GUI do Swing
        VisualizationViewer<String, String> vv = new VisualizationViewer<String, String>(layout);
        vv.setPreferredSize(new Dimension(350, 350));

        //Retorna a cor verde para cada vértice
        Transformer<String, Paint> elementPaint = new Transformer<String, Paint>() {
            public Paint transform(String s) {
                return vertexColor;
            }
        };

        //Define a visualização dos vértices e arestas
        vv.getRenderContext().setVertexFillPaintTransformer(elementPaint);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

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

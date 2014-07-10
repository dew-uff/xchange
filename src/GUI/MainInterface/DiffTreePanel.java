/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.MainInterface;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import gems.ic.uff.br.modelo.LcsXML;
import gems.ic.uff.br.modelo.XML;
import java.awt.Color;
import java.awt.Paint;
import org.apache.commons.collections15.Transformer;
import org.w3c.dom.Node;

/**
 *
 * @author Jorge
 */
public class DiffTreePanel {

    public static VisualizationViewer<Node, String> build(String documentFrom, String documentTo) {
        XML xml1 = new XML(documentFrom);
        XML xml2 = new XML(documentTo);

        //Constroi o diff
        LcsXML lcsXML = new LcsXML(xml1, xml2);

        System.out.println("\n\nInicio\n\n");
        System.out.println(lcsXML.getDiffXML().toString());
        System.out.println("\n\nFinal\n\n");

        //Responável por colorir os nós da árvore
        Transformer<Node, Paint> nodePaint = new Transformer<Node, Paint>() {
            public Paint transform(Node node) {
                if (node.hasAttributes()) {
                    Node nodeSimilaridade = node.getAttributes().getNamedItem("diff:similarity");
                    if (nodeSimilaridade != null) {
                        if (nodeSimilaridade.getNodeValue().equals("1.0")) {
                            return Color.WHITE;
                        } else if (nodeSimilaridade.getNodeValue().equals("0.0")) {
                            Node nodeSide = node.getAttributes().getNamedItem("diff:side");
                            if (nodeSide != null && nodeSide.getNodeValue().equals("left")) {
                                return Color.RED;
                            } else {
                                return Color.GREEN;
                            }
                        } else {
                            int escalaTrucada = Math.round(255 * Float.parseFloat(nodeSimilaridade.getNodeValue()));
                            return new Color(escalaTrucada, escalaTrucada, escalaTrucada); // similaridade diferente de 0 e 1;
//                            return new Color((255 * Float.parseFloat(nodeSimilaridade.getNodeValue())),
//                                    (255 * Float.parseFloat(nodeSimilaridade.getNodeValue())),
//                                    (255 * Float.parseFloat(nodeSimilaridade.getNodeValue()))); // similaridade diferente de 0 e 1;
                        }
                    }
                }
                if (node.getNodeName().contains("diff:left")) {
                    return Color.RED;
                } else if (node.getNodeName().contains("diff:right")) {
                    return Color.GREEN;
                }
                if (node.getNodeType() == Node.TEXT_NODE) {
                    return Color.WHITE;
                }
                return Color.BLACK; // verificar 
            }
        };

        //Responável por "escrever" o texto de cada nó
        Transformer<Node, String> nodeLabeller = new Transformer<Node, String>() {
            public String transform(Node node) {
                if (node.getNodeType() != Node.TEXT_NODE) {
                    if (node.getNodeName().contains("diff:left") || node.getNodeName().contains("diff:right")) {
                        return node.getNodeValue();
                    } else {
                        return node.getNodeName();
                    }
                } else {
                    return node.getNodeValue();
                }
            }
        };

        //Constroi a árvore
        return new Tree(lcsXML, nodeLabeller, nodePaint).build();
    }
}

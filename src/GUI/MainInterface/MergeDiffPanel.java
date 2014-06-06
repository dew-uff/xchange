package GUI.MainInterface;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import gems.ic.uff.br.modelo.LcsXML;
import gems.ic.uff.br.modelo.XML;
import java.awt.Color;
import java.awt.Paint;
import org.apache.commons.collections15.Transformer;
import org.w3c.dom.Node;

/**Classe para construir o painel da aba "Merge Tree" de Syntatic Merge
 *
 * @author Jorge Moreira
 */
public class MergeDiffPanel {

    public static VisualizationViewer<Node, String> build(String documentsContent1, String documentsContent2, String documentsContentFather) {
        XML xml1 = new XML(documentsContent1);
        XML xml2 = new XML(documentsContent2);
        XML xmlFather = new XML(documentsContentFather);

        //Constroi o diff
        LcsXML lcsXML = new LcsXML(xmlFather, xml1, xml2, true);

        /*
        System.out.println("\n\nInicio\n\n");
        System.out.println(lcsXML.getDiffXML().toString());
        System.out.println("\n\nFinal\n\n");
        */

        //Responável por colorir os nós da árvore
        Transformer<Node, Paint> nodePaint = new Transformer<Node, Paint>() {
            public Paint transform(Node node) {
                if (node.hasAttributes()) {
                    Node nodeSimilaridade = node.getAttributes().getNamedItem("diff:similarity");
                    if (nodeSimilaridade != null) {
                        String similaridade = nodeSimilaridade.getNodeValue();
                        if (similaridade.contains("son1")) {
                            similaridade = similaridade.replace("son1:", "");
                            similaridade = similaridade.replace("son2: ", "");
                            String[] vetSim = similaridade.split(" ");
                            float sim1 = 1;
                            float sim2 = 0;
                            if (vetSim.length >= 2) {
                                sim1 = Float.parseFloat(vetSim[2]);
                                sim2 = Float.parseFloat(vetSim[3]);
                            }

                            similaridade = String.valueOf((sim1 + sim2) / 2);
                        }

                        if (similaridade.equals("1.0")) {
                            return Color.WHITE;
                        } else if (similaridade.equals("0.0")) {

                            Node nodeRelation = node.getAttributes().getNamedItem("diff:relation");
                            Node nodeSide = node.getAttributes().getNamedItem("diff:side");

                            if (nodeRelation != null) {
                                if (nodeRelation.getNodeValue().equals("son1")) {
                                    System.out.println("son1 !!!");
                                    return Color.RED;
                                }
                                if (nodeRelation.getNodeValue().equals("son2")) {
                                    System.out.println("son2 !!!");
                                    return Color.PINK;
                                }

                                if (nodeRelation.getNodeValue().equals("ancestral")) {
                                    System.out.println("ancestral !!!");
                                    return Color.GREEN;
                                }

                                if (nodeRelation.getNodeValue().equals("ancestralSon1")) {
                                    return Color.BLUE;
                                }

                                if (nodeRelation.getNodeValue().equals("ancestralSon2")) {
                                    return Color.YELLOW;
                                }
                                if (nodeRelation.getNodeValue().equals("son1Son2")) {
                                    return Color.ORANGE;
                                }
                            } else {
                                if (nodeSide != null && nodeSide.getNodeValue().equals("left")) {
                                    return Color.RED;
                                } else {

                                    if (nodeSide != null && nodeSide.getNodeValue().equals("right")) {
                                        return Color.GREEN;
                                    } else {
                                        return Color.MAGENTA;
                                    }
                                }
                            }
                        } else {
                            int escalaTrucada = Math.round(255 * Float.parseFloat(similaridade));
                            return new Color(escalaTrucada, escalaTrucada, escalaTrucada);
                        }
                    }
                }
                if (node.getNodeName().contains("diff:left")) {
                    return Color.RED;
                } else if (node.getNodeName().contains("diff:right")) {
                    return Color.GREEN;
                } else if (node.getNodeName().contains("diff:center")) {
                    return Color.YELLOW;
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
                    if (node.getNodeName().contains("diff:left") || node.getNodeName().contains("diff:right") || node.getNodeName().contains("diff:center")) {
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

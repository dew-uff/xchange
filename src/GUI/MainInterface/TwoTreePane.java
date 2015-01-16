package GUI.MainInterface;

import Phoenix.PhoenixWrapper;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Paint;
import javax.swing.JSplitPane;
import org.apache.commons.collections15.Transformer;
import org.w3c.dom.Node;

/**Classe para construir um painel com duas árvores lado a lado
 *
 * @author Jorge Moreira
 */
public class TwoTreePane {

    public static JSplitPane build(String document1Content, String document2Content) {
        JSplitPane jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jsplit.setOneTouchExpandable(true);

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        //Responável por colorir os nós da primeira árvore
        Transformer<Node, Paint> nodePaint0 = new Transformer<Node, Paint>() {
            public Paint transform(Node node) {
                return Color.GREEN;
            }
        };

        //Responável por colorir os nós da segunda árvore
        Transformer<Node, Paint> nodePaint1 = new Transformer<Node, Paint>() {
            public Paint transform(Node node) {
                return Color.RED;
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

        //Constroi as árvores
        VisualizationViewer<Node, String> left = new Tree(PhoenixWrapper.createDOMDocument(document1Content), nodeLabeller, nodePaint0).build();
        VisualizationViewer<Node, String> right = new Tree(PhoenixWrapper.createDOMDocument(document2Content), nodeLabeller, nodePaint1).build();

        //altera as propriedades de expansão e de ancoragem, e a posição
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        gridBag.setConstraints(right, constraints);
        gridBag.setConstraints(left, constraints);

        //define o componente de cada lado
        jsplit.setLeftComponent(left);
        jsplit.setRightComponent(right);
        jsplit.setContinuousLayout(true);

        //define a proporção do split pane
        jsplit.setResizeWeight(0.5);

        return jsplit;
    }

}

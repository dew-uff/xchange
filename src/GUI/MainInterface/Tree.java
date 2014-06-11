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
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import gems.ic.uff.br.modelo.LcsXML;
import gems.ic.uff.br.modelo.XML;
import java.awt.event.MouseEvent;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**Classe para construir visuzalização de árvores
 *
 * @author Jorge Moreira
 */
public class Tree {

    private Forest<Node, String> tree;
    private int tam = 1;
    private final Factory<String> edgeFactory = new Factory<String>() {

        public String create() {
            return "" + tam++;
        }
    };
    private final XML xml;
    private Transformer nodePaint;
    private Transformer nodeLabeller;

    public Tree(LcsXML lcsXML, Transformer nodeLabeller, Transformer nodePaint) {
        xml = new XML(lcsXML.getDiffXML().toString());
        this.nodeLabeller = nodeLabeller;
        this.nodePaint = nodePaint;
    }

    public Tree(XML xml, Transformer nodeLabeller, Transformer nodePaint) {
        this.xml = xml;
        this.nodeLabeller = nodeLabeller;
        this.nodePaint = nodePaint;
    }

    /** Constroi a estrutura e 
     * @return  **/
    public VisualizationViewer<Node, String> build() throws DOMException {
        tree = new DelegateTree<Node, String>() {
        };
        //Insere a raiz
        tree.addVertex(xml.getDocument().getDocumentElement());
        //Insere os nós filhos
        insertChildren(xml.getDocument().getDocumentElement());
        
        //-- Final da construção da estrutura --//
        //-- Início da construção do componente visual --//
        
        //Cria um layout posicionando os vértices do gráfico igualmente espaçados em uma área circular
        Layout<Node, String> layout = new TreeLayout<Node, String>(tree, 100, 100);
        //Sub-classe de JPanel que pode ser colocada na GUI do Swing
        VisualizationViewer<Node, String> vv = new VisualizationViewer<Node, String>(layout);

        //Define a visualização dos vértices e arestas
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexFillPaintTransformer(nodePaint);
        vv.getRenderContext().setVertexLabelTransformer(nodeLabeller);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);

        //Adiciona o comportamento do mouse sobre o gráfico
        PluggableGraphMouse gm = new PluggableGraphMouse();
        gm.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON1_MASK));
        gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 0.9f));
        vv.setGraphMouse(gm);

        return vv;
    }

    /** Insere todos os filhos de um nó **/
    private void insertChildren(Node item) {
        NodeList nl = item.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().contains("diff:value")) {
                Node noEsquerdo = nl.item(i).getAttributes().getNamedItem("diff:left");
                Node noDireito = nl.item(i).getAttributes().getNamedItem("diff:right");
                Node noCentral = nl.item(i).getAttributes().getNamedItem("diff:center");
                if (noEsquerdo != null) {
                    tree.addEdge(edgeFactory.create(), item, noEsquerdo);
                }
                if (noDireito != null) {
                    tree.addEdge(edgeFactory.create(), item, noDireito);
                }
                if (noCentral != null) {
                    tree.addEdge(edgeFactory.create(), item, noCentral);
                }
            } else {
                tree.addEdge(edgeFactory.create(), item, nl.item(i));
            }
            if (nl.item(i).hasChildNodes()) {
                insertChildren(nl.item(i));
            }
        }
    }

}

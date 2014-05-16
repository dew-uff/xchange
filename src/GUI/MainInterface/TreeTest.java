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
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Jorge
 */
public class TreeTest extends JFrame {

    private Forest<String, String> t;

    public TreeTest(final Color vertexColor) {
        super("Tree Test");
        
        buildTree();        
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().add(buildView(vertexColor));
        this.pack();
    }

    /**
     * Construção do gráfico em si
     */
    private void buildTree() {
        t = new DelegateTree<String, String>() {};
        
        //Adiciona os vértices (pais e filhos)
        t.addVertex("company");

        //Adiciona as arestas, cujos nomes representam os campos
        t.addEdge("employee", "company", "1");
        t.addEdge("name", "1", "John");
        t.addEdge("cpf", "1", "000000");
    }

    /**
     * Constroi a visualização do gráfico para ser colocada na GUI
     * @return O BasicVisualizationServer responsável pela visualização
     */
    private VisualizationViewer<String, String> buildView(final Color vertexColor) {
        //Cria um layout posicionando os vértices do gráfico igualmente espaçados em uma área circular
        Layout<String, String> layout = new TreeLayout<String, String>(t, 100, 100);
        //Sub-classe de JPanel que pode ser colocada na GUI do Swing
        VisualizationViewer<String, String> vv = new VisualizationViewer<String, String>(layout);
        vv.setPreferredSize(new Dimension(350,350));
        
        //Retorna a cor verde para cada vértice
        Transformer<String,Paint> elementPaint = new Transformer<String, Paint>(){
            public Paint transform(String s){
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
}
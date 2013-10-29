package GUI.MainInterface;


import Documents.Documents;
import GUI.Layout.LayoutConstraints;
import Manager.Manager;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

/**
 *  Cria a aba onde será exibido o resultado da inferência entre os documentos
 * @author Marcio Tadeu de Oliveira Júnior
 */
public class ResultsTab extends JPanel{
    
    private JTextPane resultsTextPane;//TextPane para mostra de resultados
    private JScrollPane scpResults;//ScrollPane dos resultados
    private InferenceFileChooser inferenceFileChooser;//Seletor de documentos para a a inferencia

    /**
     * Construtor da classe.
     */
    public ResultsTab(){
        super();     
        
        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();        
        this.setLayout(gridBag);
        
        //cria a area de texto com os resultados da inferencia
        resultsTextPane = new JTextPane();
        
        resultsTextPane.setEditable(false);
        
        scpResults = new JScrollPane(resultsTextPane);
        
        //cria a lista de combobox para selecionar os arquivos da inferencia
        inferenceFileChooser = new InferenceFileChooser(resultsTextPane);
                           
        
        JSplitPane jsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,inferenceFileChooser,
                scpResults);
        jsPane.setOneTouchExpandable(true);
        
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor=GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(jsPane, constraints);
                
        this.add(jsPane);
        jsPane.setVisible(true);
        jsPane.setResizeWeight(0.01); 
        
    }
    
    /**
     * Atualiza os componentes gráficos de acordo com a lista de documentos passada como parâmetro, e atualiza o gerenciador de regras
     * @param documents
     * @param manager 
     */
    public void refresh(Documents documents,Manager manager){//atualiza a tela de resultados
       resultsTextPane.setText("");
       inferenceFileChooser.refresh(documents,manager);
    }
    
    /**
     * 
     * @return O JTextPane onde serão escritos os resultados 
     */
    public JTextPane getResultsTextPane(){//retorna o TextPane de resultados
        return resultsTextPane;
    }
    
    /**
     * @return O painel de seleção de documentos para a inferência
     */
    public InferenceFileChooser getInferenceFileChooser(){//retorna o seletor de documentos para a inferencia
        return inferenceFileChooser;
    }
}

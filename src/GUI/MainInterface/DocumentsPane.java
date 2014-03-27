package GUI.MainInterface;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

/**
 *
 * @author Marcio Tadeu de Oliveira Júnior
 * 
 * Esta classe tem como objetivo criar o painel de visualização dos documentos
 */
public class DocumentsPane extends JSplitPane{
    private JTextArea left,right;
    
    /**
     * Construtor da classe.
     */
    public DocumentsPane(){
        super(JSplitPane.HORIZONTAL_SPLIT);//define a separação do SplitPane como lado-a-lado
        this.setOneTouchExpandable(true);
        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints(); 
        
        //cria duas "JTextAreas", onde cada uma terá o conteúdo dos documentos XML
        this.left = new JTextArea();
        this.right = new JTextArea();
        
        //cria Scroll Panes, já que a JTextArea não pode ser colocada diretamente no SplitPane
        JScrollPane leftSP,rightSP;
        leftSP = new JScrollPane(left);
        rightSP = new JScrollPane(right);
        
        leftSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        rightSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        //altera as propriedades de expansão e de ancoragem, e a posição
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor=GridBagConstraints.SOUTHEAST;
        gridBag.setConstraints(rightSP,constraints);
        gridBag.setConstraints(leftSP,constraints);
        
        //torna as "Text Areas" visiveis
        this.left.setVisible(true);
        this.right.setVisible(true);
        
        this.setLeftComponent(leftSP);//adiciona um Scroll Pane no interior esquerdo do SplitPane
        this.setRightComponent(rightSP);//adiciona um Scroll Pane no interior direito do SplitPane
        this.setContinuousLayout(true);
        
        //desabilita a edição pelo usuario 
        this.left.setEditable(false);
        this.right.setEditable(false);
          
        this.setResizeWeight(0.5);//define que a proporção do split pane que cada lado terá é de 50%, e que ele vai se redimensionar na proporção atual
        this.setVisible(true);//torna o splitpane visivel
        
    }
    
    /**
     * Modifica o texto da esquerda pelo parametro s
     * @param s 
     */
    void setLeftText(String s){//escreve o texto relativo ao documento selecionado no combobox esquerdo
        this.left.setText(s);
        this.left.setCaretPosition(0);
    }
    
    /**
     * Modifica o texto da direita pelo parametro s
     * @param s 
     */
    void setRightText(String s){//escreve o texto relativo ao documento selecionado no combobox direito
        this.right.setText(s);
        this.right.setCaretPosition(0);
    }
    
    
    /**
     * Redefine a posição dos SplitPane's de acordo com a quantidade de documentos abertos
     * @param docSize 
    */
    public void resizeSplitPane(int docSize){
        /*double dimJFrame = MainInterface.getWidthJFrame(); // Obtém o Tamanho Atual da Janela do XChange
        
        Toolkit tk = Toolkit.getDefaultToolkit();//objeto para pegar as dimensoes da tela
        Dimension dimension = tk.getDefaultToolkit().getScreenSize();//funçao para pegar as dimensoes da tela
        double dimPC = ((dimension.getSize().getWidth())); // Obtem a Largura da Tela do Computador
        double inicial = (7*(int)dimPC/8); // Tamanho da Tela Inicialmente
        
        double diferenca = ((dimPC-inicial)/3); // Diferença da Tela do Computador e da Tela Inicial do XChange divido por 3
        int divider = (int)((dimJFrame-diferenca)/3)+1; // Calcula a localização do SplitPane Geral
        
        if(docSize==1 || docSize>=2){
            this.setDividerLocation(divider);
        }*/
    }
}

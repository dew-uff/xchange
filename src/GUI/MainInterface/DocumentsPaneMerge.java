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
 * author MARCIO e CARLOS
 * 
 * Esta classe tem como objetivo criar o painel de visualização
 */
public class DocumentsPaneMerge extends JSplitPane{
    private JTextArea left,center, right;
    private JSplitPane versoeSP;
    private JScrollPane leftSP, centerSP, rightSP;
    
    /**
     * Construtor da classe.
     */
    public DocumentsPaneMerge(){
        super(JSplitPane.HORIZONTAL_SPLIT);//define a separação do SplitPane como lado-a-lado
        this.setOneTouchExpandable(true);
        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints(); 
        
        //cria três "JTextAreas", onde cada uma terá o conteúdo dos documentos XML
        this.left = new JTextArea();
        this.center = new JTextArea();
        this.right = new JTextArea();
        
        //cria Scroll Panes, já que a JTextArea não pode ser colocada diretamente no SplitPane
        this.leftSP = new JScrollPane(left);
        this.centerSP = new JScrollPane(center);
        this.rightSP = new JScrollPane(right);
                
        this.leftSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.leftSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        this.centerSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.centerSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        this.rightSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.rightSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        //altera as propriedades de expansão e de ancoragem, e a posição
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor=GridBagConstraints.SOUTHEAST;
        gridBag.setConstraints(leftSP,constraints);
        gridBag.setConstraints(rightSP,constraints);
        gridBag.setConstraints(centerSP,constraints);
        
        //torna as "Text Areas" visiveis
        this.left.setVisible(true);
        this.center.setVisible(true);
        this.right.setVisible(true);
        
        
        this.versoeSP = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);//define o split para as versões do documento
        this.versoeSP.setOneTouchExpandable(true);
        versoeSP.setLeftComponent(centerSP);//adiciona um Scroll Pane no interior esquerdo do SplitPane
        versoeSP.setRightComponent(rightSP);//adiciona um Scroll Pane no interior direito do SplitPane
        this.versoeSP.setContinuousLayout(true);
        this.versoeSP.setResizeWeight(0.498);//define que a proporção do split pane que cada lado te´ra é de 50%, e que ele vai se redimensionar na proporção atual

                
        this.setLeftComponent(leftSP);//adiciona um Scroll Pane no interior esquerdo do SplitPane
        this.setRightComponent(versoeSP);//adiciona um Scroll Pane no interior direito do SplitPane
        this.setContinuousLayout(true);
        
        //desabilita a edição pelo usuario 
        this.left.setEditable(false);
        this.center.setEditable(false);
        this.right.setEditable(false);
        
        this.setResizeWeight(0.336);//define que a proporção do split pane que cada lado terá é de 33%, e que ele vai se redimensionar na proporção atual
        this.setVisible(true);//torna o splitpane visivel
    }
        
    /**
     * Modifica o texto da esquerda pelo parametro s
     * @param s 
     */
    public void setLeftText(String s){//escreve o texto relativo ao documento selecionado no combobox centro
        this.left.setText(s);
        this.left.setCaretPosition(0);
    }
    
    /**
     * Modifica o texto do centro pelo parametro s
     * @param s 
     */
    public void setCenterText(String s){//escreve o texto relativo ao documento selecionado no combobox esquerdo
        this.center.setText(s);
        this.center.setCaretPosition(0);
    }
    
    /**
     * Modifica o texto da direita pelo parametro s
     * @param s 
     */
    public void setRightText(String s){//escreve o texto relativo ao documento selecionado no combobox direito
        this.right.setText(s);
        this.right.setCaretPosition(0);
    }
    
    /**
     * Redefine a posição dos SplitPane's de acordo com a quantidade de documentos abertos
     * @param docSize 
     */
    public void resizeSplitPane(int docSize){
        double dimJFrame = MainInterface.getWidthJFrame(); // Obtém o Tamanho Atual da Janela do XChange
        
        Toolkit tk = Toolkit.getDefaultToolkit();//objeto para pegar as dimensoes da tela
        Dimension dimension = tk.getDefaultToolkit().getScreenSize();//funçao para pegar as dimensoes da tela
        double dimPC = ((dimension.getSize().getWidth())); // Obtem a Largura da Tela do Computador
        double inicial = (7*(int)dimPC/8); // Tamanho da Tela Inicialmente

        double diferenca = ((dimPC-inicial)/3); // Diferença da Tela do Computador e da Tela Inicial do XChange divido por 3
        int thisSP = (int)((dimJFrame-diferenca)/3); // Calcula a localização do SplitPane Geral
        
        int versoesSP = (int)(thisSP-6); // Calcula a localização do SplitPane das Versoes XML
        
        if(docSize==1){
            this.setDividerLocation(thisSP); // Define a Localização do SplitPane Geral
        }else if(docSize==2){
            this.setDividerLocation(thisSP); // Define a Localização do SplitPane Geral
            this.versoeSP.setDividerLocation(versoesSP); // Define a Localização do SplitPane das Versoes XML
        }else if(docSize==3){
            this.setDividerLocation(thisSP); // Define a Localização do SplitPane Geral
        }
    }
}

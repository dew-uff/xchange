package GUI.MainInterface;


import Documents.Documents;
import GUI.Layout.LayoutConstraints;
import Manager.Manager;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 *
 * @author CARLOS
 * 
 * Esta classe tem por objetivo criar o conteudo da aba "Documents" do Merge, com suas 
 * areas de texto e os ComboBoxes que servirão para selecionar qual arquivo XML 
 * será mostrado em cada Text Area definidas em DocumentsPane
 */
public class DocumentsTabMerge extends JPanel implements ActionListener{
    private DocumentsPaneMerge docPane;
    private JComboBox leftCB, centerCB, rightCB;
    private Documents documents;
    private JPanel jp;
    
    /*
     * Construtor da classe
     */
    public DocumentsTabMerge(){
        super();     
        
        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridBag);
        
        //cria os Comboxes de seleção do documento XML que será mostrado        
        leftCB = new JComboBox();
        centerCB = new JComboBox();
        rightCB = new JComboBox();
        
        //paineis para posicionamento dos comboboxes
        JPanel leftPane = new JPanel();
        JPanel centerPane = new JPanel();
        JPanel rightPane = new JPanel(); 
        
        //Adiciona os ComboBoxes a seus paineis
        leftPane.add(leftCB);
        centerPane.add(centerCB);
        rightPane.add(rightCB);
        
        //cria paineis que permitem aos comboboxes alongarem-se na horizontal
        GridBagLayout leftGBL = new GridBagLayout();
        leftPane.setLayout(leftGBL);
        GridBagLayout centerGBL = new GridBagLayout();
        centerPane.setLayout(centerGBL);
        GridBagLayout rightGBL = new GridBagLayout();
        rightPane.setLayout(rightGBL);
        
        //layout dos paineis que contem os comboxes
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        
        //aplica o layout que alonga os comboboxes
        leftGBL.setConstraints(leftCB, constraints);
        centerGBL.setConstraints(centerCB, constraints);
        rightGBL.setConstraints(rightCB, constraints);
        
        //cria o painel superior com os paineis com comboboxes
        JPanel cbPanel = new JPanel();
        cbPanel.setLayout(new GridLayout(1, 3));
        
        //Adiciona os paineis dos ComboBoxes à interface gráfica
        cbPanel.add(leftPane);
        cbPanel.add(centerPane);
        cbPanel.add(rightPane);
        
        /*Define o layout e a posição dos ComboBoxes*/
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(2, 2, 0, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        gridBag.setConstraints(cbPanel, constraints);
        
        //torna os ComboBoxes visíveis
        leftCB.setVisible(true);
        centerCB.setVisible(true);
        rightCB.setVisible(true);

        //Adiciona os ComboBoxes à interface gráfica
        this.add(cbPanel);
 
        docPane = new DocumentsPaneMerge();
        
        LayoutConstraints.setConstraints(constraints,0,1,2,1,1,100);
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor=GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(docPane,constraints);
        
        this.add(docPane);
    }

    /**
     * Modifica o tetxo da Text Area da direita para o valor no parâmetro s
     * @param s
     */
    void setLeftText(String s){
        this.docPane.setLeftText(s);
    }
    
    /**
     * Modifica o tetxo da Text Area do cento para o valor no parâmetro s
     * @param s 
     */
    void setCenterText(String s){
        this.docPane.setCenterText(s);
    }
    
    /**
     * Modifica o tetxo da Text Area da esquerda para o valor no parâmetro s
     * @param s 
     */
    void setRightText(String s){
        this.docPane.setRightText(s);
    }
    
    /**
     * Atualiza os componentes gráficos de acordo com a lista de documentos passada como parâmetro
     * @param documents 
     */
    public void refresh(Documents documents){
        
        //Variaveis que indicam o indice do XML aberto em cada area de texto
        int leftCBIndex = leftCB.getSelectedIndex();
        int baseCBIndex = centerCB.getSelectedIndex();
        int rigthCBIndex = rightCB.getSelectedIndex();        


        //remove os eventos relacionados aos comboboxes
        leftCB.removeActionListener(this);
        centerCB.removeActionListener(this);
        rightCB.removeActionListener(this);   
  
        
        //limpa o conteudo dos comboboxes
        leftCB.removeAllItems();
        centerCB.removeAllItems();
        rightCB.removeAllItems();        

        //reescreve o conteudo nos comboboxes
        for(String s:documents.docsIds()){
           leftCB.addItem(s);
           centerCB.addItem(s);
           rightCB.addItem(s);
        }
        
        if(documents.getSize() == 0){//se não há documentos limpa as areas de texto
            setLeftText("");
            setCenterText("");
            setRightText("");                          
        }
        else if(documents.getSize() == 1){//se há apenas um documento só o mostra no lado esquerdo
            leftCB.setSelectedIndex(0);
            this.setLeftText(documents.getContent(leftCB.getSelectedIndex()));
            leftCB.addItem("");
            centerCB.addItem("");
            centerCB.setSelectedIndex(1);
            rightCB.addItem("");
            rightCB.setSelectedIndex(1);
        }
        else if(documents.getSize() == 2){//se há dois documentos mostra o primeiro à esquerda e o segundo no cento
            leftCB.setSelectedIndex(0);  
            centerCB.setSelectedIndex(1);            
            this.setLeftText(documents.getContent(leftCB.getSelectedIndex()));
            this.setCenterText(documents.getContent(centerCB.getSelectedIndex()));
            leftCB.addItem("");
            centerCB.addItem("");
            rightCB.addItem("");
            rightCB.setSelectedIndex(2);
        }
        else if(documents.getSize()== 3){//se há três documentos mostra o primeiro à esquerda e o segundo no cento e o terceiro à direita
            leftCB.setSelectedIndex(0);   
            centerCB.setSelectedIndex(1);
            rightCB.setSelectedIndex(2);            
                     
            this.setLeftText(documents.getContent(leftCB.getSelectedIndex()));
            this.setCenterText(documents.getContent(centerCB.getSelectedIndex())); 
            this.setRightText(documents.getContent(rightCB.getSelectedIndex()));
        }        
        
        //adiciona novamente os eventos
        leftCB.addActionListener(this);
        centerCB.addActionListener(this);
        rightCB.addActionListener(this);
        
        //Redefine a posição dos SplitPane's de acordo com a quantidade de documentos abertos
        this.docPane.resizeSplitPane(documents.getSize());
        
        //define a nova lista de documentos
        this.documents=documents;
    }
    
    /**
     * Trata os eventos da classe
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(leftCB)){//muda o texto esquerdo de acordo com o combobox esquerdo           
            this.setLeftText(documents.getContent(leftCB.getSelectedIndex()));
        }
        if(e.getSource().equals(centerCB)){//muda o texto do centro de acordo com o combobox do centro
            this.setCenterText(documents.getContent(centerCB.getSelectedIndex()));
        }
        if(e.getSource().equals(rightCB)){//muda o texto direito de acordo com o combobox direito
            this.setRightText(documents.getContent(rightCB.getSelectedIndex()));
        }
    }
    
    /** 
     * @return Uma String com o caminho do documento da esquerda
     */
    public String getPathDocLeft(){
        int leftCBIndex = leftCB.getSelectedIndex();
        return documents.getDocument(leftCBIndex+1).getPathWay();
    }
    
    /**
     * @return Uma String com o caminho do documento do meio
     */
    public String getPathDocCenter(){
        int centerCBIndex = centerCB.getSelectedIndex();
        return documents.getDocument(centerCBIndex+1).getPathWay();    
    }
    
    /**
     * @return Uma String com o caminho do documento da direita
     */
    public String getPathDocRight(){
        int rightCBIndex = rightCB.getSelectedIndex();
        return documents.getDocument(rightCBIndex+1).getPathWay();    
    }
}

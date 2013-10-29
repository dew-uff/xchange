/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.MainInterface;

import Documents.Documents;
import GUI.Layout.LayoutConstraints;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import gems.ic.uff.br.newView.DiffTreePanel;

/**
 * @author Carlos Roberto Carvalho Oliveira
 * @author Marcio Tadeu de Oliveira Júnior
 * 
 * Esta classe tem por objetivo criar o conteudo da aba "Sintatic Diff Tree", com a
 * árvore de Diff Sintático do Phoenix e os ComboBoxes que servirão para selecionar quais arquivos XML
 * serão comparados e exibidados na árvore.
 */
public class SintaticDiffTree extends JPanel implements ActionListener{
    
    private JPanel diffTreePane;
    private JComboBox leftCB, rightCB;
    private Documents documents;
    private GridBagLayout gridBag = new GridBagLayout();
    private GridBagConstraints constraints = new GridBagConstraints();
    
    /**
     * Construtor da classe.
     *
     * @param documents
     */
    public SintaticDiffTree(Documents doc){
        super();
        this.documents = doc;
        
        //declara objetos de controle do layout
        this.setLayout(gridBag);

        //cria os Comboxes de seleção do documento XML que será mostrado
        leftCB = new JComboBox();
        rightCB = new JComboBox();

        //paineis para posicionamento dos comboboxes
        JPanel leftPane = new JPanel();
        JPanel rightPane = new JPanel();

        //Adiciona os ComboBoxes a seus paineis
        leftPane.add(leftCB);
        rightPane.add(rightCB);

        //cria paineis que permitem aos comboboxes alongarem-se na horizontal
        GridBagLayout leftGBL = new GridBagLayout();
        leftPane.setLayout(leftGBL);
        GridBagLayout rightGBL = new GridBagLayout();
        rightPane.setLayout(rightGBL);

        //layout dos paineis que contem os comboxes
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        //aplica o layout que alonga os comboboxes
        leftGBL.setConstraints(leftCB, constraints);
        rightGBL.setConstraints(rightCB, constraints);

        //cria o painel superior com os paineis com comboboxes
        JPanel cbPanel = new JPanel();
        cbPanel.setLayout(new GridLayout(1, 2));

        //Adiciona os paineis dos ComboBoxes à interface gráfica
        cbPanel.add(leftPane);
        cbPanel.add(rightPane);

        /*Define o layout e a posição dos ComboBoxes*/
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(2, 2, 0, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        gridBag.setConstraints(cbPanel, constraints);

        //Adiciona os ComboBoxes à interface gráfica
        this.add(cbPanel);
        
        diffTreePane = new JPanel();
        /*Define o layout e a posição dos ComboBoxes*/
        LayoutConstraints.setConstraints(constraints, 0, 1, 100, 100, 100, 100);
        constraints.insets = new Insets(2, 2, 0, 2);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(diffTreePane, constraints);
        this.add(diffTreePane);
    }
    
    /**
     * Atualiza os componentes graficos de acordo com a lista de documentos passada como parametros
     * @param documents 
     */
    public void refresh(Documents documents) {

        //Variaveis que indicam o indice do XML aberto em cada area de texto
        int leftCBIndex = leftCB.getSelectedIndex();
        int rigthCBIndex = rightCB.getSelectedIndex();

        //remove os eventos relacionados aos comboboxes
        leftCB.removeActionListener(this);
        rightCB.removeActionListener(this);

        //limpa o conteudo dos comboboxes
        leftCB.removeAllItems();
        rightCB.removeAllItems();

        //reescreve o conteudo nos comboboxes
        for (String s : documents.docsIds()) {
            leftCB.addItem(s);
            rightCB.addItem(s);
        }
        
        if(documents.getSize()==2){
            leftCB.setSelectedIndex(0);
            rightCB.setSelectedIndex(1);
        }else if(documents.getSize()>=2){
            leftCB.setSelectedIndex(leftCBIndex);
            rightCB.setSelectedIndex(rigthCBIndex);
        }else if(documents.getSize()==1){
            leftCB.setSelectedIndex(0);
            rightCB.setSelectedIndex(0);
        }
        
        //adiciona novamente os eventos
        leftCB.addActionListener(this);
        rightCB.addActionListener(this);

        //define a nova lista de documentos da classe
        this.documents = documents;
        this.revalidate();
    }

    /**
     * Trata os eventos que ocorrem em objetos-atributos do objeto SintaticDiffTree
     * @param e 
     */
    public void actionPerformed(ActionEvent e) {   
        plotTree();
    }

    /**
     * Calcula o Diff e renderiza a árvore de Diff
     */
    public void plotTree() {
        if(documents.getSize()>=2){
            //indica a posição e o layout
            LayoutConstraints.setConstraints(constraints,0,1,100,100,100,100);
            constraints.insets=new Insets(2,2,2,2);
            constraints.fill=GridBagConstraints.BOTH;
            constraints.anchor=GridBagConstraints.NORTHWEST;
            
            this.remove(diffTreePane);
            diffTreePane.setLayout(gridBag);
            diffTreePane = new DiffTreePanel(documents.getContent(leftCB.getSelectedIndex()),documents.getContent(rightCB.getSelectedIndex()));         
            diffTreePane.setVisible(true);
            gridBag.setConstraints(diffTreePane, constraints);
            this.add(diffTreePane);
            diffTreePane.revalidate();
            this.revalidate();
        }
    }
}

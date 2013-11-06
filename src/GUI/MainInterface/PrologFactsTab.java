package GUI.MainInterface;

import Documents.Documents;
import GUI.Layout.LayoutConstraints;
import Manager.Manager;
import Translate.Similarity;
import Translate.TranslateModule;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 *
 * @author Marcio Tadeu de Oliveira Júnior
 *
 * Esta classe tem por objetivo criar o conteudo da aba "Prolog Facts", com suas
 * areas de texto e os ComboBoxes que servirão para selecionar qual fato prolog
 * dedeterminado arquivo XML será mostrado em cada Text Area definidas em
 * DocumentsPane
 */
public class PrologFactsTab extends JPanel implements ActionListener {

    private DocumentsPane docPane;
    private JComboBox leftCB, rightCB;
    private Documents documents;
    private Manager manager;
    private MainInterface main;

    /**
     * Construtor da classe.
     *
     * @param manager
     */
    public PrologFactsTab(Manager manager, MainInterface main) {
        super();
        this.main = main;

        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
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


        //torna os ComboBoxes visíveis
        leftCB.setVisible(true);
        rightCB.setVisible(true);

        //Adiciona os ComboBoxes à interface gráfica
        this.add(cbPanel);

        /*cria um DocumentsPane, que contem as duas TextAreas onde estará o
         * conteúdo dos documentos XML
         */
        docPane = new DocumentsPane();

        LayoutConstraints.setConstraints(constraints, 0, 1, 2, 1, 1, 100);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(docPane, constraints);
        docPane.setVisible(true);

        this.add(docPane);
        this.manager = manager;
    }

    /**
     * modifica o texto da TextArea esquerda para o texto no parametro s
     *
     * @param s
     */
    void setLeftText(String s) {
        this.docPane.setLeftText(s);
    }

    /**
     * modifica o texto da TextArea direita para o texto no parametro s
     *
     * @param s
     */
    void setRightText(String s) {
        this.docPane.setRightText(s);
    }

    /**
     * Atualiza os componentes graficos de acordo com a lista de documentos
     * passada como parametros
     *
     * @param documents
     */
    public void refresh(Documents documents) {

        //Variaveis que indicam o indice do XML aberto em cada area de texto
        int leftCBIndex = leftCB.getSelectedIndex();
        int rightCBIndex = rightCB.getSelectedIndex();

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
        if (documents.docsIds().size() == 1) {
            rightCB.addItem("");
            rightCB.setSelectedIndex(1);
        }

        if (documents.getSize() < 2) {//se não há documentos limpa as areas de texto
            setRightText("");
            setLeftText("");
        } else if (documents.getSize() >= 2) {//se há mais de dois documentos permanecem os que estavam em exibição
            if (!this.main.getSimilarity()) {
                if(this.manager.getContextKey()!=null && this.manager.getContextKey().size()>=2){
                    this.setLeftText(this.manager.getContextKey().get(0).getFacts());
                    this.setRightText(this.manager.getContextKey().get(1).getFacts());
                }
            } else {
                showSimilarityFacts(leftCBIndex, rightCBIndex);
                rightCBIndex = rightCB.getSelectedIndex();
                leftCBIndex = leftCB.getSelectedIndex();
            }
            leftCB.setSelectedIndex(leftCBIndex);
            rightCB.setSelectedIndex(rightCBIndex);
        }

        //adiciona novamente os eventos
        leftCB.addActionListener(this);
        rightCB.addActionListener(this);

        //define a nova lista de documentos da classe
        this.documents = documents;
    }

    /**
     * Trata os eventos da classe.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(leftCB)) {//muda o texto esquerdo de acordo com o combobox esquerdo
            if (!this.main.getSimilarity()) {
                this.setLeftText(this.manager.getContextKey().get(this.leftCB.getSelectedIndex()).getFacts());
            } else {
                showSimilarityFacts(leftCB.getSelectedIndex(), rightCB.getSelectedIndex());
            }
        }
        if (e.getSource().equals(rightCB)) {//muda o texto direito de acordo com o combobox direito
            if (rightCB.getSelectedIndex() < rightCB.getItemCount()) {
                if (!this.main.getSimilarity()) {
                    this.setRightText(this.manager.getContextKey().get(this.rightCB.getSelectedIndex()).getFacts());
                } else {
                    showSimilarityFacts(leftCB.getSelectedIndex(), rightCB.getSelectedIndex());
                }
            }
        }
    }

    private void showSimilarityFacts(int leftCBIndex, int rightCBIndex) {
        TranslateModule.reset();
        Similarity sim = new Similarity(this.documents.getPathWays().get(leftCBIndex));
        if (rightCBIndex == leftCBIndex) {
            setLeftText("The documents are the same");
            setRightText("The documents are the same");
        } else {


            if (rightCBIndex > leftCBIndex) {
                sim.documentsWithIDs(documents.getFile(leftCBIndex).getAbsolutePath(), documents.getFile(rightCBIndex).getAbsolutePath(), manager.getSimilarityRate());
            } else if (leftCBIndex > rightCBIndex) {
                sim.documentsWithIDs(documents.getFile(rightCBIndex).getAbsolutePath(), documents.getFile(leftCBIndex).getAbsolutePath(), manager.getSimilarityRate());
                leftCB.setSelectedIndex(rightCBIndex);
                rightCB.setSelectedIndex(leftCBIndex);
            }
            sim.translateFactsID(new File("temp1.xml"));
            setLeftText(sim.getFacts());

            sim.translateFactsID(new File("temp2.xml"));
            setRightText(sim.getFacts());


        }
    }
    
    public void setLeftCB(int i){
        this.leftCB.setSelectedIndex(i);
    }
    
    public void setRightCB(int i){
        this.rightCB.setSelectedIndex(i);
    }
    
    public boolean alreadySet(){
        return (this.leftCB.getSelectedIndex()==-1|| this.rightCB.getSelectedIndex()==-1);
    }
}

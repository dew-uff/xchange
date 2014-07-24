/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.MainInterface;

import Documents.Documents;
import GUI.Layout.LayoutConstraints;
import Manager.Manager;
import Translate.TranslateModule;
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
 * @author Fernando
 */
public class PrologFactsMergeTab extends JPanel implements ActionListener {

    private DocumentsPaneMerge docPaneMerge;
    private JComboBox leftCB, rightCB, centerCB;
    private Documents documents;
    private Manager manager;
    private MainInterface main;

    /**
     * Construtor da classe.
     *
     * @param manager
     */
    public PrologFactsMergeTab(Manager manager, MainInterface main) {
        super();
        this.main = main;

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

        /*cria um DocumentsPane, que contem as duas TextAreas onde estará o
         * conteúdo dos documentos XML
         */
        docPaneMerge = new DocumentsPaneMerge();

        LayoutConstraints.setConstraints(constraints, 0, 1, 2, 1, 1, 100);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(docPaneMerge, constraints);
        docPaneMerge.setVisible(true);

        this.add(docPaneMerge);
        this.manager = manager;
    }

    /**
     * modifica o texto da TextArea esquerda para o texto no parametro s
     *
     * @param s
     */
    void setLeftText(String s) {
        this.docPaneMerge.setLeftText(s);
    }

    /**
     * modifica o texto da TextArea direita para o texto no parametro s
     *
     * @param s
     */
    void setCenterText(String s) {
        this.docPaneMerge.setCenterText(s);
    }

    /**
     * modifica o texto da TextArea direita para o texto no parametro s
     *
     * @param s
     */
    void setRightText(String s) {
        this.docPaneMerge.setRightText(s);
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
        int centerCBIndex = centerCB.getSelectedIndex();
        int rightCBIndex = rightCB.getSelectedIndex();

        //remove os eventos relacionados aos comboboxes
        leftCB.removeActionListener(this);
        centerCB.removeActionListener(this);
        rightCB.removeActionListener(this);

        //limpa o conteudo dos comboboxes
        leftCB.removeAllItems();
        centerCB.removeAllItems();
        rightCB.removeAllItems();

        //reescreve o conteudo nos comboboxes
        for (String s : documents.docsIds()) {
            leftCB.addItem(s);
            centerCB.addItem(s);
            rightCB.addItem(s);
        }

        if (documents.getSize() < 3) {//se não há documentos limpa as areas de texto
            setRightText("");
            setCenterText("");
            setLeftText("");
        } else if (documents.getSize() >= 3) {//se há mais de dois documentos permanecem os que estavam em exibição
            if (leftCBIndex == -1 && rightCBIndex == -1 && centerCBIndex == -1) {//Quando se está em algum modulo (ex: Syntatic Diff) e troca para outro modulo (ex: Semantic Diff), ou vice e versa, esses valores ficam igual a -1. O mesmo ocorre quando se abre um projeto
                leftCBIndex = 0;
                centerCBIndex = 1;
                rightCBIndex = 2;
            }
            if (!this.main.getSimilarity()) {
                if (this.manager.getContextKey() != null && this.manager.getContextKey().size() >= 3) {
                    this.setLeftText(this.manager.getContextKey().get(0).getFacts());
                    this.setCenterText(this.manager.getContextKey().get(1).getFacts());
                    this.setRightText(this.manager.getContextKey().get(2).getFacts());
                }
            } else {
                showSimilarityFacts(leftCBIndex, rightCBIndex);
                rightCBIndex = rightCB.getSelectedIndex();
                leftCBIndex = leftCB.getSelectedIndex();
            }
            leftCB.setSelectedIndex(leftCBIndex);
            centerCB.setSelectedIndex(centerCBIndex);
            rightCB.setSelectedIndex(rightCBIndex);
        }

        //adiciona novamente os eventos
        leftCB.addActionListener(this);
        centerCB.addActionListener(this);
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
        if (e.getSource().equals(centerCB)){
            if (!this.main.getSimilarity()) {
                this.setCenterText(this.manager.getContextKey().get(this.centerCB.getSelectedIndex()).getFacts());
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
        if (rightCBIndex == leftCBIndex) {
            setLeftText("The documents are the same");
            setRightText("The documents are the same");
        } else {
            if (leftCBIndex > rightCBIndex) {
                leftCB.setSelectedIndex(rightCBIndex);
                rightCB.setSelectedIndex(leftCBIndex);
            }
            setLeftText(manager.getSimilarityFacts().get(leftCBIndex).get(documents.getSize() - rightCBIndex - 1).get(0));
            setRightText(manager.getSimilarityFacts().get(leftCBIndex).get(documents.getSize() - rightCBIndex - 1).get(1));
        }
    }

    public void setLeftCB(int i) {
        this.leftCB.setSelectedIndex(i);
    }

    public void setRightCB(int i) {
        this.rightCB.setSelectedIndex(i);
    }

    public void setCenterCB(int i) {
        this.centerCB.setSelectedIndex(i);
    }

    public boolean alreadySet() {
        return (this.leftCB.getSelectedIndex() == -1 || this.rightCB.getSelectedIndex() == -1 || this.centerCB.getSelectedIndex() == -1);
    }
}

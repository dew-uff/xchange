package GUI.MainInterface;

import Documents.Document;
import Documents.Documents;
import GUI.Layout.LayoutConstraints;
import Manager.Manager;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Marcio Tadeu de Oliveira Júnior
 *
 * Esta classe tem por objetivo criar o conteudo da aba "Documents", com suas
 * areas de texto e os ComboBoxes que servirão para selecionar qual arquivo XML
 * será mostrado em cada Text Area definidas em DocumentsPane
 */
public class DocumentsTab extends JPanel implements ActionListener {

    private DocumentsPane docPane;
    private JComboBox leftCB, rightCB;
    private Documents documents;
    private Manager manager;
    private MainInterface main;
    private ArrayList<LineFile> lineFiles;
    private JPanel pnlFiles;
    final static int MOVE_UP = 1, REMOVE = 0, MOVE_DOWN = -1; //utilizadas para controlar a lista de documentos

    /**
     * Construtor da classe.
     *
     * @param manager
     */
    public DocumentsTab(Manager manager, MainInterface main) {
        super();
        this.main = main;

        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridBag);

        //cria o painel para a lista de documentos abertos
        JPanel pnlFilesList = new JPanel();
        setMinimumSize(new Dimension(310, 40));
        pnlFilesList.setBorder(javax.swing.BorderFactory.createTitledBorder("Files added"));

        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 2, 1, 100);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(pnlFilesList, constraints);
        pnlFilesList.setVisible(true);
        this.add(pnlFilesList);

        //adiciona a lista de arquivos ao painel esquerdo
        GridBagLayout gridBagMid = new GridBagLayout();
        pnlFilesList.setLayout(gridBagMid);

        pnlFiles = new JPanel();
        JScrollPane jsPane = new JScrollPane(pnlFiles);
        jsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsPane.setBorder(null);

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlFilesList.add(jsPane, constraints);

        pnlFiles.setLayout(new BoxLayout(pnlFiles, BoxLayout.PAGE_AXIS));

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
        LayoutConstraints.setConstraints(constraints, 1, 0, 1, 1, 3, 1);
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

        LayoutConstraints.setConstraints(constraints, 1, 1, 1, 1, 3, 100);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(docPane, constraints);
        docPane.setVisible(true);

        this.add(docPane);

        //Conclui a construção da interface
        lineFiles = new ArrayList<LineFile>();
        documents = new Documents();
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
        //Atualiza a lista de arquivos a ser exibida
        final int index = documents.getDocuments().size() - 1;
        if (index >= 0 && lineFiles.size() <= index) {
            final LineFile lineFile = new LineFile(documents.getDocument(index + 1));

            lineFile.getButtonUp().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    moveLineFile((LineFile) lineFile.getButtonUp().getParent(), MOVE_UP);
                }
            });
            lineFile.getButtonRemove().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    moveLineFile((LineFile) lineFile.getButtonUp().getParent(), REMOVE);
                }
            });
            lineFile.getButtonDown().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    moveLineFile((LineFile) lineFile.getButtonUp().getParent(), MOVE_DOWN);
                }
            });

            pnlFiles.add(lineFile);
            LineFile.setPnlFiles(pnlFiles);
            lineFiles.add(lineFile);
        }

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

        if (documents.getSize() == 0) {//se não há documentos limpa as areas de texto
            setRightText("");
            setLeftText("");
        } else if (documents.getSize() == 1) {//se há apenas um documento só o mostra no lado esquerdo
            leftCB.setSelectedIndex(0);
            this.setLeftText(documents.getContent(leftCB.getSelectedIndex()));
            rightCB.addItem("");
            rightCB.setSelectedIndex(1);
        } else if (documents.getSize() >= 2) {//se há mais de dois documentos permanecem os que estavam em exibição
            if (leftCBIndex == -1 && rightCBIndex == -1) {//Quando se está em algum modulo (ex: Syntatic Diff) e troca para outro modulo (ex: Semantic Diff), ou vice e versa, esses valores ficam igual a -1. O mesmo ocorre quando se abre um projeto
                leftCBIndex = 0;
                rightCBIndex = 1;
            }
            this.setLeftText(documents.getContent(leftCBIndex));
            this.setRightText(documents.getContent(rightCBIndex));

            leftCB.setSelectedIndex(leftCBIndex);
            rightCB.setSelectedIndex(rightCBIndex);
        }

        //adiciona novamente os eventos
        leftCB.addActionListener(this);
        rightCB.addActionListener(this);

        //Redefine a posição dos SplitPane's de acordo com a quantidade de documentos abertos
        this.docPane.resizeSplitPane(documents.getSize());

        //define a nova lista de documentos da classe
        this.documents = documents;

        this.revalidate();
    }

    private void moveLineFile(LineFile caller, int action) {
        int callerIndex = lineFiles.indexOf(caller);
        Document currentDoc = documents.getDocuments().get(callerIndex);

        switch (action) {
            case MOVE_UP:
                //Troca posições na lista de LineFiles
                LineFile before = lineFiles.get(callerIndex - 1);
                lineFiles.set(callerIndex - 1, caller);
                lineFiles.set(callerIndex, before);

                //Troca posição dos Documents
                Document beforeDoc = documents.getDocuments().get(callerIndex - 1);
                documents.getDocuments().set(callerIndex - 1, currentDoc);
                documents.getDocuments().set(callerIndex, beforeDoc);
                break;
            case REMOVE:
                //Remove da lista de LineFiles
                lineFiles.remove(callerIndex);

                //Remove dos Documents
                documents.remove(callerIndex);
                break;
            case MOVE_DOWN:
                //Troca posições na lista de LineFiles
                LineFile after = lineFiles.get(callerIndex + 1);
                lineFiles.set(callerIndex + 1, caller);
                lineFiles.set(callerIndex, after);

                //Troca posição dos Documents                
                Document afterDoc = documents.getDocuments().get(callerIndex + 1);
                documents.getDocuments().set(callerIndex + 1, currentDoc);
                documents.getDocuments().set(callerIndex, afterDoc);
                break;
        }

        //Troca posições na UI
        pnlFiles.removeAll();
        for (LineFile lf : lineFiles) {
            pnlFiles.add(lf);
        }

        //Atualiza UI
        revalidate();
        updateUI();
    }

    /**
     * Trata os eventos da classe.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e
    ) {
        if (e.getSource().equals(leftCB)) {//muda o texto esquerdo de acordo com o combobox esquerdo
            this.setLeftText(documents.getContent(leftCB.getSelectedIndex()));
        }
        if (e.getSource().equals(rightCB)) {//muda o texto direito de acordo com o combobox direito
            if (rightCB.getItemCount() >= 2) {
                this.setRightText(documents.getContent(rightCB.getSelectedIndex()));
            }
        }
    }

    public int getLeftCBIndex() {
        return this.leftCB.getSelectedIndex();
    }

    public int getRightCBIndex() {
        return this.rightCB.getSelectedIndex();
    }

    public Documents getDocuments() {
        return this.documents;
    }
}

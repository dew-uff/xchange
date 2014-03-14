package GUI.MainInterface;

import Documents.Document;
import Documents.Documents;
import GUI.Layout.LayoutConstraints;
import Manager.Manager;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Marcio Tadeu de Oliveira Júnior, Jorge Moreira
 *
 * Esta classe tem por objetivo criar o conteudo da aba "Documents", com suas
 * areas de texto e os ComboBoxes que servirão para selecionar qual arquivo XML
 * será mostrado em cada Text Area definidas em DocumentsPane
 */
public class DocumentsTab extends JPanel {

    private DocumentPane docPane;
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
     * @param main
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

        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 1, 100);
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

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlFilesList.add(jsPane, constraints);

        pnlFiles.setLayout(new BoxLayout(pnlFiles, BoxLayout.PAGE_AXIS));

        /*cria um DocumentsPane, que contem as duas TextAreas onde estará o
         * conteúdo dos documentos XML
         */
        docPane = new DocumentPane();

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
     * modifica o texto da TextArea direita para o texto no parametro s
     *
     * @param s
     */
    void setText(String s) {
        this.docPane.setText(s);
    }

    void setText(int documentIndex) {
        this.docPane.setText(documents.getDocument(documentIndex).getContent());
    }

    /**
     * Atualiza os componentes graficos de acordo com a lista de documentos
     * passada como parametros
     *
     * @param documents
     */
    public void refresh(Documents documents) {
        this.setText(documents.getContent(documents.getSize() - 1));

        //Atualiza a lista de arquivos a ser exibida
        final int index = documents.getDocuments().size() - 1;
        System.out.println(lineFiles.size() + " < " + (index + 1));
        if (index >= 0 && lineFiles.size() <= index) {

            final LineFile lineFile = new LineFile(documents.getDocument(index + 1));
            lineFile.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    setText(index + 1);
                }
            });

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

        this.documents = documents;

        this.revalidate();
        this.updateUI();
    }

    /**
     * Método utilizado para reordenar a lista ou remover um dos arquivos.
     *
     * @param action Define a ação a ser tomada. 0 para remover; -1 para mover
     * para baixo; 1 para mover para cima.
     * @param index Índice da LineFile que chama o método.
     */
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

    public Documents getDocuments() {
        return this.documents;
    }

    //Será removido posteriormente
    public int getLeftCBIndex() {
        return 0;
    }

    //Será removido posteriormente
    public int getRightCBIndex() {
        return 1;
    }
}

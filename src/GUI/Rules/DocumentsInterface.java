/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Rules;

import AutomaticRules.WekaParser;
import Documents.Document;
import Documents.Documents;
import GUI.Layout.LayoutConstraints;
import GUI.MainInterface.DocumentsTab;
import GUI.MainInterface.LineFile;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Jorge
 */
public class DocumentsInterface extends JDialog {

    private DocumentsTab documentsTab;
    private ArrayList<LineFile> lineFiles;
    private JPanel pnlFiles;
    private List<String> chosenTags;
    private String keyChoice;
    private List<Set> listRules = new ArrayList<Set>();
    private final static int MOVE_UP = 1, REMOVE = 0, MOVE_DOWN = -1; //utilizadas para controlar a lista de documentos
    private Documents documents;

    protected DocumentsInterface(DocumentsTab documentsTab, List<Set> listRules, List<String> chosenTags, String keyChoice) {
        this.documentsTab = documentsTab;
        
        this.setSize(new Dimension(300, 500));
        this.setTitle("Files ordering");

        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridBag);

        pnlFiles = new JPanel();
        JScrollPane jsPane = new JScrollPane(pnlFiles);
        jsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsPane.setBorder(null);

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        this.add(jsPane, constraints);

        pnlFiles.setLayout(new BoxLayout(pnlFiles, BoxLayout.PAGE_AXIS));

        documents = documentsTab.getDocuments();
        lineFiles = new ArrayList<LineFile>();
        for (Document doc : documents.getDocuments()) {
            final LineFile lineFile = new LineFile(doc);

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

        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1000);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTH;
        gridBag.setConstraints(this, constraints);
        this.add(jsPane, constraints);
        
        JButton btnOK = new JButton("OK");
        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.NORTH;
        gridBag.setConstraints(this, constraints);
        this.add(btnOK, constraints);

        setVisible(true);
        setModal(true);
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
    }

    private void aux() {
        listRules = new WekaParser().generateRules(documentsTab, chosenTags, keyChoice);
    }

}

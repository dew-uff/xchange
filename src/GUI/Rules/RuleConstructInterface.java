package GUI.Rules;

import GUI.Layout.LayoutConstraints;
import Manager.Manager;
import Rules.RulesModule;
import AutomaticRules.WekaParser;
import Exception.NoSelectedFileException;
import GUI.FileManager.LastpathManager;
import GUI.FileManager.XMLFileFilter;
import GUI.MainInterface.DocumentsTab;
import GUI.MainInterface.InferenceFileChooser;
import GUI.MainInterface.LineFile;
import Rules.Rule;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static java.awt.image.ImageObserver.WIDTH;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Interface onde o usuário monta as regras para a realização de inferencia
 *
 * @author Guilherme Martins, Marcio Oliveira Junior, Celio H. N. Larcher Junior
 * e Jorge Moreira
 */
public class RuleConstructInterface extends JDialog implements ActionListener {

    //Variáveis utilizadas
    private DocumentsTab documentsTab;
    private RulesModule rulesModule;
    private JButton btnAtributs;
    private static JPanel pnlRules; //onde ficam os campos da regra em construção
    private String[] factsPart;
    private String[] namesFacts;
    public String keyChoice; //chave de contexto
    private String nameFactInRule;
    private String factBase2v1, factBase2v2, factBase1v1, factBase1v2;
    private String factBase2, factBase1, baseRule;
    private final JLabel lblChoiceKey;
    private JButton btnSaveRule, btnFinishBuilder, btnMineRules, btnFilesList; //Botões do rodapé
    private JButton btnOpen, btnSave, btnExport; //Botões da barra de ferramentas
    private JTextField nameRule;
    private JComboBox comboOutput;
    private JLabel labelRuleName, labelOutput;
    private boolean terminalOpen;
    //Lista que contém todas as linhas de regra
    ArrayList<LineRule> lineRules;
    //Elementos de tela da mineração de regras
    private static JPanel pnlGeneratedRules; //exibe as regras geradas a partir da mineração
    private ArrayList<JCheckBox> checkTagsArray = new ArrayList<JCheckBox>();
    private ArrayList<String> chosenTags = new ArrayList<String>();
    List<Set> listRules = new ArrayList<Set>();
    private JComboBox cmbKey; //permite escolher a chave de contexto
    private String results; //regras usadas pelo usuário
    private JPanel pnlBar, pnlMining, pnlConstructRule, pnlResults, pnlOutput; //paineis principais
    private JPanel pResults; //painel que contem as regras construídas e permite usuário escolher quais regras usar
    private GridBagLayout gridBag;
    private InferenceFileChooser inferenceFileChooser;
    private int selectedRuleIndex; //usado para identificar qual regra estásendo construída/editada (-1 para nova regra)
    private ArrayList<LineFile> lineFiles;
    private JPanel pnlFiles;
    private final static int MOVE_UP = 1, REMOVE = 0, MOVE_DOWN = -1; //utilizadas para controlar a lista de documentos

    /**
     * Exibe a janela para construção das regras.
     *
     * @param manager Objeto do tipo "Manager" que chamou esta função.
     * @param isSimilarity Booleano que indica se o método escolhido foi
     * "Context Key" ou "Similarity".
     * @param inferenceFileChooser
     * @param documentsTab
     */
    public RuleConstructInterface(Manager manager, boolean isSimilarity, InferenceFileChooser inferenceFileChooser, DocumentsTab documentsTab) {
        this.documentsTab = documentsTab;
        this.inferenceFileChooser = inferenceFileChooser;
        setModal(true);
        setTitle("Key Attribute");
        this.rulesModule = manager.getRulesModule();
        lineRules = new ArrayList<LineRule>();
        LineRule.setLinerules(lineRules);
        terminalOpen = false;
        pnlRules = new JPanel(new FlowLayout());
        lblChoiceKey = new JLabel("Select the Key Attribute:");
        cmbKey = new JComboBox<String>();

        btnAtributs = new JButton("List the attributes");
        btnAtributs.addActionListener(this);
        btnSaveRule = new JButton();
        btnSaveRule.addActionListener(this);
        btnFinishBuilder = new JButton();
        btnFinishBuilder.addActionListener(this);

        selectedRuleIndex = -1;

        if (isSimilarity) { //Se o metodo utilizado for o "Similarity"
            factsPart = manager.getSimilarity().get(0).partFacts(manager.getSimilarity().get(0).getFacts());
            namesFacts = manager.getSimilarity().get(0).getNameFacts();
            nameFactInRule = manager.getSimilarity().get(0).getElementName().toUpperCase();

            factBase1v1 = manager.getSimilarity().get(0).getMainFact(factsPart, "before");
            factBase1v2 = manager.getSimilarity().get(0).getMainFact(factsPart, "after");
            keyChoice = "id"; //No modulo "Similarity" a chave de contexto sempre será o atributo ID
        } else { //Se o metodo utilizado for o "Context Key"
            factsPart = manager.getContextKey().get(0).partFacts(manager.getContextKey().get(0).getFacts());
            List<String> listNameFacts = new WekaParser().getTags(this.documentsTab.getDocuments().getPathWays().get(this.documentsTab.getRightCBIndex()));
            nameFactInRule = listNameFacts.get(0).toUpperCase();
            listNameFacts.remove(0);
            namesFacts = listNameFacts.toArray(new String[listNameFacts.size()]);
            factBase1v1 = manager.getContextKey().get(0).getMainFact(factsPart, "before");
            factBase1v2 = manager.getContextKey().get(0).getMainFact(factsPart, "after");

            keyChoice = listNameFacts.get(0);

            cmbKey = new JComboBox(listNameFacts.toArray());
            cmbKey.setSelectedIndex(0);
            cmbKey.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        keyChoice = ((JComboBox) e.getSource()).getSelectedItem().toString();
                        constructRules();
                    }
                }
            });
        }

        LineRule.setNamesFacts(namesFacts);
        factBase1 = factBase1v1 + "," + factBase1v2;

        constructRules();

        buildInterface();

        setVisible(true);
    }

    /**
     * Trata as ações dos botões e das seleções realizadas pelo usuário.
     *
     * @param e Recebe um evento gerado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAtributs) {
        } else if (e.getSource() == btnSaveRule) { //Valida as opções de selecionadas na construção da regra e a adiciona ao conjunto de regras
            finishRuleAndCreateNew();
        } else if (e.getSource() == btnFinishBuilder) {
            finish();
        } else if (e.getSource() == btnSave) {
            saveProject();
        } else if (e.getSource() == btnOpen) {
            try {
                loadProject();
            } catch (NoSelectedFileException ex) {
            }
        } else if (e.getSource() == btnMineRules) {
            for (JCheckBox item : checkTagsArray) {
                if (item.isSelected()) {
                    chosenTags.add(item.getName());
                }
            }
            setModal(false);
            //DocumentsInterface documentsInterface = new DocumentsInterface(documentsTab, listRules, chosenTags, keyChoice);
            listRules = new WekaParser().generateRules(documentsTab, chosenTags, keyChoice);
            showMinedRules(listRules);
        } else if (e.getSource() == btnFilesList)
            showFilesList();
    }
    
    private void showFilesList(){
        pnlMining.removeAll();

        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        pnlMining.setLayout(gridBag);

        pnlFiles = new JPanel();
        JScrollPane jsPane = new JScrollPane(pnlFiles);
        jsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsPane.setBorder(null);

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlMining.add(jsPane, constraints);

        pnlFiles.setLayout(new BoxLayout(pnlFiles, BoxLayout.PAGE_AXIS));

        lineFiles = new ArrayList<LineFile>();
        for (Documents.Document doc : documentsTab.getDocuments().getDocuments()) {
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
        pnlMining.add(jsPane, constraints);
        
        JPanel btnMineRulesPanel = new JPanel();

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlMining.add(btnMineRulesPanel, constraints);

        //Botão para minerar regras
        btnMineRules = new JButton("Mine Rules");
        btnMineRules.addActionListener(this);

        btnMineRules.setVisible(true);
        btnMineRules.setMinimumSize(new Dimension(350, 25));
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        btnMineRulesPanel.add(btnMineRules, constraints);
        
        pnlMining.revalidate();
    }
    
    private void moveLineFile(LineFile caller, int action) {
        int callerIndex = lineFiles.indexOf(caller);
        Documents.Document currentDoc = documentsTab.getDocuments().getDocuments().get(callerIndex);

        switch (action) {
            case MOVE_UP:
                //Troca posições na lista de LineFiles
                LineFile before = lineFiles.get(callerIndex - 1);
                lineFiles.set(callerIndex - 1, caller);
                lineFiles.set(callerIndex, before);

                //Troca posição dos Documents
                Documents.Document beforeDoc = documentsTab.getDocuments().getDocuments().get(callerIndex - 1);
                documentsTab.getDocuments().getDocuments().set(callerIndex - 1, currentDoc);
                documentsTab.getDocuments().getDocuments().set(callerIndex, beforeDoc);
                break;
            case REMOVE:
                //Remove da lista de LineFiles
                lineFiles.remove(callerIndex);

                //Remove dos Documents
                documentsTab.getDocuments().remove(callerIndex);
                break;
            case MOVE_DOWN:
                //Troca posições na lista de LineFiles
                LineFile after = lineFiles.get(callerIndex + 1);
                lineFiles.set(callerIndex + 1, caller);
                lineFiles.set(callerIndex, after);

                //Troca posição dos Documents                
                Documents.Document afterDoc = documentsTab.getDocuments().getDocuments().get(callerIndex + 1);
                documentsTab.getDocuments().getDocuments().set(callerIndex + 1, currentDoc);
                documentsTab.getDocuments().getDocuments().set(callerIndex, afterDoc);
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

    private void finishRuleAndCreateNew() {
        finishRule(); //Salva a atual

        //"Limpa" o construtor
        comboOutput.setSelectedItem("");
        nameRule.setText("");

        btnSaveRule.setEnabled(true);
        pnlRules.removeAll();
        lineRules.clear();
        LineRule aux = new LineRule();
        lineRules.add(aux);
        pnlRules.add(aux);
        aux.getComboTerm1().requestFocus();
        pnlRules.revalidate();
    }

    private String formatGetTextPane(String paneRules) {
        String textFormated = paneRules;
        textFormated = textFormated.replaceAll("\n", ""); //Eliminando quebras de linha
        textFormated = textFormated.replaceAll("\r", ""); //Eliminando quebras de linha
        textFormated = textFormated.replaceAll("\t", ""); //Eliminando tabulações
        textFormated = textFormated.replaceAll(" ", ""); //Eliminando espaçoes em branco
        textFormated = textFormated.replaceAll("\\.", "\\.\n"); //Acrescenta quebra de linha entre as regras do painel

        return textFormated;
    }

    /**
     * Finaliza a construção da regra atual
     */
    private void finishRule() {
        if (nameRule.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "It's necessary give the rule a name", "Error", JOptionPane.ERROR_MESSAGE);
            nameRule.requestFocus();
        }
        if (comboOutput.getSelectedItem().equals("")) {
            JOptionPane.showMessageDialog(this, "It's necessary choose output type", "Error", JOptionPane.ERROR_MESSAGE);
            comboOutput.requestFocus();
        } else {
            Iterator iter = lineRules.iterator();
            lineRules = LineRule.getLinerules();
            LineRule.setLinerules(lineRules);
            int validRows = 0;
            while (iter.hasNext()) {
                LineRule condition = (LineRule) iter.next();
                if ((!condition.getComboTerm1().getSelectedItem().toString().equals("") && !condition.getComboTerm2().getSelectedItem().toString().equals("")) || (condition.getComboOperator().getSelectedItem().toString().equals("new_element") || (condition.getComboOperator().getSelectedItem().toString().equals("deleted_element")))) {
                    validRows += 1;
                    break;
                }
            }
            if (validRows == 0) {
                JOptionPane.showMessageDialog(this, "You must select at least one valid condition!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String regraConst = "";
                for (LineRule condition : lineRules) {
                    if ((!condition.getComboTerm1().getSelectedItem().toString().equals("") && !condition.getComboTerm2().getSelectedItem().toString().equals("")) || (condition.getComboOperator().getSelectedItem().toString().equals("new_element") || (condition.getComboOperator().getSelectedItem().toString().equals("deleted_element")))) {
                        String aux = buildCondition(comboOutput.getSelectedItem().toString(), condition.getComboTerm1().getSelectedItem().toString(), condition.getComboOperator().getSelectedItem().toString(), condition.getComboTerm2().getSelectedItem().toString(), false);
                        if (regraConst.equals("")) {
                            regraConst = aux;
                        } else {
                            regraConst = regraConst + "," + aux;
                        }
                    }
                }
                //Prepara as regras construídas
                if (lineRules.get(0).getComboOperator().getSelectedItem().toString().indexOf("_") < 0) {
                    regraConst = nameRule.getText().toLowerCase() + "(" + comboOutput.getSelectedItem().toString().toUpperCase() + "):-" + baseRule + "," + comboOutput.getSelectedItem().toString() + "(" + nameFactInRule + "Before," + comboOutput.getSelectedItem().toString().toUpperCase() + ")," + regraConst + ".";
                } else {
                    regraConst = nameRule.getText().toLowerCase() + "(" + comboOutput.getSelectedItem().toString().toUpperCase() + "):-" + "" + regraConst + ".";
                }

                if (selectedRuleIndex == -1) {
                    rulesModule.addRule(regraConst);
                } else {
                    rulesModule.getRules().set(selectedRuleIndex, new Rule(regraConst));
                }

                results = formatSetTextPane(rulesModule.getRulesString()); //Formata as regras que serão exibidas na tela
                System.out.println(results);

                if (!results.isEmpty()) {
                    String[] partRules = rulesModule.partRules(results); //Pega o cabeçalho das regras (ex: salary(NAME))
                    buildResultsPanel(partRules, null);
                } else {
                    JOptionPane.showMessageDialog(this, "It's necessary to difine the rules to "
                            + "realize inference of informations.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Encerra o construtor
     */
    private void finish() {
        if (!results.isEmpty()) {
            ArrayList<String> selectedRules = new ArrayList<String>();
            int cont = 0;
            for (int i = 0; i < pResults.getComponentCount(); i++) { //Verifica quais regras foram selecionadas pelo usuário
                JCheckBox item = (JCheckBox) ((JPanel) pResults.getComponents()[i]).getComponents()[0];
                if (item.isSelected()) {
                    cont++;
                    selectedRules.add(item.getName());
                }
            }
            if (cont > 0) {
                String paneRules = formatGetTextPane(results); //Formata as regras obtidas através do Painel

                this.rulesModule.addRule(paneRules); //Adiciona as regras do painel
                this.rulesModule.addSelectRules(selectedRules); //Adiciona as regras selecionadas em sua respectiva variável
                this.rulesModule.adjustRules(); //Remove as regras repetidas
                this.inferenceFileChooser.setSelectedRules(selectedRules);//Envia ao InferenceFileChooser a lista de regras selecionadas
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "It's necessary to define the rules to "
                        + "realize inference of informations.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "It's necessary to define the rules to "
                    + "realize inference of informations.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Atualiza a lista de regras construidas
     */
    private void buildResultsPanel(String[] partRules, List<Boolean> enabledList) {
        pResults.removeAll();

        String[] rulesHeads = rulesModule.getNameAndArgumentsRules(partRules);
        for (int i = 0; i < rulesHeads.length; i++) { //Cria os campos do CheckBox de acordo com as regras inseridas pelo usuário
            JPanel pnlRule = new JPanel();

            JCheckBox chkItem = new JCheckBox();
            chkItem.setName(rulesHeads[i]);
            if (enabledList != null) {
                chkItem.setSelected(enabledList.get(i));
            }

            GridBagConstraints constraints = new GridBagConstraints();

            LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            pnlRule.add(chkItem, constraints);

            JLabel rule = new JLabel(rulesHeads[i]);

            LayoutConstraints.setConstraints(constraints, 1, 0, 1, 1, 1000, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            pnlRule.add(rule, constraints);

            if (enabledList != null) {
                chkItem.setSelected(enabledList.get(i));
            }
            final int index = i;
            rule.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    editRule(index);
                }
            });

            constraints = new GridBagConstraints();
            LayoutConstraints.setConstraints(constraints, 0, i, 1, 1, 1, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            pResults.add(pnlRule);
        }

        btnFinishBuilder.setEnabled(pResults.getComponentCount() > 0);
        pResults.revalidate();
    }

    /**
     * Exibe a regra selecionada no construtor
     */
    private void editRule(int index) {
        selectedRuleIndex = index;
        lineRules.clear();

        String rule = rulesModule.getRules().get(index).getRule();
        rule = rule.replace(" ", "");
        String[] aux = rule.split(":-");

        //lê o cabeçalho da regra
        comboOutput.setSelectedItem(rule.substring(rule.indexOf("(") + 1, rule.indexOf(")")).toLowerCase());
        nameRule.setText(aux[0].substring(0, aux[0].indexOf("(")));

        //lê cada condição
        for (String s : aux[1].split(",")) {
            LineRule condition = new LineRule();

            String change;
            if (s.contains("\\=")) {
                change = "\\=";
                condition.getComboOperator().setSelectedIndex(4);
            } else if (s.contains("<")) {
                change = "<";
                condition.getComboOperator().setSelectedIndex(2);
            } else if (s.contains(">")) {
                change = ">";
                condition.getComboOperator().setSelectedIndex(1);
            } else if (s.contains("==")) {
                change = "==";
                condition.getComboOperator().setSelectedIndex(3);
            } else {
                continue;
            }

            String[] terms = s.split(change);

            if (terms[0].contains("Before")) {
                String term1 = terms[0].substring(0, terms[0].indexOf("Before")).toLowerCase() + " - v. Before";
                condition.getComboTerm1().setSelectedItem(term1);
            } else {
                String term1 = terms[0].substring(0, terms[0].indexOf("After")).toLowerCase() + " - v. After";
                condition.getComboTerm1().setSelectedItem(term1);
            }

            if (terms[1].contains("Before")) {
                String term2 = terms[1].substring(0, terms[1].indexOf("Before")).toLowerCase() + " - v. Before";
                condition.getComboTerm2().setSelectedItem(term2);
            } else {
                String term2 = terms[1].substring(0, terms[1].indexOf("After")).toLowerCase() + " - v. After";
                condition.getComboTerm2().setSelectedItem(term2);
            }

            lineRules.add(condition);
            LineRule.setLinerules(lineRules);
            pnlRules.revalidate();
        }
    }

    /**
     * Exibe a interface de construção de regras.
     */
    private void buildInterface() {
        JPanel allPane = new JPanel();
        this.setMinimumSize(new Dimension(1000, 500));
        this.setSize(this.getMinimumSize());

        this.setContentPane(allPane);
        if (!terminalOpen) { //Se for a primeira vez que ele é acionado
            nameRule = new JTextField();
            labelRuleName = new JLabel();
            comboOutput = new JComboBox();
            labelOutput = new JLabel();
        }
        terminalOpen = true;

        //declara objetos de controle do layout
        gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        allPane.setLayout(gridBag);

        //paineis da janela de criação de regras
        pnlBar = new JPanel();
        pnlBar.setLayout(new BoxLayout(pnlBar, BoxLayout.Y_AXIS));

        pnlOutput = new JPanel();
        pnlOutput.setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));

        pnlConstructRule = new JPanel();
        pnlConstructRule.setBorder(javax.swing.BorderFactory.createTitledBorder("Conditions"));

        pnlMining = new JPanel();
        pnlMining.setBorder(javax.swing.BorderFactory.createTitledBorder("Association Rules"));

        pnlResults = new JPanel();
        pnlResults.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));

        //adiciona os paineis à janela de construção de regras
        LayoutConstraints.setConstraints(constraints, 0, 0, 3, 1, 1, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        allPane.add(pnlBar, constraints);

        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 2, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        allPane.add(pnlMining, constraints);

        LayoutConstraints.setConstraints(constraints, 1, 1, 1, 1, 2, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        allPane.add(pnlOutput, constraints);

        LayoutConstraints.setConstraints(constraints, 1, 2, 1, 1, 2, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        allPane.add(pnlConstructRule, constraints);

        LayoutConstraints.setConstraints(constraints, 2, 1, 1, 2, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        allPane.add(pnlResults, constraints);

        //Cria a barra de ferramentas
        JToolBar tBar = new JToolBar();

        //Define os icones que serão usados nos botões
        ImageIcon openIcon = new ImageIcon(getClass().getResource("/GUI/icons/open.png"));
        ImageIcon saveIcon = new ImageIcon(getClass().getResource("/GUI/icons/save.png"));

        //Cria os botões e seus eventos        
        btnOpen = new JButton(openIcon);
        btnOpen.setToolTipText("Open Project");
        btnOpen.setEnabled(true);
        btnOpen.addActionListener(this);

        btnSave = new JButton(saveIcon);
        btnSave.setToolTipText("Save Project");
        btnSave.setEnabled(true);
        btnSave.addActionListener(this);

        btnExport = new JButton(openIcon);
        btnExport.setToolTipText("Export to Prolog Facts");
        btnExport.setEnabled(true);
        btnExport.addActionListener(this);

        //Adiciona os botões à barra de ferramentas
        tBar.add(btnOpen);
        tBar.add(btnSave);
        tBar.add(btnExport);
        if (!keyChoice.equals("id")) {
            tBar.add(new JSeparator(SwingConstants.VERTICAL));
            tBar.add(new JLabel("Context Key: "));
            tBar.add(cmbKey);
        }

        tBar.setAlignmentX(0);

        tBar.setFloatable(false); //Fixa a barra de ferramentas à sua posição

        //indica a posição e layout da barra de ferramentas
        LayoutConstraints.setConstraints(constraints, 0, 0, 3, 1, 1000, 0);
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(pnlBar, constraints);

        //Adiciona a barra de ferramentas ao seu painel
        pnlBar.add(tBar);
        tBar.setVisible(true);
        pnlBar.setVisible(true);

        //declara objetos de controle do layout do painel do topo
        GridBagLayout gridBagTop = new GridBagLayout();
        pnlOutput.setLayout(gridBagTop);

        JPanel ruleNamePnl = new JPanel();//painel para o label e a area de texto para o nome de regra
        JPanel ruleOutputPnl = new JPanel();//painel para o label e o combobox para a saida da regra

        //layout para os paineis com nome de regra e saida de regra
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        pnlOutput.add(ruleNamePnl, constraints);

        LayoutConstraints.setConstraints(constraints, 1, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        pnlOutput.add(ruleOutputPnl, constraints);

        //layout para painel com o label e a area de texto para o nome de regra
        GridBagLayout gridBagRuleName = new GridBagLayout();
        ruleNamePnl.setLayout(gridBagRuleName);

        labelRuleName.setText("Rule Name:");
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(10, 10, 10, 5);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        ruleNamePnl.add(labelRuleName, constraints);

        nameRule.setText("");
        constraints.insets = new Insets(10, 0, 10, 5);
        LayoutConstraints.setConstraints(constraints, 1, 0, 1000, 1, 10, 1);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        ruleNamePnl.add(nameRule, constraints);

        //layout para painel com o label e o combobox para a saida da regra
        GridBagLayout gridBagRuleOutput = new GridBagLayout();
        ruleOutputPnl.setLayout(gridBagRuleOutput);

        labelOutput.setText("Output:");
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(10, 5, 10, 5);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        ruleOutputPnl.add(labelOutput, constraints);

        comboOutput.setModel(new javax.swing.DefaultComboBoxModel(namesFacts));
        comboOutput.insertItemAt("", 0);
        comboOutput.setSelectedItem("");
        constraints.insets = new Insets(10, 0, 10, 10);
        LayoutConstraints.setConstraints(constraints, 1, 0, 1000, 1, 10, 1);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        ruleOutputPnl.add(comboOutput, constraints);

        //declara objetos de controle do layout do painel central
        GridBagLayout gridBagMid = new GridBagLayout();
        pnlConstructRule.setLayout(gridBagMid);

        pnlRules = new JPanel();

        JScrollPane jsPane = new JScrollPane(pnlRules);
        jsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsPane.setBorder(null);

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1000);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlConstructRule.add(jsPane, constraints);

        JPanel buttonsPanel = new JPanel();

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlConstructRule.add(buttonsPanel, constraints);

        btnSaveRule.setText("Save rule and create new");
        btnSaveRule.setMinimumSize(new Dimension(350, 25));
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        buttonsPanel.add(btnSaveRule, constraints);

        LineRule firstLineRule = new LineRule();
        pnlRules.setLayout(new BoxLayout(pnlRules, BoxLayout.PAGE_AXIS));
        pnlRules.add(firstLineRule);
        LineRule.setPnlRules(pnlRules);
        lineRules.add(firstLineRule);

        //declara objetos de controle do layout do painel da esquerda (regras de associação)
        GridBagLayout gridBagLeft = new GridBagLayout();
        GridBagConstraints constraintsMining = new GridBagConstraints();
        pnlMining.setLayout(gridBagLeft);

        JLabel lblMineRules = new JLabel("Select the tags you want to mine:");
        lblMineRules.revalidate();

        LayoutConstraints.setConstraints(constraintsMining, 0, 0, 1, 1, 1, 0);
        constraintsMining.fill = GridBagConstraints.NONE;
        constraintsMining.anchor = GridBagConstraints.NORTHWEST;
        pnlMining.add(lblMineRules, constraintsMining);

        //Painel com as tags no painel "Association Rules"
        GridBagLayout centerGridBag = new GridBagLayout();

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, WIDTH));
        p.setVisible(true);

        JScrollPane jscPane = new JScrollPane(p);
        jscPane.setBorder(null);

        LayoutConstraints.setConstraints(constraintsMining, 0, 0, 1, 1, 1, 1);
        constraintsMining.fill = GridBagConstraints.BOTH;
        centerGridBag.addLayoutComponent(p, constraintsMining);

        //Recupera a lista de tags a partir do segundo arquivo carregado no projeto
        chosenTags = new ArrayList<String>();
        checkTagsArray = new ArrayList<JCheckBox>();

        List<String> tags = new WekaParser().getTags(documentsTab.getDocuments().getPathWays().get(documentsTab.getRightCBIndex()));
        for (String tag : tags) { //Cria os campos do CheckBox de acordo com as regras inseridas pelo usuário
            if (tags.get(0).equals(tag)) {
                continue;
            }
            JCheckBox chkItem = new JCheckBox(tag);
            chkItem.setName(tag);
            chkItem.setSelected(true);
            checkTagsArray.add(chkItem);
            p.add(chkItem);
        }

        LayoutConstraints.setConstraints(constraintsMining, 0, 1, 1, 1, 1, 1000);
        constraintsMining.fill = GridBagConstraints.BOTH;
        constraintsMining.anchor = GridBagConstraints.NORTHWEST;
        pnlMining.add(jscPane, constraintsMining);

        JPanel btnFileListPanel = new JPanel();

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlMining.add(btnFileListPanel, constraints);

        //Botão para minerar regras
        btnFilesList = new JButton("Next");
        btnFilesList.addActionListener(this);

        btnFilesList.setVisible(true);
        btnFilesList.setMinimumSize(new Dimension(350, 25));
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        btnFileListPanel.add(btnFilesList, constraints);

        //Painel de resultados
        GridBagLayout gridBagRight = new GridBagLayout();
        GridBagConstraints constraintsResults = new GridBagConstraints();
        pnlResults.setLayout(gridBagRight);

        GridBagLayout rightGridBag = new GridBagLayout();

        pResults = new JPanel();
        pResults.setLayout(new BoxLayout(pResults, WIDTH));
        pResults.setVisible(true);

        JScrollPane jscResults = new JScrollPane(pResults);
        jscResults.setBorder(null);

        LayoutConstraints.setConstraints(constraintsResults, 0, 0, 1, 1, 1, 1);
        constraintsResults.fill = GridBagConstraints.BOTH;
        rightGridBag.addLayoutComponent(pResults, constraintsResults);

        LayoutConstraints.setConstraints(constraintsResults, 0, 0, 1, 1, 1, 1000);
        constraintsResults.fill = GridBagConstraints.BOTH;
        constraintsResults.anchor = GridBagConstraints.NORTHWEST;
        pnlResults.add(jscResults, constraintsResults);

        JPanel btnFinishBuilderPanel = new JPanel();

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlResults.add(btnFinishBuilderPanel, constraints);

        btnFinishBuilder = new JButton("Show results");
        btnFinishBuilder.setVisible(true);
        btnFinishBuilder.setEnabled(false);
        btnFinishBuilder.setMinimumSize(new Dimension(350, 25));
        btnFinishBuilder.addActionListener(this);

        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        btnFinishBuilderPanel.add(btnFinishBuilder, constraints);

        results = formatSetTextPane(rulesModule.getRulesString()); //Formata as regras que serão exibidas na tela

        setVisible(true);
        pResults.setVisible(true);
    }

    private String formatSetTextPane(String paneRules) {
        String textFormated = paneRules;
        textFormated = textFormated.replaceAll("\n", ""); //Eliminando quebras de linha
        textFormated = textFormated.replaceAll("\r", ""); //Eliminando quebras de linha
        textFormated = textFormated.replaceAll("\t", ""); //Eliminando tabulações
        textFormated = textFormated.replaceAll(" ", ""); //Eliminando espaçoes em branco
        textFormated = textFormated.replaceAll("\\.", "\\.\n\n"); //Acrescenta quebra de linha entre as regras do painel
        textFormated = textFormated.replaceAll("(\\:-|\\:-\n)", "\\:-\n    "); //Acrescenta quebra de linha e espaços depois de ":-"
        textFormated = textFormated.replaceAll("(\\),|\\),\n)", "\\),\n    "); //Acrescenta quebra de linha e espaços depois de "),"

        return textFormated;
    }

    /**
     * Função para criar cada condição existente na regra composta.
     *
     * @param exit O que se deseja como saída da regra.
     * @param term1 Primeiro termo a ser utilizado.
     * @param operator Operador a ser aplicado.
     * @param term2 Segundo termo a ser utilizado.
     * @return newRule Nova regra construída.
     */
    private String buildCondition(String exit, String term1, String operator, String term2, boolean openningProject) {
        String newRule;
        String ruleAux;
        String term1After;
        String term2After;
        String arg1term1;
        String arg1term2;

        ruleAux = "";
        newRule = "";

        if (operator.equals("new_element")) {
            /*
             ex: exists_after(NOME):-	funcionario(after,Fa),	nome(Fa,NOME).
             existeAfter = "exists_after("+saida.toUpperCase()+"):-"+fatoBase1v2+","+saida+"(Fa,"+saida.toUpperCase()+").";
             ex: exists_before(NOME):-	funcionario(before,Fb),	nome(Fb,NOME).
             existeBefore = "exists_before("+saida.toUpperCase()+"):-"+fatoBase1v1+","+saida+"(Fb,"+saida.toUpperCase()+").";
             new_element(X):- funcionario(after,Fa),nome(Fa,NOME),not((funcionario(before,Fb),nome(Fb,NOME))).
             */
            newRule = factBase1v2 + "," + keyChoice + "(" + nameFactInRule + "After," + keyChoice.toUpperCase() + ")," + exit + "(" + nameFactInRule + "After," + exit.toUpperCase() + ")," + "not((" + factBase1v1 + "," + keyChoice + "(" + nameFactInRule + "Before," + keyChoice.toUpperCase() + ")))";
        } else if (operator.equals("deleted_element")) {
            /*
             ex: exists_after(NOME):-	funcionario(after,Fa),	nome(Fa,NOME).
             existeAfter = "exists_after("+saida.toUpperCase()+"):-"+fatoBase1v2+","+saida+"(Fa,"+saida.toUpperCase()+").";
             ex: exists_before(NOME):-	funcionario(before,Fb),	nome(Fb,NOME).
             existeBefore = "exists_before("+saida.toUpperCase()+"):-"+fatoBase1v1+","+saida+"(Fb,"+saida.toUpperCase()+").";
             element_deleted(X):-funcionario(before,Fb),nome(Fb,NOME),not((funcionario(after,Fa),nome(Fa,NOME))).
             */
            newRule = factBase1v1 + "," + keyChoice + "(" + nameFactInRule + "Before," + keyChoice.toUpperCase() + ")," + exit + "(" + nameFactInRule + "Before," + exit.toUpperCase() + ")," + "not((" + factBase1v2 + "," + keyChoice + "(" + nameFactInRule + "After," + keyChoice.toUpperCase() + ")))";
        } else {
            String[] term1part;
            String[] term2part;

            if (openningProject) {
                if (term1.contains("Before")) {
                    term1part = new String[]{term1.substring(0, term1.indexOf("Before")).toLowerCase(), "Before"};
                    arg1term1 = nameFactInRule + "Before";
                } else {
                    term1part = new String[]{term1.substring(0, term1.indexOf("After")).toLowerCase(), "After"};
                    arg1term1 = nameFactInRule + "After";
                }

                if (term2.contains("Before")) {
                    term2part = new String[]{term2.substring(0, term2.indexOf("Before")).toLowerCase(), "Before"};
                    arg1term2 = nameFactInRule + "Before";
                } else {
                    term2part = new String[]{term2.substring(0, term2.indexOf("After")).toLowerCase(), "After"};
                    arg1term2 = nameFactInRule + "After";
                }
            } else {
                term1part = term1.split("\\-");
                //Indice 0: nome do fato que compõe o termo
                //Indice 1: v. Before OU v. After
                term2part = term2.split("\\-");

                //pegando o "Before" ou "After"
                term1part[1] = term1part[1].substring(term1part[1].lastIndexOf(".") + 2);
                term2part[1] = term2part[1].substring(term2part[1].lastIndexOf(".") + 2);

                if (term1part[1].equals("Before")) {
                    arg1term1 = nameFactInRule + "Before";
                } else {
                    arg1term1 = nameFactInRule + "After";
                }

                if (term2part[1].equals("Before")) {
                    arg1term2 = nameFactInRule + "Before";
                } else {
                    arg1term2 = nameFactInRule + "After";
                }

                term1part[0] = term1part[0].replaceAll(" ", "");
                term1part[1] = term1part[1].replaceAll(" ", "");
                term2part[0] = term2part[0].replaceAll(" ", "");
                term2part[1] = term2part[1].replaceAll(" ", "");
            }

            term1After = term1part[0] + "(" + arg1term1 + "," + term1part[0].toUpperCase() + term1part[1] + ")";
            term2After = term2part[0] + "(" + arg1term2 + "," + term2part[0].toUpperCase() + term2part[1] + ")";

            if (operator.equals("and")) {
                //Nao faz nada
                ruleAux = "";
            } else if (operator.equals(">")) {
                //Adiciona uma regra do tipo SalarioB>SalarioM
                ruleAux = term1part[0].toUpperCase() + term1part[1] + ">" + term2part[0].toUpperCase() + term2part[1];

            } else if (operator.equals("<")) {
                //Adiciona uma regra do tipo SalarioB<SalarioM
                ruleAux = term1part[0].toUpperCase() + term1part[1] + "<" + term2part[0].toUpperCase() + term2part[1];

            } else if (operator.equals("=")) {
                ruleAux = term1part[0].toUpperCase() + term1part[1] + "==" + term2part[0].toUpperCase() + term2part[1];

            } else if ((openningProject && operator.equals("\\=")) || (!openningProject && operator.equals("!="))) {
                ruleAux = term1part[0].toUpperCase() + term1part[1] + "\\=" + term2part[0].toUpperCase() + term2part[1];

            }
            newRule = term1After + "," + term2After + "," + ruleAux;
        }//Fecha else do teste dos operadores new_element ou element_deleted
        return newRule;
    }

    /**
     * Retorna o nome de um único fato regra.
     *
     * @param fact String contendo um fato completo.
     * @return nameFact String contendo o nome de um fato.
     */
    public String getNameRule(String fact) {
        String nameFact = "";

        int indexParenthesis = fact.indexOf("(");
        nameFact = fact.substring(0, indexParenthesis);

        return nameFact;
    }

    /**
     * Retorna os argumentos de uma regra passada para a função
     *
     * @param rule A regra completa, com o nome e os argumentos.
     * @return Um argumento da regra recebida é representado em cada índice do
     * vetor.
     */
    public String[] getArgumentsRule(String rule) {
        int idxOpenParenthesis = rule.indexOf("(");
        int idxCloseParenthesis = rule.indexOf(")");
        String[] argumentos = rule.substring(idxOpenParenthesis + 1, idxCloseParenthesis).split(",");
        return argumentos;
    }

    /**
     * Método que cria a estrutura das regras a partir da chave escolhida
     */
    private void constructRules() {
        setTitle("Rule Builder");
        setPreferredSize(new Dimension(660, 330));
        setLocation(250, 100);
        pack();
        factBase2v1 = keyChoice + "(" + nameFactInRule + "Before," + keyChoice.toUpperCase() + ")";
        factBase2v2 = keyChoice + "(" + nameFactInRule + "After," + keyChoice.toUpperCase() + ")";
        factBase2 = factBase2v1 + "," + factBase2v2;

        baseRule = factBase1 + "," + factBase2;

        lblChoiceKey.setVisible(true);
    }

    /**
     * Constroi o painel de regras mineradas
     */
    private void showMinedRules(List<Set> listRules) {
        pnlMining.removeAll();

        pnlGeneratedRules = new JPanel();
        JScrollPane jsPaneWest = new JScrollPane(pnlGeneratedRules);
        jsPaneWest.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsPaneWest.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsPaneWest.setBorder(null);

        GridBagConstraints constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1000);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlMining.add(jsPaneWest, constraints);

        pnlGeneratedRules.setLayout(new BoxLayout(pnlGeneratedRules, BoxLayout.PAGE_AXIS));
        pnlGeneratedRules.setAutoscrolls(true);
        pnlGeneratedRules.removeAll();

        int i = 1;

        for (final Set<String> rules : listRules) {
            JPanel painel = new JPanel();
            FlowLayout flow = new FlowLayout();
            flow.setAlignment(FlowLayout.LEFT);
            painel.setLayout(flow);
            painel.setAlignmentX(LEFT_ALIGNMENT);

            JLabel label = new JLabel("label" + i);
            label.setText(rules.toString().replace("[", "").replace("]", ""));

            final JButton button = new JButton("+");
            button.setSize(15, 15);
            button.setMaximumSize(new Dimension(15, 15));
            button.setMinimumSize(new Dimension(15, 15));
            button.setPreferredSize(new Dimension(15, 15));
            button.setFont(new Font("verdana", 1, 8));
            button.setHorizontalTextPosition(SwingConstants.LEFT);
            button.setVerticalTextPosition(SwingConstants.TOP);
            button.setMargin(new Insets(1, 1, 1, 1));
            button.setName("button" + 1);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pnlRules.removeAll();
                    pnlRules.updateUI();
                    lineRules.removeAll(lineRules);
                    pnlRules.revalidate();
                    pnlRules.updateUI();

                    for (String rule : rules) {
                        LineRule aux = new LineRule();
                        pnlRules.add(aux);

                        aux.getComboOperator().setSelectedItem("!=");
                        aux.getComboOperator().setEnabled(false);

                        aux.getComboTerm1().setSelectedItem(rule + " - v. Before");
                        aux.getComboTerm1().setEnabled(false);

                        aux.getComboTerm2().setSelectedItem(rule + " - v. After");
                        aux.getComboTerm2().setEnabled(false);

                        aux.getBtnAddCondition().setEnabled(false);
                        aux.getComboTerm1().requestFocus();

                        lineRules.add(aux);
                        LineRule.setLinerules(lineRules);
                        pnlRules.revalidate();
                    }

                    LineRule aux = new LineRule();
                    lineRules.add(aux);
                    pnlRules.add(aux);
                    aux.getComboTerm1().requestFocus();
                    LineRule.setPnlRules(pnlRules);
                    pnlRules.revalidate();
                }
            });

            painel.add(button);
            painel.add(label);

            pnlGeneratedRules.add(painel);
            pnlGeneratedRules.updateUI();
            i++;
        }
        pnlGeneratedRules.setAlignmentX(LEFT_ALIGNMENT);

        JPanel btnMineRulesPanel = new JPanel();

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlMining.add(btnMineRulesPanel, constraints);

        JButton btnMineRules = new JButton("Mine Rules");
        btnMineRules.setVisible(true);
        btnMineRules.setEnabled(false);
        btnMineRules.setMinimumSize(new Dimension(350, 25));
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        btnMineRulesPanel.add(btnMineRules, constraints);

        pnlMining.revalidate();
    }

    /**
     * Salva as regras construídas em um arquivo XML
     */
    private void saveProject() {
        //Verifica se é possível salvar o projeto
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "It's necessary to define at least one rule to "
                    + "save the project.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Constroi o XML
        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            // cria o elemento raiz
            Element rootEle = dom.createElement("project");
            rootEle.setAttribute("context-key", keyChoice.toLowerCase());

            // cria o elemento rule
            ArrayList<Rule> rules = rulesModule.getRules();
            for (int i = 0; i < rules.size(); i++) {
                Rule rule = rules.get(i);
                String[] aux = rule.getRule().split(":-");

                //lê o cabeçalho da regra. ex: nomeRegra(SAIDA)
                Element ruleEle = dom.createElement("rule");
                ruleEle.setAttribute("output", rule.getRule().substring(rule.getRule().indexOf("(") + 1, rule.getRule().indexOf(")")));
                ruleEle.setAttribute("enabled", ((JCheckBox) ((JPanel) pResults.getComponents()[i]).getComponents()[0]).isSelected() ? "true" : "false");
                ruleEle.setAttribute("name", aux[0].substring(0, aux[0].indexOf("(")));

                //lê os argumentos da regra e identifica as condições
                for (String s : aux[1].split(",")) {
                    String change;
                    if (s.contains("new_element")) {
                        Element conditionEle = dom.createElement("condition");
                        conditionEle.setAttribute("change", "new_element");
                        continue;
                    } else if (s.contains("deleted_element")) {
                        Element conditionEle = dom.createElement("condition");
                        conditionEle.setAttribute("change", "deleted_element");
                        continue;
                    } else if (s.contains("\\=")) {
                        change = "\\=";
                    } else if (s.contains("<")) {
                        change = "<";
                    } else if (s.contains(">")) {
                        change = ">";
                    } else if (s.contains("==")) {
                        change = "==";
                    } else {
                        continue;
                    }

                    s = s.replace(".", "");

                    Element conditionEle = dom.createElement("condition");
                    conditionEle.setAttribute("change", change);
                    conditionEle.setAttribute("term1", s.substring(0, s.indexOf(change)));
                    conditionEle.setAttribute("term2", s.substring(s.indexOf(change) + change.length(), s.length()));

                    ruleEle.appendChild(conditionEle);
                }

                rootEle.appendChild(ruleEle);
            }

            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                //Solicita o endereço em que o arquivo deverá ser salvo
                JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("xmlproject") + ".XML");//caso exista um histórico com o ultimo caminho acessado, cria um JFileChooser com este caminho
                chooser.setFileFilter(new XMLFileFilter());
                //abre o salvamento do arquivo de projetos
                String pathWay;
                int openedFile = chooser.showSaveDialog(null); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
                if (openedFile == JFileChooser.APPROVE_OPTION) {
                    pathWay = chooser.getSelectedFile().getAbsolutePath();
                    if (!pathWay.endsWith(".xml")) {
                        pathWay += ".xml";
                    }
                    LastpathManager.savelastpath(pathWay, "xmlproject");
                    try {
                        // cria o arquivo XML
                        tr.transform(new DOMSource(dom),
                                new StreamResult(new FileOutputStream(pathWay)));
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(this, "An error occured. Did you selected a valid location?", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    /**
     * Carrega projeto a partir de um arquivo XML
     */
    private void loadProject() throws NoSelectedFileException {
        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("xmlproject") + ".XML");//caso exista um histórico com o ultimo caminho acessado, cria um JFileChooser com este caminho
        chooser.setFileFilter(new XMLFileFilter());//define o filtro de seleção para XML
        String pathWay;
        int openedFile = chooser.showOpenDialog(null);
        if (openedFile == JFileChooser.APPROVE_OPTION) {//caso um arquivo tenha sido selecionado
            pathWay = chooser.getSelectedFile().getAbsolutePath();//atribui o caminho onde o arquivo foi selecionado a variável pathWay
            LastpathManager.savelastpath(pathWay, "xmlproject");

            //Lê o arquivo
            if (pathWay.toUpperCase().endsWith(".XML")) {
                try {
                    rulesModule.clearRules();
                    List<Boolean> enabledList = new ArrayList<Boolean>(); //utilizada para marcar ou não as checkboxes dos resultados

                    File xml = new File(pathWay);
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(xml);

                    doc.getDocumentElement().normalize();

                    NodeList ruleNodeList = doc.getElementsByTagName("rule");

                    for (int i = 0; i < ruleNodeList.getLength(); i++) {

                        Element ruleElement = (Element) ruleNodeList.item(i);

                        //Lê os atributos de cada regra
                        String output = ruleElement.getAttribute("output");
                        String name = ruleElement.getAttribute("name");
                        String enabled = ruleElement.getAttribute("enabled");
                        enabledList.add(enabled.equals("true"));

                        NodeList conditionNodeList = ruleElement.getElementsByTagName("condition");

                        String rule = "";
                        for (int j = 0; j < conditionNodeList.getLength(); j++) {

                            Element conditionElement = (Element) conditionNodeList.item(j);

                            //Lê as condições de cada regra
                            String change = conditionElement.getAttribute("change");
                            String term1 = conditionElement.getAttribute("term1");
                            String term2 = conditionElement.getAttribute("term2");

                            String aux = buildCondition(output.toLowerCase(), term1, change, term2, true);
                            if (rule.equals("")) {
                                rule = aux;
                            } else {
                                rule = rule + "," + aux;
                            }
                        }

                        if (!((Element) conditionNodeList.item(0)).getAttribute("change").contains("_")) {
                            rule = name + "(" + output + "):-" + baseRule + "," + output.toLowerCase() + "(" + nameFactInRule + "Before," + output + ")," + rule + ".";
                        } else {
                            rule = name + "(" + output + "):-" + "" + rule + ".";
                        }

                        rulesModule.addRule(rule);

                        results = formatSetTextPane(rulesModule.getRulesString()); //Formata as regras que serão exibidas na tela
                        System.out.println(results);

                    }

                    String[] partRules = rulesModule.partRules(results); //Pega o cabeçalho das regras (ex: salary(NAME))
                    buildResultsPanel(partRules, enabledList);

                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DOMException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Only XML files are supported.", "Invalid file", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            throw new NoSelectedFileException(); //caso não tenha sido selecionado nenhum arquivo
        }
    }

}

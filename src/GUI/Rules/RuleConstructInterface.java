package GUI.Rules;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import AutomaticRules.WekaParser;
import Documents.Document;
import Exception.NoSelectedFileException;
import GUI.FileManager.LastpathManager;
import GUI.FileManager.LineFile;
import GUI.FileManager.TXTFileFilter;
import GUI.FileManager.XMLFileFilter;
import GUI.Layout.LayoutConstraints;
import GUI.MainInterface.IDocumentsTab;
import GUI.MainInterface.InferenceFileChooser;
import Manager.Manager;
import Rules.Condition;
import Rules.Rule;
import Rules.RulesModule;
import static java.awt.image.ImageObserver.WIDTH;

/**
 * Interface onde o usuário monta as regras para a realização de inferencia
 *
 * @author Guilherme Martins, Marcio Oliveira Junior, Celio H. N. Larcher Junior
 * e Jorge Moreira
 */
public class RuleConstructInterface extends JDialog implements ActionListener {

    //Variáveis utilizadas
    private IDocumentsTab documentsTab;
    private RulesModule rulesModule;
    private JButton btnAtributs;
    private static JPanel pnlConditions; //onde ficam os campos da regra em construção
    private String[] factsPart;
    private String[] namesFacts;
    public String keyChoice; //chave de contexto
    private String nameFactInRule;
    private String factBase2v1, factBase2v2, factBase1v1, factBase1v2;
    private String factBase2, factBase1, baseRule;
    private final JLabel lblChoiceKey;
    private JButton btnSaveRule, btnCancelRule, btnFinishBuilder, btnMineRules, btnFilesList, btnSelection; //Botões do rodapé
    private JButton btnOpen, btnSave, btnExport; //Botões da barra de ferramentas
    private JTextField nameRule;
    private JComboBox comboOutput;
    private JLabel labelRuleName, labelOutput;
    private boolean terminalOpen;
    //Lista que contém todas as linhas de regra
    private ArrayList<LineCondition> lineConditions;
    //Elementos de tela da mineração de regras
    private static JPanel pnlGeneratedRules; //exibe as regras geradas a partir da mineração
    private ArrayList<JCheckBox> checkTagsArray = new ArrayList<JCheckBox>();
    private ArrayList<String> chosenTags = new ArrayList<String>();
    private JComboBox cmbKey; //permite escolher a chave de contexto
    private String results; //regras usadas pelo usuário
    private JPanel allPane, pnlBar, pnlMining, pnlConstructRule, pnlResults, pnlOutput; //paineis principais
    private JPanel pResults; //painel que contem as regras construídas e permite usuário escolher quais regras usar
    private GridBagLayout gridBag;
    private InferenceFileChooser inferenceFileChooser;
    private int selectedRuleIndex; //usado para identificar qual regra estásendo construída/editada (-1 para nova regra)
    private ArrayList<LineFile> lineFiles;
    private List<LineRule> buildedRules;
    private JPanel pnlFiles;
    private final static int MOVE_UP = 1, MOVE_DOWN = -1; //utilizadas para controlar a lista de documentos
    public static boolean succeeded;
    private List<Document> documentsToMine = new ArrayList<Document>();
    private JDialog metricas = new JDialog(this);

    /**
     * Exibe a janela para construção das regras.
     *
     * @param manager Objeto do tipo "Manager" que chamou esta função.
     * @param isSimilarity Booleano que indica se o método escolhido foi
     * "Context Key" ou "Similarity".
     * @param inferenceFileChooser
     * @param documentsTab
     */
    public RuleConstructInterface(Manager manager, boolean isSimilarity, InferenceFileChooser inferenceFileChooser, IDocumentsTab documentsTab) {
        succeeded = false;
        this.documentsTab = documentsTab;
        this.inferenceFileChooser = inferenceFileChooser;
        setModal(true);
        setTitle("Key Attribute");
        rulesModule = manager.getRulesModule();
        lineConditions = new ArrayList<LineCondition>();
        LineCondition.setLineConditions(lineConditions);
        terminalOpen = false;
        pnlConditions = new JPanel(new FlowLayout());
        lblChoiceKey = new JLabel("Select the Key Attribute:");
        cmbKey = new JComboBox<String>();

        btnAtributs = new JButton("List the attributes");
        btnAtributs.addActionListener(this);
        btnSaveRule = new JButton();
        btnSaveRule.addActionListener(this);
        btnCancelRule = new JButton();
        btnCancelRule.addActionListener(this);
        btnFinishBuilder = new JButton();
        btnFinishBuilder.addActionListener(this);

        selectedRuleIndex = -1;

        boolean block_comp = false;
        if (isSimilarity) { //Se o metodo utilizado for o "Similarity"
            factsPart = manager.getSimilarity().get(0).partFacts(manager.getSimilarity().get(0).getFacts());
            namesFacts = manager.getSimilarity().get(0).getNameFacts();
            nameFactInRule = manager.getSimilarity().get(0).getElementName().toUpperCase();

            factBase1v1 = manager.getSimilarity().get(0).getMainFact(factsPart, "before");
            factBase1v2 = manager.getSimilarity().get(0).getMainFact(factsPart, "after");
            keyChoice = "id"; //No modulo "Similarity" a chave de contexto sempre será o atributo ID
        } else { //Se o metodo utilizado for o "Context Key"
            block_comp = true;
            factsPart = manager.getContextKey().get(0).partFacts(manager.getContextKey().get(0).getFacts());
            List<String> listNameFacts = new WekaParser().getTags(this.documentsTab.getDocuments().getPathWays().get(this.documentsTab.getRightCBIndex()));
            nameFactInRule = listNameFacts.get(0).toUpperCase();
            listNameFacts.remove(0);
            namesFacts = listNameFacts.toArray(new String[listNameFacts.size()]);
            factBase1v1 = manager.getContextKey().get(0).getMainFact(factsPart, "before");
            factBase1v2 = manager.getContextKey().get(0).getMainFact(factsPart, "after");

            keyChoice = listNameFacts.get(0);

            //Inicialização e configuração da Combobox da Context Key            
            cmbKey = new JComboBox(listNameFacts.toArray());
            cmbKey.setSelectedIndex(-1);
            cmbKey.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        keyChoice = cmbKey.getSelectedItem().toString();
                        //Reconstroi as regras
                        constructRules();
                        //Ativa todos os componentes
                        setAllEnabled(allPane, true);
                    }
                }
            });
        }

        LineCondition.setNamesFacts(namesFacts);
        factBase1 = factBase1v1 + "," + factBase1v2;
        //Constroi as regras
        constructRules();

        //Constroi a interface
        buildInterface(block_comp);
    }

    private enum Button {

        btnSaveRule, btnCancelRule, btnFinishBuilder, btnMineRules, btnFilesList, btnOpen, btnSave, btnExport;
    }

    /**
     * Trata as ações dos botões e das seleções realizadas pelo usuário.
     *
     * @param e Recebe um evento gerado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSaveRule) { //Valida as opções de selecionadas na construção da regra e a adiciona ao conjunto de regras
            finishRuleAndCreateNew();
        } else if(e.getSource() == btnSelection){
            if(btnSelection.getText().equals("Select All")){//se estiver como select all
                selectAll();
            }else{
                unSelectAll();
            }
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
            mineRules();
        } else if (e.getSource() == btnFilesList) {
            showFilesList();
        } else if (e.getSource() == btnExport) {
            exportProlog();
        } else if (e.getSource() == btnCancelRule) {
            cancelRule();
        } else {
            btnSelection.removeActionListener(this);
            btnSelection.setText("Unselect All");
            btnSelection.addActionListener(this);
            for (int i = 0; i < pResults.getComponentCount(); i++) { //percorre a lista de checkboxes, se um estiver deselecionado, troca o botão para selecionar todos
            JCheckBox item = (JCheckBox) ((JPanel) pResults.getComponents()[i]).getComponents()[0];
                if(!item.isSelected()){
                    btnSelection.removeActionListener(this);
                    btnSelection.setText("Select All");
                    btnSelection.addActionListener(this);
                    break;
                }
            }
        }
    }
    
    /**
     * Seleciona todos os checboxes
     */
    public void selectAll(){
        for (int i = 0; i < pResults.getComponentCount(); i++) { //Verifica quais regras foram selecionadas pelo usuário
            JCheckBox item = (JCheckBox) ((JPanel) pResults.getComponents()[i]).getComponents()[0];
            item.removeActionListener(this);
            item.setSelected(true);
            item.addActionListener(this);
        }
        btnSelection.removeActionListener(this);
        btnSelection.setText("Unselect All");
        btnSelection.addActionListener(this);
    }
    
    /**
     * Deseleciona todos os checkboxes
     */
    public void unSelectAll(){
        for (int i = 0; i < pResults.getComponentCount(); i++) { //Verifica quais regras foram selecionadas pelo usuário
            JCheckBox item = (JCheckBox) ((JPanel) pResults.getComponents()[i]).getComponents()[0];
            item.removeActionListener(this);
            item.setSelected(false);
            item.addActionListener(this);
        }
        btnSelection.removeActionListener(this);
        btnSelection.setText("Select All");
        btnSelection.addActionListener(this);
    }
    
    /**
     * Minera as regras com base nas tags e nos documentos selecionados
     */
    private void mineRules() {
        //Identifica as tags a serem mineradas
        chosenTags.clear();
        for (JCheckBox item : checkTagsArray) {
            if (item.isSelected()) {
                chosenTags.add(item.getName());
            }
        }

        //Seleciona os documentos a minerar
        for (LineFile lf : lineFiles) {
            if (lf.getCheckbox().isSelected()) {
                documentsToMine.add(lf.getDocument());
            }
        }

        //Verifica se ao menos 2 documetnos foram minrerados
        if (documentsToMine.size() < 2) {
            JOptionPane.showMessageDialog(this, "Please select at least two documents to mine rules", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        //Pega do usuario as metricas desejadas
        //JFrame metricas = new JFrame("Metrics Values");        
    	Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();
        dim.setSize(250,210);
        metricas.setMinimumSize(dim);
        metricas.setLocationRelativeTo(null);
            	
        JPanel tudo = new JPanel();        
        GridLayout gridBag = new GridLayout(4,1);
        tudo.setLayout(gridBag);
        metricas.add(tudo);
        tudo.setSize(50, 50);
        
        JLabel support = new JLabel("Minimum Support: ");
        JLabel metric = new JLabel("Metric:                      ");
        JLabel metValue = new JLabel("Metric Value:          ");
        
        final JTextField valueSup = new JTextField(10);
        final JTextField valueMet = new JTextField(10);
        
        valueSup.setText("0.0");
        valueMet.setText("0.0");
        
        final JComboBox<String> combo = new JComboBox<String>();
        combo.addItem("Confidence");
        combo.addItem("Lift");
        combo.addItem("Leverage");
        combo.addItem("Conviction");
        
        combo.setPreferredSize(new Dimension(112,25));
        
       
        
        JPanel vSup = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel met = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel vMet = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        vSup.add(support);
        vSup.add(valueSup);
        
        met.add(metric);
        met.add(combo);
        
        vMet.add(metValue);
        vMet.add(valueMet);
        
        tudo.add(vSup);
        tudo.add(met);
        tudo.add(vMet);
        
        JButton botao = new JButton("Mine Rules");
        botao.setEnabled(true);
        botao.setPreferredSize(new Dimension(100, 25));        
        bot.add(botao);
        tudo.add(bot);
        metricas.setVisible(true);  
        metricas.repaint();
                
        botao.addActionListener(new ActionListener() {
	        
        	public void actionPerformed(ActionEvent e) {	            
        		//Gera as regras e as mostra
                List<Set<String>> listRules = new WekaParser().generateRules(documentsToMine, chosenTags, keyChoice, Float.parseFloat(valueSup.getText()), Float.parseFloat(valueMet.getText()), combo.getSelectedIndex());
                showMinedRules(listRules);
                metricas.setVisible(false);
	        }
	    }); 
    }

    /**
     * Mostra a liata de documentos adicionados
     */
    private void showFilesList() {
        pnlMining.removeAll();
        pnlMining.setBorder(javax.swing.BorderFactory.createTitledBorder("Files to mine"));

        GridBagConstraints constraints = new GridBagConstraints();

        //Cria um painel para a lista de LineFile e o coloca em um painel rolável
        pnlFiles = new JPanel();
        JScrollPane jsPane = new JScrollPane(pnlFiles);
        jsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsPane.setBorder(null);

        pnlFiles.setLayout(new BoxLayout(pnlFiles, BoxLayout.PAGE_AXIS));

        //Constroi uma LineFile para cada documento
        lineFiles = new ArrayList<LineFile>();
        for (int i = 0; i < documentsTab.getDocuments().getSize(); i++) {
            final LineFile lineFile = new LineFile(documentsTab.getDocuments().getDocuments().get(i));
            //Pré-seleciona os dois primeiros arquivos
            if (i < 2) {
                lineFile.getCheckbox().setSelected(true);
            }

            lineFile.getButtonUp().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    moveLineFile((LineFile) lineFile.getButtonUp().getParent(), MOVE_UP);
                }
            });
            lineFile.getButtonDown().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    moveLineFile((LineFile) lineFile.getButtonUp().getParent(), MOVE_DOWN);
                }
            });

            pnlFiles.add(lineFile);
            lineFiles.add(lineFile);
        }

        //Desativa alguns botões de ordenação nos primeiro e último arquivos
        lineFiles.get(0).getButtonUp().setEnabled(false);
        lineFiles.get(lineFiles.size() - 1).getButtonDown().setEnabled((false));

        //Adiciona o painel com os arquivos ao painel esquerdo
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

    /**
     * Método para ordenar a lista de documentos adicionados
     *
     * @param caller {@link LineFile} a ser ordenado
     * @param action {@link RuleConstructInterface.MOVE_UP} para mover para cima
     * ou {@link RuleConstructInterface.MOVE_DOWN} para mover para baixo
     */
    private void moveLineFile(LineFile caller, int action) {
        int callerIndex = lineFiles.indexOf(caller);
        Document currentDoc = documentsTab.getDocuments().getDocuments().get(callerIndex);

        switch (action) {
            case MOVE_UP:
                //Verifica se vai para a primeira posição
                caller.getButtonUp().setEnabled(callerIndex - 1 > 0);
                caller.getButtonDown().setEnabled(true);

                //Troca posições na lista de LineFiles
                LineFile before = lineFiles.get(callerIndex - 1);
                lineFiles.set(callerIndex - 1, caller);
                lineFiles.set(callerIndex, before);

                //Verifica se o anterior está indo para a primeira posição
                before.getButtonDown().setEnabled(callerIndex + 1 < lineFiles.size() - 1);
                before.getButtonUp().setEnabled(true);

                //Troca posição dos Documents
                Document beforeDoc = documentsTab.getDocuments().getDocuments().get(callerIndex - 1);
                documentsTab.getDocuments().getDocuments().set(callerIndex - 1, currentDoc);
                documentsTab.getDocuments().getDocuments().set(callerIndex, beforeDoc);
                break;
            case MOVE_DOWN:
                //Verifica se vai para a última posição
                caller.getButtonDown().setEnabled(callerIndex + 1 < lineFiles.size() - 1);
                caller.getButtonUp().setEnabled(true);

                //Troca posições na lista de LineFiles
                LineFile after = lineFiles.get(callerIndex + 1);
                lineFiles.set(callerIndex + 1, caller);
                lineFiles.set(callerIndex, after);

                //Verifica se vai para a primeira posição
                after.getButtonUp().setEnabled(callerIndex - 1 > 0);
                after.getButtonDown().setEnabled(true);

                //Troca posição dos Documents                
                Document afterDoc = documentsTab.getDocuments().getDocuments().get(callerIndex + 1);
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
    private void finishRuleAndCreateNew() {
        //Verifica se o usuário informou o nome da regra
        if (nameRule.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "It's necessary give the rule a name", "Error", JOptionPane.ERROR_MESSAGE);
            nameRule.requestFocus();
        } else if (comboOutput.getSelectedItem().equals("")) {
            JOptionPane.showMessageDialog(this, "It's necessary choose output type", "Error", JOptionPane.ERROR_MESSAGE);
            comboOutput.requestFocus();
        } else {
            //Valida cada condição da regra. Caso encontre uma inválida, avisa o usuário
            List<Condition> conditions = new ArrayList<Condition>();
            lineConditions = LineCondition.getLineConditions();
            LineCondition.setLineConditions(lineConditions);
            for (int i = 0; i < lineConditions.size(); i++) {
                Condition condition = new Condition(lineConditions.get(i));
                if ((!condition.getFirstTerm().equals("") && !condition.getSecondTerm().equals("") && !condition.getOperator().equals("")) || (condition.getOperator().equals("new_element") || (condition.getOperator().equals("deleted_element")))) {
                    conditions.add(condition);
                } else {
                    
                   //JOptionPane.showMessageDialog(this, "Your " + (i + 1) + "º condition is not valid.", "Invalid condition encoutered", JOptionPane.ERROR_MESSAGE);
                   //return;
                    
                }
            }

            //COnstroi a regra
            String regraConst = "";
            for (Condition condition : conditions) {
                System.out.println('"'+comboOutput.getSelectedItem().toString()+'"');
                String aux = buildCondition(comboOutput.getSelectedItem().toString(), condition.getFirstTerm(), condition.getOperator(), condition.getSecondTerm(), false);
                if (regraConst.equals("")) {
                    regraConst = aux;
                } else {
                    regraConst = regraConst + "," + aux;
                }

            }
            String ruleName = nameRule.getText().toLowerCase().replaceAll(" ", "");

            //Prepara as regras construídas
            if (lineConditions.get(0).getComboOperator().getSelectedItem().toString().indexOf("_") < 0) {
                regraConst = ruleName + "(" + comboOutput.getSelectedItem().toString().toUpperCase() + "):-" + baseRule + "," + comboOutput.getSelectedItem().toString() + "(" + nameFactInRule + "Before," + comboOutput.getSelectedItem().toString().toUpperCase() + ")," + regraConst + ".";
            } else {
                regraConst = ruleName + "(" + comboOutput.getSelectedItem().toString().toUpperCase() + "):-" + "" + regraConst + ".";
            }

            Rule rule = new Rule(ruleName, comboOutput.getSelectedItem().toString().toLowerCase(), conditions, regraConst);
            if (!rulesModule.checkExists(rule, selectedRuleIndex)) {
                if (selectedRuleIndex == -1) {
                    rulesModule.addRule(rule);
                } else {
                    rulesModule.getRules().set(selectedRuleIndex, rule);
                    selectedRuleIndex = -1;
                }

                results = formatSetTextPane(rulesModule.getRulesString()); //Formata as regras que serão exibidas na tela

                if (!results.isEmpty()) {
                    String[] partRules = rulesModule.partRules(results); //Pega o cabeçalho das regras (ex: salary(NAME))
                    buildRulesPanel(partRules, null);

                    //"Limpa" o construtor
                    comboOutput.setSelectedItem("");
                    nameRule.setText("");

                    btnSaveRule.setEnabled(true);
                    pnlConditions.removeAll();
                    lineConditions.clear();
                    LineCondition aux = new LineCondition();
                    lineConditions.add(aux);
                    pnlConditions.add(aux);
                    aux.getComboTerm1().requestFocus();
                    pnlConditions.revalidate();

                } else {
                    JOptionPane.showMessageDialog(this, "It's necessary to difine the rules to "
                            + "realize inference of informations.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "This RULE already exists", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    /**
     * Encerra o construtor
     */
    private void finish() {
        ArrayList<String> selectedRules = new ArrayList<String>();
        for (int i = 0; i < pResults.getComponentCount(); i++) { //Verifica quais regras foram selecionadas pelo usuário
            JCheckBox item = (JCheckBox) ((JPanel) pResults.getComponents()[i]).getComponents()[0];
            if (item.isSelected()) {
                selectedRules.add(item.getName());
            }
        }
        if (selectedRules.size() > 0) {
            this.rulesModule.addSelectRules(selectedRules); //Adiciona as regras selecionadas em sua respectiva variável
            this.inferenceFileChooser.setSelectedRules(selectedRules);//Envia ao InferenceFileChooser a lista de regras selecionadas
            succeeded = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "It's necessary to define at least one rule to "
                    + "realize inference of informations.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Atualiza a lista de regras construidas
     */
    private void buildRulesPanel(String[] partRules, List<Boolean> enabledList) {
        //"Limpa" o painel de regras construídas
        pResults.removeAll();
        List<LineRule> newBuiledRules = new ArrayList<LineRule>();
        List<Rule> selectedRules = rulesModule.getSelectRules();

        String[] rulesHeads = rulesModule.getNameAndArgumentsRules(partRules);
        //Cria uma LineRule para cada regra
        for (int i = 0; i < rulesHeads.length; i++) {
            final LineRule pnlRule = new LineRule(rulesHeads[i], false);

            JCheckBox chk = pnlRule.getCheckbox();
            chk.setName(rulesHeads[i]);
            chk.addActionListener(this);
            if (enabledList != null) {
                chk.setSelected(enabledList.get(i));
            } else if (buildedRules != null) {
                if (i < buildedRules.size()) {
                    chk.setSelected(buildedRules.get(i).getCheckbox().isSelected());
                }
            } else {
                for (Rule selectedRule : selectedRules) {
                    if (selectedRule.getName().equals(rulesHeads[i].substring(0, rulesHeads[i].indexOf("(")))) {
                        chk.setSelected(true);
                        break;
                    }
                }
            }

            final int index = i;
            pnlRule.getLabel().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    editRule(index);
                }
            });

            pnlRule.getButtonRemove().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pResults.remove(pnlRule);
                    pResults.updateUI();
                    rulesModule.removeRule(index);

                    if (index == selectedRuleIndex) {
                        nameRule.setText("");
                        comboOutput.setSelectedIndex(0);
                        pnlConditions.removeAll();
                        pnlConditions.updateUI();
                    }
                }
            });

            pResults.add(pnlRule);
            newBuiledRules.add(pnlRule);
        }

        buildedRules = newBuiledRules;
        btnFinishBuilder.setEnabled(pResults.getComponentCount() > 0);
        pResults.revalidate();
    }

    /**
     * Exibe a regra selecionada no construtor
     *
     * @param index Índice da {@link Rule}
     */
    private void editRule(int index) {
        selectedRuleIndex = index;
        lineConditions.clear();

        btnSaveRule.setText("Update rule");
        btnCancelRule.setText("Cancel");

        Rule rule = rulesModule.getRules().get(index);

        comboOutput.setSelectedItem(rule.getOutput().toLowerCase());
        nameRule.setText(rule.getName());

        //lê cada condição
        for (Condition condition : rule.getConditions()) {
            LineCondition lineCondition = new LineCondition();
            lineCondition.getComboTerm1().setSelectedItem(condition.getFirstTerm());
            lineCondition.getComboOperator().setSelectedItem(condition.getOperator());
            lineCondition.getComboTerm2().setSelectedItem(condition.getSecondTerm());

            lineConditions.add(lineCondition);
            LineCondition.setLineConditions(lineConditions);
            pnlConditions.revalidate();
        }

    }

    /**
     * Método para cancelar a construção da regra
     */
    private void cancelRule() {
        lineConditions = new ArrayList<LineCondition>();
        lineConditions.add(new LineCondition());
        LineCondition.setLineConditions(lineConditions);

        btnSaveRule.setText("Save rule");
        btnCancelRule.setText("Clear");

        comboOutput.setSelectedItem("");
        nameRule.setText("");

        selectedRuleIndex = -1;
    }

    /**
     * Exibe a interface de construção de regras.
     * 
     */
    private void buildInterface(boolean block_comp) {
        allPane = new JPanel();
        this.setMinimumSize(new Dimension(1200, 500));
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
        pnlMining.setBorder(javax.swing.BorderFactory.createTitledBorder("Tags to mine"));

        pnlResults = new JPanel();
        pnlResults.setBorder(javax.swing.BorderFactory.createTitledBorder("Builded Rules"));

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

        pnlConditions = new JPanel();

        JScrollPane jsPaneCentral = new JScrollPane(pnlConditions);
        jsPaneCentral.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsPaneCentral.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsPaneCentral.setBorder(null);

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1000);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlConstructRule.add(jsPaneCentral, constraints);

        JPanel buttonsPanel = new JPanel();

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlConstructRule.add(buttonsPanel, constraints);

        btnSaveRule.setText("Save rule");
        btnSaveRule.setMinimumSize(new Dimension(350, 25));
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        buttonsPanel.add(btnSaveRule, constraints);

        btnCancelRule.setText("Clear");
        btnCancelRule.setMinimumSize(new Dimension(350, 25));
        LayoutConstraints.setConstraints(constraints, 1, 0, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        buttonsPanel.add(btnCancelRule, constraints);

        LineCondition firstConditionRule = new LineCondition();
        pnlConditions.setLayout(new BoxLayout(pnlConditions, BoxLayout.PAGE_AXIS));
        pnlConditions.add(firstConditionRule);
        LineCondition.setPnlConditions(pnlConditions);
        lineConditions.add(firstConditionRule);

        //declara objetos de controle do layout do painel da esquerda (regras de associação)
        GridBagLayout gridBagLeft = new GridBagLayout();
        pnlMining.setLayout(gridBagLeft);

        JPanel pnlTags = new JPanel();
        JScrollPane jsPaneLeft = new JScrollPane(pnlTags);
        jsPaneLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsPaneLeft.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsPaneLeft.setBorder(null);

        pnlTags.setLayout(new BoxLayout(pnlTags, BoxLayout.PAGE_AXIS));

        //Painel com as tags no painel "Association Rules"
        //Recupera a lista de tags a partir do segundo arquivo carregado no projeto
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
            pnlTags.add(chkItem);
        }

        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1000);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTH;
        gridBag.setConstraints(this, constraints);
        pnlMining.add(jsPaneLeft, constraints);

        JPanel btnFileListPanel = new JPanel();

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlMining.add(btnFileListPanel, constraints);

        //Botão para escolher os arquivos a serem minerados e sua ordem
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

        btnSelection = new JButton("Select All");
        btnSelection.setVisible(true);
        btnSelection.setEnabled(false);
        btnSelection.setPreferredSize(new Dimension(110,26));
        btnSelection.addActionListener(this);
        
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        btnFinishBuilderPanel.add(btnSelection, constraints);
        
        btnFinishBuilder = new JButton("Show results");
        btnFinishBuilder.setVisible(true);
        btnFinishBuilder.setEnabled(false);
        btnFinishBuilder.setPreferredSize(new Dimension(110,26));
        btnFinishBuilder.addActionListener(this);

        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        btnFinishBuilderPanel.add(btnFinishBuilder, constraints);

        results = formatSetTextPane(rulesModule.getRulesString()); //Formata as regras que serão exibidas na tela

        if (!results.isEmpty()) {
            String[] partRules = rulesModule.partRules(results); //Pega o cabeçalho das regras (ex: salary(NAME))
            buildRulesPanel(partRules, null);
        }

        //Desabilita todos os JComponents criados até agora
        setAllEnabled(allPane, !block_comp);

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

        setVisible(true);
        pResults.setVisible(true);
    }

    private void setAllEnabled(JComponent comp, boolean enabled) {
        if (comp.getComponents().length == 0) {
            return;
        }
        //Desabilita todos os JComponents filhos deste e de seus filhos
        for (Component c : comp.getComponents()) {
            c.setEnabled(enabled);
            try {
                setAllEnabled((JComponent) c, enabled);
            } catch (ClassCastException e) {
                //Se não é um JComponent, este não possui "filhos"
                return;
            }
        }
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

            } else if (operator.equals("\\=") || operator.equals("!=")) {
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
    private void showMinedRules(List<Set<String>> listTags) {
        pnlMining.removeAll();
        pnlMining.setBorder(javax.swing.BorderFactory.createTitledBorder("Mined rules"));

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

        for (final Set<String> tags : listTags) {
            final LineRule pnlMinedRule = new LineRule(tags.toString().replace("[", "").replace("]", ""), true);

            pnlMinedRule.getLabel().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    pnlConditions.removeAll();
                    pnlConditions.updateUI();
                    lineConditions.removeAll(lineConditions);
                    pnlConditions.revalidate();
                    pnlConditions.updateUI();
                  
                    for (String tag : tags) {
                     
                        
                        LineCondition aux = new LineCondition();
                        pnlConditions.add(aux);
                       
                        if(tag.contains("↑")){
                            aux.getComboOperator().setSelectedItem("<");
                            String str = "↑";
                            int l =(tag.length() - str.length())-1;
                            tag = (tag.substring(0,l));
                            
                    }
                        else{
                            if(tag.contains("↓")){
                                aux.getComboOperator().setSelectedItem(">");
                                String str = "↓";
                                int l =(tag.length() - str.length())-1;
                                tag = (tag.substring(0,l));
                            
                            }
                            else{
                        aux.getComboOperator().setSelectedItem("!=");
                            }
                    }
                        aux.getComboOperator().setEnabled(false);

                        aux.getComboTerm1().setSelectedItem(tag + " - v. Before");
                        aux.getComboTerm1().setEnabled(false);

                        aux.getComboTerm2().setSelectedItem(tag+ " - v. After");
                        aux.getComboTerm2().setEnabled(false);

                        aux.getBtnAddCondition().setEnabled(false);
                        aux.getComboTerm1().requestFocus();

                        lineConditions.add(aux);
                        LineCondition.setLineConditions(lineConditions);
                        pnlConditions.revalidate();
                    }

                    LineCondition aux = new LineCondition();
                    lineConditions.add(aux);
                    pnlConditions.add(aux);
                    aux.getComboTerm1().requestFocus();
                    LineCondition.setPnlConditions(pnlConditions);
                    pnlConditions.revalidate();
                }
            });

            pnlMinedRule.getButtonRemove().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pnlGeneratedRules.remove(pnlMinedRule);
                    pnlGeneratedRules.updateUI();
                }
            });

            pnlGeneratedRules.add(pnlMinedRule);
            pnlGeneratedRules.updateUI();
        }

        JPanel btnMineRulesPanel = new JPanel();

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlMining.add(btnMineRulesPanel, constraints);

        btnMineRules = new JButton("Mine Rules");
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
        org.w3c.dom.Document dom;
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

                //lê o cabeçalho da regra. ex: nomeRegra(SAIDA)
                Element ruleEle = dom.createElement("rule");
                ruleEle.setAttribute("output", rule.getOutput());
                ruleEle.setAttribute("enabled", ((JCheckBox) ((JPanel) pResults.getComponents()[i]).getComponents()[0]).isSelected() ? "true" : "false");
                ruleEle.setAttribute("name", rule.getName());

                for (Condition condition : rule.getConditions()) {
                    Element conditionEle = dom.createElement("condition");
                    conditionEle.setAttribute("change", condition.getOperator());
                    if (!condition.getOperator().equals("new_element") && !condition.getOperator().equals("deleted_element")) {
                        conditionEle.setAttribute("term1", condition.getFirstTerm());
                        conditionEle.setAttribute("term2", condition.getSecondTerm());

                    }

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
                    org.w3c.dom.Document doc = dBuilder.parse(xml);

                    doc.getDocumentElement().normalize();

                    NodeList ruleNodeList = doc.getElementsByTagName("rule");

                    for (int i = 0; i < ruleNodeList.getLength(); i++) {

                        Element ruleElement = (Element) ruleNodeList.item(i);

                        //Lê os atributos de cada regra
                        String output = ruleElement.getAttribute("output").toUpperCase();
                        String name = ruleElement.getAttribute("name");
                        String enabled = ruleElement.getAttribute("enabled");
                        enabledList.add(enabled.equals("true"));

                        NodeList conditionNodeList = ruleElement.getElementsByTagName("condition");
                        List<Condition> conditions = new ArrayList<Condition>();

                        String rule = "";
                        for (int j = 0; j < conditionNodeList.getLength(); j++) {

                            Element conditionElement = (Element) conditionNodeList.item(j);

                            //Lê as condições de cada regra
                            String change = conditionElement.getAttribute("change");
                            String term1 = conditionElement.getAttribute("term1");
                            String term2 = conditionElement.getAttribute("term2");
                            conditions.add(new Condition(term1, term2, change));

                            if (change.equals("!=")) {
                                change = "\\=";
                            }

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

                        rulesModule.addRule(new Rule(name, output, conditions, rule));
                        results = formatSetTextPane(rulesModule.getRulesString()); //Formata as regras que serão exibidas na tela

                    }

                    String[] partRules = rulesModule.partRules(results); //Pega o cabeçalho das regras (ex: salary(NAME))
                    buildRulesPanel(partRules, enabledList);

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

    /**
     * Exporta as regras construídas para Prolog
     */
    public void exportProlog() {
        //Solicita o endereço em que o arquivo deverá ser salvo
        JFileChooser chooser = new JFileChooser(LastpathManager.getlastpath("txt") + ".TXT");//caso exista um histórico com o ultimo caminho acessado, cria um JFileChooser com este caminho
        chooser.setFileFilter(new TXTFileFilter());
        //abre o salvamento do arquivo de projetos
        String pathWay;
        int openedFile = chooser.showSaveDialog(null); // showSaveDialog retorna um inteiro , e ele ira determinar que o chooser será para salvar.
        if (openedFile == JFileChooser.APPROVE_OPTION) {
            pathWay = chooser.getSelectedFile().getAbsolutePath();
            if (!pathWay.endsWith(".txt")) {
                pathWay += ".txt";
            }
            LastpathManager.savelastpath(pathWay, "txt");

            try {
                FileWriter fw = new FileWriter(new File(pathWay).getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                results = results.replaceAll("\n", System.getProperty("line.separator"));
                bw.write(results);
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(RuleConstructInterface.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}

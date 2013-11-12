package GUI.Rules;

import GUI.Layout.LayoutConstraints;
import Manager.Manager;
import Rules.RulesModule;
import AutomaticRules.WekaParser;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import weka.associations.Item;

/**
 * Interface onde o usuário monta as regras para a realização de inferencia
 *
 * @author Guilherme Martins, Marcio Oliveira Junior e Celio H. N. Larcher
 * Junior
 */
public class RuleConstructInterface extends JDialog implements ActionListener {

    //Variáveis utilizadas
    private RulesModule rulesModule;
    private JButton btnAtributs;
    private JPanel pnlAll;
    private final JPanel pnBotton;
    private final JPanel pnlUp;
    private final JPanel pnlCenter;
    private static JPanel pnlRules;
    private String[] factsPart;
    private String[] namesFacts;
    private final JButton btnNext;
    private ArrayList<JRadioButton> factsKey;
    public String keyChoice;
    private String nameFactInRule;
    private String factBase2v1, factBase2v2, factBase1v1, factBase1v2;
    private String factBase2, factBase1, baseRule;

    private final JLabel lblChoiceKey;

    private JButton btnFinish, btnAddRule;
    private JTextField nameRule;
    private JComboBox comboExit;
    private JLabel jLabel1, jLabel2;
    private boolean terminalOpen;
    private JScrollPane pn;
    //Lista que contém todas as linhas de regra
    ArrayList<LineRule> linerules;

    //Elementos de tela da mineração de regras
    private JButton btnMiningRules;
    private static JPanel pnlWest;
    private JScrollPane pnWestScroll;

    /**
     * Exibe a janela para construção das regras.
     *
     * @param manager Objeto do tipo "Manager" que chamou esta função.
     * @param isSimilarity Booleano que indica se o método escolhido foi
     * "Context Key" ou "Similarity".
     */
    public RuleConstructInterface(Manager manager, boolean isSimilarity) {
        setModal(true);
        setTitle("Key Attribute");
        this.rulesModule = manager.getRulesModule();
        linerules = new ArrayList<LineRule>();
        LineRule.setLinerules(linerules);
        pnlAll = new JPanel(new BorderLayout());
        terminalOpen = false;
        btnNext = new JButton("Continue");
        btnNext.addActionListener(this);
        btnNext.setVisible(false);
        pnBotton = new JPanel(new FlowLayout());
        pnlUp = new JPanel(new FlowLayout());
        pnlCenter = new JPanel(new FlowLayout());
        pnlWest = new JPanel(new FlowLayout());
        pnlRules = new JPanel(new FlowLayout());
        lblChoiceKey = new JLabel("Select the Key Attribute:");

        btnAtributs = new JButton("List the attributes");
        btnAtributs.addActionListener(this);
        btnFinish = new JButton();
        btnFinish.addActionListener(this);
        btnAddRule = new JButton();
        btnAddRule.addActionListener(this);
        btnMiningRules = new JButton();
        btnMiningRules.addActionListener(this);
        pnlUp.add(btnAtributs);
        pnlUp.add(lblChoiceKey);
        pnBotton.add(btnNext);
        btnNext.setVisible(false);
        pn = new JScrollPane(pnlCenter, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pn.setPreferredSize(new Dimension(250, 300));
        pnWestScroll = new JScrollPane(pnlWest, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnWestScroll.setPreferredSize(new Dimension(250, 300));
        pnlAll.add(pnlUp, BorderLayout.NORTH);
        pnlAll.add(pn, BorderLayout.CENTER);
        pnlAll.add(pn, BorderLayout.WEST);
        pnlAll.add(pnBotton, BorderLayout.SOUTH);

        this.add(pnlAll, "Center");
        this.setMinimumSize(new Dimension(160, 200));
        setResizable(true);
        setAlwaysOnTop(false);
        pack();
        setLocation(400, 100);

        if (isSimilarity) { //Se o metodo utilizado for o "Similarity"
            factsPart = manager.getSimilarity().get(0).partFacts(manager.getSimilarity().get(0).getFacts());
            namesFacts = manager.getSimilarity().get(0).getNameFacts();
            nameFactInRule = manager.getSimilarity().get(0).getElementName().toUpperCase();

            factBase1v1 = manager.getSimilarity().get(0).getMainFact(factsPart, "before");
            factBase1v2 = manager.getSimilarity().get(0).getMainFact(factsPart, "after");
        } else { //Se o metodo utilizado for o "Context Key"
            factsPart = manager.getContextKey().get(0).partFacts(manager.getContextKey().get(0).getFacts());
            namesFacts = manager.getContextKey().get(0).getNameFacts();
            nameFactInRule = manager.getContextKey().get(0).getElementName().toUpperCase();

            factBase1v1 = manager.getContextKey().get(0).getMainFact(factsPart, "before");
            factBase1v2 = manager.getContextKey().get(0).getMainFact(factsPart, "after");
        }
        LineRule.setNamesFacts(namesFacts);
        namesFacts = removeFactsRepeated(namesFacts);
        factBase1 = factBase1v1 + "," + factBase1v2;

        pnlCenter.removeAll(); //Limpa o painel central

        setIdentifyFacts(namesFacts);//Gera os radioButtons

        if (isSimilarity) { //Se modulo "Similarity" estiver ativo
            setVisible(false); //Oculta a tela onde se permite que escolha a chave de contexto
            keyChoice = "id"; //No modulo "Similarity" a chave de contexto sempre será o atributo ID
            constructRules(); //Constroi as regras com ID como chave de contexto
        } else {
            setVisible(true);
        } //Senão chama a tela onde é permitido escolher a chave de contexto
    }

    /**
     * Trata as ações dos botões e das seleções realizadas pelo usuário.
     *
     * @param e Recebe um evento gerado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int cont = 0;
        if (e.getSource() == btnAtributs) {
        } else if (e.getSource() == btnNext) {
            for (JRadioButton item : factsKey) { //Obtêm o atributo chave de contexto selecionado
                if (item.isSelected()) {
                    cont++;
                    keyChoice = item.getName();
                }
            }
            if (cont > 0) {
                constructRules();
                pn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            } else {
                JOptionPane.showMessageDialog(this, "It's necessary choose the id context key", "Error", JOptionPane.ERROR_MESSAGE);
            }
            //Sobrepõe tela de escolha do atributo chave (radio buttons) pela tela com o terminal de construção de regras
        } else if (e.getSource() == btnFinish) {
            if (nameRule.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "It's necessary give the rule a name", "Error", JOptionPane.ERROR_MESSAGE);
                nameRule.requestFocus();
            }
            if (comboExit.getSelectedItem().equals("")) {
                JOptionPane.showMessageDialog(this, "It's necessary choose output type", "Error", JOptionPane.ERROR_MESSAGE);
                comboExit.requestFocus();
            } else {
                Iterator iter = linerules.iterator();
                LineRule.setLinerules(linerules);
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
                    for (Iterator it = linerules.iterator(); it.hasNext();) {
                        LineRule condition = (LineRule) it.next();
                        if ((!condition.getComboTerm1().getSelectedItem().toString().equals("") && !condition.getComboTerm2().getSelectedItem().toString().equals("")) || (condition.getComboOperator().getSelectedItem().toString().equals("new_element") || (condition.getComboOperator().getSelectedItem().toString().equals("deleted_element")))) {
                            String aux = buildCondition(comboExit.getSelectedItem().toString(), condition.getComboTerm1().getSelectedItem().toString(), condition.getComboOperator().getSelectedItem().toString(), condition.getComboTerm2().getSelectedItem().toString());
                            if (regraConst.equals("")) {
                                regraConst = aux;
                            } else {
                                regraConst = regraConst + "," + aux;
                            }
                        }
                    }
                    //Adiciona as regras construídas
                    if (linerules.get(0).getComboOperator().getSelectedItem().toString().indexOf("_") < 0) {
                        regraConst = nameRule.getText().toLowerCase() + "(" + comboExit.getSelectedItem().toString().toUpperCase() + "):-" + baseRule + "," + comboExit.getSelectedItem().toString() + "(" + nameFactInRule + "Before," + comboExit.getSelectedItem().toString().toUpperCase() + ")," + regraConst + ".";
                        rulesModule.addRules(regraConst);
                    } else {
                        regraConst = nameRule.getText().toLowerCase() + "(" + comboExit.getSelectedItem().toString().toUpperCase() + "):-" + "" + regraConst + ".";
                        rulesModule.addRules(regraConst);
                    }
                    dispose();
                }
            }
        } else if (e.getSource() == btnAddRule) { //Valida as opções de selecionadas na construção da regra e a adiciona ao conjunto de regras
            if (nameRule.getText().isEmpty()) { //Caso falte o "nome" da regra
                JOptionPane.showMessageDialog(this, "It's necessary give the rule a name", "Error", JOptionPane.ERROR_MESSAGE);
                nameRule.requestFocus();
            }
            if (comboExit.getSelectedItem().equals("")) { //Caso falte a "saída" da regra
                JOptionPane.showMessageDialog(this, "It's necessary choose output type", "Error", JOptionPane.ERROR_MESSAGE);
                comboExit.requestFocus();
            } else {
                Iterator iter = linerules.iterator();
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
                    for (Iterator it = linerules.iterator(); it.hasNext();) {
                        LineRule condition = (LineRule) it.next();
                        if ((!condition.getComboTerm1().getSelectedItem().toString().equals("") && !condition.getComboTerm2().getSelectedItem().toString().equals("")) || (condition.getComboOperator().getSelectedItem().toString().equals("new_element") || (condition.getComboOperator().getSelectedItem().toString().equals("deleted_element")))) {
                            String aux = buildCondition(comboExit.getSelectedItem().toString(), condition.getComboTerm1().getSelectedItem().toString(), condition.getComboOperator().getSelectedItem().toString(), condition.getComboTerm2().getSelectedItem().toString());
                            if (regraConst.equals("")) {
                                regraConst = aux;
                            } else {
                                regraConst = regraConst + "," + aux;
                            }
                        }
                        condition.setVisible(false);
                    }
                    if (linerules.get(0).getComboOperator().getSelectedItem().toString().indexOf("_") < 0) {
                        regraConst = nameRule.getText().toLowerCase() + "(" + comboExit.getSelectedItem().toString().toUpperCase() + "):-" + baseRule + "," + comboExit.getSelectedItem().toString() + "(" + nameFactInRule + "Before," + comboExit.getSelectedItem().toString().toUpperCase() + ")," + regraConst + ".";
                        rulesModule.addRules(regraConst);
                    } else {
                        regraConst = nameRule.getText().toLowerCase() + "(" + comboExit.getSelectedItem().toString().toUpperCase() + "):-" + regraConst + ".";
                        rulesModule.addRules(regraConst);
                    }

                    comboExit.setSelectedItem("");
                    nameRule.setText("");

                    btnAddRule.setEnabled(true);
                    pnlRules.removeAll();
                    linerules.clear();
                    LineRule aux = new LineRule();
                    linerules.add(aux);
                    pnlRules.add(aux);
                    aux.getComboTerm1().requestFocus();
                    pnlRules.revalidate();
                }
            }
        } else if (e.getSource() == btnMiningRules) { //Mineração de regras de associação
            List<Set> listRules = WekaParser.gerarRegras(null);
            createListRules(listRules);
        }
    }

    /**
     * Remove os fatos repetidos.
     *
     * @param array Array contendo todos os fatos.
     * @return arrayResult Array com os fatos repetidos removidos.
     */
    private static String[] removeFactsRepeated(String[] array) {
        String[] arrayInvert = new HashSet<String>(Arrays.asList(array)).toArray(new String[0]);
        String[] arrayResult = new String[arrayInvert.length];

        //Reorganiza o array de fatos
        for (int i = 0; i < arrayInvert.length; i++) {
            arrayResult[i] = arrayInvert[arrayInvert.length - i - 1];
        }
        return arrayResult;
    }

    /**
     * Cria as caixas para a exibição dos fatos (somente os nomes) para montar
     * os radioButtons
     *
     * @param facts Um vetor de Strings onde cada índice contém os fatos
     * completos.
     */
    private void setIdentifyFacts(String[] facts) {
        btnAtributs.setVisible(false);
        pnlCenter.setLayout(new BoxLayout(pnlCenter, WIDTH));
        ButtonGroup btnGroup = new ButtonGroup();
        factsKey = new ArrayList<JRadioButton>();

        for (String fact : facts) { //Cria os radioBottons
            JRadioButton radioItem = new JRadioButton(fact);
            radioItem.setName(fact);
            btnGroup.add(radioItem);
            factsKey.add(radioItem);
            pnlCenter.add(radioItem);
        }
        btnNext.setVisible(true);
    }

    /**
     * Monta as opções dos comboBox de termos para a construção das regras.
     * Exemplo: (fato - v. Before / fato - v. After).
     *
     * @param namesFacts Vetors de Strings contendo o nome de todos os fatos.
     * @return doubleNames Nomes duplificados e normalizados.
     */
    private String[] getDoubleNames(String[] namesFacts) {
        String[] doubleNames;
        ArrayList<String> listNomesDuplicados = new ArrayList<String>();

        for (int i = 0; i < namesFacts.length; i++) {
            listNomesDuplicados.add(namesFacts[i] + " - v. Before");
            listNomesDuplicados.add(namesFacts[i] + " - v. After");
        }
        doubleNames = (String[]) listNomesDuplicados.toArray(new String[listNomesDuplicados.size()]);
        return doubleNames;
    }

    /**
     * Exibe a interface de construção de regras.
     */
    private void setPanelTerminal() {
        JPanel allPane = new JPanel();
        this.setMinimumSize(new Dimension(900, 300));
        this.setSize(this.getMinimumSize());

        this.setContentPane(allPane);
        if (!terminalOpen) { //Se for a primeira vez que ele é acionado
            nameRule = new JTextField();
            jLabel1 = new JLabel();
            comboExit = new JComboBox();
            jLabel2 = new JLabel();
        }
        terminalOpen = true;

        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        allPane.setLayout(gridBag);

        //paineis da janela de criação de regras
        JPanel pnlTop = new JPanel();
        pnlTop.setBorder(javax.swing.BorderFactory.createTitledBorder("Output:"));

        JPanel pnlMiddle = new JPanel();
        pnlMiddle.setBorder(javax.swing.BorderFactory.createTitledBorder("Conditions:"));

        JPanel pnlBottom = new JPanel();

        pnlWest.setBorder(javax.swing.BorderFactory.createTitledBorder("Association Rules:"));

        //adiciona os paineis à janela de construção de regras
        LayoutConstraints.setConstraints(constraints, 1, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        allPane.add(pnlTop, constraints);

        LayoutConstraints.setConstraints(constraints, 1, 1, 1, 1, 1000, 1000);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        allPane.add(pnlMiddle, constraints);

        LayoutConstraints.setConstraints(constraints, 1, 2, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.SOUTH;
        allPane.add(pnlBottom, constraints);

        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 2, 1000, 1000);
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.anchor = GridBagConstraints.WEST;
        allPane.add(pnlWest, constraints);

        //declara objetos de controle do layout do painel do topo
        GridBagLayout gridBagTop = new GridBagLayout();
        pnlTop.setLayout(gridBagTop);

        JPanel ruleNamePnl = new JPanel();//painel para o label e a area de texto para o nome de regra
        JPanel ruleOutputPnl = new JPanel();//painel para o label e o combobox para a saida da regra

        //layout para os paineis com nome de regra e saida de regra
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        pnlTop.add(ruleNamePnl, constraints);

        LayoutConstraints.setConstraints(constraints, 1, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        pnlTop.add(ruleOutputPnl, constraints);

        //Layout para painel de geração de regras de associação
        btnMiningRules.setText("Mining Rule");
        btnMiningRules.setSize(btnFinish.getSize());
        btnMiningRules.setMinimumSize(btnFinish.getSize());
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        pnlWest.add(btnMiningRules, constraints);

        //layout para painel com o label e a area de texto para o nome de regra
        GridBagLayout gridBagRuleName = new GridBagLayout();
        ruleNamePnl.setLayout(gridBagRuleName);

        jLabel1.setText("Rule Name:");
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(10, 10, 10, 5);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        ruleNamePnl.add(jLabel1, constraints);

        nameRule.setText("");
        constraints.insets = new Insets(10, 0, 10, 5);
        LayoutConstraints.setConstraints(constraints, 1, 0, 1000, 1, 10, 1);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        ruleNamePnl.add(nameRule, constraints);

        //layout para painel com o label e o combobox para a saida da regra
        GridBagLayout gridBagRuleOutput = new GridBagLayout();
        ruleOutputPnl.setLayout(gridBagRuleOutput);

        jLabel2.setText("Output:");
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(10, 5, 10, 5);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        ruleOutputPnl.add(jLabel2, constraints);

        comboExit.setModel(new javax.swing.DefaultComboBoxModel(namesFacts));
        comboExit.insertItemAt("", 0);
        comboExit.setSelectedItem("");
        constraints.insets = new Insets(10, 0, 10, 10);
        LayoutConstraints.setConstraints(constraints, 1, 0, 1000, 1, 10, 1);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        ruleOutputPnl.add(comboExit, constraints);

        //declara objetos de controle do layout do painel do topo
        GridBagLayout gridBagMid = new GridBagLayout();
        pnlMiddle.setLayout(gridBagMid);

        pnlRules = new JPanel();

        JScrollPane jsPane = new JScrollPane(pnlRules);
        jsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        constraints = new GridBagConstraints();
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        pnlMiddle.add(jsPane, constraints);

        LineRule firstLineRule = new LineRule();
        pnlRules.setLayout(new BoxLayout(pnlRules, BoxLayout.PAGE_AXIS));
        pnlRules.add(firstLineRule);
        LineRule.setPnlRules(pnlRules);
        linerules.add(firstLineRule);

        //declara objetos de controle do layout do painel do topo
        GridBagLayout gridBagBottom = new GridBagLayout();
        pnlBottom.setLayout(gridBagBottom);
        constraints = new GridBagConstraints();

        btnFinish.setText("Save and finish the builder");
        btnFinish.setSize(new Dimension(350, 25));
        btnFinish.setMinimumSize(btnFinish.getSize());
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        pnlBottom.add(btnFinish, constraints);

        btnAddRule.setText("Save and create more rules");
        btnAddRule.setSize(btnFinish.getSize());
        btnAddRule.setMinimumSize(btnFinish.getSize());
        LayoutConstraints.setConstraints(constraints, 1, 0, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        pnlBottom.add(btnAddRule, constraints);

        pnlCenter.validate();
        pnlCenter.setVisible(true);
        setVisible(true);
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
    private String buildCondition(String exit, String term1, String operator, String term2) {
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
            String[] term1part = term1.split("\\-");
            //Indice 0: nome do fato que compõe o termo
            //Indice 1: v. Before OU v. After
            String[] term2part = term2.split("\\-");

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

            //salario(Fb,SALARIOBefore)
            term1After = term1part[0] + "(" + arg1term1 + "," + term1part[0].toUpperCase() + term1part[1] + ")";
            term2After = term2part[0] + "(" + arg1term2 + "," + term1part[0].toUpperCase() + term2part[1] + ")";

            if (operator.equals("and")) {
                //Nao faz nada
                ruleAux = "";
            } else if (operator.equals(">")) {
                //Adiciona uma regra do tipo SalarioB>SalarioM
                ruleAux = term1part[0].toUpperCase() + term1part[1] + ">" + term1part[0].toUpperCase() + term2part[1];
            } else if (operator.equals("<")) {
                //Adiciona uma regra do tipo SalarioB<SalarioM
                ruleAux = term1part[0].toUpperCase() + term1part[1] + "<" + term1part[0].toUpperCase() + term2part[1];
            } else if (operator.equals("=")) {
                ruleAux = term1part[0].toUpperCase() + term1part[1] + "==" + term1part[0].toUpperCase() + term2part[1];
            } else if (operator.equals("!=")) {
                ruleAux = term1part[0].toUpperCase() + term1part[1] + "\\=" + term1part[0].toUpperCase() + term2part[1];
            }
            newRule = term1After + "," + term2After + "," + ruleAux;
            return newRule;
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

        btnNext.setVisible(false);
        lblChoiceKey.setVisible(false);
        pnlCenter.removeAll();

        pnlCenter.setLayout(new BoxLayout(pnlCenter, WIDTH));
        setPanelTerminal();
    }

    private void createListRules(List<Set> listRules) {

        pnlWest.setLayout(new BoxLayout(pnlWest, WIDTH));
        int i = 1;

        for (final Set<String> rules : listRules) {

            JPanel painel = new JPanel();
            painel.setLayout(new FlowLayout());
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

//                GridBagConstraints constraints = new GridBagConstraints();       
//                LayoutConstraints.setConstraints(constraints, 1, 0, 1, 1, 1, 1);
//                constraints.anchor = GridBagConstraints.CENTER;
//                constraints.fill = GridBagConstraints.NONE;                
//                
//                pnlWest.add(label, constraints);
//                pnlWest.add(button, constraints);                    
            button.addActionListener(new ActionListener() {
                /**
                 * evento do botão adicionar regras mineradas
                 */
                public void actionPerformed(ActionEvent e) {

                    button.setEnabled(false);                    
                    
                    for(String rule : rules) {
                        
                        LineRule aux = new LineRule();
                        linerules.add(aux);
                        LineRule.setLinerules(linerules);
                        pnlRules.add(aux);

                        aux.getComboOperator().setSelectedItem("!=");
                        aux.getComboOperator().setEnabled(false);
                        
                        aux.getComboTerm1().setSelectedItem(rule + " - v. Before");
                        aux.getComboTerm1().setEnabled(false);
                        
                        aux.getComboTerm2().setSelectedItem(rule + " - v. After");
                        aux.getComboTerm2().setEnabled(false);
                        
                        aux.getBtnAddCondition().setEnabled(false);
                        aux.getComboTerm1().requestFocus();
                        
                        linerules.remove(0);                                
                        pnlRules.revalidate();
                    }
                }
            });

            painel.add(button);
            painel.add(label);

//            pnlWest.add(label);
//            pnlWest.add(button);
            pnlWest.add(painel);
            pnlWest.updateUI();
            i++;
        }
        pnlWest.setAlignmentX(LEFT_ALIGNMENT);
    }
}

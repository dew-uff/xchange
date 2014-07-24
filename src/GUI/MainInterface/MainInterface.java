package GUI.MainInterface;

import Documents.Documents;
import Exception.NoSelectedFileException;
import GUI.About.*;
import GUI.FileManager.MultiDiffSaver;
import GUI.FileManager.ProjectLoader;
import GUI.FileManager.ProjectSaver;
import GUI.FileManager.XMLLoader;
import GUI.Layout.LayoutConstraints;
import GUI.Rules.RuleConstructInterface;
import GUI.Util.MainInterfaceHandler;
import GUI.Util.ProgressBar;
import GUI.Util.ProgressHandler;
import Manager.Manager;
import Manager.Results;
import Merge.MergeShow;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import SemanticMerge.SemanticMergeShow;
import SemanticMerge.SemanticRules;
import gems.ic.uff.br.newView.MergeThreeWayPanel;
import gems.ic.uff.br.newView.SettingsDialog;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.w3c.dom.Node;

/**
 * @author Marcio Tadeu de Oliveira Júnior
 */
public class MainInterface extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu mFile, mTools, mView, mAbout, mMerge, mTreeTest;
    private JMenuItem miNew, miOpen, miSave, miContext, miSimilarity, miAdd, miAboutGET, miAboutXChange, miHowToUse, miManager, miSettings, miMerge, miApplyChoice, miWriteMerged, miMergeCancel, miSyntaticDiff, miSemanticDiff, miSyntaticMerge, miSemanticMerge, miTreeTest;
    private ResultsTab resultsTab;
    private ResultsTabMerge resultsTabSemanticMerge;
    private DocumentsTab documentsTab;
    private PrologFactsTab prologFactsTab;
    private PrologFactsMergeTab prologFactsMergeTab;
    private JTabbedPane tabbedPane, tabbedPaneMerge;
    private Documents documents;
    private JButton testeBtn, newBtn, openBtn, addBtn, saveBtn, contextBtn, similarityBtn, managerBtn, mergeBtn, saveXMLDiffBtn, syntaticDiffBtn, semanticDiffBtn, syntaticMergeBtn, semanticMergeBtn, applyChoicesBtn, writeBtn, cancelBtn, reportBtn;
    private boolean isSimilarity;
    private float similarityRate;
    private Manager manager;
    private JPanel resultsTabMerge, conflictsTabMerge, semanticConflictsTabMerge, treesTabMerge, mergeTreeTabMerge, initialPane, optionPane;
    private DocumentsTabMerge documentsTabMerge;
    private Boolean isSyntaticMerge = false, isSyntaticDiff = false, isSemanticDiff = false, isSemanticMerge = false;
    private SintaticDiffTree sintaticDiff;
    private XMLDiffTab xmldifftab;
    private static JFrame jFrame;
    private ProgressBar pBar;
    private GridBagLayout gridBag;
    private GridBagConstraints constraints;

    public MainInterface(Manager manager) {//define caracteriscas da janela, tais como tamanho e redimensionamento
        super("XChange");//contrutor, nomeia a janela
        this.manager = manager;//passa à interface qual seu gerenciador de regras
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//função ao clicar no botão fechar
        Toolkit tk = Toolkit.getDefaultToolkit();//objeto para pegar as dimensoes da tela
        Dimension dim = tk.getScreenSize();//funçao para pegar as dimensoes da tela
        this.setSize(7 * (int) dim.getWidth() / 8, 7 * (int) dim.getHeight() / 8);//tamanho inicial da interface grafica
        dim.setSize(dim.getWidth() / 3, dim.getHeight() / 3);//tamanho mínimo da interface grafica
        this.setMinimumSize(dim);//seta o tamanho mínimo da interface grafica
        this.setLocationRelativeTo(null);//posicao inicial central
        this.buildInterface(); //cria o conteudo da janela principal
        MainInterfaceHandler.setMainInterface(this);
    }

    private void buildInterface() {//monta a interface grafica

        //lista de documentos XML abertos
        this.documents = new Documents();

        //declara objetos de controle do layout
        gridBag = new GridBagLayout();
        constraints = new GridBagConstraints();
        this.setLayout(gridBag);

        //cria a barra de menu
        menuBar = new JMenuBar();
        menuBar.setVisible(true);
        this.setJMenuBar(menuBar);

        //cria o menu "File" e seus itens
        mFile = new JMenu("File");
        mFile.setMnemonic('f');
        menuBar.add(mFile);

        miNew = new JMenuItem("New");
        miNew.setMnemonic('n');
        mFile.add(miNew);
        miNew.addActionListener(this);

        miAdd = new JMenuItem("Add");
        miAdd.setMnemonic('d');
        mFile.add(miAdd);
        miAdd.addActionListener(this);

        miOpen = new JMenuItem("Open");
        miOpen.setMnemonic('o');
        mFile.add(miOpen);
        miOpen.addActionListener(this);

        miSave = new JMenuItem("Save");
        miSave.setMnemonic('s');
        mFile.add(miSave);
        miSave.addActionListener(this);

        //cria o item "View" na barra de menus    
        mView = new JMenu("View");
        mView.setMnemonic('v');
        menuBar.add(mView);
        mView.addActionListener(this);

        miSyntaticDiff = new JMenuItem("Syntatic Diff");
        miSyntaticDiff.setMnemonic('y');
        mView.add(miSyntaticDiff);
        miSyntaticDiff.addActionListener(this);
        miSyntaticDiff.setEnabled(true);

        miSemanticDiff = new JMenuItem("Semantic Diff");
        miSemanticDiff.setMnemonic('e');
        mView.add(miSemanticDiff);
        miSemanticDiff.addActionListener(this);
        miSemanticDiff.setEnabled(true);

        mView.addSeparator();

        miSyntaticMerge = new JMenuItem("Syntatic Merge");
        miSyntaticMerge.setMnemonic('n');
        mView.add(miSyntaticMerge);
        miSyntaticMerge.addActionListener(this);
        miSyntaticMerge.setEnabled(true);

        miSemanticMerge = new JMenuItem("Semantic Merge");
        miSemanticMerge.setMnemonic('m');
        mView.add(miSemanticMerge);
        miSemanticMerge.addActionListener(this);
        miSemanticMerge.setEnabled(true);

        //cria o item "Tools" na barra de menus
        mTools = new JMenu("Tools");
        mTools.setMnemonic('o');
        menuBar.add(mTools);
        mTools.addActionListener(this);
        mTools.setEnabled(true);

        miManager = new JMenuItem("Manager");
        miManager.setMnemonic('m');
        mTools.add(miManager);
        miManager.addActionListener(this);
        miManager.setEnabled(false);

        mTools.addSeparator();

        miContext = new JMenuItem("Context Key");
        miContext.setMnemonic('k');
        mTools.add(miContext);
        miContext.addActionListener(this);
        miContext.setEnabled(false);

        miSimilarity = new JMenuItem("Similarity");
        miSimilarity.setMnemonic('s');
        mTools.add(miSimilarity);
        miSimilarity.addActionListener(this);
        miSimilarity.setEnabled(false);

        mMerge = new JMenu("Merge Actions");
        mMerge.setMnemonic('g');
        mTools.add(mMerge);
        mMerge.addActionListener(this);
        mMerge.setEnabled(false);

        miMerge = new JMenuItem("Merge");
        miMerge.setMnemonic('r');
        miMerge.setVisible(true);
        miMerge.setEnabled(false);
        mMerge.add(miMerge);
        miMerge.addActionListener(this);

        miApplyChoice = new JMenuItem("Apply Choice");
        miApplyChoice.setMnemonic('p');
        miApplyChoice.setVisible(true);
        miApplyChoice.setEnabled(false);
        mMerge.add(miApplyChoice);
        miApplyChoice.addActionListener(this);

        miWriteMerged = new JMenuItem("Write Merged");
        miWriteMerged.setMnemonic('w');
        miWriteMerged.setVisible(true);
        miWriteMerged.setEnabled(false);
        mMerge.add(miWriteMerged);
        miWriteMerged.addActionListener(this);

        miMergeCancel = new JMenuItem("Cancel");
        miMergeCancel.setMnemonic('c');
        miMergeCancel.setVisible(true);
        miMergeCancel.setEnabled(false);
        mMerge.add(miMergeCancel);
        miMergeCancel.addActionListener(this);

        mTools.addSeparator();

        miSettings = new JMenuItem("Settings");
        miSettings.setMnemonic('t');
        miSettings.setVisible(true);
        mTools.add(miSettings);
        miSettings.addActionListener(this);
        miSettings.setEnabled(false);

        //Cria o menu "About" e seus itens
        mAbout = new JMenu("About");
        mAbout.setMnemonic('a');
        menuBar.add(mAbout);

        miAboutXChange = new JMenuItem("XChange");
        miAdd.setMnemonic('x');
        mAbout.add(miAboutXChange);
        miAboutXChange.addActionListener(this);

        miAboutGET = new JMenuItem("GETComp");
        miAboutGET.setMnemonic('g');
        mAbout.add(miAboutGET);
        miAboutGET.addActionListener(this);

        mAbout.addSeparator();

        miHowToUse = new JMenuItem("How To Use");
        miHowToUse.setMnemonic('u');
        mAbout.add(miHowToUse);
        miHowToUse.addActionListener(this);

        //Cria a barra de ferramentas
        JToolBar tBar = new JToolBar();

        //Cria o painel que contem  a barra de ferramentas
        JPanel buttonsPane = new JPanel();
        buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.Y_AXIS));

        //Define os icones que serão usados nos botões
        ImageIcon newIcon = new ImageIcon(getClass().getResource("/GUI/icons/new.png"));
        ImageIcon testeIcon = new ImageIcon(getClass().getResource("/GUI/icons/new.png"));
        ImageIcon openIcon = new ImageIcon(getClass().getResource("/GUI/icons/open.png"));
        ImageIcon addIcon = new ImageIcon(getClass().getResource("/GUI/icons/add.png"));
        ImageIcon saveIcon = new ImageIcon(getClass().getResource("/GUI/icons/save.png"));
        ImageIcon contextIcon = new ImageIcon(getClass().getResource("/GUI/icons/context.png"));
        ImageIcon similarityIcon = new ImageIcon(getClass().getResource("/GUI/icons/similarity.png"));
        ImageIcon managerIcon = new ImageIcon(getClass().getResource("/GUI/icons/manager.png"));
        ImageIcon mergeIcon = new ImageIcon(getClass().getResource("/GUI/icons/merge.png"));
        ImageIcon applyChoicesIcon = new ImageIcon(getClass().getResource("/GUI/icons/applyChoices.png"));
        ImageIcon writeIcon = new ImageIcon(getClass().getResource("/GUI/icons/write.png"));
        ImageIcon cancelIcon = new ImageIcon(getClass().getResource("/GUI/icons/cancelMerge.png"));
        ImageIcon reportIcon = new ImageIcon(getClass().getResource("/GUI/icons/report.png"));
        ImageIcon saveXMLDiffIcon = new ImageIcon(getClass().getResource("/GUI/icons/saveXMLDiff.png"));

        //Cria os botões e seus eventos
        testeBtn = new JButton(testeIcon);
        testeBtn.setToolTipText("Teste");
        testeBtn.addActionListener(this);
        testeBtn.setEnabled(true);

        //Cria os botões e seus eventos
        newBtn = new JButton(newIcon);
        newBtn.setToolTipText("New Project");
        newBtn.addActionListener(this);
        newBtn.setEnabled(false);

        openBtn = new JButton(openIcon);
        openBtn.setToolTipText("Open Project");
        openBtn.addActionListener(this);
        openBtn.setEnabled(false);

        addBtn = new JButton(addIcon);
        addBtn.setToolTipText("Add XML");
        addBtn.addActionListener(this);
        addBtn.setEnabled(false);

        saveBtn = new JButton(saveIcon);
        saveBtn.setToolTipText("Save Project");
        saveBtn.addActionListener(this);
        saveBtn.setEnabled(false);

        contextBtn = new JButton(contextIcon);
        contextBtn.setToolTipText("Context Key");
        contextBtn.addActionListener(this);
        contextBtn.setEnabled(false);

        similarityBtn = new JButton(similarityIcon);
        similarityBtn.setToolTipText("Similarity");
        similarityBtn.addActionListener(this);
        similarityBtn.setEnabled(isSimilarity);

        managerBtn = new JButton(managerIcon);
        managerBtn.setToolTipText("Manager");
        managerBtn.addActionListener(this);
        managerBtn.setEnabled(false);

        mergeBtn = new JButton(mergeIcon);
        mergeBtn.setToolTipText("Merge");
        mergeBtn.addActionListener(this);
        mergeBtn.setEnabled(false);
        mergeBtn.setVisible(false);

        applyChoicesBtn = new JButton(applyChoicesIcon);
        applyChoicesBtn.setToolTipText("Apply Choice");
        applyChoicesBtn.addActionListener(this);
        applyChoicesBtn.setEnabled(false);
        applyChoicesBtn.setVisible(false);

        writeBtn = new JButton(writeIcon);
        writeBtn.setToolTipText("Write Merged");
        writeBtn.addActionListener(this);
        writeBtn.setEnabled(false);
        writeBtn.setVisible(false);

        cancelBtn = new JButton(cancelIcon);
        cancelBtn.setToolTipText("Cancel");
        cancelBtn.addActionListener(this);
        cancelBtn.setEnabled(false);
        cancelBtn.setVisible(false);

        saveXMLDiffBtn = new JButton(saveXMLDiffIcon);
        saveXMLDiffBtn.setToolTipText("Save XML Diff");
        saveXMLDiffBtn.addActionListener(this);
        saveXMLDiffBtn.setEnabled(false);
        saveXMLDiffBtn.setVisible(false);

        reportBtn = new JButton(reportIcon);
        reportBtn.setToolTipText("Generate and Save a report as pfd file");
        reportBtn.addActionListener(this);
        reportBtn.setEnabled(false);
        reportBtn.setVisible(false);

        //Adiciona os botões à barra de ferramentas
        tBar.add(newBtn);
        tBar.add(openBtn);
        tBar.add(saveBtn);
        tBar.add(addBtn);
        tBar.add(contextBtn);
        tBar.add(similarityBtn);
        tBar.add(managerBtn);
        tBar.add(mergeBtn);
        tBar.add(applyChoicesBtn);
        tBar.add(writeBtn);
        tBar.add(cancelBtn);
        tBar.add(saveXMLDiffBtn);
        tBar.add(reportBtn);
        tBar.add(testeBtn);
        tBar.setAlignmentX(0);

        tBar.setFloatable(false); //Fixa a barra de ferramentas à sua posição

        //Adiciona a barra de ferramentas ao seu painel
        buttonsPane.add(tBar);

        //Adiciona o painel da barra de ferramentas à interface gráfica
        this.add(buttonsPane);

        //indica a posição e layout da barra de ferramentas
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 100, 1);
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(buttonsPane, constraints);

        //cria o painel de opçoes inicial do XChange
        this.initialPane = new JPanel();
        this.initialPane.setVisible(true);
        this.initialPane.setBorder(BorderFactory.createEtchedBorder());
        this.initialPane.setLayout(gridBag);

        this.optionPane = new JPanel();
        this.optionPane.setBorder(BorderFactory.createTitledBorder(""));
        this.optionPane.setLayout(gridBag);

        Font font = new Font("Serif", Font.BOLD, 30);

        this.syntaticDiffBtn = new JButton("Syntathic Diff");
        this.syntaticDiffBtn.setPreferredSize(new Dimension(300, 100));
        this.syntaticDiffBtn.setBackground(Color.WHITE);
        this.syntaticDiffBtn.setFont(font);
        this.syntaticDiffBtn.addActionListener(this);
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 100, 100);
        constraints.insets = new Insets(70, 15, 5, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(syntaticDiffBtn, constraints);

        this.semanticDiffBtn = new JButton("Semantic Diff");
        this.semanticDiffBtn.setPreferredSize(new Dimension(300, 100));
        this.semanticDiffBtn.setBackground(Color.WHITE);
        this.semanticDiffBtn.setFont(font);
        this.semanticDiffBtn.addActionListener(this);
        LayoutConstraints.setConstraints(constraints, 1, 0, 1, 1, 100, 100);
        constraints.insets = new Insets(70, 10, 5, 15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(semanticDiffBtn, constraints);

        this.syntaticMergeBtn = new JButton("Syntathic Merge");
        this.syntaticMergeBtn.setPreferredSize(new Dimension(300, 100));
        this.syntaticMergeBtn.setBackground(Color.WHITE);
        this.syntaticMergeBtn.setFont(font);
        this.syntaticMergeBtn.addActionListener(this);
        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 100, 100);
        constraints.insets = new Insets(5, 15, 5, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(syntaticMergeBtn, constraints);

        this.semanticMergeBtn = new JButton("Semantic Merge");
        this.semanticMergeBtn.setPreferredSize(new Dimension(300, 100));
        this.semanticMergeBtn.setBackground(Color.WHITE);
        this.semanticMergeBtn.setFont(font);
        this.semanticMergeBtn.addActionListener(this);
        LayoutConstraints.setConstraints(constraints, 1, 1, 1, 1, 100, 100);
        constraints.insets = new Insets(5, 10, 5, 15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(semanticMergeBtn, constraints);

        // Adiciona os botões ao Panel de Opção
        this.optionPane.add(syntaticDiffBtn);
        this.optionPane.add(semanticDiffBtn);
        this.optionPane.add(syntaticMergeBtn);
        this.optionPane.add(semanticMergeBtn);

        LayoutConstraints.setConstraints(constraints, 0, 1, 10, 10, 100, 100);
        constraints.insets = new Insets(75, 200, 75, 200);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        gridBag.setConstraints(optionPane, constraints);

        initialPane.add(optionPane);

        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 100, 100);
        constraints.insets = new Insets(5, 10, 0, 10);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(initialPane, constraints);

        this.add(initialPane);

        pBar = ProgressHandler.makeNew(100, "Ready: Waiting for User");
        ProgressHandler.stop();

        LayoutConstraints.setConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(pBar, constraints);

        this.add(pBar);

        //inicia o programa zerado
        this.refresh(this.documents);

        //torna a janela visivel
        this.setVisible(true);

        //cria o gerenciador de abas adicionando as abas de resultados e documentos
        tabbedPane = new JTabbedPane();

        this.revalidate();
        this.repaint();
    }

    /**
     *
     *
     * @param documents
     * @param gridBag
     * @param constraints
     */
    public void buildSemanticMerge(Documents documents, GridBagLayout gridBag, GridBagConstraints constraints) {
        //desativa Semantic Diff e Syntatic Diff caso os mesmo estejam ativos
        if (isSyntaticDiff || isSemanticDiff || isSyntaticMerge) {
            tabbedPane.setVisible(false);
            this.isSemanticDiff = false;
            this.isSyntaticDiff = false;
            this.isSyntaticMerge = false;
        }

        newBtn.setEnabled(true);
        openBtn.setEnabled(true);
        addBtn.setEnabled(true);
        mTools.setEnabled(true);
        mMerge.setVisible(true);
        miManager.setEnabled(false);
        miManager.setVisible(true);
        miContext.setEnabled(false);
        miSimilarity.setEnabled(false);
        miSettings.setEnabled(false);
        similarityBtn.setVisible(false);
        contextBtn.setVisible(true);
        managerBtn.setVisible(true);
        managerBtn.setEnabled(false);
        reportBtn.setVisible(false);
        mergeBtn.setVisible(true);
        mMerge.setEnabled(true);
        applyChoicesBtn.setEnabled(false);
        applyChoicesBtn.setVisible(true);
        writeBtn.setEnabled(false);
        writeBtn.setVisible(true);
        cancelBtn.setEnabled(false);
        cancelBtn.setVisible(true);
        saveXMLDiffBtn.setVisible(false);

        documentsTabMerge = new DocumentsTabMerge();
        resultsTabSemanticMerge = new ResultsTabMerge();
        conflictsTabMerge = new JPanel();
        semanticConflictsTabMerge = new JPanel();
        resultsTabMerge = new JPanel();
        prologFactsMergeTab = new PrologFactsMergeTab(manager, this);

        //cria o gerenciador de abas do merge adicionando as abas de documentos, resultados e conflitos
        tabbedPaneMerge = new JTabbedPane();
        tabbedPaneMerge.add(documentsTabMerge, "XML Version");//adiciona a aba de documentos
        tabbedPaneMerge.add(prologFactsMergeTab, "Prolog Facts");
        tabbedPaneMerge.add(resultsTabSemanticMerge, "XML Diff");//adiciona a aba de resultados
        tabbedPaneMerge.add(conflictsTabMerge, "Conflicts");//adiciona a aba de conflitos
        tabbedPaneMerge.add(semanticConflictsTabMerge, "Semantic Conflicts"); //adiciona a aba de conflitos semanticos.
        tabbedPaneMerge.add(resultsTabMerge, "XML Merge"); //adiciona a aba Diff XML

        tabbedPaneMerge.setEnabledAt(1, false);
        tabbedPaneMerge.setEnabledAt(2, false);
        tabbedPaneMerge.setEnabledAt(3, false);
        tabbedPaneMerge.setEnabledAt(4, false);
        tabbedPaneMerge.setVisible(true);

        tabbedPaneMerge.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (tabbedPaneMerge.getSelectedIndex() == 1 && !prologFactsMergeTab.alreadySet()) {
                    prologFactsMergeTab.setLeftCB(documentsTabMerge.getLeftCBIndex());
                    prologFactsMergeTab.setCenterCB(documentsTabMerge.getCenterCBIndex());
                    prologFactsMergeTab.setRightCB(documentsTabMerge.getRightCBIndex());
                }
            }
        });

        //indica a posição e layout do gerenciador de abas
        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 100, 100);
        constraints.insets = new Insets(0, 10, 0, 10);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(tabbedPaneMerge, constraints);

        //adiciona o gerenciador de abas do merge à interface grafica
        this.add(tabbedPaneMerge);

        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 100, 100);
        constraints.insets = new Insets(0, 10, 0, 10);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(tabbedPaneMerge, constraints);

        tabbedPaneMerge.setVisible(true);
        this.refresh(documents);
    }

    /*
     * Monta a Interface do Diff Sintatico e do Diff Semantico
     */
    public void buildDiffInterface(Documents documents, GridBagLayout gridBag, GridBagConstraints constraints) {
        //Desetiva o Syntatic Merge caso o mesmo esteja ativo
        if (isSyntaticMerge || isSemanticMerge) {
            tabbedPaneMerge.setVisible(false);
            this.isSyntaticMerge = false;
            this.isSemanticMerge = false;
        }

        //torna visivel os botões iniciais
        newBtn.setEnabled(true);
        openBtn.setEnabled(true);
        addBtn.setEnabled(true);

        //cria a aba de documentos
        documentsTab = new DocumentsTab(manager, this);

        //cria o gerenciador de abas adicionando as abas de resultados e documentos
        tabbedPane = new JTabbedPane();

        tabbedPane.add(documentsTab, "XML Version");//adiciona a aba de documentos
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setVisible(true);

        // Monta a interface com os componentes especificos do Diff Semantico
        if (isSemanticDiff) {
            saveXMLDiffBtn.setVisible(false);
            mergeBtn.setVisible(false);
            writeBtn.setVisible(false);
            applyChoicesBtn.setVisible(false);
            cancelBtn.setVisible(false);
            contextBtn.setVisible(true);
            similarityBtn.setVisible(true);
            managerBtn.setVisible(true);
            reportBtn.setVisible(true);
            miSettings.setEnabled(true);

            //cria a aba de Fatos Prolog
            prologFactsTab = new PrologFactsTab(manager, this);

            //Abilita e desabilita os menus necessários na aba "Tools"
            mTools.setEnabled(true);
            mMerge.setEnabled(false);

            //cria o gerenciador de abas adicionando a aba de Fatos Prolog
            tabbedPane.add(prologFactsTab, "Prolog Facts");//adiciona a aba de fatosprolog
            tabbedPane.setEnabledAt(1, false);
            tabbedPane.setVisible(true);

            //cria a aba de resultados
            resultsTab = new ResultsTab();

            //cria o gerenciador de abas adicionando a aba de resultados
            tabbedPane.add(resultsTab, "XML Diff");//adiciona a aba de resultados
            tabbedPane.setEnabledAt(2, false);
            tabbedPane.setVisible(true);

            // Adiciona um evento para mudança de aba
            tabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (tabbedPane.getSelectedIndex() == 1 && !prologFactsTab.alreadySet()) {
                        prologFactsTab.setLeftCB(documentsTab.getLeftCBIndex());
                        prologFactsTab.setRightCB(documentsTab.getRightCBIndex());
                    }
                }
            });
        } // Monta a interface com os componentes especificos do Diff Sintatico
        else if (isSyntaticDiff) {
            contextBtn.setVisible(false);
            similarityBtn.setVisible(false);
            managerBtn.setVisible(false);
            mergeBtn.setVisible(false);
            writeBtn.setVisible(false);
            applyChoicesBtn.setVisible(false);
            cancelBtn.setVisible(false);
            saveXMLDiffBtn.setVisible(true);
            reportBtn.setVisible(false);
            miSettings.setEnabled(false);

            //cria a aba Tree Diff
            sintaticDiff = new SintaticDiffTree(documents);

            //cria o gerenciador de abas adicionando a aba de Tree Diff
            tabbedPane.add(sintaticDiff, "Tree Diff"); //adiciona aba de arvores (Diff Sintatico)
            tabbedPane.setEnabledAt(1, false);
            sintaticDiff.setVisible(true);

            //cria a aba XML Diff
            xmldifftab = new XMLDiffTab();

            //cria o gerenciador de abas adicionando a aba de XML Diff
            tabbedPane.add(xmldifftab, "XML Diff");
            tabbedPane.setEnabledAt(2, false);
            xmldifftab.setVisible(true);

            // Adiciona um evento para mudança de aba
            tabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (tabbedPane.getSelectedIndex() == 1) {
                        sintaticDiff.plotTree();
                    } else if (tabbedPane.getSelectedIndex() == 2) {
                        sintaticDiff.setVisible(false);
                        xmldifftab.showXMLDiff();
                    }
                }
            });
        }

        //adiciona o gerenciador de abas à interface grafica
        this.add(tabbedPane);

        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 100, 100);
        constraints.insets = new Insets(0, 10, 0, 10);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(tabbedPane, constraints);

        tabbedPane.setVisible(true);
        this.refresh(documents);
    }

    /*
     * Monta a Interface do Merge Sintatico
     */
    public void buildSyntaticMerge(Documents documents, GridBagLayout gridBag, GridBagConstraints constraints) {
        //desativa Semantic Diff e Syntatic Diff caso os mesmo estejam ativos
        if (isSyntaticDiff || isSemanticDiff) {
            tabbedPane.setVisible(false);
            this.isSemanticDiff = false;
            this.isSyntaticDiff = false;
            this.isSemanticMerge = false;
        }

        newBtn.setEnabled(true);
        openBtn.setEnabled(true);
        addBtn.setEnabled(true);
        mTools.setEnabled(true);
        mMerge.setVisible(true);
        miManager.setEnabled(false);
        miContext.setEnabled(false);
        miSimilarity.setEnabled(false);
        miSettings.setEnabled(false);
        similarityBtn.setVisible(false);
        contextBtn.setVisible(false);
        managerBtn.setVisible(false);
        reportBtn.setVisible(false);
        mergeBtn.setVisible(true);
        mMerge.setEnabled(true);
        applyChoicesBtn.setEnabled(false);
        applyChoicesBtn.setVisible(true);
        writeBtn.setEnabled(false);
        writeBtn.setVisible(true);
        cancelBtn.setEnabled(false);
        cancelBtn.setVisible(true);
        saveXMLDiffBtn.setVisible(false);

        documentsTabMerge = new DocumentsTabMerge();
        resultsTabMerge = new JPanel();
        conflictsTabMerge = new JPanel();
        treesTabMerge = new JPanel();
        mergeTreeTabMerge = new JPanel();

        //cria o gerenciador de abas do merge adicionando as abas de documentos, resultados e conflitos
        tabbedPaneMerge = new JTabbedPane();
        tabbedPaneMerge.add(documentsTabMerge, "XML Version");//adiciona a aba de documentos
        tabbedPaneMerge.add(resultsTabMerge, "XML Merge");//adiciona a aba de resultados
        tabbedPaneMerge.add(conflictsTabMerge, "Conflicts");//adiciona a aba de conflitos
        tabbedPaneMerge.add(treesTabMerge, "XML Version Tree"); //adiciona a aba das Arvores XML
        tabbedPaneMerge.add(mergeTreeTabMerge, "Merge Tree");//adiciona a aba de Merge Three (arvores)
        tabbedPaneMerge.setEnabledAt(1, false);
        tabbedPaneMerge.setEnabledAt(2, false);
        tabbedPaneMerge.setEnabledAt(3, false);
        tabbedPaneMerge.setEnabledAt(4, false);
        tabbedPaneMerge.setVisible(true);

        //indica a posição e layout do gerenciador de abas
        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 100, 100);
        constraints.insets = new Insets(0, 10, 0, 10);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(tabbedPaneMerge, constraints);

        //adiciona o gerenciador de abas do merge à interface grafica
        this.add(tabbedPaneMerge);

        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 100, 100);
        constraints.insets = new Insets(0, 10, 0, 10);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(tabbedPaneMerge, constraints);

        tabbedPaneMerge.setVisible(true);
        this.refresh(documents);
    }

    /**
     * Atualiza as informações na tela inclusive seus subcomponentes
     *
     * @param documents
     */
    public void refresh(Documents documents) {//modifica a tela de acordo com que novos documentos XML são adcionados ao projeto, ou quand se inicia um novo projeto      
        if (isSyntaticMerge) {
            documentsTabMerge.refresh(documents);
            if (documents.getSize() >= 3) {
                miMerge.setEnabled(true);
                mergeBtn.setEnabled(true);
            }
        } else if (isSemanticMerge) {
            documentsTabMerge.refresh(documents);
            prologFactsMergeTab.refresh(documents);
            resultsTabSemanticMerge.refresh(documents, manager);
            if (documents.getSize() >= 3) {
                contextBtn.setEnabled(true);
                miContext.setEnabled(true);
            }
        } else if (isSyntaticDiff) {
            documentsTab.refresh(documents);
            sintaticDiff.refresh(documents);
            xmldifftab.refresh(documents);
            if (documents.getSize() >= 2) {
                tabbedPane.setEnabledAt(1, true);
                tabbedPane.setEnabledAt(2, true);
                miSave.setEnabled(true);
                saveBtn.setEnabled(true);
                miSimilarity.setEnabled(true);
                similarityBtn.setEnabled(true);
                saveXMLDiffBtn.setEnabled(true);
            } else {
                miSave.setEnabled(false);
                saveBtn.setEnabled(false);
                miSimilarity.setEnabled(false);
                similarityBtn.setEnabled(false);
                saveXMLDiffBtn.setEnabled(false);
            }
        } else if (isSemanticDiff) {
            documentsTab.refresh(documents);
            prologFactsTab.refresh(documents);
            resultsTab.refresh(documents, manager);
            if (documents.getSize() >= 2) {
                miContext.setEnabled(true);
                contextBtn.setEnabled(true);
                miSave.setEnabled(true);
                saveBtn.setEnabled(true);
                miSimilarity.setEnabled(true);
                similarityBtn.setEnabled(true);
            } else {
                miContext.setEnabled(false);
                contextBtn.setEnabled(false);
                miSave.setEnabled(false);
                saveBtn.setEnabled(false);
                miSimilarity.setEnabled(false);
                similarityBtn.setEnabled(false);
            }
        }
        this.revalidate();
        this.repaint();
        jFrame = this;
    }

    /**
     * Trata os eventos que ocorrem em objetos-atributos do objeto MainInterface
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(syntaticDiffBtn) || e.getSource().equals(miSyntaticDiff)) {
            this.initialPane.setVisible(false);
            this.isSyntaticDiff = true;
            this.isSemanticDiff = false;
            tabbedPane.setVisible(false);
            if (tabbedPaneMerge != null) {
                tabbedPaneMerge.setVisible(false);
            }
            this.buildDiffInterface(documents, gridBag, constraints);
        } else if (e.getSource().equals(semanticDiffBtn) || e.getSource().equals(miSemanticDiff)) {
            this.initialPane.setVisible(false);
            this.isSemanticDiff = true;
            this.isSyntaticDiff = false;
            tabbedPane.setVisible(false);
            this.buildDiffInterface(documents, gridBag, constraints);
        } else if (e.getSource().equals(syntaticMergeBtn) || e.getSource().equals(miSyntaticMerge)) {
            this.initialPane.setVisible(false);
            this.isSyntaticMerge = true;
            this.buildSyntaticMerge(documents, gridBag, constraints);
        } else if (e.getSource().equals(semanticMergeBtn) || e.getSource().equals(miSemanticMerge)) {
            this.initialPane.setVisible(false);
            this.isSemanticMerge = true;
            this.buildSemanticMerge(documents, gridBag, constraints);
        } else if (e.getSource().equals(miAdd) || e.getSource().equals(addBtn)) {//ação "ADICIONAR"
            addXMLFile();
        } else if (e.getSource().equals(similarityBtn) || e.getSource().equals(miSimilarity)) {//ação "SIMILARIDADE"
            similarity();
            tabbedPane.setEnabledAt(1, true);
            this.repaint();
        } else if (e.getSource().equals(contextBtn) || e.getSource().equals(miContext)) {//ação "CHAVE DE CONTEXTO"
            contextKey();
            if (!isSemanticMerge) {
                tabbedPane.setEnabledAt(1, true);
            } else {
                tabbedPaneMerge.setEnabledAt(1, true);
            }
        } else if (e.getSource().equals(newBtn) || e.getSource().equals(miNew)) {//ação "NOVO"
            newProject();
        } else if (e.getSource().equals(saveBtn) || e.getSource().equals(miSave)) {//ação "SALVAR"
            try {
                boolean isEnabledFactsPrologFacts=false;
                boolean isEnabledXMLDiff=false;
                if(tabbedPane.isEnabledAt(1)){//Se estiver habilitada a aba de fatos prolog
                    isEnabledFactsPrologFacts = true;
                }
                if(tabbedPane.isEnabledAt(2)){
                    isEnabledXMLDiff = true;
                }
                ProjectSaver.save(documents, this, manager, isEnabledFactsPrologFacts, isEnabledXMLDiff);
            } catch (NoSelectedFileException ex) {
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error on save Project", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(openBtn) || e.getSource().equals(miOpen)) {//ação "ABRIR"
            try {
                this.documents = ProjectLoader.load();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error on load Project", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NoSelectedFileException ex) {
            }
            this.refresh(this.documents);
        } else if ((e.getSource().equals(managerBtn) || e.getSource().equals(miManager)) && (!isSemanticMerge)) {//ação "MANAGER"
            callManager();
            tabbedPane.setEnabledAt(2, true);
            reportBtn.setEnabled(true);
        } else if ((e.getSource().equals(managerBtn) || e.getSource().equals(miManager)) && isSemanticMerge) {//ação "MANAGER" para o merge semântico
            callManagerSemantic();
            tabbedPaneMerge.setEnabledAt(2, true);
            reportBtn.setEnabled(true);
        } else if (e.getSource().equals(reportBtn)) {
            resultsTab.getInferenceFileChooser().exportReport();
        } else if (e.getSource().equals(miHowToUse)) {//Abre o canal do youtube que contem os tutoriais do XChange
            try {
                URI tutoURL = new URI("http://www.youtube.com/watch?v=hKO_NDfLX-s&list=PLa7uZeJ8iahfcw62gG8-qrP3RSCLykKcz&feature=plpp_play_all");
                try {
                    Desktop.getDesktop().browse(tutoURL);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Genric error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (URISyntaxException ex) {
                JOptionPane.showMessageDialog(null, "Error on get URL", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(miAboutXChange)) {//abre About XChange
            try {
                callAboutXChange();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error on AboutXChange", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(miAboutGET)) {//abre About GET
            try {
                callAboutGET();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error on AboutGET", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(mergeBtn) || e.getSource().equals(miMerge)) {//ação "Merge"
            merge();
        } else if (e.getSource().equals(miSettings)) {//ação "Settings"
            SettingsDialog settingsFrame = new SettingsDialog(this);
            settingsFrame.show();
        } else if (e.getSource().equals(saveXMLDiffBtn)) {//ação "Save XML Diff"
            try {
                MultiDiffSaver.save(documents);
            } catch (NoSelectedFileException ex) {
                JOptionPane.showMessageDialog(null, "Diff saving failure!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Constroi a interface de merge
     */
    public void merge() {
        MergeShow mergeshow = new MergeShow();
        mergeshow.merge(documentsTabMerge.getPathDocLeft(), documentsTabMerge.getPathDocCenter(), documentsTabMerge.getPathDocRight(), this);
        conflictsTabMerge.removeAll();

        resultsTabMerge.removeAll();

        treesTabMerge.removeAll();
        mergeTreeTabMerge.removeAll();

        applyChoicesBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
        miApplyChoice.setEnabled(true);
        miMergeCancel.setEnabled(true);

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        //indica a posição e o layout
        LayoutConstraints.setConstraints(gridBagConstraints, 0, 0, 1, 1, 1, 1);
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

        if (mergeshow.getConflictsJP() != null) {
            conflictsTabMerge.setLayout(gridBag);
            JPanel conflictsJP = mergeshow.getConflictsJP();
            gridBag.setConstraints(conflictsJP, gridBagConstraints);
            conflictsTabMerge.add(conflictsJP);
            tabbedPaneMerge.setEnabledAt(2, true);
            tabbedPaneMerge.setSelectedIndex(2);
        } else {
            tabbedPaneMerge.setSelectedIndex(1);
        }
        JPanel resultsJP = mergeshow.getResultsJP();
        gridBag.setConstraints(resultsJP, gridBagConstraints);
        if (isSemanticMerge) {
            tabbedPaneMerge.setEnabledAt(5, true);
        }
        resultsTabMerge.setLayout(gridBag);
        resultsTabMerge.add(resultsJP);
        tabbedPaneMerge.setEnabledAt(1, true);

        // Arvores do Phoenix
        treesTabMerge.setLayout(gridBag);
        JSplitPane trees = TwoTreePane.build(documents.getContent(1), documents.getContent(2));
        gridBag.setConstraints(trees, gridBagConstraints);
        treesTabMerge.add(trees);
        tabbedPaneMerge.setEnabledAt(3, true);

        mergeTreeTabMerge.setLayout(gridBag);
        VisualizationViewer<Node, String> mergeTree = MergeDiffPanel.build(documents.getContent(0), documents.getContent(1), documents.getContent(2));
        gridBag.setConstraints(mergeTree, gridBagConstraints);
        mergeTreeTabMerge.add(mergeTree);
        tabbedPaneMerge.setEnabledAt(4, true);

        this.refresh(documents);
    }

        public void semanticMerge(Manager manager) {
        SemanticMergeShow semanticMergeShow = new SemanticMergeShow();
        semanticMergeShow.merge(documentsTabMerge.getPathDocLeft(), documentsTabMerge.getPathDocCenter(), documentsTabMerge.getPathDocRight(), this, manager);
        conflictsTabMerge.removeAll();

        resultsTabMerge.removeAll();

        applyChoicesBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
        miApplyChoice.setEnabled(true);
        miMergeCancel.setEnabled(true);

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        //indica a posição e o layout
        LayoutConstraints.setConstraints(gridBagConstraints, 0, 0, 1, 1, 1, 1);
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

        if (semanticMergeShow.getConflictsJP() != null) {
            conflictsTabMerge.setLayout(gridBag);
            JPanel conflictsJP = semanticMergeShow.getConflictsJP();
            gridBag.setConstraints(conflictsJP, gridBagConstraints);
            conflictsTabMerge.add(conflictsJP);
            tabbedPaneMerge.setEnabledAt(2, true);
            //tabbedPaneMerge.setSelectedIndex(2);
        } 
        if (semanticMergeShow.getSemanticConflictisJP()!= null) {
            semanticConflictsTabMerge.setLayout(gridBag);
            JPanel semanticConflictsJP = semanticMergeShow.getSemanticConflictisJP();
            gridBag.setConstraints(semanticConflictsJP, gridBagConstraints);
            semanticConflictsTabMerge.add(semanticConflictsJP);
            tabbedPaneMerge.setEnabledAt(4, true);
            //tabbedPaneMerge.setSelectedIndex(4);
        }
        else {
            tabbedPaneMerge.setSelectedIndex(1);
        }
        JPanel resultsJP = semanticMergeShow.getResultsJP();
        gridBag.setConstraints(resultsJP, gridBagConstraints);
        if (isSemanticMerge) {
            tabbedPaneMerge.setEnabledAt(4, true);
            tabbedPaneMerge.setEnabledAt(3, true);
        }
        resultsTabMerge.setLayout(gridBag);
        resultsTabMerge.add(resultsJP);
        tabbedPaneMerge.setEnabledAt(1, true);

        this.refresh(documents);
    }

    /**
     * Atualiza a aba de resultado do merge
     */
    public void updateResults(JPanel results) {
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        //indica a posição e o layout
        LayoutConstraints.setConstraints(gridBagConstraints, 0, 0, 1, 1, 1, 1);
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        resultsTabMerge.setLayout(gridBag);
        resultsTabMerge.setVisible(false);
        resultsTabMerge.removeAll();
        gridBag.setConstraints(results, gridBagConstraints);
        resultsTabMerge.add(results);
        resultsTabMerge.setVisible(true);
    }

    /**
     * Incia o gerenciador de regras da parte de inferencia
     */
    public void callManager() {
        //Se um dos metodos estiver ativo, ou seja, "Context Key" ou "Similarity"
        if (!manager.getContextKey().isEmpty() || !manager.getSimilarity().isEmpty()) {
            RuleConstructInterface ruleMainInterface = new RuleConstructInterface(manager, isSimilarity, resultsTab.getInferenceFileChooser(), documentsTab);
            if (RuleConstructInterface.succeeded) {
//            if(!isSimilarity){ //Se o metodo utilizado for o "Context Key"
//                manager.startResultsInferenciaContextKey();
//            } else{ //Se o metodo utilizado for o "Similarity"
//                manager.startResultsInferenciaSimilarity(documents);
//            }
                resultsTab.refresh(documents, manager);
                tabbedPane.setSelectedIndex(2);
            }
        } else { //Se nenhum metodo estiver ativo
            JOptionPane.showMessageDialog(this, "No method started!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void callManagerSemantic() {
        //Se um dos metodos estiver ativo, ou seja, "Context Key" ou "Similarity"
        if (!manager.getContextKey().isEmpty() || !manager.getSimilarity().isEmpty()) {
            RuleConstructInterface ruleMainInterface = new RuleConstructInterface(manager, isSimilarity, resultsTabSemanticMerge.getInferenceFileChooser(), documentsTabMerge);
            //Selecionando todos os documentos para executar as regras
            //boolean marked[] = new boolean[manager.getContextKey().size()];
            //for (int i = 0; i < manager.getContextKey().size(); i++)
            //    marked[i] = true;
            
           // manager.startResultsInferenciaContextKey(marked);
            if (!isSemanticMerge) {
                resultsTabSemanticMerge.refresh(documents, manager);
                tabbedPaneMerge.setSelectedIndex(2);
            } else {
                semanticMerge(manager);
                tabbedPaneMerge.setSelectedIndex(2);
            }

        } else { //Se nenhum metodo estiver ativo
            JOptionPane.showMessageDialog(this, "No method started!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inicia o About do XChange
     */
    public void callAboutXChange() {
        AboutXChange aboutXChange = new AboutXChange();
    }

    /**
     * Inicia o About do GET
     */
    public void callAboutGET() {
        AboutGET aboutGET = new AboutGET();
    }

    /**
     * Inicia um novo projeto apagando a lista de documentos e atualizando os
     * componentes gráficos para seu padrão
     */
    private void newProject() {
        documents = new Documents();
        if (isSyntaticMerge) {
            tabbedPaneMerge.setEnabledAt(1, false);
            tabbedPaneMerge.setEnabledAt(2, false);
            tabbedPaneMerge.setEnabledAt(3, false);
            tabbedPaneMerge.setEnabledAt(4, false);
            tabbedPaneMerge.setSelectedIndex(0);
            conflictsTabMerge.removeAll();
            resultsTabMerge.removeAll();
            treesTabMerge.removeAll();
            mergeTreeTabMerge.removeAll();
            mergeBtn.setEnabled(false);
            applyChoicesBtn.setEnabled(false);
            writeBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
            miMerge.setEnabled(false);
            miApplyChoice.setEnabled(false);
            miWriteMerged.setEnabled(false);
            miMergeCancel.setEnabled(false);
        } else {
            tabbedPane.setSelectedIndex(0);
            tabbedPane.setEnabledAt(1, false);
            tabbedPane.setEnabledAt(2, false);
            manager.refreshFullManager();
            miManager.setEnabled(false);
            managerBtn.setEnabled(false);
            saveXMLDiffBtn.setEnabled(false);
            reportBtn.setEnabled(false);
        }
        this.refresh(documents);
    }

    /**
     * Inicia a inferenia por chave de contexto
     */
    private void contextKey() {
        if (documents.getSize() >= 2) {
            this.isSimilarity = false;
            manager.getSimilarity().clear();
            manager.startContextKey(documents); //Inicia arquivos como ContextKey
            JOptionPane.showMessageDialog(this, "Context Key successfully started!", "Success", JOptionPane.INFORMATION_MESSAGE);
            managerBtn.setEnabled(true);
            miManager.setEnabled(true);
            this.refresh(documents);
        } else {
            JOptionPane.showMessageDialog(this, "There must be at least two XML documents loaded!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inicia a inferência por similaridade
     */
    private void similarity() {
        if (documents.getSize() >= 2) {
            try {
                SettingsDialog settingsFrame = new SettingsDialog(this);
                settingsFrame.show();
                this.similarityRate = Float.parseFloat(JOptionPane.showInputDialog("Similarity Rate:"));
                this.isSimilarity = true;
                manager.getContextKey().clear();
                manager.startSimilarity(documents);
                JOptionPane.showMessageDialog(this, "Similarity successfully started!", "Success", JOptionPane.INFORMATION_MESSAGE);
                managerBtn.setEnabled(true);
                miManager.setEnabled(true);
                saveXMLDiffBtn.setEnabled(true);
                tabbedPane.setEnabledAt(4, true);
                xmldifftab.revalidate();
            } catch (Exception a) {
            }
        } else {
            JOptionPane.showMessageDialog(this, "There must be at least two XML documents loaded!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adiciona um arquivo XML ao projeto que não havia sido adicionado
     * anteriormente
     */
    private void addXMLFile() {
        try {
            File f;
            try {
                f = XMLLoader.open();
                this.documents.add(f);
                this.refresh(documents);
                if (isSyntaticMerge || isSemanticMerge) {

                    tabbedPaneMerge.setSelectedIndex(0);
                } else {
                    if (documents.getSize() >= 2) {
                        if (isSyntaticDiff) {
                            tabbedPane.setEnabledAt(1, true);
                            tabbedPane.setEnabledAt(2, true);
                        } else {
                            tabbedPane.setEnabledAt(1, false);
                            tabbedPane.setEnabledAt(2, false);
                        }
                    } else {
                        tabbedPane.setEnabledAt(1, false);
                        tabbedPane.setEnabledAt(2, false);
                    }
                }
                this.refresh(documents);
                managerBtn.setEnabled(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error on load the file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NoSelectedFileException ex) {
            System.out.println("\nNo file was selected\n");
        }
    }

    /**
     * @return O botão de aplicar escolhas
     */
    public JButton getApplyChoicesBtn() {
        return applyChoicesBtn;
    }

    /**
     * @return O menu de aplicar escolhas
     */
    public JMenuItem getMiApplyChoices() {
        return miApplyChoice;
    }

    /**
     * @return O botão de criar o merge e fechar
     */
    public JButton getWriteMergedBtn() {
        return writeBtn;
    }

    /**
     * @return O menu de criar o merge e fechar
     */
    public JMenuItem getMiWriteMerged() {
        return miWriteMerged;
    }

    /**
     * @return O botão de cancelar o merge
     */
    public JButton getCancelBtn() {
        return cancelBtn;
    }

    /**
     * @return O botão de cancelar o merge
     */
    public JMenuItem getMiMergeCancel() {
        return miMergeCancel;
    }

    /**
     * Seleciona a aba do gerenciador de aba pelo seu indice
     *
     * @param ind
     */
    public void selectTabbedPaneMerged(int ind) {
        tabbedPaneMerge.setSelectedIndex(ind);
    }

    public boolean getSimilarity() {
        return this.isSimilarity;
    }

    /**
     * Obtém a largura do JFrame, com a finalidade de ajustar os JSplitPane do
     * Syntatic Merge
     */
    public static double getWidthJFrame() {
        return jFrame.getSize().getWidth();
    }

    public float getSimilarityRate() {
        return this.similarityRate;
    }
}

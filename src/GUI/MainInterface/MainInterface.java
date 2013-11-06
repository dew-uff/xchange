package GUI.MainInterface;
import Documents.Documents;
import GUI.About.*;
import GUI.FileManager.MultiDiffSaver;
import Exception.NoSelectedFileException;
import GUI.FileManager.ProjectLoader;
import GUI.FileManager.ProjectSaver;
import GUI.FileManager.XMLLoader;
import GUI.Layout.LayoutConstraints;
import GUI.Rules.RuleMainInterface;
import GUI.Util.ProgressBar;
import GUI.Util.ProgressHandler;
import Manager.Manager;
import Merge.MergeShow;
import gems.ic.uff.br.newView.MergeThreeWayPanel;
import gems.ic.uff.br.newView.SettingsDialog;
import gems.ic.uff.br.newView.TreePanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * @author Marcio Tadeu de Oliveira Júnior
 */
public class MainInterface extends JFrame implements ActionListener{
    private JMenuBar menuBar;
    private JMenu mFile,mTools, mAbout;
    private JMenuItem miNew,miOpen,miSave,miContext,miSimilarity, miAdd, miAboutGET, miAboutXChange, miHowToUse,miManager,miMerge, miInference;
    private ResultsTab resultsTab;
    private DocumentsTab documentsTab;
    private PrologFactsTab prologFactsTab;
    private JTabbedPane tabbedPane, tabbedPaneMerge;
    private Documents documents;
    private JButton newBtn,openBtn,addBtn,saveBtn,contextBtn,similarityBtn,managerBtn, mergeBtn, saveXMLDiffBtn;
    private static JButton applyChoicesBtn, writeBtn, cancelBtn;
    private boolean isSimilarity;
    private float similarityRate;
    private Manager manager;
    private JPanel resultsTabMerge, conflictsTabMerge, treesTabMerge, mergeTreeTabMerge;
    private DocumentsTabMerge documentsTabMerge;
    private Boolean isMerge = false;
    private SintaticDiffTree sintaticDiff;
    private XMLDiffTab xmldifftab;
    private static JFrame jFrame;
    private ProgressBar pBar;
    
    public MainInterface(Manager manager){//define caracteriscas da janela, tais como tamanho e redimensionamento
        super("XChange");//contrutor, nomeia a janela
        this.manager = manager;//passa à interface qual seu gerenciador de regras
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//função ao clicar no botão fechar
        Toolkit tk = Toolkit.getDefaultToolkit();//objeto para pegar as dimensoes da tela
        Dimension dim = tk.getScreenSize();//funçao para pegar as dimensoes da tela
        this.setSize(7*(int)dim.getWidth()/8,7*(int)dim.getHeight()/8);//tamanho inicial da interface grafica
        dim.setSize(dim.getWidth()/3,dim.getHeight()/3);//tamanho mínimo da interface grafica
        this.setMinimumSize(dim);//seta o tamanho mínimo da interface grafica
        this.setLocationRelativeTo(null);//posicao inicial central
        this.buildInterface(); //cria o conteudo da janela principal
    }
    
    private void buildInterface(){//monta a interface grafica
        
        //lista de documentos XML abertos
        this.documents = new Documents();
        
        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();        
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
            
        //cria o item "Tools" na barra de menus
            
        mTools = new JMenu("Tools");
        mTools.setMnemonic('o');
        menuBar.add(mTools); 
        mTools.addActionListener(this);
            
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
            
            miSimilarity = new JMenuItem("Similarity");
            miSimilarity.setMnemonic('s');
            mTools.add(miSimilarity);
            miSimilarity.addActionListener(this);
           
            miMerge = new JMenuItem("Merge");
            miMerge.setMnemonic('g');
            mTools.add(miMerge);
            miMerge.addActionListener(this);
            
            miInference = new JMenuItem("Inference");
            miInference.setMnemonic('i');
            miInference.setVisible(false);
            mTools.add(miInference);
            miInference.addActionListener(this);
            
            mTools.addSeparator();
            
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
        ImageIcon showFactsIcon = new ImageIcon(getClass().getResource("/GUI/icons/showFacts.png"));
        ImageIcon saveXMLDiffIcon = new ImageIcon(getClass().getResource("/GUI/icons/save.png"));
        
        //Cria os botões e seus eventos
        newBtn = new JButton(newIcon);
        newBtn.setToolTipText("New Project");
        newBtn.addActionListener(this);
        
        openBtn = new JButton(openIcon);
        openBtn.setToolTipText("Open Project");
        openBtn.addActionListener(this);
        
        addBtn = new JButton(addIcon);
        addBtn.setToolTipText("Add XML");
        addBtn.addActionListener(this);
        
        saveBtn = new JButton(saveIcon);
        saveBtn.setToolTipText("Save");
        saveBtn.addActionListener(this);
        
        contextBtn = new JButton(contextIcon);
        contextBtn.setToolTipText("Context Key");
        contextBtn.addActionListener(this);
        
        similarityBtn = new JButton(similarityIcon);
        similarityBtn.setToolTipText("Similarity");
        similarityBtn.addActionListener(this);
        
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
        tBar.setAlignmentX(0);
        
        tBar.setFloatable(false); //Fixa a barra de ferramentas à sua posição
        
        //Adiciona a barra de ferramentas ao seu painel
        buttonsPane.add(tBar);
        
        //Adiciona o painel da barra de ferramentas à interface gráfica
        this.add(buttonsPane);
        
        //indica a posição e layout da barra de ferramentas
        LayoutConstraints.setConstraints(constraints,0,0,1,1,100,1);
        constraints.insets=new Insets(0,0,0,0);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(buttonsPane,constraints);       
                   
        //cria a aba de resultados
        resultsTab = new ResultsTab();
        
        //cria a aba de documentos
        documentsTab = new DocumentsTab(manager,this);
        
        //cria a aba de documentos
        prologFactsTab = new PrologFactsTab(manager,this);
            
        //cria o gerenciador de abas adicionando as abas de resultados e documentos
        tabbedPane = new JTabbedPane();
        tabbedPane.add(documentsTab,"Documents");//adiciona a aba de documentos
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setVisible(true);
        
        //cria o gerenciador de abas adicionando as abas de resultados e documentos
        tabbedPane.add(prologFactsTab,"Prolog Facts");//adiciona a aba de fatosprolog
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setVisible(true);
        
        //cria o gerenciador de abas adicionando as abas de resultados e documentos
        tabbedPane.add(resultsTab,"Results");//adiciona a aba de resultados
        tabbedPane.setEnabledAt(2, false);
        tabbedPane.setVisible(true);
       
        sintaticDiff = new SintaticDiffTree(documents); 
        tabbedPane.add(sintaticDiff, "Sintatic Diff Tree"); //adiciona de arvores (Diff Sintatico)
        tabbedPane.setEnabledAt(3, false);
        sintaticDiff.setVisible(true);
        
        xmldifftab = new XMLDiffTab();
        tabbedPane.add(xmldifftab, "XML Diff");
        tabbedPane.setEnabledAt(4,false);
        xmldifftab.setVisible(true);
        
        // Adiciona um evento para mudança de aba
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(tabbedPane.getSelectedIndex()==1 && !prologFactsTab.alreadySet()){
                    prologFactsTab.setLeftCB(documentsTab.getLeftCBIndex());
                    prologFactsTab.setRightCB(documentsTab.getRightCBIndex());
                }else
                if(tabbedPane.getSelectedIndex()==3)
                    sintaticDiff.plotTree();
                else
                if(tabbedPane.getSelectedIndex()==4){
                    sintaticDiff.setVisible(false);
                    xmldifftab.showXMLDiff();
                }
            }
        });
        
        documentsTabMerge = new DocumentsTabMerge();
        resultsTabMerge = new JPanel();
        conflictsTabMerge = new JPanel();
        treesTabMerge = new JPanel();
        mergeTreeTabMerge = new JPanel();
                
                
        //cria o gerenciador de abas do merge adicionando as abas de documentos, resultados e conflitos
        tabbedPaneMerge = new JTabbedPane(); 
        tabbedPaneMerge.add(documentsTabMerge,"Documents");//adiciona a aba de documentos
        tabbedPaneMerge.add(resultsTabMerge,"Results");//adiciona a aba de resultados
        tabbedPaneMerge.add(conflictsTabMerge,"Conflicts");//adiciona a aba de conflitos
        tabbedPaneMerge.add(treesTabMerge, "Trees"); //adiciona a aba das Arvores XML
        tabbedPaneMerge.add(mergeTreeTabMerge,"Merge Tree");//adiciona a aba de Merge Three (arvores)
        tabbedPaneMerge.setEnabledAt(1, false);
        tabbedPaneMerge.setEnabledAt(2, false);
        tabbedPaneMerge.setEnabledAt(3, false);
        tabbedPaneMerge.setEnabledAt(4, false);
        tabbedPaneMerge.setVisible(false);
       
        //indica a posição e layout do gerenciador de abas
        LayoutConstraints.setConstraints(constraints,0,1,1,1,100,100);
        constraints.insets=new Insets(0,10,0,10);        
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor=GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(tabbedPane,constraints);
        gridBag.setConstraints(tabbedPaneMerge,constraints);

        //adiciona o gerenciador de abas do merge à interface grafica
        this.add(tabbedPaneMerge); 
        
        //adiciona o gerenciador de abas à interface grafica
        this.add(tabbedPane);
        
        LayoutConstraints.setConstraints(constraints,0,1,1,1,100,100);
        constraints.insets=new Insets(0,10,0,10);        
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor=GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(tabbedPane,constraints);
        gridBag.setConstraints(tabbedPaneMerge,constraints);
        
        pBar = ProgressHandler.makeNew(100, "Ready: Waiting for User");
        ProgressHandler.stop();
        
        LayoutConstraints.setConstraints(constraints,0,2,1,1,1,1);
        constraints.insets=new Insets(0,0,0,0);        
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor=GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(pBar,constraints);
        
        this.add(pBar);
       
        //inicia o programa zerado
        this.refresh(this.documents);
        
        //torna a janela visivel
        this.setVisible(true);
        
        this.revalidate();
        this.repaint();
 }
    
    /**
     * Atualiza as informações na tela inclusive seus subcomponentes
     * @param documents 
     */
    public void refresh(Documents documents){//modifica a tela de acordo com que novos documentos XML são adcionados ao projeto, ou quand se inicia um novo projeto
        if (isMerge){
            documentsTabMerge.refresh(documents);
            if(documents.getSize()>=3){
                mergeBtn.setEnabled(true);
            }
        }
        else{
            resultsTab.refresh(documents,manager);
            documentsTab.refresh(documents);
            sintaticDiff.refresh(documents);
            xmldifftab.refresh(documents);
            prologFactsTab.refresh(documents);
            if(documents.getSize()>=2){
                miContext.setEnabled(true);
                contextBtn.setEnabled(true);
                miSave.setEnabled(true);
                saveBtn.setEnabled(true);
                miSimilarity.setEnabled(true);
                similarityBtn.setEnabled(true);
            }else{
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
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource().equals(miAdd)||e.getSource().equals(addBtn)){//ação "ADICIONAR"
            addXMLFile();
        }else
        if(e.getSource().equals(similarityBtn)||e.getSource().equals(miSimilarity)){//ação "SIMILARIDADE"
            similarity();
            tabbedPane.setEnabledAt(1, true);
            this.repaint();
        }else
            
        if(e.getSource().equals(contextBtn)||e.getSource().equals(miContext)){//ação "CHAVE DE CONTEXTO"
            contextKey();
            tabbedPane.setEnabledAt(1, true);
        }else
            
        if(e.getSource().equals(newBtn)||e.getSource().equals(miNew)){//ação "NOVO"
            newProject();
        }else
            
        if(e.getSource().equals(saveBtn)||e.getSource().equals(miSave)){//ação "SALVAR"
            try {
                ProjectSaver.save(documents);
            } catch (NoSelectedFileException ex) {
            } catch (Exception ex){
                JOptionPane.showMessageDialog(null, "Error on save Project", "Error",JOptionPane.ERROR_MESSAGE);
            }
        }else
            
        if(e.getSource().equals(openBtn)||e.getSource().equals(miOpen)){//ação "ABRIR"
            try {
                this.documents = ProjectLoader.load();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error on load Project", "Error",JOptionPane.ERROR_MESSAGE);
            } catch (NoSelectedFileException ex) {
            }
            this.refresh(this.documents);
        }else
            
        if(e.getSource().equals(managerBtn)||e.getSource().equals(miManager)){//ação "MANAGER"
            callManager();
            tabbedPane.setEnabledAt(2, true);
        }
        
        else if(e.getSource().equals(miHowToUse)){//Abre o canal do youtube que contem os tutoriais do XChange
            try { 
                URI tutoURL = new URI("http://www.youtube.com/watch?v=hKO_NDfLX-s&list=PLa7uZeJ8iahfcw62gG8-qrP3RSCLykKcz&feature=plpp_play_all");
                try {
                    Desktop.getDesktop().browse(tutoURL);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Genric error", "Error",JOptionPane.ERROR_MESSAGE);
                }
            } catch (URISyntaxException ex) {
                JOptionPane.showMessageDialog(null, "Error on get URL", "Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        
        else if(e.getSource().equals(miAboutXChange)){//abre About XChange
            try{
                callAboutXChange();
            }catch (Exception ex){
                JOptionPane.showMessageDialog(null, "Error on AboutXChange", "Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        
        else if(e.getSource().equals(miAboutGET)){//abre About GET
            try{
                callAboutGET();
            }catch (Exception ex){
                JOptionPane.showMessageDialog(null, "Error on AboutGET", "Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        
        else if(e.getSource().equals(miInference)){//ação "Inference"
            setInference();
        }
        
        else if(e.getSource().equals(miMerge)){//ação "Merge"
            setMerge();
        }
        
        else if(e.getSource().equals(mergeBtn)){//ação "Merge"
            merge();
        }
        
        else if(e.getSource().equals(saveXMLDiffBtn)){//ação "Save XML Diff"
            try {  
                MultiDiffSaver.save(documents);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Diff saving failure!", "Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Inicia o modo de inferencia habilitando componentes necessários à inferencia e desabilitando componentes exclusivamente necessários à opção de merge
     */
    public void setInference(){
        isMerge = false;
        miMerge.setVisible(true);
        miManager.setVisible(true);
        miContext.setVisible(true);
        miSimilarity.setVisible(true);
        miInference.setVisible(false);
        tabbedPaneMerge.setVisible(false);
        tabbedPane.setVisible(true);
        contextBtn.setVisible(true);
        similarityBtn.setVisible(true);
        managerBtn.setVisible(true);
        mergeBtn.setEnabled(false);
        mergeBtn.setVisible(false);
        applyChoicesBtn.setEnabled(false);
        applyChoicesBtn.setVisible(false);
        writeBtn.setEnabled(false);
        writeBtn.setVisible(false);
        cancelBtn.setEnabled(false);
        cancelBtn.setVisible(false);
        saveXMLDiffBtn.setVisible(true);
        this.refresh(documents);
    }
    /**
     * Inicia o modo de inferencia habilitando componentes necessários à opção de merge e desabilitando componentes exclusivamente necessários à inferencia
     */    
    public void setMerge(){
        isMerge = true;
        miMerge.setVisible(false);
        miManager.setVisible(false);
        miContext.setVisible(false);
        miSimilarity.setVisible(false);
        miInference.setVisible(true);
        tabbedPane.setVisible(false);
        tabbedPaneMerge.setVisible(true);
        similarityBtn.setVisible(false);
        contextBtn.setVisible(false);
        managerBtn.setVisible(false);
        mergeBtn.setVisible(true);
        applyChoicesBtn.setEnabled(false);
        applyChoicesBtn.setVisible(true);
        writeBtn.setEnabled(false);
        writeBtn.setVisible(true);
        cancelBtn.setEnabled(false);
        cancelBtn.setVisible(true);
        saveXMLDiffBtn.setVisible(false);
        this.refresh(documents);
    }
    
    /**
     * Constroi a interface de merge
     */
    public void merge(){
        MergeShow mergeshow = new MergeShow();
        mergeshow.merge(documentsTabMerge.getPathDocLeft(), documentsTabMerge.getPathDocCenter(), documentsTabMerge.getPathDocRight(), this);
        conflictsTabMerge.removeAll();
        resultsTabMerge.removeAll();
        treesTabMerge.removeAll();
        mergeTreeTabMerge.removeAll();
        
        applyChoicesBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
        
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        //indica a posição e o layout
        LayoutConstraints.setConstraints(gridBagConstraints,0,0,1,1,1,1);
        gridBagConstraints.insets=new Insets(2,2,2,2);
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
        
        if(mergeshow.getConflictsJP()!= null){
            conflictsTabMerge.setLayout(gridBag);
            JPanel conflictsJP = mergeshow.getConflictsJP();       
            gridBag.setConstraints(conflictsJP,gridBagConstraints);
            conflictsTabMerge.add(conflictsJP);
            tabbedPaneMerge.setEnabledAt(2, true);
            tabbedPaneMerge.setSelectedIndex(2);                    
        } else {
            tabbedPaneMerge.setSelectedIndex(1);
        }
        resultsTabMerge.setLayout(gridBag);
        JPanel resultsJP = mergeshow.getResultsJP();
        gridBag.setConstraints(resultsJP,gridBagConstraints);
        resultsTabMerge.add(resultsJP);
        tabbedPaneMerge.setEnabledAt(1, true);
        
        // Arvores do Phoenix
        treesTabMerge.setLayout(gridBag);
        JPanel trees = new TreePanel(documents.getContent(1), documents.getContent(2));
        gridBag.setConstraints(trees, gridBagConstraints);
        treesTabMerge.add(trees);
        tabbedPaneMerge.setEnabledAt(3, true);        
       
        mergeTreeTabMerge.setLayout(gridBag);
        JPanel mergeTree = new MergeThreeWayPanel(documents.getContent(0), documents.getContent(1), documents.getContent(2));
        gridBag.setConstraints(mergeTree, gridBagConstraints);
        mergeTreeTabMerge.add(mergeTree);
        tabbedPaneMerge.setEnabledAt(4, true);
        
        this.refresh(documents);
    }
    
    /**
     * Atualiza a aba de resultado do merge
     */
     public void updateResults(JPanel results){
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        //indica a posição e o layout
        LayoutConstraints.setConstraints(gridBagConstraints,0,0,1,1,1,1);
        gridBagConstraints.insets=new Insets(2,2,2,2);
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
        resultsTabMerge.setLayout(gridBag);
        resultsTabMerge.setVisible(false);
        resultsTabMerge.removeAll();
        gridBag.setConstraints(results,gridBagConstraints);
        resultsTabMerge.add(results);
        resultsTabMerge.setVisible(true);
     }
    
  /**
   * Incia o gerenciador de regras da parte de inferencia
   */
    public void callManager(){
        //Se um dos metodos estiver ativo, ou seja, "Context Key" ou "Similarity"
        if(!manager.getContextKey().isEmpty() || !manager.getSimilarity().isEmpty()){  
            RuleMainInterface ruleMainInterface = new RuleMainInterface(manager, isSimilarity,resultsTab.getInferenceFileChooser());
            if(!isSimilarity){ //Se o metodo utilizado for o "Context Key"
                manager.startResultsInferenciaContextKey();
            } else{ //Se o metodo utilizado for o "Similarity"
                manager.startResultsInferenciaSimilarity(documents);
            }
            resultsTab.refresh(documents,manager);
            tabbedPane.setSelectedIndex(2);
        } else{ //Se nenhum metodo estiver ativo
            JOptionPane.showMessageDialog(this, "No method started!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Inicia o About do XChange
     */
    public void callAboutXChange(){
        AboutXChange aboutXChange = new AboutXChange();
    }
    
    /**
     * Inicia o About do GET
     */
    public void callAboutGET(){
        AboutGET aboutGET = new AboutGET();
    }

    /**
     * Inicia um novo projeto apagando a lista de documentos e atualizando os componentes gráficos para seu padrão
     */
    private void newProject() {
        documents = new Documents();
        if(isMerge){
            tabbedPaneMerge.setEnabledAt(1, false);
            tabbedPaneMerge.setEnabledAt(2, false);
            tabbedPaneMerge.setEnabledAt(3, false);
            tabbedPaneMerge.setEnabledAt(4, false);
            tabbedPaneMerge.setSelectedIndex(0);
            tabbedPane.setEnabledAt(1,false);
            tabbedPane.setEnabledAt(2,false);
            tabbedPane.setEnabledAt(3, false);
            tabbedPane.setEnabledAt(4, false);
            tabbedPane.setSelectedIndex(0);
            conflictsTabMerge.removeAll();
            resultsTabMerge.removeAll();
            treesTabMerge.removeAll();
            mergeTreeTabMerge.removeAll();
            mergeBtn.setEnabled(false);
            applyChoicesBtn.setEnabled(false);
            writeBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
            manager.refreshFullManager();
            miManager.setEnabled(false);
            managerBtn.setEnabled(false);
            saveXMLDiffBtn.setEnabled(false);
        }
        else{              
            tabbedPane.setSelectedIndex(0);
            tabbedPane.setEnabledAt(1,false);
            tabbedPane.setEnabledAt(2,false);
            tabbedPane.setEnabledAt(3, false);
            tabbedPane.setEnabledAt(4, false);
            manager.refreshFullManager();
            miManager.setEnabled(false);
            managerBtn.setEnabled(false);
            saveXMLDiffBtn.setEnabled(false);
        }            
        this.refresh(documents);
    }

    /**
     * Inicia a inferenia por chave de contexto
     */
    private void contextKey() {
        if(documents.getSize() >= 2){
            this.isSimilarity=false;
            manager.getSimilarity().clear();
            manager.startContextKey(documents); //Inicia arquivos como ContextKey
            JOptionPane.showMessageDialog(this, "Context Key successfully started!", "Success", JOptionPane.INFORMATION_MESSAGE);
            managerBtn.setEnabled(true);
            miManager.setEnabled(true);
            this.refresh(documents);
        } else{   
            JOptionPane.showMessageDialog(this, "There must be at least two XML documents loaded!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inicia a inferência por similaridade
     */
    private void similarity() {
        if(documents.getSize() >= 2){
            try{
                SettingsDialog settingsFrame = new SettingsDialog(this);
                settingsFrame.show();
                this.similarityRate = Float.parseFloat(JOptionPane.showInputDialog("Similarity Rate:"));
                this.isSimilarity=true;
                manager.getContextKey().clear();
                manager.startSimilarity(documents);
                JOptionPane.showMessageDialog(this, "Similarity successfully started!", "Success", JOptionPane.INFORMATION_MESSAGE);
                managerBtn.setEnabled(true);
                miManager.setEnabled(true);
                saveXMLDiffBtn.setEnabled(true);
                tabbedPane.setEnabledAt(4, true);
                xmldifftab.revalidate();
            }catch(Exception a){  
            }
        } else{
            JOptionPane.showMessageDialog(this, "There must be at least two XML documents loaded!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Adiciona um arquivo XML ao projeto que não havia sido adicionado anteriormente
     */
    private void addXMLFile() {
        try {
            File f;
            try {
                f = XMLLoader.open();
                this.documents.add(f);
                this.refresh(documents);
                if (isMerge){
                    tabbedPaneMerge.setSelectedIndex(0);
                }
                else{
                    if(documents.getSize()>=2){
                        tabbedPane.setEnabledAt(1, false);
                        tabbedPane.setEnabledAt(2, false);
                        tabbedPane.setEnabledAt(3, true);
                        tabbedPane.setEnabledAt(4, true);
                    } else {
                        tabbedPane.setEnabledAt(1, false);
                        tabbedPane.setEnabledAt(2, false);
                        tabbedPane.setEnabledAt(3, false);
                        tabbedPane.setEnabledAt(4, false);
                    }                        
                }
                this.refresh(documents);
                managerBtn.setEnabled(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error on load the file", "Error",JOptionPane.ERROR_MESSAGE);
            }
        } catch (NoSelectedFileException ex) {
            System.out.println("\nNo file was selected\n");
        }
    }
    
    /**
     * @return O botão de aplicar escolhas
     */
    public static JButton getApplyChoicesBtn(){
        return applyChoicesBtn;
    }
    
    /**
     * @return O botão de criar o merge e fechar
     */
    public static JButton getWriteMerged(){
        return writeBtn;
    }
    
    /**
     * @return O botão de cancelar o merge
     */
    public static JButton getCancelBtn(){
        return cancelBtn;
    }
   
    /**
     * Seleciona a aba do gerenciador de aba pelo seu indice
     * @param ind 
     */
    public void selectTabbedPaneMerged(int ind){
        tabbedPaneMerge.setSelectedIndex(ind);
    }
    
    public boolean getSimilarity(){
        return this.isSimilarity;
   }
    
    public static double getWidthJFrame (){
       return jFrame.getSize().getWidth();
   }
    
    public float getSimilarityRate(){
        return this.similarityRate;
    }
    
}
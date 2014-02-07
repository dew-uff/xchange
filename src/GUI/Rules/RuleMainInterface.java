package GUI.Rules;
import GUI.FileManager.PLOpener;
import GUI.Layout.LayoutConstraints;
import GUI.MainInterface.DocumentsTab;
import GUI.MainInterface.InferenceFileChooser;
import Manager.Manager;
import Rules.RulesModule;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Guilherme Martins, Marcio Oliveira Junior e Celio H. N. Larcher Junior
 */
public class RuleMainInterface extends JDialog implements ActionListener{
    
    private Manager manager;
    private DocumentsTab documentsTab;
    private RulesModule inferenceModule;
    private JButton btnOpenRules, btnCreateRules, btnDone, btnIdentifyRules, btnSelectAll, btnSaveRules;
    private JPanel pnlTop, pnlBotton, pnlCenter;
    private JTextArea txtPane;
    public boolean correctClose = false, similarityActive;
    private ArrayList<JCheckBox> rulesSelect;
    private JScrollPane scrlPane;
    private InferenceFileChooser inferenceFileChooser;
    private GridBagConstraints constraints;
    private GridBagLayout generalGridBag,centerGridBag;
    
    /**
     * Exibe a janela para o gerenciamento das regras.
     * @param manager
     * Objeto do tipo "Manager" que chamou esta função.
     * @param isSimilarity
     * Booleano que indica se o método escolhido foi "Context Key" ou "Similarity"
     * @param inferenceFileChooser 
     */
    public RuleMainInterface(Manager manager, boolean isSimilarity,InferenceFileChooser inferenceFileChooser, DocumentsTab documentsTab){

        //atribuições de manager,modulo de inferencia, similaridade, e da lista de documentos a serem mostrados no diff
        this.manager = manager;
        this.documentsTab = documentsTab;
        this.inferenceModule = manager.getRulesModule();
        similarityActive = isSimilarity;
        this.inferenceFileChooser=inferenceFileChooser;
        
        //cria definições da tela
        Dimension btnSize = new Dimension(110,25);//dimensão padrão dos botões
        this.setPreferredSize(new Dimension(367,450));
        this.setMinimumSize(new Dimension(367,200));

        
        //cria os gerenciadores de layout
        constraints = new GridBagConstraints();
        generalGridBag = new GridBagLayout();        
        this.setLayout(generalGridBag);        
        
        /** painel superior **/
        pnlTop = new JPanel();
        
        //botão para abrir regras
        btnOpenRules = new JButton("Load Rules");
        btnOpenRules.addActionListener(this);
        btnOpenRules.setPreferredSize(btnSize);
        btnOpenRules.revalidate();
        pnlTop.add(btnOpenRules);
        
        //botão para criar regras
        btnCreateRules = new JButton("Create Rules");
        btnCreateRules.addActionListener(this); 
        btnCreateRules.setPreferredSize(btnSize);
        btnCreateRules.revalidate();
        pnlTop.add(btnCreateRules); 
        
        //adciona o painel superior à janela
        this.add(pnlTop);
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1); 
        generalGridBag.setConstraints(pnlTop, constraints);
        
        /** painel central **/
        
        //layout do painel central
        centerGridBag = new GridBagLayout();
        
        //cria o painel central
        pnlCenter = new JPanel();
        pnlCenter.setLayout(centerGridBag);
        
        //cria o painel de texto,onde ficarão as regras
        txtPane = new JTextArea();
        
        String textView;
        textView = formatSetTextPane(inferenceModule.getRulesString()); //Formata as regras que serão exibidas na tela
        txtPane.setText(txtPane.getText()+"\n"+textView);
        txtPane.setToolTipText("Create or select a file that contains the Prolog rules on context.");
        txtPane.setLineWrap(true);
        txtPane.setWrapStyleWord(true);
        txtPane.setAutoscrolls(true);
        
        scrlPane = new JScrollPane(txtPane);
        
        //gerencia olayout do painel central
        constraints.fill = GridBagConstraints.BOTH;
        centerGridBag.setConstraints(scrlPane, constraints);
        
        //adiciona o painel de texto ao painel central
        pnlCenter.add(scrlPane);
        
        //adiciona o painel central à janela
        this.add(pnlCenter);
        constraints.insets = new Insets(5,5,5,5);
        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 1000, 1000); 
        constraints.anchor = GridBagConstraints.NORTHWEST;
        generalGridBag.setConstraints(pnlCenter, constraints);
        constraints.insets = new Insets(0,0,0,0);
        
        /** painel inferior **/
        pnlBotton = new JPanel();
        
        //cria o botão done
        btnDone = new JButton("Done");
        btnDone.addActionListener(this);
        btnDone.setVisible(false);
        btnDone.setPreferredSize(btnSize);
        pnlBotton.add(btnDone);
        
        //cria o botão de identificar as regras 
        btnIdentifyRules = new JButton("Identify Rules");
        btnIdentifyRules.addActionListener(this);
        btnIdentifyRules.setPreferredSize(btnSize);
        pnlBotton.add(btnIdentifyRules);
        
        //cria o botão de salvar as regras
        btnSaveRules = new JButton("Save Rules");
        btnSaveRules.addActionListener(this);
        btnSaveRules.setVisible(false);
        btnSaveRules.setPreferredSize(btnSize);
        pnlBotton.add(btnSaveRules);    
        
        //cria o botão de selecionar as regras
        btnSelectAll = new JButton("Select all");
        btnSelectAll.addActionListener(this);
        btnSelectAll.setVisible(false);
        btnSelectAll.setPreferredSize(btnSize);
        pnlBotton.add(btnSelectAll);

        //addiciona o painel inferior 
        this.add(pnlBotton);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        LayoutConstraints.setConstraints(constraints, 0, 2, 1, 1, 1, 1); 
        constraints.anchor = GridBagConstraints.SOUTH;
        generalGridBag.setConstraints(pnlBotton, constraints);
        
        //modifica características da janela
        setModal(true);//não permite que a tela do XChange seja acessada antes desta ser fechada
        setTitle("Select Rules");//modifica o titulo
        setResizable(true);//permite redimensionar a janela
        setAlwaysOnTop(false);//esconde a janela se o XChange perder o foco 
        pack();//desenha a janela com o melhor tamanho para seus componentes
        
        Toolkit tk = Toolkit.getDefaultToolkit();//objeto para pegar as dimensoes da tela
        Dimension dim = tk.getScreenSize();//funçao para pegar as dimensoes da tela
        this.setLocation(((int)dim.getWidth()-this.getWidth())/2,((int)dim.getHeight()-this.getHeight())/2);//posiciona a janela no meio da tela 
        
        setVisible(true);//torna a janela visivel
    }

    /**
     * Trata as ações dos botões e das seleções realizadas pelo usuário.
     * @param ae 
     * Recebe um evento gerado.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
         if (ae.getSource() == btnSaveRules) {
            this.saveRules();
        } else if (ae.getSource() == btnDone) { //Ação do botão "Done"
            System.out.println(this.getSize());
            if (!txtPane.getText().isEmpty()) {
                ArrayList<String> selectedRules = new ArrayList<String>();
                int cont = 0;
                for (JCheckBox item : rulesSelect) { //Verifica quais regras foram selecionadas pelo usuário
                    if (item.isSelected()) {
                        cont++;
                        selectedRules.add(item.getName());
                    }
                }
                if (cont > 0) {
                    String paneRules = formatGetTextPane(txtPane.getText()); //Formata as regras obtidas através do Painel
                    
                    this.inferenceModule.addRules(paneRules); //Adiciona as regras do painel
                    this.inferenceModule.addSelectRules(selectedRules); //Adiciona as regras selecionadas em sua respectiva variável
                    this.inferenceModule.adjustRules(); //Remove as regras repetidas
                    this.inferenceFileChooser.setSelectedRules(selectedRules);//Envia ao InferenceFileChooser a lista de regras selecionadas
                    this.correctClose = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "It's necessary to difine the rules to "
                            + "realize inference of informations.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "It's necessary to difine the rules to "
                        + "realize inference of informations.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else if (ae.getSource() == btnOpenRules) { //Ação do botão "Load Rules"
            PLOpener open = new PLOpener();
            //String auxiliar para formatar as regras que carregadas que serão exibidas na tela
            String auxLoaded = open.getText(); 
            auxLoaded = auxLoaded.replaceAll("\t", "    "); //Substitui as tabulações das regras carregados por espaços
            txtPane.setText(txtPane.getText()+auxLoaded); //Exibe no painel as regras que já estavam nele juntamente com as regras carregadas
            txtPane.setCaretPosition(0);

        } else if (ae.getSource() == btnCreateRules) { //Ação do botão "Create new Rules"
            if (!txtPane.getText().equals("")) { //Se tem alguma regra carregada ou digitada, devo permitir usá-las no construtor
                String rulesInLine = txtPane.getText().replaceAll("\n", ""); //Eliminando quebras de linha
                rulesInLine = rulesInLine.replaceAll("\r", ""); //Eliminando quebras de linha
                rulesInLine = rulesInLine + "\n";
                String[] rulesPart = rulesInLine.split("\\."); //O final de cada regra é o delimitador "ponto"
                addLoadedRules(rulesPart);
            }
            new RuleConstructInterface(manager, similarityActive, documentsTab);

            String textView;
            textView = formatSetTextPane(inferenceModule.getRulesString()); //Formata as regras que serão exibidas na tela
            
            txtPane.setText("\n"+textView);

        } else if (ae.getSource() == btnIdentifyRules) { //Ação do botão "Identify Rules"
            if (!txtPane.getText().isEmpty()) {
                String rules = txtPane.getText();
                btnIdentifyRules.setEnabled(false);
                btnIdentifyRules.setVisible(false);
                pnlBotton.remove(btnIdentifyRules);
                btnOpenRules.setEnabled(false);
                btnCreateRules.setEnabled(false);

                String[] partRules = inferenceModule.partRules(rules); //Pega o cabeçalho das regras (ex: salary(NAME))
                txtPane.setVisible(false);
                setIdentifyRules(inferenceModule.getNameAndArgumentsRules(partRules));
            } else {
                JOptionPane.showMessageDialog(this, "It's necessary to difine the rules to "
                        + "realize inference of informations.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (ae.getSource() == btnSelectAll) { //Ações para o botão "Select all", utilizado para marcar todos quando existir pelo menos uma opção sem marcar, e para desmarcar todos quando todos estiverem marcados
            if (btnSelectAll.getText().equalsIgnoreCase("Select all")) {
                btnSelectAll.setText("Unselect all");
                for (JCheckBox item : rulesSelect) {
                    item.setSelected(true);
                }
            } else {
                btnSelectAll.setText("Select all");
                for (JCheckBox item : rulesSelect) {
                    item.setSelected(false);
                }
            }
        } else {
            btnSelectAll.setText("Select all");
            for (JCheckBox item : rulesSelect) {
                item.setSelected(false);
            }
        }
    }

    /**
     * Cria as caixas para a exibição das regras (nomes + argumentos).
     * @param rules
     * Recebe um vetor de String onde cada índice contém o nome da regra e os argumentos dela.
     * Ex: same_salario(Nome).
     */
    private void setIdentifyRules(String[] rules) {
        rulesSelect = new ArrayList<JCheckBox>();
        JPanel p = new JPanel();
        
        p.setLayout(new BoxLayout(p, WIDTH));
        pnlCenter.setLayout(new BoxLayout(pnlCenter, WIDTH));
        
        pnlCenter.removeAll();
        JScrollPane jscPane = new JScrollPane(p);
        pnlCenter.add(jscPane);
        
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill=GridBagConstraints.BOTH;
        centerGridBag.addLayoutComponent(p, constraints);
        
        p.setVisible(true);
        for (String rule : rules) { //Cria os campos do CheckBox de acordo com as regras inseridas pelo usuário
            JCheckBox chkItem = new JCheckBox(rule);
            chkItem.setName(rule);
            rulesSelect.add(chkItem);
            p.add(chkItem);
        }
        
        
        btnOpenRules.setEnabled(false);
        pnlBotton.add(btnSelectAll);
        btnSelectAll.setVisible(true);
        btnDone.setVisible(true);
        btnSaveRules.setVisible(true);
    }
    
    /**
     * Adiciona as regras carregadas de arquivo ou digitadas ao vetor de regras montadas pelo construtor.
     * @param loadedRules
     * Recebe um vetor de String onde cada índice contém o nome da regra e os argumentos dela.
     */
    private void addLoadedRules(String[] loadedRules) {
        for (String rule : loadedRules) {
            rule=rule+".";
            rule=rule.replaceAll("\n", "");
            rule=rule.replaceAll(" ", "");
            if(!rule.isEmpty()){
                if(rule.length()>1){
                    inferenceModule.addRules(rule);
                }    
            }            
        }
        inferenceModule.adjustRules();
    }
    
    /**
     * Formata as regras obtidas do textPane (tela) para que fiquem no formato adequado para serem salvas pelo XChange.
     * @param paneRules
     * String com as regras obtidas do textPane (tela).
     * @return textFormated
     * String com as regras formatadas.
     */
    private String formatGetTextPane (String paneRules) {
        String textFormated = paneRules;
        textFormated = textFormated.replaceAll("\n", ""); //Eliminando quebras de linha
        textFormated = textFormated.replaceAll("\r", ""); //Eliminando quebras de linha
        textFormated = textFormated.replaceAll("\t", ""); //Eliminando tabulações
        textFormated = textFormated.replaceAll(" ", ""); //Eliminando espaçoes em branco
        textFormated = textFormated.replaceAll("\\.", "\\.\n"); //Acrescenta quebra de linha entre as regras do painel

        return textFormated;
    }
    
    /**
     * Formata as regras que serão exibidas no textPane (tela) para que fiquem em um formato de fácil compreenssão para o usuário.
     * @param paneRules
     * String com as regras que serão exibidas no textPane (tela).
     * @return textFormated
     * String com as regras formatadas.
     */
    private String formatSetTextPane (String paneRules) {
        String textFormated = paneRules;
        textFormated = textFormated.replaceAll("\n", ""); //Eliminando quebras de linha
        textFormated = textFormated.replaceAll("\r", ""); //Eliminando quebras de linha
        textFormated = textFormated.replaceAll("\t", ""); //Eliminando tabulações
        textFormated = textFormated.replaceAll(" ", ""); //Eliminando espaçoes em branco
        textFormated = textFormated.replaceAll("\\.", "\\.\n\n"); //Acrescenta quebra de linha entre as regras do painel
        textFormated = textFormated.replaceAll("(\\:-|\\:-\n)","\\:-\n    "); //Acrescenta quebra de linha e espaços depois de ":-"
        textFormated = textFormated.replaceAll("(\\),|\\),\n)","\\),\n    "); //Acrescenta quebra de linha e espaços depois de "),"
        
        return textFormated;
    }
    
    /**
     * Salva as regras "pl". 
     * Usa o ultimo caminho em que as regras foram carregadas como default. 
     */
    public void saveRules(){
          try{
            File arqcam = new File("lastpathpl.txt");
            String path = "";
            if(arqcam.exists()){
                    BufferedReader leitor=new BufferedReader(new FileReader(arqcam));
                    path=leitor.readLine();
                    leitor.close();
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save");
            fileChooser.setCurrentDirectory(new File(path));
            int resp = fileChooser.showSaveDialog(this);
            if (resp != JFileChooser.APPROVE_OPTION) {
                return;
            }
            String[] preparado = {"test"};
            String arquivo1 = fileChooser.getSelectedFile().getAbsolutePath().toString();
            PrintWriter in;
            if(arquivo1.endsWith(".pl")) {
                  in= new PrintWriter(new File(arquivo1));
            } else {
                  in = new PrintWriter(new File(arquivo1)+".pl");
            }
            for(String regra:preparado){
                if(!regra.isEmpty()){
                    if(regra.length()>1){
                    regra=regra+".";
                    in.println();
                    in.println(regra);
                    }
                }
            }
            in.close();
            JOptionPane.showMessageDialog(this, "Suscefully writing of file!", "Saved file", JOptionPane.INFORMATION_MESSAGE);
        }catch (Exception erro) {
            JOptionPane.showMessageDialog(this, "ERROR!" + erro.toString());
        }
    }
}


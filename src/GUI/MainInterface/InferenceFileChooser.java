package GUI.MainInterface;

import AutomaticRules.WekaParser;
import Documents.Document;
import Documents.Documents;
import GUI.FileManager.ReportUtil;
import GUI.Layout.LayoutConstraints;
import GUI.Util.MainInterfaceHandler;
import Manager.Manager;
import Manager.Results;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * Classe que trata a lista de documentos selecionados dos quais deseja-se obter o resultado da inferência
 * @author Marcio Tadeu de Oliveira Júnior
 */
public class InferenceFileChooser extends JPanel implements ActionListener{
    public JButton saveResultsBtn, showResultsBtn,selectionBtn;//Botões de mostrar resultados e (de)selecionar tudo
    private CheckBoxList cbList;//Lista de Chaeckboxes para seleção de quais documentos deseja-se fazer a inferencia
    private Manager manager = null;//manager
    private JTextPane resultsTextPane;//TextPane para mostrar os resultados
    private ArrayList<String> selectedRules = new ArrayList<String>();//Lista de regras selecionadas
    private Documents documents;
    private String result, saveResult;//String com os resultados
    private int qtdCasamentosCorretos;

    /**
     * Costrutor da classe.
     * @param outputTextPane Painel de Texto onde aparecerá o resultado da inferência
     */
    InferenceFileChooser(JTextPane outputTextPane){
        super();

        resultsTextPane=outputTextPane;//aponta para a TextPane de saida

        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridBag);

        //define o painel dos botões
        JPanel btnPane = new JPanel();
        this.add(btnPane);

        GridBagLayout btnGridBag = new GridBagLayout();
        btnPane.setLayout(btnGridBag);
        
        //Cria o botão de mostrar os resultados e associa evento
        saveResultsBtn = new JButton("Save Results");
        saveResultsBtn.addActionListener(this);
        btnPane.add(saveResultsBtn);

        //Cria o botão de mostrar os resultados e associa evento
        showResultsBtn = new JButton("Show Results");
        showResultsBtn.setMnemonic('s');
        showResultsBtn.addActionListener(this);
        btnPane.add(showResultsBtn);

        //Cria o botão de (de)selecionar os documentos e associa evento que é tratado pela classe CheckBoxList
        selectionBtn = new JButton("Select All");
        selectionBtn.addActionListener(cbList);
        btnPane.add(selectionBtn);

        //Cria a lista de comboboxes
        cbList = new CheckBoxList(selectionBtn);

        //Cria uma scrollpane para os comboboxes
        JScrollPane cbPane = new JScrollPane(cbList);
        this.add(cbPane);

        //Define o Layout dos botões
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 100, 100);
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor=GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(cbPane, constraints);

        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 1, 1);
        constraints.fill=GridBagConstraints.NONE;
        constraints.anchor=GridBagConstraints.SOUTH;
        gridBag.setConstraints(btnPane, constraints);

        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(2,2,2,2);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.SOUTH;
        btnGridBag.setConstraints(selectionBtn, constraints);

        LayoutConstraints.setConstraints(constraints, 0, 1, 1, 1, 1, 1);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.SOUTH;
        btnGridBag.setConstraints(showResultsBtn, constraints);
        
        LayoutConstraints.setConstraints(constraints, 1, 1, 1, 1, 1, 1);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.SOUTH;
        btnGridBag.setConstraints(saveResultsBtn, constraints);
    }

    /**
     * Atualiza os componentes gráficos de acordo com a lista de documentos passada como parâmetro
     * @param documents 
     */
    public void refresh(Documents documents,Manager manager){//atualiza a interface grafica
        cbList.refresh(documents);
        this.manager=manager;
        this.documents=documents;
        this.selectionBtn.setText("Select All");
        cbList.unSelectAll();
        showResults();
    }

    /**
     * Trata os eventos da classe
     * @param e (evento)
     */
    public void actionPerformed(ActionEvent e) {
        //Evento para mostrar o resultado da inferência
        if (e.getSource().equals(showResultsBtn)) {
            showResults();
        } else if (e.getSource().equals(saveResultsBtn)) {
            if(saveResult == null || saveResult.length() < 1){
                JOptionPane.showMessageDialog(this, "É preciso ter o resultado primeiro", "Salvo", JOptionPane.ERROR_MESSAGE);
            }else{
                saveIntervalResults();
            }
        }
    }
    
    /**
     * Salva o Resultado em Exibição (sem o cabeçalho e nome da regra)
     */
    public void saveIntervalResults(){
        File saveFile;
        File saveFileGraoFino;
        String separator = System.getProperty("file.separator");
        String workingPath = System.getProperty("user.dir");
        File saveLocation = new File(workingPath + separator + "results" + separator);
        
        List<String> tags = new WekaParser().getTags(this.documents.getDocuments().get(0).getPathWay());
        
        if(!saveLocation.isDirectory())
            saveLocation.mkdir();
        if(MainInterfaceHandler.getMainInterface().getSimilarity()){
            saveFile = new File(saveLocation.getAbsolutePath()+separator+String.format("%.3f", manager.getSimilarityRate()).replaceAll(",", ".")+".txt");
            saveFileGraoFino = new File(saveLocation.getAbsolutePath()+separator+String.format("%.3f", manager.getSimilarityRate()).replaceAll(",", ".")+"_GraoFino.txt");
        }else{
            saveFile = new File(saveLocation.getAbsolutePath()+separator+"Gabarito.txt");
            saveFileGraoFino = new File(saveLocation.getAbsolutePath()+separator+"Gabarito_GraoFino.txt");
        }
        
        try {
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(saveFile));
            BufferedWriter bwGraoFino = new BufferedWriter(new FileWriter(saveFileGraoFino));
            if(qtdCasamentosCorretos==0){
                bwGraoFino.write(qtdCasamentosCorretos+" Casamentos");
            }else{
                bwGraoFino.write(((tags.size()-1)*qtdCasamentosCorretos)+" Casamentos");
            }
            
            if(saveResult.length() > 2)
                bw2.write(saveResult.substring(0, saveResult.length()-2));
            else
                bw2.write("");
            bw2.close();
            bwGraoFino.close();
        } catch (IOException ex) {
            Logger.getLogger(InferenceFileChooser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    /**
     * Atualiza o ArrayList com as regras selecionadas
     * @param selected 
     */
    public void setSelectedRules(ArrayList<String> selected){//aponta para as regras selecionadas
        if(selectedRules.size() > 0) {
            selectedRules.clear();
        }
        selectedRules=selected;
    }
    
    /**
     * Exibe os resultados da inferência entre os documentos selecionadas no JTextPane passado como parâmetro no cosntrutor da classe
     */
    public void showResults(){
        result="";
        saveResult="";
        qtdCasamentosCorretos=-1;
        boolean isSimilarity = MainInterfaceHandler.getMainInterface().getSimilarity();
        
        boolean marked[] = new boolean[cbList.getCheckBoxes().size()];
        for (int i = 0; i < cbList.getCheckBoxes().size(); i++)
            marked[i]=cbList.getCheckBoxes().get(i).isSelected();
        
        if(!isSimilarity){ //Se o metodo utilizado for o "Context Key"
                manager.startResultsInferenciaContextKey(marked);
        } else{ //Se o metodo utilizado for o "Similarity"
                manager.startResultsInferenciaSimilarity(documents,marked);
        }
        
        if (manager != null) {//se gerenciador for diferente de nulo
            int k, l;//variaves de iteração da lista de checkboxes
                        
            //combinação dos checkboxes dos documentos selecionados 2 a 2
            for (k = 0; k < cbList.getCheckBoxes().size(); k++) {//primeiro nivel de iteração dos checkboxes
                if (cbList.getCheckBoxes().get(k).isSelected()) {//se o checkbox do primeiro nivel estiver selecionado
                    for (l = k + 1; l < cbList.getCheckBoxes().size(); l++) {//segundo nivel de iteração dos checkboxes
                        if (cbList.getCheckBoxes().get(l).isSelected()) {//se o checkbox do segundo nivel estiver selecionado

                            //faz a combinação 2 a 2 dos documentos em si
                            int i, j;
                            i = -1;
                            j = -1;
                            for (Document document : documents.getDocuments()) {//percorre a lista de documentos
                                if (document.getCode().equals(cbList.getCheckBoxes().get(k).getText())) {
                                    i = document.getId();//carrega para i a id do primeiro documento selecionado nos checkboxes
                                } else if (document.getCode().equals(cbList.getCheckBoxes().get(l).getText())) {
                                    j = document.getId();//carrega para j a id do segundo documento selecionado nos checkboxes
                                }
                            } 
                            if (i != j) {//garantia de que não estamos comparando os mesmos documentos, pois não há resultados do diff de um documento consigo mesmo
                                for (Results results : manager.getResultsInference()) {//pega resultado por resultado de cada documento
                                    if (results.getDocument1() == i && results.getDocument2() == j) {//se o resultado for de documentos desejados pela combinação de documentos
                                        result += "diff(" + documents.getDocument(i).getCode() + ", " + documents.getDocument(j).getCode() + ")\n\n";//adciona cabeçalho do resultado de uma combinação de documentos
                                        for (String ruleResult : results.getResultInference()) {//pega um resultado especifico de cada documentos para ver se ele faz parte das regras selecionadas
                                            for (String selectedRule : selectedRules) {//peg uma regra selecionada
                                                if (ruleResult.toLowerCase().substring(0, ruleResult.indexOf(":")).equals(selectedRule.toLowerCase().substring(0, selectedRule.indexOf("(")))) {//se o resultado vier de uma regra selecionada
                                                    saveResult += ruleResult.substring(ruleResult.indexOf("\n")+1);
                                                    if(qtdCasamentosCorretos==-1)
                                                        qtdCasamentosCorretos = saveResult.split("\n").length;
                                                    result += ruleResult;//adciona o resultado à string de resultados
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                result += "\n";//fim de inferencia entre uma das combinações de documentos
                                break;
                            }
                        }
                    }
                }
            }
        }
        resultsTextPane.setText(result);//mostra os resultados no textpane de saida
        resultsTextPane.setCaretPosition(0);//rola a barra de rolagem para o topo
    }
    
    /*
     * Responsável por exportar o relatorio
     */
    public void exportReport(){
        //Chama o metodo responsável por gerar o relatório.
        ReportUtil.exportReport(result);
    }
    
    /*
     * Retorna o checkboxlist com os documentos xml
     */
    public CheckBoxList getCheckBoxList(){
        return cbList;
    }
    
}
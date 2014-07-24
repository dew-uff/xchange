/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SemanticMerge;

import GUI.Layout.LayoutConstraints;
import GUI.MainInterface.DocumentsTab;
import GUI.MainInterface.InferenceFileChooser;
import Manager.Manager;
import Rules.RulesModule;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.image.ImageObserver.WIDTH;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import weka.gui.CheckBoxList;

/**
 *
 * @author Fernando Marques
 */
public class SemanticRules extends JDialog implements ActionListener {


    private RulesModule inferenceModule;
    private JButton btnDone, btnSelectAll;
    private JPanel pnlBotton, pnlCenter;
    public boolean correctClose = false, similarityActive;
    private ArrayList<JCheckBox> rulesSelect;
    private GridBagConstraints constraints;
    private GridBagLayout generalGridBag, centerGridBag;
    private List<String> selectedRules;

    public SemanticRules(RulesModule rules) {
//        this.setPreferredSize(new Dimension(1100, 500));
//        this.setMinimumSize(new Dimension(1100, 400));
//        JList checkBoxes = new CheckBoxList();
//        for (int i = 0; i < rules.size(); i++) {
//            JCheckBox jb = new JCheckBox(rules.get(i).getName());
//            checkBoxes.add(jb);
//        }
//        sc = new JScrollPane();
//
//        sc.add(checkBoxes);
//
//        //modifica características da janela
//        GridBagConstraints constraints = new GridBagConstraints();
//        this.setModal(true);//não permite que a tela do XChange seja acessada antes desta ser fechada
//        this.setTitle("Popapizao");//modifica o titulo
//        this.setResizable(true);//permite redimensionar a janela'
//        GridBagLayout gb = new GridBagLayout();
//        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 100, 100);
//        constraints.insets = new Insets(70, 15, 5, 10);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        constraints.anchor = GridBagConstraints.NORTHWEST;
//        gb.addLayoutComponent(sc, constraints);
//        this.setLayout(gb);
//        this.setAlwaysOnTop(false);//esconde a janela se o XChange perder o foco 
//        this.pack();//desenha a janela com o melhor tamanho para seus componentes
//
//        Toolkit tk = Toolkit.getDefaultToolkit();//objeto para pegar as dimensoes da tela
//        Dimension dim = tk.getScreenSize();//funçao para pegar as dimensoes da tela
//        this.setLocation(((int) dim.getWidth() - this.getWidth()) / 2, ((int) dim.getHeight() - this.getHeight()) / 2);//posiciona a janela no meio da tela 
//
//        this.setVisible(true);

        /**
         * painel central *
         */
        //cria definições da tela
        Dimension btnSize = new Dimension(110, 25);//dimensão padrão dos botões
        this.setPreferredSize(new Dimension(367, 450));
        this.setMinimumSize(new Dimension(367, 200));

        //cria os gerenciadores de layout
        constraints = new GridBagConstraints();
        generalGridBag = new GridBagLayout();
        this.setLayout(generalGridBag);

        //layout do painel central
        centerGridBag = new GridBagLayout();

        //cria o painel central
        pnlCenter = new JPanel();
        pnlCenter.setLayout(centerGridBag);
        pnlCenter.setBackground(Color.red);

        //gerencia olayout do painel central
        constraints.fill = GridBagConstraints.BOTH;

        inferenceModule = new RulesModule();
        String[] partRules = inferenceModule.partRules(rules.getRulesString()); //Pega o cabeçalho das regras (ex: salary(NAME))
        setIdentifyRules(inferenceModule.getNameAndArgumentsRules(partRules));

        //adiciona o painel central à janela
        this.add(pnlCenter);
        constraints.insets = new Insets(5, 5, 5, 5);
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1000, 1000);
        constraints.anchor = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTH;
        generalGridBag.setConstraints(pnlCenter, constraints);
        constraints.insets = new Insets(0, 0, 0, 0);

        /**
         * painel inferior *
         */
        pnlBotton = new JPanel();

        //cria o botão done
        btnDone = new JButton("Done");
        btnDone.addActionListener(this);
        btnDone.setVisible(true);
        btnDone.setPreferredSize(btnSize);
        pnlBotton.add(btnDone);

        //cria o botão de selecionar as regras
        btnSelectAll = new JButton("Select all");
        btnSelectAll.addActionListener(this);
        btnSelectAll.setVisible(true);
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
        setTitle("Semantic Rules");//modifica o titulo
        setResizable(true);//permite redimensionar a janela
        setAlwaysOnTop(false);//esconde a janela se o XChange perder o foco 
        pack();//desenha a janela com o melhor tamanho para seus componentes

        Toolkit tk = Toolkit.getDefaultToolkit();//objeto para pegar as dimensoes da tela
        Dimension dim = tk.getScreenSize();//funçao para pegar as dimensoes da tela
        this.setLocation(((int) dim.getWidth() - this.getWidth()) / 2, ((int) dim.getHeight() - this.getHeight()) / 2);//posiciona a janela no meio da tela 

        setVisible(true);//torna a janela visivel
    }

    /**
     * Formata as regras que serão exibidas no textPane (tela) para que fiquem
     * em um formato de fácil compreenssão para o usuário.
     *
     * @param paneRules String com as regras que serão exibidas no textPane
     * (tela).
     * @return textFormated String com as regras formatadas.
     */
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
     * Cria as caixas para a exibição das regras (nomes + argumentos).
     *
     * @param rules Recebe um vetor de String onde cada índice contém o nome da
     * regra e os argumentos dela. Ex: same_salario(Nome).
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
        constraints.fill = GridBagConstraints.BOTH;
        centerGridBag.addLayoutComponent(p, constraints);;

        p.setVisible(true);
        for (String rule : rules) { //Cria os campos do CheckBox de acordo com as regras inseridas pelo usuário
            JCheckBox chkItem = new JCheckBox(rule);
            chkItem.setName(rule);
            rulesSelect.add(chkItem);
            p.add(chkItem);
        }

    }

    /**
     * Trata as ações dos botões e das seleções realizadas pelo usuário.
     *
     * @param ae Recebe um evento gerado.
     */
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnDone) { //Ação do botão "Done"
            selectedRules = new ArrayList<String>();
            int cont = 0;
            for (JCheckBox item : rulesSelect) { //Verifica quais regras foram selecionadas pelo usuário
                if (item.isSelected()) {
                    cont++;
                    selectedRules.add(item.getName());
                }
            }
            dispose();
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
    
    public List<String> getSelectedRules(){
        return this.selectedRules;
    }
}

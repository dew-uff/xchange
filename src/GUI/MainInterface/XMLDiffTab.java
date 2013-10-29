package GUI.MainInterface;

import Documents.Documents;
import Documents.XMLFormatter;
import GUI.FileManager.NoSelectedFileException;
import GUI.FileManager.SingleDiffSaver;
import GUI.Layout.LayoutConstraints;
import gems.ic.uff.br.modelo.LcsXML;
import gems.ic.uff.br.modelo.XML;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Carlos Roberto Carvalho Oliveira
 */
public class XMLDiffTab extends JPanel implements ActionListener{
    
    private JTextArea textarea;
    private JComboBox leftCB, rightCB;
    private Documents documents;
    private JButton saveDiffBtn;
    
    /**
     * Construtor da classe.
     */
    public XMLDiffTab(){
        super();
        this.documents = null;
        
        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridBag);

        //cria os Comboxes de seleção do documento XML que será mostrado
        leftCB = new JComboBox();
        rightCB = new JComboBox();

        //paineis para posicionamento dos comboboxes
        JPanel leftPane = new JPanel();
        JPanel rightPane = new JPanel();

        //Adiciona os ComboBoxes a seus paineis
        leftPane.add(leftCB);
        rightPane.add(rightCB);

        //cria paineis que permitem aos comboboxes alongarem-se na horizontal
        GridBagLayout leftGBL = new GridBagLayout();
        leftPane.setLayout(leftGBL);
        GridBagLayout rightGBL = new GridBagLayout();
        rightPane.setLayout(rightGBL);

        //layout dos paineis que contem os comboxes
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        //aplica o layout que alonga os comboboxes
        leftGBL.setConstraints(leftCB, constraints);
        rightGBL.setConstraints(rightCB, constraints);

        //cria o painel superior com os paineis com comboboxes
        JPanel cbPanel = new JPanel();
        cbPanel.setLayout(new GridLayout(1, 2));

        //Adiciona os paineis dos ComboBoxes à interface gráfica
        cbPanel.add(leftPane);
        cbPanel.add(rightPane);

        /*Define o layout e a posição dos ComboBoxes*/
        LayoutConstraints.setConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.insets = new Insets(2, 2, 0, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        gridBag.setConstraints(cbPanel, constraints);


        //Adiciona os ComboBoxes à interface gráfica
        this.add(cbPanel);

        /*cria um DocumentsPane, que contem a TextAreas onde estará o
         * conteúdo dos documentos XML
         */
        
        this.textarea = new JTextArea();
        
        JScrollPane areaSP = new JScrollPane(textarea);
        areaSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        areaSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        LayoutConstraints.setConstraints(constraints, 0, 1, 2, 1, 1, 100);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(areaSP, constraints);
        textarea.setEditable(true);
        textarea.setVisible(true);
        
        //adiciona o botão para salvar um diff especifico
        saveDiffBtn = new JButton("Save Diff");
        saveDiffBtn.setMnemonic('d');
        this.add(saveDiffBtn);
        saveDiffBtn.addActionListener(this);
        
        LayoutConstraints.setConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(saveDiffBtn, constraints);

        this.add(areaSP);
    }
    
    /**
     * Atualiza os componentes graficos de acordo com a lista de documentos passada como parametros
     * @param documents 
     */
    public void refresh(Documents documents) {

        //Variaveis que indicam o indice do XML aberto em cada area de texto
        int leftCBIndex = leftCB.getSelectedIndex();
        int rigthCBIndex = rightCB.getSelectedIndex();

        //remove os eventos relacionados aos comboboxes
        leftCB.removeActionListener(this);
        rightCB.removeActionListener(this);

        //limpa o conteudo dos comboboxes
        leftCB.removeAllItems();
        rightCB.removeAllItems();

        //reescreve o conteudo nos comboboxes
        for (String s : documents.docsIds()) {
            leftCB.addItem(s);
            rightCB.addItem(s);
        }
        
        
        if(documents.getSize()==2){
            leftCB.setSelectedIndex(0);
            rightCB.setSelectedIndex(1);
        }else if(documents.getSize()>=2){
            leftCB.setSelectedIndex(leftCBIndex);
            rightCB.setSelectedIndex(rigthCBIndex);
        }else if(documents.getSize()==1){
            leftCB.setSelectedIndex(0);
            rightCB.setSelectedIndex(0);
        }
        
        //adiciona novamente os eventos
        leftCB.addActionListener(this);
        rightCB.addActionListener(this);
       
        //define a nova lista de documentos da classe
        this.documents = documents;
        this.revalidate();
    }
    
    /**
     * modifica o texto da TextArea para o texto no parametro s
     * @param s 
     */
    void setText(String s) {
        this.textarea.setText(s);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==saveDiffBtn){
            try {
                SingleDiffSaver.save(textarea.getText());
            } catch (NoSelectedFileException ex) {
                 JOptionPane.showMessageDialog(null, "It was not possible save the file.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }else
        this.showXMLDiff();
    }
    
    public void showXMLDiff(){
        XML xml1 = new XML(documents.getContent(leftCB.getSelectedIndex()));
        XML xml2 = new XML(documents.getContent(rightCB.getSelectedIndex()));
        LcsXML lcsxml = new LcsXML(xml1, xml2);
        this.textarea.setText(XMLFormatter.format(lcsxml.getDiffXML().toString()));
        this.textarea.setCaretPosition(0);
    }
}

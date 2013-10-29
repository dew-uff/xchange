package GUI.MainInterface;


import Documents.Documents;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Marcio Tadeu de Oliveira Júnior
 */

 /*A função desta classe é criar a lista de checkbox que se referem aos documentos XML abertos no
 * XChange, ao se marcar um desses checkboxes o documento que ele se refere entra na lista
 * de documentos que passarão para a inferencia
 */
public class CheckBoxList extends JPanel implements ActionListener{
    ArrayList<JCheckBox> cbList;//lista com checkbox
    JButton selectionBtn;
    
    /**
     * Construtor da classe.
     * @param selection Botão que confirma a seleção dos documentos que se deseja diferenciar para a inferencia
     */
    public CheckBoxList(JButton selection){
        
        super();    
        
        selectionBtn=selection;//recebe o botão de (de)seleção de documentos para ter seus ventos tratados dentro desta classe
        
        selectionBtn.addActionListener(this);
        
        cbList = new ArrayList<JCheckBox>();//cria a lista de CheckBoxes
        
        this.setVisible(true);
    }
    
    /**
     * 
     * @return Lista de Checkboxes
     */
    public ArrayList<JCheckBox> getCheckBoxes(){
        return cbList;
    }
    
    /**
     * Atualiza a lista de checkboxes de acordo com a lista de documentos passada como parametro
     * @param documents 
     */
    public void refresh(Documents documents){
        int i=0;
        
        //some com os CheckBoxes presentes na tela tornando-os invisiveis
        for(JCheckBox cb:cbList) {
            cb.setVisible(false);
        }
        
        //apaga os CheckBoxes presentes no painel da memoria
        this.removeAll();
        
        //declara objetos de controle do layout
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        
        cbList = new ArrayList<JCheckBox>();
        
        //cria uma lista de checkbox com os nomes que os arquivos .XML levam no projeto
        for(String s : documents.docsIds()){
            JCheckBox checkBox = new JCheckBox(s);
            cbList.add(checkBox);
            checkBox.setSelected(false);
            this.add(checkBox);
            checkBox.setVisible(true);
            i++;
        }   
        
        //redesenha o componente na tela
        this.revalidate();
        
    }   
    
    /**
     * Trata dos eventos da classe
     * @param e 
     */
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource().equals(selectionBtn)){//evento para clique no botão de (de)seleção de regras
            if(selectionBtn.getText().equals("Select All")){//se estiver como select all
                selectAll();
            }else{
                unSelectAll();
            }
        }else{//evento de clique em combobox
            selectionBtn.removeActionListener(this);
            selectionBtn.setText("Unselect All");
            selectionBtn.addActionListener(this);
            for(JCheckBox cb:cbList){//percorre a lista de checkboxes, se um estiver deselecionado, troca o botão para selecionar todos
                if(!cb.isSelected()){
                    selectionBtn.removeActionListener(this);
                    selectionBtn.setText("Select All");
                    selectionBtn.addActionListener(this);
                    break;
                }
            }
        }
    }
    
    /**
     * Seleciona todos os checboxes
     */
    public void selectAll(){
        for(JCheckBox cb:cbList){//percorre a lista de checkboxes para selecionar todos
            cb.removeActionListener(this);
            cb.setSelected(true);//seleciona checkbox
            cb.addActionListener(this);
        }
        selectionBtn.removeActionListener(this);
        selectionBtn.setText("Unselect All");
        selectionBtn.addActionListener(this);
    }
    
    /**
     * Deseleciona todos os checkboxes
     */
    public void unSelectAll(){
        for(JCheckBox cb:cbList){//percorre a lista de checkboxes para selecionar todos
            cb.removeActionListener(this);
            cb.setSelected(false);//deseleciona checkbox
            cb.addActionListener(this);
        }
        selectionBtn.removeActionListener(this);
        selectionBtn.setText("Select All");
        selectionBtn.addActionListener(this);
    }
}
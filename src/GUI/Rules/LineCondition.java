package GUI.Rules;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Classe contém cada linha em que a regra é montada, inicializando seus
 * combobox e seus eventos.
 *
 * @author Celio Henrique Nogueira Larcher Júnior, Jorge Moreira
 *
 */
public class LineCondition extends JPanel {

    private JComboBox comboTerm1, comboTerm2, comboOperator;
    private JButton btnAddCondition;
    private JButton btnRemoveCondition;
    private static JPanel pnlConditions;
    private static String[] namesFacts;

    LineCondition() {
        super(new FlowLayout());
        this.setMaximumSize(new Dimension(770, 32));
        this.comboTerm1 = new JComboBox();
        this.comboTerm2 = new JComboBox();
        this.comboOperator = new JComboBox();
        this.btnAddCondition = new JButton("+");
        this.btnRemoveCondition = new JButton("-");
        this.add(comboTerm1);
        this.add(new JSeparator());
        this.add(comboOperator);
        this.add(new JSeparator());
        this.add(comboTerm2);
        this.add(new JSeparator());
        this.add(btnAddCondition);
        this.add(btnRemoveCondition);
        this.btnAddCondition.addActionListener(new ActionListener() {
            /**
             * evento do botão de adicionar condição
             */
            public void actionPerformed(ActionEvent e) {
                LineCondition aux = new LineCondition();
                pnlConditions.add(aux);
                btnAddCondition.setEnabled(false);
                ((LineCondition) pnlConditions.getComponent(0)).btnRemoveCondition.setEnabled(true);
                aux.comboTerm1.requestFocus();
                pnlConditions.revalidate();
            }
        });
        final LineCondition aux = this; //utilizada pra os Listeners
        this.btnRemoveCondition.addActionListener(new ActionListener() {
            /**
             * evento do botão de remover condição
             */
            public void actionPerformed(ActionEvent e) {
                int size = pnlConditions.getComponentCount();
                if (size > 1) {
                    if (pnlConditions.getComponent(size - 1) == aux) {
                        ((LineCondition) pnlConditions.getComponent(size - 2)).btnAddCondition.setEnabled(true);
                    }
                    pnlConditions.remove(aux);
                    if (size == 2) {
                        ((LineCondition) pnlConditions.getComponent(0)).btnRemoveCondition.setEnabled(false);
                    }
                    pnlConditions.updateUI();
                } else {
                    JOptionPane.showMessageDialog(pnlConditions, "It's necessary one or more conditions to create a rule.");
                }
                ((LineCondition) pnlConditions.getComponent(size - 1)).btnAddCondition.requestFocus();
                pnlConditions.revalidate();
            }
        });
        this.comboOperator.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if ((comboOperator.getSelectedItem().toString().indexOf("_")) >= 0) { //somente os operadores de diferença tem esse simbolo ex.: element_deleted
                        if (pnlConditions.getComponentCount() == 1) { //só permite selecionar operadores de diferença se a regra tiver somente 1 condição
                            comboTerm1.setEnabled(false);
                            comboTerm2.setEnabled(false);
                            comboTerm1.setSelectedItem("");
                            comboTerm2.setSelectedItem("");
                            btnAddCondition.setEnabled(false);
                        } else {
                            JOptionPane.showMessageDialog(pnlConditions, "The new_element operator and the element_deleted operator do not support more than 1 condition.");
                            comboOperator.setSelectedItem("");
                        }
                    } else {
                        comboTerm1.setEnabled(true);
                        comboTerm2.setEnabled(true);
                        if (pnlConditions.getComponent(pnlConditions.getComponentCount() - 1) == aux) { //só ativa o botão "+" na última condição
                            btnAddCondition.setEnabled(true);
                        }
                    }
                }
            }
        });
        this.comboTerm1.setModel(new DefaultComboBoxModel(getDoubleNames(namesFacts)));
        this.comboTerm2.setModel(new DefaultComboBoxModel(getDoubleNames(namesFacts)));
        this.comboTerm1.insertItemAt("", 0);
        this.comboTerm2.insertItemAt("", 0);
        this.comboTerm2.setSelectedItem("");
        this.comboTerm1.setSelectedItem("");
        this.comboOperator.setModel(new DefaultComboBoxModel(new String[]{"", ">", "<", "=", "!=", "new_element", "deleted_element"}));
        this.comboOperator.setSelectedItem("");
    }

    public JComboBox getComboTerm1() {
        return comboTerm1;
    }

    public JComboBox getComboTerm2() {
        return comboTerm2;
    }

    public JComboBox getComboOperator() {
        return comboOperator;
    }

    public JButton getBtnAddCondition() {
        return btnAddCondition;
    }

    /**
     * - * Monta as opções dos comboBox de termos para a construção das regras.
     * - * Exemplo: (fato - v. Before / fato - v. After). - * @param namesFacts
     * - * Vetors de Strings contendo o nome de todos os fatos. - * @return
     * doubleNames - * Nomes duplificados e normalizados. -
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

    public static JPanel getPnlConditions() {
        return pnlConditions;
    }

    public static void setPnlConditions(JPanel pnlConditions) {
        LineCondition.pnlConditions = pnlConditions;
    }

    public static ArrayList<LineCondition> getLineConditions() {
        ArrayList<LineCondition> lineConditions = new ArrayList<LineCondition>();
        for (Component c : pnlConditions.getComponents()) {
            lineConditions.add((LineCondition) c);
        }
        return lineConditions;
    }

    public static void setLineConditions(ArrayList<LineCondition> lineConditions) {
        if (pnlConditions != null) {
            pnlConditions.removeAll();
            for (LineCondition lineCondition : lineConditions) {
                pnlConditions.add(lineCondition);
            }
            pnlConditions.updateUI();
            pnlConditions.revalidate();
        }
    }

    public static String[] getNamesFacts() {
        return namesFacts;
    }

    public static void setNamesFacts(String[] namesFacts) {
        LineCondition.namesFacts = namesFacts;
    }
}

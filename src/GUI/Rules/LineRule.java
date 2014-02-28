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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * @author Celio Henrique Nogueira Larcher Júnior Classe contém cada linha em
 * que a regra é montada, inicializando seus combobox e seus eventos.
 */
public class LineRule extends JPanel {

    private JComboBox comboTerm1, comboTerm2, comboOperator;
    private JButton btnAddCondition;
    private JButton btnRemoveCondition;
    private static JPanel pnlRules;
    private static String[] namesFacts;

    LineRule() {
        super(new FlowLayout());
        this.setMaximumSize(new Dimension(730, 32));
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
                LineRule aux = new LineRule();
                pnlRules.add(aux);
                btnAddCondition.setEnabled(false);
                ((LineRule) pnlRules.getComponent(0)).btnRemoveCondition.setEnabled(true);
                aux.comboTerm1.requestFocus();
                pnlRules.revalidate();
            }
        });
        final LineRule aux = this; //utilizada pra os Listeners
        this.btnRemoveCondition.addActionListener(new ActionListener() {
            /**
             * evento do botão de remover condição
             */
            public void actionPerformed(ActionEvent e) {
                int size = pnlRules.getComponentCount();
                if (size > 1) {
                    if (pnlRules.getComponent(size - 1) == aux) {
                        ((LineRule) pnlRules.getComponent(size - 2)).btnAddCondition.setEnabled(true);
                    }
                    pnlRules.remove(aux);
                    if (size == 2) {
                        ((LineRule) pnlRules.getComponent(0)).btnRemoveCondition.setEnabled(false);
                    }
                    pnlRules.updateUI();
                } else {
                    JOptionPane.showMessageDialog(pnlRules, "It's necessary one or more conditions to create a rule.");
                }
                ((LineRule) pnlRules.getComponent(size - 1)).btnAddCondition.requestFocus();
                pnlRules.revalidate();
            }
        });
        this.comboOperator.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if ((comboOperator.getSelectedItem().toString().indexOf("_")) >= 0) { //somente os operadores de diferença tem esse simbolo ex.: element_deleted
                        if (pnlRules.getComponentCount() == 1) { //só permite selecionar operadores de diferença se a regra tiver somente 1 condição
                            comboTerm1.setEnabled(false);
                            comboTerm2.setEnabled(false);
                            comboTerm1.setSelectedItem("");
                            comboTerm2.setSelectedItem("");
                            btnAddCondition.setEnabled(false);
                        } else {
                            JOptionPane.showMessageDialog(pnlRules, "The new_element operator and the element_deleted operator do not support more than 1 condition.");
                            comboOperator.setSelectedItem("");
                        }
                    } else {
                        comboTerm1.setEnabled(true);
                        comboTerm2.setEnabled(true);
                        if (pnlRules.getComponent(pnlRules.getComponentCount() - 1) == aux) { //só ativa o botão "+" na última condição
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

    public static JPanel getPnlRules() {
        return pnlRules;
    }

    public static void setPnlRules(JPanel pnlRules) {
        LineRule.pnlRules = pnlRules;
    }

    public static ArrayList<LineRule> getLinerules() {
        ArrayList<LineRule> lineRules = new ArrayList<LineRule>();
        for (Component c : pnlRules.getComponents()) {
            lineRules.add((LineRule) c);
        }
        return lineRules;
    }

    public static void setLinerules(ArrayList<LineRule> linerules) {
        if (pnlRules != null) {
            pnlRules.removeAll();
            for (LineRule lineRule : linerules) {
                pnlRules.add(lineRule);
            }
            pnlRules.updateUI();
            pnlRules.revalidate();
        }
    }

    public static String[] getNamesFacts() {
        return namesFacts;
    }

    public static void setNamesFacts(String[] namesFacts) {
        LineRule.namesFacts = namesFacts;
    }
}

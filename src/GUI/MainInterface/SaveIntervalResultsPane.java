/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI.MainInterface;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Carlos
 */
public class SaveIntervalResultsPane extends JDialog implements ActionListener{
    
    private JTextField tfMaximumInterval, tfMinimumInterval, tfStepInterval;
    private JButton btnOk, btnCancel;
    private ArrayList<BigDecimal> intervalArray;
    
    public SaveIntervalResultsPane(){
        
        //Inicia os componentes
        tfMaximumInterval = new JTextField("1.0", 5);
        tfMinimumInterval = new JTextField("0.0", 5);
        tfStepInterval = new JTextField("0.01", 5);
        btnOk = new JButton("Ok");
        btnCancel = new JButton("Cancel");
        
        //Painel, Label, TextField para o valor Maximo.
        JPanel jpMaximumInterval = new JPanel();
        jpMaximumInterval.add(new JLabel("Max:", JLabel.TRAILING));
        jpMaximumInterval.add(tfMaximumInterval);
        this.getContentPane().add(jpMaximumInterval);
        
        //Painel, Label, TextField para o valor Minimo.
        JPanel jpMinimumInterval = new JPanel();
        jpMinimumInterval.add(new JLabel("Min:", JLabel.TRAILING));
        jpMinimumInterval.add(tfMinimumInterval);
        this.getContentPane().add(jpMinimumInterval);
        
        //Painel, Label, TextField para o valor de passo.
        JPanel jpStepInterval = new JPanel();
        jpStepInterval.add(new JLabel("Interval Step:", JLabel.TRAILING));
        jpStepInterval.add(tfStepInterval);
        this.getContentPane().add(jpStepInterval);
        
        //Painel, botoes
        JPanel jpButton = new JPanel();
        btnOk.setPreferredSize(new Dimension(80, 27));
        btnOk.addActionListener(this);
        btnCancel.setPreferredSize(new Dimension(80, 27));
        btnCancel.addActionListener(this);
        jpButton.add(btnOk);
        jpButton.add(btnCancel);
        this.getContentPane().add(jpButton);

        //propriedades do JDialog
        this.getRootPane().setDefaultButton(btnOk);
        this.setTitle("Save Interval");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setModal(true);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(btnOk)){
            if(this.tfMaximumInterval.getText().equals("") || this.tfMinimumInterval.getText().equals("") || this.tfStepInterval.getText().equals(""))
                JOptionPane.showMessageDialog(null, "Empty Field", "Error", JOptionPane.ERROR_MESSAGE);
            else if(this.getTfMaximumInterval().doubleValue() >= this.getTfMinimumInterval().doubleValue() && 
                    this.getTfMaximumInterval().doubleValue() <= 1.0 && this.getTfMinimumInterval().doubleValue() >= 0.0){
                generateIntervalArray();
                SaveIntervalResultsPane.this.dispose();
            }
            else if(this.getTfMaximumInterval().doubleValue() < this.getTfMinimumInterval().doubleValue())
                JOptionPane.showMessageDialog(null, "Min Value higher then Max Value", "Error", JOptionPane.ERROR_MESSAGE);
            else if(this.getTfMaximumInterval().doubleValue() > 1.0)
                JOptionPane.showMessageDialog(null, "Max Value higher then 1.0", "Error", JOptionPane.ERROR_MESSAGE);
            else if(this.getTfMinimumInterval().doubleValue() < 0.0)
                JOptionPane.showMessageDialog(null, "Min Value less 0.0", "Error", JOptionPane.ERROR_MESSAGE);
            else if(this.tfMaximumInterval.getText().equals(""))
                JOptionPane.showMessageDialog(null, "Campo vazio", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else if(e.getSource().equals(btnCancel)){
            SaveIntervalResultsPane.this.dispose();
        }
    }
    
    private void generateIntervalArray(){
        //Obtenho os valores inseridos pelo Usuario
        BigDecimal maximumValue = this.getTfMaximumInterval();
        BigDecimal minimumValue = this.getTfMinimumInterval();
        BigDecimal stepValue = this.getTfStepInterval();
        
        intervalArray = new ArrayList<BigDecimal>();
        BigDecimal step = maximumValue;
        
        //Adiciona os valores "inteiros"
        while(step.doubleValue() >= minimumValue.doubleValue()){
            intervalArray.add(step);
            step = step.subtract(stepValue);
        }
    }

    public ArrayList<BigDecimal> getIntervalArray() {
        return intervalArray;
    }
    
    public BigDecimal getTfMaximumInterval() {
        return BigDecimal.valueOf(Double.parseDouble(tfMaximumInterval.getText()));
    }
    
    public BigDecimal getTfMinimumInterval() {
        return BigDecimal.valueOf(Double.parseDouble(tfMinimumInterval.getText()));
    }

    public BigDecimal getTfStepInterval() {
        return BigDecimal.valueOf(Double.parseDouble(tfStepInterval.getText()));
    }
}

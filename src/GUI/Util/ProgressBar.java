/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Util;

import GUI.Layout.LayoutConstraints;
import Manager.Manager;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author Marcio Tadeu de Oliveira Jr
 */
public class ProgressBar extends JPanel implements Runnable{

    private int current;
    private JProgressBar pBar;
    private String text;
    private JLabel label;
    private Thread running;
    private boolean threadFlag;
    private String marker="...";

    /**
     * Constrói uma janela com barra de progresso.
     * @param m Número de passos máximo que a barra fará
     * @param legend Título da janela
     */
    public ProgressBar(int m, String legend) {
        super();
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //declara objetos de controle do layout
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();        
        this.setLayout(gridBag);
        
        pBar = new JProgressBar();
        pBar.setStringPainted(true);
        pBar.setVisible(true);
        pBar.setMaximum(m);
        pBar.setValue(0);
        
        JPanel pBarPane = new JPanel();
        
        GridBagLayout pBarGridBag = new GridBagLayout();
        pBarPane.setLayout(pBarGridBag);
        
        LayoutConstraints.setConstraints(constraints,0,0,1,1,1,1);
        constraints.insets=new Insets(5,0,0,9);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.NORTHWEST;
        pBarGridBag.setConstraints(pBar,constraints); 
        
        
        pBarPane.add(pBar);
        
        this.add(pBarPane);
        
        LayoutConstraints.setConstraints(constraints,1,0,1,1,25,1);
        constraints.insets=new Insets(0,0,0,0);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(pBarPane,constraints);   
        
        label = new JLabel(legend);
        setLabel(legend);
        label.setVisible(true);
        
        JPanel labelPane = new JPanel();
        
        GridBagLayout labelGridBag = new GridBagLayout();
        labelPane.setLayout(labelGridBag);
        
        LayoutConstraints.setConstraints(constraints,0,0,1,1,1,1);
        constraints.insets=new Insets(0,9,0,0);
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor=GridBagConstraints.WEST;
        labelGridBag.setConstraints(label,constraints); 
        
        labelPane.add(label);
        
        this.add(labelPane);
        
        LayoutConstraints.setConstraints(constraints,0,0,1,1,100,1);
        constraints.insets=new Insets(0,0,0,0);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.WEST;
        gridBag.setConstraints(labelPane,constraints); 

        current = 50;

        this.setVisible(true);

        pBar.paintImmediately(pBar.getBounds());
        
        this.repaint();
        this.revalidate();
    }

    /**
     * Incrementa o contador da barra de progresso.
     */
    public void increase() {
        current++;
        pBar.setValue(current);
        pBar.paintImmediately(pBar.getBounds());
    }

    /**
     * Muda o valor da legenda.
     * @param legend Novo valor da legenda.
     */
    public void setLabel(String legend) {
        text=legend;
        label.setText(text+marker+" "+getValue());
        label.setVisible(true);
    }
    
    public String getLabel(){
        return text;
    }
    
    public void stop(){
        setLabel("Ready: Waiting for User");
        threadFlag=true;
        pBar.setVisible(true);
        pBar.setValue(0);
        pBar.paintImmediately(pBar.getBounds());
        current=0;
    }
    
    public void restart(int m, String legend){
        pBar.setStringPainted(true);
        pBar.setVisible(true);
        pBar.setMaximum(m);
        pBar.setValue(0);
        pBar.paintImmediately(pBar.getBounds());
        
        threadFlag=false;
        
        setLabel(legend);

        running = new Thread(this); 
        running.start();
    }
    

    /**
     * Executa as ações constantes na barra de progresso.
     */
    public void run() {
        int i=0;
        while(!threadFlag){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            }
            int a = i%4;
            if(a==0){
                marker="...";
            }else if(a==1){
                marker="·..";
            } else if(a==2){
                marker=".·.";
            } else if(a==3){
                marker="..·";
            }
            i++;
            label.setText(text+marker+" "+getValue());
        }
        if(!getLabel().equals("Ready: Waiting for User")) {
            setLabel("Finish");
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        setLabel("Ready: Waiting for User");
    }
    
    public int getValue(){
        return current;
    }
}

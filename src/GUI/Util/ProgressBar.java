/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Util;

import Manager.Manager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 *
 * @author Marcio Tadeu de Oliveira Jr
 */
public class ProgressBar extends JFrame implements Runnable{

    private int current;
    private JProgressBar pBar;
    private int height = 60, width = 300;
    private String label;
    private Thread running;
    private boolean threadFlag=false;

    /**
     * Constrói uma janela com barra de progresso.
     * @param m Número de passos máximo que a barra fará
     * @param title Título da janela
     */
    public ProgressBar(int m, String title) {
        super(title);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//função ao clicar no botão fechar
        this.setLocationRelativeTo(null);

        this.setSize(width, height);

        this.setResizable(false);

        pBar = new JProgressBar();
        pBar.setStringPainted(true);
        pBar.setVisible(true);
        pBar.setMaximum(m);
        pBar.setValue(0);
        
        this.label=title;

        current = 0;

        this.add(pBar);

        this.setVisible(true);

        pBar.paintImmediately(pBar.getBounds());
        
        running = new Thread(this); 
        running.start();
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
     * @param label Novo valor da legenda.
     */
    public void setLabel(String label) {
        this.label = label; 
    }

    @Override
    public void dispose() {
        this.threadFlag=true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.dispose();
    }

    /**
     * Executa as ações cnstantes de mudança na interface de progresso.
     */
    public void run() {
        int i=0;
        while(!threadFlag){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            }
            String marker="";
            int a = i%4;
            if(a==0){
//                marker="|";
                marker="...";
            }else if(a==1){
//                 marker="/";
                marker="·..";
            } else if(a==2){
//                 marker="-";
                marker=".·.";
            } else if(a==3){
                marker="..·";
//                 marker="\\";
            } //else if(a==4){
//                 marker="|";
//            }
            i++;
            this.setTitle(marker+" "+this.label);                    
        }
    }
}

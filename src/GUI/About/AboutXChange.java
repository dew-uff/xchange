package GUI.About;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Carlos
 */
public class AboutXChange extends JDialog{

    /**
     * Construtor da classe sobre XChange
     */
    public AboutXChange(){
        
        // Criação dos painéis que seram utilizados
        JPanel pnlTop = new JPanel(new GridLayout(2, 1));
        JPanel pnlCenter = new JPanel();
        JPanel pnlBaixo = new JPanel(new FlowLayout());
        
        // Define o Titulo que estará no "pnlTop"
        JLabel titulo =  new JLabel("  XChange");
        titulo.setFont(new Font("Courier", Font.PLAIN, 14));
        
        // Define o Texto que estará no "pnlCenter"
        JTextArea areaCenter = new JTextArea(11,45); // JTextArea 11 linha e 45 colunas
        areaCenter.setFont(new Font("Courier", Font.PLAIN, 12)); // Fonte
        areaCenter.setEditable(false); // Não deixa o texto ser Editado
        areaCenter.setLineWrap(true); // Quebra de linha automatica
        areaCenter.setWrapStyleWord(true); // Não deixa as palavras serem separadas no final da linha
        areaCenter.setOpaque(false); // Deixa opaco, pegando a cor do fundo
        areaCenter.setText("An alternative to control the changes in XML documents is to adapt techniques "
                            + "from Software Configuration Management in the context of change control. Therefore, "
                            + "this work presents a proposal for detection and analysis of semantic changes between "
                            + "documents versions."
                            + "This approach is applied in the prototype XChange, which uses Prolog to perform inference data "
                            + "into XML documents via a graphical interface that enables the construction of "
                            + "rules in a high level. "
                            + "\n \n"
                            + "XChange is the result of research conducted by the group of Configuration Management of the GETComp and the project of research of scientific initiation, Configuration Management of XML.");
        
        // Carrega as imagens que serão utilizadas no painel inferior
        JLabel imgCC = new JLabel(new ImageIcon(getClass().getResource("/GUI/imageAbout/CC.png")));
        JLabel imgGET = new JLabel(new ImageIcon(getClass().getResource("/GUI/imageAbout/GET.png")));
        JLabel imgUFJF = new JLabel(new ImageIcon(getClass().getResource("/GUI/imageAbout/UFJF.png")));
        
        // Adiciona os componentes ao painéis
        pnlTop.add(titulo);
        pnlCenter.add(areaCenter);
        pnlBaixo.add(imgCC);
        pnlBaixo.add(imgGET);
        pnlBaixo.add(imgUFJF);
        
        // Adiciona os painéis a JDialog
        this.add(pnlTop, BorderLayout.NORTH);
        this.add(pnlCenter, BorderLayout.CENTER);
        this.add(pnlBaixo, BorderLayout.SOUTH);
        
        // Define propriedade da janela
        this.setLocationByPlatform(true); // relaciona a localização com a interface principal
        this.setTitle("About XChange"); // Titulo da janela
        this.setModal(true); // Não permite que a tela do XChange seja acessada antes desta ser fechada
        this.setResizable(false); // Não permite o redimensionamento 
        this.setAlwaysOnTop(false); // Esconde a janela se o XChange perder o foco
        this.pack(); // Deixa a janela com o melhor tamanho para seus componentes
        this.setVisible(true); // Deixa a janela Visível
    }
}
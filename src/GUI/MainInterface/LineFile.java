package GUI.MainInterface;

import Documents.Document;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Celio Henrique Nogueira Larcher Júnior, Jorge Moreira
 * Classe contém cada linha que representa um Document
 * que será usado no Diff.
 */
public class LineFile extends JPanel {

    private final JLabel lblFileName;
    private final JButton btnUp, btnDown;
    private final JCheckBox chkMine;
    private static JPanel pnlFiles;
    private Document document;

    public LineFile(Document document) {
        super(new FlowLayout());
        
        this.document = document;
        setMaximumSize(new Dimension(300, 32));
        
        lblFileName = new JLabel(document.getFile().getName());
        btnUp = new JButton("^");
        btnDown = new JButton("v");
        chkMine = new JCheckBox();
                
        add(chkMine);
        add(lblFileName);
        add(btnUp);
        add(btnDown);
    }
    
    public JButton getButtonUp(){
        return btnUp;
    }
    
    public JButton getButtonDown(){
        return btnDown;
    }
    
    public JCheckBox getCheckbox(){
        return chkMine;
    }
    
    public Document getDocument(){
        return document;
    }
    
    public void setDocument(Document document){
        this.document = document;
    }

    public static JPanel getPnlFiles() {
        return pnlFiles;
    }

    public static void setPnlFiles(JPanel pnlFiles) {
        LineFile.pnlFiles = pnlFiles;
    }

    public static ArrayList<LineFile> getLineFiles() {
        ArrayList<LineFile> lineFiles = new ArrayList<LineFile>();
        for (Component c : pnlFiles.getComponents()) {
            lineFiles.add((LineFile) c);
        }
        return lineFiles;
    }

    public static void setLineFiles(ArrayList<LineFile> lineFiles) {
        if (pnlFiles != null) {
            pnlFiles.removeAll();
            for (LineFile lineFile : lineFiles) {
                pnlFiles.add(lineFile);
            }
            pnlFiles.updateUI();
            pnlFiles.revalidate();
        }
    }
}

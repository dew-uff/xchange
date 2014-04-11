package GUI.FileManager;

import Documents.Document;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Classe contém cada linha que representa um Document que será usado no Diff.
 *
 * @author Celio Henrique Nogueira Larcher Júnior, Jorge Moreira
 */
public class LineFile extends JPanel {

    private final JLabel lblFileName;
    private final JButton btnUp, btnDown;
    private final JCheckBox chkMine;
    private Document document;

    public LineFile(Document document) {
        super();
        setMaximumSize(new Dimension(300, 32));
        FlowLayout flow = new FlowLayout();
        flow.setAlignment(FlowLayout.LEFT);
        setLayout(flow);
        setAlignmentX(LEFT_ALIGNMENT);

        this.document = document;
        setMaximumSize(new Dimension(300, 32));

        lblFileName = new JLabel(document.getFile().getName());
        btnUp = new JButton("^");
        btnDown = new JButton("v");
        chkMine = new JCheckBox();

        btnUp.setPreferredSize(new Dimension(15, 15));
        btnUp.setFont(new Font("verdana", 1, 8));
        btnUp.setHorizontalTextPosition(SwingConstants.LEFT);
        btnUp.setVerticalTextPosition(SwingConstants.TOP);
        btnUp.setMargin(new Insets(1, 1, 1, 1));

        btnDown.setPreferredSize(new Dimension(15, 15));
        btnDown.setFont(new Font("verdana", 1, 8));
        btnDown.setHorizontalTextPosition(SwingConstants.LEFT);
        btnDown.setVerticalTextPosition(SwingConstants.TOP);
        btnDown.setMargin(new Insets(1, 1, 1, 1));

        add(chkMine);
        add(lblFileName);
        add(btnUp);
        add(btnDown);
    }

    public JButton getButtonUp() {
        return btnUp;
    }

    public JButton getButtonDown() {
        return btnDown;
    }

    public JCheckBox getCheckbox() {
        return chkMine;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}

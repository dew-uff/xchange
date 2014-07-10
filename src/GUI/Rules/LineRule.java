package GUI.Rules;

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
public class LineRule extends JPanel {

    private JLabel lblRule;
    private JButton btnRemove;
    private JCheckBox chkMine;

    public LineRule(String rule, boolean isMined) {
        super();
        setMaximumSize(new Dimension(300, 32));
        FlowLayout flow = new FlowLayout();
        flow.setAlignment(FlowLayout.LEFT);
        setLayout(flow);
        setAlignmentX(LEFT_ALIGNMENT);

        if (!isMined) {
            chkMine = new JCheckBox();
            add(chkMine);
        }

        lblRule = new JLabel(rule);
        add(lblRule);

        btnRemove = new JButton("x");
        btnRemove.setPreferredSize(new Dimension(15, 15));
        btnRemove.setFont(new Font("verdana", 1, 8));
        btnRemove.setHorizontalTextPosition(SwingConstants.LEFT);
        btnRemove.setVerticalTextPosition(SwingConstants.TOP);
        btnRemove.setMargin(new Insets(1, 1, 1, 1));
        add(btnRemove);

    }

    public JLabel getLabel() {
        return lblRule;
    }

    public JButton getButtonRemove() {
        return btnRemove;
    }

    public JCheckBox getCheckbox() {
        return chkMine;
    }
}

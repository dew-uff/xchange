package Merge.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import Merge.model.loghandling.NodeConstants;
import Merge.model.vos.Node;
import Merge.resources.icons.Icons;

public class IconNodeRenderer extends DefaultTreeCellRenderer {

     private static final long serialVersionUID = 1L;

     private static final Color COLOR_INSERTED = Color.GREEN;

     private static final Color COLOR_DELETED = Color.RED;

     private static final Color COLOR_OK = Color.BLACK;

     private static final Color COLOR_UPDATED = Color.ORANGE;

     private static final Color COLOR_COPIED = Color.PINK;

     protected static ImageIcon ELEMENT_INSERTED = createImageIcon("element_add.png");

     protected static ImageIcon ELEMENT_UPDATED = createImageIcon("element_updated.png");

     protected static ImageIcon ELEMENT_DELETED = createImageIcon("element_delete.png");

     protected static ImageIcon ELEMENT_MOVE_IN = createImageIcon("element_move_in.png");

     protected static ImageIcon ELEMENT_MOVE_OUT = createImageIcon("element_move_out.png");

     protected static ImageIcon ELEMENT_COPY = createImageIcon("element_copy.png");

     protected static ImageIcon ELEMENT_OK = createImageIcon("element_ok.png");

     protected static ImageIcon ELEMENT = createImageIcon("element.png");

     protected static ImageIcon ATTRIBUTE = createImageIcon("attribute.png");
     
     protected static ImageIcon TEXT_INSERTED = createImageIcon("text_add.png");

     protected static ImageIcon TEXT_UPDATED = createImageIcon("text_updated.png");

     protected static ImageIcon TEXT_DELETED = createImageIcon("text_delete.png");

     protected static ImageIcon TEXT_MOVE_IN = createImageIcon("text_move_in.png");

     protected static ImageIcon TEXT_MOVE_OUT = createImageIcon("text_move_out.png");

     protected static ImageIcon TEXT_COPY = createImageIcon("text_copy.png");

     protected static ImageIcon TEXT_OK = createImageIcon("text_ok.png");

     /** Returns an ImageIcon, or null if the path was invalid. */
     protected static ImageIcon createImageIcon(String path) {
          java.net.URL imgURL = Icons.class.getResource(path);
          if (imgURL != null) {
               return new ImageIcon(imgURL);
          } else {
               System.err.println("Couldn't find file: " + path);
               return null;
          }
     }

     public IconNodeRenderer() {

     }

     public Component getTreeCellRendererComponent(JTree tree, Object value,
               boolean sel, boolean expanded, boolean leaf, int row,
               boolean hasFocus) {
          if (sel)
               setForeground(getTextSelectionColor());
          else
               setForeground(getTextNonSelectionColor());

          DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
          Object userObject = node.getUserObject();
          if (userObject == null || userObject instanceof String) {
               setTextNonSelectionColor(COLOR_OK);
               setTextSelectionColor(COLOR_OK);
               return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
          }
          Node nodeModel = ((Node) userObject);
          int status = nodeModel.getStatus();
          setEnabled(!nodeModel.isRemoved());
          setText(nodeModel.getLabel());

          if (nodeModel.getType() == NodeConstants.TYPE_ATTIBUTE) {
               setIcon(ATTRIBUTE);
          } else {     

               switch (status) {
               case NodeConstants.STATUS_OK:
                    if (nodeModel.getType() == NodeConstants.TYPE_TEXT)
                         setIcon(TEXT_OK);
                    else 
                         setIcon(ELEMENT);
                    // setTextNonSelectionColor(COLOR_OK);
                    break;
               case NodeConstants.STATUS_DELETED:
                    if (nodeModel.getType() == NodeConstants.TYPE_TEXT)
                         setIcon(TEXT_DELETED);
                    else
                         setIcon(ELEMENT_DELETED);
                    // setTextNonSelectionColor(COLOR_DELETED);
                    break;
               case NodeConstants.STATUS_INSERTED:
                    if (nodeModel.getType() == NodeConstants.TYPE_TEXT)
                         setIcon(TEXT_INSERTED);
                    else
                         setIcon(ELEMENT_INSERTED);
                    // setTextNonSelectionColor(COLOR_INSERTED);
                    break;
               case NodeConstants.STATUS_UPDATED:
                    if (nodeModel.getType() == NodeConstants.TYPE_TEXT)
                         setIcon(TEXT_UPDATED);
                    else
                         setIcon(ELEMENT_UPDATED);
                    // setTextNonSelectionColor(COLOR_UPDATED);
                    break;
               case NodeConstants.STATUS_COPIED:
                    if (nodeModel.getType() == NodeConstants.TYPE_TEXT)
                         setIcon(TEXT_COPY);
                    else
                         setIcon(ELEMENT_COPY);
                    // setTextNonSelectionColor(COLOR_COPIED);
                    break;
               case NodeConstants.STATUS_MOVED_IN:
                    if (nodeModel.getType() == NodeConstants.TYPE_TEXT)
                         setIcon(TEXT_MOVE_IN);
                    else
                         setIcon(ELEMENT_MOVE_IN);
                    // setTextNonSelectionColor(COLOR_COPIED);
                    break;
               case NodeConstants.STATUS_MOVED_OUT:
                    if (nodeModel.getType() == NodeConstants.TYPE_TEXT)
                         setIcon(TEXT_MOVE_OUT);
                    else
                         setIcon(ELEMENT_MOVE_OUT);
                    // setTextNonSelectionColor(COLOR_COPIED);
                    break;
               }
          }
          setComponentOrientation(tree.getComponentOrientation());

          selected = sel;

          return this;// super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
     }

     class DisabledLeafIcon implements Icon {
          int width = 16;

          int height = 16;

          int additionalHeight = 4;

          public void paintIcon(Component c, Graphics g, int x, int y) {
               int right = width - 1;
               int bottom = height + 1;

               g.setColor(c.getBackground());
               g.fillRect(0, 0, width, getIconHeight());

               // Draw frame
               g.setColor(MetalLookAndFeel.getControlDisabled());
               g.drawLine(2, 2, 2, bottom); // left
               g.drawLine(2, 2, right - 4, 2); // top
               g.drawLine(2, bottom, right - 1, bottom); // bottom
               g.drawLine(right - 1, 8, right - 1, bottom); // right
               g.drawLine(right - 6, 4, right - 2, 8); // slant 1
               g.drawLine(right - 5, 3, right - 4, 3); // part of slant 2
               g.drawLine(right - 3, 4, right - 3, 5); // part of slant 2
               g.drawLine(right - 2, 6, right - 2, 7); // part of slant 2
          }

          public int getIconWidth() {
               return width;
          }

          public int getIconHeight() {
               return height + additionalHeight;
          }
     }
}

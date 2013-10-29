package Merge.view.frames;

import GUI.Layout.LayoutConstraints;
import GUI.MainInterface.MainInterface;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import Merge.model.TreeNodeExporter;
import Merge.model.XMLEditHandler;
import Merge.model.loghandling.NodeConstants;
import Merge.model.vos.Node;
import Merge.view.dndtree.NodeMoveTransferHandler;
import Merge.view.dndtree.TreeDropTarget;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;

public class DiffFrame extends JPanel {

     private static final long serialVersionUID = 1L;
     private JPanel jPanel = null;
     private JScrollPane jScrollPane = null;
     private JPanel jPanel1 = null;
     private JTree jTree = null;
     private JLabel jLabel = null;
     private JLabel jLabelSelecionado = null;
     private JLabel jLabel1 = null;
     private JLabel jLabelStatus = null;
     private JLabel jLabel3 = null;
     private JLabel jLabelVersao = null;
     private JButton jButtonAplicar = null;
     private JButton jButtonCancelar = null;
     private JLabel jLabelNome = null;
     private JTextField jTextFieldNome = null;
     private JLabel jLabelValor = null;
     private JTextField jTextFieldValor = null;
     private JPanel jPanelSpacer = null;
     private boolean fireEditChanges;
     private JLabel jLabelTexto = null;
     private JTextPane jTextPaneTexto = null;     
     private String saidaFileName;
     private JScrollPane textScrollPane = null;
     
     /**
      * This method initializes
      * 
      */
     public DiffFrame() {
          super();
          initialize();
          this.saidaFileName = "";
     }

     /**
      * This method initializes this
      * 
      */
     private void initialize(){
        this.setJButtonCancel();
        this.setJButtonAplicar();
        GridBagLayout gridBag = new GridBagLayout();
        this.setLayout(gridBag);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanelMerge = getJPanel();
        LayoutConstraints.setConstraints(gridBagConstraints,0,0,1,1,1,1);
        gridBagConstraints.insets=new Insets(0,0,0,0);
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
        gridBag.setConstraints(jPanelMerge,gridBagConstraints);
        this.add(jPanelMerge);
     }

     /**
      * This method initializes jPanel
      * 
      * @return javax.swing.JPanel
      */
     private JPanel getJPanel() {
          if (jPanel == null) {
              jPanel = new JPanel();
              GridBagLayout gridBag = new GridBagLayout();
              jPanel.setLayout(gridBag);
              GridBagConstraints gridBagConstraints = new GridBagConstraints();
                           
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,0,0,1,1,100,100);
              gridBagConstraints.insets=new Insets(2,2,2,2);
              gridBagConstraints.fill=GridBagConstraints.BOTH;
              gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
              
              gridBag.setConstraints(getJScrollPane(),gridBagConstraints);
              jPanel.add(getJScrollPane());
              
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,1,0,1,1,100,100);
              gridBag.setConstraints(getJPanel1(),gridBagConstraints);
              jPanel.add(getJPanel1());
          }
          return jPanel;
     }

     /**
      * This method initializes jScrollPane
      * 
      * @return javax.swing.JScrollPane
      */
     private JScrollPane getJScrollPane() {
          if (jScrollPane == null) {
               jScrollPane = new JScrollPane();
               jScrollPane.setViewportView(getJTree());
          }
          return jScrollPane;
     }

     /**
      * This method initializes jPanel1
      * 
      * @return javax.swing.JPanel
      */
     private JPanel getJPanel1() {
          if (jPanel1 == null) {
              jPanel1 = new JPanel();
              GridBagLayout gridBag = new GridBagLayout();
              jPanel1.setLayout(gridBag);
              GridBagConstraints gridBagConstraints = new GridBagConstraints();
                           
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,1,5,1,1,100,1);
              gridBagConstraints.insets=new Insets(2,2,2,2);
              gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
              gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
              
              gridBag.setConstraints(getJTextFieldValor(), gridBagConstraints);
              jPanel1.add(getJTextFieldValor());
              
              jLabelValor = new JLabel();
              jLabelValor.setText("Value:");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,0,5,1,1,1,1);
              gridBag.setConstraints(jLabelValor,gridBagConstraints);
              jPanel1.add(jLabelValor);
              
              jLabel = new JLabel();
              jLabel.setText("Selected: ");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 0, 0, 1, 1, 1, 1);
              gridBag.setConstraints(jLabel, gridBagConstraints);
              jPanel1.add(jLabel);
              
              jLabelSelecionado = new JLabel();
              jLabelSelecionado.setText("");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 1, 0, 1, 1, 100, 1);
              gridBag.setConstraints(jLabelSelecionado, gridBagConstraints);
              jPanel1.add(jLabelSelecionado);
              
              jLabel1 = new JLabel();
              jLabel1.setText("Status: ");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 0, 1, 1, 1, 1, 1);
              gridBag.setConstraints(jLabel1, gridBagConstraints);
              jPanel1.add(jLabel1);
              
              jLabelStatus = new JLabel();
              jLabelStatus.setText("");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 1, 1, 1, 1, 100, 1);
              gridBag.setConstraints(jLabelStatus, gridBagConstraints);
              jPanel1.add(jLabelStatus);
              
              jLabel3 = new JLabel();
              jLabel3.setText("Version: ");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 0, 2, 1, 1, 1, 1);
              gridBag.setConstraints(jLabel3, gridBagConstraints);
              jPanel1.add(jLabel3);
              
              jLabelVersao = new JLabel();
              jLabelVersao.setText("");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 1, 2, 1, 1, 100, 1);
              gridBag.setConstraints(jLabelVersao, gridBagConstraints);
              jPanel1.add(jLabelVersao);
              
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 0, 3, 2, 1, 100, 20);
              gridBag.setConstraints(getJPanelSpacer(), gridBagConstraints);
              jPanel1.add(getJPanelSpacer());
              
              jLabelNome = new JLabel();
              jLabelNome.setText("Name: ");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 0, 4, 1, 1, 1, 1);
              gridBag.setConstraints(jLabelNome, gridBagConstraints);
              jPanel1.add(jLabelNome);
              
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 1, 4, 1, 1, 100, 1);
              gridBag.setConstraints(getJTextFieldNome(), gridBagConstraints);
              jPanel1.add(getJTextFieldNome());
              
              jLabelTexto = new JLabel();
              jLabelTexto.setText("Text: ");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints, 0, 6, 1, 1, 1, 1);
              gridBag.setConstraints(jLabelTexto, gridBagConstraints);
              jPanel1.add(jLabelTexto);
              
              textScrollPane = new JScrollPane(getJTextPaneTexto());
              textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
              textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
              
              //indica a posição e layout
              gridBagConstraints.fill=GridBagConstraints.BOTH;
              LayoutConstraints.setConstraints(gridBagConstraints, 1, 6, 1, 1, 100, 10);
              gridBag.setConstraints(textScrollPane, gridBagConstraints);
              jPanel1.add(textScrollPane);

          }
          return jPanel1;
     }

     /**
      * This method initializes jTree
      * 
      * @return javax.swing.JTree
      */
     public JTree getJTree() {
          if (jTree == null) {
               jTree = new JTree();
               NodeMoveTransferHandler handler = new NodeMoveTransferHandler();
               jTree.setTransferHandler(handler);
               jTree.setDropTarget(new TreeDropTarget(handler));
               jTree.setDragEnabled(true);
               

               jTree.addTreeSelectionListener(new TreeSelectionListener() {
                    public void valueChanged(TreeSelectionEvent evt) {
                         treeSelecionChanged();
                    }
               });

               jTree.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                         if (e.isPopupTrigger() && e.getClickCount() == 1) {
                              doPopup(e.getX(), e.getY());
                         }
                    }

                    public void mouseReleased(MouseEvent e) {
                         if (e.isPopupTrigger() && e.getClickCount() == 1) {
                              doPopup(e.getX(), e.getY());
                         }
                    }

                    public void doPopup(int x, int y) {
                         // Get the tree element under the mouse
                         TreePath clickedElement = jTree.getPathForLocation(x, y);

                         // Update the selection if necessary
                         updateSelection(clickedElement);

                         /*
                          * currentSelectionField.setText("Display Popup for: " + clickedElementName);
                          */

                         // Get the desired context menu and show it
                         JPopupMenu contextMenu = retrieveContextMenu(clickedElement);
                         contextMenu.show(jTree, x, y);
                    }

                    private JPopupMenu retrieveContextMenu(
                              TreePath clickedElement) {
                         JPopupMenu contextMenu;

                         if (clickedElement != null)
                              contextMenu = retrieveElementContextMenu(clickedElement);
                         else
                              contextMenu = null;

                         if (contextMenu != null) {
                              // This is the code that attempts but fails to shrink the menu to fit the current commands
                              // Make sure the size of the menu is uptodate with any chages made to its actions before display
                              contextMenu.invalidate();
                              contextMenu.pack();
                         }

                         return contextMenu;
                    }

                    private JPopupMenu retrieveElementContextMenu(
                              TreePath clickedElement) {
                         if (clickedElement == null)
                              return null;

                         DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) clickedElement.getLastPathComponent();
                         Node node = (Node) lastPathComponent.getUserObject();
                         boolean isElement = node.getType() == NodeConstants.TYPE_ELEMENT;
                         boolean isDeleted = node.isRemoved();

                         if (myElementContextMenu == null) {
                              // First asked for element context menu - build it
                              myElementContextMenu = new JPopupMenu("Element Context Menu");

                              // Create and add the element action
                              deleteElementAction = new ElementAction(lastPathComponent, ElementAction.DELETE);
                              undeleteElementAction = new ElementAction(lastPathComponent, ElementAction.UNDELETE);
                              addChildElementAction = new ElementAction(lastPathComponent, ElementAction.ADDCHILDELEMENT);
                              addChildAttributeAction = new ElementAction(lastPathComponent, ElementAction.ADDCHILDATTRIBUTE);
                              addChildTextAction = new ElementAction(lastPathComponent, ElementAction.ADDCHILDTEXT);
                              myElementContextMenu.add(deleteElementAction);
                              myElementContextMenu.add(undeleteElementAction);
                              myElementContextMenu.addSeparator();
                              myElementContextMenu.add(addChildElementAction);
                              myElementContextMenu.add(addChildAttributeAction);
                              myElementContextMenu.add(addChildTextAction);
                              
                         } else {
                              // Update the action to refer to the clicked on element
                              deleteElementAction.setElementNode(lastPathComponent);
                              undeleteElementAction.setElementNode(lastPathComponent);
                              addChildElementAction.setElementNode(lastPathComponent);
                              addChildAttributeAction.setElementNode(lastPathComponent);
                              addChildTextAction.setElementNode(lastPathComponent);
                         }
                         addChildAttributeAction.setEnabled(isElement);
                         addChildElementAction.setEnabled(isElement);
                         addChildTextAction.setEnabled(isElement);
                         deleteElementAction.setEnabled(!isDeleted);
                         undeleteElementAction.setEnabled(isDeleted);
                         return myElementContextMenu;
                    }

                    private void updateSelection(TreePath clickedElement) {

                         // Find out if the clicked on element is already selected
                         boolean clickedElementSelected = false;
                         TreePath[] selection = jTree.getSelectionPaths();
                         if (clickedElement != null && selection != null) {
                              // Determine if it one of the selected paths
                              for (int index = 0; index < selection.length; ++index) {
                                   if (clickedElement.equals(selection[index])) {
                                        clickedElementSelected = true;
                                        break;
                                   }
                              }
                         }

                         // Select the clicked on element or clear all selections
                         if (!clickedElementSelected) {
                              if (clickedElement != null) {
                                   // Clicked on unselected item - make it the selection
                                   jTree.setSelectionPath(clickedElement);
                              } else {
                                   // clicked over nothing clear the selection
                                   jTree.clearSelection();
                              }
                         }
                    }

                    private JPopupMenu myElementContextMenu = null;

                    private ElementAction deleteElementAction = null;

                    private ElementAction undeleteElementAction = null;
                    
                    private ElementAction addChildElementAction = null;
                    
                    private ElementAction addChildAttributeAction = null;
                    
                    private ElementAction addChildTextAction = null;
               });

          }
          return jTree;
     }

     private void treeSelecionChanged() {
          TreePath path = jTree.getSelectionPath();
          JTree xmlTree = jTree;

          fireEditChanges = false;

          if (path != null) {
               DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
               Object userObject = treeNode.getUserObject();
               if (userObject instanceof Node) {
                    Node node = (Node) userObject;
                    jLabelSelecionado.setText(node.getLabel());
                    jLabelStatus.setText(node.getStatusText());
                    jLabelVersao.setText(node.getBranchText());

                    if (node.getType() == NodeConstants.TYPE_ELEMENT) {
                         setVisibilityName(true);
                         getJTextFieldNome().setText(node.getLabel());
                         setVisibilityValor(false);
                         setVisibilityTexto(false);
                    } else if (node.getType() == NodeConstants.TYPE_ATTIBUTE) {
                         setVisibilityName(true);
                         setVisibilityValor(true);
                         getJTextFieldNome().setText(node.getAttibuteName());
                         getJTextFieldValor().setText(node.getAttibuteValue());
                         setVisibilityTexto(false);
                    } else if (node.getType() == NodeConstants.TYPE_TEXT) {
                         setVisibilityName(false);
                         setVisibilityValor(false);
                         setVisibilityTexto(true);
                         getJTextPaneTexto().setText(node.getLabel());
                    }

               } else {
                    jLabelSelecionado.setText("");
                    jLabelStatus.setText("");
                    jLabelVersao.setText("");
                    setVisibilityName(false);
                    setVisibilityValor(false);
                    setVisibilityTexto(false);
               }

          }
          fireEditChanges = true;
     }

     private void setVisibilityName(boolean visible) {
          jLabelNome.setVisible(visible);
          getJTextFieldNome().setVisible(visible);
     }

     private void setVisibilityValor(boolean visible) {
          jLabelValor.setVisible(visible);
          getJTextFieldValor().setVisible(visible);
     }

     private void setVisibilityTexto(boolean visible) {
          jLabelTexto.setVisible(visible);
          textScrollPane.setVisible(visible);
          getJTextPaneTexto().setVisible(visible);
     }
     
     
     public class ElementAction extends AbstractAction {

          private static final long serialVersionUID = 1L;

          public static final int DELETE = 0;

          public static final int UNDELETE = 1;
          
          public static final int ADDCHILDELEMENT = 2;
          
          public static final int ADDCHILDATTRIBUTE = 3;
          
          public static final int ADDCHILDTEXT = 4;

          public ElementAction(TreeNode node, int action) {
               super(getActionName(action) + node.toString());
               contextElementNode = node;
               this.action = action;
          }

          public void setElementNode(TreeNode node) {
               contextElementNode = node;
               putValue(NAME, (getActionName(action) + contextElementNode));
          }

          public void actionPerformed(ActionEvent e) {
               DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
               switch (action) {
               case DELETE:
                    XMLEditHandler.getInstance().applyManualDelete(contextElementNode);
                    break;
               case UNDELETE:
                    XMLEditHandler.getInstance().applyManualUndelete(contextElementNode);
                    break;
               case ADDCHILDELEMENT:
                    XMLEditHandler.getInstance().applyManualAddNode(contextElementNode, NodeConstants.TYPE_ELEMENT, (DefaultTreeModel)jTree.getModel());
                    break;
               case ADDCHILDATTRIBUTE:
                    XMLEditHandler.getInstance().applyManualAddNode(contextElementNode, NodeConstants.TYPE_ATTIBUTE, (DefaultTreeModel)jTree.getModel());
                    break;
               case ADDCHILDTEXT:
                    XMLEditHandler.getInstance().applyManualAddNode(contextElementNode, NodeConstants.TYPE_TEXT, (DefaultTreeModel)jTree.getModel());
                    break;
               }
               model.nodeChanged(contextElementNode);
               jTree.repaint();
               treeSelecionChanged();
          }

          private TreeNode contextElementNode = null;

          private int action;
     }

     public static String getActionName(int action) {
          switch (action) {
          case ElementAction.DELETE:
               return "Remove - ";
          case ElementAction.UNDELETE:
               return "Cancel Deletion - ";
          case ElementAction.ADDCHILDELEMENT:
               return "Add element to ";
          case ElementAction.ADDCHILDATTRIBUTE:
               return "Add attribute to ";
          case ElementAction.ADDCHILDTEXT:
               return "Add text to ";
          }
          return "";
     }

     /**
      * This method initializes jButtonAplicar
      * 
      * @return javax.swing.JButton
      */
     private void setJButtonAplicar() {
               jButtonAplicar = MainInterface.getWriteMerged();
               for(ActionListener actionListener : jButtonAplicar.getActionListeners()){
                  jButtonAplicar.removeActionListener(actionListener);
               }
               jButtonAplicar.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if(e.getSource() == jButtonAplicar){
                            JFileChooser chooser = new JFileChooser();
                            String pathWay = "";
                            int openedFile = chooser.showSaveDialog(null);
                            if (openedFile==JFileChooser.APPROVE_OPTION){
                                pathWay = chooser.getSelectedFile().getAbsolutePath();  // o getSelectedFile pega o arquivo e o getAbsolutePath retorna uma string contendo o endereço.
                            }
                            if(!pathWay.equals("")){
                                String name = pathWay;
                                if(!name.toLowerCase().endsWith(".xml")) {
                                    name+=".xml";
                                }
                                saidaFileName = name;
                            }
                            TreeNodeExporter.getInstance().printTreeNode((DefaultMutableTreeNode) jTree.getModel().getRoot(), saidaFileName);
                        }
                    }
               }); 
     }

     /**
      * This method initializes jButtonCancelar
      * 
      * @return javax.swing.JButton
      */
     private void setJButtonCancel(){
               jButtonCancelar = MainInterface.getCancelBtn();
               jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                         //System.exit(1);
                    	jPanel.setVisible(false);
                    }
               });
     }

     /**
      * This method initializes jTextFieldNome
      * 
      * @return javax.swing.JTextField
      */
     private JTextField getJTextFieldNome() {
          if (jTextFieldNome == null) {
               jTextFieldNome = new JTextField();
               jTextFieldNome.getDocument().addDocumentListener(new TextDocumentListener());
               jTextFieldNome.getDocument().putProperty("name", "name");

               // jTextFieldNome.setVisible(false);
          }
          return jTextFieldNome;
     }

     /**
      * This method initializes jTextFieldValor
      * 
      * @return javax.swing.JTextField
      */
     private JTextField getJTextFieldValor() {
          if (jTextFieldValor == null) {
               jTextFieldValor = new JTextField();
               jTextFieldValor.getDocument().addDocumentListener(new TextDocumentListener());
               jTextFieldValor.getDocument().putProperty("name", "value");

               // jTextFieldValor.setVisible(false);
          }
          return jTextFieldValor;
     }

     /**
      * This method initializes jPanelSpacer
      * 
      * @return javax.swing.JPanel
      */
     private JPanel getJPanelSpacer() {
          if (jPanelSpacer == null) {
               jPanelSpacer = new JPanel();
               jPanelSpacer.setLayout(new GridBagLayout());
          }
          return jPanelSpacer;
     }

     class TextDocumentListener implements DocumentListener {
          String newline = "\n";

          public void insertUpdate(DocumentEvent e) {
               updateLog(e);
          }

          public void removeUpdate(DocumentEvent e) {
               updateLog(e);
          }

          public void changedUpdate(DocumentEvent e) {
               // Plain text components do not fire these events
          }

          public void updateLog(DocumentEvent e) {
               Document doc = (Document) e.getDocument();
               if (fireEditChanges) {
                    Object propertyName = doc.getProperty("name");
                    TreePath path = jTree.getSelectionPath();
                    
                    DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Node node = (Node) lastPathComponent.getUserObject();
                    
                    if (propertyName.equals("name")) {
                         node.setName( jTextFieldNome.getText() );
                    } else if (propertyName.equals("value")) {
                         node.setValue( jTextFieldValor.getText() );
                    } else if (propertyName.equals("text")) {
                         node.setLabel( jTextPaneTexto.getText() );
                    }
                    node.setBranch(NodeConstants.BRANCH_USER);
                    node.setStatus(NodeConstants.STATUS_UPDATED);
                    DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
                    model.nodeChanged(lastPathComponent);
                    jTree.repaint();
               }
          }

     }

     /**
      * This method initializes jTextPaneTexto	
      * 	
      * @return javax.swing.JTextPane	
      */
     private JTextPane getJTextPaneTexto() {
          if (jTextPaneTexto == null) {
               jTextPaneTexto = new JTextPane();
               jTextPaneTexto.getDocument().addDocumentListener(new TextDocumentListener());
               jTextPaneTexto.getDocument().putProperty("name", "text");

          }
          return jTextPaneTexto;
     }
} // @jve:decl-index=0:visual-constraint="10,10"

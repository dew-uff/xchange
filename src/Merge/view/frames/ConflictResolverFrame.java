package Merge.view.frames;

import GUI.Layout.LayoutConstraints;
import GUI.MainInterface.MainInterface;
import GUI.Util.MainInterfaceHandler;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;

import Merge.model.ConflictResolverControler;
import Merge.view.MultilineLabel;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;

public class ConflictResolverFrame extends JPanel {

     private static final long serialVersionUID = 1L;
     private JPanel jPanel = null;
     private JPanel jPanelSelecaoConflito = null;
     private JPanel jPanelConflictList = null;
     private JPanel jPanelMergeOption = null;
     private JPanel jPanelMergeConflictsDescription = null;
     private JScrollPane jScrollPaneTreeM1 = null;
     private JTree jTreeM1 = null;
     private JScrollPane jScrollPaneTreeM2 = null;
     private JTree jTreeM2 = null;
     private JLabel jLabelM1 = null;
     private JLabel jLabelM2 = null;
     private JLabel jLabelConflictList = null;
     private JLabel jLabelTipo = null;
     private JScrollPane jScrollPaneConflictList = null;
     private JList jListConflict = null;
     private JLabel jLabelTipoConflito = null;
     private JLabel jLabelChosen = null;
     private JRadioButton jRadioButtonBranchM1 = null;
     private JRadioButton jRadioButtonBranchM2 = null;
     private JRadioButton jRadioButtonBranchBoth = null;
     
     private MultilineLabel jLabelUseVersion1 = null;
     private MultilineLabel jLabelUseVersion2 = null;
     private MultilineLabel jLabelUseBoth = null;
     
     private JButton jButtonConfirmar = null;
     private JButton jButtonCancel = null;

     
     private ConflictResolverControler controller;
     private JScrollPane jScrollPaneBase = null;
     private JTree jTreeBase = null;
     private JLabel jLabel = null;
     private JLabel jLabel1 = null;
     private MultilineLabel jLabelDescription = null;
      /**
      * This method initializes 
      * 
      */
     public ConflictResolverFrame(ConflictResolverControler controller, MainInterface mainInterface) {
     	super();
     	initialize(mainInterface);
        this.controller = controller;
     }

     /**
      * This method initializes this
      * 
      */
     private void initialize(MainInterface mainInterface) {
        this.setJButtonConfirmar(mainInterface);
        this.setJButtonCancel();
        GridBagLayout gridBag = new GridBagLayout();
        this.setLayout(gridBag);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JPanel jPanelMerge = getJPanel();
        //indica a posição e layout
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
              LayoutConstraints.setConstraints(gridBagConstraints,0,2,3,1,1,100);
              gridBagConstraints.insets=new Insets(2,2,2,2);
              gridBagConstraints.fill=GridBagConstraints.BOTH;
              gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
              
              gridBag.setConstraints(getJPanelSelecaoConflito(),gridBagConstraints);
              jPanel.add(getJPanelSelecaoConflito());
             
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,0,1,1,1,100,300);
              gridBag.setConstraints(getJScrollPaneBase(),gridBagConstraints);
              jPanel.add(getJScrollPaneBase());
              
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,1,1,1,1,100,100);
              gridBag.setConstraints(getJScrollPaneTreeM1(),gridBagConstraints);
              jPanel.add(getJScrollPaneTreeM1());
              
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,2,1,1,1,100,100);
              gridBag.setConstraints(getJScrollPaneTreeM2(),gridBagConstraints);
              jPanel.add(getJScrollPaneTreeM2());
              
              jLabel = new JLabel();
              jLabel.setText("Base Version");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,0,0,1,1,1,1);
              gridBag.setConstraints(jLabel,gridBagConstraints);
              jPanel.add(jLabel);
              
              jLabelM1 = new JLabel();
              jLabelM1.setText("Version 1");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,1,0,1,1,1,1);
              gridBag.setConstraints(jLabelM1,gridBagConstraints);
              jPanel.add(jLabelM1);
              
              jLabelM2 = new JLabel();
              jLabelM2.setText("Version 2");
              //indica a posição e layout
              LayoutConstraints.setConstraints(gridBagConstraints,2,0,1,1,1,1);
              gridBag.setConstraints(jLabelM2,gridBagConstraints);
              jPanel.add(jLabelM2);
          }
          return jPanel;
     }

     /**
      * This method initializes jPanelSelecaoConflito	
      * 	
      * @return javax.swing.JPanel	
      */
     private JPanel getJPanelSelecaoConflito() {
          if (jPanelSelecaoConflito == null) {
              jPanelSelecaoConflito = new JPanel();
              GridBagLayout gridBag = new GridBagLayout();
              jPanelSelecaoConflito.setLayout(gridBag);
              GridBagConstraints gridBagConstraints = new GridBagConstraints();
              
              gridBagConstraints.insets=new Insets(2,2,2,10);
              gridBagConstraints.fill=GridBagConstraints.BOTH;
              gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
              
              //indica a posição e layout da barra de ferramentas
              JPanel jPanelConfictList = getJScrollPaneConflictList();
              jPanelConfictList.setBorder(javax.swing.BorderFactory.createTitledBorder("Conflicts List"));
              LayoutConstraints.setConstraints(gridBagConstraints,0,0,1,1,1,100);
              gridBag.setConstraints(jPanelConfictList,gridBagConstraints);
              jPanelSelecaoConflito.add(jPanelConfictList);
              
              jPanelMergeConflictsDescription = getMergeConflictsDescription();
              LayoutConstraints.setConstraints(gridBagConstraints,1,0,1,1,1,1);
              gridBag.setConstraints(jPanelMergeConflictsDescription,gridBagConstraints);
              jPanelSelecaoConflito.add(jPanelMergeConflictsDescription);
              
              jPanelMergeOption = getMergeOption();
              LayoutConstraints.setConstraints(gridBagConstraints,2,0,1,1,1,1);
              gridBag.setConstraints(jPanelMergeOption,gridBagConstraints);
              jPanelSelecaoConflito.add(jPanelMergeOption);
               
              ButtonGroup branchGroup = new ButtonGroup();
              branchGroup.add(getJRadioButtonBranchM1());
              branchGroup.add(getJRadioButtonBranchM2());
              branchGroup.add(getJRadioButtonBranchBoth());     
          }
          return jPanelSelecaoConflito;
     }
     
     private JPanel getMergeConflictsDescription(){
        JPanel jPanelConflictsDescription = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        jPanelConflictsDescription.setLayout(gridBag);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        //indica a posição e layout
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
        
        jLabelTipo = new JLabel();
        jLabelTipo.setText("Conflict:");
        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,0,0,1,1,1,1);
        gridBag.setConstraints(jLabelTipo,gridBagConstraints);
        jPanelConflictsDescription.add(jLabelTipo);
        
        jLabelTipoConflito = new JLabel();
        jLabelTipoConflito.setText("");
        LayoutConstraints.setConstraints(gridBagConstraints,1,0,1,1,100,1);
        gridBag.setConstraints(jLabelTipoConflito,gridBagConstraints);
        jPanelConflictsDescription.add(jLabelTipoConflito);

        jLabel1 = new JLabel();
        jLabel1.setText("Description:");
        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,0,1,1,1,1,1);
        gridBag.setConstraints(jLabel1,gridBagConstraints);
        jPanelConflictsDescription.add(jLabel1);

        jLabelDescription = new MultilineLabel("");
        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,1,1,1,1,100,1);
        gridBag.setConstraints(jLabelDescription,gridBagConstraints);
        jPanelConflictsDescription.add(jLabelDescription);
              
        return jPanelConflictsDescription;
     }
     
     private JPanel getMergeOption(){
        JPanel jPanelOption = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        jPanelOption.setLayout(gridBag);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        //indica a posição e layout
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
        
        jLabelChosen = new JLabel();
        jLabelChosen.setText("Use: ");
        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,0,0,1,1,1,1);
        gridBag.setConstraints(jLabelChosen,gridBagConstraints);
        jPanelOption.add(jLabelChosen);

        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,1,0,1,1,100,1);
        gridBag.setConstraints(getJRadioButtonBranchM1(),gridBagConstraints);
        jPanelOption.add(getJRadioButtonBranchM1());
        
        //descrição da versão 1
        
        jLabelUseVersion1 = new MultilineLabel("");
        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,1,1,2,1,100,1);
        gridBag.setConstraints(jLabelUseVersion1,gridBagConstraints);
        jPanelOption.add(jLabelUseVersion1);        
        

        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,1,2,1,1,100,1);
        gridBag.setConstraints(getJRadioButtonBranchM2(),gridBagConstraints);
        jPanelOption.add(getJRadioButtonBranchM2());
        
        //descrição da versão 2
         jLabelUseVersion2 = new MultilineLabel("");
        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,1,3,2,1,100,1);
        gridBag.setConstraints(jLabelUseVersion2,gridBagConstraints);
        jPanelOption.add(jLabelUseVersion2);  

        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,1,4,1,1,100,1);
        gridBag.setConstraints(getJRadioButtonBranchBoth(),gridBagConstraints);
        jPanelOption.add(getJRadioButtonBranchBoth());
        
        
        //descrição de ambos
         jLabelUseBoth = new MultilineLabel("");
        //indica a posição e layout
        LayoutConstraints.setConstraints(gridBagConstraints,1,5,2,1,100,1);
        gridBag.setConstraints(jLabelUseBoth,gridBagConstraints);
        jPanelOption.add(jLabelUseBoth);  
       
        return jPanelOption;
     }

     /**
      * This method initializes jScrollPaneTreeB	
      * 	
      * @return javax.swing.JScrollPane	
      */
     private JScrollPane getJScrollPaneTreeM1() {
          if (jScrollPaneTreeM1 == null) {
               jScrollPaneTreeM1 = new JScrollPane();
               jScrollPaneTreeM1.setViewportView(getJTreeM1());
          }
          return jScrollPaneTreeM1;
     }

     /**
      * This method initializes jTreeBase	
      * 	
      * @return javax.swing.JTree	
      */
     public JTree getJTreeM1() {
          if (jTreeM1 == null) {
               jTreeM1 = new JTree();
          }
          return jTreeM1;
     }

     /**
      * This method initializes jScrollPaneTreeM1	
      * 	
      * @return javax.swing.JScrollPane	
      */
     private JScrollPane getJScrollPaneTreeM2() {
          if (jScrollPaneTreeM2 == null) {
               jScrollPaneTreeM2 = new JScrollPane();
               jScrollPaneTreeM2.setViewportView(getJTreeM2());
          }
          return jScrollPaneTreeM2;
     }

     /**
      * This method initializes jTreeM1	
      * 	
      * @return javax.swing.JTree	
      */
     public JTree getJTreeM2() {
          if (jTreeM2 == null) {
               jTreeM2 = new JTree();
          }
          return jTreeM2;
     }

     /**
      * This method initializes jScrollPaneConflictList	
      * 	
      * @return javax.swing.JScrollPane	
      */
     private JPanel getJScrollPaneConflictList() {
          if (jScrollPaneConflictList == null) {
              jPanelConflictList = new JPanel();
              GridBagLayout gridBag = new GridBagLayout();
              jPanelConflictList.setLayout(gridBag);
              GridBagConstraints gridBagConstraints = new GridBagConstraints();
              
              gridBagConstraints.insets=new Insets(2,2,2,2);
              gridBagConstraints.fill=GridBagConstraints.BOTH;
              gridBagConstraints.anchor=GridBagConstraints.NORTHWEST;
              
              jScrollPaneConflictList = new JScrollPane();
              jScrollPaneConflictList.setViewportView(getJListConflict());
              
              LayoutConstraints.setConstraints(gridBagConstraints,0,0,1,1,100,100);
              gridBag.setConstraints(jScrollPaneConflictList,gridBagConstraints);
              jPanelConflictList.add(jScrollPaneConflictList);
          }
          return jPanelConflictList;
     }

     /**
      * This method initializes jListConflict	
      * 	
      * @return javax.swing.JList	
      */
     public JList getJListConflict() {
          if (jListConflict == null) {
               jListConflict = new JList();
               jListConflict.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
               jListConflict.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                         if (jListConflict.getSelectedIndex() >= 0) {
                              controller.conflictSelected(jListConflict.getSelectedValue());
                         }                         
                    }
               });
          }
          return jListConflict;
     }

     /**
      * This method initializes jRadioButtonBranch	
      * 	
      * @return javax.swing.JRadioButton	
      */
     public JRadioButton getJRadioButtonBranchM1() {
          if (jRadioButtonBranchM1 == null) {
               jRadioButtonBranchM1 = new JRadioButton();
               jRadioButtonBranchM1.setText("Version 1");
               jRadioButtonBranchM1.addItemListener(new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent e) {
                         controller.updateSelectedBranch();
                    }
               });
          }
          return jRadioButtonBranchM1;
     }

     /**
      * This method initializes jRadioButtonBranchM2	
      * 	
      * @return javax.swing.JRadioButton	
      */
     public JRadioButton getJRadioButtonBranchM2() {
          if (jRadioButtonBranchM2 == null) {
               jRadioButtonBranchM2 = new JRadioButton();
               jRadioButtonBranchM2.setText("Version 2");
               jRadioButtonBranchM2.addItemListener(new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent e) {
                         controller.updateSelectedBranch();
                    }
               });
          }
          return jRadioButtonBranchM2;
     }

     /**
      * This method initializes jButtonConfirmar (ApplyChoicesBtn)
      * 	
      * @return javax.swing.JButton	
      */
     private void setJButtonConfirmar(MainInterface mainInterface) {
        jButtonConfirmar = MainInterfaceHandler.getMainInterface().getApplyChoicesBtn();
        JMenuItem miApplyChoices = MainInterfaceHandler.getMainInterface().getMiApplyChoices();
        final MainInterface mInterface = mainInterface;
          
        jButtonConfirmar.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                mInterface.selectTabbedPaneMerged(1);
                MainInterfaceHandler.getMainInterface().getWriteMergedBtn().setEnabled(true);
                MainInterfaceHandler.getMainInterface().getMiWriteMerged().setEnabled(true);
                mInterface.updateResults(controller.conflitosResolvidos());
            }
        });
        
        miApplyChoices.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 mInterface.selectTabbedPaneMerged(1);
                 MainInterfaceHandler.getMainInterface().getWriteMergedBtn().setEnabled(true);
                 MainInterfaceHandler.getMainInterface().getMiWriteMerged().setEnabled(true);
                 mInterface.updateResults(controller.conflitosResolvidos());
             }
         });
     }

     /**
      * This method initializes jButtonCancel	
      * 	
      * @return javax.swing.JButton	
      */
    private void setJButtonCancel() {
        jButtonCancel = MainInterfaceHandler.getMainInterface().getCancelBtn();
        JMenuItem miMergeCancel = MainInterfaceHandler.getMainInterface().getMiMergeCancel();
          
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controller.getFrame().setVisible(false);
            }
        });
               
        miMergeCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.getFrame().setVisible(false);
            }
        });
     }

     public JLabel getJLabelTipoConflito() {
          return jLabelTipoConflito;
     }

     /**
      * This method initializes jScrollPaneBase	
      * 	
      * @return javax.swing.JScrollPane	
      */
     private JScrollPane getJScrollPaneBase() {
          if (jScrollPaneBase == null) {
               jScrollPaneBase = new JScrollPane();
               jScrollPaneBase.setViewportView(getJTreeBase());
          }
          return jScrollPaneBase;
     }

     /**
      * This method initializes jTreeBase	
      * 	
      * @return javax.swing.JTree	
      */
     public JTree getJTreeBase() {
          if (jTreeBase == null) {
               jTreeBase = new JTree();
          }
          return jTreeBase;
     }

     public MultilineLabel getJLabelDescription() {
          return jLabelDescription;
     }

     /**
      * This method initializes jRadioButtonBoth	
      * 	
      * @return javax.swing.JRadioButton	
      */
     public JRadioButton getJRadioButtonBranchBoth() {
          if (jRadioButtonBranchBoth == null) {
               jRadioButtonBranchBoth = new JRadioButton();
               jRadioButtonBranchBoth.setText("Both");
               jRadioButtonBranchBoth.setVisible(false);
          }
          return jRadioButtonBranchBoth;
     }

     public JLabel getJLabelChosen() {
          return jLabelChosen;
     }

     public JLabel getJLabelTipo() {
          return jLabelTipo;
     }

    public MultilineLabel getjLabelUseVersion1() {
        return jLabelUseVersion1;
    }

    public MultilineLabel getjLabelUseVersion2() {
        return jLabelUseVersion2;
    }

    public MultilineLabel getjLabelUseBoth() {
        return jLabelUseBoth;
    }

    public void setjLabelUseVersion1(MultilineLabel jLabelUseVersion1) {
        this.jLabelUseVersion1 = jLabelUseVersion1;
    }

    public void setjLabelUseVersion2(MultilineLabel jLabelUseVersion2) {
        this.jLabelUseVersion2 = jLabelUseVersion2;
    }

    public void setjLabelUseBoth(MultilineLabel jLabelUseBoth) {
        this.jLabelUseBoth = jLabelUseBoth;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"

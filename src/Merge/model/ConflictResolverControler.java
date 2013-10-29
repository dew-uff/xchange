package Merge.model;

import GUI.MainInterface.MainInterface;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import Merge.MergeShow;
import Merge.StringUtil;
import Merge.model.loghandling.NodeConstants;
import Merge.model.vos.Conflict;
import Merge.model.vos.ConflictNode;
import Merge.view.IconNodeRenderer;
import Merge.view.frames.ConflictResolverFrame;

import Merge.model.vos.Node;

public class ConflictResolverControler {

     private List<Conflict> conflicts;
     
     private DefaultMutableTreeNode m1;
     
     private DefaultMutableTreeNode m2;
     
     private DefaultMutableTreeNode base;
     
     private DefaultMutableTreeNode merged;

     private ConflictResolverFrame frame;
     
     String statusV1, statusV2;

     public void setConflicts(List<Conflict> conflicts) {
          this.conflicts = conflicts;
     }
     
     public JPanel handleConflicts(MainInterface mainInterface) {
          frame = new ConflictResolverFrame(this, mainInterface);
          DefaultListModel listModel = new DefaultListModel();
          int countWarning = 0;
          int countConflict = 0;
          
          
          for (Iterator it = conflicts.iterator(); it.hasNext();) {
              Conflict conflict = (Conflict) it.next();
              if(conflict.getConflictWarning()== 0){
                  countConflict ++;
                  conflict.setName(countConflict+ ": " + conflict.getType());                  
              } else{
                  countWarning ++;
                  conflict.setName(countWarning+ ": " + conflict.getType()); 
              }
              listModel.addElement(conflict);
          }
          frame.getJListConflict().setModel(listModel);
          
          JTree treeM1 = frame.getJTreeM1();
          TreeModel treeModel1 = new DefaultTreeModel(m1);
          treeM1.setCellRenderer(new IconNodeRenderer());
          treeM1.setModel(treeModel1);
          
               
          
          
          JTree treeM2 = frame.getJTreeM2();
          TreeModel treeModel2 = new DefaultTreeModel(m2);
          treeM2.setCellRenderer(new IconNodeRenderer());
          treeM2.setModel(treeModel2);
          
                  
          JTree treeBase = frame.getJTreeBase();
          TreeModel treeModelBase = new DefaultTreeModel(base);
          treeBase.setCellRenderer(new IconNodeRenderer());
          treeBase.setModel(treeModelBase);
          
          frame.setVisible(true);
          return frame;
     }

     public void setM1(DefaultMutableTreeNode m1) {
          this.m1 = m1;
     }

     public void setM2(DefaultMutableTreeNode m2) {
          this.m2 = m2;
     }

     public void conflictSelected(Object selectedValue) {
          if (!(selectedValue instanceof Conflict)) {
               throw new IllegalArgumentException("Não é um conflito");
          }          
          Conflict conflict = (Conflict) selectedValue;         

          frame.getJLabelTipo().setText( conflict.getConflictWarning() == Conflict.WARNING ? "Warning:" : "Conflict:" );
          
          frame.getJLabelTipoConflito().setText(conflict.getType());
          String description = StringUtil.wrap(conflict.getDescription(), "\n", 22);
          
          
          boolean showM1 = conflict.getConflictWarning() == Conflict.CONFLICT || conflict.getType().equals(Conflict.WARNING_DELETE_UPDATE);
          boolean showM2 = showM1;
          
          boolean showBoth = conflict.getType().equals(Conflict.CONFLICT_FARMOVE_FARMOVE);
          
          frame.getJLabelChosen().setVisible(showM1 || showM2 || showBoth);

          frame.getJRadioButtonBranchM1().setVisible(showM1);
          frame.getJRadioButtonBranchM2().setVisible(showM2);
          frame.getJRadioButtonBranchBoth().setVisible(showBoth);
          
          
          expandConflictingNode(conflict, NodeConstants.BRANCH_B, frame.getJTreeBase());
          expandConflictingNode(conflict, NodeConstants.BRANCH_M1, frame.getJTreeM1());
          expandConflictingNode(conflict, NodeConstants.BRANCH_M2, frame.getJTreeM2());
          
          JTree treeM1 = frame.getJTreeM1();
          TreePath path1 = treeM1.getSelectionPath();   
          if (path1 != null) {
               DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path1.getLastPathComponent();
               Object userObject = treeNode.getUserObject();
               if (userObject instanceof Node) {
                    Node node = (Node) userObject;
                    statusV1 = null;
                    statusV1 = node.getStatusText();
                    conflict.setStatusno1(statusV1);
                    }
          }
          
          JTree treeM2 = frame.getJTreeM2();
          TreePath path2 = treeM2.getSelectionPath();          
          if (path2 != null) {
               DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path2.getLastPathComponent();
               Object userObject = treeNode.getUserObject();
               if (userObject instanceof Node) {
                    Node node = (Node) userObject;
                    statusV2 = null;
                    statusV2 = node.getStatusText();
                    conflict.setStatusno2(statusV2);
                    }
          }
          
          frame.getJLabelDescription().setText(description);
          frame.getjLabelUseBoth().setText(getTextVersions(0, conflict));
          frame.getjLabelUseVersion1().setText(getTextVersions(1, conflict));
          frame.getjLabelUseVersion2().setText(getTextVersions(2, conflict));
          
          frame.getjLabelUseVersion1().setVisible(showM1);
          frame.getjLabelUseVersion2().setVisible(showM2);
          frame.getjLabelUseBoth().setVisible(showBoth);
          
          if (conflict.getSelectedBranch() == NodeConstants.BRANCH_M1) {
               frame.getJRadioButtonBranchM1().setSelected(showM2);
          }
          if (conflict.getSelectedBranch() == NodeConstants.BRANCH_M2) {
               frame.getJRadioButtonBranchM2().setSelected(showM2);
          }
          if (conflict.getSelectedBranch() == NodeConstants.BRANCH_MERGED) {
               frame.getJRadioButtonBranchBoth().setSelected(showM2);
          }
   
          
                   
     }

     private void expandConflictingNode(Conflict conflict, int branch, JTree tree) {
          ConflictNode conflictNode = conflict.getConflictNode(branch);
          if (conflictNode != null) {
               String pathBase = conflictNode.getPath();
               expandOnPath(branch, tree, pathBase);
          }else{
            tree.setSelectionPath(null);
            tree.repaint();
          }
     }

     private void expandOnPath(int branch, JTree treeM, String pathText) {
          DefaultTreeModel model = (DefaultTreeModel) treeM.getModel();
          DefaultMutableTreeNode rootM = (DefaultMutableTreeNode) model.getRoot();
          
          DefaultMutableTreeNode foundNode = XMLEditHandler.getInstance().findNode(rootM, pathText, false, branch);
          TreeNode[] pathNode = foundNode.getPath();
          TreePath path = new TreePath(pathNode);
          treeM.setSelectionPath(path);
          treeM.repaint();
     }

     public void setBase(DefaultMutableTreeNode base) {
          this.base = base;
     }

     public void updateSelectedBranch() {
          Conflict conflict = (Conflict) frame.getJListConflict().getSelectedValue();
          if (conflict == null) {
               return;
          }
          if (frame.getJRadioButtonBranchM1().isSelected()) {
               conflict.setSelectedBranch(NodeConstants.BRANCH_M1);
          }
          if (frame.getJRadioButtonBranchM2().isSelected()) {
               conflict.setSelectedBranch(NodeConstants.BRANCH_M2);
          }
          if (frame.getJRadioButtonBranchBoth().isSelected()) {
               conflict.setSelectedBranch(NodeConstants.BRANCH_MERGED);
          }
          
     }

     public JPanel conflitosResolvidos() {
          for (Iterator it = conflicts.iterator(); it.hasNext();) {
               Conflict conflict = (Conflict) it.next();
               if (conflict.getDefaultBranch() != conflict.getSelectedBranch()) {
                    applyChanges(conflict);
               }
          }
         
          return MergeShow.showCompareTree(merged);
          
     }

     private void applyChanges(Conflict conflict) {
          XMLEditHandler editHandler = XMLEditHandler.getInstance();
          if (conflict.getType() == Conflict.CONFLICT_UPDATE_UPDATE) {
               ConflictNode conflictNodeBase = conflict.getConflictNode(NodeConstants.BRANCH_MERGED);
               ConflictNode conflictNodeM2 = conflict.getConflictNode(NodeConstants.BRANCH_M2);
               editHandler.applyUpdate(merged, m2, conflictNodeBase.getPath(), NodeConstants.BRANCH_M2, conflictNodeM2.getPath());
          } else if (conflict.getType() == Conflict.CONFLICT_FARMOVE_NEARMOVE || conflict.getType() == Conflict.CONFLICT_NEARMOVE_NEARMOVE) {
               
               ConflictNode conflictNodeBase = conflict.getConflictNode(NodeConstants.BRANCH_MERGED);
               ConflictNode conflictNodeToDelete = conflict.getConflictNode(conflict.getDefaultBranch());
               ConflictNode conflictNodeToAdd = conflict.getConflictNode(conflict.getSelectedBranch());
               ConflictNode conflictNodeIgnorado = conflict.getConflictNode(conflict.getSelectedBranch());
               
               editHandler.applyManualMove(merged, conflict.getSelectedBranch() == NodeConstants.BRANCH_M1 ? m1 : m2, conflictNodeBase.getPath(), 
                         conflict.getSelectedBranch() == NodeConstants.BRANCH_M1 ? NodeConstants.BRANCH_M1 : NodeConstants.BRANCH_M2,
                                   conflictNodeToAdd.getPath(), conflictNodeToDelete.getPath(), conflictNodeIgnorado.getPath());
          } else if (conflict.getType() == Conflict.CONFLICT_FARMOVE_FARMOVE || conflict.getType() == Conflict.CONFLICT_FARMOVE_DELETE){
               int branchToDelete = conflict.getSelectedBranch() == NodeConstants.BRANCH_M1 ? NodeConstants.BRANCH_M2 : NodeConstants.BRANCH_M1; 
               ConflictNode conflictNodeToDelete = conflict.getConflictNode(branchToDelete);
               editHandler.applyManualDelete(merged, conflictNodeToDelete.getPath());
          } else if (conflict.getType() == Conflict.CONFLICT_LOCKED_DELETE
                    || conflict.getType() == Conflict.WARNING_DELETE_UPDATE 
                    ) {
               ConflictNode conflictNodeToAdd = conflict.getConflictNode(conflict.getSelectedBranch());
               ConflictNode conflictNodeParent = conflict.getConflictNode(NodeConstants.BRANCH_MERGED);
               editHandler.applyManualUndelete(merged, conflictNodeToAdd.getPath(), conflictNodeParent.getPath(), conflict.getSelectedBranch() == NodeConstants.BRANCH_M1 ? m1 : m2, conflict.getSelectedBranch());
          } else if (conflict.getType() == Conflict.CONFLICT_NEARMOVE_DELETE){                
             ConflictNode conflictNodeToAdd = conflict.getConflictNode(conflict.getSelectedBranch());
             ConflictNode conflictNodeParent = conflict.getConflictNode(NodeConstants.BRANCH_MERGED);
             editHandler.applyManualUndeleteNearMoveDelete(merged, conflictNodeToAdd.getPath(), conflictNodeParent.getPath(), conflict.getSelectedBranch() == NodeConstants.BRANCH_M1 ? m1 : m2, conflict.getSelectedBranch());
          }
          else {
               throw new IllegalStateException("Escolha de resolu��o de conflito n�o tratada!");
          }
     }

     public void setMerged(DefaultMutableTreeNode merged) {
          this.merged = merged;
     }

    public ConflictResolverFrame getFrame() {
            return frame;
    }
    
    public String getTextVersions(int version, Conflict confl){
               
        if (confl.getType().equals("Update/Update")){
           switch (version){   
               case 0:
                    return "";
            case 1:
                return "Persist changes from version 1";                
            case 2:
                return "Persist changes from version 2";                
            }            
        }else
        if (confl.getType().equals("Locked/Delete")){
            switch (version){ 
                case 0:
                    return "";
                case 1:
                    if (confl.getStatusno1() == null || confl.getStatusno1().equals("Deleted")) return "Delete the node";
                    else return "Maintain the node";
                case 2:
                    if (confl.getStatusno2() == null || confl.getStatusno2().equals("Deleted")) return "Delete the node";
                    else return "Maintain the node";                
            }
        }else
        if (confl.getType().equals("Far Move/Delete")){
            switch (version){
                case 0:
                    return "";
                case 1:
                    if (confl.getStatusno1() == null || confl.getStatusno1().equals("Deleted")) return "Delete the node";
                    else return "Move the node";
                case 2:
                    if (confl.getStatusno2() == null || confl.getStatusno2().equals("Deleted")) return "Delete the node";
                    else return "Move the node";                
            }
        }else
        if (confl.getType().equals("Far Move/Near Move")){
            switch (version){  
                case 0:
                    return "";
                case 1:
                    return "Move as version 1";
                case 2:
                    return "Move as version 2";                
            }
        }else
        if (confl.getType().equals("Near Move/Near Move")){
            switch (version){
                case 0:
                    return "";
                case 1:
                    return "Move as version 1";
                case 2:
                    return "Move as version 2";             
            }
        }else
        if (confl.getType().equals("Far Move/Far Move")){
            switch (version){
                case 0:
                    return "Move as version 1 and 2 (Duplicate the node)";
                case 1:
                    return "Move as version 1";
                case 2:
                    return "Move as version 2";                
            }
        }
        if (confl.getType().equals("Delete/Update")){
            switch (version){
                case 0:
                    return "";
               case 1:
                    if (confl.getStatusno1() == null || confl.getStatusno1().equals("Deleted")) return "Delete the node";
                    else return "Update the node";
                case 2:
                    if (confl.getStatusno2() == null || confl.getStatusno2().equals("Deleted")) return "Delete the node";
                    else return "Update the node";                
            }
        }
       return "";
    }
     
     
}

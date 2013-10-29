package Merge.model.loghandling;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import Merge.model.vos.Conflict;
import Merge.model.vos.ConflictNode;

public class ConflictLogHandler extends LogHandler {

     private List<Conflict> conflicts = new ArrayList<Conflict>();

     public ConflictLogHandler(String path, DefaultMutableTreeNode tB, DefaultMutableTreeNode tM1, DefaultMutableTreeNode tM2) {
          super();
          this.path = path;
          this.tB = tB;
          this.tM1 = tM1;
          this.tM2 = tM2;
     }

     public void parseFile() throws JDOMException, IOException {
          Document d = new SAXBuilder().build(new File(path));
          outputDocument(d);
          Element conflictlist = d.getRootElement();
          List lists = conflictlist.getChildren();
          for (Iterator it = lists.iterator(); it.hasNext();) {
               Element list = (Element) it.next();
               if (list.getName().equals("conflicts"))
                    parseConflicts(list);
               if (list.getName().equals("warnings"))
                    parseWarnings(list);
          }

     }

     
     public void parseWarnings(Element warningsElement) {
          List children = warningsElement.getChildren();
          for (Iterator itConfl = children.iterator(); itConfl.hasNext();) {
               Element warningName = (Element) itConfl.next();
               Conflict conflict = new Conflict();
               String text = warningName.getText();
               conflict.setDescription(text);
               conflict.setType(getWarningType(warningName.getName(), text));
               List nodes = warningName.getChildren();
               for (Iterator it = nodes.iterator(); it.hasNext();) {

                    Element node = (Element) it.next();

                    String path = node.getAttributeValue("path");
                    String tree = node.getAttributeValue("tree");

                    int branch = getBranchId(tree);

                    ConflictNode conflictNode = new ConflictNode(branch, path);
                    conflict.setConflictNode(branch, conflictNode);
               }
               setWarningBranchUsed(conflict, warningName.getName(), text);
               conflict.setSelectedBranch(conflict.getDefaultBranch());
               conflict.setConflictWarning(Conflict.WARNING);
               if(nodes.size()>1) conflicts.add(conflict);
          }
     }     
     
     public void parseConflicts(Element conflictsElement) {
          List children = conflictsElement.getChildren();
          for (Iterator itConfl = children.iterator(); itConfl.hasNext();) {
               Element conflictElement = (Element) itConfl.next();
               Conflict conflict = new Conflict();
               String text = conflictElement.getText();
               conflict.setDescription(text);
               conflict.setType(getConflictType(conflictElement.getName(), text));
               List nodes = conflictElement.getChildren();
               for (Iterator it = nodes.iterator(); it.hasNext();) {

                    Element node = (Element) it.next();

                    String path = node.getAttributeValue("path");
                    String tree = node.getAttributeValue("tree");

                    int branch = getBranchId(tree);

                    ConflictNode conflictNode = new ConflictNode(branch, path);
                    conflict.setConflictNode(branch, conflictNode);
               }
               setConflictBranchUsed(conflict, conflictElement.getName(), text);
               conflict.setSelectedBranch(conflict.getDefaultBranch());
               conflict.setConflictWarning(Conflict.CONFLICT);
                if(nodes.size()>1) conflicts.add(conflict);
          }
     }

     public List<Conflict> getConflicts() {
          return conflicts;
     }

     private static String getConflictType(String tagName, String text) {
          if (tagName.equals("update")) {
               return Conflict.CONFLICT_UPDATE_UPDATE;
          } else if (tagName.equals("delete")) {
               return Conflict.CONFLICT_LOCKED_DELETE;
          } else if (tagName.equals("move")) {
               if (text.indexOf("The node was moved and deleted") >= 0)
                    return Conflict.CONFLICT_FARMOVE_DELETE;
               if (text.indexOf("ignoring move inside childlist") >= 0)
                    return Conflict.CONFLICT_FARMOVE_NEARMOVE;
               if (text.indexOf("Conflicting moves inside child list") >= 0)
                    return Conflict.CONFLICT_NEARMOVE_NEARMOVE;
               if (text.indexOf("different locations. It will appear at each location.") >=0) {
                    return Conflict.CONFLICT_FARMOVE_FARMOVE;
               }
                if (text.indexOf("Node moved and deleted - trying to recover by deleting the node") >=0){
                   return Conflict.CONFLICT_NEARMOVE_DELETE;
                   //Node moved and deleted - trying to recover by deleting the node (copies and inserts immediately following the node may also have been deleted)

               }
          }
          //throw new IllegalStateException("Conflito desconhecido");
          return "";
     }
     
     
     private static String getWarningType(String tagName, String text) {
          if (tagName.equals("update")) {
               return Conflict.WARNING_EQUAL_UPDATES;
          } else if (tagName.equals("insert")) {
               if (text.indexOf("Equal insertions/copies in both") >= 0)
                    return Conflict.WARNING_EQUAL_INSERTS;
               if (text.indexOf("Insertions/copies in both") >= 0)
                    return Conflict.WARNING_INSERTS;
          } else if (tagName.equals("delete")) {
               return Conflict.WARNING_DELETE_UPDATE;
          }
          throw new IllegalStateException("Warning desconhecido");
     }     
     

     private static void setWarningBranchUsed(Conflict warning,
               String tagName, String text) {
          if (warning.getType().equals(Conflict.WARNING_EQUAL_UPDATES)
               || warning.getType().equals(Conflict.WARNING_EQUAL_INSERTS)
               || warning.getType().equals(Conflict.WARNING_INSERTS)) {
               warning.setDefaultBranch(NodeConstants.BRANCH_MERGED);
          } else if (warning.getType().equals(Conflict.WARNING_DELETE_UPDATE)) {
               setBranchForUnavailableConflictNode(warning);
          } else {
               throw new IllegalStateException("Warning desconhecido");
          }    
     }
     
     private static void setConflictBranchUsed(Conflict conflict,
               String tagName, String text) {
          if (conflict.getType().equals(Conflict.CONFLICT_UPDATE_UPDATE)) {
               conflict.setDefaultBranch(NodeConstants.BRANCH_M1);
          } else if (conflict.getType().equals(Conflict.CONFLICT_FARMOVE_DELETE)){
               setBranchForAvailableConflictNode(conflict);
          } else if (conflict.getType().equals(Conflict.CONFLICT_LOCKED_DELETE) 
                  || conflict.getType().equals(Conflict.CONFLICT_NEARMOVE_DELETE)) {
               setBranchForUnavailableConflictNode(conflict);
          } else if (conflict.getType().equals(Conflict.CONFLICT_FARMOVE_NEARMOVE)
                      || conflict.getType().equals(Conflict.CONFLICT_NEARMOVE_NEARMOVE)
                      || conflict.getType().equals(Conflict.CONFLICT_FARMOVE_FARMOVE)) {
               ConflictNode m1 = conflict.getConflictNode(NodeConstants.BRANCH_M1);
               String mPath = m1.getPath();
               ConflictNode base = conflict.getConflictNode(NodeConstants.BRANCH_B);
               String bPath = base.getPath();
               String m1PathParent = mPath.substring(0, mPath.lastIndexOf("/"));
               String bPathParent = bPath.substring(0, bPath.lastIndexOf("/"));
               if (conflict.getType().equals(Conflict.CONFLICT_NEARMOVE_NEARMOVE)) {
                    conflict.setDefaultBranch(NodeConstants.BRANCH_M1);
               } else if (conflict.getType().equals(Conflict.CONFLICT_FARMOVE_FARMOVE)) {
                    conflict.setDefaultBranch(NodeConstants.BRANCH_MERGED);
               } else if (m1PathParent.equals(bPathParent)) { //FARMOVE_NEARMOVE
                    conflict.setDefaultBranch(NodeConstants.BRANCH_M2);
               } else {
                    conflict.setDefaultBranch(NodeConstants.BRANCH_M1);
               }
               
          } else {
               return;
               //throw new IllegalStateException("Conflito desconhecido");
          }
     }

     private static void setBranchForAvailableConflictNode(Conflict conflict) {
          if (conflict.getConflictNode(NodeConstants.BRANCH_M1) != null) {
               conflict.setDefaultBranch(NodeConstants.BRANCH_M1);
          } else if (conflict.getConflictNode(NodeConstants.BRANCH_M2) != null) {
               conflict.setDefaultBranch(NodeConstants.BRANCH_M2);
          } else {
               throw new IllegalStateException("Delete conflict: n�o foi possivel determinar branch padrao");
          }
     }

     private static void setBranchForUnavailableConflictNode(Conflict conflict) {
          if (conflict.getConflictNode(NodeConstants.BRANCH_M1) != null) {
               conflict.setDefaultBranch(NodeConstants.BRANCH_M2);
          } else if (conflict.getConflictNode(NodeConstants.BRANCH_M2) != null) {
               conflict.setDefaultBranch(NodeConstants.BRANCH_M1);
          } else {
               throw new IllegalStateException("Delete conflict: n�o foi possivel determinar branch padrao");
          }
     }

     
}

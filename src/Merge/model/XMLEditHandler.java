package Merge.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import Merge.model.loghandling.NodeConstants;
import Merge.model.vos.Node;

public class XMLEditHandler {

     private XMLEditHandler() {

     }

     private static XMLEditHandler instance;

     public static XMLEditHandler getInstance() {
          if (instance == null) {
               instance = new XMLEditHandler();
          }
          return instance;
     }

     public void markDeletion(TreeNode treeResultado, String src,
               TreeNode treeModificada, String originList, int originTree,
               TreeNode treeBase) {

          // DefaultMutableTreeNode deletedNode = findNode(treeBase, srcPath, false, NodeConstants.BRANCH_B);

          // TODO: deletar recursivamente mesmo?
          // recursiveApplyStatus(originTree, deletedNode, NodeConstants.STATUS_DELETED);

          DefaultMutableTreeNode deletedNode = findNode(treeBase, src, false, NodeConstants.BRANCH_B);

          // String parentPathInto = src.substring(0, src.lastIndexOf('/'));
          String pathInParent = src.substring(src.lastIndexOf('/') + 1);
          int posInParent = Integer.parseInt(pathInParent);

          DefaultMutableTreeNode parentNode = findNode(treeResultado, originList, true, NodeConstants.BRANCH_MERGED);

          recursiveCopy(deletedNode, parentNode, posInParent, NodeConstants.STATUS_DELETED, originTree);

     }

     public void markUpdate(TreeNode tree, String srcPathIn, int originTree,
               String srcPathFrom) {
          DefaultMutableTreeNode updatedNode = findNode(tree, srcPathIn, true, NodeConstants.BRANCH_MERGED);

          recursiveApplyStatus(originTree, updatedNode, NodeConstants.STATUS_UPDATED);

     }

     public void applyUpdate(TreeNode treeBase, TreeNode treeM,
               String srcPathIn, int originTree, String srcPathFrom) {
          DefaultMutableTreeNode updatedNode = findNode(treeM, srcPathFrom, false, originTree);

          String parentPathInto = srcPathIn.substring(0, srcPathIn.lastIndexOf('/'));
          String pathInParent = srcPathIn.substring(srcPathIn.lastIndexOf('/') + 1);
          int posInParent = Integer.parseInt(pathInParent);

          DefaultMutableTreeNode parentNode = findNode(treeBase, parentPathInto, true, NodeConstants.BRANCH_B);

          DefaultMutableTreeNode oldTreeNode = null;

          for (int j = 0; j < parentNode.getChildCount(); ++j) {
               DefaultMutableTreeNode prox = (DefaultMutableTreeNode) parentNode.getChildAt(j);
               Object userObject = prox.getUserObject();
               Node node = (Node) userObject;
               if ((node.getType() == NodeConstants.TYPE_TEXT || node.getType() == NodeConstants.TYPE_ELEMENT)
                         && node.getNodeOriginalPos() == posInParent) {
                    oldTreeNode = prox;
                    break;
               }
          }

          if (oldTreeNode == null) {
               throw new IllegalStateException("Path invalido para a arvore");
          }

          Object userObject = updatedNode.getUserObject();
          Node updatedOriginalNode = (Node) userObject;

          try {
               Node updatedClonedNode = (Node) updatedOriginalNode.clone();

               updatedClonedNode.setStatus(NodeConstants.STATUS_UPDATED);
               updatedClonedNode.setBranch(originTree);

               oldTreeNode.setUserObject(updatedClonedNode);

               // copy Attributes

               // first remove all attributes
               if (oldTreeNode.getChildCount() > 0) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) oldTreeNode.getFirstChild();
                    while (child != null) {
                         DefaultMutableTreeNode nextChild = child.getNextSibling();
                         Object userObjectChild = child.getUserObject();
                         Node nodeChild = (Node) userObjectChild;
                         if (nodeChild.getType() == NodeConstants.TYPE_ATTIBUTE) {
                              oldTreeNode.remove(child);
                         }
                         child = nextChild;
                    }
               }
               // now, copy the new ones
               if (updatedNode.getChildCount() > 0) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) updatedNode.getFirstChild();
                    while (child != null) {
                         DefaultMutableTreeNode nextChild = child.getNextSibling();
                         Object userObjectChild = child.getUserObject();
                         Node nodeChild = (Node) userObjectChild;
                         if (nodeChild.getType() == NodeConstants.TYPE_ATTIBUTE) {
                              DefaultMutableTreeNode atribute = new DefaultMutableTreeNode();
                              Node clonedAtribute = (Node) nodeChild.clone();
                              clonedAtribute.setBranch(originTree);
                              clonedAtribute.setStatus(NodeConstants.STATUS_UPDATED);
                              atribute.setUserObject(clonedAtribute);
                              oldTreeNode.insert(atribute, 0);
                         }
                         child = nextChild;
                    }
               }

          } catch (CloneNotSupportedException e) {

               e.printStackTrace();
          }

     }

     public void markInsertion(TreeNode tree, String srcPathInto,
               int originTree, String srcPathFrom) {

          DefaultMutableTreeNode insertedNode = findNode(tree, srcPathInto, true, NodeConstants.BRANCH_MERGED);

          recursiveApplyStatus(originTree, insertedNode, NodeConstants.STATUS_INSERTED);
     }

     public void applyManualMove(DefaultMutableTreeNode moved) {
          Node node = (Node) moved.getUserObject();
          node.setBranch(NodeConstants.BRANCH_USER);
          node.setStatus(NodeConstants.STATUS_MOVED_IN);
     }
     
     
     public void applyManualMove(TreeNode treeMerged, TreeNode treeM,
               String srcPathInto, int originTree, String srcPathFrom,
               String srcPathDeleted, String pathIgnorado) {

          DefaultMutableTreeNode movedNode = findNode(treeMerged, srcPathDeleted, true, NodeConstants.BRANCH_MERGED);
          // TODO: deletar recursivamente mesmo?
          recursiveApplyStatus(originTree, movedNode, NodeConstants.STATUS_MOVED_OUT);

          DefaultMutableTreeNode insertedNode = findNode(treeM, srcPathFrom, false, originTree);

          String parentPathInto = srcPathInto.substring(0, srcPathInto.lastIndexOf('/'));
          String pathInParent = pathIgnorado.substring(pathIgnorado.lastIndexOf('/') + 1);
          int posInParent = Integer.parseInt(pathInParent);

          DefaultMutableTreeNode parentNode = findNode(treeMerged, parentPathInto, false, NodeConstants.BRANCH_MERGED);

          recursiveCopy(insertedNode, parentNode, posInParent, NodeConstants.STATUS_MOVED_IN, originTree);
     }

     public void markCopy(TreeNode treeMerge, String srcPathInto,
               int originTree, String srcPathFrom, String srcPathCopyLocation) {
          DefaultMutableTreeNode copiedTreeNode = findNode(treeMerge, srcPathInto, true, NodeConstants.BRANCH_MERGED);

          recursiveApplyStatus(originTree, copiedTreeNode, NodeConstants.STATUS_COPIED);
     }

     public void markMove(TreeNode treeResult, TreeNode treeM,
               TreeNode treeBase, String src, int originTree,
               String originNode, String srcPathCopyLocation) {
          /*
           * DefaultMutableTreeNode movedNode = findNode(treeBase, srcPathBase, false, NodeConstants.BRANCH_B); // TODO: deletar
           * recursivamente mesmo? recursiveApplyStatus(originTree, movedNode, NodeConstants.STATUS_DELETED);
           * 
           * DefaultMutableTreeNode insertedNode = findNode(treeM, srcPathBranch, false, originTree);
           * 
           * String parentPathInto = srcPathCopyLocation.substring(0, srcPathCopyLocation.lastIndexOf('/')); String pathInParent =
           * srcPathCopyLocation.substring(srcPathCopyLocation.lastIndexOf('/') + 1); int posInParent = Integer.parseInt(pathInParent);
           * 
           * DefaultMutableTreeNode parentNode = findNode(treeBase, parentPathInto, false, NodeConstants.BRANCH_B);
           * 
           * recursiveCopy(insertedNode, parentNode, posInParent, NodeConstants.STATUS_INSERTED, originTree);
           */

          DefaultMutableTreeNode copiedTreeNode = findNode(treeResult, srcPathCopyLocation, true, NodeConstants.BRANCH_MERGED);

          recursiveApplyStatus(originTree, copiedTreeNode, NodeConstants.STATUS_MOVED_IN);

          DefaultMutableTreeNode movedNode = findNode(treeBase, src, false, NodeConstants.BRANCH_B);

          String parentPathInto = src.substring(0, src.lastIndexOf('/'));
          String pathInParent = src.substring(src.lastIndexOf('/') + 1);
          int posInParent = Integer.parseInt(pathInParent);

          DefaultMutableTreeNode parentNode = findNode(treeResult, parentPathInto, true, NodeConstants.BRANCH_MERGED);

          recursiveCopy(movedNode, parentNode, posInParent, NodeConstants.STATUS_MOVED_OUT, originTree);

     }

     public void applyManualDelete(TreeNode root, String path) {
          DefaultMutableTreeNode deletedNode = findNode(root, path, true, NodeConstants.BRANCH_MERGED);
          
          recursiveApplyStatus(NodeConstants.BRANCH_USER, (DefaultMutableTreeNode) deletedNode, NodeConstants.STATUS_DELETED);
     }
   
     public void applyManualDelete(TreeNode root) {
          recursiveApplyStatus(NodeConstants.BRANCH_USER, (DefaultMutableTreeNode) root, NodeConstants.STATUS_DELETED);
     }

     public void applyManualUndelete(TreeNode root) {
          recursiveApplyStatus(NodeConstants.BRANCH_USER, (DefaultMutableTreeNode) root, NodeConstants.STATUS_OK);
     }
     
     public void applyManualUndelete(TreeNode treeResultado, String srcBranch, String pathToParent,
                TreeNode treeBase, int branch) {

          // DefaultMutableTreeNode deletedNode = findNode(treeBase, srcPath, false, NodeConstants.BRANCH_B);

          // TODO: deletar recursivamente mesmo?
          // recursiveApplyStatus(originTree, deletedNode, NodeConstants.STATUS_DELETED);

          DefaultMutableTreeNode deletedNode = findNode(treeBase, srcBranch, false, branch);

          // String parentPathInto = src.substring(0, src.lastIndexOf('/'));
          String pathInParent = srcBranch.substring(srcBranch.lastIndexOf('/') + 1);
          int posInParent = Integer.parseInt(pathInParent);

          DefaultMutableTreeNode parentNode = findNode(treeResultado, pathToParent, true, NodeConstants.BRANCH_MERGED);
          // TODO: mark manual
          recursiveCopy(deletedNode, parentNode, posInParent, NodeConstants.STATUS_INSERTED, NodeConstants.BRANCH_USER);
     }
     
     public void applyManualUndeleteNearMoveDelete(TreeNode treeResultado, String srcBranch, String pathToParent,
                TreeNode treeBase, int branch) {

          // DefaultMutableTreeNode deletedNode = findNode(treeBase, srcPath, false, NodeConstants.BRANCH_B);

          // TODO: deletar recursivamente mesmo?
          // recursiveApplyStatus(originTree, deletedNode, NodeConstants.STATUS_DELETED);

          DefaultMutableTreeNode deletedNode = findNode(treeBase, srcBranch, true, branch);

          // String parentPathInto = src.substring(0, src.lastIndexOf('/'));
          String pathInParent = srcBranch.substring(srcBranch.lastIndexOf('/') + 1);
          int posInParent = Integer.parseInt(pathInParent);

          DefaultMutableTreeNode parentNode = findNode(treeResultado, pathToParent, true, NodeConstants.BRANCH_MERGED);
          // TODO: mark manual
          recursiveCopy(deletedNode, parentNode, posInParent, NodeConstants.STATUS_INSERTED, NodeConstants.BRANCH_USER);
     }

     private void recursiveApplyStatus(int originTree,
               DefaultMutableTreeNode deletedNode, int status) {
          Object userObject = deletedNode.getUserObject();
          Node node = (Node) userObject;
          node.setStatus(status);
          node.setBranch(originTree);

          if (deletedNode.getChildCount() > 0) {
               DefaultMutableTreeNode child = (DefaultMutableTreeNode) deletedNode.getFirstChild();
               while (child != null) {
                    recursiveApplyStatus(originTree, child, status);
                    child = child.getNextSibling();
               }
          }

     }

     private void recursiveCopy(DefaultMutableTreeNode copied,
               DefaultMutableTreeNode parentNode, int posInParent, int status,
               int branch) {
          DefaultMutableTreeNode copiedCloned = (DefaultMutableTreeNode) copied.clone();
          Object userObject = copied.getUserObject();
          Node node = (Node) userObject;
          try {
               Node nodeClone = (Node) node.clone();
               copiedCloned.setUserObject(nodeClone);
               nodeClone.setStatus(status);
               nodeClone.setBranch(branch);
          } catch (CloneNotSupportedException e) {
               e.printStackTrace();
          }

          copiedCloned.removeAllChildren();

          if (copied.getChildCount() > 0) {
               DefaultMutableTreeNode child = (DefaultMutableTreeNode) copied.getFirstChild();
               int i = 0;
               while (child != null) {
                    recursiveCopy(child, copiedCloned, i, status, branch);
                    child = child.getNextSibling();
                    i++;
               }
          }

          // consider that some child might be deleted

          if (parentNode.getChildCount() > 0) {
               DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getFirstChild();
               int countDown = posInParent + 1;
               while (child != null && countDown > 0) {
                    Node childNode = (Node) child.getUserObject();
                    if (/*!childNode.isRemoved() && */childNode.getType() != NodeConstants.TYPE_ATTIBUTE) {
                         countDown--;
                    } else {
                         posInParent++;
                    }
                    child = child.getNextSibling();
               }
          }
          if (parentNode.getChildCount() > posInParent)
               parentNode.insert(copiedCloned, posInParent);
          else 
               parentNode.add(copiedCloned);
     }

     public DefaultMutableTreeNode findNode(TreeNode treeBase, String srcPath,
               boolean considerNewOperations, int branch) {
          DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) treeBase;

          String[] pathCut = srcPath.split("/");
          // 0 is empty, and 1 is always 0 (the root node)
          for (int i = 2; i < pathCut.length; i++) {
               String nodeNumberText = pathCut[i];
               int nodeNumber = Integer.parseInt(nodeNumberText);

               boolean found = false;

               int deleted = 0;
               for (int j = 0; j < currentNode.getChildCount(); ++j) {
                    DefaultMutableTreeNode prox = (DefaultMutableTreeNode) currentNode.getChildAt(j);
                    Object userObject = prox.getUserObject();
                    Node node = (Node) userObject;
                    if (node.isRemoved()) {
                         deleted++;
                         continue;
                    }
                    if (((node.getType() == NodeConstants.TYPE_TEXT) || node.getType() == NodeConstants.TYPE_ELEMENT)) {
                         if ((considerNewOperations && (j - deleted) == nodeNumber)
                                   || (!considerNewOperations
                                             && node.getNodeOriginalPos() == nodeNumber && (node.getBranch() == branch || node.getStatus() == NodeConstants.STATUS_UPDATED))
                                   || (node.getType() == NodeConstants.TYPE_TEXT && currentNode.getChildCount() - deleted <= nodeNumber)) {
                              found = true;

                              currentNode = prox;
                              break;
                         }
                    } else {
                         deleted++;
                    }
               }
               if (!found) {
                   // throw new IllegalStateException("Path inv�lido para a �rvore");
               }
          }
          return currentNode;
     }

     public TreeNode applyManualAddNode(TreeNode contextElementNode, int type,
               DefaultTreeModel model) {
          Node newNode = new Node(NodeConstants.STATUS_INSERTED, "");
          newNode.setBranch(NodeConstants.BRANCH_USER);
          newNode.setStatus(NodeConstants.STATUS_INSERTED);
          newNode.setType(type);
          if (type == NodeConstants.TYPE_ATTIBUTE) {
               newNode.setLabel(" = ");
          }

          DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
          treeNode.setUserObject(newNode);

          DefaultMutableTreeNode parentTreeNode = (DefaultMutableTreeNode) contextElementNode;
          // parentTreeNode.add(treeNode);
          model.insertNodeInto(treeNode, parentTreeNode, parentTreeNode.getChildCount());
          return treeNode;
     }

}

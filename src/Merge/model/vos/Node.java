package Merge.model.vos;

import java.io.Serializable;

import Merge.model.loghandling.NodeConstants;

public class Node implements Cloneable, Serializable {
     private static final long serialVersionUID = 1L;

     private int status = NodeConstants.STATUS_OK;

     private int type;

     private String label;

     private int nodeOriginalPos = 0;

     private int childrenCount = 0;

     private int branch = NodeConstants.BRANCH_B;

     private Node referedNode;

     public Node getReferedNode() {
          return referedNode;
     }

     public void setReferedNode(Node referedNode) {
          this.referedNode = referedNode;
     }

     public int getBranch() {
          return branch;
     }

     public void setBranch(int branch) {
          this.branch = branch;
     }

     public void incChildren() {
          childrenCount++;
     }

     public int getChildrenCount() {
          return childrenCount;
     }

     public void setChildrenCount(int childrenCount) {
          this.childrenCount = childrenCount;
     }

     public Node(int status, String label) {
          super();
          this.status = status;
          this.label = label;
     }

     public String getLabel() {
          return label;
     }

     public void setLabel(String label) {
          this.label = label;
     }

     public int getStatus() {
          return status;
     }

     public void setStatus(int status) {
          this.status = status;
     }

     public String toString() {
          return label;
     }

     public String getStatusText() {
          switch (getStatus()) {
          case NodeConstants.STATUS_OK:
               return "No Changes";
          case NodeConstants.STATUS_DELETED:
               return "Deleted";
          case NodeConstants.STATUS_INSERTED:
               return "Inserted";
          case NodeConstants.STATUS_UPDATED:
               return "Modified";
          case NodeConstants.STATUS_COPIED:
               return "Copied";
          case NodeConstants.STATUS_MOVED_IN:
          case NodeConstants.STATUS_MOVED_OUT:
               return "Moved";

          }
          return null;
     }

     public String getBranchText() {
          switch (getBranch()) {
          case NodeConstants.BRANCH_B:
               return "Base";
          case NodeConstants.BRANCH_M1:
               return "1";
          case NodeConstants.BRANCH_M2:
               return "2";
          case NodeConstants.BRANCH_USER:
               return "User";
          }
          return null;
     }

     public boolean isRemoved() {
          return getStatus() == NodeConstants.STATUS_DELETED
                    || getStatus() == NodeConstants.STATUS_MOVED_OUT;
     }

     public int getNodeOriginalPos() {
          return nodeOriginalPos;
     }

     public void setNodeOriginalPos(int nodeOriginalPos) {
          this.nodeOriginalPos = nodeOriginalPos;
     }

     public int getType() {
          return type;
     }

     public void setType(int type) {
          this.type = type;
     }

     public String getAttibuteName() {
          if (type != NodeConstants.TYPE_ATTIBUTE) {
               throw new IllegalStateException("Tentando obter nome do atributo sem ser atributo!");
          }
          return label.substring(0, label.indexOf("=")-1);
     }

     public String getAttibuteValue() {
          if (type != NodeConstants.TYPE_ATTIBUTE) {
               throw new IllegalStateException("Tentando obter nome do atributo sem ser atributo!");
          }
          return label.substring(label.indexOf("=")+2);
     }
     
     
     public Object clone() throws CloneNotSupportedException {
          return super.clone();
     }

     public void setName(String name) {
          if (type == NodeConstants.TYPE_ATTIBUTE) {
               setLabel(name + " = " + getAttibuteValue());
          }
          else
               setLabel(name);
          
     }

     public void setValue(String value) {
          if (type != NodeConstants.TYPE_ATTIBUTE) {
               throw new IllegalStateException("Tentando obter nome do atributo sem ser atributo!");
          } 
          else
               setLabel(getAttibuteName() + " = " + value);
          
     }

}

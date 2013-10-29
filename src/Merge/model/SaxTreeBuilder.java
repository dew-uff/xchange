package Merge.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import Merge.model.loghandling.NodeConstants;
import Merge.model.vos.Node;



public class SaxTreeBuilder extends DefaultHandler {
     
     private DefaultMutableTreeNode currentNode = null;

     private DefaultMutableTreeNode previousNode = null;

     private DefaultMutableTreeNode rootNode = null;
     
     private int branch;

     public SaxTreeBuilder(int branch) {
          rootNode = null;
          this.branch = branch;
     }

     public void startDocument() {
          currentNode = rootNode;
     }

     public void endDocument() {
     }
     
      public void characters(char[] data, int start, int end) {
          String str = new String(data, start, end);
          str = str.trim();
          if (!str.equals("")/* && Character.isLetter(str.charAt(0))*/)
               currentNode.add(createTextNode(str));
     }


     public void startElement(String uri, String qName, String lName,
               Attributes atts) {

          previousNode = currentNode;
          
          int nodePos = 0;
          
          if (previousNode != null) {
               Object userObject = previousNode.getUserObject();
               if (userObject != null && userObject instanceof Node) {
                    Node node = (Node) userObject;
                    nodePos = node.getChildrenCount();
                    node.incChildren();
               }
          }          
          currentNode = createElementNode(lName, nodePos);
          
          // Add attributes as child nodes //
          attachAttributeList(currentNode, atts);
          if (rootNode == null) {
               rootNode = currentNode;
          } else {
               previousNode.add(currentNode);
          }
          
          
     }

     public void endElement(String uri, String qName, String lName) {
          Object userObject = currentNode.getUserObject();
          Node node = (Node) userObject;
          
          if (node.getLabel().equals(lName))
               currentNode = (DefaultMutableTreeNode) currentNode.getParent();
     }

     public DefaultMutableTreeNode getTree() {
          return rootNode;
     }

     private void attachAttributeList(DefaultMutableTreeNode treeNode,
               Attributes atts) {
          for (int i = 0; i < atts.getLength(); i++) {
               String name = atts.getLocalName(i);
               String value = atts.getValue(name);
               treeNode.add(createAtributeNode(name + " = " + value));
          }
     }
     
     public MutableTreeNode createTextNode(String label) {
          return createNode(label, NodeConstants.STATUS_OK, 0, NodeConstants.TYPE_TEXT);
     }

     public MutableTreeNode createAtributeNode(String label) {
          return createNode(label, NodeConstants.STATUS_OK, 0, NodeConstants.TYPE_ATTIBUTE);
     }

    
     public DefaultMutableTreeNode createElementNode(String label, int pos) {
          return createNode(label, NodeConstants.STATUS_OK, pos, NodeConstants.TYPE_ELEMENT);
     }

     public DefaultMutableTreeNode createNode(String label, int status, int pos, int type) {
          DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(label);
          Node node = new Node(status, label);
          node.setNodeOriginalPos(pos);
          node.setType(type);
          node.setBranch(branch);
          defaultMutableTreeNode.setUserObject(node);
          return defaultMutableTreeNode;
     }

}
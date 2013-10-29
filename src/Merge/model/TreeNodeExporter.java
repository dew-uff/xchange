package Merge.model;

import java.io.FileOutputStream;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import Merge.model.loghandling.NodeConstants;
import Merge.model.vos.Node;

public class TreeNodeExporter {

     private TreeNodeExporter() {

     }

     private static TreeNodeExporter instance;

     public static TreeNodeExporter getInstance() {
          if (instance == null) {
               instance = new TreeNodeExporter();
          }
          return instance;
     }

     public void printTreeNode(DefaultMutableTreeNode viewNode, String file) {
          Element rootElement = buildElement(viewNode);
          Document document = new Document(rootElement);
          try {
               XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
               outputter.output(document, System.out);
               outputter.output(document, new FileOutputStream(file));
          } catch (java.io.IOException e) {
               e.printStackTrace();
          }
     }

     public Element buildElement(DefaultMutableTreeNode viewNode) {
          Object userObject = viewNode.getUserObject();
          Node node = (Node) userObject;
          if (node.getType() != NodeConstants.TYPE_ELEMENT) {
               throw new IllegalArgumentException("Elemento esperado! ");
          }
          Element e = new Element(node.getLabel());

          if (viewNode.getChildCount() > 0) {
               DefaultMutableTreeNode child = (DefaultMutableTreeNode) viewNode.getFirstChild();

               while (child != null) {
                    Node childNode = (Node) child.getUserObject();
                    if (!childNode.isRemoved()) {
                         switch (childNode.getType()) {
                         case NodeConstants.TYPE_ELEMENT:
                              e.addContent(buildElement(child));
                              break;
                         case NodeConstants.TYPE_ATTIBUTE:
                              e.setAttribute(childNode.getAttibuteName(), childNode.getAttibuteValue());
                              break;
                         case NodeConstants.TYPE_TEXT:
                              e.addContent(childNode.getLabel());
                         }
                    }
                    child = child.getNextSibling();
               }

          }

          return e;
     }

}

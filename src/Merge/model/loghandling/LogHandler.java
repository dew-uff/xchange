package Merge.model.loghandling;

import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;


public abstract class LogHandler {

     protected String path;

     public abstract void parseFile() throws JDOMException, IOException;

     protected DefaultMutableTreeNode tB;
     protected DefaultMutableTreeNode tM1;
     protected DefaultMutableTreeNode tM2;
     protected DefaultMutableTreeNode tMerged;

     public LogHandler() {
          super();
     }

     protected DefaultMutableTreeNode getBranchTree(String originTree) {
          DefaultMutableTreeNode tree;
          if (originTree.equalsIgnoreCase("branch1")) {
               tree = tM1;
          } else if (originTree.equalsIgnoreCase("branch2")) {
               tree = tM2;
          } else {
               throw new IllegalArgumentException("branch inv�lido");
          }
          return tree;
     }

     protected int getBranchId(String originTree) {
          if (originTree.equalsIgnoreCase("branch1")) {
               return NodeConstants.BRANCH_M1;
          } else if (originTree.equalsIgnoreCase("branch2")) {
               return NodeConstants.BRANCH_M2;
          } else if (originTree.equalsIgnoreCase("merged")) {
               return NodeConstants.BRANCH_MERGED;
          } else if (originTree.equalsIgnoreCase("base")) {
               return NodeConstants.BRANCH_B;
          } else {
               throw new IllegalArgumentException("branch inválido");
          }
     }

     protected void outputDocument(Document d) throws IOException {
          XMLOutputter out = new XMLOutputter();
          out.output(d, System.out);
     }

}
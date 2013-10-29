package Merge.model.loghandling;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import Merge.model.XMLEditHandler;

public class EditLogHandler extends LogHandler {
     public EditLogHandler(String path, DefaultMutableTreeNode tB, DefaultMutableTreeNode tM1, DefaultMutableTreeNode tM2, DefaultMutableTreeNode tMerged) {
          super();
          this.path = path;
          this.tB = tB;
          this.tM1 = tM1;
          this.tM2 = tM2;
          this.tMerged = tMerged;
     }
     
     @Override
     public void parseFile() throws JDOMException, IOException {
          Document d = new SAXBuilder().build(new File(path));
          outputDocument(d);
          Element edits = d.getRootElement();
          List children = edits.getChildren();
          for (Iterator it = children.iterator(); it.hasNext();) {
               Element edit = (Element) it.next();
               String name = edit.getName();
               String path = edit.getAttributeValue("path");
               String src = edit.getAttributeValue("src");
               String originTree = edit.getAttributeValue("originTree");
               String originList = edit.getAttributeValue("originList");
               String originNode = edit.getAttributeValue("originNode");
               runOperation(name, path, src, originTree, originList, originNode);
          }
     }

     protected void runOperation(String operation, String path, String src, String originTree, String originList, String originNode) {
          if (operation.equalsIgnoreCase("delete")) {
               XMLEditHandler.getInstance().markDeletion(tMerged, src, tB, originList, getBranchId(originTree), tB);
          }
          else if (operation.equalsIgnoreCase("insert")) {
               XMLEditHandler.getInstance().markInsertion(tMerged, path, getBranchId(originTree), originNode);
          }
          else if (operation.equalsIgnoreCase("update")) {
               XMLEditHandler.getInstance().markUpdate(tMerged, path, getBranchId(originTree), originNode);
          }
          else if (operation.equalsIgnoreCase("copy")) {
               XMLEditHandler.getInstance().markCopy(tMerged, path, getBranchId(originTree), originNode, path);
          }
          else if (operation.equalsIgnoreCase("move")) {
               XMLEditHandler.getInstance().markMove(tMerged, getBranchTree(originTree), tB, src, getBranchId(originTree), originNode, path);
          }
     }
     
     
    
}

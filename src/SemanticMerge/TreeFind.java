/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SemanticMerge;

import Merge.model.vos.Node;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Classe que servirá para buscas na árvore gerada pelo DOM.
 *
 * @author Fernando Marques
 */
public class TreeFind {

    public static TreePath pathByKey(DefaultMutableTreeNode tree, String key) {
        Enumeration<DefaultMutableTreeNode> e = tree.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            if (node.toString().equalsIgnoreCase(key)) {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    public static List<SemanticNode> returnAttributes(DefaultMutableTreeNode tree, String key) {
        List<SemanticNode> result = new ArrayList<SemanticNode>();
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        Enumeration<DefaultMutableTreeNode> e = tree.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            node = e.nextElement();
            if (node.toString().equalsIgnoreCase( key )) {
                break;
            }
        }
        node = node.getPreviousNode();
        node = node.getPreviousNode();
        for (int i = 0; i < node.getChildCount(); i++) {
            SemanticNode sn = new SemanticNode();
            sn.setLabel(node.getChildAt(i).toString());
            TreePath treeAtribute = pathByKey(tree, sn.getLabel());
            String pathAtribute = returnPath(treeAtribute);
            pathAtribute = pathAtribute.substring(pathAtribute.lastIndexOf("/"));
            sn.setPath(pathAtribute);
            result.add(sn);
        }
        return result;
    }

    public static List<SemanticNode> returnVariablesInRule(String rule, List<SemanticNode> attributes, HashMap<String, List<SemanticNode>> previousRules) {

        List<SemanticNode> result = new ArrayList<SemanticNode>();
        String[] words;
        rule = rule.toLowerCase();
        rule = rule.replaceAll("[^a-zA-Z0-9  ]", " ");

        words = rule.split(" ");
        for (String word : words) {
            SemanticNode compare = new SemanticNode();
            compare.setLabel(word);
            if (attributes.contains(compare)) {
                for (SemanticNode index : attributes) {
                    if (index.getLabel().equalsIgnoreCase(word)) {
                        if (!result.contains(index)) {
                            result.add(index);
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static String returnPath(TreePath tree){
        StringBuilder path = new StringBuilder();
        path.append("0");
        DefaultMutableTreeNode previousNode = (DefaultMutableTreeNode) tree.getPathComponent(0);
        for (int i=1; i<tree.getPathCount() ; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getPathComponent(i);
            path.append("/").append(String.valueOf(previousNode.getIndex(node)));
            previousNode = node;
        }
        
        return path.toString();
            
    }
}

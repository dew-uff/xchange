package SemanticMerge;

import GUI.MainInterface.MainInterface;
import Manager.Manager;
import Manager.Results;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.xerces.parsers.SAXParser;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import Merge.model.ConflictResolverControler;
import Merge.model.SaxTreeBuilder;
import Merge.model.loghandling.ConflictLogHandler;
import Merge.model.loghandling.EditLogHandler;
import Merge.model.loghandling.LogHandler;
import Merge.model.loghandling.NodeConstants;
import Merge.model.vos.Conflict;
import Merge.model.vos.ConflictNode;
import Merge.view.IconNodeRenderer;
import Merge.view.frames.DiffFrame;
import Rules.RulesModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.tree.TreePath;

public class SemanticMergeShow {

    private static String merged;
    private JPanel conflictsJP, resultsJP, semanticConflictisJP;

    /**
     * @param args
     */
    public void merge(String fileBase, String fileM1, String fileM2, MainInterface mainInterface, Manager manager) {
        try {
            File tempDirectory = createTempDirectory("3dmdir");
            String tempPath = tempDirectory.getPath();

            String editLogPath = tempPath + "\\edit.xml";

            String conflictLogPath = tempPath + "\\conflictLog.xml";

            int exitValue = run3DM(fileBase, fileM1, fileM2, editLogPath, conflictLogPath);

            DefaultMutableTreeNode topBase;

            DefaultMutableTreeNode topM1;

            DefaultMutableTreeNode topM2;

            topBase = parseFile(fileBase, NodeConstants.BRANCH_B);
            topM1 = parseFile(fileM1, NodeConstants.BRANCH_M1);
            topM2 = parseFile(fileM2, NodeConstants.BRANCH_M2);

            DefaultMutableTreeNode topMerged = parseString(merged, NodeConstants.BRANCH_MERGED);

            LogHandler editHandler = new EditLogHandler(editLogPath, topBase, topM1, topM2, topMerged);
            editHandler.parseFile();
            SemanticRules semanticRules = new SemanticRules(manager.getRulesModule());

            ConflictLogHandler conflictHandler = new ConflictLogHandler(conflictLogPath, topBase, topM1, topM2);
            conflictHandler.parseFile();

            List<Conflict> semanticConflicts = new ArrayList<Conflict>();
            List<Conflict> conflicts = conflictHandler.getConflicts();
            
            List<SemanticNode> semanticNode = TreeFind.returnAttributes(topBase, ("\"" + "146-02-6844" + "\""));

            HashMap<String, List<SemanticNode>> rules = new HashMap<String, List<SemanticNode>>();
            // RulesModule inferenceModule = new RulesModule();
            //String[] partRules = inferenceModule.partRules(semanticRules.getSelectedRules().toString()); //Pega o cabeçalho das regras (ex: salary(NAME))
            // inferenceModule.getNameAndArgumentsRules(partRules);
            for (int i = 0; i < manager.getRulesModule().getRules().size(); i++) {
                for (String index : semanticRules.getSelectedRules()) {
                    String changedRule = index.substring(0, index.indexOf("("));
                    if (changedRule.equalsIgnoreCase(manager.getRulesModule().getRules().get(i).getName())) {
                        rules.put(manager.getRulesModule().getRules().get(i).getName(), (List<SemanticNode>) TreeFind.returnVariablesInRule(manager.getRulesModule().getRules().get(i).getRule(), semanticNode, rules));
                        break;
                    }
                }
            }
            semanticConflicts = separeMerge(rules, conflicts, manager, topBase, topM1, topM2);
            resultsJP = showCompareTree(topMerged);
            conflicts.removeAll(semanticConflicts);

            if (conflicts.size() > 0) {
                ConflictResolverControler conflictResolver = new ConflictResolverControler();
                conflictResolver.setConflicts(conflicts);
                conflictResolver.setM1(topM1);
                conflictResolver.setM2(topM2);
                conflictResolver.setBase(topBase);
                conflictResolver.setMerged(topMerged);
                conflictsJP = conflictResolver.handleConflicts(mainInterface);
            }
            if(semanticConflicts.size() > 0){
                ConflictResolverControler conflictResolver = new ConflictResolverControler();
                conflictResolver.setConflicts(semanticConflicts);
                conflictResolver.setM1(topM1);
                conflictResolver.setM2(topM2);
                conflictResolver.setBase(topBase);
                conflictResolver.setMerged(topMerged);
                semanticConflictisJP = conflictResolver.handleConflicts(mainInterface);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static List<Conflict> separeMerge(HashMap<String, List<SemanticNode>> rules, List<Conflict> conflicts, Manager manager, DefaultMutableTreeNode topBase, DefaultMutableTreeNode topM1, DefaultMutableTreeNode topM2) {
        List<SemanticChange> semanticsChange = new ArrayList<SemanticChange>();
        List<Conflict> semanticConflicts = new ArrayList<Conflict>();
        for (Results result : manager.getResultsInference()) {
            semanticsChange.addAll(returnSemanticChanges(result, rules, topBase, topM1, topM2));
        }

        for (Conflict conflict : conflicts) {
            for (int i = 0; i < 3; i++) {
                ConflictNode conflictNode = conflict.getConflictNode(i);
                if (conflictNode != null) {
                    for (SemanticChange index : semanticsChange) {
                        if (conflictNode.getPath().equals(index.getPath(i + 1)) && !semanticConflicts.contains(conflict)  ) {
                            conflict.setType(conflict.getType() + " " + index.getRule());
                            semanticConflicts.add(conflict);
                            
                            break;
                        }
                    }
                }
            }

        }
        return semanticConflicts;
    }

    private static List<SemanticChange> returnSemanticChanges(Results result, HashMap<String, List<SemanticNode>> rules, DefaultMutableTreeNode topBase, DefaultMutableTreeNode topM1, DefaultMutableTreeNode topM2) {
        List<SemanticChange> semanticChanges = new ArrayList<SemanticChange>();
        SemanticChange semanticChange = new SemanticChange();

        Object[] idChangesRules = result.getResultInference().toArray();
        String[] idRule;
        for (Object changeRule : idChangesRules) {
            idRule = changeRule.toString().split("'");
            String rule = idRule[0].substring(0, idRule[0].indexOf(":")).toLowerCase();
            if (rules.containsKey(rule)) {
                List<String> keys = new ArrayList<String>();
                for (int i = 1; i < idRule.length; i = i + 2) {
                    keys.add(idRule[i]);
                }
                for (String key : keys) {
                    DefaultMutableTreeNode d1 = new DefaultMutableTreeNode();
                    DefaultMutableTreeNode d2 = new DefaultMutableTreeNode();
                    switch (result.getDocument1()) {
                        case 1:
                            d1 = topBase;
                            break;
                        case 2:
                            d1 = topM1;
                            break;
                        case 3:
                            d1 = topM2;
                            break;
                        default:
                            break;
                    }
                    //Analise documento 1
                    TreePath pathTokey1 = TreeFind.pathByKey(d1, "\"" + key + "\"");
                    pathTokey1 = pathTokey1.getParentPath().getParentPath();
                    String semanticChangePath1 = TreeFind.returnPath(pathTokey1);

                    switch (result.getDocument2()) {
                        case 1:
                            d2 = topBase;
                            break;
                        case 2:
                            d2 = topM1;
                            break;
                        case 3:
                            d2 = topM2;
                            break;
                        default:
                            break;

                    }
                    //Analise Documento 2
                    TreePath pathTokey2 = TreeFind.pathByKey(d2, "\"" + key + "\"");
                    pathTokey2 = pathTokey2.getParentPath().getParentPath();
                    String semanticChangePath2 = TreeFind.returnPath(pathTokey2);

                    List<SemanticNode> pathOfRules = rules.get(rule);
                    for (SemanticNode semanticNode : pathOfRules) {
                        semanticChange.setDocument1(result.getDocument1());
                        semanticChange.setDocument2(result.getDocument2());
                        String path1Case = "/" + semanticChangePath1 + semanticNode.getPath() + "/0";
                        String path2Case = "/" + semanticChangePath2 + semanticNode.getPath() + "/0";
                        semanticChange.setPaths1(path1Case);
                        semanticChange.setPaths2(path2Case);
                        semanticChanges.add(semanticChange);
                        semanticChange.setRule(rule);
                        semanticChange = new SemanticChange();
                    }
                }
            }
        }
        return semanticChanges;
    }

    public static JPanel showCompareTree(DefaultMutableTreeNode topBase) {
        DiffFrame frame = new DiffFrame();
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTree tree = frame.getJTree();
        TreeModel treeModel = new DefaultTreeModel(topBase);
        tree.setCellRenderer(new IconNodeRenderer());
        tree.setModel(treeModel);
        frame.setVisible(true);
        return frame;
    }

    private static DefaultMutableTreeNode parseFile(String file, int branch)
            throws SAXException, IOException, FileNotFoundException {
        SaxTreeBuilder saxTreeBase = new SaxTreeBuilder(branch);
        SAXParser saxParser = new SAXParser();
        saxParser.setContentHandler(saxTreeBase);
        saxParser.parse(new InputSource(new FileInputStream(file)));
        return saxTreeBase.getTree();
    }

    private static DefaultMutableTreeNode parseString(String content, int branch)
            throws SAXException, IOException {
        SaxTreeBuilder saxTreeBase = new SaxTreeBuilder(branch);
        SAXParser saxParser = new SAXParser();
        saxParser.setContentHandler(saxTreeBase);
        saxParser.parse(new InputSource(new StringReader(content)));
        return saxTreeBase.getTree();
    }

    private static int run3DM(String pathBase, String pathM1, String pathM2,
            String pathEditLog, String pathConflictLog) throws IOException {

        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec("cmd /c run3dm.bat \"" + pathBase + "\" \""
                + pathM1 + "\" \"" + pathM2 + "\" \"" + pathEditLog
                + "\" \"" + pathConflictLog + "\" ");

        /*
         * InputStreamReader reader = // step 3 new InputStreamReader ( process.getInputStream () );
         * 
         * BufferedReader buf_reader = new BufferedReader ( reader ); // step 4. process.getOutputStream().close(); String line; while
         * ((line = buf_reader.readLine ()) != null) System.out.println (line);
         */
        // any error message?
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");

        // any output?
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");

        // kick them off
        errorGobbler.start();
        outputGobbler.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int exitValue = process.exitValue();

        merged = outputGobbler.getBuffer().toString();
        System.out.println(merged);
        String err = errorGobbler.getBuffer().toString();
        System.err.println(err);
        return exitValue;

    }

    public static String inputStreamAsString(InputStream stream)
            throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        return sb.toString();
    }

    public static File createTempDirectory(String prefix) throws IOException {
        File tempFile = File.createTempFile(prefix, "");
        if (!tempFile.delete()) {
            throw new IOException();
        }
        if (!tempFile.mkdir()) {
            throw new IOException();
        }
        return tempFile;
    }

    static class StreamGobbler extends Thread {

        InputStream is;

        String type;

        StringBuffer buffer = new StringBuffer();

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        private Conflict returnConflicts() {

            return null;
        }

        public StringBuffer getBuffer() {
            return buffer;
        }
    }

    public JPanel getConflictsJP() {
        return conflictsJP;
    }
    
    public JPanel getSemanticConflictisJP() {
        return semanticConflictisJP;
    }

    public JPanel getResultsJP() {
        return resultsJP;
    }
}

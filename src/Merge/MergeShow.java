package Merge;

import GUI.MainInterface.MainInterface;
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
import Merge.view.IconNodeRenderer;
import Merge.view.frames.DiffFrame;

public class MergeShow {

     private static String merged;
     private JPanel conflictsJP, resultsJP;
     
     /**
      * @param args
      */
     public void merge(String fileBase, String fileM1, String fileM2, MainInterface mainInterface){
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

               ConflictLogHandler conflictHandler = new ConflictLogHandler(conflictLogPath, topBase, topM1, topM2);
               conflictHandler.parseFile();
               List<Conflict> conflicts = conflictHandler.getConflicts();
               if (conflicts.size() > 0) {
                    ConflictResolverControler conflictResolver = new ConflictResolverControler();
                    conflictResolver.setConflicts(conflicts);
                    conflictResolver.setM1(topM1);
                    conflictResolver.setM2(topM2);
                    conflictResolver.setBase(topBase);
                    conflictResolver.setMerged(topMerged);
                    conflictsJP = conflictResolver.handleConflicts(mainInterface);
               } 
               
               resultsJP = showCompareTree(topMerged);
              

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
          saxParser.parse(new InputSource( new StringReader( content ) ));
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
          if (!tempFile.delete())
               throw new IOException();
          if (!tempFile.mkdir())
               throw new IOException();
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
                    while ((line = br.readLine()) != null)
                         buffer.append(line);
               } catch (IOException ioe) {
                    ioe.printStackTrace();
               }
          }

          public StringBuffer getBuffer() {
               return buffer;
          }
     }
     public JPanel getConflictsJP(){
         return conflictsJP;
     }
     
     public JPanel getResultsJP(){
         return resultsJP;
     }
}
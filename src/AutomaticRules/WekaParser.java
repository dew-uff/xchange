package AutomaticRules;

import GUI.MainInterface.DocumentsTab;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.associations.Item;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class WekaParser {
    
    public List<Set> generateRules(DocumentsTab documentsTab, List<String> mapeamentoTags, String keyChoice) {
        try {
            String separator = System.getProperty("file.separator");
            String workingPath = System.getProperty("user.dir");
            String document1, document2;
            if(keyChoice == "id") {
                document1 = workingPath+separator+"temp1.xml";
                document2 = workingPath+separator+"temp2.xml";
            } else {
                ArrayList<String> paths = documentsTab.getDocuments().getPathWays();
                document1 = paths.get(documentsTab.getLeftCBIndex());
                document2 = paths.get(documentsTab.getRightCBIndex());
            }
            
            String fileDiff = workingPath+separator+"temp"+separator+"mining_diff.xml";
            String fileArff = workingPath+separator+"temp"+separator+"mining_arff.arff";     
            
            //Gerando o Diff
            XDiff diff = new XDiff(document1, document2, fileDiff);
            //Mepeando o Diff em Lists
            List<List> mapeamentoDiff = new ArrayList<List>();
            int i = 0;
            
            XMLInputFactory factory = XMLInputFactory.newInstance();
            InputStream is = new FileInputStream(fileDiff);
            XMLStreamReader reader = factory.createXMLStreamReader(is);

            String root = null;
            String each = null;
            
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (root == null) {
                        root = reader.getLocalName();
                    } else if (each == null) {
                        each = reader.getLocalName();
                        mapeamentoDiff.add(new ArrayList<String>());
                    } else if (reader.getLocalName().equalsIgnoreCase(each)) {
                        mapeamentoDiff.add(new ArrayList<String>());
                        i++;
                    } else {
                        mapeamentoDiff.get(i).add(reader.getLocalName().toLowerCase());
                    }
                }
            }
            is.close();
            
            
            //Gerando ARFF baseado nas tags que você quer
            StringBuilder arff = new StringBuilder();
            arff.append("@relation ").append(root).append('\n');
            for (i = 0; i < mapeamentoTags.size(); i++) {
                    arff.append("@attribute '").append(mapeamentoTags.get(i)).append("' {y}\n");
            }
            arff.append("@data\n");

            for (i = 0; i < mapeamentoDiff.size(); i++) {
                
                if(mapeamentoDiff.get(i).contains(keyChoice)) //Eliminando DIFF que possua a tag selecionada como chave primária
                    continue;
                
                for (int j = 0; j < mapeamentoTags.size(); j++) {
                    if (j != 0) {
                        arff.append(',');
                    }
                    arff.append(mapeamentoDiff.get(i).contains(mapeamentoTags.get(j)) ? 'y' : '?');
                }
                arff.append('\n');
            }

            System.out.println(arff);        

            //Passar arff para o Weka e tratar a sáida 
            BufferedWriter br = new BufferedWriter(new FileWriter(new File(fileArff)));  
            br.write(arff.toString());  
            br.close(); 

            InputStream isArff = new FileInputStream(fileArff);

            Instances data = ConverterUtils.DataSource.read(isArff);
            data.setClassIndex(data.numAttributes() - 1);

            // build associator
            Apriori apriori = new Apriori();        
            apriori.setClassIndex(data.classIndex());
            apriori.setNumRules(1000);
            apriori.setMinMetric(0);
            apriori.setLowerBoundMinSupport(0.0);
            apriori.buildAssociations(data);
            apriori.getAssociationRules();

            List<Set> listRules = new ArrayList<Set>();        

            for (AssociationRule rule : apriori.getAssociationRules().getRules()) {
                Set<String> currentRule = new HashSet<String>();

                Collection<Item> allTags = rule.getPremise();
                allTags.addAll(rule.getConsequence());
                for (Item tag : allTags) {
                    currentRule.add(tag.getAttribute().name());
                }

                if(!listRules.contains(currentRule)) {
                    listRules.add(currentRule);
                }           
            }

            System.out.println(apriori);

            System.out.println("Saída em lista a ser passada para o XChange");
            System.out.println(listRules);

            return listRules;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<String> getTags(String document) {
        
        List<String> mapeamentoTags = new ArrayList<String>();
        try {
            //Lendo todas as tags do <emp> baseado na segunda versão
            XMLInputFactory factory = XMLInputFactory.newInstance();
            InputStream is = new FileInputStream(document);
            XMLStreamReader reader = factory.createXMLStreamReader(is);            
            
            String root = null;
            String each = null;
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (root == null) {
                        root = reader.getLocalName();
                    } else if (each == null) {
                        each = reader.getLocalName();
                        mapeamentoTags.add(each.toLowerCase());
                    } else if (reader.getLocalName().equalsIgnoreCase(each)) {
                        break;
                    } else {
                        mapeamentoTags.add(reader.getLocalName().toLowerCase());
                    }
                }
            }
            is.close();           
            
        } catch (IOException ex) {
            Logger.getLogger(WekaParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(WekaParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapeamentoTags;
    }
}

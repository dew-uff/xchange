package AutomaticRules;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.associations.Item;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class WekaParser {

    public static void main(String args[]){
        gerarRegras(args);
    }
    
    public static List<Set> gerarRegras(String args[]) {
        try {
            String file1 = "/Users/Matheus/Desktop/v1.xml";
            String file2 = "/Users/Matheus/Desktop/v2.xml";
            String fileDiff = "/Users/Matheus/Desktop/diff.xml";
            String fileArff = "/Users/Matheus/Desktop/arff.arff";
            XDiff diff = new XDiff(file1, file2, fileDiff);

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
                        mapeamentoDiff.get(i).add(reader.getLocalName());
                    }
                }
            }
            is.close();

            is = new FileInputStream(file2);
            reader = factory.createXMLStreamReader(is);
            List<String> mapeamentoTags = new ArrayList<String>();

            root = null;
            each = null;
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (root == null) {
                        root = reader.getLocalName();
                    } else if (each == null) {
                        each = reader.getLocalName();
                    } else if (reader.getLocalName().equalsIgnoreCase(each)) {
                        break;
                    } else {
                        mapeamentoTags.add(reader.getLocalName());
                    }
                }
            }
            is.close();

            StringBuilder arff = new StringBuilder();
            arff.append("@relation ").append(root).append('\n');
            for (i = 0; i < mapeamentoTags.size(); i++) {
                arff.append("@attribute '").append(mapeamentoTags.get(i)).append("' {y}\n");
            }
            arff.append("@data\n");

            for (i = 0; i < mapeamentoDiff.size(); i++) {
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

            String fileArff2 = "/Users/Matheus/Desktop/arff.arff";

            InputStream isArff = new FileInputStream(fileArff2);

            Instances data = ConverterUtils.DataSource.read(isArff);
            data.setClassIndex(data.numAttributes() - 1);

            // build associator
            Apriori apriori = new Apriori();        
            apriori.setClassIndex(data.classIndex());
            apriori.setNumRules(1000);
            apriori.setMinMetric(0.0);
            apriori.setLowerBoundMinSupport(0.0);
            apriori.buildAssociations(data);
            apriori.getAssociationRules();

            List<Set> listRules = new ArrayList<Set>();        

            for (AssociationRule rule : apriori.getAssociationRules().getRules()) {
                Set<String> currentRule = new HashSet<String>();

                Collection<Item> teste = rule.getPremise();
                ArrayList<Item> teste2 = new ArrayList<Item>(teste);
                String tag = teste2.get(0).getAttribute().name();
                currentRule.add(tag);

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
}

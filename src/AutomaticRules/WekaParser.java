package AutomaticRules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.associations.Item;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import Documents.Document;

public class WekaParser {
    
	public static void main(String args[]) {
		paraTestes(0.01F, 0.0F, 0);
    }
	
	public static void paraTestes(float valueSup, float valueMet, int metrica) {
		
		List<String> mapeamentoTags = new ArrayList<String>();
	    String[] campos = {"siape", "firstname", "lastname", "ssn", "phone", "birthdate", "sex", "fatherfirstname", "fatherlastname", "motherfirstname", "motherlastname", "hiredate", "bloodgroup", "placebirth", "nationality", "maritalstatus", "removed", "datelastremoved", "jobschema", "title", "level", "class", "departament", "university", "graduateprogram", "numbercourse", "numberadvisors", "classificationpaper"};
	    mapeamentoTags.addAll(Arrays.asList(campos));
	          
	    List<Document> documents = new ArrayList<Document>();
	           
	    String separator = System.getProperty("file.separator");
	    String workingPath = System.getProperty("user.dir");
	    
	    
	    String versao;
//	    Document doc;
//	    
//	    for (int i = 1 ; i <= 10 ; i++) {
//	    	versao = workingPath + separator + "tempRandom" + separator + "v" + i + ".xml";
//	    	doc = new Document(new File(versao));
//	    	documents.add(doc);
//	    } 
	    
	    new WekaParser().generateRules(documents, mapeamentoTags, "siape", valueSup, valueMet, metrica);
	}
    
    public List<Set<String>> generateRules(List<Document> documents, List<String> mapeamentoTags, String keyChoice, float valueSup, float valueMet, int metrica) {
        System.out.println("Inicio da ferramenta: " + new Date());
        
        try {
            String separator = System.getProperty("file.separator");
            String workingPath = System.getProperty("user.dir");
            
            String fileArff = workingPath + separator + "temp" + separator + "mining_arff.arff";

            //Gerando o Diff
            ArrayList<List<String>> mapeamentoDiff = new ArrayList<List<String>>();
            String root = "root";
            
            System.out.println("Inicio Diff: " + new Date());
            for (int k = 1; k < documents.size(); k++) {
                String path1 = null, path2 = null;
                
                if (keyChoice.equals("id")) {
                    String file1 = documents.get(k - 1).getFile().getName();
                    String file2 = documents.get(k).getFile().getName();
                    String preName = workingPath + separator + "temp" + separator + file1.substring(0, file1.length() - 4) + file2.substring(0, file2.length() - 4) + "_";
                    if (new File(preName + file1).isFile()) {
                        path1 = preName + file1;
                        path2 = preName + file2;
                    } else {
                        preName = workingPath + separator + "temp" + separator + file2.substring(0, file2.length() - 4) + file1.substring(0, file1.length() - 4) + "_";
                        path1 = preName + file1;
                        path2 = preName + file2;
                    }

                } else {
                    path1 = documents.get(k - 1).getPathWay();
                    path2 = documents.get(k).getPathWay();
                }                
                
                mapeamentoDiff = new XChangeDiff().diff(path1, path2, keyChoice, mapeamentoDiff);                
            }
            
            System.out.println("Inicio ARFF: " + new Date());
            
            //Gerando ARFF baseado nas tags que o usuario quiser
            StringBuilder arff = new StringBuilder();
            
            arff.append("@relation ").append(root).append('\n');

            for (String mapeamentoTag : mapeamentoTags) {
                arff.append("@attribute '").append(mapeamentoTag).append("' {y,u,d}\n");
            }
            
            arff.append("@data\n");

            for (List<String> mapeamentoDiff1 : mapeamentoDiff) {
                if (mapeamentoDiff1.contains(keyChoice)) {
                    continue;
                }
                //System.out.println(mapeamentoDiff1);
                for (int j = 0; j < mapeamentoTags.size(); j++) {
                    if (j != 0) {
                        arff.append(',');
                    }
                    if(mapeamentoDiff1.contains(mapeamentoTags.get(j)+"+up")){
                        arff.append('u');
                    }
                    else{
                        if(mapeamentoDiff1.contains(mapeamentoTags.get(j)+"+down")){
                            arff.append('d');
                        }
                        else{
                            if(mapeamentoDiff1.contains(mapeamentoTags.get(j))){
                                arff.append('y');
                            }
                            else{
                                arff.append('?');
                            }
                        }
                         
                    }
                }
                arff.append('\n');
            }

            System.out.println(arff);

            System.out.println("Inicio Apriori: " + new Date());
            //Passar arff para o Weka e tratar a sáida 
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(fileArff)));
            bw2.write(arff.toString());
            bw2.close();

            InputStream isArff = new FileInputStream(fileArff);

            Instances data = ConverterUtils.DataSource.read(isArff);
            data.setClassIndex(data.numAttributes() - 1);

            //Opcoes de metricas para passar ao Apriori
            String[] options = new String[16];
            //Number of rules
            options[0] = "-N";
            options[1] = "1000";
            //Metric Type (0: "Confidence" 1: "Lift" 2: "Leverage" 3: "Conviction")
            options[2] = "-T";
            options[3] = ""+metrica;
            //Min Metric
            options[4] = "-C";
            options[5] = ""+valueMet;
            //Delta
            options[6] = "-D";
            options[7] = "0.05";
            //Upper Bound Min Support
            options[8] = "-U";
            options[9] = "1.0";
            //Lower Bound Min Support
            options[10] = "-M";
            options[11] = ""+valueSup;
            //Significance Level
            options[12] = "-S";
            options[13] = "-1.0";
            //Class Index
            options[14] = "-c";
            options[15] = "-1";
            
            Apriori apriori = new Apriori();
            apriori.setOptions(options);
            apriori.buildAssociations(data);

            System.out.println("Inicio Regras em lista: " + new Date());
            List<Set<String>> listRules = new ArrayList<Set<String>>();
            Set<String> currentRule;
            
            for (AssociationRule rule : apriori.getAssociationRules().getRules()) {
                currentRule = new HashSet<String>();

                Collection<Item> allTags = rule.getPremise();
                allTags.addAll(rule.getConsequence());
                for (Item tag : allTags) {
                   // System.out.println(tag.getItemValueAsString());
                    if(tag.getItemValueAsString().equals("u")){
                        currentRule.add((tag.getAttribute().name() + " ↑"));
                    }
                    else{
                        if(tag.getItemValueAsString().equals("d")){
                            currentRule.add((tag.getAttribute().name() + " ↓"));
                        }
                        else{
                            currentRule.add(tag.getAttribute().name());
                        }
                    }
                }

                if (!listRules.contains(currentRule)) {
                    listRules.add(currentRule);
                }
            }
            
            // Adicionando regras unitárias
            for (String mapeamentoTag : mapeamentoTags) {
                currentRule = new HashSet<String>();
                currentRule.add(mapeamentoTag);
                listRules.add(currentRule);
            }
            
            System.out.println("Fim: " + new Date());

            System.out.println(apriori);

            System.out.println("Saida em lista a ser passada para o XChange");
            
            return listRules;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getTags(String document) {

        List<String> mapeamentoTags = new ArrayList<String>();
        try {
            //Lendo todas as tags do <emp> baseado na segunda versao
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

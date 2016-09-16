package AutomaticRules;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class XChangeDiff {
        
    public XChangeDiff(){
        
    }
    
    public ArrayList<List<String>> diff(String doc1, String doc2, String key, ArrayList<List<String>> results) {           
        
        try {            
            HashMap<String, HashMap<String, String>> xml1 = parser(doc1, key);
            HashMap<String, HashMap<String, String>> xml2 = parser(doc2, key);
            
            for (String regKey : xml1.keySet()) {
                
                if (xml2.containsKey(regKey)) {
                    
                    ArrayList<String> regDiff = new ArrayList<String>();
                                
                    for (String atribute : xml1.get(regKey).keySet()) {
                        String aux = xml1.get(regKey).get(atribute);
                        String aux1 = xml2.get(regKey).get(atribute);
                        try{
                            double i = Double.parseDouble(aux);
                            double i1 = Double.parseDouble(aux1);
                            if (i<i1) {                                
                                regDiff.add(atribute+"+up");
                           }
                            if(i>i1){
                                regDiff.add(atribute+"+down");
                            }
                        }
                        catch (Exception a){
                            if (!(aux.trim()).equalsIgnoreCase(aux1.trim())) {
                                regDiff.add(atribute);
                           }
                        }

                        
                    }

                    if (!regDiff.isEmpty()) {
                        //if (regDiff.size() >2) System.out.println(regDiff);
                        results.add(regDiff);
                    }
                }
            }
            
            return results;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public HashMap<String, HashMap<String, String>> parser(String doc, String key) {
        
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            InputStream is = new FileInputStream(doc);
            XMLStreamReader reader = factory.createXMLStreamReader(is);

            String root = null;
            String register = null;
            HashMap<String, HashMap<String, String>> xml = new HashMap<String, HashMap<String, String>>();
            HashMap<String, String> reg = new HashMap<String, String>();

            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (root == null) {
                        root = reader.getLocalName();
                    } else if (register == null) {
                        register = reader.getLocalName();
                        reg = new HashMap<String, String>();
                    } else {
                        reg.put(reader.getLocalName().toLowerCase(), reader.getElementText().toLowerCase());
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    register = null;
                    
                    if (!xml.containsKey(reg.get(key))) {
                        xml.put(reg.get(key), reg);
                    }
                    
                }
            }
            
            is.close();
            
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Phoenix;

import br.uff.ic.gems.phoenix.PhoenixDiffCalculator;
import br.uff.ic.gems.phoenix.XmlParser;
import br.uff.ic.gems.phoenix.exception.ComparisonException;
import br.uff.ic.gems.phoenix.exception.PhoenixDiffException;
import br.uff.ic.gems.phoenix.exception.XmlParserException;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author Matheus
 */
public class PhoenixWrapper {
    
    public static String doSimilarity(String xmlfilepath1, String xmlfilepath2) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PhoenixDiffCalculator cmp = new PhoenixDiffCalculator(xmlfilepath1,xmlfilepath2);
            cmp.setOutputStream(baos);
            cmp.executeComparison();
        } catch (PhoenixDiffException ex) {
            Logger.getLogger(PhoenixWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ComparisonException ex) {
            Logger.getLogger(PhoenixWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return baos.toString();
    }
    
    
    public static Document createDOMDocument(String file){
        try {
            return XmlParser.createDOMDocument(file);
        } catch (XmlParserException ex) {
            Logger.getLogger(PhoenixWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}

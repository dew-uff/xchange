
import Translate.Similarity;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matheus
 */
public class TestClass {
    
    public static void main(String[] args) {
        Similarity sim = new Similarity(null);
        sim.documentsWithIDs("/Users/Matheus/Desktop/v1.xml", "/Users/Matheus/Desktop/v2.xml");
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SemanticMerge;


/**
 * Classe responsável pelo armazenamento das mudanças (irá conter o documento e os caminho do que foi alterado e entra na condição semântica)
 * @author Frnando
 */
public class SemanticChange {
    private int document1;
    private String paths1;
    private int document2;
    private String paths2;
    private String rule;

    public int getDocument1() {
        return document1;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public void setDocument1(int document1) {
        this.document1 = document1;
    }

    public String getPaths1() {
        return paths1;
    }

    public void setPaths1(String paths1) {
        this.paths1 = paths1;
    }

    public int getDocument2() {
        return document2;
    }

    public void setDocument2(int document2) {
        this.document2 = document2;
    }

    public String getPaths2() {
        return paths2;
    }

    public void setPaths2(String paths2) {
        this.paths2 = paths2;
    }

    public String getPath(int document){
        if(document == this.document1){
            return this.paths1;
        }
        if(document == this.document2){
            return this.paths2;
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.document1;
        hash = 53 * hash + (this.paths1 != null ? this.paths1.hashCode() : 0);
        hash = 53 * hash + this.document2;
        hash = 53 * hash + (this.paths2 != null ? this.paths2.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SemanticChange other = (SemanticChange) obj;
        if (this.document1 != other.document1) {
            return false;
        }
        if ((this.paths1 == null) ? (other.paths1 != null) : !this.paths1.equals(other.paths1)) {
            return false;
        }
        if (this.document2 != other.document2) {
            return false;
        }
        if ((this.paths2 == null) ? (other.paths2 != null) : !this.paths2.equals(other.paths2)) {
            return false;
        }
        return true;
    }
    
    
}

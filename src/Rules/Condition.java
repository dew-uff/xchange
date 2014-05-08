/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rules;

import GUI.Rules.LineCondition;

/**
 *
 * @author Matheus
 */
public class Condition {

    private String firstTerm;
    private String secondTerm;
    private String operator;

    public Condition(LineCondition lineRule) {
        this.firstTerm = lineRule.getComboTerm1().getSelectedItem().toString();
        this.secondTerm = lineRule.getComboTerm2().getSelectedItem().toString();
        this.operator = lineRule.getComboOperator().getSelectedItem().toString();
    }
    
    public Condition(String firstTerm, String secondTerm, String operator) {
        this.firstTerm = firstTerm;
        this.secondTerm = secondTerm;
        this.operator = operator;
    }

    public String getFirstTerm() {
        return firstTerm;
    }

    public void setFirstTerm(String firstTerm) {
        this.firstTerm = firstTerm;
    }

    public String getSecondTerm() {
        return secondTerm;
    }

    public void setSecondTerm(String secondTerm) {
        this.secondTerm = secondTerm;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object != null && object instanceof Condition)
        {
            if(this.firstTerm.equals(((Condition)object).getFirstTerm())
                && this.secondTerm.equals(((Condition)object).getSecondTerm())
                    && this.operator.equals(((Condition)object).getOperator()))
                equals = true;
        }

        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.firstTerm != null ? this.firstTerm.hashCode() : 0);
        hash = 97 * hash + (this.secondTerm != null ? this.secondTerm.hashCode() : 0);
        hash = 97 * hash + (this.operator != null ? this.operator.hashCode() : 0);
        return hash;
    }
}

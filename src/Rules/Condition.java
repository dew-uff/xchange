/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rules;

import GUI.Rules.LineRule;

/**
 *
 * @author Matheus
 */
public class Condition {

    private String firstTerm;
    private String secondTerm;
    private String operator;

    public Condition(LineRule lineRule) {
        this.firstTerm = lineRule.getComboTerm1().getSelectedItem().toString();
        this.secondTerm = lineRule.getComboTerm2().getSelectedItem().toString();
        this.operator = lineRule.getComboOperator().getSelectedItem().toString();
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
}

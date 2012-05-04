/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.ue;

/**
 *
 * @author mafre
 */
public enum UETerm {
    BROW("BRWOSER"), 
    USER("USER"), 
    RES("RESULT"), 
    START("START"), 
    END("END"), 
    CLICKED("CLICKEDWIDGETS"), 
    ID("ID"), 
    TS("TIMESTAMP"), 
    VAL("VALUE"),
    BREAK("BREAK"),
    SOL("SOLUTION"),
    UEQ("UE_QUESTIONNAIRE"),
    UEF("UE_FREE_FEEDBACK"),
    TYPE("DIALOGTYPE"),
    LOAD("LOAD"),
    ISOL("INTERMED_SOLS"),
    GROUP("STUDY_GROUP"),
    NAN("NONE");
    
    private String stringrep;
     
    UETerm(String stringrep)
    {
        this.stringrep = stringrep;
    }

    public String toString()
    {
         return stringrep;
    }
}

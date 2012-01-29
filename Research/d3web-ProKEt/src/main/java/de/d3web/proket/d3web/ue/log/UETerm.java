/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.ue.log;

/**
 *
 * @author mafre
 */
public enum UETerm {
    BROW("browser"), USER("user"), RES("result"), 
    START("start"), END("end"), CLICKED("clickedwidgets"), 
    ID("id"), 
    TS("timestamp"), 
    VAL("value"),
    BREAK("BREAK"),
    LOAD("LOAD");
    
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

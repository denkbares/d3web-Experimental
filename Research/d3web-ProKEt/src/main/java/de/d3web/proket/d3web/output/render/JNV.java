/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.output.render;

/**
 *
 * @author Martina Freiberg
 * @date Aug2012
 */
public enum JNV {
    J("ja"), 
    N("nein"),
    V("vielleicht");
    
    private String stringrep;
     
    JNV(String stringrep)
    {
        this.stringrep = stringrep;
    }

    public String toString()
    {
         return stringrep;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.input;

/**
 * Class for storing USER-specific stuff, such as for example, the language
 * that is to be used for a given user in the current session.
 * 
 * @author Martina Freiberg
 * @Date 25.01.2012
 */
public class D3webUserSettings {
    
    private int languageId;
    
    public D3webUserSettings(){
        // set default language id to 2 which means english language
        this.languageId = 2;
    }
    
    public void setLanguageId(int id){
        this.languageId = id;
    }
    
    public int getLanguageId(){
        return this.languageId;
    }
    
    
}

/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.proket.d3web.input;

/**
 * Class for storing all UI Properties of a productive KBS as specified in the
 * XML, e.g. definition of CSS, header text etc etc
 * UISettings are parsed from the specification and are the same for each user
 * given that he uses the same dialog. They are distinct for each distinct
 * dialog however.
 * 
 * @author Martina Freiberg
 * @date 29/08/2012
 */
public class UISettings {
 
    /*
     * The Css parsed from the d3web XML
     */
    private String css;

    /*
     * The header / title of the dialog parsed from the d3web XML
     */
    private String header;
    
     /*
     * number of columns for multicolumn styles (dialog)
     */
    private int dcols;

    /*
     * number of columns for multicolumn styles (questionnaire)
     */
    private int questcols = -1;

    /*
     * number of columns for multicolumn styles (questionnaire)
     */
    private int qcols = -1;

    /*
     * prefix that can be set by the user to define more specific dialog types
     */
    private String uiprefix = "";

    /*
     * In case we want to internationalize
     */
    private String language = "en";
    
    /*
     * The login mode, needed for adding the chosen form of login
     */
    private D3webXMLParser.LoginMode loginMode;
   

    private D3webXMLParser.SolutionExplanationType solExpType;
    
    
    private static UISettings instance;
    
    private boolean diagnosisNavi = false;
    
    private boolean questionnaireNavi = false;
    
    public static UISettings getInstance() {
        if (instance == null) {
            instance = new UISettings();
        }
        return instance;
    }

    private UISettings() {
    }
    
    
    
    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getDialogColumns() {
        return this.dcols;
    }

    public void setDialogColumns(int c) {
        this.dcols = c;
    }

    public int getQuestionColumns() {
        return this.qcols;
    }

    public void setQuestionColumns(int c) {
        this.qcols = c;
    }

    public int getQuestionnaireColumns() {
        return this.questcols;
    }

    public void setQuestionnaireColumns(int c) {
        this.questcols = c;
    }
    
    public String getUIprefix() {
        return this.uiprefix;
    }

    public void setUIprefix(String pref) {
        this.uiprefix = pref;
    }

     public void setLanguage(String lang) {
        this.language = lang;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLoginMode(D3webXMLParser.LoginMode login) {
        this.loginMode = login;
    }

    public D3webXMLParser.LoginMode getLoginMode() {
        return this.loginMode;
    }
    
    public void setSolutionExplanationType(D3webXMLParser.SolutionExplanationType sol) {
        this.solExpType = sol;
    }

    public D3webXMLParser.SolutionExplanationType getSolutionExplanationType() {
        return this.solExpType;
    }
 
      public boolean hasDiagnosisNavi() {
        return diagnosisNavi;
    }

    public void setDiagnosisNavi(boolean diagnosisNavi) {
        this.diagnosisNavi = diagnosisNavi;
    }

    public boolean hasQuestionnaireNavi() {
        return questionnaireNavi;
    }

    public void setQuestionnaireNavi(boolean questionnaireNavi) {
        this.questionnaireNavi = questionnaireNavi;
    }
}

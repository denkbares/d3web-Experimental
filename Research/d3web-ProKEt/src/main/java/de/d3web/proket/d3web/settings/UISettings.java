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
package de.d3web.proket.d3web.settings;

import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;
import java.util.ArrayList;
import java.util.HashMap;

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
    private int dialogColumns;

    /*
     * number of columns for multicolumn styles (questionnaire)
     */
    private int questionnaireColumns = -1;

    /*
     * number of columns for multicolumn styles (questionnaire)
     */
    private int questionColumns = -1;

    /*
     * prefix that can be set by the user to define more specific dialog types
     */
    private DialogType dialogType;

    /*
     * In case we want to internationalize
     */
    private String language = "en";
    
    /*
     * The login mode, needed for adding the chosen form of login
     */
    private D3webXMLParser.LoginMode loginMode;
    
    private ArrayList solutionDepths;
    
    
    private static UISettings instance;
    
    private boolean diagnosisNavi = false;
    
    private boolean questionnaireNavi = false;
    
    private boolean dropdown = false;
    
    private boolean largetext = false;
    
    private boolean questionnaireNumbering = false;
    private boolean questionNumbering = false;
    
    
    private D3webXMLParser.IndicationRepresentation showIndicated;
    private D3webXMLParser.IndicationRepresentation showContraIndicated;
    private D3webXMLParser.IndicationRepresentation showNonIndicated;
    
    private Boolean debug;
    private DialogStrategy dialogStrategy;
    private boolean unknownVisible;
    private boolean ynFlat;
    private String autocolumns;
    
    private HashMap<String, Boolean> unknownVisibleQuestionsLoc;
    private HashMap<String, Integer> questionColumnsLoc;
    //adapt parser
    private HashMap<String, Boolean> dropdownQuestionsLoc;
    private HashMap<String, Boolean> overlayQuestionsLoc;
    private HashMap<String, Boolean> largeTextQuestionsLoc;
    private HashMap<String, String> autocolumnsQuestionsLoc;
    private HashMap<String, ArrayList> groupedQuestionsLoc;
    
    
    
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
        return this.dialogColumns;
    }

    public void setDialogColumns(int c) {
        this.dialogColumns = c;
    }

    public int getQuestionColumns() {
        return this.questionColumns;
    }

    public void setQuestionColumns(int c) {
        this.questionColumns = c;
    }

    public int getQuestionnaireColumns() {
        return this.questionnaireColumns;
    }

    public void setQuestionnaireColumns(int c) {
        this.questionnaireColumns = c;
    }
    
    public DialogType getDialogType() {
        return this.dialogType;
    }

    public void setDialogType(DialogType type) {
        this.dialogType = type;
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

   
    
    public D3webXMLParser.IndicationRepresentation getShowIndicated() {
        return this.showIndicated;
    }

    public void setShowIndicated(D3webXMLParser.IndicationRepresentation sIndi) {
        this.showIndicated = sIndi;
    }
    
    public D3webXMLParser.IndicationRepresentation getShowContraIndicated() {
        return this.showContraIndicated;
    }

    public void setShowContraIndicated(D3webXMLParser.IndicationRepresentation sIndi) {
        this.showContraIndicated = sIndi;
    }
    
    public D3webXMLParser.IndicationRepresentation getShowNonIndicated() {
        return this.showNonIndicated;
    }

    public void setShowNonIndicated(D3webXMLParser.IndicationRepresentation sIndi) {
        this.showNonIndicated = sIndi;
    }

   

    public String getAutocolumns() {
        return autocolumns;
    }

    public void setAutocolumns(String autocolumns) {
        this.autocolumns = autocolumns;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public DialogStrategy getDialogStrategy() {
        return dialogStrategy;
    }

    public void setDialogStrategy(DialogStrategy dialogStrategy) {
        this.dialogStrategy = dialogStrategy;
    }

    public boolean getUnknownVisible() {
        return unknownVisible;
    }

    public void setUnknownVisible(boolean unknownVisible) {
        this.unknownVisible = unknownVisible;
    }

    public boolean isYnFlat() {
        return ynFlat;
    }

    public void setYnFlat(boolean ynFlat) {
        this.ynFlat = ynFlat;
    }

    public HashMap<String, String> getAutocolumnsQuestionsLoc() {
        return autocolumnsQuestionsLoc;
    }

    public void setAutocolumnsQuestionsLoc(HashMap<String, String> autocolumnsQuestionsLoc) {
        this.autocolumnsQuestionsLoc = autocolumnsQuestionsLoc;
    }

    public HashMap<String, Boolean> getDropdownQuestionsLoc() {
        return dropdownQuestionsLoc;
    }

    public void setDropdownQuestionsLoc(HashMap<String, Boolean> dropdownQuestionsLoc) {
        this.dropdownQuestionsLoc = dropdownQuestionsLoc;
    }

    public HashMap<String, ArrayList> getGroupedQuestionsLoc() {
        return groupedQuestionsLoc;
    }

    public void setGroupedQuestionsLoc(HashMap<String, ArrayList> groupedQuestionsLoc) {
        this.groupedQuestionsLoc = groupedQuestionsLoc;
    }

    public HashMap<String, Boolean> getLargeTextQuestionsLoc() {
        return largeTextQuestionsLoc;
    }

    public void setLargeTextQuestionsLoc(HashMap<String, Boolean> largeTextEntryQuestionsLoc) {
        this.largeTextQuestionsLoc = largeTextEntryQuestionsLoc;
    }

    public HashMap<String, Boolean> getOverlayQuestionsLoc() {
        return overlayQuestionsLoc;
    }

    public void setOverlayQuestionsLoc(HashMap<String, Boolean> overlayQuestionsLoc) {
        this.overlayQuestionsLoc = overlayQuestionsLoc;
    }

    public HashMap<String, Integer> getQuestionColumnsLoc() {
        return questionColumnsLoc;
    }

    public void setQuestionColumnsLoc(HashMap<String, Integer> questionColumnsLoc) {
        this.questionColumnsLoc = questionColumnsLoc;
    }

    public HashMap<String, Boolean> getUnknownVisibleQuestionsLoc() {
        return unknownVisibleQuestionsLoc;
    }

    public void setUnknownVisibleQuestionsLoc(HashMap<String, Boolean> unknownVisibleQuestionsLoc) {
        this.unknownVisibleQuestionsLoc = unknownVisibleQuestionsLoc;
    }

    public boolean getDropdown() {
        return dropdown;
    }

    public void setDropdown(boolean dropdown) {
        this.dropdown = dropdown;
    }

    public boolean getLargetext() {
        return largetext;
    }

    public void setLargetext(boolean largetext) {
        this.largetext = largetext;
    }

    public boolean getQuestionNumbering() {
        return questionNumbering;
    }

    public void setQuestionNumbering(boolean questionNumbering) {
        this.questionNumbering = questionNumbering;
    }

    public boolean getQuestionnaireNumbering() {
        return questionnaireNumbering;
    }

    public void setQuestionnaireNumbering(boolean questionnaireNumbering) {
        this.questionnaireNumbering = questionnaireNumbering;
    }
    
    
}

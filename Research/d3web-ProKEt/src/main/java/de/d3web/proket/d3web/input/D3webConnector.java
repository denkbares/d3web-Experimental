/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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

import java.util.HashMap;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webXMLParser.LoginMode;
import de.d3web.proket.d3web.ue.log.JSONLogger;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.data.IndicationMode;

/**
 * Data storage class for everything that is parsed from the d3web XML and
 * created while working with d3web.
 * 
 * @author Martina Freiberg
 * @created 16.10.2010
 */
public class D3webConnector {

    private static D3webConnector instance;

    /* The current session */
    private Session session;

    /* The default strategy */
    private DialogStrategy dialogStrat = DialogStrategy.NEXTFORM;

    /* The default dialogtype */
    private DialogType dialogType = DialogType.SINGLEFORM;

    /* Mode how not indicated qasets are handles */
    private IndicationMode indicationMode = IndicationMode.NORMAL;

    /* The knowledge base */
    private KnowledgeBase kb;

    /* Map that contains an ID for each TO connected to the root */
    private Map<TerminologyObject, String> idMap;

    /* Counter for question IDs */
    private int qCount = 1;

    /* Counter for questionnaire IDs */
    private int qcCount = 0;

    /* Counter for solution IDs */
    private int sCount = 0;

    /* The Css parsed from the d3web XML */
    private String css;

    /* The header / title of the dialog parsed from the d3web XML */
    private String header;

    /* The knowledge base management */
    private String kbn;

    /* number of columns for multicolumn styles (dialog) */
    private int dcols;

    /* number of columns for multicolumn styles (questionnaire) */
    private int questcols = -1;

    /* number of columns for multicolumn styles (questionnaire) */
    private int qcols = -1;

    /* prefix that can be set by the user to define more specific dialog types */
    private String userprefix = "";

    /* single element specification, e.g. selectbox... */
    private HashMap<String, HashMap<String, String>> singleSpecs;
    private D3webXMLParser d3webParser = null;
    // set english as default language setting
    private String language = "en";
    private JSONLogger logger = null;
    private LoginMode loginMode;
    private boolean loggingActive = false;

    public static D3webConnector getInstance() {
        if (instance == null) {
            instance = new D3webConnector();
        }
        return instance;
    }

    private D3webConnector() {
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session s) {
        session = s;
    }

    public DialogStrategy getDialogStrat() {
        return dialogStrat;
    }

    public void setDialogStrat(DialogStrategy dialogStrat) {
        this.dialogStrat = dialogStrat;
    }

    public DialogType getDialogType() {
        return dialogType;
    }

    public void setDialogType(DialogType dialogType) {
        this.dialogType = dialogType;
    }

    public IndicationMode getIndicationMode() {
        return this.indicationMode;
    }

    public void setIndicationMode(IndicationMode mode) {
        this.indicationMode = mode;
    }

    public KnowledgeBase getKb() {
        return kb;
    }

    public void setKb(KnowledgeBase kb) {
        this.kb = kb;
        this.idMap = new HashMap<TerminologyObject, String>();
        generateIDs(kb.getRootQASet());
        generateIDs(kb.getRootSolution());

    }

    private void generateIDs(TerminologyObject... tos) {
        for (TerminologyObject to : tos) {
            int count = -1;
            if (to instanceof QContainer) {
                count = qcCount;
                qcCount++;
            } else if (to instanceof Question) {
                count = qCount;
                qCount++;
            } else if (to instanceof Solution) {
                count = sCount;
                sCount++;
            }
            idMap.put(to, count == -1 ? "" : String.valueOf(count));
            generateIDs(to.getChildren());
        }
    }

    public String getID(TerminologyObject to) {
        String id = idMap.get(to);
        if (id == null) {
            id = "";
        }
        return id;
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

    public String getKbName() {
        return this.kbn;
    }

    public void setKbName(String kbn) {
        this.kbn = kbn;
    }

    public String getUserprefix() {
        return this.userprefix;
    }

    public void setUserprefix(String pref) {
        this.userprefix = pref;
    }

    public void setSingleSpecs(HashMap<String, HashMap<String, String>> singleSpecs) {
        this.singleSpecs = singleSpecs;
    }

    public HashMap<String, HashMap<String, String>> getSingleSpecs() {
        return this.singleSpecs;
    }

    public void setD3webParser(D3webXMLParser parser) {
        this.d3webParser = parser;
    }

    public D3webXMLParser getD3webParser() {
        return d3webParser;
    }

    public void setLanguage(String lang) {
        this.language = lang;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLoginMode(LoginMode login) {
        this.loginMode = login;
    }

    public LoginMode getLoginMode() {
        return this.loginMode;
    }

    public void setLogger(JSONLogger logger) {
        this.logger = logger;
    }

    public JSONLogger getLogger() {
        return this.logger;
    }

    public void activateLogging(){
        this.loggingActive = true;
    }
    
    public boolean loggingActive(){
        return this.loggingActive;
    }
}

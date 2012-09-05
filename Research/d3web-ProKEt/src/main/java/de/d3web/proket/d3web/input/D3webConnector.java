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
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.data.IndicationMode;

/**
 * Data storage class for everything that is parsed from the d3web XML 
 * specification and concerns ONLY d3web
 *
 * @author Martina Freiberg @created 16.10.2010
 */
public class D3webConnector {

    /*
     * The current session
     */
    private Session session;

    /*
     * The default strategy
     */
    private DialogStrategy dialogStrat = DialogStrategy.NEXTFORM;

    /*
     * The default dialogtype
     */
    private DialogType dialogType = DialogType.SINGLEFORM;

    /*
     * Mode how not indicated qasets are handles
     */
    private IndicationMode indicationMode = IndicationMode.NORMAL;

    /*
     * The knowledge base
     */
    private KnowledgeBase kb;

    /*
     * Map that contains a number count for each TO connected to the root
     */
    private Map<TerminologyObject, String> toCountMap;

    /*
     * Counter for question IDs; needs to be 1 as there is no root question 
     */
    private int qCount = 1;

    /*
     * Counter for questionnaire IDs; needs to be 0 as there exists a root qc
     */
    private int qcCount = 0;

    /*
     * Counter for solution IDs; needs to be 0 as there exists a root solution
     */
    private int sCount = 0;

    /**
     * The parser instance that parsed the d3web specs that contained the 
     * properties to be stored here
     */
    private D3webXMLParser d3webParser = null;

    
    private static D3webConnector instance;
    
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
        this.toCountMap = new HashMap<TerminologyObject, String>();
        qcCount = 0;
        qCount = 1;
        sCount = 0;
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
            toCountMap.put(to, count == -1 ? "" : String.valueOf(count));
            generateIDs(to.getChildren());
        }
    }

    public String getTOCount(TerminologyObject to) {
        String id = toCountMap.get(to);
        if (id == null) {
            id = "";
        }
        return id;
    }

    public String getKbName() {
        return this.kb!=null?this.kb.getName():"";
    }

    public void setD3webParser(D3webXMLParser parser) {
        this.d3webParser = parser;
    }

    public D3webXMLParser getD3webParser() {
        return d3webParser;
    }

}

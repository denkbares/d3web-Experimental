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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;

/**
 * Data storage class for everything that is parsed from the d3web XML and
 * created while working with d3web.
 * 
 * @author Martina Freiberg
 * @created 16.10.2010
 */
public class D3webConnector {

	private int quesCount = 0;

	private int qCount = 0;

	private static D3webConnector instance;

	/* The current session */
	private Session session;

	/* The default strategy */
	private DialogStrategy dialogStrat = DialogStrategy.NEXTFORM;

	/* The default dialogtype */
	private DialogType dialogType = DialogType.SINGLEFORM;

	/* The knowledge base */
	private KnowledgeBase kb;

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

	public static D3webConnector getInstance() {
		if (instance == null) {
			instance = new D3webConnector();
		}
		return instance;
	}

	private D3webConnector() {
		this.quesCount = 0;
		this.qCount = 0;
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

	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
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

	public int getQuestionnaireCount() {
		return this.quesCount;
	}

	public void setQuestionnaireCount(int qc) {
		this.quesCount = qc;
	}

	public int getQuestionCount() {
		return this.qCount;
	}

	public void setQuestionCount(int qc) {
		this.qCount = qc;
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
}

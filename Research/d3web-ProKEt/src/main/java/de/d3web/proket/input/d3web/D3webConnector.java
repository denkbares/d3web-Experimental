package de.d3web.proket.input.d3web;

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

}

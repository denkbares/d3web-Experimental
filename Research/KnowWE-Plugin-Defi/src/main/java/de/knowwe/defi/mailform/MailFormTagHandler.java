/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.mailform;

import java.util.List;
import java.util.Map;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * A normal mailform.
 * 
 * @author dupke
 * @created 17.03.2011
 */
public class MailFormTagHandler extends AbstractTagHandler {

	/** tagattributes **/
	private static final String FORM_NAME = "id";

	/**
	 * @param name
	 */
	public MailFormTagHandler() {
		super("mailform");
	}

	/**
	 * A mailform
	 * 
	 */
	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder mailform = new StringBuilder();
		if (userContext.userIsAsserted()) {
			String id = "";
			if (parameters.containsKey(FORM_NAME)) id = "mf_" + parameters.get(FORM_NAME);
			else return KnowWEUtils.maskHTML(mailform.append(
					"<p>Jedes Mail-Formular muss eine eindeutige ID besitzen</p>").toString());
			if (checkID(id, article)) return KnowWEUtils.maskHTML(mailform.append(
					"<p>Jedes Mail-Formular muss eine eindeutige ID besitzen</p>").toString());

			mailform.append("<form id='" + id
					+ "' class='mailform'  method='post' enctype='text/plain'>");
			mailform.append("<p>Schicken sie uns eine Nachricht:</p>");
			mailform.append("<textarea name='nachricht' rows='8' cols='50'></textarea><br>");
			mailform.append("<p><input type='button' value='Abschicken' onclick='mailForm(\"" + id
					+ "\")' />");
			mailform.append("</form>");

		}
		else {
			mailform.append("<p>Melden Sie sich bitte an um eine Nachricht zu schreiben.</p>");
		}
		return KnowWEUtils.maskHTML(mailform.toString());
	}

	// TODO: BUTTON VERÄNDERN NACH DRÜCKEN / BUTTON AUF DOPPELTE BUTTONS PRÜFEN

	/**
	 * Checks the id for errors.
	 * 
	 * @return true = double id
	 */
	boolean checkID(String id, KnowWEArticle article) {
		List<Section<? extends Type>> allNodes = article.getAllNodesPreOrder();
		Section<? extends Type> node;
		int count = 0;
		id = id.substring(3);

		// (2) Check for double id
		for (int i = 0; i < allNodes.size(); i++) {
			node = allNodes.get(i);

			// conditions:
			// - TagHandlerType
			// - mailform
			// - mailform id = current id
			if (node.get().toString().contains("TagHandlerType")
					&& node.toString().contains("KnowWEPlugin mailform")
					&& getMailFormID(node.toString()).equals(id)) count++;
		}

		// (2) current id has been found more often than once => double id
		if (count > 1) return true;
		return false;
	}

	/**
	 * 
	 * @created 05.06.2011
	 * @param mf
	 * @return
	 */
	private String getMailFormID(String mf) {
		String id = "";

		id = mf.split("id=")[1];
		if (id.split(" ,").length > 1) return id.split(" ,")[0];
		else if (id.split(" }]").length > 1) return id.split(" }]")[0];
		else return id.split("}]")[0];
	}

}

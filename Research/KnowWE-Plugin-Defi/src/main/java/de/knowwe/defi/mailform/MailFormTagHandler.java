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

import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

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
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult mailform) {
		if (userContext.userIsAsserted()) {
			String id = "";
			if (parameters.containsKey(FORM_NAME)) id = "mf_" + parameters.get(FORM_NAME);
			else {
				mailform.appendHtml(
						"<p>Jedes Mail-Formular muss eine eindeutige ID besitzen</p>");
				return;
			}
			if (checkID(id, section.getArticle())) {
				mailform.appendHtml("<p>Jedes Mail-Formular muss eine eindeutige ID besitzen</p>");
				return;
			}

			mailform.appendHtml("<form id='" + id
					+ "' class='mailform'  method='post' enctype='text/plain'>");
			mailform.appendHtml("<p>Schicken sie uns eine Nachricht:</p>");
			mailform.appendHtml("<textarea name='nachricht' rows='8' cols='50'></textarea><br>");
			mailform.appendHtml("<p><input type='button' value='Abschicken' onclick='mailForm(\""
					+ id
					+ "\")' />");
			mailform.appendHtml("</form>");

		}
		else {
			mailform.appendHtml("<p>Melden Sie sich bitte an um eine Nachricht zu schreiben.</p>");
		}

	}

	// TODO: BUTTON VERÄNDERN NACH DRÜCKEN / BUTTON AUF DOPPELTE BUTTONS PRÜFEN

	/**
	 * Checks the id for errors.
	 * 
	 * @return true = double id
	 */
	boolean checkID(String id, Article article) {
		List<Section<?>> allNodes = Sections.getSubtreePreOrder(article.getRootSection());
		Section<?> node;
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

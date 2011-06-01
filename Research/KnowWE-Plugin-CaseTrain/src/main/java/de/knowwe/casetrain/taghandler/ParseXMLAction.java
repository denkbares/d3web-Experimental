/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.casetrain.taghandler;

import java.io.IOException;
import java.util.Collection;
import java.util.ResourceBundle;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMNotice;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.casetrain.type.MetaData;
import de.knowwe.casetrain.util.XMLUtils;


/**
 * Parses the WikiPage to XML via {@link XMLUtils}
 * 
 * @author Johannes Dienst
 * @created 30.05.2011
 */
public class ParseXMLAction extends AbstractAction {

	private final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	/**
	 * 
	 * @created 01.06.2011
	 * @param context
	 * @return
	 */
	private String perform(UserActionContext context) {

		StringBuilder buildi = new StringBuilder();

		String topic = context.getTopic();
		String web = context.getWeb();
		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(web, topic);
		Section<KnowWEArticle> sec = article.getSection();


		if (Sections.findSuccessor(sec, MetaData.class) == null) {
			buildi.append("<span class=\"error\">" +
					bundle.getString("NO_CASE_PAGE") +
			"</span>");
			return buildi.toString();
		}

		Collection<KDOMError> errors =
			KnowWEUtils.getMessagesFromSubtree(article, sec, KDOMError.class);
		Collection<KDOMWarning> warnings =
			KnowWEUtils.getMessagesFromSubtree(article, sec, KDOMWarning.class);
		Collection<KDOMNotice> notices =
			KnowWEUtils.getMessagesFromSubtree(article, sec, KDOMNotice.class);

		if (!errors.isEmpty()) {
			buildi.append("<span class=\"error\">" +
					bundle.getString("ERROR_PARSE") +
			"</span>");
			return buildi.toString();
		} else if (!warnings.isEmpty()) {
			buildi.append("<span class=\"warning\">" +
					bundle.getString("WARNING_PARSE") +
			"</span>");
		} else if (!notices.isEmpty()) {
			buildi.append("<span class=\"information\">" +
					bundle.getString("NOTICE_PARSE") +
			"</span>");
		} else {
			buildi.append("<span class=\"information\">" +
					bundle.getString("CORRECT_PARSE") +
			"</span>");
		}

		XMLUtils.createXMLWithBindings(article);

		return buildi.toString();
	}

}

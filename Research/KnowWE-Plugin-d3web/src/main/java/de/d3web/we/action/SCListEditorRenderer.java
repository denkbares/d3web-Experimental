/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.action;

import java.io.IOException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.WikiEnvironment;
import de.d3web.we.core.KnowWEAttributes;

public class SCListEditorRenderer extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {
		// String data = map.get(KnowWEAttributes.SCL_DATA);

		StringBuffer htmlBuffi = new StringBuffer();
		// htmlBuffi.append("<html><head></head><body>");
		renderQuestions(htmlBuffi, context);
		// htmlBuffi.append("</body></html>");

		return htmlBuffi.toString();
	}

	private void renderQuestions(StringBuffer htmlBuffi, UserActionContext context) {
		htmlBuffi.append(getQuestionStrings(context));
	}

	// private Term getTerm(DPSEnvironment dpse, String termName) {
	// Term result = null;
	// for (GlobalTerminology each : dpse.getTerminologyServer().getBroker()
	// .getGlobalTerminologies()) {
	// result = each.getTerm(termName, null);
	// if (result != null) {
	// return result;
	// }
	// }
	// return result;
	// }

	private StringBuffer getQuestionStrings(UserActionContext context) {

		StringBuffer sb = new StringBuffer();

		WikiEnvironment dpse = D3webModule.getDPSE(context.getParameters());
		String namespace = context.getParameter(KnowWEAttributes.SEMANO_NAMESPACE);
		// String type = map.get(KnowWEAttributes.SEMANO_TERM_TYPE);
		// String user = map.get(KnowWEAttributes.USER);

		KnowledgeBase base = dpse.getKnowledgeBase(namespace);
		if (namespace == null) {
			return null;
		}
		sb.append("<table>");
		QASet qaset = base.getRootQASet();
		int cnt = 0;
		int level = 0;
		renderQASet(qaset, sb, cnt, level);

		sb.append("<div>Einfügen</div>");

		sb.append("</table>");
		return sb;
	}

	private void renderQASet(QASet qaset, StringBuffer sb, int cnt, int level) {
		level++;
		String tab = "";
		for (int i = 0; i < level; i++) {
			tab += "&nbsp;&nbsp;";
		}
		if (qaset instanceof QASet && (!(qaset instanceof Question))) {

			sb.append(qaset.getName() + "<br>");
			for (Object q : qaset.getChildren()) {
				renderQASet((QASet) q, sb, ++cnt, level);

			}
		}

		if (qaset instanceof QuestionChoice) {
			sb
					.append("<span class=\"semLink\">"
							+ tab
							+ "<a onmouseout=\"return nd();\" onmouseover=\"return overlib('&lt;div class=&quot;semContents&quot; &gt;&lt;form action=&quot; javascript:void(); &quot; name=&quot;semanomcQ72&quot; id=&quot;semanomcQ72&quot; &gt;&lt;div align=left &gt;");
			for (Choice a : ((QuestionChoice) qaset).getAllAlternatives()) {
				sb.append("&lt;input type=\\'checkbox\\' name=\\'f0idmc"
						+ qaset.getId() + "\\' value=\\'" + a.getId()
						+ " \\' &gt;" + replaceHTML(a.getName())
						+ "&lt;br /&gt;");
			}

			sb
					.append("&lt;input type=\\'button\\' name=\\'submit\\' value=\\'ok\\' onclick=\\'javascript:readformSCLEdit(&quot;KnowWE.jsp?KWikiWeb=Sportberatung&action=SetFindingAction&namespace=Schwimmen%2E%2ESchwimmen%20%28im%20Verein%29&ObjectID=Q1&quot;,&quot;mc"
							+ qaset.getId()
							+ "&quot;,&quot;0&quot;)\\' &gt;&lt;/div&gt;&lt;/form &gt;&lt;/div &gt;',STICKY,MOUSEOFF,NOCLOSE,TEXTCOLOR, '#111111',FGCOLOR, '#fbf7e8',BGCOLOR, '#AA3311',CAPTION,'Medium',CAPCOLOR,'#FFFFFF');\" href=\"javascript:void(0);\">"
							+ replaceHTML(qaset.getName())
							+ "</a><div id=\"scledit"
							+ qaset.getId()
							+ "\"></div>");
			sb.append("</span><br>");
		}
		level--;
	}

	private String replaceHTML(String text) {
		text = text.replaceAll("\"", "&quot;");
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		return text;
	}
	// FL: commented out, cause it was never used
	// private IdentifiableInstance getII(DPSEnvironment dpse, String namespace,
	// Term term) {
	// IdentifiableInstance ii = null;
	// List<IdentifiableInstance> iis = dpse.getTerminologyServer()
	// .getBroker().getAlignedIdentifiableInstances(term,
	// TerminologyAlignmentLinkFilter.getInstance());
	// if (iis != null && !iis.isEmpty()) {
	// for (IdentifiableInstance instance : iis) {
	// if (instance.getNamespace().equals(namespace)) {
	// ii = instance;
	// }
	// }
	// }
	// return ii;
	// }

}

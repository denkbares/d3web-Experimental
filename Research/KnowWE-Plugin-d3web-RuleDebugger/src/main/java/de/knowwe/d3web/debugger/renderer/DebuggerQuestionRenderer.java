/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3web.debugger.renderer;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.strings.Strings;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.rendering.RenderResult;

/**
 * Get the HTML-output for debugging a question.
 * 
 * @author dupke
 */
public class DebuggerQuestionRenderer {

	/**
	 * Provides a border-layout and dropdown menu for the rendered question.
	 * 
	 * @param q The rendered question
	 * @return HTML-Rendering
	 */
	public static void renderQuestion(Question q, Session session, String title, boolean inside, RenderResult buffer) {
		String valueText;
		Value value = D3webUtils.getValueNonBlocking(session, q);
		String kbid = session.getKnowledgeBase().getId();
		if (value == null) valueText = "";
		else if (UndefinedValue.isUndefinedValue(value) || value.toString().equals("-?-")) valueText = "unknown";
		else valueText = value.toString();

		if (q instanceof QuestionOC) {
			buffer.appendHtml("<span class='debuggerQuestionOC'> ");
			buffer.append(q.getName());
			buffer.appendHtml("</span>");
		}
		else if (q instanceof QuestionMC) {
			buffer.appendHtml("<span class='debuggerQuestionMC'> ");
			buffer.append(q.getName());
			buffer.appendHtml("</span>");
		}
		else if (q instanceof QuestionNum) {
			buffer.appendHtml("<span class='debuggerQuestionNum'> ");
			buffer.append(q.getName());
			buffer.appendHtml("</span>");
		}
		else if (q instanceof QuestionText) {
			buffer.appendHtml("<span class='debuggerQuestionText'> ");
			buffer.append(q.getName());
			buffer.appendHtml("</span>");
		}
		// Build dropdownmenu
		buffer.appendHtml("<div class='debuggerDropdown' id='' style='display:none;'>");

		if (q instanceof QuestionOC) {
			renderQuestionOC((QuestionOC) q, valueText, kbid, title, inside, buffer);
		}
		else if (q instanceof QuestionMC) {
			renderQuestionMC((QuestionMC) q, valueText, kbid, title, inside, buffer);
		}
		else if (q instanceof QuestionNum) {
			renderQuestionNum((QuestionNum) q, valueText, kbid, title, inside, buffer);
		}
		else if (q instanceof QuestionText) {
			renderQuestionText((QuestionText) q, valueText, kbid, title, inside, buffer);
		}

		buffer.appendHtml("<a href='Wiki.jsp?page=ObjectInfoPage&objectname="
				+ Strings.encodeHtml(q.getName())
				+ "' class='dquestionLink'>Show Info Page</a>");

		buffer.appendHtml("</div>");

	}

	/**
	 * Render a OneChoice-Question.
	 */
	private static void renderQuestionOC(QuestionOC q, String valueText, String kbid, String title, boolean inside, RenderResult buffer) {
		String web = Environment.DEFAULT_WEB;
		for (Choice choice : q.getAllAlternatives()) {
			buffer.appendHtml("<p onClick='KNOWWE.plugin.debuggr.questionOCclicked(this, " + inside
					+ ")' ");
			buffer.appendHtml("rel=\"{kbid: '" + kbid + "', web:'" + web + "', ns:'" + title
					+ "', qid:'"
					+ Strings.encodeHtml(q.getName()) + "'}\" ");

			if (valueText.equals(choice.getName())) {
				buffer.appendHtml("class='dchoiceActive'");
			}
			else {
				buffer.appendHtml("class='dchoice'");
			}

			buffer.appendHtml(">");
			buffer.append(choice);
			buffer.appendHtml("</p>");
		}
	}

	/**
	 * Render a MultipleChoice-Question.
	 */
	private static void renderQuestionMC(QuestionMC q, String valueText, String kbid, String title, boolean inside, RenderResult buffer) {
		String web = Environment.DEFAULT_WEB;
		boolean active;
		String[] values = valueText.substring(1, valueText.length() - 1).split(", ");

		for (Choice choice : ((QuestionChoice) q).getAllAlternatives()) {
			buffer.appendHtml("<p onClick='KNOWWE.plugin.debuggr.questionMCclicked(this, " + inside
					+ ")' ");
			buffer.appendHtml("rel=\"{kbid: '" + kbid + "', web:'" + web + "', ns:'" + title
					+ "', qid:'"
					+ Strings.encodeHtml(q.getName()) + "'}\" ");

			active = false;
			for (String v : values) {
				if (v.equals(choice.getName())) active = true;
			}
			if (active) {
				buffer.appendHtml("class='dchoiceActive'");
			}
			else {
				buffer.appendHtml("class='dchoice'");
			}
			buffer.appendHtml(">");
			buffer.append(choice);
			buffer.appendHtml("</p>");
		}
	}

	/**
	 * Render a Numerical-Question.
	 */
	private static void renderQuestionNum(QuestionNum q, String valueText, String kbid, String title, boolean inside, RenderResult buffer) {
		String web = Environment.DEFAULT_WEB;
		String unit = "";
		Double rangeMin = Double.MAX_VALUE * -1;
		Double rangeMax = Double.MAX_VALUE;
		Object rangeValue = q.getInfoStore().getValue(BasicProperties.QUESTION_NUM_RANGE);
		if (rangeValue != null) {
			NumericalInterval range = (NumericalInterval) rangeValue;
			rangeMax = range.getRight();
			rangeMin = range.getLeft();
		}

		// Adding text-input-field and submit-button
		buffer.appendHtml("<p class='dQnum'>");
		buffer.appendHtml("<input value='" + valueText + "' type='text' size='15' />");
		buffer.appendHtml("<input type='button' value='O.K.' rel=\"{kbid: '" + kbid + "', web:'"
				+ web + "', ns:'" + title + "', qid:'"
				+ Strings.encodeHtml(q.getName()) + "', rangeMin:'" + rangeMin
				+ "', rangeMax:'" + rangeMax
				+ "'}\" onClick='KNOWWE.plugin.debuggr.questionNumClicked(this, " + inside
				+ ");' />");

		Object questionUnit = q.getInfoStore().getValue(MMInfo.UNIT);
		if (questionUnit != null) {
			unit = questionUnit.toString();
			buffer.appendHtml("<span class='dQnumUnit'>[" + unit + "]</span>");
		}
		buffer.appendHtml("<span class='dQnumError'></span></p>");

	}

	/**
	 * Render a Textual-Question.
	 */
	private static void renderQuestionText(QuestionText q, String valueText, String kbid, String title, boolean inside, RenderResult result) {

		String web = Environment.DEFAULT_WEB;

		result.appendHtml("<p class='dQtext'><input value='" + valueText
				+ "' type='text' size='20' />"
				+ "<input type='button' value='O.K.' rel=\"{kbid: '" + kbid + "', web:'"
				+ web + "', ns:'" + title + "', qid:'" + Strings.encodeHtml(q.getName())
				+ "'}\" "
				+ "onClick='KNOWWE.plugin.debuggr.questionTextClicked(this, " + inside
				+ ");' /></p>");
	}
}

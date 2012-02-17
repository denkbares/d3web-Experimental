/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
import de.knowwe.core.utils.KnowWEUtils;


/**
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
	public static String renderQuestion(Question q, Session session, String topic, String web) {
		StringBuffer buffer = new StringBuffer();
		String valueText;
		Value value = session.getBlackboard().getValue(q);
		String kbid = session.getKnowledgeBase().getId();
		if (UndefinedValue.isUndefinedValue(value) || value.toString().equals("-?-")) valueText = "unknown";
		else valueText = value.toString();

		if (q instanceof QuestionOC) {
			buffer.append("<span class='debuggerQuestionOC'><img src='KnowWEExtension/images/questionOC.gif' /> "
					+ q.getName() + "</span>");
		}
		else if (q instanceof QuestionMC) {
			buffer.append("<span class='debuggerQuestionMC'><img src='KnowWEExtension/images/questionMC.gif' /> "
					+ q.getName() + "</span>");
		}
		else if (q instanceof QuestionNum) {
			buffer.append("<span class='debuggerQuestionNum'><img src='KnowWEExtension/images/questionNum.gif' /> "
					+ q.getName() + "</span>");
		}
		else if (q instanceof QuestionText) {
			buffer.append("<span class='debuggerQuestionText'><img src='KnowWEExtension/images/questionText.gif' /> "
					+ q.getName() + "</span>");
		}
		// Build dropdownmenu
		buffer.append("<div class='debuggerDropdown' id='' style='display:none;'>");

		if (q instanceof QuestionOC) {
			buffer.append(renderQuestionOC((QuestionOC) q, valueText, web, topic, kbid));
		}
		else if (q instanceof QuestionMC) {
			buffer.append(renderQuestionMC((QuestionMC) q, valueText, web, topic, kbid));
		}
		else if (q instanceof QuestionNum) {
			buffer.append(renderQuestionNum((QuestionNum) q, valueText, web, topic, kbid));
		}
		else if (q instanceof QuestionText) {
			buffer.append(renderQuestionText((QuestionText) q, valueText, web, topic, kbid));
		}

		buffer.append("<a href='Wiki.jsp?page=ObjectInfoPage&objectname=" + q.getName()
				+ "' class='dquestionLink'>Show Info Page</a>");

		buffer.append("</div>");

		return KnowWEUtils.maskHTML(buffer.toString());
	}

	/**
	 * Render a OneChoice-Question.
	 * 
	 * @param q
	 * @param valueText
	 * @param web
	 * @param topic
	 * @return
	 */
	private static String renderQuestionOC(QuestionOC q, String valueText, String web, String topic, String kbid) {
		StringBuffer buffer = new StringBuffer();
		for (Choice choice : q.getAllAlternatives()) {
			buffer.append("<p onClick='KNOWWE.plugin.debuggr.questionOCclicked(this);return' ");
			buffer.append("rel=\"{kbid: '" + kbid + "', web:'" + web + "', ns:'" + topic
					+ "', qid:'" + q.getName()
					+ "'}\" ");

			if (valueText.equals(choice.getName())) {
				buffer.append("class='dchoiceActive'");
			}
			else {
				buffer.append("class='dchoice'");
			}

			buffer.append(">" + choice + "</p>");
		}
		return buffer.toString();
	}

	/**
	 * Render a MultipleChoice-Question.
	 * 
	 * @param q
	 * @param valueText
	 * @param web
	 * @param topic
	 * @return
	 */
	private static String renderQuestionMC(QuestionMC q, String valueText, String web, String topic, String kbid) {
		StringBuffer buffer = new StringBuffer();
		boolean active;
		String[] values = valueText.substring(1, valueText.length() - 1).split(", ");

		for (Choice choice : ((QuestionChoice) q).getAllAlternatives()) {
			buffer.append("<p onClick='KNOWWE.plugin.debuggr.questionMCclicked(this);return' ");
			buffer.append("rel=\"{kbid: '" + kbid + "', web:'" + web + "', ns:'" + topic
					+ "', qid:'" + q.getName()
					+ "'}\" ");

			active = false;
			for (String v : values) {
				if (v.equals(choice.getName())) active = true;
			}
			if (active) {
				buffer.append("class='dchoiceActive'");
			}
			else {
				buffer.append("class='dchoice'");
			}
			buffer.append(">" + choice + "</p>");
		}
		return buffer.toString();
	}

	/**
	 * Render a Numerical-Question.
	 * 
	 * @param q
	 * @param valueText
	 * @param web
	 * @param topic
	 * @return
	 */
	private static String renderQuestionNum(QuestionNum q, String valueText, String web, String topic, String kbid) {
		StringBuffer buffer = new StringBuffer();
		String unit = "";
		Double rangeMin = Double.MIN_VALUE;
		Double rangeMax = Double.MAX_VALUE;
		Object rangeValue = q.getInfoStore().getValue(BasicProperties.QUESTION_NUM_RANGE);
		if (rangeValue != null) {
			NumericalInterval range = (NumericalInterval) rangeValue;
			rangeMax = range.getRight();
			rangeMin = range.getLeft();
		}

		// Adding text-input-field and submit-button
		buffer.append("<p class='dQnum'>");
		buffer.append("<input value='" + valueText + "' type='text' size='15' />");
		buffer.append("<input type='button' value='O.K.' rel=\"{kbid: '" + kbid + "', web:'"
				+ web + "', ns:'" + topic + "', qid:'"
				+ q.getName() + "', rangeMin:'" + rangeMin
				+ "', rangeMax:'" + rangeMax
				+ "'}\" onClick='KNOWWE.plugin.debuggr.questionNumClicked(this);return false' />");

		Object questionUnit = q.getInfoStore().getValue(MMInfo.UNIT);
		if (questionUnit != null) {
			unit = questionUnit.toString();
			buffer.append("<span class='dQnumUnit'>[" + unit + "]</span>");
		}
		buffer.append("<span class='dQnumError'></span></p>");

		return buffer.toString();
	}

	/**
	 * Render a Textual-Question.
	 * 
	 * @param q
	 * @param valueText
	 * @param web
	 * @param topic
	 * @return
	 */
	private static String renderQuestionText(QuestionText q, String valueText, String web, String topic, String kbid) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("<p class='dQtext'><input value='" + valueText
				+ "' type='text' size='20' />"
				+ "<input type='button' value='O.K.' rel=\"{kbid: '" + kbid + "', web:'"
				+ web + "', ns:'" + topic + "', qid:'" + q.getName() + "'}\" "
				+ "onClick='KNOWWE.plugin.debuggr.questionTextClicked(this);return false' />"
				+ "</p>");

		return buffer.toString();
	}
}

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.we.basic.D3webModule;

/**
 * Generates the XML representation of a Question Object
 * 
 * @author Michael Scharvogel
 */
public class FindingXMLWriter {

	public static final String ID = FindingXMLWriter.class.getName();

	private static ResourceBundle rb;

	private void appendAnswers(Question theQuestion, StringBuffer sb, Session session) {
		sb.append("<Answers>\n");
		if (theQuestion instanceof QuestionChoice) {
			QuestionChoice theQC = (QuestionChoice) theQuestion;
			if (theQC.getAllAlternatives() != null) {
				for (Choice each : theQC.getAllAlternatives()) {
					appendAnswer(theQuestion, sb, new ChoiceValue(each), session);
				}
				appendAnswer(theQuestion, sb, Unknown.getInstance(), session);
			}
		}
		if (theQuestion instanceof QuestionNum) {
			if (session != null) {
				NumValue answer = (NumValue) session.getBlackboard().getValue(theQuestion);
				if (answer != null) {
					appendAnswer(theQuestion, sb, answer, session);
				}
			}
			else {
				appendAnswer(theQuestion, sb, new NumValue(0), session);
			}
		}
		sb.append("</Answers>\n");
	}

	private void appendAnswer(Question theQuestion, StringBuffer sb, Value theAnswer, Session session) {
		String theID = theAnswer.getValue().toString();
		if (theAnswer instanceof ChoiceValue) {
			theID = ((ChoiceValue) theAnswer).getAnswerChoiceID();
		}
		else if (theAnswer instanceof MultipleChoiceValue) {
			theID = ChoiceID.encodeChoiceIDs(((MultipleChoiceValue) theAnswer).getChoiceIDs());
		}

		sb.append("<Answer ID='" + theID + "'");
		// sb.append("<Answer ID='" + theAnswer.getId() + "'");
		// joba, 04.2010, yes/no should be replaced by standard choice values
		// if (theAnswer instanceof AnswerNo) {
		// sb.append(" type='AnswerNo'");
		// } else if (theAnswer instanceof AnswerYes) {
		// sb.append(" type='AnswerYes'");
		// } else
		if (theAnswer instanceof Unknown) {
			sb.append(" type='AnswerUnknown'");
		}
		else if (theAnswer instanceof NumValue) {
			sb.append(" type='AnswerNum'");
		}
		else {
			sb.append(" type='AnswerChoice'");
		}
		if (session != null && session.getBlackboard().getValue(theQuestion).equals(theAnswer)) {
			sb.append(" active='true'");
		}
		sb.append(">\n");
		String answerText = theAnswer.getValue().toString();
		if (theAnswer instanceof Unknown) {
			answerText = rb.getString("KnowWE.answer.unknown");
		}
		String text = "";
		try {
			text = URLEncoder.encode(answerText, "ISO-8859-1");
		}
		catch (UnsupportedEncodingException e) {
		}
		sb.append("<Text><![CDATA[" + text + "]]></Text>\n");
		sb.append("</Answer>\n");
	}

	private String getXMLString(Question theQuestion, String type, Session session) {
		StringBuffer sb = new StringBuffer();
		String questionID = theQuestion.getId();
		sb.append(
				"<Question ID='"
						+ questionID
						+ "' type='"
						+ type + "'");
		sb.append(">\n");
		String text = "";
		try {
			text = URLEncoder.encode(theQuestion.getName(), "ISO-8859-1");
		}
		catch (UnsupportedEncodingException e) {
		}
		sb.append("<Text><![CDATA[" + text + "]]></Text>\n");
		appendAnswers(theQuestion, sb, session);

		sb.append("</Question>\n");
		return sb.toString();
	}

	public String getXMLString(Question question, Session session) {

		rb = D3webModule.getKwikiBundle_d3web();
		String retVal = null;
		if (question == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no Question");
		}
		else {
			if (question instanceof QuestionYN) {
				retVal = getXMLString(question, "YN", session);
			}
			else if (question instanceof QuestionOC) {
				retVal = getXMLString(question, "OC", session);
			}
			else if (question instanceof QuestionMC) {
				retVal = getXMLString(question, "MC", session);
			}
			else if (question instanceof QuestionNum) {
				retVal = getXMLString(question, "Num", session);
			} /*
			 * else if (question instanceof QuestionText) { retVal =
			 * getXMLString((Question) question, "Text", session); } else if
			 * (question instanceof QuestionDate) { retVal =
			 * getXMLString((Question) question, "Date", session); }
			 */
		}
		return retVal;
	}
}
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
package de.knowwe.defi.feedback;

import java.io.File;
import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.user.UserContext;

/**
 * 
 * @author Stefan Mark
 * @created 22.09.2011
 */
public class FeedbackTagHandler extends AbstractHTMLTagHandler {

	private int questionNum = 1;

	/**
	 * @param name
	 */
	public FeedbackTagHandler() {
		super("defifeedback");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		questionNum = 1;

		StringBuilder html = new StringBuilder();
		if (!user.userIsAsserted()) {
			return "<p class=\"info\">Bitte melden Sie sich an, um die Seite sehen zu können!</p>";
		}

		html.append("<h1>Abschluss-Fragebogen</h1>");

		html.append("Bei den folgenden Fragen geht es um Ihr Resümee des Programms und der Plattform im "
				+ "Allgemeinen und auf bestimmte Teilaspekte bezogen. "
				+ "Bitte Antworten Sie so ehrlich wie möglich und seien Sie ruhig kritisch.");

		String path = KnowWEEnvironment.getInstance().getWikiConnector().getSavePath();
		String filename = user.getUserName().toLowerCase() + ".xml";
		File f = new File(path + filename);

		// render save button or note that feedback already found
		if (f.exists()) {
			html.append("<p class=\"info\">Sie haben den Feedbackbogen bereits ausgefüllt! Vielen Dank!</p>");
		}

		html.append("<form action=\"KnowWE.jsp\" method=\"post\">");

		// render the feedback form
		StringBuilder q = new StringBuilder();
		q.append("<p class=\"header2\">Allgemeines</p>");
		renderQuestion1(q);
		q.append("<br />");
		renderQuestion2(q);
		q.append("<br />");
		renderQuestion3(q);
		q.append("<br />");
		renderQuestion4(q);

		q.append("<p class=\"header2\">Zu den Inhalten</p>");
		renderQuestion5(q);
		q.append("<br />");
		renderQuestion6(q);
		q.append("<br />");
		renderQuestion7(q);
		q.append("<br />");
		renderQuestion8(q);

		q.append("<p class=\"header2\">Zum Diskussionsforum</p>");
		renderQuestion9(q);
		q.append("<br />");
		renderQuestion10(q);
		q.append("<br />");
		renderQuestion11(q);
		q.append("<br />");
		renderQuestion12(q);
		q.append("<br />");
		renderQuestion13(q);
		q.append("<br />");
		renderQuestion14(q);
		q.append("<br />");
		renderQuestion15(q);
		q.append("<br />");
		renderQuestion16(q);
		q.append("<br />");
		renderQuestion17(q);
		q.append("<br />");

		q.append("<p class=\"header2\">zu Ihren Aktivitäten</p>");
		renderQuestion18(q);
		q.append("<br />");
		renderQuestion19(q);
		q.append("<br />");
		renderQuestion20(q);
		q.append("<br />");
		renderQuestion21(q);
		q.append("<br />");
		renderQuestion22(q);
		q.append("<br />");
		renderQuestion23(q);
		q.append("<br />");
		renderQuestion24(q);
		q.append("<br />");
		renderQuestion25(q);
		q.append("<br />");
		renderQuestion26(q);
		q.append("<br />");
		html.append(q);

		if (!f.exists()) {
			html.append("<p><input type=\"submit\" value=\"Speichern\"/></p>");
			html.append("<input type=\"hidden\" name=\"action\" value=\"FeedbackSaveAction\" />");
			html.append("<input type=\"hidden\" name=\"KWiki_Topic\" value=\""
					+ topic + "\" />");
		}

		html.append("</form>");

		return html.toString();
	}

	private void renderQuestion1(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Die Teilnahme am ICD Forum hat mir insgesamt weitergeholfen.");
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") Die Teilnahme am ICD Forum hat mir insgesamt weitergeholfen.\" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion2(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") ICD-Forum hat mir geholfen, meinen Alltag besser zu bewältigen.");
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") ICD-Forum hat mir geholfen, meinen Alltag besser zu bewältigen. \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion3(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") ICD-Forum hat mich motiviert, neue Dinge auszuprobieren.");
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") ICD-Forum hat mich motiviert, neue Dinge auszuprobieren.\"/>");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion4(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Durch die Teilnahme am ICD-Forum, komme ich mit meiner Erkrankung besser zurecht. ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Durch die Teilnahme am ICD-Forum, komme ich mit meiner Erkrankung besser zurecht. \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion5(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"4\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Welche Themen waren für Sie besonders wichtig?  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Welche Themen waren für Sie besonders wichtig?  \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createDoubleHeader("Einheit 1 - Kennenlernen", "QFB" + questionNum + "-1",
				"Einheit 4 - Angst", "QFB" + questionNum + "-4"));

		result.append(createDoubleCheckBox("Was erwartet mich im ICD-Forum", radioID + "-1-1",
				"Angst verstehen", radioID + "-4-1"));
		result.append(createDoubleCheckBox("Unser Team", radioID + "-1-2", "Wie sich Angst zeigt",
				radioID + "-4-2"));
		result.append(createDoubleCheckBox("Tipps und Anregungen", radioID + "-1-3",
				"Die eigenen Ängste kennen lernen", radioID + "-4-3"));
		result.append(createDoubleCheckBox("Unser Sicherheitskonzept", radioID + "-1-4",
				"Die Angst vor der Sterblichkeit", radioID + "-4-4"));

		result.append(createDoubleHeader("Einheit 2 - Wissen über den ICD", "QFB" + questionNum
				+ "-2",
				"Einheit 5 - Probleme anpacken", "QFB" + questionNum + "-5"));

		result.append(createDoubleCheckBox("Wie funktioniert mein ICD?", radioID + "-2-1",
				"Angst überwinden", radioID + "-5-1"));
		result.append(createDoubleCheckBox("Was stört meinen ICD?", radioID + "-2-2",
				"Ziele erreichen", radioID + "-5-2"));
		result.append(createDoubleCheckBox("Den richtigen Rhythmus finden", radioID + "-2-3",
				"Gedankenmuster durchbrechen", radioID + "-5-3"));
		result.append(createDoubleCheckBox("Der ICD löst aus - was tun!?", radioID + "-2-4",
				"Stress reduzieren", radioID + "-5-4"));
		result.append(createDoubleCheckBox("Kleine und große Reisen mit dem ICD", radioID + "-2-5",
				"Wenn Selbsthilfe nicht mehr reicht", radioID + "-5-5"));
		result.append(createDoubleCheckBox("Wo finde ich weitere Informationen?", radioID + "-2-6",
				"", ""));

		result.append(createDoubleHeader("Einheit 3 - Alltag gestalten",
				"QFB" + questionNum + "-3",
				"Einheit 6 - Meine Zukunft mit dem ICD", "QFB" + questionNum + "-6"));

		result.append(createDoubleCheckBox("Der ICD als Teil von mir.", radioID + "-3-1",
				"Das Ende ist erst der Anfang.", radioID + "-6-1"));
		result.append(createDoubleCheckBox("Sich vom ICD nicht unterkriegen lassen.", radioID
				+ "-3-2", "Resümee", radioID + "-6-2"));
		result.append(createDoubleCheckBox("Körperliche Aktivität", radioID + "-3-3", "", radioID
				+ ""));
		result.append(createDoubleCheckBox("Mein soziales Netz", radioID + "-3-4", "", radioID + ""));
		result.append(createDoubleCheckBox("Mein Partner versteht mich einfach nicht.", radioID
				+ "-3-5", "", ""));
		result.append(createDoubleCheckBox("Intimität", radioID + "-3-6", "", ""));

		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion6(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"4\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Welche Themen waren für Sie besonders wichtig?  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Welche Themen waren für Sie besonders wichtig?  \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createDoubleHeader("Einheit 1 - Kennenlernen", "QFB" + questionNum + "-1",
				"Einheit 4 - Angst", "QFB" + questionNum + "-4"));

		result.append(createDoubleCheckBox("Was erwartet mich im ICD-Forum", radioID + "-1-1",
				"Angst verstehen", radioID + "-4-1"));
		result.append(createDoubleCheckBox("Unser Team", radioID + "-1-2", "Wie sich Angst zeigt",
				radioID + "-4-2"));
		result.append(createDoubleCheckBox("Tipps und Anregungen", radioID + "-1-3",
				"Die eigenen Ängste kennen lernen", radioID + "-4-3"));
		result.append(createDoubleCheckBox("Unser Sicherheitskonzept", radioID + "-1-4",
				"Die Angst vor der Sterblichkeit", radioID + "-4-4"));

		result.append(createDoubleHeader("Einheit 2 - Wissen über den ICD", "QFB" + questionNum
				+ "-2",
				"Einheit 5 - Probleme anpacken", "QFB" + questionNum + "-5"));

		result.append(createDoubleCheckBox("Wie funktioniert mein ICD?", radioID + "-2-1",
				"Angst überwinden", radioID + "-5-1"));
		result.append(createDoubleCheckBox("Was stört meinen ICD?", radioID + "-2-2",
				"Ziele erreichen", radioID + "-5-2"));
		result.append(createDoubleCheckBox("Den richtigen Rhythmus finden", radioID + "-2-3",
				"Gedankenmuster durchbrechen", radioID + "-5-3"));
		result.append(createDoubleCheckBox("Der ICD löst aus - was tun!?", radioID + "-2-4",
				"Stress reduzieren", radioID + "-5-4"));
		result.append(createDoubleCheckBox("Kleine und große Reisen mit dem ICD", radioID + "-2-5",
				"Wenn Selbsthilfe nicht mehr reicht", radioID + "-5-5"));
		result.append(createDoubleCheckBox("Wo finde ich weitere Informationen?", radioID + "-2-6",
				"", ""));

		result.append(createDoubleHeader("Einheit 3 - Alltag gestalten",
				"QFB" + questionNum + "-3",
				"Einheit 6 - Meine Zukunft mit dem ICD", "QFB" + questionNum + "-6"));

		result.append(createDoubleCheckBox("Der ICD als Teil von mir.", radioID + "-3-1",
				"Das Ende ist erst der Anfang.", radioID + "-6-1"));
		result.append(createDoubleCheckBox("Sich vom ICD nicht unterkriegen lassen.", radioID
				+ "-3-2", "Resümee", radioID + "-6-2"));
		result.append(createDoubleCheckBox("Körperliche Aktivität", radioID + "-3-3", "", radioID
				+ ""));
		result.append(createDoubleCheckBox("Mein soziales Netz", radioID + "-3-4", "", radioID + ""));
		result.append(createDoubleCheckBox("Mein Partner versteht mich einfach nicht.", radioID
				+ "-3-5", "", ""));
		result.append(createDoubleCheckBox("Intimität", radioID + "-3-6", "", ""));

		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion7(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Wie haben Sie den Gesamtumfang des Programms empfunden? ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Wie haben Sie den Gesamtumfang des Programms empfunden?  \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createCheckBox(
				"zu wenig - ich hätte mir die Inhalte ausführlicher gewünscht. ", radioID));
		result.append(createCheckBox(
				"zu wenig - ich hätte mir mehr zusätzliche Inhalte gewünscht ", radioID));
		result.append(createCheckBox("zu viel", radioID));
		result.append(createCheckBox("genau passen", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion8(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Was haben Sie bei unserem inhaltlichen Angebot vermisst?   ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Was haben Sie bei unserem inhaltlichen Angebot vermisst?   \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createTextArea(radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion9(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Haben Sie das Diskussionsforum genutzt?  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Haben Sie das Diskussionsforum genutzt?\" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Nein", radioID));
		result.append(createRadioButton("Ja, ich habe mitgelesen ", radioID));
		result.append(createRadioButton("Ja, ich habe mitgelesen und eigene Beiträge geschrieben ",
				radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion10(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Es hat mir gut getan, im Diskussionsforum mitzulesen.");
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") Es hat mir gut getan, im Diskussionsforum mitzulesen.\"/>");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Ich habe im Diskussionsforum nicht mitgelesen. ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion11(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Ich habe den Moderator im Diskussionsforum als hilfreich empfunden ");
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") Ich habe den Moderator im Diskussionsforum als hilfreich empfunden\" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Kann ich nicht beurteilen. ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion12(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Es hat mir gut getan, im Diskussionsforum Fragen zu stellen  ");
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") Es hat mir gut getan, im Diskussionsforum Fragen zu stellen\"  />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton(
				"Ich habe im Diskussionsforum keine eigenen Fragen gestellt. ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion13(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Es hat mir gut getan, im Diskussionsforum Antworten auf meine Fragen zu erhalten");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Es hat mir gut getan, im Diskussionsforum Antworten auf meine Fragen zu erhalten\" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton(
				"Ich habe im Diskussionsforum Antworten auf meine Fragen erhalten. ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion14(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Es hat mir gut getan, im Diskussionsforum auf die Fragen anderer antworten zu können. ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Es hat mir gut getan, im Diskussionsforum auf die Fragen anderer antworten zu können. \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton(
				"Ich habe im Diskussionsforum nicht auf die Fragen anderer geantwortet. ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion15(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Ich habe den Eindruck, dass der Moderator im Diskussionsforum an meinen Fragen und Problemen interessiert war.");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Ich habe den Eindruck, dass der Moderator im Diskussionsforum an meinen Fragen und Problemen interessiert war.\" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Weiß nicht ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion16(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Ich habe den Eindruck, dass die Teilnehmer an meinen Fragen und Problemen wirklich interessiert waren.");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Ich habe den Eindruck, dass die Teilnehmer an meinen Fragen und Problemen wirklich interessiert waren.\" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Weiß nicht ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion17(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Was haben Sie im Diskussionsforum vermisst? ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Was haben Sie im Diskussionsforum vermisst? \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createTextArea(radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion18(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Ich wusste, an wen ich mich bei ICD-Forum mit meinen persönlichen Fragen wenden kann? ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Ich wusste, an wen ich mich bei ICD-Forum mit meinen persönlichen Fragen wenden kann? \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion19(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") ICD-Forum hat mir einen vertrauensvollen Rahmen geboten, in dem ich mich über die Themen austauschen konnte, die mir wirklich wichtig sind.  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") ICD-Forum hat mir einen vertrauensvollen Rahmen geboten, in dem ich mich über die Themen austauschen konnte, die mir wirklich wichtig sind.  \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion20(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Wieviel Zeit haben Sie auf ICD-Forum verbracht?");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Wieviel Zeit haben Sie auf ICD-Forum verbracht?\" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Ich war (fast) jeden Tag hier.", radioID));
		result.append(createRadioButton("Mehrmals die Woche", radioID));
		result.append(createRadioButton("1-2 Mal pro Woche.", radioID));
		result.append(createRadioButton("seltener", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion21(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") In welchen Bereichen von ICD-Forum haben Sie mehr Zeit verbracht? ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") In welchen Bereichen von ICD-Forum haben Sie mehr Zeit verbracht? \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("in den Inhalten", radioID));
		result.append(createRadioButton("im Forum", radioID));
		result.append(createRadioButton("ausgeglichen ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion22(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Wie haben Sie die Dauer des Programms empfunden? 6 Wochen waren... ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Wie haben Sie die Dauer des Programms empfunden? 6 Wochen waren... \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("...zu kurz.", radioID));
		result.append(createRadioButton("...zu lang.", radioID));
		result.append(createRadioButton("...genau passend. ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion23(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Wie fanden Sie die Bedienbarkeit von ICD-Forum?  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Wie fanden Sie die Bedienbarkeit von ICD-Forum?  \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton(
				"Ich konnte mich schnell hier \"einleben\" und fand mich gut zurecht.", radioID));
		result.append(createRadioButton(
				"Ich habe einige Zeit gebraucht, bis ich mich zurecht fand, dann ging es aber gut.",
				radioID));
		result.append(createRadioButton(
				"Ich hatte immer wieder Schwierigkeiten bei der Benutzung der Plattform.", radioID));
		result.append(createRadioButton("Die Bedienung war sehr schwierig.", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion24(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Wenn Sie Schwierigkeiten bei der Bedienung hatten - Woran lag das?  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Wenn Sie Schwierigkeiten bei der Bedienung hatten - Woran lag das? \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createTextArea(radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion25(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Würden Sie in Zukunft an einem ähnlichen Angebot teilnehmen?   ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Würden Sie in Zukunft an einem ähnlichen Angebot teilnehmen?  \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;

		result.append(createRadioButton("Ich würde prinzipiell an einem neuen Angebot teilnehmen.",
				radioID));
		result.append(createRadioButton(
				"Ich würde teilnehmen, wenn Verbesserungen am ICD-Forum stattfinden (bitte auswählen) ",
				radioID));

		result.append(createCheckBox(
				"Ich würde mir ein verbessertes Angebot mit einer stärkeren Beteiligung von Kardiologen und des ICD-Herstellers wünschen.",
				radioID + "-1"));
		result.append(createCheckBox(
				"Ich würde mir ein fortlaufendes Angebot wünschen, so dass sich mit der Zeit immer mehr Teilnehmer im Forum einbringen können. ",
				radioID + "-2"));
		result.append(createCheckBox(
				"Ich würde mir ein verbessertes Angebot mit den folgenden Schwerpunkten/Zusätzen wünschen (bitte Art der Verbesserung angeben): ",
				radioID + "-3"));
		result.append(createTextArea(radioID + "-4"));

		result.append(createRadioButton("Nein, ich habe kein Interesse. ", radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private void renderQuestion26(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") Haben Sie sonst noch Wünsche, Kritik oder Erfahrungen, die Sie uns mitteilen möchten?  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") Haben Sie sonst noch Wünsche, Kritik oder Erfahrungen, die Sie uns mitteilen möchten?  \" />");
		result.append("</td></tr>");

		String radioID = "FB" + questionNum;
		result.append(createTextArea(radioID));
		result.append("</tbody></table>");
		questionNum++;
	}

	private String createTextArea(String radioID) {
		return "<tr><td colspan=\"2\"><textarea style=\"font-size:100%;\"rows=\"6\" cols=\"70\" name=\""
				+ radioID
				+ "\"></textarea></td></tr>";
	}

	private String createRadioButton(String answer, String radioID) {
		return "<tr><td class=\"radio\"><input type=\"radio\" name=\"" + radioID + "\" value=\""
				+ answer
				+ "\">"
				+ "</td>"
				+ "<td class=\"answer\">"
				+ answer
				+ "</td></tr>";
	}

	private String createCheckBox(String answer, String radioID) {
		return "<tr><td class=\"radio\"><input type=\"checkbox\" name=\"" + radioID + "\" value=\""
				+ answer
				+ "\">"
				+ "</td>"
				+ "<td class=\"answer\">"
				+ answer
				+ "</td></tr>";
	}

	private String createDoubleCheckBox(String answer1, String radioID1, String answer2, String radioID2) {

		StringBuilder tmp = new StringBuilder();

		tmp.append("<tr><td class=\"radio\">");

		if (answer1 != "") {
			tmp.append("<input type=\"checkbox\" name=\"" + radioID1
					+ "\" value=\""
					+ answer1
					+ "\">");
		}
		tmp.append("</td>"
				+ "<td class=\"answer2\">"
				+ answer1
				+ "</td><td class=\"radio\">");

		if (answer2 != "") {
			tmp.append("<input type=\"checkbox\" name=\"" + radioID2
					+ "\" value=\""
					+ answer2
					+ "\">");
		}
		tmp.append("</td>"
				+ "<td class=\"answer2\">"
				+ answer2
				+ "</td></tr>");
		return tmp.toString();
	}

	private String createDoubleHeader(String head1, String qfb1, String head2, String qfb2) {

		return "<td colspan=\"2\" class=\"rowhead2\">"
				+ head1
				+ "<input type=\"hidden\" name=\"" + qfb1 + "\" value=\"" + head1 + "\" />"
				+ "</td>"
				+ "<td colspan=\"2\" class=\"rowhead2\">"
				+ head2
				+ "<input type=\"hidden\" name=\"" + qfb2 + "\" value=\"" + head2 + "\" />"
				+ "</td></tr>";
	}
}

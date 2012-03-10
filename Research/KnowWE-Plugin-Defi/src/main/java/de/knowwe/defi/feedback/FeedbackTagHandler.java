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

import de.knowwe.core.Environment;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * 
 * @author Stefan Mark
 * @created 22.09.2011
 */
public class FeedbackTagHandler extends AbstractHTMLTagHandler {

	private int questionNum = 1;

	public static final String FRAGE1 = "Die Teilnahme am ICD Forum hat mir insgesamt weitergeholfen.";
	public static final String FRAGE2 = "ICD-Forum hat mir geholfen, meinen Alltag besser zu bewältigen.";
	public static final String FRAGE3 = "ICD-Forum hat mich motiviert, neue Dinge auszuprobieren.";
	public static final String FRAGE4 = "Durch die Teilnahme am ICD-Forum, komme ich mit meiner Erkrankung besser zurecht.";
	public static final String FRAGE5 = "Welche Themen waren für Sie besonders wichtig?";
	public static final String FRAGE6 = "Wie gut haben Ihnen die Themen in den einzelnen Bereichen geholfen?";
	public static final String FRAGE7 = "Wie haben Sie den Gesamtumfang des Programms empfunden?";
	public static final String FRAGE8 = "Was haben Sie bei unserem inhaltlichen Angebot vermisst?";
	public static final String FRAGE9 = "Haben Sie das Diskussionsforum genutzt?";
	public static final String FRAGE10 = "Es hat mir gut getan, im Diskussionsforum mitzulesen.";
	public static final String FRAGE11 = "Ich habe den Moderator im Diskussionsforum als hilfreich empfunden.";
	public static final String FRAGE12 = "Es hat mir gut getan, im Diskussionsforum Fragen zu stellen.";
	public static final String FRAGE13 = "Es hat mir gut getan, im Diskussionsforum Antworten auf meine Fragen zu erhalten.";
	public static final String FRAGE14 = "Es hat mir gut getan, im Diskussionsforum auf die Fragen anderer antworten zu können.";
	public static final String FRAGE15 = "Ich habe den Eindruck, dass der Moderator im Diskussionsforum an meinen Fragen und Problemen interessiert war.";
	public static final String FRAGE16 = "Ich habe den Eindruck, dass die Teilnehmer an meinen Fragen und Problemen wirklich interessiert waren.";
	public static final String FRAGE17 = "Was haben Sie im Diskussionsforum vermisst?";
	public static final String FRAGE18 = "Ich wusste, an wen ich mich bei ICD-Forum mit meinen persönlichen Fragen wenden kann.";
	public static final String FRAGE19 = "ICD-Forum hat mir einen vertrauensvollen Rahmen geboten, in dem ich mich über die Themen austauschen konnte, die mir wirklich wichtig sind.";
	public static final String FRAGE20 = "Wieviel Zeit haben Sie auf ICD-Forum verbracht?";
	public static final String FRAGE21 = "In welchen Bereichen von ICD-Forum haben Sie mehr Zeit verbracht?";
	public static final String FRAGE22 = "Wie haben Sie die Dauer des Programms empfunden? 6 Wochen waren...";
	public static final String FRAGE23 = "Wie fanden Sie die Bedienbarkeit von ICD-Forum?";
	public static final String FRAGE24 = "Wenn Sie Schwierigkeiten bei der Bedienung hatten - Woran lag das?";
	public static final String FRAGE25 = "Würden Sie in Zukunft an einem ähnlichen Angebot teilnehmen?";
	public static final String FRAGE26 = "Haben Sie sonst noch Wünsche, Kritik oder Erfahrungen, die Sie uns mitteilen möchten?";

	private static final String HINT_MUTIPLE = "(Bitte alle zutreffenden Antworten markieren!)";

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

		// html.append("<h1>Abschluss-Fragebogen</h1>");
		// html.append("Bei den folgenden Fragen geht es um Ihr Resümee des Programms und der Plattform im "
		// + "Allgemeinen und auf bestimmte Teilaspekte bezogen. "
		// +
		// "Bitte Antworten Sie so ehrlich wie möglich und seien Sie ruhig kritisch.");

		String path = Environment.getInstance().getWikiConnector().getSavePath();
		String filename = user.getUserName().toLowerCase() + ".xml";
		File f = new File(path + filename);

		// render save button or note that feedback already found
		if (f.exists()) {
			html.append("<p class=\"info\">Sie haben den Feedbackbogen bereits ausgefüllt! Vielen Dank!</p>\n");
		}
		else {

			html.append("<form action=\"KnowWE.jsp\" method=\"post\">\n");

			// render the feedback form
			StringBuilder q = new StringBuilder();
			q.append("<p class=\"header2\">Allgemeines</p>\n");
			renderQuestion1(q);
			q.append("<br />");
			renderQuestion2(q);
			q.append("<br />");
			renderQuestion3(q);
			q.append("<br />");
			renderQuestion4(q);

			q.append("<p class=\"header2\">Zu den Inhalten</p>\n");
			renderQuestion5(q);
			q.append("<br />");
			renderQuestion6(q);
			q.append("<br />");
			renderQuestion7(q);
			q.append("<br />");
			renderQuestion8(q);

			q.append("<p class=\"header2\">Zum Diskussionsforum</p>\n");
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

			q.append("<p class=\"header2\">zu Ihren Aktivitäten</p>\n");
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

			html.append("<p><input type=\"submit\" value=\"Speichern\"/></p>\n");
			html.append("<input type=\"hidden\" name=\"action\" value=\"FeedbackSaveAction\" />");
			html.append("<input type=\"hidden\" name=\"KWiki_Topic\" value=\""
					+ topic + "\" />");

			html.append("</form>");
		}
		return html.toString();
	}

	private void renderQuestion1(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE1);
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") " + FRAGE1 + "\" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion2(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE2);
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") " + FRAGE2 + " \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion3(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE3);
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") " + FRAGE3 + "\"/>");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion4(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE4);
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE4 + " \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion5(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"4\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE5 + "<br><b><i>" + HINT_MUTIPLE + "</i></b>");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE5 + "  \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createDoubleHeader("Einheit 1 - Kennenlernen", "QFB" + questionNum
				+ "-1",
				"Einheit 4 - Angst", "QFB" + questionNum + "-4"));

		result.append(createDoubleCheckBox("Was erwartet mich im ICD-Forum", radioID
				+ "-1-1",
				"Angst verstehen", radioID + "-4-1"));
		result.append(createDoubleCheckBox("Wer steckt hinter ICD Forum?", radioID
				+ "-1-2", "Wie sich Angst zeigt",
				radioID + "-4-2"));
		result.append(createDoubleCheckBox("Tipps und Anregungen", radioID + "-1-3",
				"Die eigenen Ängste kennen lernen", radioID + "-4-3"));
		result.append(createDoubleCheckBox("Unser Sicherheitskonzept", radioID + "-1-4",
				"Die Angst vor der Sterblichkeit", radioID + "-4-4"));

		result.append(createDoubleHeader("Einheit 2 - Wissen über den ICD", "QFB"
				+ questionNum
				+ "-2",
				"Einheit 5", "QFB" + questionNum + "-5"));

		result.append(createDoubleCheckBox("Wie funktioniert mein ICD?",
				radioID + "-2-1",
				"Angst überwinden", radioID + "-5-1"));
		result.append(createDoubleCheckBox("Was stört meinen ICD?", radioID + "-2-2",
				"Ziele erreichen", radioID + "-5-2"));
		result.append(createDoubleCheckBox("Den richtigen Rhythmus finden", radioID
				+ "-2-3",
				"Gedankenmuster durchbrechen", radioID + "-5-3"));
		result.append(createDoubleCheckBox("Der ICD löst aus - was tun!?", radioID
				+ "-2-4",
				"Stress reduzieren", radioID + "-5-4"));
		result.append(createDoubleCheckBox("Kleine und große Reisen mit dem ICD", radioID
				+ "-2-5",
				"Wenn Selbsthilfe nicht mehr reicht", radioID + "-5-5"));
		result.append(createDoubleCheckBox("Wo finde ich weitere Informationen?", radioID
				+ "-2-6",
				"", ""));

		result.append(createDoubleHeader("Einheit 3 - Alltag gestalten",
				"QFB" + questionNum + "-3",
				"Einheit 6 - Meine Zukunft mit dem ICD", "QFB" + questionNum + "-6"));

		result.append(createDoubleCheckBox("Der ICD als Teil von mir.", radioID + "-3-1",
				"Was kann ich für mich mitnehmen?", radioID + "-6-1"));
		result.append(createDoubleCheckBox("Sich vom ICD nicht unterkriegen lassen.",
				radioID
						+ "-3-2", "Zukünftige Hürden meistern", radioID + "-6-2"));
		result.append(createDoubleCheckBox("Körperliche Aktivität", radioID + "-3-3", "",
				radioID
						+ ""));
		result.append(createDoubleCheckBox("Mein soziales Netz", radioID + "-3-4", "",
				radioID + ""));
		result.append(createDoubleCheckBox("Mein Partner versteht mich einfach nicht.",
				radioID
						+ "-3-5", "", ""));
		result.append(createDoubleCheckBox("Intimität", radioID + "-3-6", "", ""));

		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion6(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"4\" class=\"rowhead\">");
		result.append(questionNum
				+ ")   " + FRAGE6 + "<br><b><i>" + HINT_MUTIPLE + "</i></b>");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE6 + "  \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createDoubleHeader("Einheit 1 - Kennenlernen", "QFB" + questionNum
				+ "-1",
				"Einheit 4 - Angst", "QFB" + questionNum + "-4"));

		result.append(createDoubleCheckBox("Was erwartet mich im ICD-Forum", radioID
				+ "-1-1",
				"Angst verstehen", radioID + "-4-1"));
		result.append(createDoubleCheckBox("Wer steckt hinter ICD Forum?", radioID
				+ "-1-2", "Wie sich Angst zeigt",
				radioID + "-4-2"));
		result.append(createDoubleCheckBox("Tipps und Anregungen", radioID + "-1-3",
				"Die eigenen Ängste kennen lernen", radioID + "-4-3"));
		result.append(createDoubleCheckBox("Unser Sicherheitskonzept", radioID + "-1-4",
				"Die Angst vor der Sterblichkeit", radioID + "-4-4"));

		result.append(createDoubleHeader("Einheit 2 - Wissen über den ICD", "QFB"
				+ questionNum
				+ "-2",
				"Einheit 5", "QFB" + questionNum + "-5"));

		result.append(createDoubleCheckBox("Wie funktioniert mein ICD?",
				radioID + "-2-1",
				"Angst überwinden", radioID + "-5-1"));
		result.append(createDoubleCheckBox("Was stört meinen ICD?", radioID + "-2-2",
				"Ziele erreichen", radioID + "-5-2"));
		result.append(createDoubleCheckBox("Den richtigen Rhythmus finden", radioID
				+ "-2-3",
				"Gedankenmuster durchbrechen", radioID + "-5-3"));
		result.append(createDoubleCheckBox("Der ICD löst aus - was tun!?", radioID
				+ "-2-4",
				"Stress reduzieren", radioID + "-5-4"));
		result.append(createDoubleCheckBox("Kleine und große Reisen mit dem ICD", radioID
				+ "-2-5",
				"Wenn Selbsthilfe nicht mehr reicht", radioID + "-5-5"));
		result.append(createDoubleCheckBox("Wo finde ich weitere Informationen?", radioID
				+ "-2-6",
				"", ""));

		result.append(createDoubleHeader("Einheit 3 - Alltag gestalten",
				"QFB" + questionNum + "-3",
				"Einheit 6 - Meine Zukunft mit dem ICD", "QFB" + questionNum + "-6"));

		result.append(createDoubleCheckBox("Der ICD als Teil von mir.", radioID + "-3-1",
				"Was kann ich für mich mitnehmen?", radioID + "-6-1"));
		result.append(createDoubleCheckBox("Sich vom ICD nicht unterkriegen lassen.",
				radioID
						+ "-3-2", "Zukünftige Hürden meistern", radioID + "-6-2"));
		result.append(createDoubleCheckBox("Körperliche Aktivität", radioID + "-3-3", "",
				radioID
						+ ""));
		result.append(createDoubleCheckBox("Mein soziales Netz", radioID + "-3-4", "",
				radioID + ""));
		result.append(createDoubleCheckBox("Mein Partner versteht mich einfach nicht.",
				radioID
						+ "-3-5", "", ""));
		result.append(createDoubleCheckBox("Intimität", radioID + "-3-6", "", ""));

		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion7(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE7 + "<br><b><i>" + HINT_MUTIPLE + "</i></b> ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE7 + "  \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createCheckBox(
				"zu wenig - ich hätte mir die Inhalte ausführlicher gewünscht. ", radioID));
		result.append(createCheckBox(
				"zu wenig - ich hätte mir mehr zusätzliche Inhalte gewünscht ", radioID));
		result.append(createCheckBox("zu viel", radioID));
		result.append(createCheckBox("genau passend", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion8(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE8 + "   ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE8 + "   \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createTextArea(radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion9(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE9 + "  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE9 + "\" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Nein", radioID));
		result.append(createRadioButton("Ja, ich habe mitgelesen ", radioID));
		result.append(createRadioButton(
				"Ja, ich habe mitgelesen und eigene Beiträge geschrieben ",
				radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion10(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE10);
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") " + FRAGE10 + "\"/>");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton(
				"Ich habe im Diskussionsforum nicht mitgelesen. ", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion11(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE11 + " ");
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") " + FRAGE11 + "\" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Kann ich nicht beurteilen. ", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion12(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE12 + "  ");
		result.append("<input type=\"hidden\" name=\"QFB" + questionNum + "\" value=\""
				+ questionNum
				+ ") " + FRAGE12 + "\"  />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton(
				"Ich habe im Diskussionsforum keine eigenen Fragen gestellt. ", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion13(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE13);
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE13 + "\" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton(
				"Ich habe im Diskussionsforum Antworten auf meine Fragen erhalten. ",
				radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion14(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE14 + " ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE14 + " \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton(
				"Ich habe im Diskussionsforum nicht auf die Fragen anderer geantwortet. ",
				radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion15(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE15);
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE15 + "\" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Weiß nicht ", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion16(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE16);
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE16 + "\" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append(createRadioButton("Weiß nicht ", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion17(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE17 + " ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE17 + " \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createTextArea(radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion18(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE18 + " ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE18 + " \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion19(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE19 + "  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE19 + "  \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Trifft für mich voll zu.", radioID));
		result.append(createRadioButton("Trifft für mich zu.", radioID));
		result.append(createRadioButton("Unentschieden.", radioID));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", radioID));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion20(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE20);
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE20 + "\" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("Ich war (fast) jeden Tag hier.", radioID));
		result.append(createRadioButton("Mehrmals die Woche", radioID));
		result.append(createRadioButton("1-2 Mal pro Woche.", radioID));
		result.append(createRadioButton("seltener", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion21(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE21 + " ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE21 + " \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("in den Inhalten", radioID));
		result.append(createRadioButton("im Forum", radioID));
		result.append(createRadioButton("ausgeglichen ", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion22(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE22 + " ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE22 + " \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton("...zu kurz.", radioID));
		result.append(createRadioButton("...zu lang.", radioID));
		result.append(createRadioButton("...genau passend. ", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion23(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE23 + "  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE23 + "  \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createRadioButton(
				"Ich konnte mich schnell hier \"einleben\" und fand mich gut zurecht.",
				radioID));
		result.append(createRadioButton(
				"Ich habe einige Zeit gebraucht, bis ich mich zurecht fand, dann ging es aber gut.",
				radioID));
		result.append(createRadioButton(
				"Ich hatte immer wieder Schwierigkeiten bei der Benutzung der Plattform.",
				radioID));
		result.append(createRadioButton("Die Bedienung war sehr schwierig.", radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion24(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE24 + "  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE24 + " \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createTextArea(radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion25(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE25 + "   ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE25 + "  \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;

		result.append(createRadioButton(
				"Ich würde prinzipiell an einem neuen Angebot teilnehmen.",
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
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private void renderQuestion26(StringBuilder result) {
		result.append("<table><tbody><tr>\n");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append(questionNum
				+ ") " + FRAGE26 + "  ");
		result.append("<input type=\"hidden\" name=\"QFB"
				+ questionNum
				+ "\" value=\""
				+ questionNum
				+ ") " + FRAGE26 + "  \" />");
		result.append("</td></tr>\n");

		String radioID = "FB" + questionNum;
		result.append(createTextArea(radioID));
		result.append("</tbody></table>\n");
		questionNum++;
	}

	private String createTextArea(String radioID) {
		return "<tr><td colspan=\"2\"><textarea style=\"font-size:100%;\"rows=\"6\" cols=\"70\" name=\""
				+ radioID
				+ "\"></textarea></td></tr>\n";
	}

	private String createRadioButton(String answer, String radioID) {
		return "<tr><td class=\"radio\"><input type=\"radio\" name=\"" + radioID
				+ "\" value=\""
				+ answer
				+ "\">"
				+ "</td>"
				+ "<td class=\"answer\">"
				+ answer
				+ "</td></tr>\n";
	}

	private String createCheckBox(String answer, String radioID) {
		return "<tr><td class=\"radio\"><input type=\"checkbox\" name=\"" + radioID
				+ "\" value=\""
				+ answer
				+ "\">"
				+ "</td>"
				+ "<td class=\"answer\">"
				+ answer
				+ "</td></tr>\n";
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
				+ "</td></tr>\n");
		return tmp.toString();
	}

	private String createDoubleHeader(String head1, String qfb1, String head2, String qfb2) {

		return "<td colspan=\"2\" class=\"rowhead2\">"
				+ head1
				+ "<input type=\"hidden\" name=\"" + qfb1 + "\" value=\"" + head1
				+ "\" />"
				+ "</td>"
				+ "<td colspan=\"2\" class=\"rowhead2\">"
				+ head2
				+ "<input type=\"hidden\" name=\"" + qfb2 + "\" value=\"" + head2
				+ "\" />"
				+ "</td></tr>\n";
	}
}

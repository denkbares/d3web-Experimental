package de.knowwe.defi.feedback;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;

public class FeedbackTagHandler extends AbstractTagHandler {

	/** topics the questions are ordered by */
	public static final String TOPIC1 = "Allgemeines";
	public static final String TOPIC2 = "Zu den Inhalten";
	public static final String TOPIC3 = "Zum Diskussionsforum";
	public static final String TOPIC4 = "zu Ihren Aktivitäten";

	/** questions */
	public static final String QUESTION1 = "Die Teilnahme am ICD Forum hat mir insgesamt weitergeholfen.";
	public static final String QUESTION2 = "ICD-Forum hat mir geholfen, meinen Alltag besser zu bewältigen.";
	public static final String QUESTION3 = "ICD-Forum hat mich motiviert, neue Dinge auszuprobieren.";
	public static final String QUESTION4 = "Durch die Teilnahme am ICD-Forum, komme ich mit meiner Erkrankung besser zurecht.";
	public static final String QUESTION5 = "Welche Themen waren für Sie besonders wichtig?";
	public static final String QUESTION6 = "Wie gut haben Ihnen die Themen in den einzelnen Bereichen geholfen?";
	public static final String QUESTION7 = "Wie haben Sie den Gesamtumfang des Programms empfunden?";
	public static final String QUESTION8 = "Was haben Sie bei unserem inhaltlichen Angebot vermisst?";
	public static final String QUESTION9 = "Haben Sie das Diskussionsforum genutzt?";
	public static final String QUESTION10 = "Es hat mir gut getan, im Diskussionsforum mitzulesen.";
	public static final String QUESTION11 = "Ich habe den Moderator im Diskussionsforum als hilfreich empfunden.";
	public static final String QUESTION12 = "Es hat mir gut getan, im Diskussionsforum Fragen zu stellen.";
	public static final String QUESTION13 = "Es hat mir gut getan, im Diskussionsforum Antworten auf meine Fragen zu erhalten.";
	public static final String QUESTION14 = "Es hat mir gut getan, im Diskussionsforum auf die Fragen anderer antworten zu können.";
	public static final String QUESTION15 = "Ich habe den Eindruck, dass der Moderator im Diskussionsforum an meinen Fragen und Problemen interessiert war.";
	public static final String QUESTION16 = "Ich habe den Eindruck, dass die Teilnehmer an meinen Fragen und Problemen wirklich interessiert waren.";
	public static final String QUESTION17 = "Was haben Sie im Diskussionsforum vermisst?";
	public static final String QUESTION18 = "Ich wusste, an wen ich mich bei ICD-Forum mit meinen persönlichen Fragen wenden kann.";
	public static final String QUESTION19 = "ICD-Forum hat mir einen vertrauensvollen Rahmen geboten, in dem ich mich über die Themen austauschen konnte, die mir wirklich wichtig sind.";
	public static final String QUESTION20 = "Wieviel Zeit haben Sie auf ICD-Forum verbracht?";
	public static final String QUESTION21 = "In welchen Bereichen von ICD-Forum haben Sie mehr Zeit verbracht?";
	public static final String QUESTION22 = "Wie haben Sie die Dauer des Programms empfunden? 6 Wochen waren...";
	public static final String QUESTION23 = "Wie fanden Sie die Bedienbarkeit von ICD-Forum?";
	public static final String QUESTION24 = "Wenn Sie Schwierigkeiten bei der Bedienung hatten - Woran lag das?";
	public static final String QUESTION25 = "Würden Sie in Zukunft an einem ähnlichen Angebot teilnehmen?";
	public static final String QUESTION26 = "Haben Sie sonst noch Wünsche, Kritik oder Erfahrungen, die Sie uns mitteilen möchten?";

	/** units and subunits */
	public static final Map<Integer, String> UNITS = new HashMap<Integer, String>();

	static {
		UNITS.put(1, "Einheit 1 - Kennenlernen");
		UNITS.put(11, "Was erwartet mich im ICD-Forum");
		UNITS.put(12, "Wer steckt hinter ICD Forum?");
		UNITS.put(13, "Tipps und Anregungen");
		UNITS.put(14, "Unser Sicherheitskonzept");
	}

	static {
		UNITS.put(2, "Einheit 2 - Wissen über den ICD");
		UNITS.put(21, "Wie funktioniert mein ICD?");
		UNITS.put(22, "Was stört meinen ICD");
		UNITS.put(23, "Den richtigen Rhythmus finden");
		UNITS.put(24, "Der ICD löst aus - was tun!?");
		UNITS.put(25, "Kleine und große Reisen mit dem ICD");
		UNITS.put(26, "Wo finde ich weitere Informationen?");
	}

	static {
		UNITS.put(3, "Einheit 3 - Alltag gestalten");
		UNITS.put(31, "Der ICD als Teil von mir");
		UNITS.put(32, "Sich vom ICD nicht unterkriegen lassen");
		UNITS.put(33, "Körperliche Aktivität");
		UNITS.put(34, "Mein soziales Netz");
		UNITS.put(35, "Mein Partner versteht mich einfach nicht");
		UNITS.put(36, "Intimität");
	}

	static {
		UNITS.put(4, "Einheit 4 - Angst");
		UNITS.put(41, "Angst verstehen");
		UNITS.put(42, "Wie sich Angst zeigt");
		UNITS.put(43, "Die eigenen Ängste kennen lernen");
		UNITS.put(44, "Die Angst vor der Sterblichkeit");
	}

	static {
		UNITS.put(5, "Einheit 5 - Krisen meistern");
		UNITS.put(51, "Angst überwinden");
		UNITS.put(52, "Ziele erreichen");
		UNITS.put(53, "Gedankenmuster durchbrechen");
		UNITS.put(54, "Stress reduzieren");
		UNITS.put(55, "Wenn Selbsthilfe nicht mehr reicht");
	}

	static {
		UNITS.put(6, "Einheit 6 - Meine Zukunft mit dem ICD");
		UNITS.put(61, "Was kann ich für mich mitnehmen?");
		UNITS.put(62, "Zukünftige Hürden meistern");
	}

	/** hint text */
	private static final String HINT_MUTIPLE = "<i>(Bitte alle zutreffenden Antworten markieren!)</i>";

	public FeedbackTagHandler() {
		super("defifeedback");
	}

	/**
	 *
	 */
	@Override
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {

		if (!userContext.userIsAsserted()) {
			return "<p class=\"info\">Bitte melden Sie sich an, um die Seite sehen zu können!</p>";
		}

		String path = Environment.getInstance().getWikiConnector().getSavePath();
		String filename = userContext.getUserName().toLowerCase() + "_feedback.xml";
		File f = new File(path + "/" + filename);

		StringBuilder html = new StringBuilder();
		List<String> answers;
		String title;

		if (f.exists()) {
			html.append("<div class='defifeedback'>");
			html.append("<p>Sie können den Abschlussfragebogen noch einmal ausfüllen.<br />");
			html.append("Die <span style='background-color:lightgreen;'>grünen Markierungen</span> zeigen dabei Ihre letzte Auswahl an.</p>");
			html.append("</div>");
		}

		html.append("<p class='header2'>" + TOPIC1 + "</p>");

		// question 1
		title = "1) " + QUESTION1;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		html.append(renderRadioTable(1, title, answers));
		html.append("\n<br/>\n");

		// question 2
		title = "2) " + QUESTION2;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich zu.");
		answers.add("Trifft für mich voll zu.");
		html.append(renderRadioTable(2, title, answers));
		html.append("\n<br/>\n");

		// question 3
		title = "3) " + QUESTION3;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		html.append(renderRadioTable(3, title, answers));
		html.append("\n<br/>\n");

		// question 4
		title = "4) " + QUESTION4;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich zu.");
		answers.add("Trifft für mich voll zu.");
		html.append(renderRadioTable(4, title, answers));
		html.append("\n<br/>\n");

		html.append("<p class='header2'>" + TOPIC2 + "</p>");

		// question 5
		title = "5) " + QUESTION5 + "<br/>" + HINT_MUTIPLE;
		html.append(renderMultipleUnitTable(5, title));
		html.append("\n<br/>\n");

		// question 6
		title = "6) " + QUESTION6 + "<br/>" + HINT_MUTIPLE;
		html.append(renderMultipleUnitTable(6, title));
		html.append("\n<br/>\n");

		// question 7
		title = "7) " + QUESTION7 + "<br/>" + HINT_MUTIPLE;
		answers = new LinkedList<String>();
		answers.add("zu wenig - ich hätte mir die Inhalte ausführlicher gewünscht.");
		answers.add("zu wenig - ich hätte mir mehr zusätzliche Inhalte gewünscht.");
		answers.add("zu viel.");
		answers.add("genau passend.");
		html.append(renderCheckTable(7, title, answers));
		html.append("\n<br/>\n");

		// question 8
		title = "8) " + QUESTION8;
		html.append(renderTextareaTable(8, title));
		html.append("\n<br/>\n");

		html.append("<p class='header2'>" + TOPIC3 + "</p>");

		// question 9
		title = "9) " + QUESTION9;
		answers = new LinkedList<String>();
		answers.add("Nein");
		answers.add("Ja, ich habe mitgelesen");
		answers.add("Ja, ich habe mitgelesen und eigene Beiträge geschrieben");
		html.append(renderRadioTable(9, title, answers));
		html.append("\n<br/>\n");

		// question 10
		title = "10) " + QUESTION10;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Ich habe im Diskussionsforum nicht mitgelesen. ");
		html.append(renderRadioTable(10, title, answers));
		html.append("\n<br/>\n");

		// question 11
		title = "11) " + QUESTION11;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Kann ich nicht beurteilen.");
		html.append(renderRadioTable(11, title, answers));
		html.append("\n<br/>\n");

		// question 12
		title = "12) " + QUESTION12;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Ich habe im Diskussionsforum keine eigenen Fragen gestellt.");
		html.append(renderRadioTable(12, title, answers));
		html.append("\n<br/>\n");

		// question 13
		title = "13) " + QUESTION13;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Ich habe im Diskussionsforum Antworten auf meine Fragen erhalten.");
		html.append(renderRadioTable(13, title, answers));
		html.append("\n<br/>\n");

		// question 14
		title = "14) " + QUESTION14;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Ich habe im Diskussionsforum nicht auf die Fragen anderer geantwortet.");
		html.append(renderRadioTable(14, title, answers));
		html.append("\n<br/>\n");

		// question 15
		title = "15) " + QUESTION15;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Weiß nicht ");
		html.append(renderRadioTable(15, title, answers));
		html.append("\n<br/>\n");

		// question 16
		title = "16) " + QUESTION16;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Weiß nicht ");
		html.append(renderRadioTable(16, title, answers));
		html.append("\n<br/>\n");

		// question 17
		title = "17) " + QUESTION17;
		html.append(renderTextareaTable(17, title));
		html.append("\n<br/>\n");

		html.append("<p class='header2'>" + TOPIC4 + "</p>");

		// question 18
		title = "18) " + QUESTION18;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		html.append(renderRadioTable(18, title, answers));
		html.append("\n<br/>\n");

		// question 19
		title = "19) " + QUESTION19;
		answers = new LinkedList<String>();
		answers.add("Trifft für mich voll zu.");
		answers.add("Trifft für mich zu.");
		answers.add("Unentschieden.");
		answers.add("Trifft für mich eher nicht zu.");
		answers.add("Trifft für mich überhaupt nicht zu.");
		answers.add("Weiß nicht ");
		html.append(renderRadioTable(19, title, answers));
		html.append("\n<br/>\n");

		// question 20
		title = "20) " + QUESTION20;
		answers = new LinkedList<String>();
		answers.add("Ich war (fast) jeden Tag hier.");
		answers.add("Mehrmals die Woche");
		answers.add("1-2 Mal pro Woche.");
		answers.add("Seltener");
		html.append(renderRadioTable(20, title, answers));
		html.append("\n<br/>\n");

		// question 21
		title = "21) " + QUESTION21;
		answers = new LinkedList<String>();
		answers.add("In den Inhalten.");
		answers.add("Im Forum.");
		answers.add("Ausgeglichen.");
		html.append(renderRadioTable(21, title, answers));
		html.append("\n<br/>\n");

		// question 22
		title = "22) " + QUESTION22;
		answers = new LinkedList<String>();
		answers.add("...zu kurz.");
		answers.add("...zu lang.");
		answers.add("...genau passend.");
		html.append(renderRadioTable(22, title, answers));
		html.append("\n<br/>\n");

		// question 23
		title = "23) " + QUESTION23;
		answers = new LinkedList<String>();
		answers.add("Ich konnte mich schnell hier \"einleben\" und fand mich gut zurecht.");
		answers.add("Ich habe einige Zeit gebraucht, bis ich mich zurecht fand, dann ging es aber gut.");
		answers.add("Ich hatte immer wieder Schwierigkeiten bei der Benutzung der Plattform.");
		answers.add("Die Bedienung war sehr schwierig.");
		html.append(renderRadioTable(23, title, answers));
		html.append("\n<br/>\n");

		// question 24
		title = "24) " + QUESTION24;
		html.append(renderTextareaTable(24, title));
		html.append("\n<br/>\n");

		// question 25
		title = "25) " + QUESTION25;
		answers = new LinkedList<String>();
		answers.add("Ich würde prinzipiell an einem neuen Angebot teilnehmen.");
		answers.add("Ich würde teilnehmen, wenn Verbesserungen am ICD-Forum stattfinden (bitte auswählen)");
		answers.add("Ich würde mir ein verbessertes Angebot mit einer stärkeren Beteiligung von Kardiologen und des ICD-Herstellers wünschen.");
		answers.add("Ich würde mir ein fortlaufendes Angebot wünschen, so dass sich mit der Zeit immer mehr Teilnehmer im Forum einbringen können.");
		answers.add("Ich würde mir ein verbessertes Angebot mit den folgenden Schwerpunkten/Zusätzen wünschen (bitte Art der Verbesserung angeben):");
		answers.add("Nein, ich habe kein Interesse.");
		html.append(renderSpecialTable(25, title, answers));
		html.append("\n<br/>\n");

		// question 26
		title = "26) " + QUESTION26;
		html.append(renderTextareaTable(26, title));
		html.append("\n<br/>\n");

		// save button
		if (f.exists()) {
			html.append("<p><center><input type=\"button\" value=\"Erneut speichern\" onClick=\"checkAndSubmit()\"></center></p>\n");
			html.append("<script type='text/javascript'>window.onload=function() {fillForm();prepare25();}</script>");
		}
		else {
			html.append("<p><center><input type=\"button\" value=\"Speichern\" onClick=\"checkAndSubmit()\"></center></p>\n");
			html.append("<script type='text/javascript'>window.onload=function() {prepare25();}</script>");
		}

		return Strings.maskHTML(html.toString());
	}

	/**
	 * render question as list of radiobuttons.
	 */
	private String renderRadioTable(int qnum, String title, List<String> answers) {
		StringBuilder html = new StringBuilder();
		int aCounter = 0;

		html.append("<table id='q" + qnum + "'>");
		html.append("<tr><td class='rowhead' colspan='2'>" + title + "</td><tr/>");
		for (int i = 1; i <= answers.size(); i++) {
			aCounter++;
			html.append("<tr><td class='radio'><input type='radio' value='" + answers.get(i - 1)
					+ "' id='q" + qnum + "_" + aCounter + "' name='q" + qnum + "'></td>");
			html.append("<td class='answer'>" + answers.get(i - 1) + "</td></tr>");
		}
		html.append("</table>");

		return html.toString();
	}

	/**
	 * render question as list of checkboxes.
	 */
	private String renderCheckTable(int qnum, String title, List<String> answers) {
		StringBuilder html = new StringBuilder();
		int aCounter = 0;

		html.append("<table id='q" + qnum + "'>");
		html.append("<tr><td class='rowhead' colspan='2'>" + title + "</td></tr>");
		for (int i = 1; i <= answers.size(); i++) {
			aCounter++;
			html.append("<tr><td class='radio'><input type='checkbox' value='" + answers.get(i - 1)
					+ "' id='q" + qnum + "_" + aCounter + "' name='q" + qnum + "'></td>");
			html.append("<td class='answer'>" + answers.get(i - 1) + "</td></tr>");
		}
		html.append("</table>");

		return html.toString();
	}

	/**
	 * render question as table of units.
	 */
	private String renderMultipleUnitTable(int qnum, String title) {
		StringBuilder html = new StringBuilder();
		int aCounter = 0;

		html.append("<table id='q" + qnum + "'>");
		html.append("<tr><td class='rowhead' colspan='4'>" + title + "</td></tr>");

		for (int i = 1; i <= 3; i++) {
			// unit 1,2,3 - unit 4,5,6
			html.append("<tr><td class='rowhead2' colspan='2'>" + UNITS.get(i) + "</td>");
			html.append("<td class='rowhead2' colspan='2'>" + UNITS.get(i + 3) + "</td></tr>");
			// - rows
			for (int j = 1; j <= 6; j++) {
				// cells
				html.append("<tr>");
				if (UNITS.containsKey(i * 10 + j)) {
					// radiobutton
					aCounter++;
					html.append("<td class='radio'><input type='checkbox' value='"
							+ UNITS.get(i) + " - " + UNITS.get(i * 10 + j) + "' id='q" + qnum + "_"
							+ aCounter + "' name='q" + qnum
							+ "-1'></td>");
					// answer
					html.append("<td class='answer2'>" + UNITS.get(i * 10 + j) + "</td>");
				}
				else {
					html.append("<td></td>");
				}
				if (UNITS.containsKey((i + 3) * 10 + j)) {
					// radiobutton
					aCounter++;
					html.append("<td class='radio'><input type='checkbox' value='"
							+ UNITS.get(i + 3) + " - " + UNITS.get((i + 3) * 10 + j) + "' id='q"
							+ qnum
							+ "_" + aCounter
							+ "' name='q"
							+ qnum + "-1'></td>");
					// answer
					html.append("<td class='answer2'>" + UNITS.get(i + 3) + " - "
							+ UNITS.get((i + 3) * 10 + j) + "</td>");
				}
				else {
					html.append("<td></td>");
				}
				html.append("</tr>");
			}
		}
		html.append("</table>");

		return html.toString();
	}

	/**
	 * render question as textarea.
	 */
	private String renderTextareaTable(int qnum, String title) {
		StringBuilder html = new StringBuilder();

		html.append("<table id='q" + qnum + "'>");
		html.append("<tr><td class='rowhead' colspan='2'>" + title + "</td></tr>");
		html.append("<tr><td colspan='2'><textarea id='q" + qnum
				+ "_1' cols='70' rows='6' style='font-size:100%;'></textarea></td></tr>");
		html.append("</table>");

		return html.toString();
	}

	/**
	 * Made for rendering question 25.
	 */
	private String renderSpecialTable(int qnum, String title, List<String> answers) {
		StringBuilder html = new StringBuilder();
		int aCounter = 0;

		html.append("<table id='q" + qnum + "'>");
		html.append("<tr><td class='rowhead' colspan='2'>" + title + "</td></tr>");
		// 2 radiobuttons
		for (int i = 1; i <= 2; i++) {
			aCounter++;
			html.append("<tr><td class='radio'><input type='radio' value='" + answers.get(i - 1)
					+ "' id='q" + qnum + "_" + aCounter + "' name='q" + qnum + "'></td>");
			html.append("<td class='answer'>" + answers.get(i - 1) + "</td></tr>");
		}
		// 2 checkboxes
		for (int i = 3; i <= 4; i++) {
			aCounter++;
			html.append("<tr><td class='radio'><input type='checkbox' value='" + answers.get(i - 1)
					+ "' id='q" + qnum + "_" + aCounter + "' name='q" + qnum + "'></td>");
			html.append("<td class='answer'>" + answers.get(i - 1) + "</td></tr>");
		}
		// checkbox + textarea
		aCounter++;
		html.append("<tr><td class='radio'><input type='checkbox' value='" + answers.get(4)
				+ "' id='q" + qnum + "_" + aCounter + "' name='q" + qnum + "'></td>");
		html.append("<td class='answer'>" + answers.get(4) + "</td></tr>");
		aCounter++;
		html.append("<tr><td colspan='2'><textarea id='q" + qnum + "_" + aCounter
				+ "' cols='70' rows='6' style='font-size:100%;'></textarea></td></tr>");
		// radiobox
		aCounter++;
		html.append("<tr><td class='radio'><input type='radio' value='" + answers.get(5)
				+ "' id='q" + qnum + "_" + aCounter + "' name='q" + qnum + "'></td>");
		html.append("<td class='answer'>" + answers.get(5) + "</td></tr>");
		html.append("</table>");

		return html.toString();
	}

}

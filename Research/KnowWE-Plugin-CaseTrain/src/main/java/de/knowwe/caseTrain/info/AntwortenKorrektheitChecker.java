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
package de.knowwe.caseTrain.info;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.knowwe.caseTrain.info.Antwort.AntwortErklaerung;
import de.knowwe.caseTrain.info.Antwort.AntwortMarkierung;
import de.knowwe.caseTrain.info.Antwort.AntwortText;
import de.knowwe.caseTrain.info.Antwort.AntwortTextArgument;
import de.knowwe.caseTrain.info.Antworten.Postfix;
import de.knowwe.caseTrain.info.Antworten.Praefix;
import de.knowwe.caseTrain.info.Antworten.Ueberschrift;
import de.knowwe.caseTrain.info.Frage.FrageTyp;
import de.knowwe.caseTrain.message.DuplicateComponentError;
import de.knowwe.caseTrain.message.InvalidArgumentError;
import de.knowwe.caseTrain.message.InvalidArgumentNotice;
import de.knowwe.caseTrain.message.InvalidArgumentWarning;
import de.knowwe.caseTrain.message.InvalidAttributeError;
import de.knowwe.caseTrain.message.MissingComponentWarning;
import de.knowwe.caseTrain.message.MissingContentWarning;


/**
 * 
 * Checks, if a specified Antwort is conform with its
 * correlated Frage.
 * Used in SubtreeHandler of {@link Info} and in {@link AntwortKorrektheitContent}
 * 
 * TODO Only tests the line ({t} some text) if t has the right composition
 *      no tests if the text is even there or so.
 * 
 * @author Johannes Dienst
 * @created 28.04.2011
 */
public class AntwortenKorrektheitChecker {

	public static final String PRAEFIX = "Präfix";
	public static final String POSTFIX = "Postfix";
	public static final String UEBERSCHRIFT = "Überschrift";

	public static final String MC = "MC";
	public static final String OC = "OC";
	public static final String W  = "W";
	public static final String UMW = "UMW";
	public static final String OMW = "OMW";
	public static final String N = "N";
	public static final String MN = "MN";
	public static final String T = "T";

	private final String[] types = {MC, OC, W, UMW, OMW, N, MN, T};

	private final Pattern regex;
	private static AntwortenKorrektheitChecker uniqueInstance;

	private static final String UNKNOWN_FRAGE_TYPE = "Fragetyp unbekannt";

	public static AntwortenKorrektheitChecker getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new AntwortenKorrektheitChecker();
		}
		return uniqueInstance;
	}

	private AntwortenKorrektheitChecker() {
		StringBuilder typesRegex = new StringBuilder("(");
		for(int i=0; i<types.length; i++)
			typesRegex.append(types[i] + "|");
		typesRegex.deleteCharAt(typesRegex.length()-1);
		typesRegex.append(")");
		regex = Pattern.compile(typesRegex.toString());
	}

	/**
	 * Return the Regex for finding supported FrageTypen
	 * as string.
	 * 
	 * @created 08.05.2011
	 * @return
	 */
	public String getRegexAsString() {
		return this.regex.toString();
	}

	/**
	 * Returns a list of FrageTypen, who
	 * have multiple Antworten-Blocks
	 * 
	 * @created 10.05.2011
	 * @return
	 */
	public List<String> getTypesMultiple() {
		List<String> toRet = new ArrayList<String>();
		toRet.add(UMW);
		toRet.add(OMW);
		toRet.add(MN);
		return toRet;
	}

	/**
	 * Validates the Antworten-block according to its
	 * related Frage.
	 * 
	 * @created 28.04.2011
	 * @param sec
	 */
	public void validateAntwortenBlock(Section<Frage> frage,
			Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		Section<FrageTyp> typ = Sections.findSuccessor(frage, FrageTyp.class);
		if (typ == null) {
			messages.add(new InvalidAttributeError(AntwortenKorrektheitChecker.UNKNOWN_FRAGE_TYPE));
			return;
		}

		if (typ.getOriginalText().equals(MC) || typ.getOriginalText().equals(OC)) {
			this.checkChoiceQuestion(antworten, messages);
			return;
		}

		if (typ.getOriginalText().equals(W)) {
			this.checkWordQuestion(antworten, messages);
			return;
		}

		if (typ.getOriginalText().equals(UMW)) {
			this.checkUMWQuestion(antworten, messages);
			return;
		}

		if (typ.getOriginalText().equals(OMW)) {
			this.checkOMWQuestion(antworten, messages);
			return;
		}

		if (typ.getOriginalText().equals(N)) {
			this.checkNQuestion(antworten, messages);
			return;
		}

		if (typ.getOriginalText().equals(MN)) {
			this.checkMNQuestion(antworten, messages);
			return;
		}

		if (typ.getOriginalText().equals(T)) {
			this.checkTQuestion(antworten, messages);
		}

	}

	/**
	 * Tests if {@link AntwortText} is empty
	 * 
	 * @created 09.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkText(Section<Antworten> antworten, List<KDOMReportMessage> messages) {

		List<Section<Antwort>> found = new ArrayList<Section<Antwort>>();
		Sections.findSuccessorsOfType(antworten, Antwort.class, found);

		Section<AntwortText> antwortText = null;
		for(Section<Antwort> ans : found) {
			antwortText = Sections.findSuccessor(ans, AntwortText.class);
			if ( antwortText == null )
				messages.add(new MissingContentWarning("Antwort hat keinen Antwort-Text"));
			else if (antwortText.getOriginalText().trim().length() == 0)
				messages.add(new MissingContentWarning("Antwort hat leeren Antwort-Text"));

		}
	}

	/**
	 * Tests if {@link AntwortErklaerung} is empty
	 * 
	 * @created 09.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkErklaerung(Section<Antworten> antworten, List<KDOMReportMessage> messages) {

		List<Section<Antwort>> found = new ArrayList<Section<Antwort>>();
		Sections.findSuccessorsOfType(antworten, Antwort.class, found);

		Section<AntwortErklaerung> antwortErklaerung = null;
		for(Section<Antwort> ans : found) {
			antwortErklaerung = Sections.findSuccessor(ans, AntwortErklaerung.class);
			if ( antwortErklaerung != null )
				if (antwortErklaerung.getOriginalText().trim().length() == 0)
					messages.add(new MissingContentWarning(
					"Antwort-Erklärung enthält keinen Text"));
		}
	}

	private void checkMarkierung(
			Section<Antworten> antworten, List<KDOMReportMessage> messages, int minAnswers) {

		// First: Right Syntax of Markierung
		List<Section<Antwort>> found = new ArrayList<Section<Antwort>>();
		Sections.findSuccessorsOfType(antworten, Antwort.class, found);

		if (found.size() < minAnswers) {
			messages.add(new InvalidArgumentError("Weniger als "+ minAnswers +" Antworten für Frage angegeben."));
		}

		// Counting right and wrong answers
		int rightAnswers = 0;
		int wrongAnswers = 0;
		int numAnswers = 0;
		Section<AntwortMarkierung> weight = null;
		String content = "";
		for(Section<Antwort> ans : found) {
			weight = Sections.findSuccessor(ans, AntwortMarkierung.class);
			content = weight.getOriginalText().substring(1, weight.getOriginalText().length()-1).trim();

			if (content.equals("+") || content.equals("1")) {
				rightAnswers++;
				continue;
			}
			if (content.equals("-") || content.equals("0")) {
				wrongAnswers++;
				continue;
			}
			numAnswers++;
		}

		if (rightAnswers == 0) {
			messages.add(new InvalidArgumentError("Keine richtige Antwort für Frage angegeben"));
		}
		if ( (wrongAnswers == 0) && (minAnswers > 1) ) {
			messages.add(new InvalidArgumentWarning("Keine falsche Antwort für Frage angegeben"));
		}
		if  ( (numAnswers > 0) && ((rightAnswers > 0 ) || (wrongAnswers > 0)) ) {
			messages.add(new InvalidArgumentNotice("+/- ist gemischt mit Zahlen"));
		}

	}

	private void checkChoiceQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		this.checkMarkierung(antworten, messages, 2);
		this.checkText(antworten, messages);
		this.checkErklaerung(antworten, messages);
	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkWordQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		this.checkMarkierung(antworten, messages, 1);
		this.checkText(antworten, messages);

		List<Section<Antwort>> found = new ArrayList<Section<Antwort>>();
		Sections.findSuccessorsOfType(antworten, Antwort.class, found);

		Section<AntwortText> antwortText = null;
		Section<AntwortTextArgument> arg = null;
		String antString = "";
		String c = "";
		for(Section<Antwort> ans : found) {
			antwortText = Sections.findSuccessor(ans, AntwortText.class);

			if ( (antwortText == null))
				continue;

			antString = antwortText.getOriginalText().trim();
			if (antString.length() == 0 )
				continue;

			if (antString.startsWith("{")) {

				if (antString.indexOf("}") == -1) {
					messages.add(new InvalidArgumentError("Kein schließendes '}'"));
					continue;
				}

				antString = antString.substring(antString.indexOf("}")+1);
				if (antString.length() == 0)
					messages.add(new InvalidArgumentError("Leeres Wort eingegeben"));
			}

			// Test the Argument
			arg = Sections.findSuccessor(ans, AntwortTextArgument.class);
			if (arg != null) {
				c = arg.getOriginalText().trim().substring(1, arg.getOriginalText().trim().length()-1);
				// Regex is marked with r
				if ( c.equals("r")) {
					try {
						Pattern.compile(antString);
					} catch (Exception e) {
						messages.add(new InvalidArgumentError("Regex fehlerhaft: " + antString));
					}
					continue;
				}

				// TODO how to check formulas?
				if ( c.equals("f")) continue;

				// Cohesion must be Long
				try {
					Long.valueOf(c);
				} catch(Exception e) {
					messages.add(new InvalidArgumentError("Kein gueltiges Zeichen innerhalb {}"));
					continue;
				}

			}

		}

		this.checkErklaerung(antworten, messages);
	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkUMWQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		this.checkMarkierung(antworten, messages, 1);
		this.checkText(antworten, messages);
		this.checkErklaerung(antworten, messages);
		this.checkUeberschrift(antworten, messages);
	}

	/**
	 * 
	 * TODO: Only tests one Antworten-Block
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkOMWQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		this.checkMarkierung(antworten, messages, 1);
		this.checkText(antworten, messages);
		this.checkErklaerung(antworten, messages);
		this.checkUeberschrift(antworten, messages);
	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkNQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		this.checkMarkierung(antworten, messages, 1);
		this.checkText(antworten, messages);
		this.checkNumAntwortenBlock(antworten, messages);
		this.checkErklaerung(antworten, messages);
	}

	private void checkNumAntwortenBlock(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		List<Section<Antwort>> found = new ArrayList<Section<Antwort>>();
		Sections.findSuccessorsOfType(antworten, Antwort.class, found);

		Section<AntwortText> antwortText = null;
		String antString = "";
		for(Section<Antwort> ans : found) {
			antwortText = Sections.findSuccessor(ans, AntwortText.class);
			antString = antwortText.getOriginalText().trim();
			String[] interval = antString.split("[ ]+");

			if ( (interval.length == 0) || (interval.length > 2) ) {
				messages.add(new InvalidArgumentError("Formatierung des Intervals fehlerhaft"));
				continue;
			}

			int i1 = 0;
			int i2 = Integer.MAX_VALUE;
			try {
				i1 = Integer.valueOf(interval[0]);
				if (interval.length != 1)
					i2 = Integer.valueOf(interval[1]);
			} catch(Exception e) {
				messages.add(new InvalidArgumentError("Interval muss aus Zahlen bestehen"));
				continue;
			}

			if (i1 > i2)
				messages.add(new InvalidArgumentWarning("Erste Zahl größer als zweite Zahl"));

		}

		this.checkPraefixPostfix(antworten, messages);
	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkMNQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		this.checkMarkierung(antworten, messages, 1);
		this.checkText(antworten, messages);
		this.checkNumAntwortenBlock(antworten, messages);
		this.checkErklaerung(antworten, messages);
		this.checkUeberschrift(antworten, messages);
	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkTQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		this.checkMarkierung(antworten, messages, 1);
		this.checkText(antworten, messages);
		this.checkErklaerung(antworten, messages);
	}

	/**
	 * 
	 * @created 10.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkPraefixPostfix(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		List<Section<Praefix>> found = new ArrayList<Section<Praefix>>();
		Sections.findSuccessorsOfType(antworten, Praefix.class, found);
		if (found.size() > 1)
			messages.add(new DuplicateComponentError(PRAEFIX));
		if (found.size() == 0)
			messages.add(new MissingComponentWarning(PRAEFIX));

		List<Section<Postfix>> found2 = new ArrayList<Section<Postfix>>();
		Sections.findSuccessorsOfType(antworten, Postfix.class, found2);
		if (found2.size() > 1)
			messages.add(new DuplicateComponentError(POSTFIX));
		if (found2.size() == 0)
			messages.add(new MissingComponentWarning(POSTFIX));
	}

	/**
	 * 
	 * @created 10.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkUeberschrift(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		List<Section<Ueberschrift>> found = new ArrayList<Section<Ueberschrift>>();
		Sections.findSuccessorsOfType(antworten, Ueberschrift.class, found);
		if (found.size() > 1)
			messages.add(new DuplicateComponentError(UEBERSCHRIFT));
		if (found.size() == 0)
			messages.add(new MissingComponentWarning(UEBERSCHRIFT));
	}
}

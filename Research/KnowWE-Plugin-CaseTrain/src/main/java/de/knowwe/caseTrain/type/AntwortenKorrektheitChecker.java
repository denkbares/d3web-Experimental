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
package de.knowwe.caseTrain.type;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.knowwe.caseTrain.message.InvalidArgumentError;
import de.knowwe.caseTrain.message.InvalidArgumentNotice;
import de.knowwe.caseTrain.message.InvalidArgumentWarning;
import de.knowwe.caseTrain.message.InvalidAttributeError;
import de.knowwe.caseTrain.type.Antworten.Antwort;
import de.knowwe.caseTrain.type.Antworten.Antwort.AntwortMarkierung;
import de.knowwe.caseTrain.type.Frage.FrageTyp;


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

	private final String MC = "MC";
	private final String OC = "OC";
	private final String W  = "W";
	private final String UMW = "UMW";
	private final String OMW = "OMW";
	private final String N = "N";
	private final String MN = "MN";
	private final String T = "T";

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
		}

		if (typ.getOriginalText().equals(UMW)) {
			this.checkUMWQuestion(antworten, messages);
		}

		if (typ.getOriginalText().equals(OMW)) {
			this.checkOMWQuestion(antworten, messages);
		}

		if (typ.getOriginalText().equals(N)) {
			this.checkNQuestion(antworten, messages);
		}

		if (typ.getOriginalText().equals(MN)) {
			this.checkMNQuestion(antworten, messages);
		}

		if (typ.getOriginalText().equals(T)) {
			this.checkTQuestion(antworten, messages);
		}

	}


	private void checkChoiceQuestion(
			Section<Antworten> antworten, List<KDOMReportMessage> messages) {

		List<Section<Antwort>> found = new ArrayList<Section<Antwort>>();
		Sections.findSuccessorsOfType(antworten, Antwort.class, found);

		if (found.size() < 2) {
			messages.add(new InvalidArgumentError("Weniger als 2 Antworten für Frage angegeben."));
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

			if (content.equals("+")) {
				rightAnswers++;
				continue;
			}
			if (content.equals("-")) {
				wrongAnswers++;
				continue;
			}
			numAnswers++;
		}

		if (rightAnswers == 0) {
			messages.add(new InvalidArgumentError("Keine richtige Antwort für Frage angegeben"));
		}
		if (wrongAnswers == 0) {
			messages.add(new InvalidArgumentWarning("Keine falsche Antwort für Frage angegeben"));
		}
		if  ( (numAnswers > 0) && ((rightAnswers > 0 ) || (wrongAnswers > 0)) ) {
			messages.add(new InvalidArgumentNotice("+/- ist gemischt mit Zahlen"));
		}
	}

	/**
	 * 
	 * TODO Support all the various types
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkWordQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkUMWQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkOMWQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkNQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkMNQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @created 08.05.2011
	 * @param antworten
	 * @param messages
	 */
	private void checkTQuestion(Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		// TODO Auto-generated method stub

	}
}

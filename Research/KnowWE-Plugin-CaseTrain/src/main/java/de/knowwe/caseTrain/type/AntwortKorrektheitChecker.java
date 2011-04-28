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
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.caseTrain.message.InvalidArgumentError;
import de.knowwe.caseTrain.message.InvalidArgumentWarning;
import de.knowwe.caseTrain.message.InvalidAttributeError;
import de.knowwe.caseTrain.type.Antworten.Antwort;
import de.knowwe.caseTrain.type.Antworten.Antwort.AntwortKorrektheit.AntwortKorrektheitContent;
import de.knowwe.caseTrain.type.Frage.FrageTyp;


/**
 * 
 * Checks, if a specified Antwort is conform with its
 * correlated Frage.
 * 
 * @author Johannes Dienst
 * @created 28.04.2011
 */
public class AntwortKorrektheitChecker extends GeneralSubtreeHandler<AntwortKorrektheitContent> {

	private final String MC = "MC";
	private final String OC = "OC";
	//	private final String W  = "W";
	//	private final String UMW = "UMW";
	//	private final String OMW = "OMW";
	//	private final String N = "N";
	//	private final String MN = "MN";
	//	private final String T = "T";

	private final String[] types = {MC, OC,}; //  W, UMW, OMW, N, MN, T
	String[] symbols = {"+", "-"};

	private final Pattern regex;
	private static AntwortKorrektheitChecker uniqueInstance;

	private static final String UNKNOWN_FRAGE_TYPE = "Fragetyp unbekannt";

	public static AntwortKorrektheitChecker getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new AntwortKorrektheitChecker();
		}
		return uniqueInstance;
	}

	private AntwortKorrektheitChecker() {
		StringBuilder typesRegex = new StringBuilder("(");
		for(int i=0; i<types.length; i++)
			typesRegex.append(types[i] + "|");
		typesRegex.deleteCharAt(typesRegex.length()-1);
		typesRegex.append(")");
		regex = Pattern.compile(typesRegex.toString());
	}

	public String getRegexAsString() {
		return this.regex.toString();
	}

	/*
	 * Simply checks, if the string t inside {t} has the allowed
	 * symbols. +/-/number
	 * TODO: more to come.
	 * 
	 * TODO Only numbers from 0-1?
	 */
	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<AntwortKorrektheitContent> s) {

		List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>();
		String content = s.getOriginalText().trim();

		for (String string : symbols) {
			if (content.equals(string)) {
				return messages;
			}
		}

		double d1 = 0.0;
		try {
			d1 = Double.parseDouble(content);
		}
		catch (Exception e) {
			messages.add(new InvalidArgumentError(
			" Nur '+' oder '-' oder Zahlen zwischen 0 und 1 erlaubt!"));
		}

		if ( (d1 < 0) || (d1 > 1) ) {
			messages.add(new InvalidArgumentError(
			" Nur Zahlen zwischen 0 und 1 erlaubt!"));
		}
		return messages;
	}

	private void checkChoiceQuestion(
			Section<Antworten> antworten, List<KDOMReportMessage> messages) {

		List<Section<Antwort>> found = new ArrayList<Section<Antwort>>();
		Sections.findSuccessorsOfType(antworten, Antwort.class, found);

		if (found.size() < 2) {
			messages.add(new InvalidArgumentError("Weniger als 2 Antworten für Frage angegeben."));
		}

		// Counting right and wrong answers
		// TODO: What to do with a value 0.3 i.e.
		int rightAnswers = 0;
		int wrongAnswers = 0;
		Section<AntwortKorrektheitContent> weight = null;
		for(Section<Antwort> ans : found) {
			weight = Sections.findSuccessor(ans, AntwortKorrektheitContent.class);
			if (weight.getOriginalText().equals("+")) {rightAnswers++;}
			if (weight.getOriginalText().equals("-")) {wrongAnswers++;}
		}

		if (rightAnswers == 0) {
			messages.add(new InvalidArgumentError(
			"Keine richtige Antwort für Frage angegeben"));
		}
		if (wrongAnswers == 0) {
			messages.add(new InvalidArgumentWarning(
			"Keine falsche Antwort für Frage angegeben"));
		}
	}

	/**
	 * Validates the Antworten-block according to its
	 * related Frage.
	 * 
	 * TODO Implement the rest of Frage-Types
	 * 
	 * @created 28.04.2011
	 * @param sec
	 */
	public void validateAntwortenBlock(Section<Frage> frage,
			Section<Antworten> antworten, List<KDOMReportMessage> messages) {
		Section<FrageTyp> typ = Sections.findSuccessor(frage, FrageTyp.class);
		if (typ == null) {
			messages.add(new InvalidAttributeError(AntwortKorrektheitChecker.UNKNOWN_FRAGE_TYPE));
			return;
		}

		if (typ.getOriginalText().equals(MC) || typ.getOriginalText().equals(OC)) {
			this.checkChoiceQuestion(antworten, messages);
			return;
		}

	}
}

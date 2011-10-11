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
package de.knowwe.casetrain.info;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import de.knowwe.casetrain.info.AnswerLine.AnswerExplanation;
import de.knowwe.casetrain.info.AnswerLine.AnswerMark;
import de.knowwe.casetrain.info.AnswerLine.AnswerText;
import de.knowwe.casetrain.info.AnswerLine.AnswerTextArgument;
import de.knowwe.casetrain.info.AnswersBlock.Heading;
import de.knowwe.casetrain.info.AnswersBlock.Postfix;
import de.knowwe.casetrain.info.AnswersBlock.Praefix;
import de.knowwe.casetrain.info.Question.QuestionType;
import de.knowwe.casetrain.message.DuplicateComponentError;
import de.knowwe.casetrain.message.InvalidArgumentError;
import de.knowwe.casetrain.message.InvalidArgumentNotice;
import de.knowwe.casetrain.message.InvalidArgumentWarning;
import de.knowwe.casetrain.message.InvalidAttributeError;
import de.knowwe.casetrain.message.MissingComponentWarning;
import de.knowwe.casetrain.message.MissingContentWarning;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;


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
public class AnswersBlockValidator {

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
	private static AnswersBlockValidator uniqueInstance;

	ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	public static AnswersBlockValidator getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new AnswersBlockValidator();
		}
		return uniqueInstance;
	}

	private AnswersBlockValidator() {
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
	 * have multiple AnswersBlocks
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
	 * Validates the AnswersBlock according to its
	 * related Frage.
	 * 
	 * @created 28.04.2011
	 * @param frage
	 * @param answersBlock
	 * @param messages
	 * @param evaluation
	 */
	public void validateAnswersBlock(Section<Question> frage,
			Section<AnswersBlock> answersBlock, List<KDOMReportMessage> messages,
			boolean evaluation) {
		Section<QuestionType> typ = Sections.findSuccessor(frage, QuestionType.class);
		if (typ == null) {
			messages.add(
					new InvalidAttributeError(
							bundle.getString("UNKNOWN_FRAGE_TYPE")));
			return;
		}

		if (typ.getOriginalText().equals(MC) || typ.getOriginalText().equals(OC)) {
			this.checkChoiceQuestion(answersBlock, messages, evaluation);
			return;
		}

		if (typ.getOriginalText().equals(W)) {
			this.checkWordQuestion(answersBlock, messages, evaluation);
			return;
		}

		if (typ.getOriginalText().equals(UMW)) {
			this.checkUMWQuestion(answersBlock, messages, evaluation);
			return;
		}

		if (typ.getOriginalText().equals(OMW)) {
			this.checkOMWQuestion(answersBlock, messages, evaluation);
			return;
		}

		if (typ.getOriginalText().equals(N)) {
			this.checkNQuestion(answersBlock, messages, evaluation);
			return;
		}

		if (typ.getOriginalText().equals(MN)) {
			this.checkMNQuestion(answersBlock, messages, evaluation);
			return;
		}

		if (typ.getOriginalText().equals(T)) {
			this.checkTQuestion(answersBlock, messages, evaluation);
		}

	}

	/**
	 * Tests if {@link AnswerText} is empty
	 * 
	 * @created 09.05.2011
	 * @param answersBlock
	 * @param messages
	 */
	private void checkText(Section<AnswersBlock> answersBlock, List<KDOMReportMessage> messages) {

		List<Section<AnswerLine>> found = new ArrayList<Section<AnswerLine>>();
		Sections.findSuccessorsOfType(answersBlock, AnswerLine.class, found);

		Section<AnswerText> antwortText = null;
		for(Section<AnswerLine> ans : found) {
			antwortText = Sections.findSuccessor(ans, AnswerText.class);
			if ( antwortText == null )
				messages.add(new MissingContentWarning("Antwort hat keinen Antwort-Text"));
			else if (antwortText.getOriginalText().trim().length() == 0)
				messages.add(new MissingContentWarning("Antwort hat leeren Antwort-Text"));

		}
	}

	/**
	 * Tests if {@link AnswerExplanation} is empty
	 * 
	 * @created 09.05.2011
	 * @param answersBlock
	 * @param messages
	 */
	private void checkErklaerung(Section<AnswersBlock> answersBlock, List<KDOMReportMessage> messages) {

		List<Section<AnswerLine>> found = new ArrayList<Section<AnswerLine>>();
		Sections.findSuccessorsOfType(answersBlock, AnswerLine.class, found);

		Section<AnswerExplanation> antwortErklaerung = null;
		for(Section<AnswerLine> ans : found) {
			antwortErklaerung = Sections.findSuccessor(ans, AnswerExplanation.class);
			if ( antwortErklaerung != null )
				if (antwortErklaerung.getOriginalText().trim().length() == 0)
					messages.add(new MissingContentWarning(
					"Antwort-Erklärung enthält keinen Text"));
		}
	}

	/**
	 * 
	 * 
	 * @created 29.05.2011
	 * @param answersBlock
	 * @param messages
	 * @param minAnswers
	 * @param evaluation if the AnswersBlock is part of Evaluation block
	 */
	private void checkMarkierung(
			Section<AnswersBlock> answersBlock, List<KDOMReportMessage> messages, int minAnswers,
			boolean evaluation) {

		List<Section<AnswerLine>> found = new ArrayList<Section<AnswerLine>>();
		Sections.findSuccessorsOfType(answersBlock, AnswerLine.class, found);

		if (found.size() < minAnswers) {
			messages.add(new InvalidArgumentError("Weniger als "+ minAnswers +" answersBlockBlock für Frage angegeben."));
		}

		// Counting right and wrong answers
		int rightAnswers = 0;
		int wrongAnswers = 0;
		int numAnswers = 0;
		Section<AnswerMark> weight = null;
		String content = "";
		for(Section<AnswerLine> ans : found) {
			weight = Sections.findSuccessor(ans, AnswerMark.class);
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

		if (evaluation) {
			if (rightAnswers > 0)
				messages.add(new InvalidArgumentError("Richtige Antwort für Frage nicht erlaubt"));
			if (numAnswers > 0)
				messages.add(new InvalidArgumentError("Mit Numerisch bewertete " +
				"Antwort für Frage nicht erlaubt"));
			return;
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

	// No Präfix/Postfix/Heading-check
	private void checkChoiceQuestion(Section<AnswersBlock> answersBlock,
			List<KDOMReportMessage> messages, boolean evaluation) {
		this.checkMarkierung(answersBlock, messages, 2, evaluation);
		this.checkText(answersBlock, messages);
		this.checkErklaerung(answersBlock, messages);
	}

	// TODO Preafix/Postfix-Check
	private void checkWordQuestion(Section<AnswersBlock> answersBlock,
			List<KDOMReportMessage> messages, boolean evaluation) {
		this.checkMarkierung(answersBlock, messages, 1, evaluation);
		this.checkText(answersBlock, messages);

		List<Section<AnswerLine>> found = new ArrayList<Section<AnswerLine>>();
		Sections.findSuccessorsOfType(answersBlock, AnswerLine.class, found);

		Section<AnswerText> antwortText = null;
		Section<AnswerTextArgument> arg = null;
		String antString = "";
		String c = "";
		for(Section<AnswerLine> ans : found) {
			antwortText = Sections.findSuccessor(ans, AnswerText.class);

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
			arg = Sections.findSuccessor(ans, AnswerTextArgument.class);
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

		this.checkErklaerung(answersBlock, messages);
	}

	private void checkUMWQuestion(Section<AnswersBlock> answersBlock,
			List<KDOMReportMessage> messages, boolean evaluation) {
		this.checkMarkierung(answersBlock, messages, 1, evaluation);
		this.checkText(answersBlock, messages);
		this.checkErklaerung(answersBlock, messages);
		//		this.checkUeberschrift(answersBlock, messages);
	}

	/**
	 * 
	 * TODO: Only tests one answersBlock-Block
	 * 
	 * @created 08.05.2011
	 * @param answersBlock
	 * @param messages
	 */
	private void checkOMWQuestion(Section<AnswersBlock> answersBlock,
			List<KDOMReportMessage> messages, boolean evaluation) {
		this.checkMarkierung(answersBlock, messages, 1, evaluation);
		this.checkText(answersBlock, messages);
		this.checkErklaerung(answersBlock, messages);
		this.checkPraefixPostfix(answersBlock, messages);
		this.checkHeading(answersBlock, messages);
	}

	// No heading check. Praefix-Postfix tested by checkNumanswersBlock
	private void checkNQuestion(Section<AnswersBlock> answersBlock,
			List<KDOMReportMessage> messages, boolean evaluation) {
		this.checkMarkierung(answersBlock, messages, 1, evaluation);
		this.checkText(answersBlock, messages);
		this.checkNumanswersBlock(answersBlock, messages);
		this.checkErklaerung(answersBlock, messages);
	}

	private void checkNumanswersBlock(Section<AnswersBlock> answersBlock, List<KDOMReportMessage> messages) {
		List<Section<AnswerLine>> found = new ArrayList<Section<AnswerLine>>();
		Sections.findSuccessorsOfType(answersBlock, AnswerLine.class, found);

		Section<AnswerText> antwortText = null;
		String antString = "";
		for(Section<AnswerLine> ans : found) {
			antwortText = Sections.findSuccessor(ans, AnswerText.class);
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

		this.checkPraefixPostfix(answersBlock, messages);
	}

	private void checkMNQuestion(Section<AnswersBlock> answersBlock,
			List<KDOMReportMessage> messages, boolean evaluation) {
		this.checkMarkierung(answersBlock, messages, 1, evaluation);
		this.checkText(answersBlock, messages);
		this.checkNumanswersBlock(answersBlock, messages);
		this.checkErklaerung(answersBlock, messages);
		this.checkHeading(answersBlock, messages);
		this.checkHeading(answersBlock, messages);
	}

	private void checkTQuestion(Section<AnswersBlock> answersBlock,
			List<KDOMReportMessage> messages, boolean evaluation) {
		this.checkMarkierung(answersBlock, messages, 1, evaluation);
		this.checkText(answersBlock, messages);
		this.checkErklaerung(answersBlock, messages);
	}

	/**
	 * 
	 * @created 10.05.2011
	 * @param answersBlock
	 * @param messages
	 */
	private void checkPraefixPostfix(Section<AnswersBlock> answersBlock, List<KDOMReportMessage> messages) {
		List<Section<Praefix>> found = new ArrayList<Section<Praefix>>();
		Sections.findSuccessorsOfType(answersBlock, Praefix.class, found);
		if (found.size() > 1)
			messages.add(new DuplicateComponentError(Praefix.class.getSimpleName()));
		if (found.size() == 0)
			messages.add(new MissingComponentWarning(Praefix.class.getSimpleName()));

		List<Section<Postfix>> found2 = new ArrayList<Section<Postfix>>();
		Sections.findSuccessorsOfType(answersBlock, Postfix.class, found2);
		if (found2.size() > 1)
			messages.add(new DuplicateComponentError(Postfix.class.getSimpleName()));
		if (found2.size() == 0)
			messages.add(new MissingComponentWarning(Postfix.class.getSimpleName()));
	}

	/**
	 * 
	 * @created 10.05.2011
	 * @param answersBlock
	 * @param messages
	 */
	private void checkHeading(Section<AnswersBlock> answersBlock, List<KDOMReportMessage> messages) {
		List<Section<Heading>> found = new ArrayList<Section<Heading>>();
		Sections.findSuccessorsOfType(answersBlock, Heading.class, found);
		if (found.size() > 1)
			messages.add(new DuplicateComponentError(Heading.class.getSimpleName()));
		if (found.size() == 0)
			messages.add(new MissingComponentWarning(Heading.class.getSimpleName()));
	}
}

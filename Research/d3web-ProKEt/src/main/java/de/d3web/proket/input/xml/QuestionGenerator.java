/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.proket.input.xml;

import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Generates questions based on given bounds.
 * 
 * @author Martina Freiberg
 * @author Johannes Mitlmeier, Tobias Mikschl
 */
public class QuestionGenerator {

	// random text
	private static String exampleStr = "Lorem ipsum dolor sit amet consetetur "
			+ "sadipscing elitr sed diam nonumy eirmod tempor invidunt ut labore "
			+ "et dolore magna aliquyam erat sed diam voluptua At vero eos et "
			+ "accusam et justo duo dolores et ea rebum Stet clita kasd gubergren "
			+ "no sea takimata sanctus est Lorem ipsum dolor sit amet Lorem ipsum "
			+ "dolor sit amet consetetur sadipscing elitr sed diam nonumy eirmod "
			+ "tempor invidunt ut labore et dolore magna aliquyam erat sed diam "
			+ "voluptua At vero eos et accusam et justo duo dolores et ea rebum "
			+ "Stet clita kasd gubergren no sea takimata sanctus est Lorem ipsum "
			+ "dolor sit amet";

	private static final Logger logger = Logger.getLogger("MainLogger");
	private int aIdCounter;
	private int qIdCounter;
	private Random randomizer;
	private Hashtable<String, String> usedIds;
	private XMLFileWriter xmlWriter;

	// Constructor
	public QuestionGenerator(XMLFileWriter xmlWriter,
			Hashtable<String, String> usedIds) {
		qIdCounter = 1;
		aIdCounter = 1;
		randomizer = new Random();
		this.xmlWriter = xmlWriter;
		this.usedIds = usedIds;
	}

	/**
	 * Generates an answer for a generate tag.
	 * 
	 * @param atts Attributes of the generate tag
	 */
	private void genrateAnswer(Attributes atts) {

		AttributesImpl attsImpl = new AttributesImpl();

		// find free ID by trying if answer+counter is already contained
		// in the usedIDs
		while (usedIds.keySet().contains("answer" + aIdCounter)) {
			aIdCounter++;
		}

		// add the ID
		attsImpl.addAttribute(null, null, "id", null, "answer" + aIdCounter);

		// put into used ID storage
		usedIds.put("answer" + aIdCounter, "answer");

		String text = "automatically generated answer"; // default text

		// if answer-length was specified
		if (atts.getValue("answer-length") != null) {

			// create random text for answer also
			text = randomTextCreator(atts.getValue("answer-length"));
		}

		// add to attributesImpl
		attsImpl.addAttribute(null, null, "text", null, text);

		// write answer XML element with the help of the attsImpl and close it
		xmlWriter.openXMLElement("answer", attsImpl);
		xmlWriter.closeXMLElement("answer");
	}

	/**
	 * Generates questions for a generate tag.
	 * 
	 * @param atts
	 *            Attributes of the generate tag
	 */
	public void generate(Attributes atts) {

		// check if there is a number of questions specified. If not: no
		// generation of anything.
		// TODO: maybe generate a default number here? Candidate for
		// GlobalDefaultSettings?
		if (atts.getValue("num-questions") == null) {
			logger.warning("Generate tag without specification of num-questions - nothing generated");
			return;
		}

		int numQuestions = randomNumber(atts.getValue("num-questions"));
		logger.finest(String.format("Generating %d questions", numQuestions));
		for (int i = 0; i < numQuestions; i++) {
			generateQuestion(atts);
		}
	}

	/**
	 * Generates a question for a generate tag.
	 * 
	 * @param atts
	 *            Attributes of the generate tag
	 */
	private void generateQuestion(Attributes atts) {
		AttributesImpl attsImpl = new AttributesImpl();
		Random rand = new Random();

		// find free ID
		while (usedIds.containsKey("question" + qIdCounter)) {
			qIdCounter++;
		}

		attsImpl.addAttribute(null, null, "id", null, "question" + qIdCounter);
		usedIds.put("question" + aIdCounter, "question");
		qIdCounter++;

		// one or comma-separated list, i.e., "mc", or "mc, oc"...
		String type = atts.getValue("type");
		if (type.contains(",")) {
			String[] types = type.split(",");

			// choses one of those given randomly
			// TODO: other way?!
			type = types[rand.nextInt(types.length)];
		}
		attsImpl.addAttribute(null, null, "type", null, type);

		String text = "Autogen Frage"; // default text

		// or if defined use questsion length to generate random text
		if (atts.getValue("question-length") != null) {
			text = randomTextCreator(atts.getValue("question-length"));
		}

		// add pseudo-title with appending a "?"
		attsImpl.addAttribute("", "", "title", "", text + "?");

		// write question XML element
		xmlWriter.openXMLElement("question", attsImpl);
		if (atts.getValue("num-answers") != null) {
			int numAnswers = randomNumber(atts.getValue("num-answers"));
			for (int i = 0; i < numAnswers; i++) {
				genrateAnswer(atts); // generate answers for this question
			}
		}
		xmlWriter.closeXMLElement("question");
	}

	/**
	 * Creates a random number out of a String like "20-30"; if only a number
	 * like "20" is given, it is returned simply.
	 * 
	 * @param str String the specifying random number/length String
	 * @return the random number; -1 if no correct number could be parsed
	 */
	private int randomNumber(String str) {

		// first test with a regex if the String matches either
		// "20-30" style or "20" style
		if (!str.matches("[1-9][0-9]*([-][1-9][0-9]*)?")) {
			logger.severe("Syntax Fehler in a generate tag: the given" +
					"length is not in the right syntax \"20-30\" or \"20\".");
			return -1;
		}

		int nr = 0;

		// if 2 numbers given, split the number String first
		if (str.contains("-")) {
			String len[] = str.split("-");
			int low = Integer.parseInt(len[0]);
			int high = Integer.parseInt(len[1]);
			if (low > high) {
				logger.severe("Error in a generate tag: "
						+ "upper bound lower than lower bound.");
				return -1;
			}

			// get random int between lower and upper bound
			nr = low + randomizer.nextInt(1 + high - low);

			// otherwise just parse and return the number
		} else {
			nr = Integer.parseInt(str);
		}
		return nr;
	}

	/**
	 * Creates some random Text for a given random-text-specs
	 * 
	 * @param str the random Text specification
	 * @return random textt
	 */
	private String randomTextCreator(String str) {

		// get length of the string
		int length = randomNumber(str);

		// default length if nothing else is set is 10
		if (length == -1) {
			length = 10;
		}

		// for questions that should be longer than the long expl string
		while (exampleStr.length() < length) {
			exampleStr += " " + exampleStr;
		}

		// start position: random between 0 and exString-length, because
		// resulting string after start must at least be one time as big
		// as specified length
		int start = randomizer.nextInt(exampleStr.length() - length);

		// return corresponding substring of exampleString
		return exampleStr.substring(start, start + length);
	}

}

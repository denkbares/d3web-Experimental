package de.knowwe.lod.quiz;

import java.util.ArrayList;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

public class YearOfBirthQuizHandler extends AbstractHTMLTagHandler {

	public static final String birthyearAttribute = "lns:Geburtsjahr";

	// Number of options.
	private static final int optionsCount = 3;

	// Range that is added to the real birthyear.
	private static int valueRange = 20;

	public YearOfBirthQuizHandler() {
		super("yearofbirthquiz");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {
		Rdf2GoCore core = Rdf2GoCore.getInstance();

		// optional difficulty.
		String range = parameters.get("range");

		if (range != null && !range.isEmpty()) {
			valueRange = Integer.parseInt(range);
		}

		String encodePerson = Strings.encodeURL("Historische Persönlichkeit");
		String concept = "";

		String namespace = Rdf2GoCore.localns;
		encodePerson = namespace + encodePerson;

		String query =
				"SELECT ?x WHERE {?x rdf:type <" + encodePerson + ">} ORDER BY ASC(?x)";

		ClosableIterator<QueryRow> result = core.sparqlSelectIt(query);
		ArrayList<String> persons = new ArrayList<String>();

		boolean found = false;
		while (!found) {
			while (result.hasNext()) {
				QueryRow row = result.next();
				String title = row.getValue("x").toString();
				String realTitle = Strings.decodeURL(title);
				realTitle = realTitle.substring(title.indexOf("#") + 1);
				persons.add(realTitle);
			}

			int size = persons.size();
			int choose = (int) ((Math.random() * size) + 1);
			concept = persons.get(choose - 1);
			String hasBirthyear =
					"ASK {<" + core.createlocalURI(concept) + "> "
							+ birthyearAttribute
							+ " ?y}";
			if (core.sparqlAsk(hasBirthyear)) {
				found = true;
			}
		}

		// SPARQL real birthplace.

		String realQuery =
				"SELECT ?y WHERE {<" + core.createlocalURI(concept) + "> " + birthyearAttribute
						+ " ?y}";

		ClosableIterator<QueryRow> real = core.sparqlSelectIt(realQuery);
		String realBirthYear = "";

		while (real.hasNext()) {
			QueryRow row = real.next();
			realBirthYear = row.getValue("x").toString();
			realBirthYear = Strings.decodeURL(realBirthYear);
			realBirthYear = realBirthYear.substring(realBirthYear.indexOf("#") + 1);
		}

		int year;
		String yearForQuestion = "";
		if (realBirthYear.startsWith("-")) {
			year = Integer.parseInt(realBirthYear.substring(0, realBirthYear.indexOf("-", 1)));
			yearForQuestion = Math.abs(year) + " v. Chr.";
		}
		else {
			year = Integer.parseInt(realBirthYear.substring(0, realBirthYear.indexOf("-")));
			yearForQuestion = year + " n. Chr.";
		}

		String options = "<div class=layout><div class='tags'>Wann wurde <div id='quizyearsubject' style='display:inline'>"
				+ concept
				+ "</div> geboren?</div><br/><div id='quizyearanswers'>";

		int setCorrect = (int) ((Math.random() * optionsCount) + 1);
		int addValue = (int) ((Math.random() * valueRange) + 1);
		int plusorminus = (int) ((Math.random() * 2) + 1);

		int counter = 0;

		while (counter < optionsCount) {
			String number = "I";
			for (int j = 0; j < counter; j++) {
				number += "I";
			}
			number += ".";
			int temp;
			String tempForQuestion = "";
			if (plusorminus == 1) {
				temp = year + addValue;
			}
			else {
				temp = year - addValue;
			}
			if (temp > 0) {
				tempForQuestion = temp + " n. Chr.";
			}
			else {
				if (temp == 0) {
					temp++;
				}
				tempForQuestion = Math.abs(temp) + " v. Chr.";
			}

			if ((setCorrect == 1 && !options.matches(".*>" + yearForQuestion + "</p>.*"))
					|| (counter == optionsCount - 1 && !options.contains(">" + yearForQuestion
							+ "</p>"))) {
				options += "<div><b>"
						+ number
						+ " </b><p class='mouselink' style='display:inline' onclick=\"submitYearQuiz(this);\">"
						+ yearForQuestion + "</p></div>";
				counter++;
			}
			else if (!options.matches(".*>" + tempForQuestion + "</p>.*")) {
				options += "<div><b>"
						+ number
						+ " </b><p class='mouselink' style='display:inline' onclick=\"submitYearQuiz(this);\">"
						+ tempForQuestion
						+ "</p></div>";
				counter++;
			}
			// reroll
			setCorrect = (int) ((Math.random() * optionsCount) + 1);
			addValue = (int) ((Math.random() * valueRange) + 1);
			plusorminus = (int) ((Math.random() * 2) + 1);
			options += "<br/>";
		}
		options += "</div></div>";

		return options;
	}

}

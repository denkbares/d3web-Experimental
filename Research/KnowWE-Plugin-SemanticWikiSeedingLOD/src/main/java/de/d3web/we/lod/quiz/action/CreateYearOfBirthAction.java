package de.d3web.we.lod.quiz.action;

import java.io.IOException;
import java.net.URLDecoder;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEParameterMap;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class CreateYearOfBirthAction extends AbstractAction {

	// Number of options.
	private static final int optionsCount = 3;

	// Range that is added to the real birthyear.
	private static final int valueRange = 10;

	@Override
	public void execute(ActionContext context) throws IOException {

		KnowWEParameterMap map = context.getKnowWEParameterMap();
		String input = map.get("input");

		// SPARQL real birthplace.
		// TODO: change attribute.
		String realQuery =
				"SELECT ?y WHERE {<" + input + "> lns:GeburtsJahr ?y}";

		TupleQueryResult real = SPARQLUtil.executeTupleQuery(realQuery);
		String realBirthYear = "";

		try {
			while (real.hasNext()) {
				BindingSet set = real.next();
				realBirthYear = set.getBinding("x").getValue().stringValue();
				realBirthYear = URLDecoder.decode(realBirthYear, "UTF-8");
				realBirthYear = realBirthYear.substring(realBirthYear.indexOf("#") + 1);
			}
		}
		catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		input = URLDecoder.decode(input, "UTF-8");
		input = input.substring(input.indexOf("#") + 1);

		int year = Integer.parseInt(realBirthYear);

		String options = "<div class='tags'>Wann wurde <div id='quizyearsubject' style='display:inline'>"
				+ input
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
			if (plusorminus == 1) {
				temp = year + addValue;
			}
			else {
				temp = year - addValue;
			}
			if ((setCorrect == 1 && !options.matches(".*>" + year + "</p>.*"))
					|| (counter == optionsCount - 1 && !options.contains(">" + year + "</p>"))) {
				options += "<div><b>"
						+ number
						+ " </b><p class='quizanswers' style='display:inline' onclick=\"submitYearQuiz(this);\">"
						+ year + "</p></div>";
				counter++;
			}
			else if (!options.matches(".*>" + temp + "</p>.*")) {
				options += "<div><b>"
						+ number
						+ " </b><p class='quizanswers' style='display:inline' onclick=\"submitYearQuiz(this);\">"
						+ temp
						+ "</p></div>";
				counter++;
			}
			// reroll
			setCorrect = (int) ((Math.random() * optionsCount) + 1);
			addValue = (int) ((Math.random() * valueRange) + 1);
			plusorminus = (int) ((Math.random() * 2) + 1);
		}
		options += "</div>";

		context.getWriter().write(options);

	}

}

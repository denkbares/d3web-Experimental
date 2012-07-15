package de.knowwe.lod.quiz.action;

import java.io.IOException;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.lod.quiz.YearOfBirthQuizHandler;
import de.knowwe.rdf2go.Rdf2GoCore;

public class SolveYearOfBirthAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		Map<String, String> map = context.getParameters();
		String answer = map.get("answer");
		String subject = Strings.encodeURL(map.get("subject"));

		String namespace = Rdf2GoCore.getInstance().getLocalNamespace();
		subject = namespace + subject;

		String realQuery =
				"SELECT ?y WHERE {<" + subject + "> " + YearOfBirthQuizHandler.birthyearAttribute
						+ " ?y}";

		ClosableIterator<QueryRow> real = Rdf2GoCore.getInstance().sparqlSelectIt(realQuery);
		String result = "";

		while (real.hasNext()) {
			QueryRow row = real.next();
			result = row.getValue("y").toString();
			result = Strings.decodeURL(result);
			result = result.substring(result.indexOf("#") + 1);
		}

		int year;
		String yearForQuestion = "";
		if (result.startsWith("-")) {
			year = Integer.parseInt(result.substring(0, result.indexOf("-", 1)));
			yearForQuestion = Math.abs(year) + " v. Chr.";
		}
		else {
			year = Integer.parseInt(result.substring(0, result.indexOf("-")));
			yearForQuestion = year + " n. Chr.";
		}

		if (answer.equals(yearForQuestion)) {
			context.getWriter().write(
					"<p>Ihre Antwort <b>"
							+ answer
							+ "</b> war richtig!</p>"
							+ "<div align='middle'><input type='button' onclick='window.location.reload()' value='Nächste Frage'></div>");
		}
		else {
			context.getWriter().write(
					"<p>Ihre Antwort <b>"
							+ answer
							+ "</b> war leider falsch. <br/>Die richtige Antwort lautete: <b>"
							+ yearForQuestion
							+ "</b></p>"
							+ "<div align='middle'><input type='button' onclick='window.location.reload()' value='Nächste Frage'></div>");
		}
	}
}

package de.d3web.we.lod.quiz.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.lod.quiz.YearOfBirthQuizHandler;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class SolveYearOfBirthAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		Map<String, String> map = context.getParameters();
		String answer = map.get("answer");
		String subject = map.get("subject");

		try {
			subject = URLEncoder.encode(subject, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String namespace = UpperOntology.getInstance().getLocaleNS();
		subject = namespace + subject;

		String realQuery =
				"SELECT ?y WHERE {<" + subject + "> " + YearOfBirthQuizHandler.birthyearAttribute
						+ " ?y}";

		TupleQueryResult real = SPARQLUtil.executeTupleQuery(realQuery);
		String result = "";

		try {
			while (real.hasNext()) {
				BindingSet set = real.next();
				result = set.getBinding("y").getValue().stringValue();
				result = URLDecoder.decode(result, "UTF-8");
				result = result.substring(result.indexOf("#") + 1);
			}
		}
		catch (QueryEvaluationException e) {
			e.printStackTrace();
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
					"<p>Ihre Antwort <b>" + answer
							+ "</b> war leider falsch. <br/>Die richtige Antwort lautete: <b>"
							+ yearForQuestion
							+ "</b></p>"
							+ "<div align='middle'><input type='button' onclick='window.location.reload()' value='Nächste Frage'></div>");
		}
	}

}

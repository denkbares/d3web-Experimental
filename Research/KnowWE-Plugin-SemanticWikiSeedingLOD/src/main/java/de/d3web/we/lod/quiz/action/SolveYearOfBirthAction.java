package de.d3web.we.lod.quiz.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.core.semantic.UpperOntology;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class SolveYearOfBirthAction extends AbstractAction {

	@Override
	public void execute(ActionContext context) throws IOException {

		KnowWEParameterMap map = context.getKnowWEParameterMap();
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
				"SELECT ?y WHERE {<" + subject + "> lns:GeburtsJahr ?y}";

		TupleQueryResult real = SPARQLUtil.executeTupleQuery(realQuery);
		String result = "";

		try {
			while (real.hasNext()) {
				BindingSet set = real.next();
				result = set.getBinding("x").getValue().stringValue();
				result = URLDecoder.decode(result, "UTF-8");
				result = result.substring(result.indexOf("#") + 1);
			}
		}
		catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		if (answer.equals(result)) {
			context.getWriter().write("<p>Ihre Antwort <b>" + answer + "</b> war richtig!</p>");
		}
		else {
			context.getWriter().write(
					"<p>Ihre Antwort <b>" + answer
							+ "</b> war leider falsch. <br/>Die richtige Antwort lautet: <b>"
							+ result
							+ "</b></p>");
		}
	}

}

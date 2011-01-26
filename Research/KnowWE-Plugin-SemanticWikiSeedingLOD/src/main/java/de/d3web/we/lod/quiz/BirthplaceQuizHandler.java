package de.d3web.we.lod.quiz;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.wikiConnector.KnowWEUserContext;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class BirthplaceQuizHandler extends AbstractHTMLTagHandler {

	public BirthplaceQuizHandler() {
		super("birthplacequiz");
	}

	@Override
	public String renderHTML(String topic, KnowWEUserContext user, Map<String, String> parameters, String web) {

		String query =
				"SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		TupleQueryResult result = SPARQLUtil.executeTupleQuery(query);

		StringBuffer input = new StringBuffer();

		input.append("<form action='javascript:getPlaceQuiz()'>"
				+ "<div class='layout' id='quizdataplace'><p class='tags'>Geburtsorts-Quiz</p>"
				+ "<SELECT id='quizinputplace'>");

		String encodePerson = "";
		String namespace = UpperOntology.getInstance().getLocaleNS();

		try {
			while (result.hasNext()) {

				BindingSet set = result.next();
				String title = set.getBinding("x").getValue().stringValue();

				encodePerson = URLEncoder.encode(
						"Historische Persönlichkeit", "UTF-8");
				encodePerson = namespace + encodePerson;

				String person = "ASK {<" + title + "> rdf:type <"
						+ encodePerson + ">}";

				if (SPARQLUtil.executeBooleanQuery(person)) {
					String realTitle = URLDecoder.decode(title, "UTF-8");
					realTitle = realTitle.substring(title.indexOf("#") + 1);
					input.append("<OPTION value='" + title + "'>" + realTitle + "</OPTION>");
				}
			}
		}
		catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		input.append("</SELECT>"
				+ "<input type='submit' onclick='getPlaceQuiz();' value='Quiz it!'>"
				+ "</div></form>");
		return input.toString();
	}
}

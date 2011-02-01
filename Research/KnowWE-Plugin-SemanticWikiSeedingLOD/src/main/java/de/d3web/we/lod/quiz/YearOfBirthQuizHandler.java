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

public class YearOfBirthQuizHandler extends AbstractHTMLTagHandler {

	public YearOfBirthQuizHandler() {
		super("yearofbirthquiz");
	}

	@Override
	public String renderHTML(String topic, KnowWEUserContext user, Map<String, String> parameters, String web) {

		String query =
				"SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		TupleQueryResult result = SPARQLUtil.executeTupleQuery(query);

		StringBuffer input = new StringBuffer();

		input.append("<form>"
				+ "<div class='layout' id='quizdatayear'><p class='tags'>Geburtsjahr-Quiz</p>"
				+ "<SELECT id='quizinputyear'>");

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
				+ "<input type='button' onclick='getYearQuiz();' value='Quiz it!'>"
				+ "</div></form>");
		return input.toString();
	}

}

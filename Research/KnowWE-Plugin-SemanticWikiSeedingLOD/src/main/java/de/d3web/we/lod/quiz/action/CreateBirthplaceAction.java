package de.d3web.we.lod.quiz.action;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEParameterMap;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class CreateBirthplaceAction extends AbstractAction {

	private static final int optionsCount = 3;

	@Override
	public void execute(ActionContext context) throws IOException {

		KnowWEParameterMap map = context.getKnowWEParameterMap();
		String input = map.get("input");

		// SPARQL real birthplace and fake ones.
		String realQuery =
				"SELECT ?y WHERE {<" + input + "> lns:GeburtsOrt ?y}";

		// Attribut in Verbindung möglich Vater, Mutter, Umgang, Lehrer
		// Bestimmter radius? sparql attribut entfernung?

		String fakeQuery1 = "SELECT ?x WHERE {<" + input + "> lns:Umgang ?y ."
				+ "?y lns:GeburtsOrt ?x"
				+ "}";

		String fakeQuery2 = "SELECT ?x WHERE {<" + input + "> lns:Vater ?y ."
				+ "?y lns:GeburtsOrt ?x"
				+ "}";

		// Falls GeburtsOrt != TodesOrt
		String fakeQuery3 = "SELECT ?x WHERE {<" + input + "> rdf:mentions ?x}";

		TupleQueryResult real = SPARQLUtil.executeTupleQuery(realQuery);

		List<TupleQueryResult> fakes = new ArrayList<TupleQueryResult>();

		fakes.add(SPARQLUtil.executeTupleQuery(fakeQuery1));
		fakes.add(SPARQLUtil.executeTupleQuery(fakeQuery2));
		fakes.add(SPARQLUtil.executeTupleQuery(fakeQuery3));

		List<String> fakeBirthPlaces = new ArrayList<String>();
		String realBirthPlace = "";

		try {
			while (real.hasNext()) {
				BindingSet set = real.next();
				realBirthPlace = set.getBinding("x").getValue().stringValue();
				realBirthPlace = URLDecoder.decode(realBirthPlace, "UTF-8");
				realBirthPlace = realBirthPlace.substring(realBirthPlace.indexOf("#") + 1);
			}
			for (TupleQueryResult result : fakes) {
				while (result.hasNext()) {
					BindingSet set = result.next();
					String fakeBirthPlace = set.getBinding("x").getValue().stringValue();
					fakeBirthPlace = URLDecoder.decode(fakeBirthPlace, "UTF-8");
					fakeBirthPlace = fakeBirthPlace.substring(fakeBirthPlace.indexOf("#") + 1);
					if (!fakeBirthPlace.equals(realBirthPlace)) {
						fakeBirthPlaces.add(fakeBirthPlace);
					}
				}
			}
		}
		catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		input = URLDecoder.decode(input, "UTF-8");
		input = input.substring(input.indexOf("#") + 1);
		String options = "<p class='tags'>Wo wurde <div id='quizplacesubject>" + input
				+ "</div> geboren?<br/><div id='quizplaceanswers'>";

		int size = fakeBirthPlaces.size();

		int setCorrect = (int) ((Math.random() * optionsCount) + 1);
		int randomFakelist = (int) ((Math.random() * size));

		for (int i = 0; i < optionsCount; i++) {
			if ((setCorrect == 1 && !options.contains(realBirthPlace))
					|| (i == optionsCount - 1 && !options.contains(realBirthPlace))) {
				options += "<p onlick=\"submitPlaceQuiz('this')\">" + realBirthPlace + "</p>";
			}
			else if (!options.contains(fakeBirthPlaces.get(randomFakelist))) {
				options += "<p onlick=\"submitPlaceQuiz('this')\">"
						+ fakeBirthPlaces.get(randomFakelist)
						+ "</p>";
			}
			// reroll
			setCorrect = (int) ((Math.random() * optionsCount) + 1);
			randomFakelist = (int) ((Math.random() * size));
		}
		options += "</div>";

		context.getWriter().write(options);

	}
}

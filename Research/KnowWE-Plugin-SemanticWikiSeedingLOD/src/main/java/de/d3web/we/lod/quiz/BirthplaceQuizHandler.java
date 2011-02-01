package de.d3web.we.lod.quiz;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.lod.quiz.map.MapForConcepts;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.wikiConnector.KnowWEUserContext;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class BirthplaceQuizHandler extends AbstractHTMLTagHandler {

	private static final int optionsCount = 3;

	public BirthplaceQuizHandler() {
		super("birthplacequiz");
	}

	@Override
	public String renderHTML(String topic, KnowWEUserContext user, Map<String, String> parameters, String web) {

		// Select concept.
		String concept = parameters.get("concept");

		// Choose random.
		if (concept == null || concept.isEmpty()) {

			String encodePerson = "";

			try {
				encodePerson = URLEncoder.encode(
						"Historische Persönlichkeit", "UTF-8");
			}
			catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			String namespace = UpperOntology.getInstance().getLocaleNS();
			encodePerson = namespace + encodePerson;

			String query =
					"SELECT ?x WHERE {?x rdf:type <" + encodePerson + ">} ORDER BY ASC(?x)";

			TupleQueryResult result = SPARQLUtil.executeTupleQuery(query);
			ArrayList<String> persons = new ArrayList<String>();

			try {
				while (result.hasNext()) {
					BindingSet set = result.next();
					String title = set.getBinding("x").getValue().stringValue();
					String realTitle = URLDecoder.decode(title, "UTF-8");
					realTitle = realTitle.substring(title.indexOf("#") + 1);
					persons.add(realTitle);
				}
			}
			catch (QueryEvaluationException e) {
				e.printStackTrace();
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			int size = persons.size();
			int choose = (int) ((Math.random() * size) + 1);
			concept = persons.get(choose - 1);
		}

		// Get Birthplace and some fake ones.

		// SPARQL real birthplace.
		// TODO: attribute.
		OwlHelper helper = SemanticCoreDelegator.getInstance().getUpper().getHelper();
		String realQuery =
				"SELECT ?y WHERE {<" + helper.createlocalURI(concept) + "> lns:GeburtsOrt ?y}";

		// All geo with coordinates.
		String allGeo = "SELECT ?x WHERE {?x rdf:type lns:Geographika. ?x lns:hasLongitude ?y. ?x lns:hasLatitude ?z.}";
		TupleQueryResult real = SPARQLUtil.executeTupleQuery(realQuery);

		TupleQueryResult fakes = SPARQLUtil.executeTupleQuery(allGeo);

		List<String> fakeBirthPlaces = new ArrayList<String>();
		String realBirthPlace = "";

		try {
			while (real.hasNext()) {
				BindingSet set = real.next();
				realBirthPlace = set.getBinding("x").getValue().stringValue();
				realBirthPlace = URLDecoder.decode(realBirthPlace, "UTF-8");
				realBirthPlace = realBirthPlace.substring(realBirthPlace.indexOf("#") + 1);
			}
			while (fakes.hasNext()) {
				BindingSet set = fakes.next();
				String fakeBirthPlace = set.getBinding("x").getValue().stringValue();
				fakeBirthPlace = URLDecoder.decode(fakeBirthPlace, "UTF-8");
				fakeBirthPlace = fakeBirthPlace.substring(fakeBirthPlace.indexOf("#") + 1);
				if (!fakeBirthPlace.equals(realBirthPlace)) {
					fakeBirthPlaces.add(fakeBirthPlace);
				}
			}
		}
		catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String options = "<div class='layout'><div class='tags' align='middle'>Wo wurde <div id='quizplacesubject' style='display:inline'>"
				+ concept
				+ "</div> geboren?</div><br/><div id='quizplacemap'>";

		int size = fakeBirthPlaces.size();

		int setCorrect = (int) ((Math.random() * optionsCount) + 1);
		int randomFakelist = (int) ((Math.random() * size));

		int counter = 0;

		ArrayList<String> concepts = new ArrayList<String>();

		// TODO: only for testing (attribute). delete after changing it on top.
		realBirthPlace = "Damaskus";

		while (counter < optionsCount) {
			if ((setCorrect == 1 && !concepts.contains(realBirthPlace))
					|| (counter == optionsCount - 1) && !concepts.contains(realBirthPlace)) {
				concepts.add(realBirthPlace);
				counter++;
			}
			else if (!concepts.contains(fakeBirthPlaces.get(randomFakelist))) {
				concepts.add(fakeBirthPlaces.get(randomFakelist));
				counter++;
			}
			// reroll
			setCorrect = (int) ((Math.random() * optionsCount) + 1);
			randomFakelist = (int) ((Math.random() * size));
		}

		String card = MapForConcepts.showMapForConcepts(concepts, realBirthPlace);

		options += card + "</div></div>";

		return options;
	}
}

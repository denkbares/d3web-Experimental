package de.knowwe.lod.quiz;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.user.UserContext;
import de.knowwe.lod.quiz.map.MapForConcepts;
import de.knowwe.rdf2go.Rdf2GoCore;

public class BirthplaceQuizHandler extends AbstractHTMLTagHandler {

	private static final String birthplaceAttribute = "lns:GeburtsOrt";

	private static final int optionsCount = 3;

	public BirthplaceQuizHandler() {
		super("birthplacequiz");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {
		Rdf2GoCore core = Rdf2GoCore.getInstance();

		String encodePerson = "";

		try {
			encodePerson = URLEncoder.encode(
						"Historische Persönlichkeit", "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		String namespace = Rdf2GoCore.localns;
		encodePerson = namespace + encodePerson;

		String query =
					"SELECT ?x WHERE {?x rdf:type <" + encodePerson + ">} ORDER BY ASC(?x)";

		ClosableIterator<QueryRow> result = core.sparqlSelectIt(query);
		ArrayList<String> persons = new ArrayList<String>();

		boolean found = false;

		String realBirthPlace = "";
		String concept = "";

		while (!found) {
			try {
				while (result.hasNext()) {
					QueryRow row = result.next();
					String title = row.getValue("x").toString();
					String realTitle = URLDecoder.decode(title, "UTF-8");
					realTitle = realTitle.substring(title.indexOf("#") + 1);
					persons.add(realTitle);
				}
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			int size = persons.size();
			int choose = (int) ((Math.random() * size) + 1);
			concept = persons.get(choose - 1);
			String hasBirthplace =
						"ASK {<" + core.createlocalURI(concept) + "> " + birthplaceAttribute
								+ " ?y}";
			String birthplace =
						"SELECT ?y WHERE {<" + core.createlocalURI(concept) + "> "
								+ birthplaceAttribute
								+ " ?y}";
			if (Rdf2GoCore.getInstance().sparqlAsk(hasBirthplace)) {
				ClosableIterator<QueryRow> real = core.sparqlSelectIt(birthplace);
				try {
					while (real.hasNext()) {
						QueryRow row = real.next();
						realBirthPlace = row.getValue("x").toString();
						realBirthPlace = URLDecoder.decode(realBirthPlace, "UTF-8");
						realBirthPlace = realBirthPlace.substring(realBirthPlace.indexOf("#") + 1);
					}
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				String hasCoords = "ASK {<" + core.createlocalURI(realBirthPlace)
						+ "> rdf:type lns:Geographika." +
						"<" + core.createlocalURI(realBirthPlace) + "> lns:hasLongitude ?y." +
								"<" + core.createlocalURI(realBirthPlace)
						+ "> lns:hasLatitude ?z.}";
				if (core.sparqlAsk(hasCoords)) {
					found = true;
				}
			}
		}

		// Get Birthplace and some fake ones.

		// All geo with coordinates.
		String allGeo = "SELECT ?x WHERE {?x rdf:type lns:Geographika. ?x lns:hasLongitude ?y. ?x lns:hasLatitude ?z.}";

		ClosableIterator<QueryRow> fakes = core.sparqlSelectIt(allGeo);

		List<String> fakeBirthPlaces = new ArrayList<String>();

		try {
			while (fakes.hasNext()) {
				QueryRow row = fakes.next();
				String fakeBirthPlace = row.getValue("x").toString();
				fakeBirthPlace = URLDecoder.decode(fakeBirthPlace, "UTF-8");
				fakeBirthPlace = fakeBirthPlace.substring(fakeBirthPlace.indexOf("#") + 1);
				if (!fakeBirthPlace.equals(realBirthPlace)) {
					fakeBirthPlaces.add(fakeBirthPlace);
				}
			}
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

		options += card
				+ "</div><br/><div align='right'><input type='button' onclick='window.location.reload()' value='Nächste Frage'></div></div>";

		return options;
	}
}

package tests;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryRow;

import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.packaging.KnowWEPackageManager;
import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.hermes.TimeEvent;
import de.d3web.we.kdom.RootType;
import de.d3web.we.kdom.semanticAnnotation.SemanticAnnotation;
import dummies.KnowWETestWikiConnector;

public class TimeEventTest extends TestCase {

	private KnowWEEnvironment ke;
	private KnowWEArticleManager am;
	private RootType type;

	@Override
	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();

		KnowWEPackageManager.overrideAutocompileArticle(true);

		RootType.getInstance().addChildType(new SemanticAnnotation());

		KnowWEEnvironment.initKnowWE(new KnowWETestWikiConnector());
		ke = KnowWEEnvironment.getInstance();
		type = ke.getRootType();
		am = ke.getArticleManager(KnowWEEnvironment.DEFAULT_WEB);
	}

	@Test
	public void testTimeEvent() {
		List<TimeEvent> eventsInput = new ArrayList<TimeEvent>();
		List<TimeEvent> eventsOutput = new ArrayList<TimeEvent>();

		String testtopic = "Testpage";
		List<String> sources = new ArrayList<String>();
		eventsInput.add(new TimeEvent("Testing", "!§$&/=?#-.,;:_", 1, sources, "8jv-9jv", "",
				testtopic));
		eventsInput.add(new TimeEvent("MeinTestevent", "Beschreibung dieses Events", 1, sources,
				"8jv", "", testtopic));
		eventsInput.add(new TimeEvent("Noch ein Event", "eine Test-Beschreibung", 2, sources,
				"1jv",
				"", testtopic));
		eventsInput.add(new TimeEvent("MeinTestevent", "Beschreibung dieses Events", 1, sources,
				"8jv", "", testtopic));

		// added line break because the new time event markup works only with a
		// beginning linebreak
		String content = "\n";
		// Create Page-Content from eventsInput
		for (TimeEvent t : eventsInput) {
			content += "<<" + t.getTitle() + "(" + t.getImportance() + ")\n"
					+ t.getTime().getEncodedString() + "\n" + t.getDescription() + "\n>>\n\n";
		}

		System.out.println(content);

		// Removing duplicate event from the input list
		eventsInput.remove(3);

		ke.processAndUpdateArticleJunit(null, content, testtopic, KnowWEEnvironment.DEFAULT_WEB,
				type);

		String querystring = "select ?x ?t ?desc ?dd ?imp  where {?x lns:hasDescription ?desc . ?x lns:hasTitle ?t . ?x lns:hasDateDescription ?dd . ?x lns:hasImportance ?imp }";
		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(querystring);

		try {
			while (result.hasNext()) {
				QueryRow row = result.next();
				System.out.println("result next");

				String title = row.getValue("t").toString();
				String impS = row.getValue("imp").asDatatypeLiteral().getValue().toString();
				String desc = row.getValue("desc").toString();
				String src = row.getValue("imp").toString();
				String dateDesc = row.getValue("dd").toString();

				try {
					title = URLDecoder.decode(title, "UTF-8");
					desc = URLDecoder.decode(desc, "UTF-8");
					src = URLDecoder.decode(src, "UTF-8");
					impS = URLDecoder.decode(impS, "UTF-8");
					dateDesc = URLDecoder.decode(dateDesc, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				// Fix intentional differences between new TimeEvent and the old
				// one
				if (impS.length() == 3) {
					impS = impS.substring(1, 2);
					desc = desc.substring(1, desc.length() - 1);
				}

				int imp = Integer.parseInt(impS);
				TimeEvent te = new TimeEvent(title, desc, imp, new ArrayList<String>(), dateDesc,
						"", testtopic);
				eventsOutput.add(te);
				System.out.println(title + "+++" + desc + "+++" + imp + "+++" + dateDesc + "+++"
						+ testtopic);
			}
		}
		catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ModelRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue("Count of sparqled TimeEvents events is " + eventsOutput.size() + ", should be "
				+ eventsInput.size(), eventsInput.size() == eventsOutput.size());
		for (TimeEvent t : eventsInput) {
			assertTrue("TimeEvent " + t + "could not be found in sparql result",
					eventsOutput.contains(t));
		}
	}
}

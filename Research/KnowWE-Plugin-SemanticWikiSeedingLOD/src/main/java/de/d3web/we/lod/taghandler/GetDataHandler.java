package de.d3web.we.lod.taghandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.lod.ConceptType;
import de.d3web.we.lod.LinkedOpenDataSet;
import de.d3web.we.lod.markup.DBpediaContentType;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class GetDataHandler extends AbstractHTMLTagHandler {

	private static final String articleforMapping = "DBpediaMapping";

	public GetDataHandler() {
		super("getdata");

	}

	@SuppressWarnings("deprecation")
	@Override
	public String renderHTML(String topic, KnowWEUserContext user,
			Map<String, String> parameters, String web) {

		String concept = parameters.get("concept");
		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(web,
				articleforMapping);
		List<Section<DBpediaContentType>> found = new Vector<Section<DBpediaContentType>>();
		article.getSection().findSuccessorsOfType(DBpediaContentType.class,
				found);
		String dbpediaMapping = "";
		String hermesMapping = "";
		for (Section<DBpediaContentType> t : found) {
			String temp = t.getChildren().get(0).getOriginalText();
			if (temp.matches(concept + " =>.*")) {
				dbpediaMapping = temp.substring(temp.indexOf(" => ") + 4);
				hermesMapping = temp.substring(0, temp.indexOf(" =>"));
			}
		}
		String encodePerson = "";
		try {
			hermesMapping = URLEncoder.encode(hermesMapping, "UTF-8");
			encodePerson = URLEncoder.encode("Historische Persönlichkeit",
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String namespace = UpperOntology.getInstance().getLocaleNS();
		hermesMapping = namespace + hermesMapping;

		// Concepttypes
		String ereignis = "ASK {<" + hermesMapping + "> rdf:type lns:Ereignis}";
		String person = "ASK {<" + hermesMapping + "> rdf:type <" + namespace
				+ encodePerson + ">}";
		String geographika = "ASK {<" + hermesMapping
				+ "> rdf:type lns:Geographika}";

		// String select = "SELECT ?x WHERE {<"+hermesMapping+"> rdf:type ?x}";
		// TupleQueryResult result2 = SPARQLUtil.executeTupleQuery(select);
		// try {
		// while (result2.hasNext()) {
		// BindingSet set = result2.next();
		// String title = set.getBinding("x").getValue().stringValue();
		// try {
		// title = URLDecoder.decode(title, "UTF-8");
		// System.out.println(title);
		//
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// }
		// } catch (QueryEvaluationException e) {
		// e.printStackTrace();
		// }

		LinkedOpenDataSet set = LinkedOpenDataSet.getInstance();
		HashMap<String, HashSet<Object>> result = new HashMap<String, HashSet<Object>>();
		// TODO: parse Data + JS.
		if (dbpediaMapping != "") {
			if (SPARQLUtil.executeBooleanQuery(ereignis)) {
				System.out.println("e");
				result = set.getLOD(ConceptType.Ereignis).getData(
						dbpediaMapping);
			} else if (SPARQLUtil.executeBooleanQuery(person)) {
				System.out.println("p");
				result = set.getLOD(ConceptType.Person).getData(dbpediaMapping);
			} else if (SPARQLUtil.executeBooleanQuery(geographika)) {
				System.out.println("g");
				result = set.getLOD(ConceptType.Geographika).getData(
						dbpediaMapping);
			}
		}

		StringBuffer buffy = new StringBuffer();
		buffy.append("<form name='form1' onsubmit='return getdata()' action='http://www.example.org/cgi-bin/feedback.cgi'>"
				+ "<table width='35%' border='1' cellpadding='5' cellspacing='1'>"
				+ "<tr><th colspan='2' width='70%' >"
				+ concept
				+ "</th><th>&Uuml;bernehmen</th><th>Abbrechen</th><th>Post-Save</th></tr>");
		// IF Object (wie gebdatum) formatiere output (date)
		int i = 0;
		for (String s : result.keySet()) {
			if (!result.get(s).isEmpty()) {
				for (Object o : result.get(s)) {
					String add = o.toString();
					if (o instanceof GregorianCalendar) {
						add = ((GregorianCalendar) o).get(GregorianCalendar.DAY_OF_MONTH) + "-"
								+ ((GregorianCalendar) o).get(GregorianCalendar.MONTH) + "-"
								+ ((GregorianCalendar) o).get(GregorianCalendar.YEAR);
					}
					buffy.append("<tr><td valign='middle' align='middle' width='20%'>"
							+ s
							+ "<td valign='middle' align='middle' width='50%'>"
							+ add + "</td>");
					buffy.append("<td valign='middle' align='middle'><input type='radio' name='Text"
							+ i
							+ "' value='&Uuml;bernehmen'></td>"
							+ "<td valign='middle' align='middle'><input type='radio' name='Text"
							+ i
							+ "' value='Abbrechen'></td>"
							+ "<td valign='middle' align='middle'><input type='radio' name='Text"
							+ i + "' value='Post-Save'></td></tr>");
					i++;
				}
			}
		}
		buffy.append(" <tr><td colspan='5' valign='middle' align='right' ><input type='submit' name='submit' value='Best&auml;tigen'>"
				+ "<input type='button' name='cancel' value='Abbrechen'></td></tr></table></form>");
		return KnowWEUtils.maskHTML(buffy.toString());

		// return "<form action=\"KnowWE.jsp?page="
		// + topic
		// +
		// "\" name=\"testform\" accept-charset=\"UTF-8\" method=\"post\" enctype=\"application/x-www-form-urlencoded\">"
		// + "<p>Concept: <input id='concept'  type='text' size='30' />"
		// + "<input onclick='test()' type='button' value='Go'/>";

	}
}

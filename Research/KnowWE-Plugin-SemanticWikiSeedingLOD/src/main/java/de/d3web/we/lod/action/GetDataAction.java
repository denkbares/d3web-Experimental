package de.d3web.we.lod.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.lod.ConceptType;
import de.d3web.we.lod.LinkedOpenData;
import de.d3web.we.lod.LinkedOpenDataSet;
import de.d3web.we.lod.markup.DBpediaContentType;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class GetDataAction extends AbstractAction {

	private static final String articleforMapping = "DBpediaMapping";

	@SuppressWarnings("deprecation")
	@Override
	public void execute(ActionContext context) throws IOException {

		KnowWEParameterMap map = context.getKnowWEParameterMap();
		String concept = map.get("concept");
		String web = map.get("web");

		// TODO: Suche Konzept else Meldung!
		if (concept.isEmpty()) {
			// schreibe eingabefeld + Meldung
			context.getWriter().write("Bitte Konzept eingeben.");
		}

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

		LinkedOpenDataSet set = LinkedOpenDataSet.getInstance();
		HashMap<String, HashSet<Object>> result = new HashMap<String, HashSet<Object>>();
		LinkedOpenData var = new LinkedOpenData();
		if (dbpediaMapping != "") {
			if (SPARQLUtil.executeBooleanQuery(ereignis)) {
				System.out.println("e");
				var = set.getLOD(ConceptType.Ereignis);
				result = var.getData(dbpediaMapping);
			} else if (SPARQLUtil.executeBooleanQuery(person)) {
				System.out.println("p");
				var = set.getLOD(ConceptType.Person);
				result = var.getData(dbpediaMapping);
			} else if (SPARQLUtil.executeBooleanQuery(geographika)) {
				System.out.println("g");
				var = set.getLOD(ConceptType.Geographika);
				result = var.getData(dbpediaMapping);
			}
		}

		HashMap<String, List<Object>> inverse = var.getInverseMap();
		StringBuffer buffy = new StringBuffer();
		buffy.append("<form name='form1' onsubmit='return getdata()' action='http://www.example.org/cgi-bin/feedback.cgi'>"
				+ "<table width='35%' border='1' cellpadding='5' cellspacing='1'>"
				+ "<tr><th colspan='2' width='70%' >"
				+ concept
				+ "</th><th>&Uuml;bernehmen</th><th>Abbrechen</th><th>Post-Save</th></tr>");

		int i = 0;
		for (String s : result.keySet()) {
			if (!result.get(s).isEmpty()) {
				List<Object> mapped = inverse.get(s);
				for (Object o : result.get(s)) {
					String add = o.toString();
					if (o instanceof GregorianCalendar) {
						add = ((GregorianCalendar) o)
								.get(GregorianCalendar.DAY_OF_MONTH)
								+ "-"
								+ ((GregorianCalendar) o)
										.get(GregorianCalendar.MONTH)
								+ "-"
								+ ((GregorianCalendar) o)
										.get(GregorianCalendar.YEAR);
					}
					buffy.append("<tr><td valign='middle' align='middle' width='20%'>"
							+ "<p title="
							+ mapped.get(mapped.lastIndexOf(o) - 1)
							+ ">"
							+ s
							+ "</p>"
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

		context.getWriter().write(buffy.toString());
	}
}

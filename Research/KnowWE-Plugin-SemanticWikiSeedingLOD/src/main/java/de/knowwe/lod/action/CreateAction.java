package de.knowwe.lod.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.lod.ConceptType;
import de.knowwe.lod.LinkedOpenData;
import de.knowwe.lod.LinkedOpenDataSet;

public class CreateAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String concept = context.getParameter("concept");
		String wikipedia = context.getParameter("wikipedia");

		String dbpediaConcept = LinkedOpenData.getDBpediaRedirect(concept);

		if (!wikipedia.isEmpty()) {
			dbpediaConcept = LinkedOpenData.getResourceforWikipedia(wikipedia);
		}

		LinkedOpenDataSet set = LinkedOpenDataSet.getInstance();
		HashMap<String, HashSet<String>> result = new LinkedHashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> dbResult = new LinkedHashMap<String, HashSet<String>>();
		LinkedOpenData var = new LinkedOpenData();

		var = set.getLOD(ConceptType.Typ);
		dbResult = var.getLODdata(dbpediaConcept);
		result = var.getHermesData(dbResult);

		HashMap<String, List<String>> inverse = var.getInverseMap();
		StringBuffer buffy = new StringBuffer();

		buffy.append("<form id='lodwizard' class='layout'><table border='0' cellpadding='5' cellspacing='1'>"
				+ "<tr><th id='conceptname' colspan='3' class='createtopic'>"
				+ concept
				+ "</th></tr>"
				+ "<tr><td colspan='3' align='center' valign='middle' class='conceptcreate'>Erstelle Konzept</td></tr>"
				+ "<tr><td><div class='spacingtop' /></td></tr></tr>");

		// Sort A-Z
		// SortedSet<String> sortedset = new TreeSet<String>(result.keySet());
		Iterator<String> it = result.keySet().iterator();

		int i = 0;
		while (it.hasNext()) {
			String s = it.next();
			List<String> mapped = inverse.get(s);
			for (String resultS : result.get(s)) {
				if (!resultS.isEmpty()) {

					// Create title for html, if values are created from
					// multiple dbpedia properties.
					String title = "";
					for (int j = 0; j < mapped.size(); j++) {
						if (mapped.get(j).equals(resultS)) {
							title += mapped.get(j - 1) + ",&nbsp;";
						}
					}
					if (!title.isEmpty()) {
						title = title.substring(0, title.length() - 7);
					}

					buffy.append("<tr><td valign='middle' align='middle'>"
								+ "<p id='hermestag" + i + "' title="
								+ title
								+ " class='tags'>"
								+ s
								+ "</p>"
								+ "<td valign='middle' align='middle'><p id='dbpediavalue"
								+ i + "'>"
								+ resultS
								+ "</p></td>");

					buffy.append("<td><input type='button' id='submit"
							+ i
							+ "' onclick='buttonToggleCreate(this);' class='submit'>"
							+ "<input type='button' id='qmarks"
							+ i
							+ "' onclick='buttonToggleCreate(this);' class='qmarksc'></td></tr>");
					i++;
				}
			}
		}

		if (i != 0) {
			buffy.append("<tr><td colspan='3'><div class='spacingtop'></div></td></tr>"
					+ "<tr><td valign='middle' align='middle' colspan='3' style='color:#ff9900'><b> Optional - manuelle Eingabe des Typs :</b>"
					+ "</td></tr>");
		}

		buffy.append("<tr><td valign='middle' align='middle'>"
				+ "<p id='hermestag" + i + "' class='tags'>"
				+ "rdf:type"
				+ "</p>"
				+ "<td valign='middle' align='middle'>"
				+ "<SELECT id='dbpediavalue" + i + "'>"
				+ "<OPTION value='Historische Persönlichkeit'>Historische Persönlichkeit</OPTION>"
				+ "<OPTION value='Geographika'>Geographika</OPTION>"
				+ "<OPTION value='Ereignis'>Ereignis</OPTION>"
				+ "</SELECT>"
				+ "</td>");

		buffy.append("<td><input type='button' id='submitoptional' onclick='buttonToggleCreateOptional(this,"
				+ i
				+ ");' class='submit'>"
				+ "<input type='button' id='qmarksoptional' onclick='buttonToggleCreateOptional(this,"
				+ i + ");' class='qmarksc'></td></tr>");
		i++;
		buffy.append("<tr><td colspan='3'><div class='spacingbuttons'/></td></tr><tr><td colspan='3' valign='middle' align='right'><img src='KnowWEExtension/images/submit.png'"
				+ "onmouseover='changeOnMouseOver(this);' onmouseout='changeOnMouseOut(this);'"
				+ "onclick='submitDataCreate("
				+ i
				+ ",\""
				+ wikipedia
				+ "\");' class='buttons'>"
				+ "<img src='KnowWEExtension/images/cancel.png' onmouseover='changeOnMouseOver(this);' "
				+ "onmouseout='changeOnMouseOut(this);' onclick='cancelWizard();' class='buttons'></td></tr></table></form><br />");
		context.getWriter().write(buffy.toString());
	}
}

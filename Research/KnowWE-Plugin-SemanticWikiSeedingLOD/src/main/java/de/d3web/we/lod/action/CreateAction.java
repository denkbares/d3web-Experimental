package de.d3web.we.lod.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.lod.ConceptType;
import de.d3web.we.lod.LinkedOpenData;
import de.d3web.we.lod.LinkedOpenDataSet;

public class CreateAction extends AbstractAction {

	@Override
	public void execute(ActionContext context) throws IOException {

		KnowWEParameterMap map = context.getKnowWEParameterMap();

		String concept = map.get("concept");
		String wikipedia = map.get("wikipedia");

		String dbpediaConcept = LinkedOpenData.getDBpediaRedirect(concept);

		if (!wikipedia.isEmpty()) {
			dbpediaConcept = LinkedOpenData.getResourceforWikipedia(wikipedia);
		}

		LinkedOpenDataSet set = LinkedOpenDataSet.getInstance();
		HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> dbResult = new HashMap<String, HashSet<String>>();
		LinkedOpenData var = new LinkedOpenData();
		
		var = set.getLOD(ConceptType.Typ);
		dbResult = var.getLODdata(dbpediaConcept);
		result = var.getHermesData(dbResult);

		HashMap<String, List<String>> inverse = var.getInverseMap();
		StringBuffer buffy = new StringBuffer();
		StringBuffer css = new StringBuffer();
		css.append("<style type=\"text/css\"> .submit {cursor:pointer;cursor:hand;padding:0;margin:0;"
				+ "border:none;background: url(KnowWEExtension/images/b1g.png) no-repeat center;height:32px;width:45px;}"
				+ ".submitc {cursor:pointer;cursor:hand;padding:0;margin:0;"
				+ "border:none;background: url(KnowWEExtension/images/b2g.png) no-repeat center;height:32px;width:45px;}"
				+ ".ignore {cursor:pointer;cursor:hand;padding:0;margin:0;"
				+ "border:none;background: url(KnowWEExtension/images/b1x.png) no-repeat center;height:32px;width:45px;}"
				+ ".ignorec {cursor:pointer;cursor:hand;padding:0;margin:0;"
				+ "border:none;background: url(KnowWEExtension/images/b2x.png) no-repeat center;height:32px;width:45px;}"
				+ ".return {cursor:pointer;cursor:hand;padding:0;margin:0;"
				+ "border:none;background: url(KnowWEExtension/images/b1r.png) no-repeat center;height:32px;width:45px;}"
				+ ".returnc {cursor:pointer;cursor:hand;padding:0;margin:0;"
				+ "border:none;background: url(KnowWEExtension/images/b2r.png) no-repeat center;height:32px;width:45px;}"
				+ ".qmarks {cursor:pointer;cursor:hand;padding:0;margin:0;"
				+ "border:none;background: url(KnowWEExtension/images/b1a.png) no-repeat center;height:32px;width:45px;}"
				+ ".qmarksc {cursor:pointer;cursor:hand;padding:0;margin:0;"
				+ "border:none;background: url(KnowWEExtension/images/b2a.png) no-repeat center;height:32px;width:45px;}"
				+ ".buttons {cursor:default;margin-left:5px;}"
				+ ".layout {border:2px solid #aaaaaa;-moz-border-radius:10px;-khtml-border-radius:30px;"
				+ "display:table;background-color:#efefef;padding:1em;padding-bottom:0.5em;}"
				+ ".innerlayout {border:1px solid #aaaaaa;-moz-border-radius:10px;-khtml-border-radius:30px;"
				+ "display:table;background-color:#787878;padding:1em;padding-bottom:0.5em;}"
				+ ".spacingtop {margin-top: 10px;}"
				+ ".spacingbuttons {margin-top: 8px;}"
				+ ".concepttopic {font-size:1.4em;font-family: Georgia, serif;}"
				+ ".conceptcreate {font-family: Georgia, serif;}"
				+ ".tags {font-weight:bold;color:#000066;}"
				+ "</style>");
		buffy.append(css
				+ "<form id='lodwizard' class='layout'><table border='0' cellpadding='5' cellspacing='1'>"
				+ "<tr><th id='conceptname' colspan='3' class='concepttopic'>"
				+ concept
				+ "</th></tr>"
				+ "<tr><td colspan='3' align='center' valign='middle' class='conceptcreate'>Erstelle Konzept</td></tr>"
				+ "<tr><td><div class='spacingtop' /></td></tr></tr>");

		SortedSet<String> sortedset = new TreeSet<String>(result.keySet());
		Iterator<String> it = sortedset.iterator();

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
				+ "<OPTION value='lns:Person'>Person</OPTION>"
				+ "<OPTION value='lns:Geographika'>Geographika</OPTION>"
				+ "<OPTION value='lns:Ereignis'>Ereignis</OPTION>"
				+ "</SELECT>"
				+ "</td>");

		buffy.append("<td><input type='button' id='submit"
				+ i
				+ "' onclick='buttonToggleCreate(this);' class='submit'>"
				+ "<input type='button' id='qmarks"
				+ i
				+ "' onclick='buttonToggleCreate(this);' class='qmarksc'></td></tr>");
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

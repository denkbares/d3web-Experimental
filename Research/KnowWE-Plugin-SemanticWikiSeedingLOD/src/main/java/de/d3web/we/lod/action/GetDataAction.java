package de.d3web.we.lod.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

		String web = map.getWeb();

		if (concept.isEmpty()) {
			context.getWriter().write("<p>Bitte Konzept eingeben.</p>");
		}
		else {
			// First letter = uppercase
			concept = concept.substring(0, 1).toUpperCase() + concept.substring(1);

			KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
					web, articleforMapping);
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
			if (dbpediaMapping.isEmpty()) {
				context.getWriter().write("<p>Konzept nicht vorhanden.</p>");
			}
			else {
				String encodePerson = "";
				try {
					hermesMapping = URLEncoder.encode(hermesMapping, "UTF-8");
					encodePerson = URLEncoder.encode(
							"Historische Persönlichkeit", "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				String namespace = UpperOntology.getInstance().getLocaleNS();
				hermesMapping = namespace + hermesMapping;

				// Concepttypes
				String ereignis = "ASK {<" + hermesMapping
						+ "> rdf:type lns:Ereignis}";
				String person = "ASK {<" + hermesMapping + "> rdf:type <"
						+ namespace + encodePerson + ">}";
				String geographika = "ASK {<" + hermesMapping
						+ "> rdf:type lns:Geographika}";

				LinkedOpenDataSet set = LinkedOpenDataSet.getInstance();
				HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
				LinkedOpenData var = new LinkedOpenData();
				if (dbpediaMapping != "") {
					if (SPARQLUtil.executeBooleanQuery(ereignis)) {
						System.out.println("e");
						var = set.getLOD(ConceptType.Ereignis);
						result = var.getData(dbpediaMapping);
					}
					else if (SPARQLUtil.executeBooleanQuery(person)) {
						System.out.println("p");
						var = set.getLOD(ConceptType.Person);
						result = var.getData(dbpediaMapping);
					}
					else if (SPARQLUtil.executeBooleanQuery(geographika)) {
						System.out.println("g");
						var = set.getLOD(ConceptType.Geographika);
						result = var.getData(dbpediaMapping);
					}
				}

				HashMap<String, List<String>> inverse = var.getInverseMap();
				StringBuffer buffy = new StringBuffer();
				StringBuffer css = new StringBuffer();
				css.append("<style type=\"text/css\"> .submit {cursor:pointer;cursor:hand;padding:0;margin:0;"
						+ "border:none;background: url(KnowWEExtension/images/b1g.png) no-repeat center;height:32px;width:45px;}"
						+ ".submitc {cursor:pointer;cursor:hand;padding:0;margin:0;"
						+ "border:none;background: url(KnowWEExtension/images/b2g.png) no-repeat center;height:32px;width:45px;}"
						+ ".cancel {cursor:pointer;cursor:hand;padding:0;margin:0;"
						+ "border:none;background: url(KnowWEExtension/images/b1x.png) no-repeat center;height:32px;width:45px;}"
						+ ".cancelc {cursor:pointer;cursor:hand;padding:0;margin:0;"
						+ "border:none;background: url(KnowWEExtension/images/b2x.png) no-repeat center;height:32px;width:45px;}"
						+ ".return {cursor:pointer;cursor:hand;padding:0;margin:0;"
						+ "border:none;background: url(KnowWEExtension/images/b1r.png) no-repeat center;height:32px;width:45px;}"
						+ ".returnc {cursor:pointer;cursor:hand;padding:0;margin:0;"
						+ "border:none;background: url(KnowWEExtension/images/b2r.png) no-repeat center;height:32px;width:45px;}"
						+ ".buttons {cursor:default}"
						+ ".div1 {border:1px solid #aaaaaa;-moz-border-radius:10px;-khtml-border-radius:30px;"
						+ "font-size:12px;font-family:Verdana;width:30%;height:40%;background-color:#efefef;padding-right:10px;padding-left:10px;}"
						+ "</style>");
				buffy.append(css
						+ "<form id='lodwizard' class='div1'><table width='35%' border='1' cellpadding='5' cellspacing='1'>"
						+ "<tr><th id='conceptname' colspan='5'>"
						+ concept
						+ "</th></tr>");

				int i = 0;
				for (String s : result.keySet()) {
					if (!result.get(s).isEmpty()) {
						List<String> mapped = inverse.get(s);
						for (String resultS : result.get(s)) {

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

							buffy.append("<tr><td valign='middle' align='middle' width='20%'>"
									+ "<p id='hermestag" + i + "' title="
									+ title
									+ ">"
									+ s
									+ "</p>"
									+ "<td id='dbpediavalue" + i
									+ "'valign='middle' align='middle' width='50%'>"
									+ resultS + "</td>");
							buffy.append("<td valign='middle' align='middle'><input type='button' name='submit"
									+ i
									+ "' onclick='buttonToggle(this);' class='submit'></td>"
									+ "<td valign='middle' align='middle'><input type='button' name='cancel"
									+ i
									+ "' onclick='buttonToggle(this);' class='cancel'></td>"
									+ "<td valign='middle' align='middle'><input type='button' name='return"
									+ i
									+ "' onclick='buttonToggle(this);' class='return'></td></tr>");
							i++;
						}
					}
				}
				buffy.append(" <tr><td colspan='5' valign='middle' align='right'><br/><img src='KnowWEExtension/images/submit.png'"
						+ "onmouseover='changeOnMouseOver(this);' onmouseout='changeOnMouseOut(this);'"
						+ "onclick='submitData("
						+ i
						+ ");' class='buttons'>"
						+ "<img src='KnowWEExtension/images/cancel.png' onmouseover='changeOnMouseOver(this);' "
						+ "onmouseout='changeOnMouseOut(this);' onclick='cancelWizard();' class='buttons'></td></tr></table></form>");

				context.getWriter().write(buffy.toString());
			}
		}
	}
}

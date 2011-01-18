package de.d3web.we.lod.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.lod.ConceptType;
import de.d3web.we.lod.HermesData;
import de.d3web.we.lod.LinkedOpenData;
import de.d3web.we.lod.LinkedOpenDataSet;
import de.d3web.we.lod.markup.DBpediaContentType;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class GetDataAction extends AbstractAction {

	@Override
	public void execute(ActionContext context) throws IOException {

		KnowWEParameterMap map = context.getKnowWEParameterMap();
		String concept = map.get("concept");
		String checkDebug = map.get("debug");
		String wikipedia = map.get("wikipedia");
		String web = map.getWeb();

		if (concept.isEmpty()) {
			context.getWriter().write(
					"<br/><div style='margin-left:10px;'><p><b>Bitte Konzept eingeben.</b></p></div>");
		}
		else {
			// First letter = uppercase
			concept = concept.substring(0, 1).toUpperCase() + concept.substring(1);

			String dbpediaConcept = HermesData.getDBpediaMapping(concept);

			String wiki = "";

			if (wikipedia != null
					&& wikipedia.matches("http://en.wikipedia.org/.*")) {

				wiki = LinkedOpenData.getResourceforWikipedia(wikipedia);

			}

			// Ask to create article?
			if (dbpediaConcept.isEmpty()) {

				if (HermesData.isHermesConcept(concept)
						|| (LinkedOpenData.getDBpediaRedirect(concept).isEmpty() && wiki.isEmpty())) {
					context.getWriter().write(
							"<br/><div style='margin-left:10px;'><p><b>Konzept nicht gefunden.</b></p></div>");
				}
				else {
					String ask = "<br/><div id='creationWizard'><p><img src='KnowWEExtension/images/newdoc.png' align='top'> Artikel <b>"
							+ concept
							+ "</b> nicht vorhanden - neue Artikelseite erstellen? ";
					ask += "<input type='button' value='Ja' onclick='createConcept()'><input type='button' value='Nein' onclick='cancelWizard()'></p></div>";

					context.getWriter().write(ask);
				}
			}
			else {

				if (!wiki.isEmpty()) {

					dbpediaConcept = wiki;

					// change links provided in mappings. -> afterwards query.
					String mappingTopic = HermesData.getMappingTopic();

					KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
							web, mappingTopic);

					List<Section<DBpediaContentType>> found = new Vector<Section<DBpediaContentType>>();
					article.getSection().findSuccessorsOfType(DBpediaContentType.class,
							found);

					Map<String, String> nodesMap = new HashMap<String, String>();

					for (Section<DBpediaContentType> t : found) {
						String complete = t.getChildren().get(0).getOriginalText();
						if (complete.matches(concept + " =>.*")) {
							// Update node with wikipedia dbpedia url.
							nodesMap.put(t.getChildren().get(0).getID(), concept + " => " + wiki
									+ " " + wikipedia);
							context.getWriter().write(
									"<br/><p><b>Mapping erfolgreich aktualisiert.</b></p>");
						}
					}
					KnowWEEnvironment.getInstance().getArticleManager(
							web).replaceKDOMNodesSaveAndBuild(map, mappingTopic, nodesMap);
				}

				String hermesConcept = concept;
				String encodePerson = "";

				try {
					hermesConcept = URLEncoder.encode(hermesConcept, "UTF-8");
					encodePerson = URLEncoder.encode(
							"Historische Persönlichkeit", "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				String namespace = UpperOntology.getInstance().getLocaleNS();
				hermesConcept = namespace + hermesConcept;

				// Concepttypes
				String ereignis = "ASK {<" + hermesConcept
						+ "> rdf:type lns:Ereignis}";
				String person = "ASK {<" + hermesConcept + "> rdf:type <"
						+ namespace + encodePerson + ">}";
				String geographika = "ASK {<" + hermesConcept
						+ "> rdf:type lns:Geographika}";

				LinkedOpenDataSet set = LinkedOpenDataSet.getInstance();
				HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
				HashMap<String, HashSet<String>> dbResult = new HashMap<String, HashSet<String>>();
				LinkedOpenData var = new LinkedOpenData();

				if (SPARQLUtil.executeBooleanQuery(ereignis)) {
					// System.out.println("e");
					var = set.getLOD(ConceptType.Ereignis);
					dbResult = var.getLODdata(dbpediaConcept);
					result = var.getHermesData(dbResult);
				}
				else if (SPARQLUtil.executeBooleanQuery(person)) {
					// System.out.println("p");
					var = set.getLOD(ConceptType.Person);
					dbResult = var.getLODdata(dbpediaConcept);
					result = var.getHermesData(dbResult);
				}
				else if (SPARQLUtil.executeBooleanQuery(geographika)) {
					// System.out.println("g");
					var = set.getLOD(ConceptType.Geographika);
					dbResult = var.getLODdata(dbpediaConcept);
					result = var.getHermesData(dbResult);
				}

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
						+ ".tags {font-weight:bold;color:#000066;}"
						+ "</style>");
				buffy.append(css
						+ "<br/><form id='lodwizard' class='layout'><table border='0' cellpadding='5' cellspacing='1'>"
						+ "<tr><th id='conceptname' colspan='3' class='concepttopic'>"
						+ concept
						+ "</th></tr><tr><td><div class='spacingtop' /></td></tr></tr>");

				SortedSet<String> sortedset = new TreeSet<String>(result.keySet());
				Iterator<String> it = sortedset.iterator();

				int i = 0;
				while (it.hasNext()) {
					String s = it.next();
					List<String> mapped = inverse.get(s);
					for (String resultS : result.get(s)) {
						// Tests if Triple is already in the hermes RDF-Store :
						// do nothing ? add.
						// if ( !HermesData.storeContainsPre(hermesConcept,
						// s, resultS)) {
						// TODO: In Store, Ignore, NoParse
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

							// If object -> no input box.
							if (resultS.matches("ist vom Typ .*")) {
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
							}
							// If concept -> no input box + build link for href
							// and <a> carries id tag for JS reading.
							else if (resultS.matches("!\\$ConceptLink:: .*")) {
								String conceptname = resultS.substring(16);
								String link = HermesData.linkString(conceptname, "dbpediavalue"
												+ i);
								buffy.append("<tr><td valign='middle' align='middle'>"
										+ "<p id='hermestag" + i + "' title="
										+ title
										+ " class='tags'>"
										+ s
										+ "</p>"
										+ "<td valign='middle' align='middle'>"
										+ link
										+ "</td>");
							}
							else {
								buffy.append("<tr><td valign='middle' align='middle'>"
										+ "<p id='hermestag"
										+ i
										+ "' title="
										+ title
										+ " class='tags'>"
										+ s
										+ "</p>"
										+ "<td valign='middle' align='middle'><input id='dbpediavalue"
										+ i
										+ "' type='text' size='40' value='"
										+ resultS.replaceAll("'", "&rsquo;") + "'>"
										+ "</td>");
							}
							buffy.append("<td><input type='button' name='submit"
									+ i
									+ "' onclick='buttonToggle(this);' class='submit'>"
									+ "<input type='button' name='ignore"
									+ i
									+ "' onclick='buttonToggle(this);' class='ignore'>"
									+ "<input type='button' name='return"
									+ i
									+ "' onclick='buttonToggle(this);' class='return'>"
									+ "<input type='button' name='qmarks"
									+ i
									+ "' onclick='buttonToggle(this);' class='qmarksc'></td></tr>");
							i++;
						}
					}
				}
				buffy.append("<tr><td colspan='3'><div class='spacingbuttons'/></td></tr><tr><td colspan='3' valign='middle' align='right'><img src='KnowWEExtension/images/submit.png'"
						+ "onmouseover='changeOnMouseOver(this);' onmouseout='changeOnMouseOut(this);'"
						+ "onclick='submitData("
						+ i
						+ ");' class='buttons'>"
						+ "<img src='KnowWEExtension/images/cancel.png' onmouseover='changeOnMouseOver(this);' "
						+ "onmouseout='changeOnMouseOut(this);' onclick='cancelWizard();' class='buttons'></td></tr></table></form><br />");

				StringBuffer debug = new StringBuffer();

				if (checkDebug.equals("true")) {
					// Debug View.
					debug.append("<div class='collapsebox'><h4 id='section-Sandbox2-Debug.' class='collapsetitle'>"
							+ "<div class='collapseOpen' title='Zuklappen'>-</div>Debug.</h4><table class='wikitable' border='1'>"
							+ "<div class='collapsebody' style='overflow: hidden; height: 0px;'>"
							+ "<tbody><tr><th class='sort'>DBpediaTag</th><th class='sort'>Value</th></tr>"
							+ "</div></div>");
					for (String s : dbResult.keySet()) {
						if (!s.isEmpty()) {
							debug.append("<tr><td>" + s + "</td><td>" + dbResult.get(s)
									+ "</td></tr>");
						}
					}
					debug.append("</tbody></table></div></div>");
				}
				context.getWriter().write(buffy.toString() + debug.toString());
			}
		}
	}
}

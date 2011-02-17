package de.d3web.we.lod.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import de.d3web.we.lod.markup.MappingContentType;
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
		else if (!KnowWEEnvironment.getInstance().getWikiConnector()
				.doesPageExist(HermesData.getMappingTopic())) {
			context.getWriter().write(
					"<br/><div style='margin-left:10px;'><p><b>Seite für Mappings nicht vorhanden. "
							+
							"Erstellen Sie diese bitte mit dem Taghandler [{KnowWEPlugin concepts}].</b></p></div>");
		}
		else if (!LinkedOpenData.sparqlAvailable()) {
			context.getWriter().write(
					"<br/><div style='margin-left:10px;'><p><b>HttpException: "
							+ LinkedOpenData.sparqlEndpoint
							+ " ist im Moment leider nicht verfügbar.</b></p></div>");
		}
		else {
			// First letter = uppercase
			concept = concept.substring(0, 1).toUpperCase() + concept.substring(1);

			String dbpediaConcept = HermesData.getDBpediaMapping(concept);

			String wiki = "";

			if (!wikipedia.isEmpty()
					&& wikipedia.matches("http://en.wikipedia.org/.*")) {

				wiki = LinkedOpenData.getResourceforWikipedia(wikipedia);

			}

			// Ask to create article?
			if (dbpediaConcept.isEmpty()) {

				// If (no mapping || no dbpedia concept found for) && no
				// wikilink provided.
				if ((HermesData.isHermesConcept(concept)
						|| LinkedOpenData.getDBpediaRedirect(concept).isEmpty()) && wiki.isEmpty()) {
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

					List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
					article.getSection().findSuccessorsOfType(MappingContentType.class,
							found);

					Map<String, String> nodesMap = new HashMap<String, String>();

					for (Section<MappingContentType> t : found) {
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
				encodePerson = namespace + encodePerson;

				// Concepttypes
				String ereignis = "ASK {<" + hermesConcept
						+ "> rdf:type lns:Ereignis}";
				String person = "ASK {<" + hermesConcept + "> rdf:type <"
						+ encodePerson + ">}";
				String geographika = "ASK {<" + hermesConcept
						+ "> rdf:type lns:Geographika}";

				LinkedOpenDataSet set = LinkedOpenDataSet.getInstance();
				HashMap<String, HashSet<String>> result = new LinkedHashMap<String, HashSet<String>>();
				HashMap<String, HashSet<String>> dbResult = new LinkedHashMap<String, HashSet<String>>();
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

				buffy.append("<br/><form id='lodwizard' class='layout'><table border='0' cellpadding='5' cellspacing='1'>"
								+ "<tr><th id='conceptname' colspan='3' class='concepttopic'>"
								+ concept
								+ "</th></tr><tr><td><div class='spacingtop' /></td></tr></tr>");

				// Sort A-Z
				// SortedSet<String> sortedset = new
				// TreeSet<String>(result.keySet());
				Iterator<String> it = result.keySet().iterator();

				int i = 0;
				if (result.size() > 0) {
					while (it.hasNext()) {
						String s = it.next();
						List<String> mapped = inverse.get(s);
						for (String resultS : result.get(s)) {
							String checkHermesTag = s;
							String checkValue = resultS;

							// objects are saved with predicate specified in
							// HermesData.
							if (resultS.matches("ist vom Typ .*")) {
								checkHermesTag = HermesData.getObjectType();
								checkValue = s;
							}

							if (resultS.matches("!\\$ConceptLink:: .*")) {
								checkValue = resultS.substring(16);
							}

							// If namespace is cut, isIgnored and isNoParse have
							// to be tested with cut NS for predicate.
							String checkHermesTagPI = checkHermesTag;
							if (HermesData.isCutPredicateNS()) {
								checkHermesTagPI = checkHermesTag.substring(checkHermesTag.indexOf(":") + 1);
							}

							if (!resultS.isEmpty()
									&& !HermesData.isIgnored(concept, checkHermesTagPI, checkValue)
									&& !HermesData.isNoParse(concept, checkHermesTagPI, checkValue)
									&& !HermesData.storeContains(concept, checkHermesTag,
											checkValue)) {

								// Create title for html, if values are created
								// from
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
											+ "<p id='hermestag"
											+ i
											+ "' title="
											+ title
											+ " class='tags'>"
											+ s
											+ "</p>"
											+ "<td valign='middle' align='middle'><p id='dbpediavalue"
											+ i + "'>"
											+ resultS
											+ "</p></td>");
								}
								// If concept -> no input box + build link for
								// href
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
											+ resultS.replaceAll("’", "&rsquo;") + "'>"
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
				else {
					context.getWriter().write("<br/><p><b>Keine Daten zu Konzept gefunden.</b></p>");
				}
			}
		}
	}
}

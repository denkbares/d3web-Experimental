package de.knowwe.lod.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.lod.ConceptType;
import de.knowwe.lod.HermesData;
import de.knowwe.lod.LinkedOpenData;
import de.knowwe.lod.LinkedOpenDataSet;
import de.knowwe.lod.markup.MappingContentType;
import de.knowwe.rdf2go.Rdf2GoCore;

public class GetDataAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String concept = context.getParameter("concept");
		String checkDebug = context.getParameter("debug");
		String wikipedia = context.getParameter("wikipedia");
		String web = context.getWeb();

		// #################### Only for logging purposes
		String path = KnowWEEnvironment.getInstance().getWikiConnector().getSavePath();
		File log = new File(path + "/temp");
		log.mkdir();
		try {
			// Create file
			FileWriter fstream = new FileWriter(path + "/temp/search.log", true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("search: " + concept + " -- " + wikipedia
					+ System.getProperty("line.separator"));
			// Close the output stream
			out.close();
		}
		catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		// ####################

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
			// Trim input
			concept = concept.trim();

			String dbpediaConcept = HermesData.getDBpediaMapping(concept);

			String wiki = "";

			if (!wikipedia.isEmpty()) {
				if (wikipedia.matches("(?i)http://en.wikipedia.org/.*")) {
					wiki = LinkedOpenData.getResourceforWikipedia(wikipedia);
				}
				else {
					context.getWriter().write(
							"<br/><div style='margin-left:10px;'><p><b>Keine gültige Wikipedia@en URL.</b></p></div>");
				}
			}
			// Ask to create article?
			if ((dbpediaConcept.isEmpty() && wiki.isEmpty()) || !HermesData.storeContains(concept)) {

				// If (no mapping || no dbpedia concept found for) && no
				// wikilink provided.
				if (((HermesData.isHermesConcept(concept) && dbpediaConcept.isEmpty())
						|| LinkedOpenData.getDBpediaRedirect(concept).isEmpty()) && wiki.isEmpty()) {
					context.getWriter().write(
							"<br/><div style='margin-left:10px;'><p><b>Kein externes Konzept als Referenz gefunden.</b></p></div>");
				}
				else if (KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(
						concept)) {
					String ask = "<br/><div id='creationWizard'><p><img src='KnowWEExtension/images/newdoc.png' align='top'> Konzept <b>"
							+ concept
							+ "</b> auf vorhandener Artikelseite erstellen? ";
					ask += "<input type='button' value='Ja' onclick='createConcept()'><input type='button' value='Nein' onclick='cancelWizard()'></p></div>";

					context.getWriter().write(ask);
				}
				else {
					String ask = "<br/><div id='creationWizard'><p><img src='KnowWEExtension/images/newdoc.png' align='top'> Konzept <b>"
							+ concept
							+ "</b> nicht vorhanden - neues Konzept und dazugehörige Artikelseite erstellen? ";
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
					Sections.findSuccessorsOfType(article.getSection(),
							MappingContentType.class, found);

					Map<String, String> nodesMap = new HashMap<String, String>();

					for (Section<MappingContentType> t : found) {
						String complete = t.getChildren().get(0).getText();
						if (complete.matches(concept + " =>.*")) {
							// Update node with wikipedia dbpedia url.
							nodesMap.put(t.getChildren().get(0).getID(), concept + " => " + wiki
									+ " " + wikipedia);
							context.getWriter().write(
									"<br/><p><b>Mapping erfolgreich aktualisiert.</b></p>");
						}
					}
					Sections.replaceSections(context, nodesMap);
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

				String namespace = Rdf2GoCore.localns;
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

				boolean validType = false;

				if (Rdf2GoCore.getInstance().sparqlAsk(ereignis)) {
					// System.out.println("e");
					var = set.getLOD(ConceptType.Ereignis);
					dbResult = var.getLODdata(dbpediaConcept);
					result = var.getHermesData(dbResult);
					validType = true;
				}
				else if (Rdf2GoCore.getInstance().sparqlAsk(person)) {
					// System.out.println("p");
					var = set.getLOD(ConceptType.Person);
					dbResult = var.getLODdata(dbpediaConcept);
					result = var.getHermesData(dbResult);
					validType = true;
				}
				else if (Rdf2GoCore.getInstance().sparqlAsk(geographika)) {
					// System.out.println("g");
					var = set.getLOD(ConceptType.Geographika);
					dbResult = var.getLODdata(dbpediaConcept);
					result = var.getHermesData(dbResult);
					validType = true;
				}

				if (validType) {

					HashMap<String, List<String>> inverse = var.getInverseMap();
					StringBuffer buffy = new StringBuffer();

					String encodeTopic = "";

					try {
						encodeTopic = URLEncoder.encode(HermesData.getTopicForConcept(concept),
								"UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					buffy.append("<br/><form id='lodwizard' class='layout'><table border='0' cellpadding='5' cellspacing='1'>"
							+ "<tr><th id='conceptname' colspan='3' class='concepttopic'><a href='/KnowWE/Wiki.jsp?page="
							+ encodeTopic + "'>"
							+ concept
							+ "</a></th></tr><tr><td><div class='spacingtop' /></td></tr>");

					// Sort A-Z
					// SortedSet<String> sortedset = new
					// TreeSet<String>(result.keySet());
					Iterator<String> it = result.keySet().iterator();

					StringBuffer attribs = new StringBuffer();
					StringBuffer newdata = new StringBuffer();

					// SPARQL intern already saved data from properties file.
					int num = 0;
					for (String attribute : var.getHermesAttributes()) {
						List<String> values = HermesData.queryStore(concept, attribute);
						for (String value : values) {
							if (!value.isEmpty()) {
								attribs.append("<tr valign='middle' align='middle' id='instore"
										+ num
										+ "' style='display:none'><td class='savetags'>"
										+ attribute
										+ "</td><td class='savepadding'>" + value
										+ "</td></tr>");
								num++;
							}
						}
					}
					attribs.append("<tr><td><div class='spacingtop' /></td></tr>");

					StringBuffer saved = new StringBuffer();
					saved.append("<tr><td colspan='3'><p id='savetoggle' class='mouselink' onclick='switchMenu(\""
							+ num + "\");'><b>+ Bereits gespeichert</b></p></tr>");
					saved.append(attribs.toString());

					int latCount = 1;
					int longCount = 1;

					int i = 0;

					boolean noValues = true;
					String baseURL = KnowWEEnvironment.getInstance().getWikiConnector().getBaseUrl();

					if (result.size() > 0) {
						while (it.hasNext()) {
							String s = it.next();
							List<String> mapped = inverse.get(s);
							for (String resultS : result.get(s)) {
								String checkHermesTag = s;
								String checkValue = resultS;
								if (!resultS.isEmpty() && !resultS.matches("\\s+")) {
									noValues = false;
								}

								// objects are saved with predicate specified in
								// HermesData.
								if (resultS.matches("ist vom Typ .*")) {
									checkHermesTag = HermesData.get();
									checkValue = s;
								}

								if (resultS.matches("!\\$ConceptLink:: .*")) {
									checkValue = resultS.substring(16);
								}

								// If namespace is cut, isIgnored and isNoParse
								// have
								// to be tested with cut NS for predicate.
								String checkHermesTagPI = checkHermesTag;
								if (HermesData.isCutPredicateNS()) {
									checkHermesTagPI = checkHermesTag.substring(checkHermesTag.indexOf(":") + 1);
								}
								// System.out.println(resultS);
								// System.out.println(concept + "," +
								// checkHermesTagPI + "," + checkValue);

								if (!resultS.isEmpty()
										&& !HermesData.isIgnored(concept, checkHermesTagPI,
												checkValue)
										&& !HermesData.isNoParse(concept, checkHermesTagPI,
												checkValue)
										&& !HermesData.storeContains(concept, checkHermesTag,
												checkValue)) {

									// Create title for html, if values are
									// created
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
										newdata.append("<tr><td valign='middle' align='middle'>"
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
									// If concept -> no input box + build link
									// for
									// href
									// and <a> carries id tag for JS reading.
									else if (resultS.matches("!\\$ConceptLink:: .*")) {
										String conceptname = resultS.substring(16);
										String link = HermesData.linkString(conceptname,
												"dbpediavalue"
														+ i);
										newdata.append("<tr><td valign='middle' align='middle'>"
												+ "<p id='hermestag" + i + "' title="
												+ title
												+ " class='tags'>"
												+ s
												+ "</p>"
												+ "<td valign='middle' align='middle'>"
												+ link
												+ "</td>");
									}
									else if (s.contains("Longitude")) {

										newdata.append("<tr><td valign='middle' align='middle'>"
												+ longCount
												+ " <a id='hermestag"
												+ i
												+ "' title="
												+ title
												+ " class='tags' href='"
												+ baseURL
												+ "Map.jsp' onclick='openWindow(createCoordURL(\""
												+ longCount
												+ "\",\""
												+ baseURL
												+ "\"),\""
												+ longCount
												+ "\");return false'>"
												+ s
												+ "</a>"
												+ "<td valign='middle' align='middle'><span id='lon"
												+ longCount + "'><input id='dbpediavalue"
												+ i
												+ "' type='text' size='40' value='"
												+ resultS.replaceAll("’", "&rsquo;") + "'></span>"
												+ "</td>");
										longCount++;
									}
									else if (s.contains("Latitude")) {
										newdata.append("<tr><td valign='middle' align='middle'>"
												+ latCount
												+ " <a id='hermestag"
												+ i
												+ "' title="
												+ title
												+ " class='tags' href='"
												+ baseURL
												+ "Map.jsp' onclick='openWindow(createCoordURL(\""
												+ latCount
												+ "\",\""
												+ baseURL
												+ "\"),\""
												+ latCount
												+ "\");return false'>"
												+ s
												+ "</a>"
												+ "<td valign='middle' align='middle'><span id='lat"
												+ latCount + "'><input id='dbpediavalue"
												+ i
												+ "' type='text' size='40' value='"
												+ resultS.replaceAll("’", "&rsquo;") + "'></span>"
												+ "</td>");
										latCount++;
									}
									else {
										newdata.append("<tr><td valign='middle' align='middle'>"
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
									newdata.append("<td><input type='button' name='submit"
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
						newdata.append("<tr><td colspan='3'><div class='spacingbuttons'/></td></tr><tr><td colspan='3' valign='middle' align='right'><img src='KnowWEExtension/images/submit.png'"
								+ "onmouseover='changeOnMouseOver(this);' onmouseout='changeOnMouseOut(this);'"
								+ "onclick='submitData("
								+ i
								+ ");' class='buttons'>"
								+ "<img src='KnowWEExtension/images/cancel.png' onmouseover='changeOnMouseOver(this);' "
								+ "onmouseout='changeOnMouseOut(this);' onclick='cancelWizard();' class='buttons'></td></tr></table></form><br />");

						saved.append("</div>");

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
						if (noValues) {
							context.getWriter().write(
									"<br/><div style='margin-left:10px;'><p><b>Keine Daten zu Konzept gefunden.</b></p></div>");
						}
						else {
							context.getWriter().write(
									buffy.toString() + saved.toString() + newdata.toString()
											+ debug.toString());
						}
					}
					else {
						context.getWriter().write(
								"<br/><div style='margin-left:10px;'><p><b>Keine Daten zu Konzept gefunden.</b></p></div>");
					}
				}
				else {
					List<String> type = HermesData.queryStore(concept, HermesData.get());
					context.getWriter().write(
							"<br/><div style='margin-left:10px;'><p><b>Objekttyp nicht zur Abfrage vorhanden:</b></p>"
									+ type + "</div>");
				}
			}
		}
	}
}

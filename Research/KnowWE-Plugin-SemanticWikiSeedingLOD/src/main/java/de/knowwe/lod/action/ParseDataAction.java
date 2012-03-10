package de.knowwe.lod.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.lod.HermesData;
import de.knowwe.lod.LinkedOpenData;
import de.knowwe.lod.markup.IgnoreContentType;
import de.knowwe.lod.markup.MappingContentType;
import de.knowwe.lod.markup.IgnoreContentType.IgnoreChild;
import de.knowwe.lod.markup.IgnoreContentType.IgnoreConcept;

public class ParseDataAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String concept = context.getParameter("concept");
		String conceptTopic = HermesData.getTopicForConcept(concept);
		String user = context.getUserName();
		String web = context.getWeb();
		String create = context.getParameter("create");
		String wiki = context.getParameter("wiki");

		String types = context.getParameter("type");
		String values = context.getParameter("dbpedia");
		String hermestags = context.getParameter("hermes");
		String filter = "\".*?\"";
		Pattern pattern = Pattern.compile(filter);

		Matcher matchTypes = pattern.matcher(types);
		Matcher matchValues = pattern.matcher(values);
		Matcher matchHermestags = pattern.matcher(hermestags);

		List<String> type = new ArrayList<String>();
		List<String> value = new ArrayList<String>();
		List<String> hermes = new ArrayList<String>();

		int countData = 0;

		while (matchTypes.find()) {
			type.add(matchTypes.group().substring(1, matchTypes.group().lastIndexOf("\"")));
			countData++;
		}
		while (matchValues.find()) {
			value.add(matchValues.group().substring(1, matchValues.group().lastIndexOf("\"")));
		}
		while (matchHermestags.find()) {
			hermes.add(matchHermestags.group().substring(1,
					matchHermestags.group().lastIndexOf("\"")));
		}

		int i = 0;
		for (String s : type) {

			// ist vom Typ ... -> predicate = rdf:type, value = hermestype.
			if (value.get(i).matches("ist vom Typ .*")) {
				value.set(i, hermes.get(i));
				hermes.set(i, HermesData.get());
			}

			// Cuts the namespace for the predicate.
			if (HermesData.isCutPredicateNS()) {
				String temp = hermes.get(i);
				hermes.set(i, temp.substring(temp.indexOf(":") + 1));
			}

			String parse = "[" + concept + " " + hermes.get(i) + ":: " + value.get(i) + "]";
			String lightParse = "[" + hermes.get(i) + ":: " + value.get(i) + "]";

			// ################## Only for logging purposes
			String path = Environment.getInstance().getWikiConnector().getSavePath();
			File log = new File(path + "/temp");
			log.mkdir();
			try {
				// Create file
				FileWriter fstream = new FileWriter(path + "/temp/data.log", true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(s + ": " + parse
						+ System.getProperty("line.separator"));
				// Close the output stream
				out.close();
			}
			catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			// ####################

			if (s.equals("qmarks")) {
				// Do nothing.
				countData--;
			}

			if (s.equals("submit")) {

				// Parse text for the ontology.

				lightParse = lightParse + System.getProperty("line.separator");

				if (!Environment.getInstance().getWikiConnector().doesPageExist(
						conceptTopic)
						&& !Environment.getInstance().getWikiConnector().doesPageExist(
								concept)) {

					Environment.getInstance().getWikiConnector().createWikiPage(
							concept, lightParse, user);

					Article article = Article.createArticle(lightParse,
							concept, Environment.getInstance().getRootType(),
							web, true);

					Environment.getInstance().getArticleManager(web)
							.registerArticle(article);

					conceptTopic = concept;

				}
				else {
					if (Environment.getInstance().getWikiConnector().doesPageExist(
								concept)) {
						conceptTopic = concept;
					}
					Environment.getInstance().getWikiConnector().appendContentToPage(
							conceptTopic,
							lightParse);

					Environment.getInstance().getArticleManager(web).addArticleToUpdate(
							conceptTopic);
				}

			}

			if (s.equals("return")) {

				// Save on NoParse article.

				String noParseTopic = HermesData.getNoParseTopic();

				parse = "~" + parse;

				if (!Environment.getInstance().getWikiConnector().doesPageExist(
						noParseTopic)) {

					String temp = "%%Mapping " + System.getProperty("line.separator")
							+ parse
							+ System.getProperty("line.separator") + "%";

					Environment.getInstance().getWikiConnector().createWikiPage(
							noParseTopic, temp, user);

					Article article = Article.createArticle(temp,
							noParseTopic, Environment.getInstance().getRootType(),
							web, true);

					Environment.getInstance().getArticleManager(web)
							.registerArticle(article);

				}
				else {

					// append triple to noParseTopic.
					Article article = Environment.getInstance().getArticle(
							web, noParseTopic);

					List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
					 Sections.findSuccessorsOfType(article.getSection(),
							MappingContentType.class, found);

					Section<MappingContentType> lastNode = found.get(found.size() - 1);

					String add = lastNode.getChildren().get(0).getText()
								+ System.getProperty("line.separator") + parse;

					Map<String, String> nodesMap = new HashMap<String, String>();
					nodesMap.put(lastNode.getChildren().get(0).getID(), add);

					Sections.replaceSections(context, nodesMap);

				}

			}

			if (s.equals("ignore")) {

				// Save ignores on wiki-page:IgnoredAttributes --> Markup
				// %%IgnoreAttributes .... %

				String ignoredTopic = HermesData.getIgnoredTopic();

				if (!Environment.getInstance().getWikiConnector().doesPageExist(
						ignoredTopic)) {

					// #concept
					// - hermestag == value
					String temp = "%%IgnoreAttributes " + System.getProperty("line.separator")
							+ "#" + concept + System.getProperty("line.separator") + "- "
							+ hermes.get(i)
							+ " == " + value.get(i)
							+ System.getProperty("line.separator") + "%";

					Environment.getInstance().getWikiConnector().createWikiPage(
							ignoredTopic, temp, user);

					Article article = Article.createArticle(temp,
							ignoredTopic, Environment.getInstance().getRootType(),
							web, true);

					Environment.getInstance().getArticleManager(web)
							.registerArticle(article);

				}
				// hasChild concept : add ? else new rootNode.
				else {

					ArticleManager mgr = Environment.getInstance().getArticleManager(
							web);

					List<Section<IgnoreContentType>> found = new Vector<Section<IgnoreContentType>>();
				    Sections.findSuccessorsOfType(
							mgr.getArticle(ignoredTopic).getSection(), IgnoreContentType.class, found);

					Map<String, String> nodesMap = new HashMap<String, String>();

					for (Section<IgnoreContentType> t : found) {

						Section<IgnoreConcept> temp = Sections.findChildOfType(t,
								IgnoreConcept.class);
						String sectionConcept = temp.getText().substring(1,
								temp.getText().length() - 1);
						boolean conceptFound = false;

						// if concept is in list - test if tag + value also.
						if (sectionConcept.equals(concept)) {

							conceptFound = true;

							List<Section<IgnoreChild>> listChilds = Sections.findChildrenOfType(
									t, IgnoreChild.class);
							boolean isIn = false;
							for (Section<IgnoreChild> child : listChilds) {
								String node = child.getText();
								if (Character.isWhitespace(node.charAt(node.length() - 1))) {
									node = node.substring(0, node.length() - 1);
								}
								if (node.equals("- " +
										hermes.get(i) + " == " + value.get(i))) {
									// tag + value already in ignorelist.
									isIn = true;
								}
								// append at the end of the section.
								else if (listChilds.indexOf(child) == listChilds.size() - 1
										&& !isIn) {
									String newIgnore = child.getText()
											+ System.getProperty("line.separator") + "- " +
											hermes.get(i) + " == " + value.get(i);
									// add to ignorelist.
									nodesMap.put(child.getID(), newIgnore);
								}
							}
						}
						else if (found.indexOf(t) == found.size() - 1 && !conceptFound) {

							// Add concept + tag & value.
							String newIgnore = t.getText()
									+ System.getProperty("line.separator") + "#" + concept
									+ System.getProperty("line.separator") + "- " + hermes.get(i)
									+ " == " + value.get(i);

							nodesMap.put(t.getID(), newIgnore);
						}

					}
					Sections.replaceSections(context, nodesMap);
				}
			}
			i++;
			// Refresh
			Environment.getInstance().getArticleManager(web).updateQueuedArticles();
		}

		if (countData != 0) {
			if (create != null && create.equals("true")) {
				// Append to MappingPage.
				// Add node for created Article.
				String mappingTopic = HermesData.getMappingTopic();

				Article article = Environment.getInstance().getArticle(
						web, mappingTopic);

				List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
				Sections.findSuccessorsOfType(article.getSection(),
						MappingContentType.class, found);

				Section<MappingContentType> lastNode = found.get(found.size() - 1);

				String add = "";

				if (!wiki.equals("")) {
					add = lastNode.getChildren().get(0).getText()
							+ System.getProperty("line.separator") + concept + " => "
							+ LinkedOpenData.getResourceforWikipedia(wiki) + " " + wiki;
				}
				else {
					add = lastNode.getChildren().get(0).getText()
							+ System.getProperty("line.separator") + concept + " => "
							+ LinkedOpenData.getDBpediaRedirect(concept);
				}

				Map<String, String> nodesMap = new HashMap<String, String>();
				nodesMap.put(lastNode.getChildren().get(0).getID(), add);

				Sections.replaceSections(context, nodesMap);

				context.getWriter().write(
						"<br/><div style='margin-left:10px;'><img src='KnowWEExtension/images/success.png' align='top'>"
								+ "<b> Konzept erfolgreich erstellt - Sie können zu diesem Konzept nun weitere Attribute abfragen!</b></div>");
			}
			else {
				context.getWriter().write(
						"<br/><div style='margin-left:10px;'><img src='KnowWEExtension/images/success.png' align='top'><b> Daten erfolgreich eingetragen!</b></div>");
			}
		}
		else {
			context.getWriter().write(
					"<br/><div style='margin-left:10px;'><b>Keine Daten zur Übergabe markiert.</b></div>");
		}
	}
}

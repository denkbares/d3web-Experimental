package de.d3web.we.lod.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.lod.HermesData;
import de.d3web.we.lod.LinkedOpenData;
import de.d3web.we.lod.markup.DBpediaContentType;
import de.d3web.we.lod.markup.IgnoreContentType;

public class ParseDataAction extends AbstractAction {

	@Override
	public void execute(ActionContext context) throws IOException {

		KnowWEParameterMap map = context.getKnowWEParameterMap();

		String concept = map.get("concept");
		String conceptTopic = HermesData.getTopicForConcept(concept);
		String user = map.getUser();
		String web = map.getWeb();
		String create = map.get("create");
		String wiki = map.get("wiki");

		String types = map.get("type");
		String values = map.get("dbpedia");
		String hermestags = map.get("hermes");
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
				hermes.set(i, "rdf:type");
			}

			String parse = "[" + concept + " " + hermes.get(i) + ":: " + value.get(i) + "]";

			if (s.equals("qmarks")) {
				// Do nothing.
				countData--;
			}

			if (s.equals("submit")) {

				// Parse text for the ontology.

				if (!KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(
						conceptTopic)) {

					KnowWEEnvironment.getInstance().getWikiConnector().createWikiPage(
							concept, parse, user);

					KnowWEArticle article = KnowWEArticle.createArticle(parse,
							concept, KnowWEEnvironment.getInstance().getRootType(),
							web, true);

					KnowWEEnvironment.getInstance().getArticleManager(web)
							.registerArticle(article);

				}
				else {

					KnowWEEnvironment.getInstance().getWikiConnector().appendContentToPage(
							conceptTopic,
							parse);

					KnowWEEnvironment.getInstance().getArticleManager(web).addArticleToRefresh(
							conceptTopic);
				}

			}

			if (s.equals("return")) {

				// Save on NoParse article.

				String noParseTopic = HermesData.getNoParseTopic();

				if (!KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(
						noParseTopic)) {

					parse = "[" + parse + "\\\\";

					KnowWEEnvironment.getInstance().getWikiConnector().createWikiPage(
							noParseTopic, parse, user);

					KnowWEArticle article = KnowWEArticle.createArticle(parse,
							noParseTopic, KnowWEEnvironment.getInstance().getRootType(),
							web, true);

					KnowWEEnvironment.getInstance().getArticleManager(web)
							.registerArticle(article);

				}
				else {

					parse = "[" + parse + "\\\\";

					KnowWEEnvironment.getInstance().getWikiConnector().appendContentToPage(
							conceptTopic,
							parse);

					KnowWEEnvironment.getInstance().getArticleManager(web).addArticleToRefresh(
							conceptTopic);
				}

			}

			if (s.equals("ignore")) {

				// Save ignores on wiki-page:IgnoredAttributes --> Markup
				// %%IgnoreAttributes .... %
				
				// TODO: tree structure.

				String ignoredTopic = HermesData.getIgnoredTopic();

				if (!KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(
						ignoredTopic)) {

					String temp = "%%IgnoreAttributes " + System.getProperty("line.separator")
							+ concept + System.getProperty("line.separator") + "- " + hermes.get(i)
							+ " == " + value.get(i)
							+ System.getProperty("line.separator") + "%";

					KnowWEEnvironment.getInstance().getWikiConnector().createWikiPage(
							ignoredTopic, temp, user);

					KnowWEArticle article = KnowWEArticle.createArticle(temp,
							ignoredTopic, KnowWEEnvironment.getInstance().getRootType(),
							web, true);

					KnowWEEnvironment.getInstance().getArticleManager(web)
							.registerArticle(article);

				}
				// hasChild concept : add ? else new rootNode.
				else {

					// TODO:
					KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(
							web);

					List<Section<IgnoreContentType>> found = new Vector<Section<IgnoreContentType>>();
					mgr.getArticle(ignoredTopic).getSection().findSuccessorsOfType(
							IgnoreContentType.class, found);

					for (Section<IgnoreContentType> t : found) {
						String temp = t.getChildren().get(0).getOriginalText();
						System.out.println(temp);
					}

					Map<String, String> nodesMap = new HashMap<String, String>();
					nodesMap.put("nodeID", "newText");

					mgr.replaceKDOMNodesSaveAndBuild(map, ignoredTopic, nodesMap);

				}

			}
			i++;
		}

		// Refresh
		KnowWEEnvironment.getInstance().getArticleManager(web).buildArticlesToRefresh();

		if (countData != 0) {
			if (create != null && create.equals("true")) {
				// Append to MappingPage.
				// Add node for created Article.
				String mappingTopic = HermesData.getMappingTopic();

				KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
						web, mappingTopic);

				List<Section<DBpediaContentType>> found = new Vector<Section<DBpediaContentType>>();
				article.getSection().findSuccessorsOfType(DBpediaContentType.class,
						found);

				Section<DBpediaContentType> lastNode = found.get(found.size() - 1);

				String add = "";

				if (!wiki.equals("")) {
					add = lastNode.getChildren().get(0).getOriginalText()
							+ System.getProperty("line.separator") + concept + " => "
							+ LinkedOpenData.getResourceforWikipedia(wiki) + " " + wiki;
				}
				else {
					add = lastNode.getChildren().get(0).getOriginalText()
							+ System.getProperty("line.separator") + concept + " => "
							+ LinkedOpenData.getDBpediaRedirect(concept);
				}

				Map<String, String> nodesMap = new HashMap<String, String>();
				nodesMap.put(lastNode.getChildren().get(0).getID(), add);

				KnowWEEnvironment.getInstance().getArticleManager(
						web).replaceKDOMNodesSaveAndBuild(map, mappingTopic, nodesMap);

				context.getWriter().write(
						"<br/><div style='margin-left:10px;'><img src='KnowWEExtension/images/success.png' align='top'>"
								+ "<b> Artikel erfolgreich erstellt - Sie können zu diesem Konzept nun weitere Attribute abfragen! [Ok]</b></div>");
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

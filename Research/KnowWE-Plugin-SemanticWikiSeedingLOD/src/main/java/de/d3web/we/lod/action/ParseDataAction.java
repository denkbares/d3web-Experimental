package de.d3web.we.lod.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.kdom.KnowWEArticle;

public class ParseDataAction extends AbstractAction {

	@Override
	public void execute(ActionContext context) throws IOException {

		KnowWEParameterMap map = context.getKnowWEParameterMap();

		String concept = map.get("concept");
		String user = map.getUser();
		String web = map.getWeb();

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

		while (matchTypes.find()) {
			type.add(matchTypes.group().substring(1, matchTypes.group().lastIndexOf("\"")));
		}
		while (matchValues.find()) {
			value.add(matchValues.group().substring(1, matchValues.group().lastIndexOf("\"")));
		}
		while (matchHermestags.find()) {
			hermes.add(matchHermestags.group().substring(1,
					matchHermestags.group().lastIndexOf("\"")));
		}
		
		// TODO: ASK Donnerstag!

		for (String s : type) {
			int i = type.indexOf(s);
			if (s.equals("cancel")) {
				// Do nothing.
			}
			if (s.equals("submit")) {

				// Parse text for the ontology.
				// Wiki page: page=concept

				KnowWEEnvironment.getInstance().getWikiConnector().appendContentToPage(concept,
						"<" + hermes.get(i) + ">" + value.get(i) + "</" + hermes.get(i) + ">");

				KnowWEEnvironment.getInstance().getArticleManager(web).addArticleToRefresh(concept);
			}
			if (s.equals("return")) {

				// Save on wiki-page but no parse. same as submit but comment it
				// out!

			}
			if (s.equals("ignore")) {
				// Save ignores on wiki-page:IgnoredAttributes --> Markup
				// %%IgnoreAttributes .... %
				if (!KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(
						"IgnoredAttributes")) {

					String temp = "%%IgnoreAttributes " + System.getProperty("line.separator")
							+ hermes.get(i) + " == " + value.get(i)
							+ System.getProperty("line.separator") + "%";

					KnowWEEnvironment.getInstance().getWikiConnector().createWikiPage(
							"IgnoredAttributes", temp, user);

					KnowWEArticle article = KnowWEArticle.createArticle(temp,
							"IgnoredAttributes", KnowWEEnvironment.getInstance().getRootType(),
							web, true);

					KnowWEEnvironment.getInstance().getArticleManager(web)
							.registerArticle(article);
				}
				else {

					KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(
							web);

					Map<String, String> nodesMap = new HashMap<String, String>();
					nodesMap.put("nodeID", "newText");
					mgr.replaceKDOMNodesSaveAndBuild(map, "IgnoredAttributes", nodesMap);

					/////
					KnowWEEnvironment.getInstance().getWikiConnector().getArticleSource(
							"IgnoredAttributes").replace(
							System.getProperty("line.separator") + "%", "");

					KnowWEEnvironment.getInstance().getArticleManager(web).getArticle(
							"IgnoredAttributes");

				}

			}

		}
		// Refresh?
		KnowWEEnvironment.getInstance().getArticleManager(web).buildArticlesToRefresh();

		context.getWriter().write("<b>Success!</b>");
	}

}

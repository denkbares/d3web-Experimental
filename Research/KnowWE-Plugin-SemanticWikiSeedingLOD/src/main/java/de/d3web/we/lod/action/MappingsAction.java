package de.d3web.we.lod.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.lod.HermesData;
import de.d3web.we.lod.LinkedOpenData;
import de.d3web.we.lod.markup.MappingContentType;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class MappingsAction extends AbstractAction {

	@Override
	public void execute(ActionContext context) throws IOException {

		KnowWEParameterMap map = context.getKnowWEParameterMap();
		String type = map.get("type");
		String user = map.getUser();
		String web = map.getWeb();
		String mappingTopic = HermesData.getMappingTopic();

		// Execute Query to get all Hermes Concepts.
		String query =
				"SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		TupleQueryResult result = SPARQLUtil.executeTupleQuery(query);

		LinkedHashMap<String, String> corresDBpediaConcepts = new
				LinkedHashMap<String, String>();

		int count = 0;
		int found = 0;
		try {
			while (result.hasNext()) {
				count++;
				BindingSet set = result.next();
				String title = set.getBinding("x").getValue().stringValue();
				title = URLDecoder.decode(title, "UTF-8");
				title = title.substring(title.indexOf("#") + 1);
				String redirect = LinkedOpenData.getDBpediaRedirect(title);
				if (redirect != "") {
					found++;
				}
				corresDBpediaConcepts.put(title, redirect);
			}
		}
		catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// New - deletes all previous contents.
		if (type.equals("new")) {

			StringBuffer buffy = new StringBuffer();
			Iterator<String> it = corresDBpediaConcepts.keySet().iterator();
			String output = "";

			while (it.hasNext()) {
				String temp = it.next();
				buffy.append(temp + " => "
							+ corresDBpediaConcepts.get(temp)
							+ System.getProperty("line.separator"));
			}

			output = "%%Mapping " + System.getProperty("line.separator")
						+ buffy.toString()
						+ "%";

			KnowWEEnvironment
						.getInstance()
						.getWikiConnector()
						.createWikiPage(mappingTopic, output,
								user);

			KnowWEArticle article = KnowWEArticle.createArticle(output,
						mappingTopic, KnowWEEnvironment.getInstance()
								.getRootType(), web, true);
			KnowWEEnvironment.getInstance().getArticleManager(web)
						.registerArticle(article);

			context.getWriter().write(
					"<div style='margin-left:10px;'><p><img src='KnowWEExtension/images/success.png'><b> Artikel "
							+ mappingTopic
							+ " erfolgreich erstellt. </b>("
							+ found
							+ "/" + count + ")</p></div>");
		}
		// Updates concepts, but ignores concepts specified by wikipedia
		// user link.
		else if (type.equals("refresh")) {

			KnowWEArticle article = KnowWEEnvironment.getInstance()
					.getArticle(web, mappingTopic);

			List<Section<MappingContentType>> found1 = new
					Vector<Section<MappingContentType>>();
			article.getSection().findSuccessorsOfType(
					MappingContentType.class, found1);

			Map<String, String> nodesMap = new HashMap<String, String>();

			for (Section<MappingContentType> t : found1) {

				String temp = t.getChildren().get(0)
								.getOriginalText();

				String hermes = temp.substring(0, temp.indexOf(" =>"));
				String value = "";

				if (temp.matches(".* => .+")) {
					value = temp.substring(temp.indexOf(" => ") + 4);
				}

				if (corresDBpediaConcepts.containsKey(hermes)) {
					if (!corresDBpediaConcepts.get(hermes).equals(value)
							&& !value.matches("http://[\\p{Alnum}/.:_]+ http://[\\p{Alnum}/.:_]+")) {
						// adde
						nodesMap.put(t.getChildren().get(0).getID(), hermes + " => "
								+ corresDBpediaConcepts.get(hermes));
					}
					corresDBpediaConcepts.remove(hermes);
				}
			}

			Section<MappingContentType> lastNode = found1.get(found1.size() - 1);

			// Neue Konzepte vorhanden.
			if (corresDBpediaConcepts.size() > 0) {
				String add = "";
				for (String s : corresDBpediaConcepts.keySet()) {
					add += s + " => " + corresDBpediaConcepts.get(s)
							+ System.getProperty("line.separator");
				}
				add = add.substring(0, add.length() - 2);
				nodesMap.put(
						lastNode.getChildren().get(0).getID(),
						lastNode.getChildren().get(0).getOriginalText()
								+ System.getProperty("line.separator") + add);
			}

			KnowWEEnvironment
						.getInstance()
						.getArticleManager(web)
						.replaceKDOMNodesSaveAndBuild(map, mappingTopic,
								nodesMap);

			StringBuffer updates = new StringBuffer();

			for (String s : nodesMap.keySet()) {
				String temp = nodesMap.get(s);
				if (temp.matches(".+ => .+")) {
					String[] split = temp.split(" => ");
					updates.append(split[0] + " => " + split[1] + "<br/>");
				}
			}

			if (updates.toString().isEmpty()) {
				updates.append("Stand bereits aktuell.");
			}

			context.getWriter().write(
						"<div style='margin-left:10px;'><p><img src='KnowWEExtension/images/success.png'><b> Artikel "
								+ mappingTopic
								+ " erfolgreich aktualisiert: </b><br/>"
								+ updates + "</div>");
		}
	}
}

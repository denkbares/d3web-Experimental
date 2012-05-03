package de.knowwe.lod.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.Strings;
import de.knowwe.lod.HermesData;
import de.knowwe.lod.LinkedOpenData;
import de.knowwe.lod.markup.MappingContentType;
import de.knowwe.rdf2go.Rdf2GoCore;

public class MappingsAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String type = context.getParameter("type");
		String user = context.getUserName();
		String web = context.getWeb();
		String mappingTopic = HermesData.getMappingTopic();

		// Execute Query to get all Hermes Concepts.
		String query =
				"SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);

		LinkedHashMap<String, String> corresDBpediaConcepts = new
				LinkedHashMap<String, String>();

		int count = 0;
		int found = 0;
		while (result.hasNext()) {
			count++;
			QueryRow row = result.next();
			String title = row.getValue("x").toString();
			title = Strings.decodeURL(title);
			title = title.substring(title.indexOf("#") + 1);
			String redirect = LinkedOpenData.getDBpediaRedirect(title);
			if (redirect != "") {
				found++;
			}
			corresDBpediaConcepts.put(title, redirect);
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

			Environment
						.getInstance()
						.getWikiConnector()
						.createArticle(mappingTopic, output,
								user);

			Article article = Article.createArticle(output,
						mappingTopic, web, true);
			Environment.getInstance().getArticleManager(web)
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

			Article article = Environment.getInstance()
					.getArticle(web, mappingTopic);

			List<Section<MappingContentType>> found1 = new
					Vector<Section<MappingContentType>>();
			Sections.findSuccessorsOfType(
						article.getRootSection(), MappingContentType.class, found1);

			Map<String, String> nodesMap = new HashMap<String, String>();

			for (Section<MappingContentType> t : found1) {

				String temp = t.getChildren().get(0)
								.getText();

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
						lastNode.getChildren().get(0).getText()
								+ System.getProperty("line.separator") + add);
			}

			Sections
						.replaceSections(context, nodesMap);

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

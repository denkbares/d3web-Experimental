package de.d3web.we.lod.taghandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.lod.HermesData;
import de.d3web.we.lod.LinkedOpenData;
import de.d3web.we.lod.markup.MappingContentType;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.wikiConnector.KnowWEUserContext;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class ConceptHandler extends AbstractHTMLTagHandler {

	public ConceptHandler() {
		super("concepts");
	}

	@Override
	public String renderHTML(String topic, KnowWEUserContext user,
			Map<String, String> parameters, String web) {

		boolean initial = parameters.containsKey("initial");

		String query = "SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		TupleQueryResult result = SPARQLUtil.executeTupleQuery(query);

		HashMap<String, String> corresDBpediaConcepts = new HashMap<String, String>();

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
		StringBuffer buffy = new StringBuffer();
		Iterator<String> it = corresDBpediaConcepts.keySet().iterator();
		String output = "";

		String mappingTopic = HermesData.getMappingTopic();

		// Initial - deletes all previous contents.
		if (initial) {
			while (it.hasNext()) {
				String temp = it.next();
				buffy.append(temp + " => " + corresDBpediaConcepts.get(temp)
						+ System.getProperty("line.separator"));
			}

			output = "%%Mapping "
					+ System.getProperty("line.separator") + buffy.toString()
					+ System.getProperty("line.separator") + "%";

			KnowWEEnvironment.getInstance().getWikiConnector()
					.createWikiPage(mappingTopic, output, user.getUserName());

			KnowWEArticle article = KnowWEArticle.createArticle(output, mappingTopic,
					KnowWEEnvironment.getInstance().getRootType(), web, true);
			KnowWEEnvironment.getInstance().getArticleManager(web)
					.registerArticle(article);

			return "<p><img src='KnowWEExtension/images/success.png'><b> Artikel " + mappingTopic
					+ " erfolgreich erstellt. </b>(" + found + "/" + count
					+ ")</p>";
		}
		// Updates concepts, but ignores concepts specified by wikipedia user
		// link.
		else {

			KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
					web, mappingTopic);

			List<Section<MappingContentType>> found1 = new Vector<Section<MappingContentType>>();
			article.getSection().findSuccessorsOfType(MappingContentType.class,
					found1);

			Map<String, String> nodesMap = new HashMap<String, String>();

			while (it.hasNext()) {
				String hermes = it.next();
				String dbpedia = corresDBpediaConcepts.get(hermes);
				if (!dbpedia.isEmpty()) {
					for (Section<MappingContentType> t : found1) {
						String complete = t.getChildren().get(0).getOriginalText();

						String filter = ".+ => http://[\\p{Alnum}/.:_]+";
						Pattern pattern = Pattern.compile(filter);
						Matcher matcher = pattern.matcher(complete);
						String cut = "";

						if (matcher.find()) {
							cut = matcher.group();
						}
						if (!cut.equals(hermes + " => " + dbpedia)
								&& !complete.matches(".+ => http://[\\p{Alnum}/.:_]+ http://[\\p{Alnum}/.:_]+")) {
							// update
							nodesMap.put(t.getChildren().get(0).getID(), hermes + " => " + dbpedia);
						}
					}
				}
			}

			KnowWEParameterMap map = new KnowWEParameterMap(KnowWEAttributes.WEB, web);
			map.put(KnowWEAttributes.USER, user.getUserName());

			KnowWEEnvironment.getInstance().getArticleManager(
					web).replaceKDOMNodesSaveAndBuild(map, mappingTopic, nodesMap);

			StringBuffer updates = new StringBuffer();

			for (String s : map.keySet()) {
				updates.append(s + " => " + map.get(s) + "<br/>");
			}

			return "<p><img src='KnowWEExtension/images/success.png'><b> Artikel " + mappingTopic
					+ " erfolgreich aktualisiert: </b><br/>" + updates;
		}
	}
}

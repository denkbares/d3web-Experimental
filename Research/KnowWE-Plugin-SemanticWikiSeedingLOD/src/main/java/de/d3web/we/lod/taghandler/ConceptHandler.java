package de.d3web.we.lod.taghandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.lod.LinkedOpenData;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.wikiConnector.KnowWEUserContext;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class ConceptHandler extends AbstractHTMLTagHandler {

	private static final String wikiPage = "DBpediaMapping";

	public ConceptHandler() {
		super("concepts");
	}

	@Override
	public String renderHTML(String topic, KnowWEUserContext user,
			Map<String, String> parameters, String web) {

		String query = "SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		TupleQueryResult result = SPARQLUtil.executeTupleQuery(query);

		HashMap<String, String> corresDBpediaConcepts = new HashMap<String, String>();

		try {
			while (result.hasNext()) {
				BindingSet set = result.next();
				String title = set.getBinding("x").getValue().stringValue();
				try {
					title = URLDecoder.decode(title, "UTF-8");
					title = title.substring(title.indexOf("#") + 1);
					String redirect = LinkedOpenData.getDBpediaRedirect(title);
					corresDBpediaConcepts.put(title, redirect);

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		StringBuffer buffy = new StringBuffer();
		Iterator<String> it = corresDBpediaConcepts.keySet().iterator();

		while (it.hasNext()) {
			String temp = it.next();
			buffy.append(temp + " => " + corresDBpediaConcepts.get(temp) + " "
					+ System.getProperty("line.separator"));
		}

		String output = "%%DBpediaMapping "
				+ System.getProperty("line.separator") + buffy.toString()
				+ System.getProperty("line.separator") + "%";

		KnowWEEnvironment.getInstance().getWikiConnector()
				.createWikiPage(wikiPage, output, user.getUserName());

		KnowWEArticle article = KnowWEArticle.createArticle(output, wikiPage,
				KnowWEEnvironment.getInstance().getRootType(), web, true);
		KnowWEEnvironment.getInstance().getArticleManager(web)
				.registerArticle(article);

		return "<b>Article DBpediaMapping succesfully created.</b>";
	}
}

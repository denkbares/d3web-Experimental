package de.d3web.we.lod.taghandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.rendering.PageAppendHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class GetDataAppendHandler implements PageAppendHandler {

	@Override
	public String getDataToAppend(String topic, String web, KnowWEUserContext user) {

		String query = "SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		TupleQueryResult result = SPARQLUtil.executeTupleQuery(query);
		List<String> titleList = new ArrayList<String>();
		try {
			while (result.hasNext()) {
				BindingSet set = result.next();

				String title = set.getBinding("x").getValue().stringValue();

				try {
					title = URLDecoder.decode(title, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				title = title.substring(title.indexOf("#") + 1);
				if (KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(title)) {
					titleList.add(title);
				}
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		if (titleList.contains(topic)) {

			return KnowWEUtils.maskHTML("<body onload='getDataForConcept();'><form action='javascript:getDataForConcept()'>"
					+ "<div class='layout'><p class='tags'>Konzept: </p><input id='conceptname'  type='text' value='"
					+ topic
					+ "'>"
					+ "<input type='submit' onclick='getDataForConcept();' value='OK'><input type='checkbox' id='debug'>Debug"
					+ "<p class='tags' id='wikiurl' onclick='shownhide();' style='cursor: pointer;'>+Wikipedia-URL (@en)</p>"
					+ "<input id='wikiinput' style='display:none;' type='text' size='38'>"
					+ "</div></form><div id='conceptdata'></div></body>");
		}
		return "";

	}

	@Override
	public boolean isPre() {
		return false;
	}

}

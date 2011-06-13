package de.knowwe.lod.taghandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.rendering.PageAppendHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.rdf2go.Rdf2GoCore;

public class GetDataAppendHandler implements PageAppendHandler {

	@Override
	public String getDataToAppend(String topic, String web, UserContext user) {

		String query = "SELECT ?x WHERE {?x rdf:type lns:Hermes-Object} ORDER BY ASC(?x)";
		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);
		List<String> titleList = new ArrayList<String>();
		try {
			while (result.hasNext()) {
				QueryRow row = result.next();

				String title = row.getValue("x").toString();

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

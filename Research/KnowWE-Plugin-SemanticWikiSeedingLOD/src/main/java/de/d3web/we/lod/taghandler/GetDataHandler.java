package de.d3web.we.lod.taghandler;

import java.util.Map;

import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class GetDataHandler extends AbstractHTMLTagHandler {

	public GetDataHandler() {
		super("getdata");

	}

	@Override
	public String renderHTML(String topic, KnowWEUserContext user,
			Map<String, String> parameters, String web) {

		return "<div id='conceptdata'><form>"
				+ "<p>Concept: <input id=\"concept\"  type=\"text\" size=\"30\" />"
				+ "<input onclick=\"getDataForConcept('"+web+"');\" type=\"button\" value=\"Ok\"/></form></div>";

	}
}

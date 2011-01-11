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

		return "<form>"
				+ "<p>Concept: <input id='concept'  type='text'>"
				+ "<input type='button' onclick='getDataForConcept();' value='OK'></p></form><div id='conceptdata'></div>";

	}
}

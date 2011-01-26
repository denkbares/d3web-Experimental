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

		return "<form action='javascript:getDataforConcept()'>"
				+ "<div class='layout'><p class='tags'>Konzept: </p><input id='conceptname'  type='text'>"
				+ "<input type='submit' onclick='getDataForConcept();' value='OK'><input type='checkbox' id='debug'>Debug?"
				+ "<p class='tags' id='wikiurl' onclick='shownhide();' style='cursor: pointer;'>+Wikipedia-URL (@en)</p><input id='wikiinput' style='display:none;' type='text' size='38'>"
				+ "</div></form><div id='conceptdata'></div>";

	}
}

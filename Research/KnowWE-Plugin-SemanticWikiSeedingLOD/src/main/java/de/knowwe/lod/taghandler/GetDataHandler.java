package de.knowwe.lod.taghandler;

import java.util.Map;

import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;

public class GetDataHandler extends AbstractHTMLTagHandler {

	public GetDataHandler() {
		super("getdata");

	}

	@Override
	public String renderHTML(String topic, UserContext user,
			Map<String, String> parameters, String web) {

		return "<form action='javascript:getDataForConcept()'>"
				+ "<div class='layout'><p class='tags'>Konzept: </p><input id='conceptname'  type='text'>"
				+ "<input type='submit' onclick='getDataForConcept();' value='OK'><input type='checkbox' id='debug'>Debug"
				+ "<p class='tags' id='wikiurl' onclick='shownhide();' style='cursor: pointer;'>+Wikipedia-URL (@en)</p><input id='wikiinput' style='display:none;' type='text' size='38'>"
				+ "</div></form><div id='conceptdata'></div>";

	}
}

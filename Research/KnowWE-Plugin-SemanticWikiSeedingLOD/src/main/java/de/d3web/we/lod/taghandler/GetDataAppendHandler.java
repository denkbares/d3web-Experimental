package de.d3web.we.lod.taghandler;

import de.d3web.we.kdom.rendering.PageAppendHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class GetDataAppendHandler implements PageAppendHandler {

	@Override
	public String getDataToAppend(String topic, String web, KnowWEUserContext user) {

		return KnowWEUtils.maskHTML("<body onload='getDataForConcept();'><form action='javascript:getDataForConcept()'>"
				+ "<div class='layout'><p class='tags'>Konzept: </p><input id='conceptname'  type='text' value='"
				+ topic
				+ "'>"
				+ "<input type='submit' onclick='getDataForConcept();' value='OK'><input type='checkbox' id='debug'>Debug"
				+ "<p class='tags' id='wikiurl' onclick='shownhide();' style='cursor: pointer;'>+Wikipedia-URL (@en)</p>"
				+ "<input id='wikiinput' style='display:none;' type='text' size='38'>"
				+ "</div></form><div id='conceptdata'></div></body>");

	}

	@Override
	public boolean isPre() {
		return false;
	}

}

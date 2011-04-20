package de.knowwe.lod.taghandler;

import java.util.Map;

import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.user.UserContext;

public class ConceptHandler extends AbstractHTMLTagHandler {

	public ConceptHandler() {
		super("concepts");
	}

	@Override
	public String renderHTML(String topic, UserContext user,
			Map<String, String> parameters, String web) {

		return "<form>"
				+ "<div class='layout'><p class='tags' align='middle'>Mapping-Handler</p>"
				+ "<input type='button' onclick=\"Mappings('new');\" value='Neu'><input type='button' onclick=\"Mappings('refresh');\" value='Aktualisieren'>"
				+ "</div></form><div id='maphandler'></div>";

	}
}
package de.knowwe.owlapi.query.pellet;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class PelletAskRenderer implements Renderer {

	private static final String PREFIX;

	static {
		PREFIX = PelletSparqlUtils.getDefaultNamespacesVerbalized();
	}

	@Override
	public void render(Section<?> section, UserContext user, RenderResult string) {

		String query = DefaultMarkupType.getAnnotation(section, PelletSparqlAsk.QUERY);

		StringBuilder html = new StringBuilder();

		if (query != null && !query.trim().isEmpty()) {
			query = query.replace(",", "");
			boolean result = PelletSparqlUtils.askQuery(PREFIX + query);

			html.append("<div style=\"background: none repeat scroll 0 0 #FFFE9D;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");
			html.append("<dl><dt>Query: " + query + "</dt>");

			if (result) {
				html.append("<dd>Congratulations! At least one answer exists!</dd>");
			}
			else {
				html.append("<dd>I am sorry! I could not find any answer!</dd>");
			}
			html.append("</dl></div>");
		}

		if (html.length() == 0) {
			html.append("<div style=\"background: none repeat scroll 0 0 #FFFE9D;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");
			html.append(PelletSparqlAsk.getDescription(user));
			html.append("</div>");
		}
		string.appendHTML(html.toString());
	}
}

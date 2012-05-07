package de.knowwe.owlapi.query.pellet;

import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class PelletSelectRenderer implements Renderer {

	public static final String LB = System.getProperty("line.separator");

	private static final String PREFIX;

	static {
		PREFIX = PelletSparqlUtils.getDefaultNamespacesVerbalized();
	}

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder string) {

		String query = DefaultMarkupType.getAnnotation(section, PelletSparqlSelect.QUERY);

		StringBuilder html = new StringBuilder();

		if (query != null && !query.trim().isEmpty()) {
			query = query.replace(",", "");
			ResultSet rs = PelletSparqlUtils.selectQuery(PREFIX + query);
			if (rs != null) {
				printDefaultView(html, rs, query);
			}
		}

		if (html.length() == 0) {
			html.append("<div style=\"background: none repeat scroll 0 0 #FFFE9D;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");
			html.append(PelletSparqlSelect.getDescription(user));
			html.append("</div>");
		}
		string.append(Strings.maskHTML(html.toString()));
	}

	/**
	 * Prints the results from the SPARQL query.
	 * 
	 * @created 06.01.2012
	 * @param string
	 * @param rs
	 * @param query
	 */
	private void printDefaultView(StringBuilder string, ResultSet rs, String query) {

		string.append("<div style=\"background: none repeat scroll 0 0 #FFFE9D;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");
		string.append("<dl><dt>Query: " + query + "</dt>");

		List<String> resultVars = rs.getResultVars();

		while (rs.hasNext()) {
			QuerySolution solution = rs.nextSolution();

			string.append("<dd>");
			for (String str : resultVars) {
				RDFNode node = solution.get(str);
				string.append(node.asResource().getLocalName());
			}
			string.append("</dd>");
		}
		string.append("</dl></div>");
	}
}

package de.knowwe.rdf2go.sparql;

import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

public class SparqlResultRenderer {

	private static final String POINT_ID = "SparqlResultNodeRenderer";

	private static SparqlResultRenderer instance = null;

	private final SparqlResultNodeRenderer[] nodeRenderers;

	public static SparqlResultRenderer getInstance() {
		if (instance == null) instance = new SparqlResultRenderer();
		return instance;
	}

	private SparqlResultRenderer() {
		nodeRenderers = getNodeRenderer();
	}

	private SparqlResultNodeRenderer[] getNodeRenderer() {
		Extension[] extensions = PluginManager.getInstance().getExtensions(
				Rdf2GoCore.PLUGIN_ID, POINT_ID);
		SparqlResultNodeRenderer[] renderers = new SparqlResultNodeRenderer[extensions.length];
		for (int i = 0; i < extensions.length; i++) {
			renderers[i] = ((SparqlResultNodeRenderer) extensions[i].getSingleton());
		}
		return renderers;
	}

	public String renderQueryResult(QueryResultTable qrt) {
		return renderQueryResult(qrt, false);
	}

	/**
	 * 
	 * @created 06.12.2010
	 * @param qrt
	 * @return html table with all results of qrt
	 */
	public String renderQueryResult(QueryResultTable qrt, boolean rawOutput) {
		boolean tablemode = false;
		boolean empty = true;

		List<String> variables = qrt.getVariables();
		ClosableIterator<QueryRow> iterator = qrt.iterator();
		StringBuilder result = new StringBuilder();
		tablemode = variables.size() > 1;

		if (tablemode) {
			result.append(Strings.maskHTML("<table>"));
			for (String var : variables) {
				result.append(Strings.maskHTML("<td><b>") + var
						+ Strings.maskHTML("<b/></td>"));
			}
		}
		else {
			result.append(Strings.maskHTML("<ul style='white-space: normal'>"));
		}

		while (iterator.hasNext()) {
			empty = false;

			QueryRow row = iterator.next();

			if (tablemode) {
				result.append(Strings.maskHTML("<tr>"));
			}
			for (String var : variables) {
				Node node = row.getValue(var);
				String erg = renderNode(node, var, rawOutput);

				if (tablemode) {
					result.append(Strings.maskHTML("<td>") + erg
							+ Strings.maskHTML("</td>\n"));
				}
				else {
					result.append(Strings.maskHTML("<li>") + erg
							+ Strings.maskHTML("</li>\n"));
				}

			}
			if (tablemode) {
				result.append(Strings.maskHTML("</tr>"));
			}
		}

		if (empty) {
			result.append(Messages.getMessageBundle().getString(
					"KnowWE.owl.query.no_result"));
		}
		if (tablemode) {
			result.append(Strings.maskHTML("</table>"));
		}
		else {
			result.append(Strings.maskHTML("</ul>"));
		}
		return result.toString();
	}

	public String renderNode(Node node, String var, boolean rawOutput) {
		if (node == null) {
			return "";
		}
		String rendered = node.toString();
		if (!rawOutput) {
			for (SparqlResultNodeRenderer nodeRenderer : nodeRenderers) {
				String temp = rendered;
				rendered = nodeRenderer.renderNode(rendered, var);
				if (!temp.equals(rendered) && !nodeRenderer.allowFollowUpRenderer()) break;
			}
			rendered = Strings.maskJSPWikiMarkup(rendered);
		}
		return rendered;
	}
}

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
import de.knowwe.rdf2go.sparql.utils.Pair;

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

	public Pair<String, Integer> renderQueryResult(QueryResultTable qrt) {
		return renderQueryResult(qrt, false, false);
	}

	/**
	 * 
	 * @created 06.12.2010
	 * @param qrt
	 * @return html table with all results of qrt and size of qrt
	 */
	public Pair<String, Integer> renderQueryResult(QueryResultTable qrt, boolean rawOutput, boolean zebraMode) {
		boolean tablemode = false;
		boolean empty = true;
		int i = 0;
		List<String> variables = qrt.getVariables();
		ClosableIterator<QueryRow> iterator = qrt.iterator();
		StringBuilder result = new StringBuilder();
		tablemode = variables.size() > 1;

		// TODO
		// for test purpose only (remove afterwards!)
		// tablemode = true;
		if (tablemode) {
			result.append(Strings.maskHTML("<table class='wikitable' border='1'>"));
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
				if (zebraMode) {
					result.append(Strings.maskHTML(i % 2 == 1 ? "<tr>" : "<tr class='odd'>"));
				}
				else {
					result.append(Strings.maskHTML("<tr>"));
				}

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
			i++;
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
		return new Pair<String, Integer>(result.toString(), i);
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

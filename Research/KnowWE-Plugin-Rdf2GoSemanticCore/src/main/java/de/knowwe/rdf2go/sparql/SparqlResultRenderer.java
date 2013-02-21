package de.knowwe.rdf2go.sparql;

import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.utils.RenderOptions;
import de.knowwe.rdf2go.sparql.utils.SparqlRenderResult;

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

	public SparqlRenderResult renderQueryResult(QueryResultTable qrt, UserContext user) {
		// TODO
		// is this a good idea?
		return renderQueryResult(qrt, new RenderOptions("defaultID"), user);
	}

	/**
	 * 
	 * @created 06.12.2010
	 * @param qrt
	 * @param opts TODO
	 * @return html table with all results of qrt and size of qrt
	 */
	public SparqlRenderResult renderQueryResult(QueryResultTable qrt, RenderOptions opts, UserContext user) {
		boolean tablemode = false;
		boolean empty = true;
		boolean zebraMode = opts.isZebraMode();
		boolean rawOutput = opts.isRawOutput();
		int i = 0;
		List<String> variables = qrt.getVariables();
		ClosableIterator<QueryRow> iterator = qrt.iterator();
		RenderResult result = new RenderResult(user);
		tablemode = variables.size() > 1;

		// TODO
		// for test purpose only (remove afterwards!)
		// tablemode = true;
		if (tablemode) {
			result.appendHtml("<table class='sparqltable'>");
			result.appendHtml(!zebraMode ? "<tr>" : "<tr class='odd'>");
			for (String var : variables) {

				result.appendHtml("<td><b>");
				result.appendHtml("<a href='#/' onclick=\"KNOWWE.plugin.semantic.actions.sortResultsBy('"
						+ var + "','" + opts.getId() + "');\">");
				result.append(var);
				result.appendHtml("</a>");
				if (hasSorting(var, opts.getSortingMap())) {
					String symbol = getSortingSymbol(var, opts.getSortingMap());
					result.appendHtml("<img src='KnowWEExtension/images/" + symbol
							+ "' alt='Sort by '"
							+ var + "border='0' /><b/></td>");
				}

			}
			result.appendHtml("</tr>");
		}
		else {
			result.appendHtml("<ul style='white-space: normal'>");
		}

		while (iterator.hasNext()) {
			empty = false;

			QueryRow row = iterator.next();

			if (tablemode) {
				if (zebraMode) {
					result.appendHtml(i % 2 == 0 ? "<tr>" : "<tr class='odd'>");
				}
				else {
					result.appendHtml("<tr>");
				}

			}

			for (String var : variables) {
				Node node = row.getValue(var);
				String erg = renderNode(node, var, rawOutput, user);

				if (tablemode) {
					result.appendHtml("<td>");
					result.append(erg);
					result.appendHtml("</td>\n");
				}
				else {
					result.appendHtml("<li>");
					result.append(erg);
					result.appendHtml("</li>\n");
				}

			}
			if (tablemode) {
				result.appendHtml("</tr>");
			}
			i++;
		}

		if (empty) {
			result.append(Messages.getMessageBundle().getString(
					"KnowWE.owl.query.no_result"));
		}
		if (tablemode) {
			result.appendHtml("</table>");
		}
		else {
			result.appendHtml("</ul>");
		}
		return new SparqlRenderResult(result.toStringRaw(), i);
	}

	public String renderNode(Node node, String var, boolean rawOutput, UserContext user) {
		if (node == null) {
			return "";
		}
		String rendered = node.toString();
		if (!rawOutput) {
			for (SparqlResultNodeRenderer nodeRenderer : nodeRenderers) {
				String temp = rendered;
				rendered = nodeRenderer.renderNode(rendered, var, user);
				if (!temp.equals(rendered) && !nodeRenderer.allowFollowUpRenderer()) break;
			}
			rendered = Strings.maskJSPWikiMarkup(rendered);
		}
		return rendered;
	}

	private String getSortingSymbol(String value, Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		if (map.containsKey(value)) {
			sb.append("arrow");
			sb.append("_");
			if (map.get(value).equals("ASC")) {
				sb.append("down");
			}
			else {
				sb.append("up");
			}
		}
		sb.append(".png");
		return sb.toString().toLowerCase();
	}

	private boolean hasSorting(String value, Map<String, String> map) {
		if (map.containsKey(value)) {
			return true;
		}
		return false;
	}
}

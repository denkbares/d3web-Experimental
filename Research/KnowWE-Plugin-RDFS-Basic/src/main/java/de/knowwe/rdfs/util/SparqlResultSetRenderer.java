package de.knowwe.rdfs.util;

import java.util.Collection;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

public class SparqlResultSetRenderer {

	/**
	 * 
	 * @created 06.12.2010
	 * @param qrt
	 * @return html table with all results of qrt
	 */
	public static String renderQueryResult(QueryResultTable qrt, boolean links) {
		boolean tablemode = false;
		boolean empty = true;

		List<String> l = qrt.getVariables();
		ClosableIterator<QueryRow> i = qrt.iterator();
		String result = "";
		if (!tablemode) {
			tablemode = l.size() > 1;
		}
		if (tablemode) {
			result += Strings.maskHTML("<table>");
			for (String var : l) {
				result += Strings.maskHTML("<th>") + var
						+ Strings.maskHTML("</th>");
			}
		}
		else {
			result += Strings.maskHTML("<ul style='white-space: normal'>");
		}

		while (i.hasNext()) {
			empty = false;

			if (!tablemode) {
				tablemode = qrt.getVariables().size() > 1;
			}
			QueryRow s = i.next();

			if (tablemode) {
				result += Strings.maskHTML("<tr>");
			}
			for (String var : l) {
				Node n = s.getValue(var);
				String erg = renderNode(links, n);

				if (tablemode) {
					result += Strings.maskHTML("<td>") + erg
							+ Strings.maskHTML("</td>\n");
				}
				else {
					result += Strings.maskHTML("<li>") + erg
							+ Strings.maskHTML("</li>\n");
				}

			}
			if (tablemode) {
				result += Strings.maskHTML("</tr>");
			}
		}

		if (empty) {
			result = Messages.getMessageBundle().getString(
					"KnowWE.owl.query.no_result");
		}
		else {
			if (tablemode) {
				result += Strings.maskHTML("</table>");
			}
			else {
				result += Strings.maskHTML("</ul>");
			}
		}
		return result;
	}

	public static String renderNode(boolean links, Node n) {
		String erg = "";
		if (n != null) {
			erg = Rdf2GoCore.getInstance().reduceNamespace(
					n.toString());
		}

		erg = Strings.decodeURL(erg);

		if (links) {
			if (erg.startsWith("lns:")) {
				erg = erg.substring(4);
			}
			Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
					new TermIdentifier(erg));

			if (termDefinitions != null && termDefinitions.size() > 0) {
				erg = CompileUtils.createLinkToDefinition(new TermIdentifier(erg));
			}
			else if (Environment.getInstance()
					.getWikiConnector().doesArticleExist(erg)) {
				erg = Strings.maskHTML("<a href=\"Wiki.jsp?page=")
						+ erg + Strings.maskHTML("\">") + erg
						+ Strings.maskHTML("</a>");
			}

		}
		return erg;
	}
}

package de.knowwe.rdfs.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;
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
			result += KnowWEUtils.maskHTML("<table>");
			for (String var : l) {
				result += KnowWEUtils.maskHTML("<th>") + var
						+ KnowWEUtils.maskHTML("</th>");
			}
		}
		else {
			result += KnowWEUtils.maskHTML("<ul>");
		}

		while (i.hasNext()) {
			empty = false;

			if (!tablemode) {
				tablemode = qrt.getVariables().size() > 1;
			}
			QueryRow s = i.next();

			if (tablemode) {
				result += KnowWEUtils.maskHTML("<tr>");
			}
			for (String var : l) {
				Node n = s.getValue(var);
				String erg = "";
				if (n != null) {
					erg = Rdf2GoCore.getInstance().reduceNamespace(
							s.getValue(var).toString());
				}

				try {
					erg = URLDecoder.decode(erg, "UTF-8");
				}
				catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (links) {
					if (erg.startsWith("lns:")) {
						erg = erg.substring(4);
					}
					try {
						Collection<Section<? extends TermDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
								erg);

						if (termDefinitions != null && termDefinitions.size() > 0) {
							erg = CompileUtils.createLinkToDefinition(erg);
						}
						else if (KnowWEEnvironment.getInstance()
								.getWikiConnector().doesPageExist(erg)
								|| KnowWEEnvironment.getInstance()
										.getWikiConnector().doesPageExist(
												URLDecoder.decode(erg,
														"UTF-8"))) {
							erg = KnowWEUtils.maskHTML("<a href=\"Wiki.jsp?page=")
									+ erg + KnowWEUtils.maskHTML("\">") + erg
									+ KnowWEUtils.maskHTML("</a>");
						}
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

				}

				if (tablemode) {
					result += KnowWEUtils.maskHTML("<td>") + erg
							+ KnowWEUtils.maskHTML("</td>\n");
				}
				else {
					result += KnowWEUtils.maskHTML("<li>") + erg
							+ KnowWEUtils.maskHTML("</li>\n");
				}

			}
			if (tablemode) {
				result += KnowWEUtils.maskHTML("</tr>");
			}
		}

		if (empty) {
			ResourceBundle rb = KnowWEEnvironment.getInstance().getKwikiBundle();
			result = rb.getString("KnowWE.owl.query.no_result");
		}
		else {
			if (tablemode) {
				result += KnowWEUtils.maskHTML("</table>");
			}
			else {
				result += KnowWEUtils.maskHTML("</ul>");
			}
		}
		return result;
	}
}

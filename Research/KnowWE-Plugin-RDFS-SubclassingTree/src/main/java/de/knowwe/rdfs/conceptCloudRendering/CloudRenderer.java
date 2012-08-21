/*
 * Copyright (C) 2012 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.rdfs.conceptCloudRendering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 21.06.2012
 */
public class CloudRenderer implements Renderer {

	private final static String dotInstallation = "/usr/local/bin/dot";

	// private final static String dotInstallation = "";

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder string) {

		// remember which edges are already rendered to avoid duplicate arcs
		Set<String> renderedEdges = new HashSet<String>();

		String concept = DefaultMarkupType.getAnnotation(section,
				ConceptCloudMarkup.CONCEPT_KEY).trim();

		String relation = DefaultMarkupType.getAnnotation(section,
				ConceptCloudMarkup.RELATION_KEY).trim();

		StringBuffer dotSource = new StringBuffer();

		dotSource.append("digraph finite_state_machine {\n");

		dotSource.append("layout=\"neato\"\ncompound=true\n	nodesep=1.0\noverlap=false\nlen=22\nsplines=true\n");

		String query = "SELECT ?z WHERE { ?z lns:" + relation + " " + getConceptURIString(concept)
				+ "  .}";

		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);
		int counter = 0;
		while (result.hasNext()) {
			QueryRow row = result.next();

			String zURI = row.getValue("z").toString();
			String z = zURI.substring(zURI.indexOf("#") + 1);

			dotSource.append(prepareConceptLabel(concept)
					+ " [shape=hexagon, style=filled, fontsize=24]\n");

			appendEdge(concept, relation, dotSource, z, renderedEdges);

			appendDotSourceCloudForConcept(z, relation, dotSource, counter, renderedEdges);
			counter++;
		}

		dotSource.append("}");

		// create figure
		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only
		String realPath = servletContext.getRealPath("");
		String tmpPath = "KnowWEExtension/tmp/";
		String path = realPath + "/" + tmpPath;
		String dotFilename = path + concept.replaceAll(" ", "_").replaceAll("/", "_")
				+ section.getID()
					+ ".dot";
		File f = new File(dotFilename);
		try {
			FileWriter writer = new FileWriter(f);
			writer.append(dotSource);
			writer.flush();
			writer.close();

			String tpng = "-Tpdf";
			String o = "-o";

			// String command = dotFilename + " -Tpng -o" + path
			// + section.getID() + ".png";

			String outputfile = path
					+ concept.replaceAll("/", "_") + section.getID() + ".pdf";

			// TODO: read stdout and stderr to avoid process being blocked
			/* Process process = */Runtime.getRuntime().exec(new String[] {
					dotInstallation, dotFilename, tpng, o, outputfile });

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		string.append("<img src='" + tmpPath + "graph"
					+ section.getID() + ".png'/>");
	}

	/**
	 * 
	 * @created 22.06.2012
	 * @param concept
	 * @return
	 */
	private String getConceptURIString(String concept) {

		String fullConcept = "lns:" + concept;

		if (concept.contains(" ") || concept.contains("+") || concept.contains("%")
				|| concept.contains(".")) {
			concept = concept.replaceAll(" ", "+");
			concept = concept.replaceAll("/", "%2F");
			String expandNamespace = Rdf2GoUtils.expandNamespace("lns:");
			fullConcept = "<" + expandNamespace + concept + ">";
		}
		return fullConcept;
	}

	/**
	 * Adds the relations for this concept to the dot source. Further recursion
	 * is started along unterkonzept-relation
	 * 
	 * @created 21.08.2012
	 * @param concept
	 * @param relation
	 * @param dotSource
	 */
	private void appendDotSourceForConcept(String concept, String relation, StringBuffer dotSource, Set<String> renderedEdges) {

		String fullConcept = "<" + concept + ">";

		String query = "SELECT ?z WHERE { ?z lns:" + relation + " " + fullConcept + "  .}";

		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);
		while (result.hasNext()) {
			QueryRow row = result.next();

			String zURI = row.getValue("z").toString();
			String z = zURI.substring(zURI.indexOf("#") + 1);

			appendEdge(concept, relation, dotSource, z, renderedEdges);

			appendDotSourceForConcept(z, relation, dotSource, renderedEdges);

		}

		appendOtherRelationsStart(concept, dotSource, fullConcept, renderedEdges);

		appendOtherRelationsEnd(concept, dotSource, fullConcept, renderedEdges);
	}

	private void appendDotSourceCloudForConcept(String concept, String relation, StringBuffer dotSource, int cloudIndex, Set<String> renderedEdges) {

		dotSource.append("\n\nsubgraph cluster_" + cloudIndex + " {\n "
				+ prepareConceptLabel(concept)
				+ " [shape=box, style=filled, fontsize=20]\n");

		String fullConcept = "<" + concept + ">";

		String query = "SELECT ?z WHERE { ?z lns:" + relation + " " + fullConcept + "  .}";

		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);
		while (result.hasNext()) {
			QueryRow row = result.next();

			String zURI = row.getValue("z").toString();
			String z = zURI.substring(zURI.indexOf("#") + 1);

			appendEdge(concept, relation, dotSource, z, renderedEdges);

			appendDotSourceForConcept(z, relation, dotSource, renderedEdges);

		}

		appendOtherRelationsStart(concept, dotSource, fullConcept, renderedEdges);

		appendOtherRelationsEnd(concept, dotSource, fullConcept, renderedEdges);

		dotSource.append("\ncolor=white\nstyle=dashed");
		dotSource.append("\n}\n\n");
	}

	/**
	 * appends relations other than unterkonzept where the current concept is
	 * start of the triple
	 * 
	 * @created 21.08.2012
	 * @param concept
	 * @param dotSource
	 * @param fullConcept
	 */
	private void appendOtherRelationsEnd(String concept, StringBuffer dotSource, String fullConcept, Set<String> renderedEdges) {
		String query3 = "SELECT ?z ?rel WHERE { " + fullConcept + " ?rel ?z .}";

		ClosableIterator<QueryRow> result3 = Rdf2GoCore.getInstance().sparqlSelectIt(query3);
		while (result3.hasNext()) {
			QueryRow row = result3.next();

			String zURI = row.getValue("z").toString();
			String z = zURI.substring(zURI.indexOf("#") + 1);

			String relURI = row.getValue("rel").toString();
			String rel = relURI.substring(relURI.indexOf("#") + 1);

			if (!rel.endsWith("unterkonzept")) {
				appendEdge(z, rel, dotSource, concept, renderedEdges);
			}

			// appendDotSourceForConcept(z, relation, dotSource);

		}
	}

	/**
	 * appends relations other than unterkonzept where the current concept is
	 * end of the triple
	 * 
	 * @created 21.08.2012
	 * @param concept
	 * @param dotSource
	 * @param fullConcept
	 */
	private void appendOtherRelationsStart(String concept, StringBuffer dotSource, String fullConcept, Set<String> renderedEdges) {
		String query2 = "SELECT ?z ?rel WHERE { ?z ?rel " + fullConcept + "  .}";

		ClosableIterator<QueryRow> result2 = Rdf2GoCore.getInstance().sparqlSelectIt(query2);
		while (result2.hasNext()) {
			QueryRow row = result2.next();

			String zURI = row.getValue("z").toString();
			String z = zURI.substring(zURI.indexOf("#") + 1);

			String relURI = row.getValue("rel").toString();
			String rel = relURI.substring(relURI.indexOf("#") + 1);

			if (!rel.endsWith("unterkonzept")) {
				appendEdge(concept, rel, dotSource, z, renderedEdges);
			}

			// appendDotSourceForConcept(z, relation, dotSource);

		}
	}

	private String prepareConceptLabel(String z) {
		if (z.startsWith("http:")) {
			z = z.substring(z.indexOf("=") + 1);
		}
		try {
			z = URLDecoder.decode(z, "UTF-8").replaceAll(" ", "_");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		z = z.replaceAll("\\+", " ");
		z = z.replaceAll("_", " ");
		// z = KBBGraphvizUtils.cleanAndAddLineBreaks(z);
		z = z.replaceAll("<br/>", "\\\\n");
		// if (z.contains(" ") || z.contains("/") || z.contains("_")) {

		if (z.contains("/")) {
			if (z.length() > 25) {
				if (!z.contains("\\n")) {
					z = z.replaceAll("/", "/\\\\n");
				}
			}
		}

		if (z.endsWith("\\n")) {
			z = z.substring(0, z.length() - 2);
		}
		z = "\"" + z + "\"";
		// }
		return z;
	}

	private void appendEdge(String concept, String relation, StringBuffer dotSource, String z, Set<String> renderedEdges) {

		if (z.endsWith("Resource") || concept.endsWith("Resource")) return; // filter
																			// rdf:Resource



		String labelZ = prepareConceptLabel(z);
		String labelConcept = prepareConceptLabel(concept);
		String exp = labelZ + " -> "
				+ labelConcept;

		String relationLabel = relation.substring(relation.indexOf("=") + 1);
		if (!relation.endsWith("unterkonzept")) {
			exp += " [ label = \"" + relationLabel + "\" ";
			if (relationLabel.endsWith("Bidirektional")) {
				exp += " , dir=\"both\"";
			}
			if (relationLabel.startsWith("muss")) {
				exp += " , color=\"red\"";
			}
			if (relationLabel.startsWith("kann")) {
				exp += " , color=\"darkgreen\"";
			}
			if (relationLabel.startsWith("assoziation")) {
				exp += " , color=\"blue\"";
			}
			if (relationLabel.startsWith("temporalBevor")) {
				exp += " , color=\"goldenrod\"";
			}
			exp += "]";
		}
		exp += ";\n";

		String tripleSignature = labelZ + relationLabel + labelConcept;
		if (!renderedEdges.contains(tripleSignature)) {
			System.out.print(exp);
			dotSource.append(exp);
			renderedEdges.add(tripleSignature);
		}
	}

}

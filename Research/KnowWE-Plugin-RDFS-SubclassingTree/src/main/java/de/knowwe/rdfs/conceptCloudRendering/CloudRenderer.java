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

			appendEdge(concept, relation, dotSource, z);

			appendDotSourceCloudForConcept(z, relation, dotSource, counter);
			counter++;
		}

		dotSource.append("}");

		// create figure
		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only
		String realPath = servletContext.getRealPath("");
		String tmpPath = "KnowWEExtension/tmp/";
		String path = realPath + "/" + tmpPath;
		String dotFilename = path + concept.replaceAll(" ", "_") + section.getID()
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
					+ concept + section.getID() + ".pdf";

			Process process = Runtime.getRuntime().exec(new String[] {
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
			String expandNamespace = Rdf2GoUtils.expandNamespace("lns:");
			fullConcept = "<" + expandNamespace + concept + ">";
		}
		return fullConcept;
	}

	private void appendDotSourceForConcept(String concept, String relation, StringBuffer dotSource) {

		String fullConcept = getConceptURIString(concept);

		String query = "SELECT ?z WHERE { ?z lns:" + relation + " " + fullConcept + "  .}";

		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);
		while (result.hasNext()) {
			QueryRow row = result.next();

			String zURI = row.getValue("z").toString();
			String z = zURI.substring(zURI.indexOf("#") + 1);

			appendEdge(concept, relation, dotSource, z);

			appendDotSourceForConcept(z, relation, dotSource);

		}
	}

	private void appendDotSourceCloudForConcept(String concept, String relation, StringBuffer dotSource, int cloudIndex) {

		dotSource.append("\n\nsubgraph cluster_" + cloudIndex + " {\n "
				+ prepareConceptLabel(concept)
				+ " [shape=box, style=filled, fontsize=20]\n");

		String fullConcept = getConceptURIString(concept);

		String query = "SELECT ?z WHERE { ?z lns:" + relation + " " + fullConcept + "  .}";

		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);
		while (result.hasNext()) {
			QueryRow row = result.next();

			String zURI = row.getValue("z").toString();
			String z = zURI.substring(zURI.indexOf("#") + 1);

			appendEdge(concept, relation, dotSource, z);

			appendDotSourceForConcept(z, relation, dotSource);

		}
		dotSource.append("\ncolor=white\nstyle=dashed");
		dotSource.append("\n}\n\n");
	}

	private String prepareConceptLabel(String z) {
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

		z = "\"" + z + "\"";
		// }
		return z;
	}

	private void appendEdge(String concept, String relation, StringBuffer dotSource, String z) {

		String exp = prepareConceptLabel(z) + "->"
				+ prepareConceptLabel(concept);

		// exp += ("[ label = " + relation + " ]");
		exp += ";\n";

		System.out.print(exp);
		dotSource.append(exp);
	}

}

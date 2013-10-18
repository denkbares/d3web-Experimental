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
package de.knowwe.wisskont.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.wisskont.ConceptMarkup;
import de.knowwe.wisskont.SubconceptMarkup;
import de.knowwe.wisskont.ValuesMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 27.11.2012
 */
public class MarkupUtils {

	/**
	 * Finds the concept definition of the local page iff there is exactly _one_
	 * on this page.
	 * 
	 * @created 28.11.2012
	 * @param section
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Section<IncrementalTermDefinition> getConceptDefinition(Section<?> section) {
		List<Section<ConceptMarkup>> conceptDefinitionMarkupSections = getConecptDefinitionForLocalPage(section);
		if (conceptDefinitionMarkupSections.size() == 1) {
			Section<ConceptMarkup> defSection = conceptDefinitionMarkupSections.get(0);
			Section<IncrementalTermDefinition> termSec = Sections.findSuccessor(
					defSection,
					IncrementalTermDefinition.class);
			return termSec;
		}
		return null;
	}

	public static Section<? extends SimpleDefinition> getConceptDefinitionGlobal(Section<? extends Term> section) {
		ReferenceManager terminology = IncrementalCompiler.getInstance().getTerminology();
		Collection<Section<? extends SimpleDefinition>> termDefinitions = terminology.getTermDefinitions(section);
		if (termDefinitions.size() == 1) {
			return termDefinitions.iterator().next();
		}
		return null;
	}

	/**
	 * 
	 * @created 22.05.2013
	 * @param testObject
	 * @param concept
	 * @return
	 */
	public static List<Identifier> getParents(Rdf2GoCore testObject, URI concept) {
		String parentQuery = "SELECT ?x WHERE { <" + concept.toString()
				+ "> lns:unterkonzept ?x.}";
		QueryResultTable resultTable = testObject.sparqlSelect(parentQuery);
		ClosableIterator<QueryRow> resultIterator = resultTable.iterator();

		List<Identifier> parents = new ArrayList<Identifier>();
		while (resultIterator.hasNext()) {
			QueryRow parentConceptResult = resultIterator.next();
			Node value = parentConceptResult.getValue("x");
			URI parent = value.asURI();
			parents.add(MarkupUtils.getConceptName(parent));

		}

		return parents;
	}

	/**
	 * 
	 * @created 22.05.2013
	 * @param testObject
	 * @param concept
	 * @return
	 */
	public static List<Identifier> getChildren(Rdf2GoCore testObject, URI concept) {
		String parentQuery = "SELECT ?x WHERE { ?x lns:" + SubconceptMarkup.SUBCONCEPT_PROPERTY
				+ " <" + concept.toString()
				+ ">.}";
		QueryResultTable resultTable = testObject.sparqlSelect(parentQuery);
		ClosableIterator<QueryRow> resultIterator = resultTable.iterator();

		List<Identifier> parents = new ArrayList<Identifier>();
		while (resultIterator.hasNext()) {
			QueryRow parentConceptResult = resultIterator.next();
			Node value = parentConceptResult.getValue("x");
			URI parent = value.asURI();
			parents.add(MarkupUtils.getConceptName(parent));

		}

		return parents;
	}

	public static List<Identifier> getParentConcepts(Identifier term) {
		List<Identifier> result = new ArrayList<Identifier>();

		Collection<Section<? extends SimpleDefinition>> defs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				term);

		if (defs.size() > 0) {
			Section<? extends SimpleDefinition> def = defs.iterator().next();
			URI uri = RDFSUtil.getURI(def);

			List<String> sparqls = new ArrayList<String>();
			String sparqlSubconcept = "SELECT ?x WHERE { <" + uri + "> lns:"
					+ SubconceptMarkup.SUBCONCEPT_PROPERTY + " ?x.}";
			sparqls.add(sparqlSubconcept);
			String sparqlValue = "SELECT ?x WHERE { ?x lns:"
					+ ValuesMarkup.VALUE_PROPERTY + "<" + uri + "> .}";
			sparqls.add(sparqlValue);
			for (String query : sparqls) {

				QueryResultTable resultTable = Rdf2GoCore.getInstance().sparqlSelect(query);

				ClosableIterator<QueryRow> resultIterator = resultTable.iterator();
				while (resultIterator.hasNext()) {
					QueryRow parentConceptResult = resultIterator.next();
					Node value = parentConceptResult.getValue("x");
					String urlString = value.asURI().toString();

					String termName = "";
					try {
						termName = URLDecoder.decode(
								urlString.substring(Rdf2GoCore.getInstance().getLocalNamespace().length()),
								"UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.add(new Identifier(termName));
				}
			}
		}
		return result;
	}

	public static List<Identifier> getChildrenConcepts(Identifier term) {
		List<Identifier> result = new ArrayList<Identifier>();

		Collection<Section<? extends SimpleDefinition>> defs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				term);

		if (defs.size() > 0) {
			Section<? extends SimpleDefinition> def = defs.iterator().next();
			URI uri = RDFSUtil.getURI(def);

			List<String> sparqls = new ArrayList<String>();
			String sparqlSuconcept = "SELECT ?x WHERE { ?x lns:unterkonzept <" + uri + ">.}";
			sparqls.add(sparqlSuconcept);
			String sparqlValue = "SELECT ?x WHERE { <" + uri + "> lns:"
					+ ValuesMarkup.VALUE_PROPERTY + " ?x .}";
			sparqls.add(sparqlValue);
			for (String query : sparqls) {

				QueryResultTable resultTable = Rdf2GoCore.getInstance().sparqlSelect(query);

				ClosableIterator<QueryRow> resultIterator = resultTable.iterator();
				while (resultIterator.hasNext()) {
					QueryRow parentConceptResult = resultIterator.next();
					Node value = parentConceptResult.getValue("x");
					String urlString = value.asURI().toString();

					String termName = "";
					try {
						termName = URLDecoder.decode(
								urlString.substring(Rdf2GoCore.getInstance().getLocalNamespace().length()),
								"UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.add(new Identifier(termName));
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @created 28.11.2012
	 * @param section
	 * @return
	 */
	public static List<Section<ConceptMarkup>> getConecptDefinitionForLocalPage(Section<?> section) {
		Section<RootType> rootSection = Sections.findAncestorOfType(section, RootType.class);
		List<Section<ConceptMarkup>> conceptDefinitionMarkupSections = Sections.findSuccessorsOfType(
				rootSection, ConceptMarkup.class);
		return conceptDefinitionMarkupSections;
	}

	public static Identifier getConceptName(Node value) {
		String uriString = value.toString();
		String uriStringDecoded = null;
		try {
			uriStringDecoded = URLDecoder.decode(uriString, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String conceptName = uriStringDecoded.substring(uriStringDecoded.indexOf("=") + 1);
		return new Identifier(conceptName);
	}

}

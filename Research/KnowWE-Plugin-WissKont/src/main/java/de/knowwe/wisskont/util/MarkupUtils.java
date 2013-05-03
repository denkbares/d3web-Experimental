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
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.wisskont.ConceptMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 27.11.2012
 */
public class MarkupUtils {

	/**
	 * Finds the concept definition iff there is exactly _one_ on this page.
	 * 
	 * @created 28.11.2012
	 * @param section
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Section<IncrementalTermDefinition> getConceptDefinition(Section<?> section) {
		List<Section<ConceptMarkup>> conceptDefinitionMarkupSections = getConecptDefinitions(section);
		if (conceptDefinitionMarkupSections.size() == 1) {
			Section<ConceptMarkup> defSection = conceptDefinitionMarkupSections.get(0);
			Section<IncrementalTermDefinition> termSec = Sections.findSuccessor(
					defSection,
					IncrementalTermDefinition.class);
			return termSec;
		}
		return null;
	}

	public static boolean isDirectSubConceptOf(URI concept1, URI concept2) {
		String sparql = "ASK { <" + concept1 + "> lns:unterkonzept <" + concept2 + ">.}";
		boolean result = Rdf2GoCore.getInstance().sparqlAsk(sparql);
		return result;
	}

	/**
	 * Determines whether concept1 is a sub-concept of the second concept
	 * (recursively), i.e., the sub-concept relation is followed transitively.
	 * 
	 * @created 12.04.2013
	 * @param concept1
	 * @param target
	 * @return
	 */
	public static boolean isSubConceptOf(URI concept1, URI target) {
		String sparql = "SELECT ?x WHERE { <" + concept1 + "> lns:unterkonzept ?x.}";
		QueryResultTable resultTable = Rdf2GoCore.getInstance().sparqlSelect(sparql);

		ClosableIterator<QueryRow> resultIterator = resultTable.iterator();
		if (!resultIterator.hasNext()) {
			return false;
		}
		while (resultIterator.hasNext()) {
			QueryRow parentConceptResult = resultIterator.next();
			Node value = parentConceptResult.getValue("x");
			URI parent = value.asURI();
			if (parent.equals(target)) {
				return true;
			}
			else {
				return isSubConceptOf(parent, target);
			}
		}
		return false;
	}

	/**
	 * 
	 * @created 28.11.2012
	 * @param section
	 * @return
	 */
	public static List<Section<ConceptMarkup>> getConecptDefinitions(Section<?> section) {
		Section<RootType> rootSection = Sections.findAncestorOfType(section, RootType.class);
		List<Section<ConceptMarkup>> conceptDefinitionMarkupSections = Sections.findSuccessorsOfType(
				rootSection, ConceptMarkup.class);
		return conceptDefinitionMarkupSections;
	}

	public static String getConceptName(Node value) {
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
		return conceptName;
	}

}

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
package de.knowwe.rdfs.wikiObjectModel.types;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.jspwiki.types.HeaderType;
import de.knowwe.jspwiki.types.SectionContentType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.SectionIDSource;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.rdfs.AbstractKnowledgeUnitCompileScriptRDFS;
import de.knowwe.rdfs.wikiObjectModel.Utils;
import de.knowwe.rdfs.wikiObjectModel.WikiObjectModel;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 06.07.2012
 */
public class WikiSectionCompiler<T extends Type> extends AbstractKnowledgeUnitCompileScriptRDFS<T> {

	@Override
	public void insertIntoRepository(Section<T> section) {

		List<Statement> data = new ArrayList<Statement>();

		String title = section.getTitle();
		try {
			title = URLEncoder.encode(title, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String pageURL = Environment.getInstance().getWikiConnector().getBaseUrl()
				+ KnowWEUtils.getURLLink(title);

		URI pageURI = new URIImpl(pageURL);

		data.add(Rdf2GoCore.getInstance().createStatement(pageURI, RDF.type,
				WikiObjectModel.WIKI_PAGE));

		Section<HeaderType> theMajorSection = Sections.successor(section, HeaderType.class);

		URI sectionURI = createTriplesForSection(data, pageURI, theMajorSection, null);

		List<Section<HeaderType>> mediumLevelSections = new ArrayList<Section<HeaderType>>();

		Sections.successors(section, HeaderType.class, 2, mediumLevelSections);
		mediumLevelSections.remove(section); // without itself

		for (Section<HeaderType> medium : mediumLevelSections) {
			URI mediumURI = createTriplesForSection(data, pageURI, medium, sectionURI);

			List<Section<HeaderType>> minorLevelSections = new ArrayList<Section<HeaderType>>();
			Sections.successors(medium, HeaderType.class, 2, minorLevelSections);
			minorLevelSections.remove(medium); // without itself

			for (Section<HeaderType> minor : minorLevelSections) {
				createTriplesForSection(data, pageURI, minor, mediumURI);
			}
		}

		Rdf2GoCore.getInstance().addStatements(new SectionIDSource(section), Rdf2GoUtils.toArray(data));
	}

	private URI createTriplesForSection(List<Statement> data, URI pageURI, Section<HeaderType> currentSection, URI parentSectionURI) {

		URI currentSectionURI = new URIImpl(Utils.createAnchorURL(currentSection));

		// is-a wiki section
		data.add(Rdf2GoCore.getInstance().createStatement(currentSectionURI, RDF.type,
				WikiObjectModel.WIKI_SECTION));

		// hasPage the page
		data.add(Rdf2GoCore.getInstance().createStatement(currentSectionURI,
				WikiObjectModel.HAS_PAGE,
				pageURI));

		// hasKDOMID
		data.add(Rdf2GoCore.getInstance().createStatement(currentSectionURI,
				WikiObjectModel.HAS_KDOM_ID,
				new URIImpl(Utils.createKDOMIDURI(currentSection))));

		// hasContentKDOMID
		Section<SectionContentType> content = Sections.successor(currentSection,
				SectionContentType.class);
		if (content != null) {
			data.add(Rdf2GoCore.getInstance().createStatement(currentSectionURI,
					WikiObjectModel.HAS_CONTENT_KDOM_ID,
					new URIImpl(Utils.createKDOMIDURI(content))));
		}
		// hierarchical relation
		if (parentSectionURI != null) {
			data.add(Rdf2GoCore.getInstance().createStatement(parentSectionURI,
					WikiObjectModel.HAS_SUBSECTION,
					currentSectionURI));
		}

		return currentSectionURI;
	}
}

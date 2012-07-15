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
package de.knowwe.rdfs.wikiObjectModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.plugin.Instantiation;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
 * A simple Wiki Object Model that can be used to describing the resources in
 * the wiki and their interrelations, possibly to relate them to concepts of a
 * domain ontology.
 * 
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 06.07.2012
 */
public class WikiObjectModel implements Instantiation {

	public static URI WIKI_CONTENT_RESOURCE = null;
	public static URI WIKI_PAGE = null;
	public static URI WIKI_ATTACHEMENT = null;
	public static URI WIKI_SECTION = null;
	public static URI WIKI_IMAGE = null;
	public static URI KDOM_ID = null;

	public static URI HAS_PAGE = null;
	public static URI HAS_KDOM_ID = null;
	public static URI HAS_CONTENT_KDOM_ID = null;
	public static URI HAS_SUBSECTION = null;

	public static URI SUBSECTION_OF = null;
	public static URI DESCRIBES = null;
	public static URI DESCRIBED_BY = null;
	public static URI DESCRIBES_ASPECT_OF = null;
	public static URI ILLUSTRATES = null;

	static {
		String bns = Rdf2GoCore.getInstance().getBaseNamespace();
		WIKI_CONTENT_RESOURCE = new URIImpl(bns + "WikiContentResource");
		KDOM_ID = new URIImpl(bns + "KDOM_ID");
		WIKI_PAGE = new URIImpl(bns + "WikiPage");
		WIKI_IMAGE = new URIImpl(bns + "Image");
		WIKI_ATTACHEMENT = new URIImpl(bns + "WikiAttachement");
		WIKI_SECTION = new URIImpl(bns + "WikiSection");

		HAS_PAGE = new URIImpl(bns + "hasPage");
		HAS_KDOM_ID = new URIImpl(bns + "hasKDOMID");
		HAS_CONTENT_KDOM_ID = new URIImpl(bns + "hasContentKDOMID");
		HAS_SUBSECTION = new URIImpl(bns + "hasSubsection");

		SUBSECTION_OF = new URIImpl(bns + "subsectionOf");
		DESCRIBES = new URIImpl(bns + "describes");
		DESCRIBED_BY = new URIImpl(bns + "describedBy");
		DESCRIBES_ASPECT_OF = new URIImpl(bns + "describesAspectOf");
		ILLUSTRATES = new URIImpl(bns + "illustrates");
	}

	public static Collection<Statement> getWikiObjectModelData() {
		List<Statement> modelData = new ArrayList<Statement>();

		// WikiContentResource is a RDFS-Class
		modelData.add(Rdf2GoCore.getInstance().createStatement(WIKI_CONTENT_RESOURCE, RDF.type,
				RDFS.Class));

		// KDOMID is a RDFS-Class
		modelData.add(Rdf2GoCore.getInstance().createStatement(KDOM_ID, RDF.type,
				RDFS.Class));

		/*
		 * WikiPage, WikiAttachement and WikiSection are Subclasses of
		 * WikiContentResource
		 */
		modelData.add(Rdf2GoCore.getInstance().createStatement(WIKI_PAGE, RDFS.subClassOf,
				WIKI_CONTENT_RESOURCE));
		modelData.add(Rdf2GoCore.getInstance().createStatement(WIKI_SECTION, RDFS.subClassOf,
				WIKI_CONTENT_RESOURCE));
		modelData.add(Rdf2GoCore.getInstance().createStatement(WIKI_ATTACHEMENT, RDFS.subClassOf,
				WIKI_CONTENT_RESOURCE));
		modelData.add(Rdf2GoCore.getInstance().createStatement(WIKI_IMAGE, RDFS.subClassOf,
				WIKI_CONTENT_RESOURCE));

		// hasPage property
		modelData.add(Rdf2GoCore.getInstance().createStatement(HAS_PAGE, RDFS.domain,
				WIKI_CONTENT_RESOURCE));
		modelData.add(Rdf2GoCore.getInstance().createStatement(HAS_PAGE, RDFS.range,
				WIKI_PAGE));

		// hasSubsection property
		modelData.add(Rdf2GoCore.getInstance().createStatement(HAS_SUBSECTION, RDFS.domain,
				WIKI_SECTION));
		modelData.add(Rdf2GoCore.getInstance().createStatement(HAS_SUBSECTION, RDFS.range,
				WIKI_SECTION));

		// hasKDOMID property
		modelData.add(Rdf2GoCore.getInstance().createStatement(HAS_KDOM_ID, RDFS.domain,
				WIKI_SECTION));
		// TODO: do WikiPages also have to have KDOMIDs?
		modelData.add(Rdf2GoCore.getInstance().createStatement(HAS_KDOM_ID, RDFS.range,
				KDOM_ID));

		// hasContentKDOMID property
		modelData.add(Rdf2GoCore.getInstance().createStatement(HAS_CONTENT_KDOM_ID, RDFS.domain,
				WIKI_SECTION));
		modelData.add(Rdf2GoCore.getInstance().createStatement(HAS_CONTENT_KDOM_ID, RDFS.range,
				KDOM_ID));

		// hasSubsection inverseOf subsectionOf
		modelData.add(Rdf2GoCore.getInstance().createStatement(HAS_SUBSECTION, OWL.inverseOf,
				SUBSECTION_OF));

		// describes inverseOf describedBy
		modelData.add(Rdf2GoCore.getInstance().createStatement(DESCRIBED_BY, OWL.inverseOf,
				DESCRIBES));

		// subsectionOf subpropertyOF describesAspectOf
		modelData.add(Rdf2GoCore.getInstance().createStatement(SUBSECTION_OF, RDFS.subPropertyOf,
				DESCRIBES_ASPECT_OF));

		// illustrates subpropertyOF describes
		modelData.add(Rdf2GoCore.getInstance().createStatement(ILLUSTRATES, RDFS.subPropertyOf,
				DESCRIBES));

		// describes subpropertyOF describesAspectOf
		modelData.add(Rdf2GoCore.getInstance().createStatement(DESCRIBES, RDFS.subPropertyOf,
				DESCRIBES_ASPECT_OF));

		// describes is transitive
		modelData.add(Rdf2GoCore.getInstance().createStatement(DESCRIBES, RDF.type,
				OWL.TransitiveProperty));

		// describesAspectOf is transitive
		modelData.add(Rdf2GoCore.getInstance().createStatement(DESCRIBES_ASPECT_OF, RDF.type,
				OWL.TransitiveProperty));

		return modelData;
	}

	@Override
	public void init() {
		Rdf2GoCore.getInstance().addStatements(Rdf2GoUtils.toArray(getWikiObjectModelData()));

	}
}

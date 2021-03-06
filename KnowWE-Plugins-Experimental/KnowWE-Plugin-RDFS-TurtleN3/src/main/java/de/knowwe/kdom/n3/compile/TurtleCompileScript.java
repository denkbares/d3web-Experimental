/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.knowwe.kdom.n3.compile;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.n3.TurtleMarkupN3;
import de.knowwe.kdom.n3.TurtleMarkupN3Content;
import de.knowwe.kdom.n3.TurtleObjectBlankNode;
import de.knowwe.kdom.n3.TurtleObjectSection;
import de.knowwe.kdom.n3.TurtleObjectTerm;
import de.knowwe.kdom.n3.TurtlePredicate;
import de.knowwe.kdom.n3.TurtleSubject;
import de.knowwe.rdf2go.BlankNodeImpl;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.SectionIDSource;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.rdfs.AbstractKnowledgeUnitCompileScriptRDFS;
import de.knowwe.rdfs.literal.TurtleObjectLiteral;
import de.knowwe.rdfs.util.RDFSUtil;

public class TurtleCompileScript extends AbstractKnowledgeUnitCompileScriptRDFS<TurtleMarkupN3> {

	@Override
	public void insertIntoRepository(Section<TurtleMarkupN3> section) {
		List<Section<TurtleObjectSection>> found = new ArrayList<Section<TurtleObjectSection>>();

		Sections.successors(section, TurtleObjectSection.class, found);

		List<Statement> triples = new ArrayList<Statement>();

		for (Section<TurtleObjectSection> objectSec : found) {

			createTriplesForObject(triples, objectSec);

		}
		Rdf2GoCore.getInstance().addStatements(new SectionIDSource(section), Rdf2GoUtils.toArray(triples));
	}

	private void createTriplesForObject(List<Statement> triples, Section<TurtleObjectSection> objectSec) {

		// Section<TurtleObjectBlankNode> blankNodeSection =
		// Sections.findSuccessor(
		// objectSec, TurtleObjectBlankNode.class);
		// if (blankNodeSection != null) {
		// Section<TurtleMarkupN3Content> blankTurtleContent =
		// Sections.findSuccessor(
		// blankNodeSection, TurtleMarkupN3Content.class);
		// List<Section<TurtleObjectSection>> found = new
		// ArrayList<Section<TurtleObjectSection>>();
		//
		// Sections.successors(blankTurtleContent,
		// TurtleObjectSection.class,
		// found);
		// for (Section<TurtleObjectSection> internalObSec : found) {
		// createTriplesForObject(triples, internalObSec);
		// }
		//
		// }

		Section<TurtleObjectLiteral> literalSec = Sections.successor(
				objectSec, TurtleObjectLiteral.class);

		Node objURI = null;

		Section<TurtleObjectBlankNode> bn = Sections.child(objectSec,
				TurtleObjectBlankNode.class);
		if (bn != null) {
			Section<TurtleMarkupN3Content> turtleInner = Sections.successor(bn,
					TurtleMarkupN3Content.class);
			objURI = new BlankNodeImpl(turtleInner);

		}
		else {
			Section<TurtleObjectTerm> termSec = Sections.successor(objectSec,
					TurtleObjectTerm.class);
			if (termSec != null) {
				objURI = RDFSUtil.getURI(termSec);
			}
			if (literalSec != null) {
				objURI = RDFSUtil.createLiteral(literalSec);
			}
		}

		Section<TurtlePredicate> predSec = Sections.successor(
				objectSec.getParent(), TurtlePredicate.class);

		URI predURI = RDFSUtil.getURI(predSec);

		Section<TurtleSubject> subjectSec = Sections.successor(
				objectSec.getParent().getParent(), TurtleSubject.class);
		Resource subjectURI = RDFSUtil.getURI(subjectSec);
		if (subjectURI == null) {
			subjectURI = new BlankNodeImpl(Sections.successor(
					objectSec.getParent().getParent().getParent(),
					TurtleMarkupN3Content.class));
		}

		if (objURI != null && predURI != null && subjectURI != null) {
			Statement triple = Rdf2GoCore.getInstance().createStatement(
					subjectURI, predURI, objURI);

			triples.add(triple);
		}
	}

}

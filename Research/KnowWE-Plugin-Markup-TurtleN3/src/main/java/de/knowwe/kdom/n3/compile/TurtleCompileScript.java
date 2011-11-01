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
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.n3.TurtleMarkupN3;
import de.knowwe.kdom.n3.TurtleObjectLiteral;
import de.knowwe.kdom.n3.TurtleObjectLiteralText;
import de.knowwe.kdom.n3.TurtleObjectSection;
import de.knowwe.kdom.n3.TurtleObjectTerm;
import de.knowwe.kdom.n3.TurtlePredicate;
import de.knowwe.kdom.n3.TurtleSubject;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.rdfs.util.RDFSUtil;

public class TurtleCompileScript implements KnowledgeUnitCompileScript<TurtleMarkupN3> {

	@Override
	public void insertIntoRepository(Section<TurtleMarkupN3> section) {
		List<Section<TurtleObjectSection>> found = new ArrayList<Section<TurtleObjectSection>>();

		Sections.findSuccessorsOfType(section, TurtleObjectSection.class, found);

		List<Statement> triples = new ArrayList<Statement>();

		for (Section<TurtleObjectSection> objectSec : found) {

			Section<TurtleObjectTerm> termSec = Sections.findSuccessor(
					objectSec, TurtleObjectTerm.class);
			
			Section<TurtleObjectLiteralText> literalSec = Sections.findSuccessor(
					objectSec, TurtleObjectLiteralText.class);

			Node objURI = null;
			if (termSec != null) {
				objURI = RDFSUtil.getURI(termSec);
			}
			if(literalSec != null) {
				objURI = Rdf2GoCore.getInstance().createLiteral(literalSec.getOriginalText());
			}
			Section<TurtlePredicate> predSec = Sections.findSuccessor(
					objectSec.getFather(), TurtlePredicate.class);

			URI predURI = RDFSUtil.getURI(predSec);

			Section<TurtleSubject> subjectSec = Sections.findSuccessor(
					objectSec.getFather().getFather(), TurtleSubject.class);
			URI subjectURI = RDFSUtil.getURI(subjectSec);

			if (objURI != null && predURI != null && subjectURI != null) {
				Statement triple = Rdf2GoCore.getInstance().createStatement(
						subjectURI, predURI, objURI);

				triples.add(triple);
			}

		}
		Rdf2GoCore.getInstance().addStatements(triples, section);
	}
	
	@Override
	public void deleteFromRepository(Section<TurtleMarkupN3> section) {
		Rdf2GoCore.getInstance().removeSectionStatementsRecursive(section);
	}

	@Override
	public Collection<Section<TermReference>> getAllReferencesOfKnowledgeUnit(
			Section<? extends KnowledgeUnit<TurtleMarkupN3>> section) {
		return CompileUtils.getAllReferencesOfCompilationUnit(section);
	}


	

}

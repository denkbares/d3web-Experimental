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
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.knowwe.kdom.n3.TurtleObject;
import de.knowwe.kdom.n3.TurtlePredicate;
import de.knowwe.kdom.n3.TurtleSubject;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.rdfs.util.RDFSUtil;

public class TurtleCompiler {

	public static void insertTriples(Section<de.knowwe.kdom.n3.TurtleMarkupN3> section) {
		List<Section<TurtleObject>> found = new ArrayList<Section<TurtleObject>>();

		Sections.findSuccessorsOfType(section, TurtleObject.class, found);

		List<Statement> triples = new ArrayList<Statement>();

		for (Section<TurtleObject> objectSec : found) {

			URI objURI = RDFSUtil.getURI(objectSec);

			Section<TurtlePredicate> predSec = Sections.findSuccessor(
					objectSec.getFather(), TurtlePredicate.class);

			URI predURI = RDFSUtil.getURI(predSec);

			Section<TurtleSubject> subjectSec = Sections.findSuccessor(
					objectSec.getFather().getFather(), TurtleSubject.class);
			URI subjectURI = RDFSUtil.getURI(subjectSec);

			if (objURI != null && predURI != null && subjectURI != null) {
				Statement triple = Rdf2GoCore.getInstance().createStatement(subjectURI,
						predURI, objURI);

				triples.add(triple);
			}

		}
		Rdf2GoCore.getInstance().addStatements(triples, section);
	}
	

	public static void removeTriples(Section<de.knowwe.kdom.n3.TurtleMarkupN3> s) {
		Rdf2GoCore.getInstance().removeSectionStatementsRecursive(s);
	}

}

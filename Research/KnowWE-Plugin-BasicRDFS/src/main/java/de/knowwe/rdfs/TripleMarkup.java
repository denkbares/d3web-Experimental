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
package de.knowwe.rdfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;

public class TripleMarkup extends AbstractType implements KnowledgeUnit<TripleMarkup> {

	public TripleMarkup() {

		
		this.setSectionFinder(new RegexSectionFinder("^>(.*?::.*?)$", Pattern.DOTALL| Pattern.MULTILINE,
				1));
		this.addChildType(new SimpleTurtlePredicate());
		this.addChildType(new SimpleTurtleSubject());
		this.addChildType(new SimpleTurtleObject());
	}

	class SimpleTurtlePredicate extends IRITermRef {
		public SimpleTurtlePredicate() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new RegexSectionFinder("\\b([^\\s]*)::", Pattern.DOTALL, 1));
			c.addConstraint(SingleChildConstraint.getInstance());
			c.addConstraint(AtMostOneFindingConstraint.getInstance());
			this.setSectionFinder(c);
		}
	}

	class SimpleTurtleSubject extends IRITermRef{
		public SimpleTurtleSubject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			c.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(c);
		}

	}

	class SimpleTurtleObject extends IRITermRef{
		public SimpleTurtleObject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new RegexSectionFinder("::\\s(.*)", Pattern.DOTALL, 1));
			c.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(c);
		}
	}

	@Override
	public void deleteFromRepository(Section<TripleMarkup> section) {
		Rdf2GoCore.getInstance().removeSectionStatementsRecursive(section);
	}
	
	

	@Override
	public void insertIntoRepository(Section<TripleMarkup> section) {

		List<Section<IRITermRef>> found = new ArrayList<Section<IRITermRef>>();
		Node subURI = null;
		Node predURI = null;
		Node objURI = null;

		Sections.findSuccessorsOfType(section, IRITermRef.class, found);

		if (found.size() == 3) {
			Section<IRITermRef> subject = found.get(0);
			Section<IRITermRef> predicate = found.get(1);
			Section<IRITermRef> object = found.get(2);

			subURI = RDFSUtil.getURI(subject);
			predURI = RDFSUtil.getURI(predicate);
			objURI = RDFSUtil.getURI(object);
		}
		else {
			// return Arrays.asList((KDOMReportMessage) new SyntaxError(
			// "invalid term combination:" + found.size()));
		}
		if (subURI == null) {
			// return Arrays.asList((KDOMReportMessage) new SyntaxError(
			// "subject URI not found"));
		}
		if (predURI == null) {
			// return Arrays.asList((KDOMReportMessage) new SyntaxError(
			// "predicate URI not found"));
		}
		if (objURI == null) {
			// return Arrays.asList((KDOMReportMessage) new SyntaxError(
			// "object URI not found"));
		}

		Rdf2GoCore.getInstance().addStatement(subURI.asResource(),
				predURI.asURI(), objURI, section);

		// return new ArrayList<KDOMReportMessage>(0);

	}

	@Override
	public Collection<Section<TermReference>> getAllReferencesOfKnowledgeUnit(
			Section<? extends KnowledgeUnit<TripleMarkup>> section) {
		return CompileUtils.getAllReferencesOfCompilationUnit(section);
	}
}

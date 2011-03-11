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
package de.knowwe.kdom.classHierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.d3web.we.core.semantic.rdf2go.RDF2GoSubtreeHandler;
import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.NoSuchObjectError;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.subtreeHandler.IncrementalConstraint;
import de.knowwe.core.dashtree.DashSubtree;
import de.knowwe.core.dashtree.DashTreeElement;
import de.knowwe.core.dashtree.DashTreeUtils;
import de.knowwe.termObject.OWLTermReference;

public class SubClassingDashTreeElement extends DashTreeElement implements
		IncrementalConstraint<SubClassingDashTreeElement> {

	public SubClassingDashTreeElement() {
		this.addSubtreeHandler(new SubClassingDashTreeElementOWLSubTreeHandler());
		OWLTermReference ref = new OWLTermReference();
		ref.setSectionFinder(new AllTextFinderTrimmed());
		this.setDashTreeElementContent(ref);
	}

	@Override
	public boolean violatedConstraints(KnowWEArticle article, Section<SubClassingDashTreeElement> s) {
		Section<? extends DashSubtree> fatherDashSubtree = DashTreeUtils.getFatherDashSubtree(s);
		if (fatherDashSubtree == null) return false; // root of dashTree
		boolean changeInSubtree = DashTreeUtils.isChangeInSubtree(article,
				fatherDashSubtree, new ArrayList(
						0));
		return changeInSubtree;
	}

	private class SubClassingDashTreeElementOWLSubTreeHandler extends
			RDF2GoSubtreeHandler<SubClassingDashTreeElement> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<SubClassingDashTreeElement> element) {
			if (element.get().isAssignableFromType(DashTreeElement.class)) {
				Section<? extends DashTreeElement> father = DashTreeUtils
						.getFatherDashTreeElement(element);
				if (father != null) {
					Section<? extends OWLTermReference> fatherElement = Sections
							.findChildOfType(father, OWLTermReference.class);
					Section<? extends OWLTermReference> childElement = Sections
							.findChildOfType(element, OWLTermReference
									.class);
					URI localURI = childElement.get().getNode(childElement);
					URI fatherURI = fatherElement.get().getNode(fatherElement);
					if (localURI == null || fatherURI == null) {
						return Arrays.asList((KDOMReportMessage) new NoSuchObjectError(
								element.getOriginalText()));
					}
					List<Statement> statements = new ArrayList<Statement>();
					Statement st = Rdf2GoCore.getInstance().createStatement(localURI,
								RDFS.subClassOf, fatherURI);
					statements.add(st);
					Rdf2GoCore.getInstance().addStatements(statements, element);
				}
			}

			return new ArrayList<KDOMReportMessage>(0);
		}

	}

}

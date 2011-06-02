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

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.ObjectNotFoundWarning;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.subtreeHandler.IncrementalConstraint;
import de.knowwe.core.dashtree.DashTreeElement;
import de.knowwe.core.dashtree.DashTreeElementContent;
import de.knowwe.core.dashtree.DashTreeUtils;
import de.knowwe.rdf2go.RDF2GoSubtreeHandler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.termObject.IRITermReference;

public class SubClassingDashTreeElementContent extends DashTreeElementContent implements
		IncrementalConstraint<SubClassingDashTreeElementContent> {

	public SubClassingDashTreeElementContent() {
		this.addSubtreeHandler(new SubClassingDashTreeElementOWLSubTreeHandler());
		IRITermReference ref = new IRITermReference();
		ref.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(ref);
	}

	@Override
	public boolean violatedConstraints(KnowWEArticle article, Section<SubClassingDashTreeElementContent> s) {
		Section<? extends DashTreeElement> fatherDashTreeElement = DashTreeUtils.getFatherDashTreeElement(s.getFather());
		if (fatherDashTreeElement == null) return false; // root of dashTree
		return fatherDashTreeElement.isOrHasChangedSuccessor(s.getArticle().getTitle(),
				null);
	}

	private class SubClassingDashTreeElementOWLSubTreeHandler extends
			RDF2GoSubtreeHandler<SubClassingDashTreeElementContent> {
		
		@Override
		public void destroy(KnowWEArticle article, Section<SubClassingDashTreeElementContent> s) {
			if (s.getOriginalText().startsWith("Hermes-Object") | s.getOriginalText().startsWith("Kulturkreis")) {
				int k = 0;
				k++;
			}
			super.destroy(article, s);
		}

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<SubClassingDashTreeElementContent> elementContent) {
			if (elementContent.hasErrorInSubtree(article)) {
				this.destroy(article, elementContent);
				Section<DashTreeElement> element = Sections.findAncestorOfType(elementContent, DashTreeElement.class);
				List<Section<DashTreeElement>> found = DashTreeUtils.findChildrenDashtreeElements(element);
				for (Section<DashTreeElement> section : found) {
					Section<SubClassingDashTreeElementContent> content = Sections.findSuccessor(section, SubClassingDashTreeElementContent.class);
					this.destroy(article, content);
				}
				return new ArrayList<KDOMReportMessage>(0);
			}
			if (elementContent.getOriginalText().startsWith("Hermes-Object") | elementContent.getOriginalText().startsWith("Kulturkreis")) {
				int k = 0;
				k++;
			}
			Section<?> element = elementContent.getFather();
			if (element.get().isAssignableFromType(DashTreeElement.class)) {
				Section<? extends DashTreeElement> father = DashTreeUtils
						.getFatherDashTreeElement(element);
				if (father != null) {
					Section<? extends IRITermReference> fatherElement = Sections
							.findSuccessor(father, IRITermReference.class);
					Section<? extends IRITermReference> childElement = Sections
							.findSuccessor(element, IRITermReference
									.class);
					URI localURI = childElement.get().getNode(childElement);
					URI fatherURI = fatherElement.get().getNode(fatherElement);
					if (localURI == null || fatherURI == null) {
						return Arrays.asList((KDOMReportMessage) new ObjectNotFoundWarning(
								element.getOriginalText()));
					}
					Rdf2GoCore.getInstance().addStatement(localURI,
								RDFS.subClassOf, fatherURI, childElement);
				}
			}

			return new ArrayList<KDOMReportMessage>(0);
		}

		

	}

}

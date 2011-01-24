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
package de.d3web.we.kdom.dashTree.subclassing;

import java.util.Collection;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryException;

import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreeHandler.IncrementalConstraint;
import de.knowwe.core.dashtree.DashTreeElement;
import de.knowwe.core.dashtree.DashTreeElementContent;
import de.knowwe.core.dashtree.DashTreeUtils;

public class SubClassingDashTreeElement extends DashTreeElement implements
		IncrementalConstraint<SubClassingDashTreeElement> {

	@Override
	protected void init() {
		super.init();
		this.addSubtreeHandler(new SubClassingDashTreeElementOWLSubTreeHandler());
	}

	@Override
	public boolean violatedConstraints(KnowWEArticle article, Section<SubClassingDashTreeElement> s) {
		return DashTreeUtils.isChangeInAncestorSubtree(article, s, 1);
	}

	private class SubClassingDashTreeElementOWLSubTreeHandler extends
			OwlSubtreeHandler<SubClassingDashTreeElement> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<SubClassingDashTreeElement> element) {
			IntermediateOwlObject io = new IntermediateOwlObject();
			if (element.getObjectType().isAssignableFromType(DashTreeElement.class)) {
				Section<? extends DashTreeElement> father = DashTreeUtils
						.getFatherDashTreeElement(element);
				if (father != null) {
					Section<? extends DashTreeElementContent> fatherElement = father
							.findChildOfType(DashTreeElementContent.class);
					Section<? extends DashTreeElementContent> childElement = element
							.findChildOfType(DashTreeElementContent
									.class);
					createSubClassRelation(childElement, fatherElement, io);
				}
			}
			SemanticCoreDelegator.getInstance().addStatements(io, element);
			return null;
		}

	}

	private void createSubClassRelation(
			Section<? extends DashTreeElementContent> child,
			Section<? extends DashTreeElementContent> fatherElement,
			IntermediateOwlObject io) {
		UpperOntology uo = UpperOntology.getInstance();
		URI localURI = uo.getHelper().createlocalURI(child.getOriginalText());
		URI fatherURI = uo.getHelper().createlocalURI(
				fatherElement.getOriginalText());
		try {
			io.addStatement(uo.getHelper().createStatement(localURI,
					RDFS.SUBCLASSOF, fatherURI));
		}
		catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

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
package de.knowwe.kdom.turtle;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.model.node.Node;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreeHandler.IncrementalConstraint;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.termObject.RDFResourceType;

public class DataTypeValueTurtle extends AbstractType implements RDFResourceType, IncrementalConstraint<TurtleMarkup> {

	public DataTypeValueTurtle() {
		this.setCustomRenderer(new StyleRenderer("font-weight:bold"));
		// add dummy handler with given Priority, so the reviseIterator will
		// consider this priority after type is overriden with other type..
		this.addSubtreeHandler(Priority.LOWER, new SubtreeHandler<DataTypeValueTurtle>() {
			@Override
			public java.util.Collection<de.d3web.we.kdom.report.KDOMReportMessage> create(KnowWEArticle article, de.d3web.we.kdom.Section<DataTypeValueTurtle> s) {
				return new ArrayList<KDOMReportMessage>(0);
				}
				;
		});
	}


	@Override
	public Node getNode(Section<? extends RDFResourceType> s) {
		return Rdf2GoCore.getInstance().createLiteral(s.getOriginalText());
	}

	@Override
	public boolean violatedConstraints(KnowWEArticle article, Section<TurtleMarkup> s) {
		List<Section<RDFResourceType>> list = new ArrayList<Section<RDFResourceType>>();
		Sections.findSuccessorsOfType(s.getFather(), RDFResourceType.class, list);
		return s.getFather().isOrHasSuccessorNotReusedBy(article.getTitle());
		// abreturn list.get(1).isReusedBy(article.getTitle());
	}
}

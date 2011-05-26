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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SyntaxError;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.rdf2go.RDF2GoSubtreeHandler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.termObject.RDFNodeType;

public class TurtleRDF2GoCompiler extends RDF2GoSubtreeHandler<TurtleMarkup> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<TurtleMarkup> s) {
		if (s.hasErrorInSubtree(article)) {
			return new ArrayList<KDOMReportMessage>(0);
		}

		List<Section<RDFNodeType>> found = new ArrayList<Section<RDFNodeType>>();
		Node subURI = null;
		Node predURI = null;
		Node objURI = null;

		Sections.findSuccessorsOfType(s, RDFNodeType.class, found);

		if (found.size() == 3) {
			Section<RDFNodeType> subject = found.get(0);
			Section<RDFNodeType> predicate = found.get(1);
			Section<RDFNodeType> object = found.get(2);

			subURI = subject.get().getNode(subject);
			predURI = predicate.get().getNode(predicate);
			objURI = object.get().getNode(object);
		}
		else {
			return Arrays.asList((KDOMReportMessage) new SyntaxError(
					"invalid term combination:" + found.size()));
		}
		if(subURI == null) {
			return Arrays.asList((KDOMReportMessage) new SyntaxError(
					"subject URI not found"));
		}
		if(predURI == null) {
			return Arrays.asList((KDOMReportMessage) new SyntaxError(
					"predicate URI not found"));
		}
		if(objURI == null) {
			return Arrays.asList((KDOMReportMessage) new SyntaxError(
					"object URI not found"));
		}
		
		Rdf2GoCore.getInstance().addStatement(subURI.asResource(),
				predURI.asURI(), objURI, s);

		return new ArrayList<KDOMReportMessage>(0);
	}

	private URI getSubject(Section<TurtleMarkup> s) {
		TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
		boolean b = terminologyHandler.isDefinedTerm(s.getArticle(),
				s.getArticle().getTitle(), KnowWETerm.GLOBAL);
		if (b) {
			Section<? extends TermDefinition> termDefiningSection = terminologyHandler.getTermDefiningSection(
					s.getArticle(), s.getArticle().getTitle(), KnowWETerm.GLOBAL);

			Object termObject = termDefiningSection.get().getTermObject(s.getArticle(),
					termDefiningSection);

			if (termObject instanceof URI) {
				return (URI) termObject;
			}
		}
		return null;

	}

}

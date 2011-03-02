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

/**
 * 
 */
package de.d3web.we.kdom.semanticAnnotation.rdf2go;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.util.RDFTool;

import de.d3web.we.core.semantic.rdf2go.IntermediateOwlObject;
import de.d3web.we.core.semantic.rdf2go.PropertyManager;
import de.d3web.we.core.semantic.rdf2go.RDF2GoSubtreeHandler;
import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.contexts.ContextManager;
import de.d3web.we.kdom.contexts.DefaultSubjectContext;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.sectionFinder.AllBeforeTypeSectionFinder;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.utils.KnowWEUtils;

/**
 * @author kazamatzuri
 * 
 */
public class SemanticAnnotationObject extends AbstractType {

	@Override
	public void init() {
		SemanticAnnotationProperty propType = new SemanticAnnotationProperty();
		SemanticAnnotationSubject subject = new SemanticAnnotationSubject();
		subject.setSectionFinder(new AllBeforeTypeSectionFinder(propType));
		this.childrenTypes.add(propType);
		this.childrenTypes.add(subject);
		this.childrenTypes.add(new SimpleAnnotation());
		this.sectionFinder = new AllTextFinderTrimmed();
		this.addSubtreeHandler(new SemanticAnnotationObjectSubTreeHandler());
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	private class SemanticAnnotationObjectSubTreeHandler extends
			RDF2GoSubtreeHandler<SemanticAnnotationObject> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section s) {
			IntermediateOwlObject io = new IntermediateOwlObject();
			List<KDOMReportMessage> msgs = new ArrayList<KDOMReportMessage>();
			List<Section> childs = s.getChildren();
			URI prop = null;
			URI stringa = null;
			URI soluri = null;
			boolean erronousproperty = false;
			String badprop = "";
			for (Section cur : childs) {
				if (cur.get().getClass().equals(
						SemanticAnnotationProperty.class)) {
					IntermediateOwlObject tempio = (IntermediateOwlObject) KnowWEUtils
							.getStoredObject(article, cur, Rdf2GoCore.getInstance().IOO);
					prop = tempio.getLiterals().get(0);
					erronousproperty = !tempio.getValidPropFlag();
					if (erronousproperty) {
						badprop = tempio.getBadAttribute();
					}
				}
				else if (cur.get().getClass().equals(
						SemanticAnnotationSubject.class)) {
					String subj = cur.getOriginalText().trim();
					soluri = Rdf2GoCore.getInstance().createlocalURI(subj);
				}
				else if (cur.get().getClass().equals(
						SimpleAnnotation.class)) {
					IntermediateOwlObject tempio = (IntermediateOwlObject) KnowWEUtils
							.getStoredObject(article, cur, Rdf2GoCore.getInstance().IOO);
					if (tempio.getValidPropFlag()) {
						stringa = tempio.getLiterals().get(0);
					}
					else {
						badprop = tempio.getBadAttribute();
					}
				}

			}

			boolean validprop = false;
			if (erronousproperty) {
				io.setBadAttribute(badprop);
				io.setValidPropFlag(false);
			}
			else if (prop != null) {
				validprop = PropertyManager.getInstance().isValid(prop);
				io.setBadAttribute(RDFTool.getLabel(prop));
			}

			io.setValidPropFlag(validprop);
			if (!validprop) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"invalid property: " + s.getOriginalText());
			}

			if (prop != null && validprop && stringa != null) {
				DefaultSubjectContext sol = (DefaultSubjectContext) ContextManager
						.getInstance().getContext(s, DefaultSubjectContext.CID);
				if (soluri == null) {
					String soluriString = sol.getSubject();
					soluri = Rdf2GoCore.getInstance().createlocalURI(soluriString);
				}
				Statement stmnt = null;
				// try {
				if (PropertyManager.getInstance().isRDFS(prop)) {
					stmnt = Rdf2GoCore.getInstance().createStatement(soluri, prop,
								stringa);
					io.addStatement(stmnt);
					io.addAllStatements(Rdf2GoCore.getInstance().createStatementSrc(soluri,
								prop, stringa, s.getFather().getFather(),
								Rdf2GoCore.getInstance().ANNOTATION));
				}
				else if (PropertyManager.getInstance().isRDF(prop)) {
					stmnt = Rdf2GoCore.getInstance().createStatement(soluri, prop,
								stringa);
					io.addStatement(stmnt);
					io.addAllStatements(Rdf2GoCore.getInstance().createStatementSrc(soluri,
								prop, stringa, s.getFather().getFather(),
								Rdf2GoCore.getInstance().ANNOTATION));
				}
				else if (PropertyManager.getInstance().isNary(prop)) {
					IntermediateOwlObject tempio = Rdf2GoCore.getInstance()
								.createAnnotationProperty(soluri, prop,
										stringa, s.getFather().getFather());
					io.merge(tempio);

				}
				else {
					stmnt = Rdf2GoCore.getInstance().createStatement(soluri, prop,
								stringa);
					io.addStatement(stmnt);
					io.addAllStatements(Rdf2GoCore.getInstance().createStatementSrc(soluri,
								prop, stringa, s.getFather().getFather(),
								Rdf2GoCore.getInstance().ANNOTATION));
				}

				// }
				// catch (RepositoryException e) {
				// msgs.add(new SimpleMessageError(e.getMessage()));
				// }
			}
			Rdf2GoCore.getInstance().addStatements(io, s);
			return msgs;
		}

	}
}

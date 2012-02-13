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

package de.d3web.we.kdom.dashTree.propertyDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryException;

import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.core.semantic.UpperOntology;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.basicType.RoundBracedType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeElementContent;
import de.knowwe.kdom.dashtree.DashTreeUtils;

/**
 * @author Jochen
 * 
 *         This DashTreeElementContent generates the property-definitions in
 *         OWL. For spefification of range and domain restrictions it contains
 *         (in round brackets) a PropertyDetails type in addition to the
 *         PropertyIDDefinition containing the name of the property.
 * 
 */
public class PropertyDashTreeElementContent extends DashTreeElementContent {

	public PropertyDashTreeElementContent() {
		RoundBracedType e = new RoundBracedType(new PropertyDetails());
		e.setSteal(true);
		this.childrenTypes.add(e);
		this.childrenTypes.add(new PropertyIDDefinition());
		this.addSubtreeHandler(new PropertyDashTreeElementContentOWLSubTreeHandler());

	}

	private class PropertyDashTreeElementContentOWLSubTreeHandler extends OwlSubtreeHandler<PropertyDashTreeElementContent> {

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<PropertyDashTreeElementContent> s) {

			List<Message> msgs = new ArrayList<Message>();

			Section<PropertyDashTreeElementContent> sec = s;
			if (s.get() instanceof PropertyDashTreeElementContent) {
				Section<PropertyIDDefinition> propIDSection = Sections.findSuccessor(sec,
						PropertyIDDefinition.class);
				if (propIDSection != null) {
					String propertyName = propIDSection.getText();
					String rangeDef = null;
					String domainDef = null;
					Section<DomainDefinition> domainDefS = Sections.findSuccessor(sec,
							DomainDefinition.class);
					if (domainDefS != null) {
						domainDef = domainDefS.getText();
					}

					Section<RangeDefinition> rangeDefS = Sections.findSuccessor(sec,
							RangeDefinition.class);
					if (rangeDefS != null) {
						rangeDef = rangeDefS.getText();
					}

					UpperOntology uo = UpperOntology.getInstance();
					IntermediateOwlObject io = new IntermediateOwlObject();

					OwlHelper helper = uo.getHelper();
					URI propURI = helper.createlocalURI(propertyName.trim());
					try {

						// creates an Object-Property (in any case)
						io.addStatement(helper.createStatement(propURI, RDF.TYPE,
								OWL.OBJECTPROPERTY));

						// creates a Subproperty relation IF father exists
						Section<? extends DashTreeElement> fatherElement = DashTreeUtils.getFatherDashTreeElement(sec.getFather());
						if (fatherElement != null) {
							Section<PropertyIDDefinition> fatherID = Sections.findSuccessor(
									fatherElement, PropertyIDDefinition.class);
							if (fatherID != null) {
								io.addStatement(helper.createStatement(
										propURI, RDFS.SUBPROPERTYOF, helper
												.createlocalURI(fatherID.getText()
														.trim())));
							}
						}

						// creates Domain restriction if defined
						if (domainDef != null) {
							String[] classes = domainDef.split(",");
							for (String string : classes) {
								if (string.trim().length() > 0) {
									io.addStatement(helper.createStatement(
													propURI, RDFS.DOMAIN, helper
															.createlocalURI(string
																	.trim())));
								}
							}
						}

						// creates Range restriction if defined
						if (rangeDef != null) {
							String[] classes = rangeDef.split(",");
							for (String string : classes) {
								if (string.trim().length() > 0) {
									io.addStatement(helper.createStatement(
													propURI, RDFS.RANGE, helper
															.createlocalURI(string
																	.trim())));
								}
							}
						}
						SemanticCoreDelegator.getInstance().addStatements(io, s);
					}
					catch (RepositoryException e) {
						msgs.add(Messages.error(e.getMessage()));
					}

				}
			}
			return msgs;
		}

	}

}

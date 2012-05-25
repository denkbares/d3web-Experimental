/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.subclassingTree;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.AbstractKnowledgeUnitCompileScriptRDFS;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.rdfs.RDFSTermCategory;
import de.knowwe.rdfs.util.RDFSUtil;

/**
 * 
 * @author jochenreutelshofer
 * @created 25.05.2012
 */
public class HierarchyDashtreeElementCompileScript extends
		AbstractKnowledgeUnitCompileScriptRDFS<HierarchyDashtreeElementContent> {

	@Override
	public void insertIntoRepository(
			Section<HierarchyDashtreeElementContent> elementContent) {

		Collection<Message> messages = new LinkedList<Message>();

		Section<?> element = elementContent.getFather();
		if (element.get().isAssignableFromType(DashTreeElement.class)) {
			Section<? extends DashTreeElement> father = DashTreeUtils
					.getFatherDashTreeElement(element);
			if (father != null) {
				Section<? extends SimpleTerm> fatherElement = Sections
						.findSuccessor(father, SimpleTerm.class);
				Section<? extends SimpleTerm> childElement = Sections
						.findSuccessor(element, SimpleTerm.class);

				URI localURI = RDFSUtil.getURI(childElement);
				URI fatherURI = RDFSUtil.getURI(fatherElement);

				if (localURI == null || fatherURI == null) {
					// error handling here
				}
				else {
					// default predicate is rdfs:subClassOf
					URI predicate = RDFS.subClassOf;

					// ..but can be overwritten by the relation annotation
					Section<DefaultMarkupType> markup = Sections.findAncestorOfType(
							elementContent, DefaultMarkupType.class);
					String relationAnnotation = DefaultMarkupType.getAnnotation(markup,
							HierarchyMarkup.RELATION_ANNOTATION_KEY);
					if (relationAnnotation != null) {
						Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
								new TermIdentifier(relationAnnotation));
						if (termDefinitions != null && termDefinitions.size() > 0) {
							Section<? extends SimpleDefinition> firstDef = termDefinitions.iterator().next();
							if (RDFSUtil.isTermCategory(firstDef,
									RDFSTermCategory.ObjectProperty)) {
								predicate = RDFSUtil.getURI(firstDef);
							}
							else {
								messages.add(new Message(Message.Type.ERROR,
										"The term specified by the relation-annotation '"
												+ firstDef.get().getTermName(firstDef)
												+ "' is not an ObjectProperty!"));
							}
						}
						else {
							messages.add(new Message(Message.Type.ERROR,
									"The term specified by the relation-annotation '"
											+ relationAnnotation
											+ "' is not defined!"));
						}
					}
					if (Messages.getErrors(messages).size() == 0) {
						Rdf2GoCore.getInstance().addStatement(localURI,
								predicate, fatherURI, childElement);
					}
				}
			}
		}
		// store messages found while compiling the current section
		Messages.storeMessages(elementContent.getArticle(), elementContent, getClass(),
				messages);

	}

	@Override
	public Collection<Section<? extends SimpleReference>> getAllReferencesOfKnowledgeUnit(
			Section<? extends KnowledgeUnit> section) {

		Set<Section<? extends SimpleReference>> result = new HashSet<Section<? extends SimpleReference>>();

		// add child-DTE to ref-list
		Section<? extends IRITermRef> childElement = Sections
				.findSuccessor(section, IRITermRef.class);
		if (childElement != null) { // can be null if this line contains a
									// definition
			result.add(childElement);
		}

		// add parent-DTE to ref-list
		Section<? extends DashTreeElement> father = DashTreeUtils
				.getFatherDashTreeElement(section);
		if (father != null) {

			Section<? extends IRITermRef> fatherElement = Sections
					.findSuccessor(father, IRITermRef.class);
			if (fatherElement != null) {
				result.add(fatherElement);
			}
		}

		return result;
	}

}

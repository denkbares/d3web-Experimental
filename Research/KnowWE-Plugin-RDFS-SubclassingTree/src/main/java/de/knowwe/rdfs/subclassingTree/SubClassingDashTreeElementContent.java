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
package de.knowwe.rdfs.subclassingTree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeElementContent;
import de.knowwe.kdom.dashtree.DashTreeUtils;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.AbstractKnowledgeUnitCompileScriptRDFS;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.rdfs.util.RDFSUtil;

public class SubClassingDashTreeElementContent extends DashTreeElementContent
		implements KnowledgeUnit<SubClassingDashTreeElementContent> {

	public SubClassingDashTreeElementContent() {
		IRITermRef ref = new IRITermRef();
		ref.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(ref);
	}

	private class SubClassingDashTreeElementCompileScript extends
			AbstractKnowledgeUnitCompileScriptRDFS<SubClassingDashTreeElementContent> {

		@Override
		public void insertIntoRepository(
				Section<SubClassingDashTreeElementContent> elementContent) {

			Section<?> element = elementContent.getFather();
			if (element.get().isAssignableFromType(DashTreeElement.class)) {
				Section<? extends DashTreeElement> father = DashTreeUtils
						.getFatherDashTreeElement(element);
				if (father != null) {
					Section<? extends IRITermRef> fatherElement = Sections
							.findSuccessor(father, IRITermRef.class);
					Section<? extends IRITermRef> childElement = Sections
							.findSuccessor(element, IRITermRef.class);

					URI localURI = RDFSUtil.getURI(childElement);
					URI fatherURI = RDFSUtil.getURI(fatherElement);

					if (localURI == null || fatherURI == null) {
						// error handling here
					} else {
						Rdf2GoCore.getInstance().addStatement(localURI,
								RDFS.subClassOf, fatherURI, childElement);
					}
				}
			}

		}

		@Override
		public Collection<Section<? extends TermReference>> getAllReferencesOfKnowledgeUnit(
				Section<? extends KnowledgeUnit<SubClassingDashTreeElementContent>> section) {
			
			Set<Section<? extends TermReference>> result = new HashSet<Section<? extends TermReference>>();
			
			Section<? extends DashTreeElement> father = DashTreeUtils
					.getFatherDashTreeElement(section);
			Section<? extends IRITermRef> fatherElement = Sections
					.findSuccessor(father, IRITermRef.class);
			Section<? extends IRITermRef> childElement = Sections
					.findSuccessor(section, IRITermRef.class);
			
			result.add(childElement);
			result.add(fatherElement);
			
			
			return result;
		}

	}

	@Override
	public KnowledgeUnitCompileScript getCompileScript() {

		return new SubClassingDashTreeElementCompileScript();
	}

}
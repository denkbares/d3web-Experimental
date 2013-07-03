/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.annotation.type.list.ListObjectIdentifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.AbstractKnowledgeUnitCompileScript;
import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.InvalidReference;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;
import de.knowwe.kdom.sectionFinder.SplitSectionFinderUnquotedNonEmpty;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.06.2013
 */
public class ConceptListContent extends AbstractType {

	public ConceptListContent() {
		this.setSectionFinder(new LineSectionFinder());
		this.addChildType(new ObjectSegment());
		this.addChildType(new ListSeparatorType());
	}

	class ListSeparatorType extends AbstractType {

		/**
		 * 
		 */
		public ListSeparatorType() {
			this.setSectionFinder(new AllTextSectionFinder());
			this.setRenderer(new StyleRenderer("float:left;"));
		}
	}

	class ObjectSegment extends AbstractKnowledgeUnitType<ObjectSegment> {

		public ObjectSegment() {
			this.setSectionFinder(new SplitSectionFinderUnquotedNonEmpty(","));
			this.addChildType(new ListObjectIdentifier(new OIDeleteItemRenderer()));
			this.setCompileScript(new ObjectSegmentCompileScript());
		}

		class ObjectSegmentCompileScript extends AbstractKnowledgeUnitCompileScript<ObjectSegment> {

			@Override
			public void insertIntoRepository(Section<ObjectSegment> section) {

				if (section.getText().length() == 0) return;

				Section<IncrementalTermDefinition> conceptDefinition = MarkupUtils.getConceptDefinition(section);
				if (conceptDefinition == null) {
					return; // do nothing
				}

				Section<Term> objectSection = Sections.findSuccessor(section,
						Term.class);

				if (objectSection == null) {
					System.out.println("Objectsection is null! " + section.getArticle().getTitle());
				}

				// if there is a compile error, do not insert knowledge
				boolean hasError = !IncrementalCompiler.getInstance().getTerminology().isValid(
						objectSection.get().getTermIdentifier(objectSection));
				if (hasError) return;

				URI subjectURI = RDFSUtil.getURI(conceptDefinition);
				Section<RelationMarkup> relationMarkup = Sections.findAncestorOfType(
						section, RelationMarkup.class);
				URI predicateURI = relationMarkup.get().getRelationURI();

				URI objectURI = RDFSUtil.getURI(objectSection);
				Statement statement = Rdf2GoCore.getInstance().createStatement(subjectURI,
						predicateURI,
						objectURI);
				if (relationMarkup.get().isInverseDir()) {
					statement = Rdf2GoCore.getInstance().createStatement(objectURI,
							predicateURI, subjectURI
							);
				}

				Rdf2GoCore.getInstance().addStatements(section,
						new Statement[] { statement });

			}

			@Override
			public void deleteFromRepository(Section<ObjectSegment> section) {
				Rdf2GoCore.getInstance().removeStatementsForSection(section);
			}

			@Override
			public Collection<Section<? extends Term>> getExternalReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit> section) {
				Set<Section<? extends Term>> result = new HashSet<Section<? extends Term>>();
				Collection<Section<ConceptMarkup>> conceptDefinitions = MarkupUtils.getConecptDefinitions(section);
				for (Section<ConceptMarkup> def : conceptDefinitions) {
					result.add(Sections.findSuccessor(def,
							IncrementalTermDefinition.class));
				}
				if (conceptDefinitions.size() != 1) {
					Section<InvalidReference> invalidReference =
							Section.createSection("foo",
									new InvalidReference(), null);
					result.add(invalidReference);
				}
				return result;
			}

		}

	}
}

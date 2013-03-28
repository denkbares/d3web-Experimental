/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.compile.object.AbstractKnowledgeUnitCompileScript;
import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.InvalidReference;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.renderer.CompositeRenderer;
import de.knowwe.compile.object.renderer.ReferenceSurroundingRenderer;
import de.knowwe.compile.object.renderer.SurroundingRenderer;
import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;
import de.knowwe.kdom.sectionFinder.SplitSectionFinderUnquotedNonEmpty;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 30.11.2012
 */
public class RelationMarkupContentType extends AbstractType {

	public RelationMarkupContentType(String regex) {
		this.setSectionFinder(new RegexSectionFinder(regex, Pattern.MULTILINE
				| Pattern.DOTALL,
				2));
		this.addChildType(new RelationLine());
		this.setRenderer(new CompositeRenderer(new SurroundingRenderer() {

			@Override
			public void renderPre(Section<?> section, UserContext user, RenderResult string) {
				string.appendHtml("<div class='relationMarkupContent' id='"
						+ section.getID() + "'>");

			}

			@Override
			public void renderPost(Section<?> section, UserContext user, RenderResult string) {
				string.appendHtml("</div>");

			}
		}));
	}

	class RelationLine extends AbstractType {

		public RelationLine() {
			this.setSectionFinder(new LineSectionFinder());
			this.addChildType(new ObjectSegment());
		}

		class ObjectSegment extends AbstractKnowledgeUnitType<ObjectSegment> {

			public ObjectSegment() {
				this.setSectionFinder(new SplitSectionFinderUnquotedNonEmpty(","));
				this.addChildType(new ObjectIdentifier());
				this.setCompileScript(new ObjectSegmentCompileScript());
			}

			class ObjectSegmentCompileScript extends AbstractKnowledgeUnitCompileScript<ObjectSegment> {

				@Override
				public void insertIntoRepository(Section<ObjectSegment> section) {

					if (section.getText().length() == 0) return;

					Section<IncrementalTermDefinition> conceptDefinition = MarkupUtils.getConceptDefinition(section);
					if (conceptDefinition == null) {
						// TODO: error message !?
						return; // do nothing
					}

					URI subjectURI = RDFSUtil.getURI(conceptDefinition);
					Section<RelationMarkup> relationMarkup = Sections.findAncestorOfType(
							section, RelationMarkup.class);
					URI predicateURI = relationMarkup.get().getRelationURI();
					Section<ObjectIdentifier> objectSection = Sections.findSuccessor(section,
							ObjectIdentifier.class);
					URI objectURI = RDFSUtil.getURI(objectSection);
					Statement statement = Rdf2GoCore.getInstance().createStatement(subjectURI,
							predicateURI,
							objectURI);
					System.out.println("Inserting: " + statement.toString());
					Rdf2GoCore.getInstance().addStatements(section,
							new Statement[] { statement });

				}

				@Override
				public void deleteFromRepository(Section<ObjectSegment> section) {
					System.out.println("Trying to remove: " + section.toString());
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

			class ObjectIdentifier extends SimpleReference {

				public ObjectIdentifier() {
					super(TermRegistrationScope.GLOBAL, String.class);
					this.setSectionFinder(new AllTextFinderTrimmed());
					CompositeRenderer renderer = new CompositeRenderer(new OIRenderer(),
							new ReferenceSurroundingRenderer());
					this.setRenderer(renderer);
				}

				class OIRenderer implements Renderer {

					@Override
					public void render(Section<?> section, UserContext user, RenderResult string) {
						Section<SimpleReference> ref = Sections.cast(section,
								SimpleReference.class);
						string.appendHtml("<a href='" + RDFSUtil.getURI(ref)
								+ "'>" + section.getText() + "</a>");

					}
				}
			}
		}
	}

}

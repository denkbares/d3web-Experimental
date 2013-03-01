/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.ontology.kdom.relation;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;

import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.ontology.kdom.OntologyUtils;
import de.knowwe.ontology.kdom.objectproperty.AbbreviatedPropertyReference;
import de.knowwe.ontology.kdom.resource.AbbreviatedResourceReference;
import de.knowwe.rdf2go.Rdf2GoCore;

public class RelationDefinition extends AbstractType {

	public RelationDefinition() {
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(new SubjectType());
		this.addChildType(new PredicateType());
		this.addChildType(new LiteralType());
		this.addChildType(new ObjectType());
		this.addSubtreeHandler(Priority.LOWER, new RelationSubtreeHandler());
	}

	private static class SubjectType extends AbstractType {

		public SubjectType() {
			this.setSectionFinder(OntologyUtils.ABBREVIATED_RESOURCE_FINDER);
			this.addChildType(new AbbreviatedResourceReference());
		}
	}

	private static class PredicateType extends AbstractType {

		public PredicateType() {
			this.setSectionFinder(OntologyUtils.ABBREVIATED_RESOURCE_FINDER);
			this.addChildType(new AbbreviatedPropertyReference());
		}
	}

	private static class ObjectType extends AbstractType {

		public ObjectType() {
			this.setSectionFinder(OntologyUtils.ABBREVIATED_RESOURCE_FINDER);
			this.addChildType(new AbbreviatedResourceReference());
		}
	}

	private static class RelationSubtreeHandler extends SubtreeHandler<RelationDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<RelationDefinition> section) {

			if (section.hasErrorInSubtree()) return Messages.noMessage();

			Rdf2GoCore core = Rdf2GoCore.getInstance(article);

			Section<SubjectType> subjectSection = Sections.findChildOfType(section,
					SubjectType.class);
			if (subjectSection == null) {
				return Messages.asList(Messages.error("No subject found for relation definition '"
						+ section.getText() + "'."));
			}
			Section<AbbreviatedResourceReference> abbrSubjectSection = Sections.findSuccessor(
					subjectSection, AbbreviatedResourceReference.class);

			URI subjectURI = abbrSubjectSection.get().getResourceURI(core, abbrSubjectSection);

			Section<PredicateType> predicateSection = Sections.findChildOfType(section,
					PredicateType.class);
			if (predicateSection == null) {
				return Messages.asList(Messages.error("No predicate found for relation definition '"
						+ section.getText() + "'."));
			}
			Section<AbbreviatedPropertyReference> abbrObjPropSection = Sections.findSuccessor(
					predicateSection, AbbreviatedPropertyReference.class);

			URI predicatedURI = abbrObjPropSection.get().getPropertyURI(core, abbrObjPropSection);

			Section<ObjectType> objectSection = Sections.findChildOfType(section,
					ObjectType.class);

			if (objectSection == null) {
				Section<LiteralType> literalSection = Sections.findChildOfType(section,
						LiteralType.class);
				String literalString = literalSection.get().getLiteral(literalSection);
				Literal literal = core.createLiteral(literalString, XSD._string);
				core.addStatements(core.createStatement(subjectURI, predicatedURI, literal));
			}
			else {

				Section<AbbreviatedResourceReference> abbrObjectSection = Sections.findSuccessor(
						objectSection, AbbreviatedResourceReference.class);
				URI objectURI = abbrObjectSection.get().getResourceURI(core, abbrObjectSection);

				core.addStatements(core.createStatement(subjectURI, predicatedURI, objectURI));
			}

			return Messages.noMessage();
		}

	}
}

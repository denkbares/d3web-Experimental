package de.knowwe.ontology.kdom.relation;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.Patterns;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.ontology.kdom.individual.NamespaceIndividualReference;
import de.knowwe.ontology.kdom.namespace.AbbreviationPrefixReference;
import de.knowwe.ontology.kdom.objectproperty.NamespaceObjectPropertyReference;
import de.knowwe.rdf2go.Rdf2GoCore;

public class RelationDefinition extends AbstractType {

	private static final String THING_PATTERN = "(?:" + Patterns.QUOTED + "|[^\" ]+)";
	private static final String ABBREVIATION_THING_PATTERN = "(?:" +
			AbbreviationPrefixReference.ABBREVIATION_PREFIX_PATTERN + ")?" + THING_PATTERN;
	private static final String RELATION_PATTERN = "(?:" + ABBREVIATION_THING_PATTERN
			+ "(?: |\\z)+){3}";

	private static final SectionFinder ABBREVIATION_THING_FINDER = new ConstraintSectionFinder(
			new RegexSectionFinder(ABBREVIATION_THING_PATTERN),
			AtMostOneFindingConstraint.getInstance());

	public RelationDefinition() {
		this.setSectionFinder(new RegexSectionFinder(RELATION_PATTERN));
		this.addChildType(new SubjectType());
		this.addChildType(new PredicateType());
		this.addChildType(new ObjectType());
		this.addSubtreeHandler(Priority.LOWER, new RelationSubtreeHandler());
	}

	private static class SubjectType extends AbstractType {

		public SubjectType() {
			this.setSectionFinder(ABBREVIATION_THING_FINDER);
			this.addChildType(new NamespaceIndividualReference());
		}
	}

	private static class PredicateType extends AbstractType {

		public PredicateType() {
			this.setSectionFinder(ABBREVIATION_THING_FINDER);
			this.addChildType(new NamespaceObjectPropertyReference());
		}
	}

	private static class ObjectType extends AbstractType {

		public ObjectType() {
			this.setSectionFinder(ABBREVIATION_THING_FINDER);
			this.addChildType(new NamespaceIndividualReference());
		}
	}

	private static class RelationSubtreeHandler extends SubtreeHandler<RelationDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<RelationDefinition> section) {

			if (section.hasErrorInSubtree()) return Messages.noMessage();

			Rdf2GoCore core = Rdf2GoCore.getInstance(article);

			Section<SubjectType> subjectSection = Sections.findChildOfType(section,
					SubjectType.class);
			Section<NamespaceIndividualReference> nsSubjectSection = Sections.findSuccessor(
					subjectSection, NamespaceIndividualReference.class);

			Section<PredicateType> predicateSection = Sections.findChildOfType(section,
					PredicateType.class);
			Section<NamespaceObjectPropertyReference> nsObjPropSection = Sections.findSuccessor(
					predicateSection, NamespaceObjectPropertyReference.class);

			Section<ObjectType> objectSection = Sections.findChildOfType(section,
					ObjectType.class);
			Section<NamespaceIndividualReference> nsObjectSection = Sections.findSuccessor(
					objectSection, NamespaceIndividualReference.class);

			String subjectAbbreviation = nsSubjectSection.get().getAbbreviation(nsSubjectSection);
			String subjectIndividual = nsSubjectSection.get().getIndividual(nsSubjectSection);

			URI subjectURI = core.createURI(subjectAbbreviation, subjectIndividual);

			String objPropAbbreviation = nsObjPropSection.get().getAbbreviation(nsObjPropSection);
			String objectProperty = nsObjPropSection.get().getProperty(nsObjPropSection);

			URI predicateURI = core.createURI(objPropAbbreviation, objectProperty);

			String objectAbbreviation = nsObjectSection.get().getAbbreviation(nsObjectSection);
			String objectIndividual = nsObjectSection.get().getIndividual(nsObjectSection);

			URI objectURI = core.createURI(objectAbbreviation, objectIndividual);

			core.addStatements(core.createStatement(subjectURI, predicateURI, objectURI));

			return Messages.noMessage();
		}

	}
}

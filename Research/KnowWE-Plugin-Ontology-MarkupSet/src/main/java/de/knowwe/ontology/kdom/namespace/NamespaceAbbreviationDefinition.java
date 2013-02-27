package de.knowwe.ontology.kdom.namespace;

import java.util.Collection;

import de.knowwe.core.compile.Priority;
import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.rdf2go.Rdf2GoCore;

public class NamespaceAbbreviationDefinition extends SimpleDefinition {

	public NamespaceAbbreviationDefinition() {
		super(TermRegistrationScope.LOCAL, NamespaceAbbreviationDefinition.class);
		this.addSubtreeHandler(Priority.HIGH, new NamespaceSubtreeHandler());
		this.setSectionFinder(new RegexSectionFinder("\\s*\\w+\\s+\\S+\\s*"));
		this.addChildType(new AbbreviationDefinition());
		this.addChildType(new NamespaceDefinition());
	}

	@Override
	public String getTermName(Section<? extends SimpleTerm> section) {
		return getAbbreviation(section) + " - " + getNamespace(section);
	}

	private String getNamespace(Section<? extends SimpleTerm> section) {
		Section<NamespaceDefinition> namespace = Sections.findChildOfType(section,
				NamespaceDefinition.class);
		String namespaceName = namespace.get().getTermName(namespace);
		return namespaceName;
	}

	private String getAbbreviation(Section<? extends SimpleTerm> section) {
		Section<AbbreviationDefinition> abbreviation = Sections.findChildOfType(section,
				AbbreviationDefinition.class);
		String abbreviationName = abbreviation.get().getTermName(abbreviation);
		return abbreviationName;
	}

	private static class NamespaceSubtreeHandler extends SubtreeHandler<NamespaceAbbreviationDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<NamespaceAbbreviationDefinition> section) {
			Rdf2GoCore core = Rdf2GoCore.getInstance(article);
			String abbreviation = section.get().getAbbreviation(section);
			String namespace = section.get().getNamespace(section);
			core.addNamespace(abbreviation, namespace);
			return Messages.noMessage();
		}
	}
}

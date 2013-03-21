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
		this.addSubtreeHandler(Priority.HIGHEST, new NamespaceSubtreeHandler());
		this.setSectionFinder(new RegexSectionFinder("\\s*\\w+\\s+\\S+\\s*"));
		this.addChildType(new AbbreviationDefinition());
		this.addChildType(new NamespaceDefinition());
	}

	@Override
	public String getTermName(Section<? extends SimpleTerm> section) {
		return getAbbreviation(section) + " - " + getNamespace(section);
	}

	public String getNamespace(Section<? extends SimpleTerm> section) {
		Section<NamespaceDefinition> namespace = Sections.findChildOfType(section,
				NamespaceDefinition.class);
		String namespaceName = namespace.get().getTermName(namespace);
		return namespaceName;
	}

	public String getAbbreviation(Section<? extends SimpleTerm> section) {
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

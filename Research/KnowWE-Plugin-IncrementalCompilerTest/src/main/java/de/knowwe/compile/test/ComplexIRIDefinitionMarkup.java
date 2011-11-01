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

package de.knowwe.compile.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.KnowWETerm;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.SplitUtility;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.RegexSectionFinderSingle;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ComplexIRIDefinitionMarkup extends AbstractType implements ComplexDefinition<ComplexIRIDefinitionMarkup>, KnowledgeUnit<ComplexIRIDefinitionMarkup> {

	private static final String REGEX = "(.+)\\s(\\w+)::\\s(.+)$";

	private static final String REGEX_DEF = "^def\\s+";

	public ComplexIRIDefinitionMarkup() {
		this.sectionFinder = new RegexSectionFinder(REGEX_DEF + REGEX, Pattern.MULTILINE);

		this.addChildType(new DefType());
		this.addChildType(new DefinitionTerm());
		this.addChildType(new Predicate());
		this.addChildType(new Object());
	}

	// @Override
	// public Collection<Section<TermReference>>
	// getAllReferencesOfComplexDefinition(
	// Section<? extends ComplexDefinition<ComplexIRIDefinitionMarkup>> section)
	// {
	// return CompileUtils.getAllReferencesOfComplexDefinition(section);
	// }

	class DefType extends AbstractType {
		public DefType() {
			this.setSectionFinder(new RegexSectionFinderSingle(REGEX_DEF));
			this.setCustomRenderer(new StyleRenderer("font-style:italic;"));
		}
	}

	class DefinitionTerm extends IncrementalTermDefinition<String> {

		public DefinitionTerm() {
			super(String.class);
			this.setSectionFinder(new RegexSectionFinderSingle(Pattern.compile(REGEX), 1));
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
			return SplitUtility.unquote(s.getOriginalText().trim());
		}

	}

	class Predicate extends IRITermRef {
		public Predicate() {
			this.sectionFinder = new RegexSectionFinderSingle("\\b([^\\s]*)::",
					Pattern.DOTALL, 1);
		}

		@Override
		public String getTermObjectDisplayName() {
			return "URI";
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
			return s.getOriginalText().trim().replaceAll("::", "").trim();
		}
	}

	class Object extends IRITermRef {
		public Object() {
			this.sectionFinder = new RegexSectionFinderSingle("::\\s(.*)",
					Pattern.DOTALL, 1);
		}

		@Override
		public String getTermObjectDisplayName() {
			return "URI";
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
			return s.getOriginalText().trim();
		}
	}
	
	class complexIRIDefinitionCompileScript implements KnowledgeUnitCompileScript<ComplexIRIDefinitionMarkup> {
	
	@Override
	public Collection<Section<TermReference>> getAllReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit<ComplexIRIDefinitionMarkup>> section) {
		return CompileUtils.getAllReferencesOfCompilationUnit(section);
	}

	@Override
	public void deleteFromRepository(Section<ComplexIRIDefinitionMarkup> section) {
		Rdf2GoCore.getInstance().removeSectionStatementsRecursive(section);
	}

	@Override
	public void insertIntoRepository(Section<ComplexIRIDefinitionMarkup> section) {
		List<Section<TermReference>> found = new ArrayList<Section<TermReference>>();
		Node subURI = null;
		Node predURI = null;
		Node objURI = null;

		Sections.findSuccessorsOfType(section, TermReference.class, found);
		Section<TermDefinition> subject = Sections.findSuccessor(section,
				TermDefinition.class);

		if (found.size() == 2) {

			Section<TermReference> predicate = found.get(0);
			Section<TermReference> object = found.get(1);

			subURI = Utils.getURI(subject);
			predURI = Utils.getURI(predicate);
			objURI = Utils.getURI(object);
		}
		else {
			// return Arrays.asList((KDOMReportMessage) new SyntaxError(
			// "invalid term combination:" + found.size()));
		}
		if (subURI == null) {
			// return Arrays.asList((KDOMReportMessage) new SyntaxError(
			// "subject URI not found"));
		}
		if (predURI == null) {
			// return Arrays.asList((KDOMReportMessage) new SyntaxError(
			// "predicate URI not found"));
		}
		if (objURI == null) {
			// return Arrays.asList((KDOMReportMessage) new SyntaxError(
			// "object URI not found"));
		}

		Rdf2GoCore.getInstance().addStatement(subURI.asResource(),
				predURI.asURI(), objURI, section);

		// return new ArrayList<KDOMReportMessage>(0);

	}

	}

	@Override
	public KnowledgeUnitCompileScript<ComplexIRIDefinitionMarkup> getCompileScript() {
		return new complexIRIDefinitionCompileScript();
	}


}

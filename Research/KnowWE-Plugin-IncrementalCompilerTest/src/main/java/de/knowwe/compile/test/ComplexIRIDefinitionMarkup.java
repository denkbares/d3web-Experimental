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

import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.SimpleKnowledgeUnitCompileScript;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.RegexSectionFinderSingle;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ComplexIRIDefinitionMarkup extends AbstractKnowledgeUnitType<ComplexIRIDefinitionMarkup> implements ComplexDefinition {

	private static final String REGEX = "(.+)\\s(\\w+)::\\s(.+)$";

	private static final String REGEX_DEF = "^def\\s+";

	public ComplexIRIDefinitionMarkup() {
		this.setCompileScript(new ComplexIRIDefinitionCompileScript());

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
			this.setRenderer(new StyleRenderer("font-style:italic;"));
		}
	}

	class DefinitionTerm extends IncrementalTermDefinition<String> {

		public DefinitionTerm() {
			super(String.class);
			this.setSectionFinder(new RegexSectionFinderSingle(Pattern.compile(REGEX), 1));
		}

		@Override
		public String getTermName(Section<? extends Term> section) {
			return Strings.unquote(section.getText().trim());
		}
	}

	class Predicate extends IRITermRef {

		public Predicate() {
			this.sectionFinder = new RegexSectionFinderSingle("\\b([^\\s]*)::",
					Pattern.DOTALL, 1);
		}

		@Override
		public String getTermName(Section<? extends Term> s) {
			return s.getText().trim().replaceAll("::", "").trim();
		}
	}

	class Object extends IRITermRef {

		public Object() {
			this.sectionFinder = new RegexSectionFinderSingle("::\\s(.*)",
					Pattern.DOTALL, 1);
		}

		// @Override
		// public TermIdentifier getTermIdentifier(Section<? extends SimpleTerm>
		// s) {
		// return new TermIdentifier(s.getText().trim());
		// }
	}

	@Override
	public Collection<Section<SimpleReference>> getAllReferences(Section<? extends ComplexDefinition> section) {
		return CompileUtils.getAllReferencesOfComplexDefinition(section);
	}

}

class ComplexIRIDefinitionCompileScript extends SimpleKnowledgeUnitCompileScript<ComplexIRIDefinitionMarkup> {

	@Override
	public void deleteFromRepository(Section<ComplexIRIDefinitionMarkup> section) {
		Rdf2GoCore.getInstance().removeStatementsForSection(section);
	}

	@Override
	public void insertIntoRepository(Section<ComplexIRIDefinitionMarkup> section) {
		List<Section<SimpleReference>> found = new ArrayList<Section<SimpleReference>>();
		Node subURI = null;
		Node predURI = null;
		Node objURI = null;

		Sections.findSuccessorsOfType(section, SimpleReference.class, found);
		Section<SimpleDefinition> subject = Sections.findSuccessor(section,
				SimpleDefinition.class);

		if (found.size() == 2) {

			Section<SimpleReference> predicate = found.get(0);
			Section<SimpleReference> object = found.get(1);

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

		Rdf2GoCore.getInstance().addStatement(section,
				subURI.asResource(), predURI.asURI(), objURI);

		// return new ArrayList<KDOMReportMessage>(0);

	}

}

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

import java.util.Collection;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermReference;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinderSingle;
import de.d3web.we.utils.SplitUtility;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.compile.utils.CompileUtils;

public class ComplexIRIDefinitionMarkup extends AbstractType implements ComplexDefinition<ComplexIRIDefinitionMarkup> {

	private static final String REGEX = "(.+)\\s(\\w+)::\\s(.+)$";

	private static final String REGEX_DEF = "^def\\s+";

	public ComplexIRIDefinitionMarkup() {
		this.sectionFinder = new RegexSectionFinder(REGEX_DEF + REGEX, Pattern.MULTILINE);

		this.addChildType(new DefType());
		this.addChildType(new DefinitionTerm());
		this.addChildType(new Predicate());
		this.addChildType(new Object());
	}

	@Override
	public Collection<Section<TermReference>> getAllReferences(
			Section<? extends ComplexDefinition<ComplexIRIDefinitionMarkup>> section) {
		return CompileUtils.getAllReferencesOfComplexDefinition(section);
	}

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

	class Predicate extends IncrementalTermReference<String> {
		public Predicate() {
			super(String.class);
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

	class Object extends IncrementalTermReference<String> {
		public Object() {
			super(String.class);
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

}

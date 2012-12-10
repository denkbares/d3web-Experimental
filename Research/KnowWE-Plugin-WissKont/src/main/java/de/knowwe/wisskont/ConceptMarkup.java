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

import java.util.regex.Pattern;

import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.support.Editable;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.11.2012
 */
public class ConceptMarkup extends AbstractType implements Editable {

	private final String REGEX;

	public ConceptMarkup() {
		String keyRegex = "(Konzept:)";
		REGEX = "(?i)^" + keyRegex + "\\s(.+)$";
		this.setSectionFinder(new RegexSectionFinder(REGEX,
				Pattern.MULTILINE));

		this.addChildType(new DefinitionTerm());
		this.addChildType(new KeyType(keyRegex));

		this.setRenderer(new DefaultMarkupRenderer());
		this.setIgnorePackageCompile(true);
	}

	@Override
	public String getName() {
		return "Konzept-Definition";
	}

	class DefinitionTerm extends IncrementalTermDefinition<String> {

		public DefinitionTerm() {
			super(String.class);
			this.setSectionFinder(new RegexSectionFinder(REGEX, 0, 2));
			this.setRenderer(new StyleRenderer("color:#19196C"));
		}

		@Override
		public String getTermName(Section<? extends SimpleTerm> s) {
			return Strings.unquote(s.getText().trim());
		}

	}
}

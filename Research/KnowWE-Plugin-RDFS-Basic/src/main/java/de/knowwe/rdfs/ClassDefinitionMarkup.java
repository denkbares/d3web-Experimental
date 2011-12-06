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
package de.knowwe.rdfs;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.KnowWETerm;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.rdfs.rendering.PreEnvRenderer;

public class ClassDefinitionMarkup extends AbstractType {

	private static final String CLASS_REGEX = "^Class:?\\s+(.*?)(\\(.*?\\))?$";

	public ClassDefinitionMarkup() {
		this.setSectionFinder(new RegexSectionFinder(CLASS_REGEX,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE, 0));

		this.addChildType(new ClassDef());

		this.setCustomRenderer(new PreEnvRenderer());
	}

	class ClassDef extends AbstractIRITermDefinition implements TypedTermDefinition {

		public ClassDef() {
			this.setSectionFinder(new RegexSectionFinder(CLASS_REGEX,
					0, 1));
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
			return s.getOriginalText();
		}

		@Override
		public Map<String, ? extends Object> getTypedTermInformation(
				Section<? extends TermDefinition> s) {
			// says that IRIs created with this markup have the type 'Class'
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(RDFSTermCategory.KEY, RDFSTermCategory.Class);
			return map;
		}

	}

}

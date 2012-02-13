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
package de.knowwe.rdfs.subclassingTree;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.SplitUtility;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.RegexSectionFinderSingle;
import de.knowwe.rdfs.AbstractIRITermDefinition;
import de.knowwe.rdfs.RDFSTermCategory;

/**
 * 
 * @author Jochen
 * @created 15.12.2011
 */
public class DashTreeClassDefinition extends AbstractType {

	private static final String REGEX = "^def\\s(.+)$";

	public DashTreeClassDefinition() {
		this.setSectionFinder(new RegexSectionFinder(REGEX,
				Pattern.MULTILINE));

		this.addChildType(new DefType());
		this.addChildType(new DefinitionTerm());
	}

	class DefType extends AbstractType {

		public DefType() {
			this.setSectionFinder(new RegexSectionFinderSingle("^def\\s+"));
			this.setCustomRenderer(new StyleRenderer("font-style:italic;"));
		}
	}

	class DefinitionTerm extends AbstractIRITermDefinition implements TypedTermDefinition {

		public DefinitionTerm() {
			this.setSectionFinder(new AllTextFinderTrimmed());
		}

		@Override
		public String getTermIdentifier(Section<? extends SimpleTerm> s) {
			return SplitUtility.unquote(s.getText().trim());
		}

		@Override
		public Map<String, ? extends Object> getTypedTermInformation(
				Section<? extends SimpleDefinition> s) {
			// says that URIs created with this markup have the type 'Class'
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(RDFSTermCategory.KEY, RDFSTermCategory.Class);
			return map;
		}
	}

}

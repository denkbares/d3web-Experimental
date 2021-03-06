/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.wikiObjectModel.types;

import java.util.List;

import de.knowwe.compile.object.TermDefinitionRenderer;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.rdfs.AbstractIRITermDefinition;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 09.07.2012
 */
public class SectionHeaderObjectDefinition extends AbstractIRITermDefinition {

	/**
	 * 
	 */
	public SectionHeaderObjectDefinition() {
		this.setSectionFinder(new SectionHeaderFinder());
		this.setRenderer(new SectionHeaderObjectDefinitionRenderer());
	}

	class SectionHeaderObjectDefinitionRenderer implements Renderer {

		private final Renderer r = new TermDefinitionRenderer<Term>(
				new ToolMenuDecoratingRenderer(
						new StyleRenderer("color:rgb(0, 0, 0)")));

		@Override
		public void render(Section<?> section, UserContext user, RenderResult string) {

			// render anchor that corresponds to term name / URI
			Section<SectionHeaderObjectDefinition> castedSection = Sections.cast(
					section,
					SectionHeaderObjectDefinition.getGenericClass());
			String termName = castedSection.get().getTermName(castedSection);
			string.appendHtml("<a name='"
					+ termName.substring(termName.indexOf("#") + 1) + "'>");
			string.appendHtml("</a>");

			// render tool stuff
			r.render(section, user, string);
		}
	}

	public static Class<SectionHeaderObjectDefinition> getGenericClass() {
		return SectionHeaderObjectDefinition.class;
	}

	class SectionHeaderFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
			String headerText = text;
			int index = 0;
			while (headerText.startsWith("!")) {
				index++;
				headerText = headerText.substring(1);
			}
			while (headerText.startsWith(" ")) {
				index++;
				headerText = headerText.substring(1);
			}
			return SectionFinderResult.singleItemList(index, text.length());
		}

	}

	@Override
	public String getTermName(Section<? extends Term> section) {
		return section.getTitle() + "#" + section.getText().trim();
	}

}

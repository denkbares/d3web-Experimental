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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.jspwiki.types.ImageType;
import de.knowwe.rdfs.AbstractKnowledgeUnitCompileScriptRDFS;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 11.07.2012
 */
public class CompiledImageTag extends AbstractKnowledgeUnitType<CompiledImageTag> {

	/**
	 * 
	 */
	public CompiledImageTag() {
		this.setCompileScript(new ImageCompileScript());

		// is plugged as only child into ImageType which does the parsing
		this.setSectionFinder(new AllTextSectionFinder());

		this.addChildType(new ImageTypeContent());
	}

	class ImageCompileScript extends AbstractKnowledgeUnitCompileScriptRDFS<CompiledImageTag> {

		@Override
		public void insertIntoRepository(Section<CompiledImageTag> section) {
			// TODO
			// System.out.println("writting image to rdf-repo:" +
			// section.toString());

			// System.out.println("src: " +
			// CompiledImageTag.getAttributeValue(section, "src"));
		}

	}

	/**
	 * Grabs xml attribute values out of the content string
	 * 
	 * @created 11.07.2012
	 * @param sec
	 * @param attributeName
	 * @return
	 */
	public static String getAttributeValue(Section<CompiledImageTag> sec, String attributeName) {
		Pattern p = Pattern.compile(attributeName + "\\w*?=\\w*?['|\"](.*?)['|\"]");
		Matcher m = p.matcher(Sections.findSuccessor(sec, ImageTypeContent.class).getText());
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	class ImageTypeContent extends AbstractType {

		public ImageTypeContent() {
			this.setSectionFinder(new RegexSectionFinder(
					Pattern.compile(ImageType.IMAGE_TAG_REGEX), 1));
		}
	}

}

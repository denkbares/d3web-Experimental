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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.LocationDependantKnowledgeUnit;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.jspwiki.types.ImageType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.AbstractKnowledgeUnitCompileScriptRDFS;
import de.knowwe.rdfs.wikiObjectModel.Utils;
import de.knowwe.rdfs.wikiObjectModel.WikiObjectModel;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 11.07.2012
 */
public class CompiledImageTag extends AbstractKnowledgeUnitType<CompiledImageTag> implements LocationDependantKnowledgeUnit {

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
			String source = CompiledImageTag.getAttributeValue(section, "src");
			String baseUrl = Environment.getInstance().getWikiConnector().getBaseUrl();
			String title = section.getTitle();

			try {
				title = URLEncoder.encode(title, "UTF-8");
				source = URLEncoder.encode(source, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// http://localhost:8080/KnowWE/attach/Test/flag_de.gif
			String imageURIString = baseUrl + "attach/" + title + "/" + source;

			URI imageURI = new URIImpl(imageURIString);
			URI targetURI = null;

			Section<SectionHeaderObjectDefinition<?>> contextSection = Utils.getContextSectionIfExisting(section);
			if (contextSection != null) {
				targetURI = new URIImpl(Utils.createAnchorURLHeaderDefinition(contextSection));
			}
			else {
				targetURI = new URIImpl(Utils.createURL(section.getTitle()));
			}

			// is an image
			Rdf2GoCore.getInstance().addStatement(section, imageURI,
					RDF.type,
					WikiObjectModel.WIKI_IMAGE);

			// image illustrates context concept
			Rdf2GoCore.getInstance().addStatement(section, imageURI,
					WikiObjectModel.ILLUSTRATES,
					targetURI);
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

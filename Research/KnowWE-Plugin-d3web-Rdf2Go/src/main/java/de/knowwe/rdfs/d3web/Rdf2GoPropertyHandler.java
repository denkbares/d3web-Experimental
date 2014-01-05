/*
 * Copyright (C) 2010 denkbares GmbH
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

package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.WordUtils;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.we.knowledgebase.D3webCompiler;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.d3web.property.PropertyContentType;
import de.knowwe.d3web.property.PropertyDeclarationHandler;
import de.knowwe.d3web.property.PropertyDeclarationType;
import de.knowwe.d3web.property.PropertyObjectReference;
import de.knowwe.d3web.property.PropertyType;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
 * Adds selected properties to the Rdf2GoCore..
 * 
 * @author Albrecht Striffler
 * @created 26.06.2013
 */
public class Rdf2GoPropertyHandler extends PropertyDeclarationHandler {

	@Override
	public Collection<Message> create(D3webCompiler compiler, Section<PropertyDeclarationType> section) {
		// get Property
		Section<PropertyType> propertySection = Sections.findSuccessor(section,
				PropertyType.class);
		if (propertySection == null) {
			return Messages.asList();
		}
		Property<?> property = propertySection.get().getProperty(propertySection);
		if (property == null
				|| !(property.equals(MMInfo.DESCRIPTION) || property.equals(MMInfo.PROMPT) || property.equals(MMInfo.LINK))) {
			return Messages.asList();
		}

		// get NamedObject
		Section<PropertyObjectReference> namendObjectSection = Sections.findSuccessor(section,
				PropertyObjectReference.class);
		if (namendObjectSection == null) {
			return Messages.asList();
		}
		List<NamedObject> objects = getNamedObjects(compiler, namendObjectSection);
		if (objects.isEmpty()) {
			return Messages.asList();
		}

		Locale locale = getLocale(section);

		// get content
		Section<PropertyContentType> contentSection = Sections.findSuccessor(section,
				PropertyContentType.class);
		if (contentSection == null) {
			return Messages.asList();
		}
		String content = contentSection.get().getPropertyContent(contentSection);
		if (content == null || content.trim().isEmpty()) {
			return Messages.asList();
		}
		Collection<Rdf2GoCompiler> rdf2GoCompilers = Rdf2GoD3webUtils.getRdf2GoCompilers(compiler,
				section);
		for (Rdf2GoCompiler rdf2GoCompiler : rdf2GoCompilers) {

			List<Statement> statements = new ArrayList<Statement>();
			Rdf2GoCore core = rdf2GoCompiler.getRdf2GoCore();
			;
			for (NamedObject namedObject : objects) {
				String externalForm = Rdf2GoD3webUtils.getIdentifierExternalForm(namedObject);
				// lns:Identifier lns:has[Property] "propertyString"@Locale
				URI identifierURI = core.createlocalURI(externalForm);
				URI propertyNameURI = core.createlocalURI(
						"has" + WordUtils.capitalize(property.getName()));
				Literal contentLiteral;
				if (locale == InfoStore.NO_LANGUAGE) {
					contentLiteral = core.createLiteral(content);
				}
				else {
					contentLiteral = core.createLanguageTaggedLiteral(content,
							locale.getLanguage());
				}
				Rdf2GoUtils.addStatement(core, identifierURI, propertyNameURI, contentLiteral,
						statements);

			}
			core.addStatements(compiler, Rdf2GoUtils.toArray(statements));
		}

		return Messages.asList();
	}
}

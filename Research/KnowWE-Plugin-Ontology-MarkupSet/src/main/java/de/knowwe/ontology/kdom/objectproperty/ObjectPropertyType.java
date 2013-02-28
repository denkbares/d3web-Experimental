/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.ontology.kdom.objectproperty;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.ontology.kdom.OntologyLineType;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ObjectPropertyType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("ObjectProperty");
		MARKUP.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		OntologyLineType lineType = new OntologyLineType();
		AbbreviatedPropertyDefinition propertyDefinition = new AbbreviatedPropertyDefinition();
		propertyDefinition.addSubtreeHandler(new AbbreviatedObjectPropertyHandler());
		lineType.addChildType(propertyDefinition);
		MARKUP.addContentType(lineType);
	}

	public ObjectPropertyType() {
		super(MARKUP);
	}

	private static class AbbreviatedObjectPropertyHandler extends SubtreeHandler<AbbreviatedPropertyDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<AbbreviatedPropertyDefinition> section) {
			Rdf2GoCore core = Rdf2GoCore.getInstance(article);
			String namespace = core.getNameSpaces().get(section.get().getAbbreviation(section));
			if (namespace == null) return Messages.noMessage();
			String property = section.get().getResource(section);
			URI propertyURI = core.createURI(namespace, property);
			core.addStatements(core.createStatement(propertyURI, RDF.type, RDF.Property));
			return Messages.noMessage();
		}
	}
}

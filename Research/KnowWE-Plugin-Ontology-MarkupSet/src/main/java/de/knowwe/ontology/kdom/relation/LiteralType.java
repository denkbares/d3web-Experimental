/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.ontology.kdom.relation;

import java.util.Collection;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.XSD;

import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.Patterns;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.rdf2go.Rdf2GoCore;

public class LiteralType extends AbstractType {

	public static final String XSD_URI_KEY = "xsdUriKey";
	public static final String XSD_PATTERN = "(?:\\^\\^xsd:(\\w+))";

	/**
	 * Either single quoted word and optionally xsd type or normal quote and
	 * mandatory xsd type.
	 */
	private static final String LITERAL_PATTERN =
			Patterns.SINGLE_QUOTED + XSD_PATTERN + "?"
					+ "|" + Patterns.QUOTED + XSD_PATTERN;

	public LiteralType() {
		this.setSectionFinder(new RegexSectionFinder(
				LITERAL_PATTERN));
		this.setRenderer(StyleRenderer.CONTENT);
		this.addChildType(new LiteralPart());
		this.addChildType(new XSDPart());
	}

	public Literal getLiteral(Rdf2GoCore core, Section<LiteralType> section) {
		Section<LiteralPart> literalPartSection = Sections.findChildOfType(section,
				LiteralPart.class);
		Section<XSDPart> xsdPartSection = Sections.findChildOfType(section, XSDPart.class);
		String literal = literalPartSection.get().getLiteral(literalPartSection);
		URI xsdType = null;
		if (xsdPartSection != null) {
			xsdType = xsdPartSection.get().getXSDType(xsdPartSection);
		}
		if (xsdType == null) {
			xsdType = deriveTypeFromLiteral(literal);
		}
		return core.createLiteral(literal, xsdType);
	}

	private URI deriveTypeFromLiteral(String literal) {
		try {
			Integer.parseInt(literal);
			return XSD._int;
		}
		catch (NumberFormatException e) {
			// do nothing;
		}
		try {
			Double.parseDouble(literal);
			return XSD._double;
		}
		catch (NumberFormatException e) {
			// do nothing;
		}
		// we don't know, just use string
		return XSD._string;
	}

	private static class LiteralPart extends AbstractType {

		public LiteralPart() {
			this.setSectionFinder(new RegexSectionFinder(Patterns.SINGLE_QUOTED + "|"
					+ Patterns.QUOTED));
		}

		public String getLiteral(Section<LiteralPart> section) {
			return Strings.unquote(section.getText(), '\'');
		}
	}

	private static class XSDPart extends AbstractType {

		public XSDPart() {
			this.setSectionFinder(new RegexSectionFinder(Pattern.compile(XSD_PATTERN), 1));
			this.addSubtreeHandler(Priority.HIGHER, new XSDHandler());
		}

		public URI getXSDType(Section<XSDPart> section) {
			return (URI) section.getSectionStore().getObject(XSD_URI_KEY);
		}
	}

	private static class XSDHandler extends SubtreeHandler<XSDPart> {

		@Override
		public Collection<Message> create(Article article, Section<XSDPart> section) {
			try {
				URIImpl xsdURI = new URIImpl(XSD.XSD_NS + section.getText(), true);
				section.getSectionStore().storeObject(XSD_URI_KEY, xsdURI);
			}
			catch (IllegalArgumentException e) {
				return Messages.asList(Messages.error(e.getMessage()));
			}
			return Messages.noMessage();
		}
	}

}
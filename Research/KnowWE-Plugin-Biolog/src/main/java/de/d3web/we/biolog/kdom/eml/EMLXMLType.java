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

package de.d3web.we.biolog.kdom.eml;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

import de.d3web.we.core.semantic.ISemanticCore;
import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.core.semantic.UpperOntology;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.xml.AbstractXMLType;
import de.knowwe.kdom.xml.GenericXMLObjectType;
import de.knowwe.kdom.xml.XMLContent;

/**
 * Root type recognizing the xml-representation of EML (Ecological Markup
 * Language)
 * 
 * @author Jochen
 * @created 16.09.2010
 */
public class EMLXMLType extends AbstractXMLType {

	public EMLXMLType() {
		super("ns4:eml");
	}

	@Override
	protected void init() {
		this.childrenTypes.add(new EMLContent());
		this.setCustomRenderer(new EMLRenderer());
		this.addSubtreeHandler(new EMLXMLTypeOWLSubTreeHandler());
	}

	private class EMLXMLTypeOWLSubTreeHandler extends OwlSubtreeHandler<EMLXMLType> {

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<EMLXMLType> s) {
			Message msg = null;
			IntermediateOwlObject io = new IntermediateOwlObject();
			ISemanticCore sc = SemanticCoreDelegator.getInstance(KnowWEEnvironment.getInstance());
			UpperOntology uo = sc.getUpper();
			OwlHelper helper = uo.getHelper();
			Section<EMLXMLType> eml = s;
			String date = null;
			Section<? extends AbstractXMLType> dateSec = AbstractXMLType
					.findSubSectionOfTag("calendarDate", eml);

			if (dateSec != null) {
				Sections.findChildOfType(dateSec, XMLContent.class).getText();
			}

			String abstractText = null;

			Section<? extends AbstractXMLType> abstractSec = AbstractXMLType
					.findSubSectionOfTag("abstract", eml);

			if (abstractSec != null) {
				Section<XMLContent> findChildOfType = Sections.findChildOfType(abstractSec,
						XMLContent.class);
				if (findChildOfType != null) {
					abstractText = findChildOfType
							.getText();
				}
			}

			String preName = null;

			Section<? extends AbstractXMLType> prenameSec = AbstractXMLType
					.findSubSectionOfTag("givenName", eml);
			if (prenameSec != null) {
				preName = Sections.findChildOfType(prenameSec, XMLContent.class)
						.getText();
			}

			String surName = null;

			Section<? extends AbstractXMLType> surNameSec = AbstractXMLType
					.findSubSectionOfTag("surName", eml);
			if (surNameSec != null) {
				surName = Sections.findChildOfType(surNameSec, XMLContent.class)
						.getText();
			}

			String methodDescription = null;

			Section<? extends AbstractXMLType> methodsSec = AbstractXMLType
					.findSubSectionOfTag("methods", eml);
			if (methodsSec != null) {
				methodDescription = Sections
						.findChildOfType(methodsSec, XMLContent.class).getText();
			}

			Set<Section<? extends AbstractXMLType>> keyWordSections = new HashSet<Section<? extends AbstractXMLType>>();
			AbstractXMLType.findSubSectionsOfTag("keyword",
					s, keyWordSections);

			String ns = sc.getNameSpaces().get("swrc");
			try {
				// BNode bnode = uo.getVf().createBNode(s.getId() + "");
				URI localURI = uo.getHelper().createlocalURI(s.getID());
				helper.attachTextOrigin(localURI, s, io);
				// String type = bte.getEntryType();
				io.addStatement(helper.createStatement(localURI, RDF.TYPE,
						helper.createURI(ns, "ResearchProject")));

				if (abstractText != null) {
					io.addStatement(helper.createStatement(localURI, helper
							.createURI(ns, "abstract"), helper
							.createLiteral(abstractText)));
				}
				if (date != null) io
							.addStatement(helper.createStatement(localURI,
									helper.createURI(ns, "year"), helper
											.createLiteral(date)));

				if (surName != null) {

					if (preName == null) preName = "";
					io.addStatement(helper.createStatement(localURI, helper
							.createURI(ns, "creator"), helper
							.createLiteral((preName + " " + surName).trim())));
				}
				if (methodDescription != null) io.addStatement(helper.createStatement(localURI,
						helper
								.createURI(ns, "description"), helper
								.createLiteral(methodDescription)));

				for (Section<? extends AbstractXMLType> cur : keyWordSections) {
					String[] split = Sections.findChildOfType(cur, XMLContent.class)
							.getText().split(";");
					for (String string : split) {
						io.addStatement(helper.createStatement(localURI, helper
								.createURI(ns, "keywords"), helper
								.createLiteral(string.trim().toLowerCase())));
					}

				}

			}
			catch (RepositoryException e) {
				msg = Messages.error(e.getMessage());
			}
			SemanticCoreDelegator.getInstance(KnowWEEnvironment.getInstance()).addStatements(
					io, s);

			return Collections.singletonList(msg);

		}

	}

	class EMLContent extends XMLContent {

		@Override
		protected void init() {
			this.childrenTypes.add(new GenericXMLObjectType());

		}

	}
}

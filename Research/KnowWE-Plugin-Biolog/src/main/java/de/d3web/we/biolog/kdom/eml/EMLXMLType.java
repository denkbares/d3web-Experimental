/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.we.biolog.kdom.eml;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.semantic.ISemanticCore;
import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SimpleMessageError;
import de.d3web.we.kdom.xml.AbstractXMLObjectType;
import de.d3web.we.kdom.xml.GenericXMLObjectType;
import de.d3web.we.kdom.xml.XMLContent;

/**
 * Root type recognizing the xml-representation of EML (Ecological Markup
 * Language)
 * 
 * @author Jochen
 * @created 16.09.2010
 */
public class EMLXMLType extends AbstractXMLObjectType {

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
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<EMLXMLType> s) {
			KDOMReportMessage msg = null;
			IntermediateOwlObject io = new IntermediateOwlObject();
			ISemanticCore sc = SemanticCoreDelegator.getInstance(KnowWEEnvironment.getInstance());
			UpperOntology uo = sc.getUpper();
			OwlHelper helper = uo.getHelper();
			Section<EMLXMLType> eml = s;
			String date = null;
			Section<? extends AbstractXMLObjectType> dateSec = AbstractXMLObjectType
					.findSubSectionOfTag("calendarDate", eml);

			if (dateSec != null) {
				dateSec.findChildOfType(XMLContent.class).getOriginalText();
			}

			String abstractText = null;

			Section<? extends AbstractXMLObjectType> abstractSec = AbstractXMLObjectType
					.findSubSectionOfTag("abstract", eml);

			if (abstractSec != null) {
				Section<XMLContent> findChildOfType = abstractSec.findChildOfType(XMLContent.class);
				if (findChildOfType != null) {
					abstractText = findChildOfType
							.getOriginalText();
				}
			}

			String preName = null;

			Section<? extends AbstractXMLObjectType> prenameSec = AbstractXMLObjectType
					.findSubSectionOfTag("givenName", eml);
			if (prenameSec != null) {
				preName = prenameSec.findChildOfType(XMLContent.class)
						.getOriginalText();
			}

			String surName = null;

			Section<? extends AbstractXMLObjectType> surNameSec = AbstractXMLObjectType
					.findSubSectionOfTag("surName", eml);
			if (surNameSec != null) {
				surName = surNameSec.findChildOfType(XMLContent.class)
						.getOriginalText();
			}

			String methodDescription = null;

			Section<? extends AbstractXMLObjectType> methodsSec = AbstractXMLObjectType
					.findSubSectionOfTag("methods", eml);
			if (methodsSec != null) {
				methodDescription = methodsSec
						.findChildOfType(XMLContent.class).getOriginalText();
			}

			Set<Section<? extends AbstractXMLObjectType>> keyWordSections = new HashSet<Section<? extends AbstractXMLObjectType>>();
			AbstractXMLObjectType.findSubSectionsOfTag("keyword",
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
				if (date != null)
					io
							.addStatement(helper.createStatement(localURI,
									helper.createURI(ns, "year"), helper
											.createLiteral(date)));

				if (surName != null) {

					if (preName == null) preName = "";
					io.addStatement(helper.createStatement(localURI, helper
							.createURI(ns, "creator"), helper
							.createLiteral((preName + " " + surName).trim())));
				}
				if (methodDescription != null)
					io.addStatement(helper.createStatement(localURI, helper
							.createURI(ns, "description"), helper
							.createLiteral(methodDescription)));

				for (Section<? extends AbstractXMLObjectType> cur : keyWordSections) {
					String[] split = cur.findChildOfType(XMLContent.class)
							.getOriginalText().split(";");
					for (String string : split) {
						io.addStatement(helper.createStatement(localURI, helper
								.createURI(ns, "keywords"), helper
								.createLiteral(string.trim().toLowerCase())));
					}

				}

			}
			catch (RepositoryException e) {
				msg = new SimpleMessageError(e.getMessage());
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

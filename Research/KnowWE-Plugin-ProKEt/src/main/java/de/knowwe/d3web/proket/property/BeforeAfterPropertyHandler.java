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

package de.knowwe.d3web.proket.property;

import java.util.Collection;

import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.d3web.property.PropertyObjectReference;
import de.knowwe.d3web.property.PropertyContentType;
import de.knowwe.d3web.property.PropertyDeclarationType;
import de.knowwe.d3web.property.PropertyType;

/**
 * Further checks some constraints with BEFORE and AFTER properties to add
 * proper error messages.
 * 
 * @author Albrecht Striffler
 * @created 20.09.2011
 */
public class BeforeAfterPropertyHandler extends D3webSubtreeHandler<PropertyDeclarationType> {

	@Override
	public Collection<Message> create(KnowWEArticle article, Section<PropertyDeclarationType> s) {
		// get Property
		Section<PropertyType> propertySection = Sections.findSuccessor(s,
				PropertyType.class);
		if (propertySection == null) {
			return Messages.asList();
		}
		Property<?> property = propertySection.get().getProperty(propertySection);
		if (property == null
				|| !(property.equals(ProKEtProperties.BEFORE) || property.equals(ProKEtProperties.AFTER))) {
			return Messages.asList();
		}

		// get NamedObject
		Section<PropertyObjectReference> namendObjectSection = Sections.findSuccessor(s,
				PropertyObjectReference.class);
		if (namendObjectSection == null) {
			return Messages.asList();
		}
		NamedObject object = namendObjectSection.get().getTermObject(article, namendObjectSection);
		if (object == null) {
			return Messages.asList();
		}

		// get content
		Section<PropertyContentType> contentSection = Sections.findSuccessor(s,
				PropertyContentType.class);
		if (contentSection == null) {
			return Messages.asList();
		}
		String content = contentSection.get().getPropertyContent(contentSection);
		if (content == null || content.trim().isEmpty()) {
			return Messages.asList();
		}
		if (!(object instanceof QuestionDate)) {
			return Messages.asList(Messages.error(
						"Properties BEFORE and AFTER can only be set for QuestionDates, '"
								+ object.getName() + "' is a " + object.getClass().getSimpleName()
								+ "."));
		}
		Question foundQuestion = getKB(article).getManager().searchQuestion(content);
		if (foundQuestion == null || !(foundQuestion instanceof QuestionDate)) {
			return Messages.asList(Messages.error(
					"Properties BEFORE and AFTER need to target other QuestionDates, '"
							+ content
							+ "' is "
							+ (foundQuestion == null
									? "not a Question"
									: "a " + foundQuestion.getClass().getSimpleName())
							+ "."));
		}
		return Messages.asList();
	}
}

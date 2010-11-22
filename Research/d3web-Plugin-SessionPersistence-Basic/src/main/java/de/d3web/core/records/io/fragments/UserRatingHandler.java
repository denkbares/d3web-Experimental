/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.core.records.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.UserRating;

/**
 * Handler for UserRatings
 * 
 * @author Reinhard Hatko
 * @created 22.11.2010
 */
public class UserRatingHandler implements FragmentHandler {

	private static final String elementName = "userRating";
	private static final String ratingElementName = "rating";
	private static final String evaluationElementName = "userRating";

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String rating = element.getElementsByTagName(ratingElementName).item(0).getTextContent();
		String evaluation = element.getElementsByTagName(evaluationElementName).item(0).getTextContent();

		return new UserRating(rating, evaluation);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		UserRating rating = (UserRating) object;
		Element element = doc.createElement(elementName);

		Element ratingElement = doc.createElement(ratingElementName);
		ratingElement.setTextContent(rating.getState().name());
		element.appendChild(ratingElement);

		Element evalElement = doc.createElement(evaluationElementName);
		evalElement.setTextContent(rating.getEvaluation().name());
		element.appendChild(evalElement);

		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(elementName);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof UserRating;
	}

}

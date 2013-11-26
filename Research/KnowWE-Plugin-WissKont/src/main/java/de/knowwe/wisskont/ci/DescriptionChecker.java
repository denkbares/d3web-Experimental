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
package de.knowwe.wisskont.ci;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.MessageObject;
import de.d3web.testing.TestParameter.Mode;
import de.d3web.testing.TestParameter.Type;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.jspwiki.types.HeaderType;
import de.knowwe.wisskont.ConceptMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 18.10.2013
 */
public class DescriptionChecker extends AbstractTest<Article> {

	/**
	 * 
	 */
	
	public DescriptionChecker() {
		this.addParameter(
				"Minimale Anzahl notwendiger Zeichen für Beschreibung",
				Type.Number,
				Mode.Mandatory,
				"Gibt die Schwelle der Länge der Beschreibung in Zeichen an, bei deren Unterschreitung der Test anschlägt.");
	}

	@Override
	public Message execute(Article testObject, String[] args, String[]... ignores) throws InterruptedException {
		int minDescriptionLength = Integer.parseInt(args[0]);

		Section<ConceptMarkup> def = Sections.findSuccessor(testObject.getRootSection(),
				ConceptMarkup.class);
		if (def != null) {
			List<Section<HeaderType>> findSuccessorsOfType = Sections.findSuccessorsOfType(
					testObject.getRootSection(), HeaderType.class);
			Section<HeaderType> descriptionHeader = null;
			for (Section<HeaderType> section : findSuccessorsOfType) {
				if (section.getText().contains("Beschreibung")) {

					descriptionHeader = section;
					break;
				}
			}
			if (descriptionHeader != null) {

				int offSetFromFatherText = descriptionHeader.getOffsetInParent();
				int descriptionBeginning = offSetFromFatherText
						+ descriptionHeader.getText().length();
				int articleLength = testObject.getRootSection().getText().length();
				if (articleLength - descriptionBeginning < minDescriptionLength) {
					Collection<MessageObject> mos = new ArrayList<MessageObject>();
					mos.add(new MessageObject(testObject.getTitle(), Article.class));
					Message m = new Message(
								Message.Type.FAILURE,
								"Folgender Begriff ist nicht oder nicht ausführlich genug beschrieben: "
										+ testObject.getTitle(), mos);
					return m;

				}
			}
			else {
				Collection<MessageObject> mos = new ArrayList<MessageObject>();
				mos.add(new MessageObject(testObject.getTitle(), Article.class));
				Message m = new Message(
							Message.Type.FAILURE,
							"Folgender Begriff einhält keine Beschreibung (oder keinen entsprechend ausgezeichneten Abschnitt): "
									+ testObject.getTitle(), mos);
				return m;

			}
		}
		return Message.SUCCESS;
	}

	@Override
	public Class<Article> getTestObjectClass() {
		return Article.class;
	}

	@Override
	public String getDescription() {
		return "Checks whether there is some description text existing for a concept page and fails if not.";
	}

}

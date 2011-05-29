/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.casetrain.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.casetrain.message.InvalidArgumentError;
import de.knowwe.casetrain.message.MissingAttributeWarning;
import de.knowwe.casetrain.message.MissingComponentError;
import de.knowwe.casetrain.message.MissingComponentWarning;
import de.knowwe.casetrain.renderer.MouseOverTitleRenderer;
import de.knowwe.casetrain.type.general.SubblockMarkup;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.Video;


/**
 * 
 * @author Johannes Dienst
 * @created 12.05.2011
 */
public class Question extends SubblockMarkup {

	private final String FRAGE_TYPE = "Fragetyp";
	private final String FRAGE_TEXT = "Fragetext";
	private final String FRAGE_GEWICHT = "Fragegewicht";
	private final String FRAGE_GEWICHT_WRONG = "Fragegewicht kleiner 0";

	public Question() {
		super("Frage");
		this.addChildType(new Title());
		this.addContentType(new Image());
		this.addContentType(new Video());
		this.addContentType(new QuestionWeight());
		this.addContentType(new QuestionType());
		this.addContentType(new QuestionText());

		this.addSubtreeHandler(new GeneralSubtreeHandler<Question>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Question> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				Section<QuestionWeight> fragegewichtSection = Sections.findSuccessor(s,
						QuestionWeight.class);
				if (fragegewichtSection == null) {
					messages.add(new MissingAttributeWarning(FRAGE_GEWICHT));
				} else if(Double.valueOf(fragegewichtSection.getOriginalText()) < 0) {
					messages.add(new InvalidArgumentError(FRAGE_GEWICHT_WRONG));
				}

				Section<QuestionType> typSection = Sections.findSuccessor(s, QuestionType.class);
				if (typSection == null) {
					messages.add(new MissingComponentError(FRAGE_TYPE));
				}

				Section<QuestionText> fragetextSection = Sections.findSuccessor(s,
						QuestionText.class);
				if (fragetextSection == null) {
					messages.add(new MissingComponentWarning(FRAGE_TEXT));
				}

				return messages;
			}
		});
	}

	public class QuestionWeight extends AbstractType {

		public QuestionWeight() {
			this.setSectionFinder(new RegexSectionFinder("[-]?[0-9]+"));
			this.setCustomRenderer(MouseOverTitleRenderer.getInstance());
		}
	}

	public class QuestionType extends AbstractType {

		public QuestionType() {
			ConstraintSectionFinder csf = new ConstraintSectionFinder(
					new RegexSectionFinder(
							AnswersBlockValidator.getInstance().getRegexAsString()));
			csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
			this.setCustomRenderer(MouseOverTitleRenderer.getInstance());
		}

	}

	public class QuestionText extends AbstractType {

		public QuestionText() {
			this.setSectionFinder(new RegexSectionFinder("([\\w]{1}[\\W]?[ ]?)+\\?"));
			this.setCustomRenderer(MouseOverTitleRenderer.getInstance());
		}
	}

}

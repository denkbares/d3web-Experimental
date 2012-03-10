/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.casetrain.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import de.knowwe.casetrain.renderer.MouseOverTitleRenderer;
import de.knowwe.casetrain.renderer.SpanClassRenderer;
import de.knowwe.casetrain.type.general.SubblockMarkup;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.Video;
import de.knowwe.casetrain.util.Utils;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.ExactlyOneFindingConstraint;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;

/**
 * 
 * @author Johannes Dienst
 * @created 12.05.2011
 */
public class Question extends SubblockMarkup {

	ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

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
			public Collection<Message> create(Article article, Section<Question> s) {

				List<Message> messages = new ArrayList<Message>(0);

				Section<QuestionWeight> fragegewichtSection = Sections.findSuccessor(s,
						QuestionWeight.class);
				if (fragegewichtSection == null) {
					messages.add(
							Utils.missingAttributeWarning(
									bundle.getString("QUESTION_WEIGHT")));
				}
				else if (Double.valueOf(fragegewichtSection.getText()) < 0) {
					messages.add(
							Utils.invalidArgumentError(
									bundle.getString("QUESTION_WEIGHT_WRONG")));
				}

				Section<QuestionType> typSection = Sections.findSuccessor(s, QuestionType.class);
				if (typSection == null) {
					messages.add(
							Utils.missingComponentError(
									bundle.getString("QUESTION_TYPE")));
				}

				Section<QuestionText> fragetextSection = Sections.findSuccessor(s,
						QuestionText.class);
				if (fragetextSection == null) {
					messages.add(Utils.missingComponentWarning(
									bundle.getString("QUESTION_TEXT")));
				}

				return messages;
			}
		});
	}

	public class QuestionWeight extends AbstractType {

		public QuestionWeight() {
			this.setSectionFinder(new RegexSectionFinder("[-]?[0-9]+"));
			this.setRenderer(new SpanClassRenderer(SpanClassRenderer.META_KEY));
		}
	}

	public class QuestionType extends AbstractType {

		public QuestionType() {
			ConstraintSectionFinder csf = new ConstraintSectionFinder(
					new RegexSectionFinder(
							AnswersBlockValidator.getInstance().getRegexAsString()));
			csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
			this.setRenderer(new SpanClassRenderer(SpanClassRenderer.META_KEY));
		}

	}

	public class QuestionText extends AbstractType {

		public QuestionText() {
			this.setSectionFinder(new RegexSectionFinder("([\\w]{1}[\\W]?[ ]?)+\\?"));
			this.setRenderer(MouseOverTitleRenderer.getInstance());
		}
	}

}

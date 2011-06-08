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
package de.knowwe.casetrain.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMNotice;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.casetrain.info.AnswersBlock;
import de.knowwe.casetrain.info.AnswersBlockValidator;
import de.knowwe.casetrain.info.Explanation;
import de.knowwe.casetrain.info.Hint;
import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.info.Question;
import de.knowwe.casetrain.info.Question.QuestionType;
import de.knowwe.casetrain.message.InvalidArgumentError;
import de.knowwe.casetrain.message.MissingComponentError;
import de.knowwe.casetrain.message.MissingComponentWarning;
import de.knowwe.casetrain.message.MissingContentWarning;
import de.knowwe.casetrain.type.general.BlockMarkupContent;
import de.knowwe.casetrain.type.general.BlockMarkupContentRenderer;
import de.knowwe.casetrain.type.general.BlockMarkupType;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Audio;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.Link;
import de.knowwe.casetrain.type.multimedia.Video;
import de.knowwe.casetrain.util.Utils;


/**
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class Evaluation extends BlockMarkupType {

	ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	public Evaluation() {
		super("Evaluation");
		this.addContentType(new Question());
		this.addContentType(new AnswersBlock());
		this.addContentType(new EvaluationEnd());
		this.addContentType(new Image());
		this.addContentType(new Video());
		this.addContentType(new Link());
		this.addContentType(new Audio());

		this.setCustomRenderer(new KnowWEDomRenderer<BlockMarkupType>() {

			@Override
			public void render(KnowWEArticle article, Section<BlockMarkupType> sec,
					UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML("<div class='"
						+ sec.get().getCSSClass()
						+ "'>"));
				string.append(KnowWEUtils.maskHTML("<div class='Evaluationstart'></div>"));

				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMError.class), string);

				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMWarning.class), string);

				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMNotice.class), string);
				Section<BlockMarkupContent> con =
					Sections.findSuccessor(sec, BlockMarkupContent.class);
				BlockMarkupContentRenderer.getInstance().render(article, con, user, string);
				string.append(KnowWEUtils.maskHTML("<div class='Evaluationend'></div>"));
				string.append(KnowWEUtils.maskHTML("</div>"));
			}
		});

		this.addSubtreeHandler(new GeneralSubtreeHandler<Evaluation>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Evaluation> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				/*
				 *  Evaluation has no content if:
				 *  - Only title as children and no other children
				 *  - It has no children at all
				 */
				List<Section<? extends Type>> blockMarkupChildren =
					Sections.findSuccessor(s, BlockMarkupContent.class).getChildren();
				if ( blockMarkupChildren.size() == 0 ) {
					messages.add(new MissingContentWarning(Info.class.getSimpleName()));
				} else {
					messages.addAll(this.testQuestionAnswerComposition(s));
				}

				return messages;
			}

			/**
			 * Tests the following:
			 * {@link Question} has {@link AnswersBlock)/{@link Explanation}
			 * 
			 * TODO Does not test if {@link AnswersBlock} has {@link Question}!
			 * Is this necessary?
			 * 
			 * @created 28.04.2011
			 * @param s
			 * @return
			 */
			@SuppressWarnings("unchecked")
			private List<KDOMReportMessage> testQuestionAnswerComposition(Section<Evaluation> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				List<Section<Question>> found = new ArrayList<Section<Question>>();
				Sections.findSuccessorsOfType(s, Question.class, found);
				if (found.isEmpty()) {
					messages.add(new MissingComponentWarning(Question.class.getSimpleName()));
					return messages;
				}

				/*
				 *  check children if the right order is given or some
				 *  thing is missing. Right is:
				 *  Hint* Question Hint* AnswersBlock Hint* Explanation
				 * 
				 *  Also validates the given AnswerBlock.
				 * 
				 */
				List<Section<? extends Type>> children =
					Sections.findSuccessor(s, BlockMarkupContent.class).getChildren();
				Section<? extends Type> actual = null;
				boolean antwortenMissing = true;
				boolean moreAnswersBlocks = false;
				for (Section<?> sec : children) {

					if (sec.get().isType(Hint.class) || sec.get().isType(Title.class)
							|| sec.get().isType(PlainText.class)
							|| sec.get().isType(Link.class)
							|| sec.get().isType(Explanation.class)) {
						continue;
					}

					if ( sec.get().isType(Question.class) ) {

						if (actual == null) {
							actual = sec;
							continue;
						}
						this.validateQuestion(actual, antwortenMissing,
								moreAnswersBlocks, messages);
						antwortenMissing = true;
						moreAnswersBlocks = false;
						continue;
					}

					if (sec.get().isType(AnswersBlock.class)) {
						// test if multiple Antworten are possible
						// Only by UMW,OMW,MN
						if (!antwortenMissing) {
							moreAnswersBlocks = true;
							String typ =
								Sections.findSuccessor(actual, QuestionType.class)
								.getOriginalText().trim();
							if ( !(AnswersBlockValidator.getInstance()
									.getTypesMultiple().contains(typ))) {
								messages.add(
										new InvalidArgumentError(
												bundle.getString("NO_MULTIPLE_ANSWERS")
												+typ)
								);
							}

						}
						antwortenMissing = false;
						AnswersBlockValidator.getInstance().
						validateAnswersBlock((Section<Question>) actual,
								(Section<AnswersBlock>) sec, messages, true);
					}

				}

				this.validateQuestion(actual, antwortenMissing,
						moreAnswersBlocks, messages);
				return messages;
			}

			private void validateQuestion(Section<? extends Type> actual,
					boolean antwortenMissing, boolean moreAnswersBlocks,
					List<KDOMReportMessage> messages) {

				if (actual == null) {
					messages.add(
							new MissingComponentError(
									Question.class.getSimpleName()));
					return;
				}

				if (antwortenMissing) {
					messages.add(
							new MissingComponentWarning(
									AnswersBlock.class.getSimpleName()));
				}
				String typ =
					Sections.findSuccessor(actual, QuestionType.class)
					.getOriginalText().trim();
				if (!moreAnswersBlocks && AnswersBlockValidator.getInstance()
						.getTypesMultiple().contains(typ)) {
					messages.add(
							new MissingComponentError(
									bundle.getString("MIN_TWO_BLOCKS")+typ));
				}

			}

		});
	}
}

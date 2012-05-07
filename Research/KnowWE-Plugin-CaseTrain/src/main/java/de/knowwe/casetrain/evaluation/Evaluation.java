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
package de.knowwe.casetrain.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import de.knowwe.casetrain.info.AnswersBlock;
import de.knowwe.casetrain.info.AnswersBlockValidator;
import de.knowwe.casetrain.info.Explanation;
import de.knowwe.casetrain.info.Hint;
import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.info.Question;
import de.knowwe.casetrain.info.Question.QuestionType;
import de.knowwe.casetrain.type.general.BlockMarkupContent;
import de.knowwe.casetrain.type.general.BlockMarkupContentRenderer;
import de.knowwe.casetrain.type.general.BlockMarkupType;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Audio;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.Link;
import de.knowwe.casetrain.type.multimedia.Video;
import de.knowwe.casetrain.util.Utils;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;

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

		this.setRenderer(new Renderer() {

			@Override
			public void render(Section<?> sec, UserContext user,
					StringBuilder string) {
				string.append(Strings.maskHTML("<div class='"
						+ ((Evaluation) sec.get()).getCSSClass()
						+ "'>"));
				string.append(Strings.maskHTML("<div class='Evaluationstart'></div>"));
				Article article = KnowWEUtils.getCompilingArticles(sec).iterator().next();
				Utils.renderKDOMReportMessageBlock(
						Messages.getErrors(Messages.getMessagesFromSubtree(
								article, sec)), string);

				Utils.renderKDOMReportMessageBlock(
						Messages.getWarnings(Messages.getMessagesFromSubtree(
								article, sec)), string);

				Utils.renderKDOMReportMessageBlock(
						Messages.getNotices(Messages.getMessagesFromSubtree(
								article, sec)), string);
				Section<BlockMarkupContent> con =
						Sections.findSuccessor(sec, BlockMarkupContent.class);
				BlockMarkupContentRenderer.getInstance().render(con, user, string);
				string.append(Strings.maskHTML("<div class='Evaluationend'></div>"));
				string.append(Strings.maskHTML("</div>"));
			}
		});

		this.addSubtreeHandler(new GeneralSubtreeHandler<Evaluation>() {

			@Override
			public Collection<Message> create(Article article, Section<Evaluation> s) {

				List<Message> messages = new ArrayList<Message>(0);

				/*
				 * Evaluation has no content if: - Only title as children and no
				 * other children - It has no children at all
				 */
				List<Section<? extends Type>> blockMarkupChildren =
						Sections.findSuccessor(s, BlockMarkupContent.class).getChildren();
				if (blockMarkupChildren.size() == 0) {
					messages.add(Utils.missingContentWarning(Info.class.getSimpleName()));
				}
				else {
					messages.addAll(this.testQuestionAnswerComposition(s));
				}

				return messages;
			}

/**
			 * Tests the following: {@link Question} has {@link AnswersBlock)/
			 * 
			 * @link Explanation}
			 * 
			 *       TODO Does not test if {@link AnswersBlock} has
			 *       {@link Question}! Is this necessary?
			 * 
			 * @created 28.04.2011
			 * @param s
			 * @return
			 */
			@SuppressWarnings("unchecked")
			private List<Message> testQuestionAnswerComposition(Section<Evaluation> s) {

				List<Message> messages = new ArrayList<Message>(0);

				List<Section<Question>> found = new ArrayList<Section<Question>>();
				Sections.findSuccessorsOfType(s, Question.class, found);
				if (found.isEmpty()) {
					messages.add(Utils.missingComponentWarning(Question.class.getSimpleName()));
					return messages;
				}

				/*
				 * check children if the right order is given or some thing is
				 * missing. Right is: Hint* Question Hint* AnswersBlock Hint*
				 * Explanation
				 * 
				 * Also validates the given AnswerBlock.
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

					if (sec.get().isType(Question.class)) {

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
											.getText().trim();
							if (!(AnswersBlockValidator.getInstance()
									.getTypesMultiple().contains(typ))) {
								messages.add(Utils.invalidArgumentError(
										bundle.getString("NO_MULTIPLE_ANSWERS")
												+ typ)
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
					List<Message> messages) {

				if (actual == null) {
					messages.add(
							Utils.missingComponentError(
									Question.class.getSimpleName()));
					return;
				}

				if (antwortenMissing) {
					messages.add(Utils.missingComponentWarning(
							AnswersBlock.class.getSimpleName()));
				}
				String typ =
						Sections.findSuccessor(actual, QuestionType.class)
								.getText().trim();
				if (!moreAnswersBlocks && AnswersBlockValidator.getInstance()
						.getTypesMultiple().contains(typ)) {
					messages.add(
							Utils.missingComponentError(
									bundle.getString("MIN_TWO_BLOCKS") + typ));
				}

			}

		});
	}
}

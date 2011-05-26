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

package de.knowwe.casetrain.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMNotice;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.casetrain.info.Question.QuestionType;
import de.knowwe.casetrain.message.InvalidArgumentError;
import de.knowwe.casetrain.message.MissingComponentError;
import de.knowwe.casetrain.message.MissingComponentWarning;
import de.knowwe.casetrain.message.MissingContentWarning;
import de.knowwe.casetrain.renderer.MouseOverTitleRenderer;
import de.knowwe.casetrain.type.Closure;
import de.knowwe.casetrain.type.Introduction;
import de.knowwe.casetrain.type.general.BlockMarkupContent;
import de.knowwe.casetrain.type.general.BlockMarkupType;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.Video;
import de.knowwe.casetrain.util.Utils;

/**
 * 
 * One part of the case-markup-structure of the casetrain-wiki-format This is
 * the main content block containing numerous subtypes.
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class Info extends BlockMarkupType {

	public static final String FRAGE = "Frage";
	public static final String EINLEITUNG = "Einleitung";
	public static final String ABSCHLUSS = "Abschluss";
	public static final String ERKLAERUNG = "Erklaerung";
	public static final String ANTWORTEN = "Antworten";
	public static final String ABSCHNITT = "Abschnitt";

	public Info() {
		super("Info");
		this.addContentType(new Hint());
		this.addContentType(new Question());
		this.addContentType(new AnswersBlock());
		this.addContentType(new Explanation());
		this.addContentType(new Image());
		this.addContentType(new Video());

		this.setCustomRenderer(new KnowWEDomRenderer<BlockMarkupType>() {

			@Override
			public void render(KnowWEArticle article, Section<BlockMarkupType> sec, UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML("<div class='"
						+ sec.get().getCSSClass()
						+ "'>"));
				string.append(KnowWEUtils.maskHTML("<div class='Infostart'></div>"));

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
				MouseOverTitleRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("<div class='Infoend'></div>"));
				string.append(KnowWEUtils.maskHTML("</div>"));
			}
		});

		this.addSubtreeHandler(new GeneralSubtreeHandler<Info>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Info> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				Section<Title> title = Sections.findSuccessor(s, Title.class);
				if (title == null) {
					messages.add(new MissingComponentError(Title.TITLE));
				}

				/*
				 *  Info has no content if:
				 *  - Only title as children and no other children
				 *  - It has no children at all
				 */
				List<Section<? extends Type>> blockMarkupChildren =
					Sections.findSuccessor(s, BlockMarkupContent.class).getChildren();
				if ( ((title != null) && (blockMarkupChildren.size() == 1))
						|| blockMarkupChildren.size() == 0) {
					messages.add(new MissingContentWarning(Info.ABSCHNITT));
				} else {
					messages.addAll(this.testQuestionAnswerComposition(s));
				}



				// TODO: This is right, as long as a Page contains ONLY ONE Info
				Section<Introduction> einleitung = Sections.findSuccessor(s.getArticle().getSection(),
						Introduction.class);
				if (einleitung == null) {
					messages.add(new MissingComponentError(EINLEITUNG));
				}
				Section<Closure> abschluss = Sections.findSuccessor(s.getArticle().getSection(),
						Closure.class);
				if (abschluss == null) {
					messages.add(new MissingComponentError(ABSCHLUSS));
				}
				/////////////////////////////////////////////////////////////////


				return messages;
			}

			/**
			 * Tests the following:
			 * Frage has Antworten/Erklaerung
			 * 
			 * TODO Does not test if Antworten has Frage! Is this necessary?
			 * 
			 * @created 28.04.2011
			 * @param s
			 * @return
			 */
			@SuppressWarnings("unchecked")
			private List<KDOMReportMessage> testQuestionAnswerComposition(Section<Info> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				List<Section<Question>> found = new ArrayList<Section<Question>>();
				Sections.findSuccessorsOfType(s, Question.class, found);
				if (found.isEmpty()) {
					messages.add(new MissingComponentWarning(FRAGE));
					return messages;
				}

				/*
				 *  check children if the right order is given or some
				 *  thing is missing. Right is:
				 *  Hinweis* Frage Hinweis* Antworten Hinweis* Erklaerung
				 * 
				 *  Also validates the given Antworten-block for:
				 *   - frage hat nur eine antwortmöglichkeit
				 *   - frage hat keine richtige antwortmöglichkeit
				 *   - frage hat keine falsche antwortmöglichkeit
				 * 
				 */
				List<Section<? extends Type>> children =
					Sections.findSuccessor(s, BlockMarkupContent.class).getChildren();
				Section<? extends Type> actual = null;
				boolean erklMissing = true;
				boolean antwortenMissing = true;
				for (Section<? extends Type> sec : children) {
					if (sec.get().isType(Question.class)) {
						if(actual == null) {
							actual = sec;
							continue;
						}
						actual = sec;
						if (erklMissing) {
							messages.add(new MissingComponentWarning(ERKLAERUNG));
						}
						if (antwortenMissing) {
							messages.add(new MissingComponentWarning(ANTWORTEN));
						}
						erklMissing = true;
						antwortenMissing = true;
						continue;
					}
					if (sec.get().isType(Hint.class)) {
						continue;
					}
					if (sec.get().isType(Explanation.class)) {
						erklMissing = false;
						continue;
					}
					if (sec.get().isType(AnswersBlock.class)) {
						// test if multiple Antworten are possible
						// Only by UMW,OMW,MN
						if (!antwortenMissing) {
							String typ = Sections.findSuccessor(actual, QuestionType.class).getOriginalText().trim();
							if ( !(AnswerValidator.getInstance().getTypesMultiple().contains(typ))) {
								messages.add(new InvalidArgumentError("Mehrfache Antworten bei diesem FrageTyp nicht zulässig: "+typ));
							}

						}
						antwortenMissing = false;
						AnswerValidator.getInstance().
						validateAntwortenBlock((Section<Question>) actual, (Section<AnswersBlock>) sec, messages);
						continue;
					}
				}
				return messages;
			}
		});
	}

}

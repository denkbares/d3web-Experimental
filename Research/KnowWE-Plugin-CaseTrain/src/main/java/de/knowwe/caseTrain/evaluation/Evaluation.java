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
package de.knowwe.caseTrain.evaluation;

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
import de.knowwe.caseTrain.info.Antworten;
import de.knowwe.caseTrain.info.AntwortenKorrektheitChecker;
import de.knowwe.caseTrain.info.Frage;
import de.knowwe.caseTrain.info.Frage.FrageTyp;
import de.knowwe.caseTrain.info.Info;
import de.knowwe.caseTrain.message.InvalidArgumentError;
import de.knowwe.caseTrain.message.MissingComponentWarning;
import de.knowwe.caseTrain.message.MissingContentWarning;
import de.knowwe.caseTrain.renderer.MouseOverTitleRenderer;
import de.knowwe.caseTrain.type.general.BlockMarkupContent;
import de.knowwe.caseTrain.type.general.BlockMarkupType;
import de.knowwe.caseTrain.util.Utils;


/**
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class Evaluation extends BlockMarkupType {

	public Evaluation() {
		super("Evaluation");
		this.addContentType(new Frage());
		this.addContentType(new Antworten());
		this.addContentType(new EvaluationEnd());

		this.setCustomRenderer(new KnowWEDomRenderer<BlockMarkupType>() {

			@Override
			public void render(KnowWEArticle article, Section<BlockMarkupType> sec, UserContext user, StringBuilder string) {
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
				MouseOverTitleRenderer.getInstance().render(article, sec, user, string);
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
					messages.add(new MissingContentWarning(Info.ABSCHNITT));
				} else {
					messages.addAll(this.testQuestionAnswerComposition(s));
				}

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
			private List<KDOMReportMessage> testQuestionAnswerComposition(Section<Evaluation> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				List<Section<Frage>> found = new ArrayList<Section<Frage>>();
				Sections.findSuccessorsOfType(s, Frage.class, found);
				if (found.isEmpty()) {
					messages.add(new MissingComponentWarning(Info.FRAGE));
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
				boolean antwortenMissing = true;
				for (Section<? extends Type> sec : children) {
					if (sec.get().isType(Frage.class)) {
						if(actual == null) {
							actual = sec;
							continue;
						}
						actual = sec;
						if (antwortenMissing) {
							messages.add(new MissingComponentWarning(Info.ANTWORTEN));
						}
						antwortenMissing = true;
						continue;
					}
					if (sec.get().isType(Antworten.class)) {
						// test if multiple Antworten are possible
						// Only by UMW,OMW,MN
						if (!antwortenMissing) {
							String typ =
								Sections.findSuccessor(actual, FrageTyp.class).
								getOriginalText().trim();
							if (!(AntwortenKorrektheitChecker.getInstance()
									.getTypesMultiple().contains(typ))) {
								messages.add(new InvalidArgumentError(
										"Mehrfache Antworten bei diesem FrageTyp nicht zulässig: "
										+ typ));
							}

						}
						antwortenMissing = false;
						AntwortenKorrektheitChecker.getInstance().
						validateAntwortenBlock((Section<Frage>) actual,
								(Section<Antworten>) sec, messages);
						continue;
					}
				}
				return messages;
			}
		});
	}
}

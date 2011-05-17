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

package de.knowwe.caseTrain.info;

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
import de.knowwe.caseTrain.info.Frage.FrageTyp;
import de.knowwe.caseTrain.message.InvalidArgumentError;
import de.knowwe.caseTrain.message.MissingComponentError;
import de.knowwe.caseTrain.message.MissingComponentWarning;
import de.knowwe.caseTrain.message.MissingContentWarning;
import de.knowwe.caseTrain.renderer.MouseOverTitleRenderer;
import de.knowwe.caseTrain.type.Abschluss;
import de.knowwe.caseTrain.type.Einleitung;
import de.knowwe.caseTrain.type.general.BlockMarkupContent;
import de.knowwe.caseTrain.type.general.BlockMarkupType;
import de.knowwe.caseTrain.type.general.Title;
import de.knowwe.caseTrain.type.multimedia.Bild;
import de.knowwe.caseTrain.type.multimedia.Video;
import de.knowwe.caseTrain.util.Utils;

/**
 * 
 * One part of the case-markup-structure of the caseTrain-wiki-format This is
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
		this.addContentType(new Hinweis());
		this.addContentType(new Frage());
		this.addContentType(new Antworten());
		this.addContentType(new Erklaerung());
		this.addContentType(new Bild());
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
				Section<Einleitung> einleitung = Sections.findSuccessor(s.getArticle().getSection(),
						Einleitung.class);
				if (einleitung == null) {
					messages.add(new MissingComponentError(EINLEITUNG));
				}
				Section<Abschluss> abschluss = Sections.findSuccessor(s.getArticle().getSection(),
						Abschluss.class);
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

				List<Section<Frage>> found = new ArrayList<Section<Frage>>();
				Sections.findSuccessorsOfType(s, Frage.class, found);
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
					if (sec.get().isType(Frage.class)) {
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
					if (sec.get().isType(Hinweis.class)) {
						continue;
					}
					if (sec.get().isType(Erklaerung.class)) {
						erklMissing = false;
						continue;
					}
					if (sec.get().isType(Antworten.class)) {
						// test if multiple Antworten are possible
						// Only by UMW,OMW,MN
						if (!antwortenMissing) {
							String typ = Sections.findSuccessor(actual, FrageTyp.class).getOriginalText().trim();
							if ( !(AntwortenKorrektheitChecker.getInstance().getTypesMultiple().contains(typ))) {
								messages.add(new InvalidArgumentError("Mehrfache Antworten bei diesem FrageTyp nicht zulässig: "+typ));
							}

						}
						antwortenMissing = false;
						AntwortenKorrektheitChecker.getInstance().
						validateAntwortenBlock((Section<Frage>) actual, (Section<Antworten>) sec, messages);
						continue;
					}
				}
				return messages;
			}
		});
	}

}

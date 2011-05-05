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

package de.knowwe.caseTrain.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMNotice;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.caseTrain.message.InvalidArgumentError;
import de.knowwe.caseTrain.message.MissingAttributeWarning;
import de.knowwe.caseTrain.message.MissingComponentError;
import de.knowwe.caseTrain.message.MissingComponentWarning;
import de.knowwe.caseTrain.message.MissingContentWarning;
import de.knowwe.caseTrain.renderer.DivStyleClassRenderer;
import de.knowwe.caseTrain.renderer.MouseOverTitleRenderer;
import de.knowwe.caseTrain.type.general.Bild;
import de.knowwe.caseTrain.type.general.BlockMarkupType;
import de.knowwe.caseTrain.type.general.SubblockMarkup;
import de.knowwe.caseTrain.type.general.Title;
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
		this.addContentType(new Erkl());
		this.addContentType(new Bild());

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
				List<Section<? extends Type>> blockMarkupChildren = s.getChildren().get(0).getChildren();
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
				 *  TODO getting the children is not save. How do I get the
				 *  the BlockMarkupContent?
				 */
				List<Section<? extends Type>> children = s.getChildren().get(0).getChildren();
				Section<? extends Type> actual = null;
				boolean erklMissing = true;
				boolean antwortenMissing = true;
				for (Section<? extends Type> sec : children) {
					if (sec.get() instanceof Frage) {
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
					if (sec.get() instanceof Hinweis) {
						continue;
					}
					if (sec.get() instanceof Erkl) {
						erklMissing = false;
						continue;
					}
					if (sec.get() instanceof Antworten) {
						antwortenMissing = false;
						AntwortKorrektheitChecker.getInstance().
						validateAntwortenBlock((Section<Frage>) actual, (Section<Antworten>) sec, messages);
						continue;
					}
				}
				return messages;
			}
		});
	}

}

/**
 * Part of Info: Contains Antwort-Lines.
 * 
 * @author Jochen Reutelshoefer
 * @created 28.04.2011
 */
class Antworten extends SubblockMarkup {

	private final String ANTWORT = "Antwort";

	public Antworten() {
		super("Antworten");
		PlainText plain = new PlainText();
		plain.setSectionFinder(new RegexSectionFinder("\\r?\\n"));
		this.addContentType(plain);
		this.addContentType(new Antwort());

		this.addSubtreeHandler(new GeneralSubtreeHandler<Frage>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Frage> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);
				List<Section<Antwort>> found = new ArrayList<Section<Antwort>>();
				Sections.findSuccessorsOfType(s, Antwort.class, found);

				if (found.isEmpty()) {
					messages.add(new MissingComponentWarning(ANTWORT));
				}

				return messages;
			}
		});
	}

	class Antwort extends AbstractType {

		public Antwort() {
			this.setSectionFinder(new LineSectionFinder());
			this.setCustomRenderer(new DivStyleClassRenderer("Antwort"));
			this.addChildType(new AntwortKorrektheit());
		}

		class AntwortKorrektheit extends AbstractType {

			String regex = "\\{(.*?)\\}";

			public AntwortKorrektheit() {
				ConstraintSectionFinder csf = new ConstraintSectionFinder(
						new RegexSectionFinder(regex));
				csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
				this.setSectionFinder(csf);
				this.addChildType(new AntwortKorrektheitContent());
			}

			/**
			 * See also {@link AntwortKorrektheitChecker}
			 * 
			 * @author Jochen
			 * @created 28.04.2011
			 */
			class AntwortKorrektheitContent extends AbstractType {

				public AntwortKorrektheitContent() {
					this.setCustomRenderer(new StyleRenderer("font-weight:bold;"));
					this.setSectionFinder(new RegexSectionFinder(regex, Pattern.DOTALL, 1));
					this.addSubtreeHandler(AntwortKorrektheitChecker.getInstance());
				}

			}
		}
	}

}

class Hinweis extends SubblockMarkup {

	public Hinweis() {
		super("Hinweis");
		this.addChildType(new Title());
		this.addContentType(new Bild());
	}

}

class Frage extends SubblockMarkup {

	private final String FRAGE_TYPE = "Fragetyp";
	private final String FRAGE_TEXT = "Fragetext";
	private final String FRAGE_GEWICHT = "Fragegewicht";
	private final String FRAGE_GEWICHT_WRONG = "Fragegewicht kleiner 0";

	public Frage() {
		super("Frage");
		this.addChildType(new Title());
		this.addContentType(new Bild());
		this.addContentType(new FrageGewicht());
		this.addContentType(new FrageTyp());
		this.addContentType(new FrageText());

		this.addSubtreeHandler(new GeneralSubtreeHandler<Frage>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Frage> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				Section<FrageGewicht> fragegewichtSection = Sections.findSuccessor(s,
						FrageGewicht.class);
				if (fragegewichtSection == null) {
					messages.add(new MissingAttributeWarning(FRAGE_GEWICHT));
				} else if(Double.valueOf(fragegewichtSection.getOriginalText()) < 0) {
					messages.add(new InvalidArgumentError(FRAGE_GEWICHT_WRONG));
				}

				Section<FrageTyp> typSection = Sections.findSuccessor(s, FrageTyp.class);
				if (typSection == null) {
					messages.add(new MissingComponentError(FRAGE_TYPE));
				}

				Section<FrageText> fragetextSection = Sections.findSuccessor(s,
						FrageText.class);
				if (fragetextSection == null) {
					messages.add(new MissingComponentWarning(FRAGE_TEXT));
				}

				return messages;
			}
		});
	}

	class FrageGewicht extends AbstractType {

		public FrageGewicht() {
			this.setSectionFinder(new RegexSectionFinder("[-]?[0-9]+"));
			this.setCustomRenderer(MouseOverTitleRenderer.getInstance());
		}
	}

	class FrageTyp extends AbstractType {

		public FrageTyp() {
			this.setSectionFinder(new RegexSectionFinder(
					AntwortKorrektheitChecker.getInstance().getRegexAsString()));
			this.setCustomRenderer(MouseOverTitleRenderer.getInstance());
		}

	}

	class FrageText extends AbstractType {

		public FrageText() {
			this.setSectionFinder(new RegexSectionFinder("([\\w]{1}[\\W]?[ ]?)+\\?"));
			this.setCustomRenderer(MouseOverTitleRenderer.getInstance());
		}
	}

}

class Erkl extends SubblockMarkup {

	public Erkl() {
		super("Erklärung");
		this.addChildType(new Title());
		this.addContentType(new Bild());
	}

	@Override
	public String getCSSClass() {
		return "Erkl";
	}

}

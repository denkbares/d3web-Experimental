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
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.kdom.report.MessageRenderer;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.caseTrain.message.InvalidArgumentError;
import de.knowwe.caseTrain.message.MissingComponentWarning;
import de.knowwe.caseTrain.renderer.DivStyleClassRenderer;
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

	// Warnings are only rendered in message block
	@Override
	public MessageRenderer getWarningRenderer() {
		return new MessageRenderer() {
			@Override
			public String preRenderMessage(KDOMReportMessage m, UserContext user) {
				return "";
			}
			@Override
			public String postRenderMessage(KDOMReportMessage m, UserContext user) {
				return "";
			}
		};
	}

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
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("<div class='Infoend'></div>"));
				string.append(KnowWEUtils.maskHTML("</div>"));

			}
		});

		this.addSubtreeHandler(new GeneralSubtreeHandler<Info>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Info> s) {
				Section<Frage> frageSection = Sections.findSuccessor(s, Frage.class);
				if (frageSection == null) {
					return Arrays.asList((KDOMReportMessage) new MissingComponentWarning(
							"Frage"));
				}
				return new ArrayList<KDOMReportMessage>(0);
			}
		});
	}

}

class Antworten extends SubblockMarkup {

	public Antworten() {
		super("Antworten");
		this.addContentType(new Antwort());
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

			class AntwortKorrektheitContent extends AbstractType {

				public AntwortKorrektheitContent() {
					this.setCustomRenderer(new StyleRenderer("font-weight:bold;"));
					this.setSectionFinder(new RegexSectionFinder(regex, Pattern.DOTALL, 1));
					this.addSubtreeHandler(new AntwortKorrektheitChecker());
				}
				private final class AntwortKorrektheitChecker extends GeneralSubtreeHandler<AntwortKorrektheitContent> {
					@Override
					public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<AntwortKorrektheitContent> s) {
						String content = s.getOriginalText().trim();
						String[] symbols = {
								"+", "-" };
						for (String string : symbols) {
							if (content.equals(string)) {
								return new ArrayList<KDOMReportMessage>(0);
							}

						}
						try {
							Double.parseDouble(content);
						}
						catch (Exception e) {
							return Arrays.asList((KDOMReportMessage) new InvalidArgumentError(
										" Nur '+' oder '-' oder Zahlen zwischen 0 und 1 erlaubt!"));
						}

						return new ArrayList<KDOMReportMessage>(0);
					}
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

	public Frage() {
		super("Frage");
		this.addChildType(new Title());
		this.addContentType(new Bild());
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

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
package de.knowwe.caseTrain.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.caseTrain.message.InvalidArgumentError;
import de.knowwe.caseTrain.message.MissingAttributeWarning;
import de.knowwe.caseTrain.message.MissingComponentError;
import de.knowwe.caseTrain.message.MissingComponentWarning;
import de.knowwe.caseTrain.renderer.MouseOverTitleRenderer;
import de.knowwe.caseTrain.type.general.Bild;
import de.knowwe.caseTrain.type.general.SubblockMarkup;
import de.knowwe.caseTrain.type.general.Title;


/**
 * 
 * @author Johannes Dienst
 * @created 12.05.2011
 */
public class Frage extends SubblockMarkup {

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
					AntwortenKorrektheitChecker.getInstance().getRegexAsString()));
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

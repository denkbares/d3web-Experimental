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

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.caseTrain.message.MissingContentWarning;
import de.knowwe.caseTrain.message.MissingPictureWarning;
import de.knowwe.caseTrain.message.MissingTitleError;
import de.knowwe.caseTrain.type.general.Bild;
import de.knowwe.caseTrain.type.general.BlockMarkupType;
import de.knowwe.caseTrain.type.general.Title;

/**
 * 
 * One part of the case-markup-structure of the caseTrain-wiki-format
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class Einleitung extends BlockMarkupType {

	public Einleitung() {
		super("Einleitung");
		this.addChildType(new Title());
		this.addContentType(new Bild());

		this.addSubtreeHandler(new GeneralSubtreeHandler<Einleitung>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Einleitung> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				Section<Title> title = Sections.findSuccessor(s, Title.class);
				if (title == null) {
					messages.add(new MissingTitleError(Title.TITLE));
				}

				Section<PlainText> plain = Sections.findSuccessor(s, PlainText.class);
				if (plain == null) {
					messages.add(new MissingContentWarning());
				}

				Section<Bild> pic = Sections.findSuccessor(s, Bild.class);
				if (pic == null) {
					messages.add(new MissingPictureWarning());
				}

				return messages;
			}
		});
	}

	@Override
	public String getCSSClass() {
		return "Ie";
	}

}


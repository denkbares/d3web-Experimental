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

package de.knowwe.casetrain.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.casetrain.message.MissingContentWarning;
import de.knowwe.casetrain.message.MissingPictureNotice;
import de.knowwe.casetrain.message.MissingTitleError;
import de.knowwe.casetrain.type.general.BlockMarkupType;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Audio;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.Link;
import de.knowwe.casetrain.type.multimedia.Video;

/**
 * 
 * One part of the case-markup-structure of the casetrain-wiki-format.
 * Represents the Introduction before the Info-Parts.
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class Introduction extends BlockMarkupType {

	public Introduction() {
		super("Einleitung");
		this.addContentType(new Title());
		this.addContentType(new Image());
		this.addContentType(new Video());
		this.addContentType(new Link());
		this.addContentType(new Audio());

		this.addSubtreeHandler(new GeneralSubtreeHandler<Introduction>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Introduction> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				Section<Title> title = Sections.findSuccessor(s, Title.class);
				if (title == null) {
					messages.add(new MissingTitleError(Introduction.class.getSimpleName()));
				} else if(title.getOriginalText().trim().equals("")) {
					messages.add(new MissingTitleError(Introduction.class.getSimpleName()));
				}

				Section<PlainText> plain = Sections.findSuccessor(s, PlainText.class);
				if (plain == null) {
					messages.add(
							new MissingContentWarning(
									Introduction.class.getSimpleName()));
				}

				Section<Image> pic = Sections.findSuccessor(s, Image.class);
				if (pic == null) {
					messages.add(
							new MissingPictureNotice(
									Introduction.class.getSimpleName()));
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


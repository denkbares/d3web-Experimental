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
import java.util.List;

import de.knowwe.casetrain.type.general.BlockMarkupType;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Audio;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.Link;
import de.knowwe.casetrain.type.multimedia.Video;
import de.knowwe.casetrain.util.Utils;
import de.knowwe.core.compile.DefaultGlobalCompiler;
import de.knowwe.core.compile.DefaultGlobalCompiler.DefaultGlobalScript;
import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;

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

		this.addCompileScript(new DefaultGlobalScript<Introduction>() {

			@Override
			public void compile(DefaultGlobalCompiler compiler, Section<Introduction> s) {

				List<Message> messages = new ArrayList<Message>(0);

				Section<Title> title = Sections.findSuccessor(s, Title.class);
				if (title == null) {
					messages.add(Utils.missingTitleError(Introduction.class.getSimpleName()));
				}
				else if (title.getText().trim().equals("")) {
					messages.add(Utils.missingTitleError(Introduction.class.getSimpleName()));
				}

				Section<PlainText> plain = Sections.findSuccessor(s, PlainText.class);
				if (plain == null) {
					messages.add(Utils.missingContentWarning(
							Introduction.class.getSimpleName()));
				}

				Section<Image> pic = Sections.findSuccessor(s, Image.class);
				if (pic == null) {
					messages.add(Utils.missingPictureNotice(
							Introduction.class.getSimpleName()));
				}
				Messages.storeMessages(s, getClass(), messages);
			}
		});
	}

	@Override
	public String getCSSClass() {
		return "Ie";
	}

}

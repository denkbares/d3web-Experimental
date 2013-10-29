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
package de.knowwe.casetrain.type.multimedia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.knowwe.casetrain.type.multimedia.MultimediaItem.MultimediaItemContent;
import de.knowwe.casetrain.util.Utils;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;

/**
 * 
 * @author Johannes Dienst
 * @created 30.05.2011
 */
public class MultimediaItemHandler extends GeneralSubtreeHandler<MultimediaItem> {

	private final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	@Override
	public Collection<Message> create(Article article, Section<MultimediaItem> s) {

		List<Message> messages = new ArrayList<Message>(0);

		List<WikiAttachment> attachments;
		try {
			attachments = Environment.getInstance().getWikiConnector()
					.getAttachments(article.getTitle());
		}
		catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"cannot access wiki attachment", e);
			messages.add(Utils.missingContentWarning(MultimediaItem.class.getSimpleName()));
			return messages;
		}

		List<String> attachmentFileNames = new ArrayList<String>(attachments.size());
		for (WikiAttachment attachment : attachments) {
			attachmentFileNames.add(attachment.getFileName());
		}

		Section<MultimediaItemContent> mmI = Sections.findChildOfType(s,
				MultimediaItemContent.class);

		String t = mmI.getText().trim();

		if (t.equals("")) {
			messages.add(Utils.missingContentWarning(MultimediaItem.class.getSimpleName()));
			return messages;
		}

		if (Sections.hasType(s, Image.class)) {
			if (!attachmentFileNames.contains(t)) {
				messages.add(Utils.missingPictureError(t));
			}
			if (!(t.endsWith(".gif") || t.endsWith(".jpg") || t.endsWith("png"))) {
				messages.add(Utils.invalidArgumentError(
						bundle.getString("WRONG_IMAGE_FORMAT")));
			}

		}
		else if (Sections.hasType(s, Video.class)) {
			if (!attachmentFileNames.contains(t)) {
				messages.add(Utils.missingVideoError(t));
			}
			if (!t.endsWith(".flv")) {
				messages.add(
						Utils.invalidArgumentError(
								bundle.getString("WRONG_VIDEO_FORMAT")));
			}

		}
		else if (Sections.hasType(s, Audio.class)) {
			if (!attachmentFileNames.contains(t)) messages.add(Utils.missingAudioError(t));
			if (!t.endsWith(".mp3")) {
				messages.add(
						Utils.invalidArgumentError(
								bundle.getString("WRONG_AUDIO_FORMAT")));
			}

		}

		// TODO: Test URL?
		// else if (s.get().isAssignableFromType(Link.class)) {
		//
		// }

		return messages;
	}

}

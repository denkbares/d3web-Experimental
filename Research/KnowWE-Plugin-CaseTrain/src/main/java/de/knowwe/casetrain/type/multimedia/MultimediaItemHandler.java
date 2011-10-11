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
package de.knowwe.casetrain.type.multimedia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import de.knowwe.casetrain.message.InvalidArgumentError;
import de.knowwe.casetrain.message.MissingAudioError;
import de.knowwe.casetrain.message.MissingContentWarning;
import de.knowwe.casetrain.message.MissingPictureError;
import de.knowwe.casetrain.message.MissingVideoError;
import de.knowwe.casetrain.type.multimedia.MultimediaItem.MultimediaItemContent;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;


/**
 * 
 * @author Johannes Dienst
 * @created 30.05.2011
 */
public class MultimediaItemHandler extends GeneralSubtreeHandler<MultimediaItem>  {

	private final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<MultimediaItem> s) {

		List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

		List<String> attachments = KnowWEEnvironment.getInstance().getWikiConnector()
		.getAttachmentFilenamesForPage(article.getTitle());
		Section<MultimediaItemContent> mmI = Sections.findChildOfType(s,
				MultimediaItemContent.class);

		String t = mmI.getOriginalText().trim();

		if (t.equals("")) {
			messages.add(new MissingContentWarning(MultimediaItem.class.getSimpleName()));
			return messages;
		}

		if (s.get().isAssignableFromType(Image.class)) {
			if (!attachments.contains(t)) {
				messages.add(new MissingPictureError(t));
			}
			if (!(t.endsWith(".gif") || t.endsWith(".jpg") || t.endsWith("png"))) {
				messages.add(
						new InvalidArgumentError(
								bundle.getString("WRONG_IMAGE_FORMAT")));
			}

		} else if (s.get().isAssignableFromType(Video.class)) {
			if(!attachments.contains(t)) {
				messages.add(new MissingVideoError(t));
			}
			if (!t.endsWith(".flv")) {
				messages.add(
						new InvalidArgumentError(
								bundle.getString("WRONG_VIDEO_FORMAT")));
			}

		} else if (s.get().isAssignableFromType(Audio.class)) {
			if(!attachments.contains(t))
				messages.add(new MissingAudioError(t));
			if (!t.endsWith(".mp3")) {
				messages.add(
						new InvalidArgumentError(
								bundle.getString("WRONG_AUDIO_FORMAT")));
			}

		}

		// TODO: Test URL?
		//		else if (s.get().isAssignableFromType(Link.class)) {
		//
		//		}

		return messages;
	}

}

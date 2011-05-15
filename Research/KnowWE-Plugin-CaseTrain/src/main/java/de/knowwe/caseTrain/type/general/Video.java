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
package de.knowwe.caseTrain.type.general;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.caseTrain.message.MissingPictureError;
import de.knowwe.caseTrain.type.general.Bild.BildContent;


/**
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class Video extends AbstractType {

	public static String KEY_VIDEO = "Video:";

	private static String REGEX = "\\{" + KEY_VIDEO + "(.*?)\\}";

	public Video() {

		this.setSectionFinder(new RegexSectionFinder(REGEX));
		this.addChildType(new VideoContent());

		this.setCustomRenderer(new KnowWEDomRenderer<Video>() {

			@Override
			public void render(KnowWEArticle article, Section<Video> sec, UserContext user, StringBuilder string) {
				Section<BildContent> bildURL = Sections.findChildOfType(sec,
						BildContent.class);
				string.append(KnowWEUtils.maskHTML("<img height='70' src='"));
				string.append("attach/" + sec.getArticle().getTitle() + "/");
				string.append(bildURL.getOriginalText().trim());
				string.append(KnowWEUtils.maskHTML("'></img>"));
			}
		});

		this.addSubtreeHandler(new GeneralSubtreeHandler<Video>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Video> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				List<String> attachments = KnowWEEnvironment.getInstance().getWikiConnector()
				.getAttachmentFilenamesForPage(article.getTitle());
				Section<VideoContent> videoURL = Sections.findChildOfType(s,
						VideoContent.class);
				if(!attachments.contains(videoURL.getOriginalText().trim())) {
					messages.add(new MissingPictureError(videoURL.getOriginalText().trim()));
				}

				return messages;
			}
		});
	}

	class VideoContent extends AbstractType{

		public VideoContent() {
			this.setSectionFinder(new RegexSectionFinder(REGEX, 0, 1));
		}
	}


}

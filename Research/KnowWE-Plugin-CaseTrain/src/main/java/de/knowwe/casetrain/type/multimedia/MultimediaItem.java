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

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;


/**
 * 
 * Extended by MultimediaItems like {@link Image} and {@link Video}
 * and {@link Audio} and {@link Link}.
 * 
 * TODO check extensions of files.
 * 
 * @author Johannes Dienst
 * @created 15.05.2011
 */
public class MultimediaItem extends AbstractType {

	private final String REGEX;
	private final MultimediaItemContent content;

	public MultimediaItem(String regex) {
		this.REGEX = regex;

		this.setSectionFinder(new RegexSectionFinder(REGEX));
		content = new MultimediaItemContent(REGEX);
		this.addChildType(content);

		this.addSubtreeHandler(new MultimediaItemHandler());

		//		this.addSubtreeHandler(new GeneralSubtreeHandler<MultimediaItem>() {
		//
		//			@Override
		//			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<MultimediaItem> s) {
		//
		//				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);
		//
		//				List<String> attachments = KnowWEEnvironment.getInstance().getWikiConnector()
		//				.getAttachmentFilenamesForPage(article.getTitle());
		//				Section<MultimediaItemContent> multimediaItemURL = Sections.findChildOfType(s,
		//						MultimediaItemContent.class);
		//				if(!attachments.contains(multimediaItemURL.getOriginalText().trim())) {
		//					messages.add(
		//							new MissingPictureError(multimediaItemURL.getOriginalText().trim()));
		//				}
		//
		//				return messages;
		//			}
		//		});
	}

	public void addContentType(Type t) {
		content.addChildType(t);
	}

	public class MultimediaItemContent extends AbstractType {
		public MultimediaItemContent(String regex) {
			this.setSectionFinder(new RegexSectionFinder(REGEX, 0, 1));
		}
	}
}

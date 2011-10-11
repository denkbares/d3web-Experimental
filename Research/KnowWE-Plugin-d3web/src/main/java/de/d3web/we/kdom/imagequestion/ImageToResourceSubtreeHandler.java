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
package de.d3web.we.kdom.imagequestion;

import java.util.Collection;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.wikiConnector.ConnectorAttachment;
import de.knowwe.d3web.resource.WikiAttachmentResource;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Stores an Attachment as a resource in the knowledgebase.
 * 
 * @author Johannes Dienst
 * 
 */
public class ImageToResourceSubtreeHandler extends D3webSubtreeHandler<ImageToResourceType> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<ImageToResourceType> s) {
		KnowledgeBase kb =
				D3webModule.getKnowledgeRepresentationHandler(
						article.getWeb()).getKB(article.getTitle());
		if (kb == null) return null;

		String imageName = DefaultMarkupType.getAnnotation(s, "Image");
		Collection<ConnectorAttachment> attachments =
				KnowWEEnvironment.getInstance().getWikiConnector().getAttachments();
		for (ConnectorAttachment att : attachments) {
			if (att.getFileName().equalsIgnoreCase(imageName)) {
				// TODO no special sub-folder should be used!
				String path = "multimedia/" + att.getFileName();
				Resource res = new WikiAttachmentResource(path, att);
				kb.addResouce(res);
			}
		}
		return null;
	}

}

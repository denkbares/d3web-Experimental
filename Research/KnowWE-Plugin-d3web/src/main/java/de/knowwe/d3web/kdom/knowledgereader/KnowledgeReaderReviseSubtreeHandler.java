/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.knowwe.d3web.kdom.knowledgereader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.tools.ant.filters.StringInputStream;

import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.d3web.report.Message;
import de.d3web.we.basic.D3webModule;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * ReviseSubtreehandler for KnowledgeReaderType
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class KnowledgeReaderReviseSubtreeHandler extends SubtreeHandler<KnowledgeReaderType> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<KnowledgeReaderType> s) {
		KnowledgeBase kb = D3webModule.getKnowledgeRepresentationHandler(
				article.getWeb()).getKB(article.getTitle());
		if (kb == null) return null;

		String readerID = DefaultMarkupType.getAnnotation(s, "KnowledgeReader");
		String toRead = DefaultMarkupType.getContent(s);
		Extension[] allextensions = PluginManager.getInstance().getExtensions(
				PersistenceManager.EXTENDED_PLUGIN_ID, PersistenceManager.EXTENDED_POINT_READER);
		List<Extension> extensions = new ArrayList<Extension>();
		for (Extension e : allextensions) {
			if (e.getID().equals(readerID)) {
				extensions.add(e);
			}
		}
		if (extensions.size() == 0) {
			KnowWEUtils.storeMessages(article, s, this.getClass(), Message.class,
					Arrays.asList(new Message(Message.ERROR,
							"KnowledgeReader " + readerID + " not found.", null, -1, null)));
			return null;
		}
		else if (extensions.size() > 1) {
			KnowWEUtils.storeMessages(article, s, this.getClass(), Message.class,
					Arrays.asList(new Message(Message.ERROR,
							"KnowledgeReaderID " + readerID + " is not unique.", null, -1, null)));
			return null;
		}
		KnowledgeReader reader = (KnowledgeReader) extensions.get(0).getSingleton();
		try {
			reader.read(kb, new StringInputStream(toRead), new DummyProgressListener());
		}
		catch (IOException e1) {
			KnowWEUtils.storeMessages(article, s, this.getClass(), Message.class,
					Arrays.asList(new Message(Message.ERROR,
							e1.getMessage(), null, -1, null)));
			return null;
		}
		return null;
	}
}

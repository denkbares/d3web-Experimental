/*
 * Copyright (C) ${year} denkbares GmbH, Germany
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

package de.d3web.we.kdom.kopic;

import java.util.Collection;

import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;

/**
 * This class defines the knowledge base markup. With this, you can specify a
 * knowledge base that will be compiled from names package definitions, found on
 * of all wiki articles.
 * <p>
 * As the content of the markup you must specify the knowledge base name. The
 * markup also supports the following annotations.
 * <ul>
 * <li><b>id:</b> a unique textual id of the knowledge base.
 * <li><b>version:</b> the current version of the knowledge base.
 * <li><b>author:</b> the responsible person/authority/company owning the
 * copyright of the knowledge base.
 * <li><b>comment:</b> some additional textual description on the knowledge
 * base.
 * <li><b>uses:</b> a package name that is searched for compiling.
 * </ul>
 * Please note that you must have at least one package defined. If you want to
 * compile your knowledge base from several packages, use multiple "@uses: ..."
 * annotations. You may specify "this" or "default" as special package names.
 * The package name "default" may be used to compile all wiki content that have
 * no explicitly defined package. The package name "this" may be used to compile
 * the contents of this article, ignoring their package declaration.
 * 
 * @author volker_belli
 * @created 13.10.2010
 */
public class KnowledgeBaseType extends DefaultMarkupType {

	public static final String ANNOTATION_ID = "id";
	public static final String ANNOTATION_VERSION = "version";
	public static final String ANNOTATION_AUTHOR = "author";
	public static final String ANNOTATION_COMMENT = "comment";
	public static final String ANNOTATION_COMPILE = "uses";

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("KnowledgeBase");
		MARKUP.addAnnotation(ANNOTATION_COMPILE, true);
		MARKUP.addAnnotationType(ANNOTATION_COMPILE, new KnowledgeBaseCompileType());
		MARKUP.addAnnotation(ANNOTATION_AUTHOR, false);
		MARKUP.addAnnotation(ANNOTATION_COMMENT, false);
		MARKUP.addAnnotation(ANNOTATION_ID, false);
		MARKUP.addAnnotation(ANNOTATION_VERSION, false);
	}

	public KnowledgeBaseType() {
		super(MARKUP);
		this.setCustomRenderer(new KnowledgeBaseRenderer());
		this.addSubtreeHandler(new D3webSubtreeHandler<KnowledgeBaseType>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<KnowledgeBaseType> section) {
				// get required information
				KnowledgeBaseManagement kbm = getKBM(article);
				KnowledgeBase kb = kbm.getKnowledgeBase();

				// prepare the items to be set into the knowledge base
				String title = getContent(section).trim();
				String id = getAnnotation(section, ANNOTATION_ID);
				String author = getAnnotation(section, ANNOTATION_AUTHOR);
				String comment = getAnnotation(section, ANNOTATION_COMMENT);
				String version = getAnnotation(section, ANNOTATION_VERSION);

				// and write it to the knowledge base
				if (id != null) kb.setId(id);

				InfoStore infoStore = kb.getInfoStore();
				infoStore.addValue(MMInfo.PROMPT, title);
				if (author != null) infoStore.addValue(BasicProperties.AUTHOR, author);
				if (comment != null) infoStore.addValue(MMInfo.DESCRIPTION, comment);
				if (version != null) infoStore.addValue(BasicProperties.VERSION, version);

				return null;
			}
		});
	}

}

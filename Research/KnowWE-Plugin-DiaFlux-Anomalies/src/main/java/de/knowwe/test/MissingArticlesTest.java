package de.knowwe.test;

/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.TestParameter.Mode;
import de.d3web.testing.TestParameter.Type;
import de.d3web.testing.TestingUtils;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.jspwiki.types.LinkType;

/**
 * This CITest checks for Missing Articles, ie. articles, that are linked, but
 * non-existant.
 * 
 * @author Reinhard Hatko
 * @created 27.03.2013
 */
public class MissingArticlesTest extends AbstractTest<ArticleManager> {

	public MissingArticlesTest() {

		this.addParameter("ignoreAttachments", Mode.Optional,
				"Ignore missing attachments. Defaults to false.",
				"true", "false");

		this.addIgnoreParameter("articles", Type.Regex, Mode.Optional,
				"A RegEx specifying articles that should be excluded from this test.");

	}

	@Override
	public Message execute(ArticleManager testObject, String[] args, String[]...
			ignores) throws InterruptedException {

		ArticleManager manager = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);

		// match lower case...
		Collection<String> titles = OrphanArticlesTest.getAllArticlesLowercase(manager);
		boolean ignoreAttachments = false;
		if (OrphanArticlesTest.checkBooleanArg(args, 0)) {
			ignoreAttachments = true;
		}

		Collection<Section<LinkType>> missingLinks = new HashSet<Section<LinkType>>();

		for (Article article : manager.getArticles()) {
			List<Section<LinkType>> links =
					Sections.findSuccessorsOfType(article.getRootSection(), LinkType.class);

			for (Section<LinkType> link : links) {
				// dont check external links :-)
				if (!LinkType.isInternal(link)) continue;

				boolean isAttachment = LinkType.isAttachment(link);
				if (isAttachment) {
					if (ignoreAttachments) continue;
					String path = getQualifiedPath(link);
					
					try {
						WikiAttachment a = Environment.getInstance().getWikiConnector().getAttachment(
								path);
						if (a == null) {
							missingLinks.add(link);
						}
					}
					catch (IOException e) {
						// when does this happen? Add to missing articles?
						continue;
					}

				}
				else {
					String title = LinkType.getLink(link).toLowerCase();
					if (!titles.contains(title)) {
						missingLinks.add(link);
					}

				}

			}

		}

		if (missingLinks.isEmpty()) return Message.SUCCESS;

		List<String> erroneousArticles = new LinkedList<String>();
		List<String> missingArticles = new LinkedList<String>();
		Collection<Pattern> ignorePatterns = TestingUtils.compileIgnores(ignores);

		for (Section<LinkType> link : missingLinks) {
			String containingArticle = link.getTitle();
			String missingArticle = LinkType.getLink(link);
			if (TestingUtils.isIgnored(missingArticle, ignorePatterns)) continue;

			if (!erroneousArticles.contains(containingArticle)) erroneousArticles.add(containingArticle);
			if (!missingArticles.contains(missingArticle)) missingArticles.add(missingArticle);
		}

		if (missingArticles.isEmpty()) return Message.SUCCESS;

		Collections.sort(erroneousArticles);
		Collections.sort(missingArticles);

		StringBuilder bob = new StringBuilder();
		bob.append("The following " + missingArticles.size() + " articles are missing :\n");
		for (String link : missingArticles) {
			bob.append(link);
			bob.append("\n");
		}
		bob.append("\nThose are referenced on the following articles:");

		return TestingUtils.createFailure(bob.toString(), erroneousArticles,
				Article.class);
	}

	/**
	 * Returns a full path to an attachment
	 */
	private static String getQualifiedPath(Section<LinkType> link) {
		String path = LinkType.getLink(link);
		if (path.contains("/")) return path;
		else return link.getArticle().getTitle() + "/" + path;
	}

	@Override
	public Class<ArticleManager> getTestObjectClass() {
		return ArticleManager.class;
	}

	@Override
	public String getDescription() {
		return "Tests for articles, that are missing.";
	}

}

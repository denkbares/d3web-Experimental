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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.TestParameter.Mode;
import de.d3web.testing.TestParameter.Type;
import de.d3web.testing.Utils;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.jspwiki.types.LinkType;

/**
 * A CITest that checks for the existence of Articles that are not reachable by
 * links. This is especially important, if they contain knowledge.
 * 
 * @author Reinhard Hatko
 * @created 27.03.2013
 */
public class OrphanArticlesTest extends AbstractTest<ArticleManager> {

	// Root articles, that are not linked anywhere
	public static final Collection<String> ROOTS = Arrays.asList(new String[] {
			"leftmenu", "leftmenufooter", "moremenu", "main", "objectinfopage" });

	private static final Pattern EMPTY_PATTERN = Pattern.compile("\\s*");

	public OrphanArticlesTest() {
		this.addParameter("ignoreEmpty", Mode.Optional,
				"Empty pages can be excluded, eg if they are no longer used, but their history should be preserved.",
				"true", "false");

		this.addParameter("ignoreUsers", Mode.Optional, "Ignore user articles.", "true", "false");

		this.addIgnoreParameter("articles", Type.Regex, Mode.Optional,
				"A RegEx specifying articles that are excluded from this test, ie. they are allowed to be orphaned.");

	}

	@Override
	public Message execute(ArticleManager testObject, String[] args, String[]...
			ignores) throws InterruptedException {

		ArticleManager manager = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);


		// links may not match the exact case, so use lowercase first...
		Collection<String> titles = getAllArticlesLowercase(manager);

		for (Article article : manager.getArticles()) {
			List<Section<LinkType>> links =
					Sections.findSuccessorsOfType(article.getRootSection(),
							LinkType.class);
			for (Section<LinkType> section : links) {
				titles.remove(LinkType.getLink(section).toLowerCase());
			}

		}

		titles.removeAll(ROOTS);

		titles = Utils.filterIgnored(titles, Utils.compileIgnores(ignores));

		if (titles.isEmpty()) return Message.SUCCESS;

		// ...later get correct case
		List<String> result = getTitlesCorrectCase(manager, titles);

		// optionally remove empty articles
		if (checkBooleanArg(args, 0)) {
			filterEmpty(result, manager);
		}

		// optionally remove user articles
		if (checkBooleanArg(args, 1)) {
			filterUserArticles(result);
		}

		Collections.sort(result);

		if (result.isEmpty()) return Message.SUCCESS;

		return Utils.createErrorMessage(result,
				"The following articles are not reachable by links: ", Article.class);
	}

	protected static boolean checkBooleanArg(String[] args, int index) {
		return args.length >= index + 1 && Boolean.valueOf(args[index]).booleanValue();
	}

	protected static Collection<Article> filterArticles(Collection<Article> allArticles, Collection<Pattern> ignorePatterns) {
		Collection<Article> result = new LinkedList<Article>();

		for (Article article : allArticles) {
			if (!Utils.isIgnored(article.getTitle(), ignorePatterns)) result.add(article);
		}
		return result;

	}

	/**
	 * 
	 * @created 28.03.2013
	 * @param manager
	 * @return
	 */
	protected static Collection<String> getAllArticlesLowercase(ArticleManager
			manager) {
		Collection<String> titles = new ArrayList<String>();

		// match lower case...
		for (Article article : manager.getArticles()) {
			titles.add(article.getTitle().toLowerCase());
		}
		return titles;
	}

	/**
	 * 
	 * @created 28.03.2013
	 * @param result
	 */
	private void filterUserArticles(List<String> result) {
		result.removeAll(Arrays.asList(Environment.getInstance().getWikiConnector().getAllUsers()));
	}

	protected static List<String> getTitlesCorrectCase(ArticleManager manager,
			Collection<String> titles) {
		List<String> result = new ArrayList<String>();

		for (Article article : manager.getArticles()) {
			if (titles.contains(article.getTitle().toLowerCase())) {
				result.add(article.getTitle());
				continue;
			}
		}
		return result;
	}

	private static void filterEmpty(Collection<String> result, ArticleManager
			manager) {
		for (String title : new ArrayList<String>(result)) {
			String text = manager.getArticle(title).getRootSection().getText();
			if (EMPTY_PATTERN.matcher(text).matches()) {
				result.remove(title);
				continue;
			}
		}
	}

	@Override
	public Class<ArticleManager> getTestObjectClass() {
		return ArticleManager.class;
	}

	@Override
	public String getDescription() {
		return "Tests for articles, that are not reachable by links.";
	}

}

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
package de.knowwe.revisions;

import java.util.HashMap;
import java.util.HashSet;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;

/**
 * This class represents a revision, which was uploaded by a user
 * 
 * @author grotheer
 * @created 22.04.2013
 */
public class UploadedRevision extends AbstractRevision {

	private HashMap<String, String> articles;

	/**
	 * @param web
	 */
	public UploadedRevision(String web, HashMap<String, String> articles) {
		super(web);
		this.articles = articles;
	}

	@Override
	protected void createArticleManager() {
		articleManager = new ArticleManager(Environment.getInstance(), web);

		for (String title : articles.keySet()) {
			String text = articles.get(title);
			Article article = Article.createArticle(text, title, web);
			articleManager.registerArticle(article);
		}
	}

	/**
	 * This method runs over all current page titles and returns the
	 * corresponding versions for the articles. 4 version states
	 * 
	 * @created 22.04.2013
	 * @return revision page version map, version is -2 if page is not existing
	 *         in this revision, -1 for the most current version, 1 if the page
	 *         does not exist in wiki
	 */
	@Override
	public HashMap<String, Integer> compareWithCurrentState() {
		HashMap<String, Integer> result = new HashMap<String, Integer>();

		ArticleManager stdAM = Environment.getInstance().getArticleManager(web);
		HashSet<Article> unprocessedStdArticles = new HashSet<Article>(stdAM.getArticles());

		for (Article article : getArticleManager().getArticles()) {
			String title = article.getTitle();
			Section<RootType> uploadedSection = article.getRootSection();
			Article stdArticle = stdAM.getArticle(title);
			if (stdArticle != null) {
				unprocessedStdArticles.remove(stdArticle);
				Section<RootType> stdSection = stdArticle.getRootSection();
				if (!uploadedSection.getText().equals(stdSection.getText())) {
					result.put(article.getTitle(), 0);
				}
				else {
					result.put(article.getTitle(), -1);
				}
			}
			else {
				result.put(article.getTitle(), 1);
			}
		}
		for (Article article : unprocessedStdArticles) {
			result.put(article.getTitle(), -2);
		}
		return result;
	}
}

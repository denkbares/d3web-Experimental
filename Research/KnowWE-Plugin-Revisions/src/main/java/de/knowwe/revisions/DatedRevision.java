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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.wikiConnector.WikiConnector;

/**
 * This class represents a default wiki revision, which has a date to select the
 * page versions
 * 
 * @author grotheer
 * @created 22.04.2013
 */
public class DatedRevision extends AbstractRevision {

	private Date date;
	private HashMap<String, Integer> articleVersions;

	/**
	 * @param articleManagers
	 */
	public DatedRevision(Date date, String web) {
		super(web);
		this.date = date;
		this.web = web;

		// this object is filled if and only if it is used for the first time
		articleVersions = null;
	}

	@Override
	protected void createArticleManager() {
		articleManager = new ArticleManager(Environment.getInstance(), web);

		for (Entry<String, Integer> entry : getArticleVersions().entrySet()) {
			String title = entry.getKey();
			int version = entry.getValue();
			String text = Environment.getInstance().getWikiConnector().getVersion(title,
					version);
			Article article = Article.createArticle(text, title, web);
			articleManager.registerArticle(article);
		}
	}

	/**
	 * Get the article version numbers to every title that exists in this
	 * revision
	 * 
	 * @created 22.04.2013
	 * @return
	 */
	public HashMap<String, Integer> getArticleVersions() {
		if (articleVersions == null) {
			compareWithCurrentState();
		}
		return articleVersions;
	}

	/**
	 * This method runs over all current page titles and returns the
	 * corresponding versions for the revision
	 * 
	 * @created 22.04.2013
	 * @return revision page version, -2 if page is did not exist, -1 for the
	 *         most current version
	 */
	public HashMap<String, Integer> compareWithCurrentState() {
		boolean updateArticleVersions = false;
		if (articleVersions == null) {
			updateArticleVersions = true;
			articleVersions = new HashMap<String, Integer>();
		}

		HashMap<String, Integer> result = new HashMap<String, Integer>();

		WikiConnector wiki = Environment.getInstance().getWikiConnector();
		Collection<String> titles = wiki.getAllArticles(web).keySet();

		for (String title : titles) {
			int version = -1;
			try {
				version = wiki.getVersionAtDate(title, date);
			}
			catch (IOException e) {
				// TODO throw error message
				e.printStackTrace();
			}

			result.put(title, version);

			if (updateArticleVersions) {
				if (version != -2) {
					if (version == -1) {
						version = Environment.getInstance().getWikiConnector().getVersion(title);
					}
					articleVersions.put(title, version);
				}
			}
		}
		return result;
	}

	public Date getDate() {
		return date;
	}
}

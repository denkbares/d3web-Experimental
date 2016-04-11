/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.baseline;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.wikiConnector.WikiConnector;

/**
 * 
 * @author Reinhard Hatko
 * @created 26.10.2012
 */
public class BaselineDiff {

	static class ArticleVersion {
		
		private final String title;
		private final int version;
		private final Date lastModDate;
		private final String lastEditor;

		public ArticleVersion(String title, int version, Date lastModDate, String lastEditor) {
			this.title = title;
			this.version = version;
			this.lastModDate = lastModDate;
			this.lastEditor = lastEditor;
		}

		public String getTitle() {
			return title;
		}

		public Date getLastModDate() {
			return lastModDate;
		}

		public int getVersion() {
			return version;
		}

		public String getLastEditor() {
			return lastEditor;
		}

	}

	private final Collection<String> removedArticles;
	private final Collection<ArticleVersion> addedArticles;
	private final Map<String, ArticleVersion[]> changedArticles;
	private final Baseline base1;
	private final Baseline base2;

	public BaselineDiff(Baseline base1, Baseline base2) {
		this.base1 = base1;
		this.base2 = base2;
		this.addedArticles = new LinkedList<ArticleVersion>();
		this.removedArticles = new LinkedList<String>();
		this.changedArticles = new HashMap<String, ArticleVersion[]>();

		calculateDiff(base1, base2);

	}

	private void calculateDiff(Baseline base1, Baseline base2) {


		for (String title : base1.getArticles()) {
			if (base2.contains(title)) {
				int version1 = base1.getVersion(title);
				int version2 = base2.getVersion(title);
				if (version1 != version2) {
					ArticleVersion info1 = createArticleInfo(title, version1);
					ArticleVersion info2 = createArticleInfo(title, version2);

					changedArticles.put(title, new ArticleVersion[] {
							info1, info2 });
				}
			}
			else {
				removedArticles.add(title);
			}
			
		}
		
		Collection<String> newArticles = new LinkedList<String>(base2.getArticles());
		newArticles.removeAll(base1.getArticles());

		for (String title : newArticles) {
			this.addedArticles.add(createArticleInfo(title));
		}

	}

	public static ArticleVersion createArticleInfo(String title) {
		return createArticleInfo(title,
				Environment.getInstance().getWikiConnector().getVersion(title));
	}

	public static ArticleVersion createArticleInfo(String title, int version) {
		WikiConnector connector = Environment.getInstance().getWikiConnector();
		return new ArticleVersion(title, version, connector.getLastModifiedDate(title, version),
				connector.getAuthor(title, version));

	}


	public Baseline getBase1() {
		return base1;
	}

	public Baseline getBase2() {
		return base2;
	}

	public boolean isEmpty() {
		return getAddedArticles().isEmpty() && getRemovedArticles().isEmpty()
				&& getChangedArticles().isEmpty();
	}

	public Collection<ArticleVersion> getAddedArticles() {
		return Collections.unmodifiableCollection(addedArticles);
	}

	public Map<String, ArticleVersion[]> getChangedArticles() {
		return Collections.unmodifiableMap(changedArticles);
	}

	public Collection<String> getRemovedArticles() {
		return Collections.unmodifiableCollection(removedArticles);
	}

}

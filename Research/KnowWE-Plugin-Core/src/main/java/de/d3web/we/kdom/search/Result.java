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

package de.d3web.we.kdom.search;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;

/**
 * Search result
 * 
 * @author Alex Legler
 */
public class Result implements Comparable<Result> {

	private String query;

	private KnowWEArticle article;

	private Section section;

	/**
	 * Creates a new Result object.
	 * 
	 * @param query The query used to find this node
	 * @param article The article containing the match
	 * @param section The section containing the match
	 */
	public Result(String query, KnowWEArticle article, Section section) {
		this.query = query;
		this.article = article;
		this.section = section;
	}

	public String getQuery() {
		return query;
	}

	public KnowWEArticle getArticle() {
		return article;
	}

	public Section getSection() {
		return section;
	}

	/**
	 * Returns additional text around the Result
	 * 
	 * @param length If length is >0 this will return characters after the
	 *        Result, if it is <0 characters before the Result.
	 * @return The context text
	 */
	public String getAdditionalContext(int length) {
		final String originalText = article.getSection().getOriginalText();
		final int absPosition = section.getAbsolutePositionStartInArticle();

		if (length < 0) {
			// length is negative, so "+ length" is ok ;)
			int start = absPosition + length;

			if (start < 0) start = 0;

			return originalText.substring(start, absPosition - 1);
		}
		else {
			int end = absPosition + section.getOriginalText().length() + length;

			if (end >= originalText.length()) {
				end = originalText.length() - 1;
			}

			return originalText.substring(absPosition + section.getOriginalText().length(), end);
		}
	}

	@Override
	public int compareTo(Result o) {
		int a = article.getTitle().compareTo(o.getArticle().getTitle());

		if (a != 0) return a;

		return section.getObjectType().getName().compareTo(o.getSection().getObjectType().getName());
	}
}

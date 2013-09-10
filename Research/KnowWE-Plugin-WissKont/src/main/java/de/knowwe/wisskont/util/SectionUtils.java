/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.util;

import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;

/**
 * 
 * @author jochenreutelshofer
 * @created 03.05.2013
 */
public class SectionUtils {

	public static Section<? extends Type> findSection(String title, String kdomid) {
		Section<? extends Type> result = null;

		Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB, title);
		result = findSection(article.getRootSection(), kdomid);

		return result;
	}

	/**
	 * 
	 * @created 03.05.2013
	 * @param rootSection
	 * @param kdomid
	 * @return
	 */
	private static Section<? extends Type> findSection(Section<? extends Type> section, String kdomid) {
		if (section.getID().equals(kdomid)) return section;
		List<Section<? extends Type>> children = section.getChildren();

		Section<? extends Type> result = null;
		for (Section<? extends Type> child : children) {
			Section<? extends Type> temp = findSection(child, kdomid);
			if (temp != null) {
				result = temp;
			}
		}
		return result;
	}
}

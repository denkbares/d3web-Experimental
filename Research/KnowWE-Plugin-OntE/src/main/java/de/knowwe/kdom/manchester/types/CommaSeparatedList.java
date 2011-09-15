/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.kdom.manchester.types;

import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.util.AllTextFinderTrimComma;

/**
 * The {@link CommaSeparatedList} splits up description parts in a frame that
 * are separated by commas. Each member of a list is wrapped into an
 * {@link ListItem} for further processing. Note: Each
 * {@link CommaSeparatedList} can have different children's that can be set
 * through the constructor.
 *
 * @author smark
 * @created 08.06.2011
 */
public class CommaSeparatedList extends AbstractType {

	public static final String PATTERN = "(" +
			"[^\"]" + // everything not in quotes
			"|" +
			"\"[^\"]*\"" + // if in quotes everything that is not a quote
			")*?" +
			"(,|\\z)"; // till comma or line end

	public CommaSeparatedList(List<Type> children) {
		this.setSectionFinder(new RegexSectionFinder(PATTERN, Pattern.DOTALL | Pattern.MULTILINE));

		ListItem item = new ListItem();
		if (children != null && children.size() > 0) {
			for (Type t : children) {
				item.addChildType(t);
			}
		}
		this.addChildType(item);
	}

	/**
	 * Simply wraps a found member of a {@link CommaSeparatedList} into a
	 * {@link ListItem} objects for further handling. Note: Each ListItem can
	 * have different childs. See {@link CommaSeparatedList} for further
	 * informations.
	 *
	 * @author smark
	 * @created 08.06.2011
	 */
	public static class ListItem extends AbstractType {

		public ListItem() {
			// TODO one Of List
			this.setSectionFinder(new AllTextFinderTrimComma());
		}
	}
}



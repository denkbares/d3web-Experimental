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

package de.knowwe.comment.kdom;

import java.util.Map;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

public class CommentTypeTag extends AbstractType {

	public CommentTypeTag() {
		Map<String, String> commentTypes = CommentModule.getCommentTypes();

		StringBuilder regexString = new StringBuilder();

		regexString.append("(");
		for (String elem : commentTypes.keySet()) {
			regexString.append(elem).append("|");
		}
		regexString.replace(regexString.length() - 1, regexString.length(), ")");

		setSectionFinder(new RegexSectionFinder(regexString + "[0-9]* "));
		this.addChildType(new CommentTypeTagName());
		this.addChildType(new CommentTypeTagID());
	}

}

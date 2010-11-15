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

package de.d3web.we.taghandler;

import java.util.regex.Pattern;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

/**
 * A taghandler can have attributes specified in this schema: %%KnowWEPlugin
 * taghandlername, att1=aat, att2=aata,... The TaghandlerAttributeSectionfinder
 * stores the attributes in the KnowWESectionStore.
 * 
 * @author Johannes Dienst
 * 
 */
public class TagHandlerTypeContent extends DefaultAbstractKnowWEObjectType {

	private final String tagHandlerName;

	public TagHandlerTypeContent(String name) {
		this.tagHandlerName = name;

		int flags = Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL;
		String regex = "\\A\\s*" + Pattern.quote(name) + "(\\s+.*)?\\s*\\z";
		this.sectionFinder = new RegexSectionFinder(Pattern.compile(regex, flags), 0);
		this.addSubtreeHandler(new TagHandlerAttributeSubTreeHandler());
	}

	@Override
	public String getName() {
		return this.tagHandlerName;
	}

}

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

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.plugin.Plugins;

public class TagHandlerType extends DefaultAbstractKnowWEObjectType {

	@Override
	protected void init() {
		// searches for Strings like [{KnowWEPlugin ...}]
		this.sectionFinder = new RegexSectionFinder("\\[\\{KnowWEPlugin [^}]*}]");

		childrenTypes.add(new TagHandlerTypeStartSymbol());
		childrenTypes.add(new TagHandlerTypeEndSymbol());
		for (TagHandler tagHandler : Plugins.getTagHandlers()) {
			childrenTypes.add(new TagHandlerTypeContent(tagHandler.getTagName()));
		}
	}

	@Override
	public String getName() {
		return "KnowWEPlugin";
	}

	@Override
	public KnowWEDomRenderer getRenderer() {
		return new TagRenderer();
	}

}

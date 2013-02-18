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
package de.knowwe.wisskont.navigation;

import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;

/**
 * 
 * @author jochenreutelshofer
 * @created 18.02.2013
 */
public class AttachmentsLinkHandler extends AbstractTagHandler {

	/**
	 * 
	 */
	public AttachmentsLinkHandler() {
		super("attachments");
	}

	@Override
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {

		return Strings.maskHTML("<a id='menu-attach' accesskey='a' class='activetab'><span class='accesskey'>A</span>ttach</a>");

	}
	// http://localhost:8080/KnowWE/PageInfo.jsp?page=Ophthalmologische%20Vorbereitung

}

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
package de.knowwe.revisions.manager.action.details;

import java.util.Date;
import java.util.Map;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.revisions.DateType;
import de.knowwe.revisions.Revision;
import de.knowwe.revisions.manager.RevisionManager;

/**
 * 
 * @author grotheer
 * @created 01.05.2013
 */
public class WikiDateDetails extends AbstractWikiRevDetails {

	@Override
	protected String perform(UserActionContext context) {
		Map<String, String> params = context.getParameters();
		if (params.containsKey("date")) {
			Date date = new Date(Long.parseLong(params.get("date")));
			String header = getHeader(date);

			// append page version diff overview
			Revision rev = RevisionManager.getRM(context).getRevision(date);
			return getRevDetailsTable(rev, header, null, context).toString();
		}
		return SHOW_REVISION_ERROR;
	}

	private static String getHeader(Date date) {
		String s = DateType.DATE_FORMAT.format(date);
		s = "<p class=\"box ok\">Date " + s + " selected.</p>";
		return s;
	}
}

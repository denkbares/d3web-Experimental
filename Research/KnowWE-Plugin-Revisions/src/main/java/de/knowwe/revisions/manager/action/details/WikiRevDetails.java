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
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.revisions.DateType;
import de.knowwe.revisions.Revision;
import de.knowwe.revisions.RevisionType;
import de.knowwe.revisions.manager.RevisionManager;

/**
 * 
 * @author grotheer
 * @created 01.05.2013
 */
public class WikiRevDetails extends AbstractWikiRevDetails {

	protected String perform(UserActionContext context) {
		Map<String, String> params = context.getParameters();

		if (params.containsKey("date") & params.containsKey("rev") & params.containsKey("id")) {
			Date date = new Date(Long.parseLong(params.get("date")));
			String revName = params.get("rev");
			String sectionId = params.get("id");
			String header = getHeader(date, revName);

			// try to get revision comment and add it
			String comment = null;
			@SuppressWarnings("unchecked")
			Section<RevisionType> section = (Section<RevisionType>) Sections.getSection(sectionId);
			if (section != null) {
				comment = RevisionType.getRevisionComment(section);
			}

			// append page version diff overview
			Revision rev = RevisionManager.getRM(context).getRevision(date);
			return getRevDetailsTable(rev, header, comment, context).toString();
		}
		return SHOW_REVISION_ERROR;
	}

	private static String getHeader(Date date, String revTitle) {
		String dateString = DateType.DATE_FORMAT.format(date);

		String result = "<p class=\"box ok\">Revision '" + revTitle
				+ "' selected, which is representing wiki state as of " + dateString + "</p>";
		return result;
	}
}

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
import java.util.HashMap;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.revisions.DatedRevision;
import de.knowwe.revisions.Revision;

/**
 * Abstract action for all revision details for in-wiki revisions (e.g. not for
 * uploaded revision)
 * 
 * @author grotheer
 * @created 01.05.2013
 */
public abstract class AbstractWikiRevDetails extends AbstractRevDetails {

	@Override
	protected String getPageDiffOverview(Revision rev, UserActionContext context) {
		StringBuffer string = new StringBuffer();
		HashMap<String, Integer> compareDiff = rev.compareWithCurrentState();

		// remove the current (revision) page,
		// restoring a old version of this page should be avoided
		compareDiff.remove(context.getTitle());

		for (String title : compareDiff.keySet()) {
			int version = compareDiff.get(title);
			if (version != -1) {
				String pageLink = KnowWEUtils.getLinkHTMLToArticle(title);
				String line;

				if (version != -2) {
					// page was other version
					line = "<div><a class=\"revFileChanged\" onClick=\"showDiff('"
							+ title
							+ "', '"
							+ version
							+ "');\" title=\"This article has changed. Compare with current revision\">";
					line += pageLink + "</a></div>";
				}
				else {
					// page did not exist
					line = "<div class=\"revFileNew\" title=\"This is a new article, did not exist in selected version\">"
							+ pageLink + "</div>";
				}

				string.append(line);
			}
		}
		return string.toString();
	}

	@Override
	/**
	 * 
	 * @created 01.05.2013
	 * @param rev
	 * @param result
	 */
	protected void appendRevisionActions(Revision rev, StringBuffer result) {
		Date date = ((DatedRevision) rev).getDate();

		result.append("<a onClick=\"restoreRev(" + date.getTime()
				+ ");\">Restore</a>");
		result.append("\n");
		result.append("<a onClick=\"downloadRev(" +
				date.getTime()
				+ ");\" title='Download this revision'>Download</a>");
	}
}

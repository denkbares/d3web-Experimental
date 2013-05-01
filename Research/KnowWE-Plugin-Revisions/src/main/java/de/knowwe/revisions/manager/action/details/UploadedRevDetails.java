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

import java.util.HashMap;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.revisions.Revision;
import de.knowwe.revisions.UploadedRevision;
import de.knowwe.revisions.manager.RevisionManager;

/**
 * 
 * @author grotheer
 * @created 01.05.2013
 */
public class UploadedRevDetails extends AbstractRevDetails {

	private static final String HEADER = "<p class=\"box ok\">Uploaded revision selected.</p>";

	@Override
	protected String perform(UserActionContext context) {
		UploadedRevision rev = RevisionManager.getRM(context).getUploadedRevision();
		return getRevDetailsTable(rev, HEADER, null, context).toString();
	}

	/**
	 * 
	 * @created 22.04.2013
	 * @param tableContent
	 * @return
	 */
	@Override
	protected String getPageDiffOverview(Revision rev, UserActionContext context) {
		StringBuffer string = new StringBuffer();
		HashMap<String, Integer> compareDiff = ((UploadedRevision) rev).compareWithCurrentState();

		// remove the current (revision) page,
		// restoring a old version of this page should be avoided
		compareDiff.remove(context.getTitle());

		for (String title : compareDiff.keySet()) {
			int version = compareDiff.get(title);
			if (version != -1) {
				String pageLink = KnowWEUtils.getLinkHTMLToArticle(title);
				String line;

				if (version == 0) {
					// page was other version
					line = "<div><a class=\"revFileChanged\" onClick=\"showUploadedDiff('"
							+ title
							+ "', '"
							+ version
							+ "');\" title=\"This article has changed. Compare with current wiki revision\">";
					line += pageLink + "</a></div>";
				}
				else if (version == -2) {
					// not in upload
					line = "<div class=\"revFileRemoved\" title=\"This article does not exist in upload\">"
							+ pageLink + "</div>";
				}
				else if (version == 1) {
					// not in wiki
					line = "<div class=\"revFileNew\" title=\"This article does not exist in wiki\">"
							+ pageLink + "</div>";
				}
				else {
					// error
					line = "<div class=\"box error\">Error in compare</div>";
				}

				string.append(line);
			}
		}
		return string.toString();
	}

	/**
	 * 
	 * @created 01.05.2013
	 * @param rev
	 * @param result
	 */
	@Override
	protected void appendRevisionActions(Revision rev, StringBuffer result) {
		result.append("<a onClick=\"alert('not yet implemented');\" title='Merge this revision into wiki'>Merge</a>");
		result.append(", ");
		result.append("<a onClick=\"alert('not yet implemented');\" title='Restore this revision>Restore</a>");
	}
}

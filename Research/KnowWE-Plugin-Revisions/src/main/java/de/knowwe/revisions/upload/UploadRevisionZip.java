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
package de.knowwe.revisions.upload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.revisions.UploadedRevision;
import de.knowwe.revisions.manager.RevisionManager;
import de.knowwe.revisions.manager.action.ShowRevision;

/**
 * 
 * @author grotheer
 * @created 22.04.2013
 */
public class UploadRevisionZip extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		try {
			HashMap<String, String> pages = new HashMap<String, String>();
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(context.getRequest());
			for (FileItem item : items) {
				InputStream filecontent = item.getInputStream();

				ZipInputStream zin = new ZipInputStream(filecontent);
				ZipEntry ze;

				while ((ze = zin.getNextEntry()) != null) {
					String title = URLDecoder.decode(ze.getName(), "UTF-8");
					title = title.substring(0, title.length() - 4);
					String content = IOUtils.toString(zin, "UTF-8");
					zin.closeEntry();
					pages.put(title, content);
				}
				zin.close();
				filecontent.close();
			}
			UploadedRevision rev = new UploadedRevision(context.getWeb(), pages);
			RevisionManager.getRM(context).setUploadedRevision(rev);

			String result = getRevisionDetails(context).toString();
			if (result != null && context.getWriter() != null) {
				context.setContentType("text/html; charset=UTF-8");
				context.getWriter().write(result);
			}
		}
		catch (FileUploadException e) {
			e.printStackTrace();
		}
	}

	private static StringBuffer getRevisionDetails(UserActionContext context) {
		StringBuffer tableContent = new StringBuffer();
		UploadedRevision rev = RevisionManager.getRM(context).getUploadedRevision();

		boolean actionsExist = appendChangesTableContent(tableContent,
				(UploadedRevision) rev, context);
		return ShowRevision.wrapTableAroundContent(tableContent, actionsExist, rev);
	}

	/**
	 * 
	 * @created 22.04.2013
	 * @param tableContent
	 * @return
	 */
	private static boolean appendChangesTableContent(StringBuffer tableContent, UploadedRevision rev, UserActionContext context) {
		HashMap<String, Integer> compareDiff = rev.compareWithCurrentState();

		// remove the current (revision) page,
		// restoring a old version of this page should be avoided
		compareDiff.remove(context.getTitle());

		boolean actionsExist = false;
		for (String title : compareDiff.keySet()) {
			int version = compareDiff.get(title);
			if (version != -1) {
				String versionString;
				String compareString = "";

				if (version == 0) {
					// diff
					versionString = "changed";
					compareString = "<a " +
							"title=\"Compare with wiki revision\""
							+ "onClick=\"showDiff('" + title + "', '" + version + "');\""
							+ ">Show Diff</a>";
					actionsExist = true;
				}
				else if (version == -2) {
					versionString = "not in upload";
				}
				else if (version == 1) {
					versionString = "not in wiki";
				}
				else {
					versionString = "erorr";
				}

				// append a new row in html table
				tableContent.append("<tr>");
				tableContent.append("<td>" + KnowWEUtils.getLinkHTMLToArticle(title) + "</td>");
				tableContent.append("<td>" + versionString + "</td>");
				tableContent.append("<td>" + compareString + "</td>");
				tableContent.append("<td>");
			}
		}
		return actionsExist;
	}
}

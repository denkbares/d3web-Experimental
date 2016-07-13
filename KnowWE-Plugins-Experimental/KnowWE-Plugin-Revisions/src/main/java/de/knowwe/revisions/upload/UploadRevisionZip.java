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
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.denkbares.strings.Strings;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.revisions.UploadedRevision;
import de.knowwe.revisions.manager.RevisionManager;

/**
 * Reads a zipped revision from servlet input stream and adds it to revision
 * manager as uploaded revision
 * 
 * @author grotheer
 * @created 22.04.2013
 */
public class UploadRevisionZip extends AbstractAction {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(UserActionContext context) throws IOException {

		HashMap<String, String> pages = new HashMap<>();
		List<FileItem> items = null;
		String zipname = null;
		try {
			items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(context.getRequest());
		}
		catch (FileUploadException e) {
			throw new IOException("error during processing upload", e);
		}

		for (FileItem item : items) {
			zipname = item.getName();
			InputStream filecontent = item.getInputStream();

			ZipInputStream zin = new ZipInputStream(filecontent);
			ZipEntry ze;

			while ((ze = zin.getNextEntry()) != null) {
				String name = ze.getName();
				if (!name.contains("/")) {
					// this is an article
					String title = Strings.decodeURL(name);
					title = title.substring(0, title.length() - 4);
					String content = IOUtils.toString(zin, "UTF-8");
					zin.closeEntry();
					pages.put(title, content);
				}
				else {
					// TODO: what to do here?
					// this is an attachment
					// String[] splittedName = name.split("/");
					// String title = Strings.decodeURL(splittedName[0]);
					// String filename = Strings.decodeURL(splittedName[1]);
					//
					// System.out.println("Attachment: " + name);
					// String content = IOUtils.toString(zin, "UTF-8");
					// Environment.getInstance().getWikiConnector().storeAttachment(title,
					// filename,
					// context.getUserName(), zin);
					zin.closeEntry();
				}
			}
			zin.close();
			filecontent.close();
		}
		if (zipname != null) {
			UploadedRevision rev = new UploadedRevision(context.getWeb(), pages, zipname);
			RevisionManager.getRM(context).setUploadedRevision(rev);
		}
		context.sendRedirect("../Wiki.jsp?page=" + context.getTitle());

	}
}

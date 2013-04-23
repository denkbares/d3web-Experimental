/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.revisions.manager.action;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.revisions.manager.RevisionManager;

/**
 * 
 * @author grotheer
 * @created 22.04.2013
 */
public class DownloadRevisionZip extends AbstractAction {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");

	@Override
	public void execute(UserActionContext context) throws IOException {
		if (context.getParameters().containsKey("date")) {
			String dateParam = context.getParameter("date");
			Date date;
			try {
				date = new Date(Long.parseLong(dateParam));

				String filename = "revision-" + DATE_FORMAT.format(date) + ".zip";
				context.setContentType("application/force-download");
				context.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
				OutputStream outs = context.getOutputStream();
				ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outs));
				zipRev(date, zos, context);
				outs.flush();
				outs.close();
			}
			catch (Exception e) {
				context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.valueOf(e));
				e.printStackTrace();
			}

		}
	}

	/**
	 * Zips the article contents from specified date and writes the resulting
	 * zip-File to the ZipOutputStream.
	 * 
	 * @created 22.04.2013
	 * @param date
	 * @param zos
	 * @throws IOException
	 */
	private void zipRev(Date date, ZipOutputStream zos, UserActionContext context) throws IOException {
		RevisionManager revm = RevisionManager.getRM(context);
		ArticleManager am = revm.getArticleManager(date);
		Collection<Article> articles = am.getArticles();
		for (Article article : articles) {
			zos.putNextEntry(new ZipEntry(URLEncoder.encode(article.getTitle() + ".txt", "UTF-8")));
			zos.write(article.getRootSection().getText().getBytes("UTF-8"));
			zos.closeEntry();
		}
		zos.close();
	}
}

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
package de.knowwe.wikicontentdownload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import de.d3web.utils.Files;
import de.d3web.utils.Streams;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.fingerprint.Fingerprint;
import de.knowwe.jspwiki.JSPWikiConnector;

/**
 * 
 * @author Johanna Latt
 * @created 16.04.2012
 */
public class DownloadWikiZIP extends AbstractAction {

	private File wikiParent;
	public static final String PARAM_FILENAME = "filename";
	public static final String PARAM_FINGERPRINT = "fingerprint";
	public static final String PARAM_VERSIONS = "versions";

	@Override
	public void execute(UserActionContext context) throws IOException {
		JSPWikiConnector con = (JSPWikiConnector) Environment.getInstance().getWikiConnector();
		String wikiSavepath = con.getWikiProperty("var.basedir");
		this.wikiParent = new File(new File(wikiSavepath).getParent());
		String[] directoryName = wikiSavepath.split("/");

		String filename = directoryName[directoryName.length - 1] + ".zip";
		context.setContentType("application/x-bin");
		context.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");

		boolean fingerprint = Boolean.valueOf(context.getParameter(PARAM_FINGERPRINT, "false"));
		boolean versions = Boolean.valueOf(context.getParameter(PARAM_VERSIONS, "false"));

		OutputStream outs = context.getOutputStream();
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outs));
		try {
			zipDir(wikiSavepath, zos, context, 0, versions);
			if (fingerprint) zipFingerprint(zos, context);
			zos.close();
		}
		catch (Exception ioe) {
			ioe.printStackTrace();
			context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.valueOf(ioe));
		}
		outs.flush();
		outs.close();
	}

	private void zipFingerprint(ZipOutputStream zos, UserActionContext context) throws IOException {
		File tempDir = Files.createTempDir();
		try {
			ArticleManager manager = Environment.getInstance().getArticleManager(
					Environment.DEFAULT_WEB);
			Collection<Article> articles = new LinkedList<Article>();
			for (Article article : manager.getArticles()) {
				if (checkRights(article, context)) articles.add(article);
			}
			Fingerprint.createFingerprint(articles, tempDir);
			File[] files = tempDir.listFiles();
			for (File file : files) {
				String relativePath = "fingerprint/" + file.getName();
				addZipEntry(relativePath, file, zos);
			}
		}
		finally {
			Files.recursiveDelete(tempDir);
		}
	}

	/**
	 * Zips the files in the given directory and writes the resulting zip-File
	 * to the ZipOutputStream.
	 * 
	 * @created 21.04.2012
	 * @param savepath
	 * @param zos
	 */
	private void zipDir(String savepath, ZipOutputStream zos, UserActionContext context, int level, boolean includeOld) throws IOException {
		// create a new File object based on the directory we have to zip
		File f = new File(savepath);
		String[] wikiList;
		// get a cleaned listing of the directory content
		// if the current file is on the first level of the Wiki or the name
		// of the current file is "OLD", the rights for those file(s) have
		// to be checked additionally
		if (level == 0) {
			wikiList = cleanFiles(checkRights(f, context));
		}
		else if (level == 1 && f.getName().equals("OLD")) {
			if (!includeOld) return;
			wikiList = cleanFiles(checkRights(f, context));
		}
		else {
			wikiList = cleanFiles(f.list());
		}
		// loop through wikiList and zip the files
		for (int i = 0; i < wikiList.length; i++) {
			File file = new File(f, wikiList[i]);
			// if the File object is a directory, this function is called
			// again to add its content recursively
			if (file.isDirectory()) {
				String filePath = file.getPath();
				zipDir(filePath, zos, context, level + 1, includeOld);
				// loop again
				continue;
			}
			// relativize the savepath of the file against the savepath
			// of the parentfolder of the actual wiki-folder
			String relativePath = wikiParent.toURI().relativize(file.toURI()).getPath();
			addZipEntry(relativePath, file, zos);
		}
	}

	private void addZipEntry(String relativePath, File file, ZipOutputStream zos) throws IOException {
		// if we reached here, the File object wiki was not
		// a directory
		FileInputStream fis = new FileInputStream(file);
		// create a new zip entry
		ZipEntry zip = new ZipEntry(relativePath);
		// place the zip entry in the ZipOutputStream object
		zos.putNextEntry(zip);
		// write the content of the file to the ZipOutputStream
		Streams.stream(fis, zos);
		// close the Stream
		fis.close();
	}

	/**
	 * Returns all files in the given file, for which the user has the rights to
	 * view them.
	 * 
	 * @created 08.05.2012
	 * @param file
	 * @return a list of files (as String array) containing all files of the
	 *         given file, for which the user has the rights to view and zip
	 *         them
	 */
	private String[] checkRights(File file, UserActionContext context) {
		JSPWikiConnector con = (JSPWikiConnector) Environment.getInstance().getWikiConnector();
		ArrayList<String> checkedList = new ArrayList<String>();
		String[] fileList = file.list();
		for (int i = 0; i < fileList.length; i++) {
			File f = new File(file, fileList[i]);
			if (f.isDirectory()) {
				try {
					String title = fileList[i];
					if (title.length() > 3
							&& title.substring(title.length() - 4, title.length()) == "-att") {
						// if the current file is a directory, has more than 3
						// characters and the last four characters of its name
						// are "-att", those last four characters have to be a
						// cut off and the resulting title has to be decoded
						title = URLDecoder.decode(
								title.substring(0, title.length() - 4),
								"UTF8");
					}
					else {
						// all other directories (including all directories in
						// the folder "OLD" of the first level) can be decoded
						// without cutting anything off
						title = URLDecoder.decode(title, "UTF8");
					}
					// if the user has the rights to view the file at
					// fileList[i], the file is added to the checked List
					if (con.userCanViewArticle(title, context.getRequest())) {
						checkedList.add(fileList[i]);
					}
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					String title = URLDecoder.decode(fileList[i], "UTF8");
					if (con.userCanViewArticle(title, context.getRequest())) {
						checkedList.add(fileList[i]);
					}
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

		}
		return checkedList.toArray(new String[checkedList.size()]);
	}

	private boolean checkRights(Article article, UserActionContext context) {
		JSPWikiConnector con = (JSPWikiConnector) Environment.getInstance().getWikiConnector();
		return con.userCanViewArticle(article.getTitle(), context.getRequest());
	}

	/**
	 * Cleans the given fileList from all directories and files that match the
	 * regular expression "\.[\p{L}\d]*" (like ".svn") and should therefore not
	 * be zipped in the zipDir method. The result is returned as a String array.
	 * 
	 * @created 29.04.2012
	 * @param fileList
	 * @return cleaned fileList as String array
	 */
	private String[] cleanFiles(String[] fileList) {
		ArrayList<String> cleanedList = new ArrayList<String>();
		// loop through all files in fileList
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].matches("\\.[\\p{L}\\d]*")) {
				continue;
			}
			else {
				cleanedList.add(fileList[i]);
			}
		}
		return cleanedList.toArray(new String[cleanedList.size()]);
	}
}

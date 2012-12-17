/*
 * Copyright (C) 2012 denkbares GmbH, Germany
 */
package com.denkbares.ciconnector;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.wikiConnector.WikiConnector;

/**
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * @created 17.12.2012
 */
public class SaveCIInfoAction extends AbstractAction {

	public static final String FILENAMESUFFIX = "ciinfo.xml";
	public static final String ARTICLE = "CIConnector";

	private static final String PARAM_BUILD = "build";

	@Override
	public void execute(UserActionContext context) throws IOException {

		HttpServletRequest request = context.getRequest();
		ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
		WikiConnector connector = Environment.getInstance().getWikiConnector();

		try {
			@SuppressWarnings("unchecked")
			List<FileItem> items = uploader.parseRequest(request);
			for (FileItem item : items) {
				InputStream fileContent = item.getInputStream();
				String fileName = context.getParameter(PARAM_BUILD) + "-" + FILENAMESUFFIX;
				System.out.println("Saving: " + fileName);
				connector.storeAttachment(ARTICLE, fileName, context.getUserName(), fileContent);
			}
		}
		catch (FileUploadException e) {
			e.printStackTrace();
		}

		while (Article.isArticleCurrentlyBuilding(Environment.DEFAULT_WEB, ARTICLE)) {
			// wait...
		}

		// trigger rebuild
		Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB, ARTICLE);
		String content = article.getRootSection().getText();
		Environment.getInstance().buildAndRegisterArticle(content, article.getTitle(),
				Environment.DEFAULT_WEB);

	}

}

/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.tables.action;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.d3web.we.poi.PoiUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.ActionContext;
import de.knowwe.core.action.UserActionContext;


/**
 * TODO This class is not used. It was a quickshot trying to upload a file
 * over AJAX. But this will not work!
 * 
 * 
 * @author Johannes Dienst
 * @created 18.10.2011
 */
public class TableImportAction extends AbstractAction {


	@Override
	public void execute(UserActionContext context) {

		try {
			String tableId = context.getParameter("tableId");
			String title = context.getTitle();

			this.uploadFile(context);

			// TODO should i store this here?
			String extensionPath = KnowWEEnvironment.getInstance().getKnowWEExtensionPath()
					+ "/workbook-" +tableId+ ".xls";
			File file = new File(extensionPath);
			String tableMarkup = PoiUtils.importTableFromFile(file, tableId, title, (ActionContext)context);


			// TODO Right method?
			Map<String,String> nodesMap = new HashMap<String,String>();
			nodesMap.put(tableId, tableMarkup);
			KnowWEEnvironment.getInstance().getArticleManager(context.getWeb()).
			replaceKDOMNodesSaveAndBuild(context, title, nodesMap);

			Writer writer = context.getWriter();
			writer.append("Import successful");
			//			in.close();
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).warning(
					"Import of xls failed");
		}

	}

	/**
	 * 
	 * @created 21.10.2011
	 * @param context
	 */
	private void uploadFile(UserActionContext context) {
		HttpServletRequest req = context.getRequest();
		HttpServletResponse res = context.getResponse();
		//		try {
		//		PrintWriter out = res.getWriter();
		//		res.setContentType("text/plain");
		//
		//		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		//		fileItemFactory.setSizeThreshold(1*1024*1024); //1 MB
		//		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		//
		//		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		//		File destinationDir = new File(System.getProperty("java.io.tmpdir"));
		//		fileItemFactory.setRepository( tmpDir );

		/*
		 * Parse the request
		 */
		//			List items = uploadHandler.parseRequest(req);
		String tableId = req.getParameter("tableId");
		String article = req.getParameter("article");
		//			Iterator itr = items.iterator();
		//
		//			while(itr.hasNext()) {
		//				FileItem item = (FileItem) itr.next();
		//				/*
		//				 * Handle Form Fields.
		//				 */
		//				if(item.isFormField()) {
		//					out.println("File Name = "+item.getFieldName()+", Value = "+item.getString());
		//				} else {
		//					//Handle Uploaded files.
		//					out.println("Field Name = "+item.getFieldName()+
		//							", File Name = "+item.getName()+
		//							", Content type = "+item.getContentType()+
		//							", File Size = "+item.getSize());
		//					/*
		//					 * Write file to the ultimate location.
		//					 */
		//					File file = new File(destinationDir, "workbook-" + tableId + ".xls");
		//					item.write(file);
		//
		//					// import the file to Wiki via PoiUtils
		//					PoiUtils.importTableFromFile(file, tableId, article);
		//				}
		//				out.close();
		//			}
		//
		//		} catch(FileUploadException ex) {
		//			//			log("Error encountered while parsing the request",ex);
		//		} catch(Exception ex) {
		//			//			log("Error encountered while uploading file",ex);
		//		}


	}

}

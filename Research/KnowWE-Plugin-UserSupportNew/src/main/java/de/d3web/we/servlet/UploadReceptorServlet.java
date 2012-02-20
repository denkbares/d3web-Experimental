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
package de.d3web.we.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.d3web.we.tables.poi.PoiUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.ActionContext;


/**
 * Simple servlet for receiving a file and launching
 * a command, specified in settings, to run it.
 * 
 * @author Johannes Dienst
 * @created 01.11.2011
 */
public class UploadReceptorServlet extends HttpServlet {

	private static final long serialVersionUID = -4681782387262665746L;

	private ResourceBundle bundle;

	@Override
	public void init() {
		ResourceBundle.getBundle("Usersupport_messages");
	}


	@Override
	public void doGet(HttpServletRequest  req, HttpServletResponse res)
			throws IOException, ServletException {
		doPost(req, res);
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void doPost(HttpServletRequest  req, HttpServletResponse res)
			throws IOException, ServletException {

		try {
			PrintWriter out = res.getWriter();
			res.setContentType("text/plain");

			DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
			fileItemFactory.setSizeThreshold(1*1024*1024); //1 MB
			ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);

			File tmpDir = new File(System.getProperty("java.io.tmpdir"));
			File destinationDir = new File(System.getProperty("java.io.tmpdir"));
			fileItemFactory.setRepository( tmpDir );

			/*
			 * Parse the request
			 */
			List items = uploadHandler.parseRequest(req);
			String tableId = req.getParameter("tableId");
			String article = req.getParameter("article");

			// TODO works everywhere?
			res.sendRedirect("/KnowWE/Wiki.jsp?page="+article);

			Iterator itr = items.iterator();

			while(itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				/*
				 * Handle Form Fields.
				 */
				if(item.isFormField()) {
					out.println("File Name = "+item.getFieldName()+", Value = "+item.getString());
				} else {
					//Handle Uploaded files.
					out.println("Field Name = "+item.getFieldName()+
							", File Name = "+item.getName()+
							", Content type = "+item.getContentType()+
							", File Size = "+item.getSize());
					/*
					 * Write file to the ultimate location.
					 */
					File file = new File(destinationDir, "workbook-" + tableId + ".xls");
					file.deleteOnExit();
					item.write(file);

					// import the file to Wiki via PoiUtils
					Map<String, String> parameters= new HashMap<String, String>();
					parameters.put("KWikiWeb", KnowWEEnvironment.DEFAULT_WEB);
					ActionContext context = new ActionContext(null, null, parameters, req, res, null, null);
					PoiUtils.importTableFromFile(file, tableId, article, context);
				}
				out.close();
			}

		} catch(FileUploadException ex) {
			log("Error encountered while parsing the request",ex);
		} catch(Exception ex) {
			log("Error encountered while uploading file",ex);
		}

	}

}
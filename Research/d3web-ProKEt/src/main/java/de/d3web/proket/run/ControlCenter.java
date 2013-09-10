/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.proket.run;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.proket.utils.FileUtils;
import de.d3web.proket.utils.GlobalSettings;

/**
 * ControlCenter Servlet.
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class ControlCenter extends HttpServlet {

	GlobalSettings defset = GlobalSettings.getInstance();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ControlCenter() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		/*
		 * get HTML template The basic template for the control center is the
		 * index.html. There, some StringTemplate attributes, e.g. $css$, are
		 * defined, that are filled in later on in this class by
		 * st.setAttribute(...)
		 */
		File template = FileUtils.getResourceFile(
				GlobalSettings.getInstance().getApplicationBasePath()
						+ "/controlcenter.st");
		StringTemplate st =
				new StringTemplate(FileUtils.getString(template));

		// set CSS
		File css = FileUtils.getResourceFile(
				GlobalSettings.getInstance().getApplicationBasePath()
						+ "/controlcenter.css");
		String cssString = FileUtils.getString(css);
		st.setAttribute("css", cssString);




		// set dialogs
		File folder = FileUtils.getResourceFile(defset.getPrototypeSpecsPath());
		StringBuilder sb = new StringBuilder();
		File[] files = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return !pathname.getName().startsWith("generated.")
						&& !pathname.getName().startsWith("d3webDialog-");
			}
		});
		/*
		 * Each file is read, and a corresponding URL is assembled that is a
		 * call to the Dialog Servlet with the respective parameters
		 */
		for (File file : files) {
			if(!file.getName().endsWith(".jar")){
			sb.append(MessageFormat.format(
					"<a href=\"Dialog?src={0}\">{0}</a><br />\n",
					URLEncoder.encode(
							file.getName().substring(0,
									file.getName().length() - 4), "UTF-8")));
			}
		}
		st.setAttribute("dialogs", sb.toString());

		// set d3web-KBs
		folder = FileUtils.getResourceFile(defset.getD3webSpecsPath());
		sb = new StringBuilder();
		File[] files2 = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return !pathname.getName().contains(".d3web") &&
						!pathname.getName().contains(".jar");
			}
		});

		for (File file : files2) {
			sb.append(MessageFormat.format(
					"<a href=\"D3webDialog?src={0}\">{0}</a><br />\n",
					URLEncoder.encode(file.getName()
							.substring(0, file.getName().length() - 4), "UTF-8")));
		}

		st.setAttribute("d3web", sb.toString());
		
		// output
		PrintWriter writer = response.getWriter();
		writer.write(st.toString());
		writer.close();
	}

}

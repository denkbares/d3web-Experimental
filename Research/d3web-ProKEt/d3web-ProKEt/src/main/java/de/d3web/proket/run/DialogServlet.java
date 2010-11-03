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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.d3web.proket.data.DialogTree;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.input.xml.IParser;
import de.d3web.proket.input.xml.ParseException;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.output.render.IRenderer;
import de.d3web.proket.output.render.Renderer;

/**
 * Servlet for rendering normal dialogs.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class DialogServlet extends HttpServlet {

	private static final long serialVersionUID = -1514789465295324518L;

	public static DialogTree parseInput(HttpServletRequest request,
			HttpServletResponse response, PrintWriter writer)
			throws ParseException {
		// parse the input source
		IParser parser = null;
		String sourceParser = "";
		// String typeParameter = request.getParameter("type");
		// if (typeParameter != null && typeParameter.matches("[\\w]{1,20}")) {
		// // validation
		// sourceParser = typeParameter;
		// }
		sourceParser = "de.d3web.proket.input.xml.XMLParser";
		String source = "Standarddialog";
		if (request.getParameter("src") != null) {
			source = request.getParameter("src");
		}
		try {
			Class<?> parserClass = Class.forName(sourceParser);
			parser = (IParser) parserClass.getConstructor(Object.class)
					.newInstance(source);
		} catch (ClassNotFoundException ex) {
			throw new ParseException(MessageFormat.format(
					"Unable to find class \"{0}\" for loading the source: {1}",
					sourceParser, ex.getLocalizedMessage()));
		} catch (IllegalAccessException ex) {
			throw new ParseException(
					MessageFormat
							.format("Unable to create class \"{0}\" for loading the source: {1}",
									sourceParser, ex.getLocalizedMessage()));
		} catch (InstantiationException ex) {
			throw new ParseException(
					MessageFormat
							.format("Unable to create class \"{0}\" for loading the source: {1}",
									sourceParser, ex.getLocalizedMessage()));
		} catch (IllegalArgumentException ex) {
			throw new ParseException(
					MessageFormat
							.format("Unable to create class \"{0}\" for loading the source: {1}",
									sourceParser, ex.getLocalizedMessage()));
		} catch (SecurityException ex) {
			throw new ParseException(
					MessageFormat
							.format("Unable to create class \"{0}\" for loading the source: {1}",
									sourceParser, ex.getLocalizedMessage()));
		} catch (InvocationTargetException ex) {
			throw new ParseException(
					MessageFormat
							.format("Unable to create class \"{0}\" for loading the source: {1}",
									sourceParser, ex.getLocalizedMessage()));
		} catch (NoSuchMethodException ex) {
			throw new ParseException(
					MessageFormat
							.format("Unable to create class \"{0}\" for loading the source: {1}",
									sourceParser, ex.getLocalizedMessage()));
		}

		// load the dialog into memory
		return parser.getTree();
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DialogServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// prepare output document
		HttpSession session = request.getSession(true);
		ServletContext context = session.getServletContext();
		String realContextPath = context.getRealPath(request.getContextPath());
		System.out.println("Living in: " + realContextPath);

		// String sessionID = session.getId();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		DialogTree dialogTree;
		try {
			dialogTree = parseInput(request, response, writer);
			System.out.println("Tree parsed:\n" + dialogTree);
		}
		catch (ParseException e) {
			writer.append(e.getLocalizedMessage());
			writer.flush();
			writer.close();
			return;
		}

		String partialId = request.getParameter("partialId");
		ContainerCollection cc;
		String html;
		if (partialId == null) {
			// decorate root element (recursively)
			IRenderer rootRenderer = Renderer.getRenderer(dialogTree.getRoot());
			cc = rootRenderer.renderRoot(dialogTree);
			// combine the containers to one single HTML file
			html = cc.html.toString();
		} else {
			// partial rendering
			IDialogObject child = dialogTree.getById(partialId);
			IRenderer rootRenderer = Renderer.getRenderer(child);
			cc = new ContainerCollection();
			html = rootRenderer.renderDialogObject(cc, child);
		}

		// deliver the rendered output
		writer.print(html);
		writer.close();
	}

}
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
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.antlr.stringtemplate.StringTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.d3web.proket.data.DialogTree;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.input.xml.IParser;
import de.d3web.proket.output.render.Renderer;
import de.d3web.proket.utils.ClassUtils;
import de.d3web.proket.utils.ProKEtLogger;
import de.d3web.proket.utils.StringUtils;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Servlet to debug dialogs. Mainly useful for investigating what the class
 * finding algorithm does.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class DebugServlet extends HttpServlet {

	private static final Logger logger = ProKEtLogger.getLogger();
	private static final long serialVersionUID = 6798812629861155410L;

	public static String nodeToString(Element elem) {
		StringBuilder bui = new StringBuilder();

		// node name
		bui.append("<" + elem.getNodeName());

		NamedNodeMap attrs = elem.getAttributes();
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i);
				bui.append(MessageFormat.format(" {0}=\"{1}\"",
						attr.getNodeName(), attr.getNodeValue()));
			}
		}

		bui.append(">");

		return bui.toString();
	}

	private IParser parser;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DebugServlet() {
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
		// System.out.println("Living in: " + realContextPath);

		// String sessionID = session.getId();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		// parse the input source
		parser = null;
		String sourceParser = "XML";
		String typeParameter = request.getParameter("type");
		if (typeParameter != null && typeParameter.matches("[\\w]{1,20}")) { // validation
			sourceParser = typeParameter;
		}
		sourceParser = "de.d3web.proket.input.xml."
				+ sourceParser + "Parser";
		String source = "Standarddialog";
		if (request.getParameter("src") != null) {
			source = request.getParameter("src");
		}
		try {
			Class<?> parserClass = Class.forName(sourceParser);
			parser = (IParser) parserClass.getConstructor(Object.class)
					.newInstance(source);
		} catch (ClassNotFoundException ex) {
			printLoadingClassError(writer, sourceParser);
			return;
		} catch (IllegalAccessException ex) {
			printLoadingClassError(writer, sourceParser);
			return;
		} catch (InstantiationException ex) {
			printLoadingClassError(writer, sourceParser);
			return;
		} catch (IllegalArgumentException e) {
			printLoadingClassError(writer, sourceParser);
			return;
		} catch (SecurityException e) {
			printLoadingClassError(writer, sourceParser);
			return;
		} catch (InvocationTargetException e) {
			printLoadingClassError(writer, sourceParser);
			return;
		} catch (NoSuchMethodException e) {
			printLoadingClassError(writer, sourceParser);
			return;
		}

		// load the dialog into memory
		DialogTree dialogTree = parser.getTree();
		System.out.println("Tree parsed:\n" + dialogTree);

		StringTemplate dialogTemplate = TemplateUtils.getStringTemplate(
				"DebugDialog", "html");
		StringTemplate itemTemplate = TemplateUtils.getStringTemplate(
				"DebugDialogElement", "html");

		renderElement(dialogTree.getRoot(), dialogTemplate, itemTemplate, 0);

		// deliver the rendered output
		writer.print(dialogTemplate.toString());
		writer.close();
	}

	private void printLoadingClassError(PrintWriter writer, String sourceParser) {
		String logMessage;
		logMessage = MessageFormat.format(
				"Unable to find class \"{0}\" for loading the source.",
				sourceParser);
		logger.severe(logMessage);
		writer.print(logMessage);
		writer.close();
	}

	private void renderElement(IDialogObject dialogObject,
			StringTemplate dialogTemplate, StringTemplate elementTemplate,
			int indentionLevel) {

		// this element
		elementTemplate.reset();
		elementTemplate.setAttribute("content",
				StringUtils.toHTML(nodeToString(dialogObject.getXMLTag())));
		elementTemplate.setAttribute("indent", indentionLevel * 30);
		elementTemplate.setAttribute("vcn", dialogObject.getVirtualClassName());
		elementTemplate.setAttribute("parser",
				parser.getParserClass(dialogObject.getXMLTag()));
		elementTemplate.setAttribute("internal", ClassUtils.getBestClass(
				dialogObject.getVirtualClassName(),
				"de.d3web.proket.data", ""));
		elementTemplate.setAttribute("renderer",
				Renderer.getRenderer(dialogObject).getClass());
		elementTemplate.setAttribute(
				"html_template",
				TemplateUtils.getTemplateFile(
						dialogObject.getVirtualClassName(), "html"));

		// System.out.println(dialogObject.getVirtualClassName());
		dialogTemplate.setAttribute("items", elementTemplate.toString());

		// this element's children
		indentionLevel++;
		Vector<IDialogObject> children = dialogObject.getChildren();
		for (IDialogObject child : children) {
			renderElement(child, dialogTemplate, elementTemplate,
					indentionLevel);
		}
		indentionLevel--;

	}
}
/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.run;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webRendererMapping;
import de.d3web.proket.d3web.output.render.HerniaDefaultRootD3webRenderer;
import de.d3web.proket.output.container.ContainerCollection;

/**
 * Servlet for creating and using dialogs with d3web binding. Binding is more of
 * a loose binding: if no d3web etc session exists, a new d3web session is
 * created and knowledge base and specs are read from the corresponding XML
 * specfication.
 * 
 * Basically, when the user selects answers in the dialog, those are transferred
 * back via AJAX calls and processed by this servlet. Here, values are
 * propagated to the d3web session (and later re-read by the renderers).
 * 
 * Both browser refresh and pressing the "new case"/"neuer Fall" Button in the
 * dialog leads to the creation of a new d3web session, i.e. all values set so
 * far are discarded, and an "empty" problem solving session begins.
 * 
 * @author Martina Freiberg
 * 
 * @date 14.01.2011; Update: 28/01/2011
 * 
 */
public class EuraHSDialog extends D3webDialog {

	private static final long serialVersionUID = -4790211381203716706L;

	@Override
	protected String getSource() {
		String source = "Hernia_Standard";
		return source;
	}

	/**
	 * Basic servlet method for displaying the dialog.
	 * 
	 * @created 28.01.2011
	 * @param request
	 * @param response
	 * @param d3webSession
	 * @throws IOException
	 */
	@Override
	protected void show(HttpServletRequest request, HttpServletResponse response,
			HttpSession httpSession)
			throws IOException {

		PrintWriter writer = response.getWriter();

		// get the root renderer --> call getRenderer with null
		HerniaDefaultRootD3webRenderer d3webr = (HerniaDefaultRootD3webRenderer)
				D3webRendererMapping.getInstance().getRenderer(null);

		// new ContainerCollection needed each time to get an updated dialog
		ContainerCollection cc = new ContainerCollection();

		Session d3webSess = (Session) httpSession.getAttribute(D3WEB_SESSION);
		cc = d3webr.renderRoot(cc, d3webSess, httpSession);

		writer.print(cc.html.toString()); // deliver the rendered output

		writer.close(); // and close
	}

	/**
	 * Handle login of new user
	 * 
	 * @created 29.04.2011
	 * @param req
	 * @param res
	 * @param httpSession
	 * @throws IOException
	 */
	@Override
	protected void loginUsrDat(HttpServletRequest req,
			HttpServletResponse res, HttpSession httpSession)
			throws IOException {

		// fetch the information sent via the request string from login
		String u = req.getParameter("u");

		// get the response writer for communicating back via Ajax
		PrintWriter writer = res.getWriter();

		httpSession.setMaxInactiveInterval(60 * 60);

		// if no valid login
		// if (!permitUser(u, p)) {
		//
		// // causes JS to display error message
		// writer.append("nosuccess");
		// return;
		// }

		// set user attribute for the HttpSession
		httpSession.setAttribute("user", u);

		httpSession.setAttribute("lastLoaded", "");

		/*
		 * in case we should have more than one user per clinic, we distinguish
		 * them by adding "_1"... to the clinic name, i.e. WUE_1, WUE_2 etc.
		 * Thus we need to extract part of the login name that also denotes the
		 * clinic name and thus the subfolder, where cases are stored that
		 * should be visible for THIS user.
		 */
		int splitter = u.indexOf("_");
		if (splitter != -1) {
			String toReplace = u.substring(splitter, u.length());
			String userSubfolder = u.replace(toReplace, "");
			httpSession.setAttribute("user", userSubfolder);
		}

		// causes JS to start new session and d3web case finally
		writer.append("newUser");
	}

}

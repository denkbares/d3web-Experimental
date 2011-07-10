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
package de.d3web.proket.d3web.output.render;

import javax.servlet.http.HttpSession;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.proket.d3web.utils.PersistenceD3webUtils;

/**
 * Basic Renderer Class for d3web-based dialogs. Defines the basic rendering of
 * d3web dialogs and methods, required by all rendering sub-classes.
 * 
 * 
 * @author Martina Freiberg, Albrecht Striffler
 * @created 13.01.2011
 */
public class HerniaDefaultRootD3webRenderer extends DefaultRootD3webRenderer {

	@Override
	public String getAvailableFiles(HttpSession http) {
		String opts;
		if ((String) http.getAttribute("login") != null
				&& (String) http.getAttribute("login") != "" &&
				(String) http.getAttribute("nname") != null
				&& (String) http.getAttribute("nname") != "" &&
				(String) http.getAttribute("institute") != null
				&& (String) http.getAttribute("institute") != "") {

			String idString = (String) http.getAttribute("login") +
					(String) http.getAttribute("nname") + (String) http.getAttribute("institute");
			opts = PersistenceD3webUtils.getCaseListFilterByID(idString);
		}
		else {
			opts = PersistenceD3webUtils.getCaseList();
		}
		return opts;
	}

	@Override
	public void addButtons(StringTemplate st) {
		// add some buttons for basic functionality
		st.setAttribute("loadcase", "true");
		st.setAttribute("savecase", "true");
		st.setAttribute("reset", "true");

		// ONLY FOR HERNIA, 3 custom buttons
		st.setAttribute("summary", true);
		st.setAttribute("statistics", true);
		st.setAttribute("followup", true);

	}

}

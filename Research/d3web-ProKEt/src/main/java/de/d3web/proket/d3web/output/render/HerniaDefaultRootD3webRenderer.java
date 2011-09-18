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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	private static Map<String, Map<String, String>> caseCache = new HashMap<String, Map<String, String>>();

	@Override
	public void setDialogSpecificAttributes(HttpSession httpSession, StringTemplate st) {
		// ONLY FOR HERNIA, 3 custom buttons
		st.setAttribute("summary", true);
		st.setAttribute("statistics", true);
		st.setAttribute("followupbutton", true);

		String opts = renderFollowUpList((String) httpSession.getAttribute("user"));
		st.setAttribute("followupdialog", opts);

	}

	private String renderFollowUpList(String user) {

		File[] caseFiles = PersistenceD3webUtils.getCaseList(user);
		StringBuffer cases = new StringBuffer();
		if (caseFiles != null && caseFiles.length > 0) {

			Arrays.sort(caseFiles);

			for (File caseFile : caseFiles) {
				if (!caseFile.getName().startsWith(PersistenceD3webUtils.AUTOSAVE)) {
					cases.append("<option");
					String filename = caseFile.getName().substring(0,
							caseFile.getName().lastIndexOf("."));
					cases.append(" title='"
							+ filename + "'>");
					cases.append("<span style='border=1px solid;'>" + filename + "</span>");
					cases.append("<span style='border=1px solid;'>"
							+ new SimpleDateFormat("dd.MM.yyyy").format(new Date(
									caseFile.lastModified()))
							+ "</span>");
					cases.append("</option>");
				}
			}
		}
		return cases.toString();
	}

}

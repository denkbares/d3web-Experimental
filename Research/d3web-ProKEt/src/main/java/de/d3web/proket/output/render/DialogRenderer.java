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

package de.d3web.proket.output.render;

import java.util.Vector;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;
import de.d3web.proket.data.Dialog;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.data.Questionnaire;
import de.d3web.proket.input.xml.IDialogObjectParser;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.ClassUtils;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Renderer that handles some general dialog specific tasks.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class DialogRenderer extends Renderer {

	public static IRenderer getRenderer(IDialogObjectParser dialogObject) {
		IRenderer renderer = (IRenderer) ClassUtils.getBestObject(
				dialogObject, "de.d3web.proket.output.render",
				"Renderer");
		return renderer;
	}

	protected void globalJS(de.d3web.proket.output.container.ContainerCollection cc,
			de.d3web.proket.data.Dialog dialog) {

		cc.js.add("$(function() {init_all();});", 1);
		cc.js.add("function init_all() {", 1);
		cc.js.add("building = true;", 2);
		cc.js.add("setup();", 2);
		cc.js.add("building = false;", 2);
		cc.js.add("remark();", 2);
		cc.js.add("generate_tooltip_functions();", 3);

		cc.js.add("}", 31);
	}


	/**
	 * Recursively build the navigation tree from the questionnaire structure.
	 * 
	 * @param startElement
	 *            {@link IDialogObject} to start searching down from.
	 * @param st
	 *            StringTemplate to add the data to.
	 */
	private void makeNavigation(IDialogObject startElement, StringTemplate st) {
		Vector<IDialogObject> children = startElement.getChildren();
		for (IDialogObject child : children) {
			if (child instanceof Questionnaire) {
				// create subItem
				StringTemplate childSt = TemplateUtils.getStringTemplate(
						"NavigationItem", "html");
				// set title and link
				if (child.getTitle() != null) {
					childSt.setAttribute("title", child.getTitle());
				} else {
					childSt.setAttribute("title", child.getId());
				}
				childSt.setAttribute("fullId", child.getFullId());
				childSt.setAttribute("link", "javascript:show_questionnaire(\'"
						+ child.getFullId() + "');");

				makeNavigation(child, childSt);
				st.setAttribute("navlist", childSt.toString());
			}
		}
	}

	@Override
	protected String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject, boolean excludeChildren, boolean force,
			Session session) {

		StringTemplate st = TemplateUtils.getStringTemplate(
				dialogObject.getVirtualClassName(), "html");
		if (st == null) {
			return null;
		}

		fillTemplate(dialogObject, st);

		handleCss(cc, dialogObject);

		/*
		 * build the navigation
		 */
		if (dialogObject instanceof Dialog) {
			Dialog dialog = (Dialog) dialogObject;
			if(dialog.isLogging()){
                            cc.js.enableClickLogging();
                        }
			makeNavigation(dialogObject, st);
			
		}

		// children
		renderChildren(st, cc, dialogObject, force);

		// add JS/CSS
		// some global JS goes here
		globalJS(cc, (Dialog) dialogObject);
		st.setAttribute("fullcss", cc.css.generateOutput());
		st.setAttribute("fulljs", cc.js.generateOutput());

		// save to output
		st.setDefaultArgumentValues();
		return st.toString();
	}
}

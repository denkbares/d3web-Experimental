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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.output.container.ContainerCollection;

/**
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class ImageQuestionRenderer extends Renderer {

	@Override
	protected void renderChildren(StringTemplate st, ContainerCollection cc,
			IDialogObject dialogObject, boolean force) {
		// first run
		super.renderChildren(st, cc, dialogObject, force);
		// change the children
		Vector<IDialogObject> children = dialogObject.getChildren();
		Map<IDialogObject, String> typeMap = new HashMap<IDialogObject, String>();
		for (IDialogObject child : children) {
			typeMap.put(child, child.getType());
			child.setType(dialogObject.getInheritableAttributes()
					.getAnswerType());
		}
		/*
		 * second run, a little trick because renderChildren does not return a
		 * String
		 */
		StringTemplate childrenText = new StringTemplate("$children$");
		super.renderChildren(childrenText, cc, dialogObject, true);
		st.setAttribute("childrenText", childrenText.toString());
		/* reset the types */
		for (IDialogObject key : typeMap.keySet()) {
			key.setType(typeMap.get(key));
		}
	}

	@Override
	public String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject, boolean excludeChildren, boolean force) {
		cc.js.enableImagequestions();
		cc.js.add("$('img[usemap]').maphilight()", 29);

		// force table (image left, answers right)
		dialogObject.getInheritableAttributes().setColumns(2);

		return super.renderDialogObject(cc, dialogObject, excludeChildren,
				force);
	}

}

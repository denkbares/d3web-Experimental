/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.visitor;

import java.util.List;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.visitor.Visitor;
import de.d3web.we.utils.KnowWEUtils;

public class RenderKDOMVisitor implements Visitor {

	private StringBuffer buffi;

	@Override
	public void visit(Section s) {
		buffi = new StringBuffer();
		renderSubtree(s, 0, buffi);
	}

	public String getRenderedKDOM() {
		return buffi.toString();
	}

	private void renderSubtree(Section s, int i, StringBuffer buffi) {
		buffi.append(getDashes(i));
		buffi.append(" <span style=\"color:black\" title=\"");
		buffi.append(" ID: " + s.getID() + "\n");
		buffi.append("\">");
		buffi.append(KnowWEUtils.html_escape(s.verbalize()));
		buffi.append("</span>\n <br />"); // \n only to avoid HTML-code being
											// cut by JspWiki (String.length >
											// 10000)
		i++;
		List<Section> children = s.getChildren();
		if (children.size() == 1
				&& children.get(0).getObjectType() instanceof PlainText) {

		}
		else {
			for (Section section : children) {
				renderSubtree(section, i, buffi);
			}
		}
	}

	private String getDashes(int cnt) {
		StringBuffer dashes = new StringBuffer();
		for (int i = 0; i < cnt; i++) {
			dashes.append("-");
		}
		return dashes.toString();
	}

}

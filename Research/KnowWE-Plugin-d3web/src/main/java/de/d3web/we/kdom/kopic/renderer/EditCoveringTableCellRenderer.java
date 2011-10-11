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

package de.d3web.we.kdom.kopic.renderer;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

@SuppressWarnings("unchecked")
public class EditCoveringTableCellRenderer extends KnowWEDomRenderer {

	private static String[] options =
				{
						"  ", "ja", "! ", "--", "++", "1 ", "2 ", "3 ", "4 ", "5 ", "10" };

	@Override
	public void render(KnowWEArticle article, Section sec, UserContext user, StringBuilder string) {
		String currentOp = sec.getOriginalText().trim();
		String secID = sec.getID();

		StringBuilder b = new StringBuilder();
		b.append("<select id=\"editCell"
					+ secID
					+ "\" class='js-cell-change' rel='{id: "
					+ secID + ", title: "
					+ sec.getTitle() + "}'>");
		b.append("<option value=\""
					+ currentOp + "\">"
					+ currentOp + "</option>");
		for (int i = 0; i < options.length; i++) {
			if (!options[i].equals(currentOp)) {
				b.append("<option value=\"" + options[i] + "\">" + options[i]
						+ "</option>");
			}
		}
		b.append("</select>");
		string.append(KnowWEUtils.maskHTML(b.toString()));
	}

}

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

import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;

public class DefaultLineNumberDeligateRenderer extends KnowWEDomRenderer {

	@Override
	public void render(KnowWEArticle article, Section sec, UserContext user, StringBuilder string) {

		int lineNum = 1;
		List<Section> children = sec.getChildren();
		for (Section child : children) {
			if (((AbstractType) child.get()).isNumberedType()) {
				String numberString = Integer.toString(lineNum);
				if (numberString.length() == 1) {
					numberString = "  " + numberString;
				}
				if (numberString.length() == 2) {
					numberString = " " + numberString;
				}
				string.append(numberString + " | ");
				child.get().getRenderer().render(article, child, user, string);
				lineNum++;
			}
			else {
				child.get().getRenderer().render(article, child, user, string);
			}
		}
	}

}

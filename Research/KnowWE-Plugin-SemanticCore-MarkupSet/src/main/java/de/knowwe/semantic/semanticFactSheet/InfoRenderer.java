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

/**
 *
 */
package de.knowwe.semantic.semanticFactSheet;

import java.util.Map;

import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.xml.AbstractXMLType;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * @author kazamatzuri
 *
 */
public class InfoRenderer extends KnowWEDomRenderer {

	private static InfoRenderer instance;

	public static synchronized InfoRenderer getInstance() {
		if (instance == null) instance = new InfoRenderer();
		return instance;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.d3web.we.dom.renderer.KnowWEDomRenderer#render(de.d3web.we.dom.Section
	 * , java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void render(KnowWEArticle article, Section sec,
			UserContext user, StringBuilder string) {
		boolean verbose = false;
		Map<String, String> params = AbstractXMLType
				.getAttributeMapFor(sec.getFather());

		if (params != null) {
			if (params.containsKey("verbose")) {
				verbose = true;
			}
		}

		if (!verbose) return;

		String text = sec.getOriginalText();
		if (sec.get() instanceof InfoContent) {
			IntermediateOwlObject io = (IntermediateOwlObject) KnowWEUtils.getStoredObject(sec,
					OwlHelper.IOO);

			if (!io.getValidPropFlag()) {
				text = KnowWEUtils.maskHTML("<p class=\"box error\">invalid property:"
								+ io.getBadAttribute() + "</p>");
			}

			if (true) {
				for (String cur : text.split("\r\n|\r|\n")) {
					if (cur.trim().length() > 0) string.append(cur.trim() + "\\\\");
				}
			}
		}
	}

}

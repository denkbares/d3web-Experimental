/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.taghandler;

import java.util.HashSet;
import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.Section;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

/**
 * The ObjectTypeTreeHandler renders the hierarchy of Types,
 * starting with the RootType. Grey items were already printed out somewhere
 * else in the tree, clicking on them jumps to the initial occurrence.
 * 
 * @author Alex Legler
 * @created 20.10.2010
 */
public class ObjectTypeTreeHandler extends AbstractTagHandler {

	public ObjectTypeTreeHandler() {
		super("ObjectTypeTree");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, KnowWEUserContext userContext, Map<String, String> parameters) {
		Type t = KnowWEEnvironment.getInstance().getRootType();

		return visitNode(t, 1, new HashSet<String>(), false);
	}

	private String visitNode(Type t, int level, HashSet<String> visitedNodes, boolean doNotRecurse) {
		// The anti-cycle strategy is rather simple:
		// Maintain a list of already visited types with the associated classes
		visitedNodes.add(t.getName() + t.getClass().getCanonicalName());

		StringBuilder sb = new StringBuilder();

		sb.append(asterisks(level));
		sb.append("%%(text-decoration: underline) ");

		// Create an anchor to be able to jump to the first occurrence
		sb.append(KnowWEUtils.maskHTML("<span id=\"objecttype-" + t.getName() + "\">"));
		sb.append(t.getName());
		sb.append(KnowWEUtils.maskHTML("</span>"));

		// add the full canonical name
		sb.append("%% %%(color: grey; font-size: 80%) (" + t.getClass().getCanonicalName() + ") %%");

		// and the renderer
		sb.append("\\\\\n %%(font-size: 70%) Renderer: __"
				+ t.getRenderer().getClass().getSimpleName() + "__%% \n");

		for (Type child_type : t.getAllowedChildrenTypes()) {
			if (doNotRecurse) {
				sb.append(asterisks(level + 1));
				sb.append(KnowWEUtils.maskHTML("<a href=\"#objecttype-"
						+ child_type.getName()
						+ "\" style=\"color: grey !important; text-decoration: underline\" title=\"Go to first occurrence\">"));
				sb.append(child_type.getName());
				sb.append(KnowWEUtils.maskHTML("</a>\n"));

			}
			else {
				if (visitedNodes.contains(child_type.getName()
						+ child_type.getClass().getCanonicalName())) {

					sb.append(visitNode(child_type, level + 1, visitedNodes, true));
				}
				else {
					sb.append(visitNode(child_type, level + 1, visitedNodes, false));
				}
			}
		}

		return sb.toString();
	}

	private String asterisks(int count) {
		StringBuilder s = new StringBuilder();
		for (; count > 0; count--) {
			s.append("*");
		}
		return s.toString();
	}
}

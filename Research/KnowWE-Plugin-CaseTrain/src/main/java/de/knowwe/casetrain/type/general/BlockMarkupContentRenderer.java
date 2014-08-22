/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.casetrain.type.general;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.knowwe.casetrain.util.Utils;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

/**
 * 
 * @author Johannes Dienst
 * @created 06.06.2011
 */
public class BlockMarkupContentRenderer implements Renderer {

	private static final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");;

	private static BlockMarkupContentRenderer unique;

	public static BlockMarkupContentRenderer getInstance() {
		if (unique == null) unique = new BlockMarkupContentRenderer();
		return unique;
	}

	private BlockMarkupContentRenderer() {
	}

	@Override
	public void render(Section<?> sec, UserContext user,
			RenderResult string) {

		string.appendHtml("%%collapsebox-closed \r\n");
		String tString = "";

		// Only 1 title here
		List<Section<Title>> titl = Sections.children(sec, Title.class);
		if (!titl.isEmpty()) {
			tString = titl.get(0).getText().trim();
		}
		else if (bundle.getString(sec.getParent().get().getName()) != null) {
			tString = bundle.getString(sec.getParent().get().getName());
		}

		string.appendHtml("! "
				+ tString
				+ "\r\n");

		string.appendHtml("<pre id=\""
				+ sec.getID()
				+ "\" class=\"casetrain-instantedit-pre\">");
		string.appendHtml("<div class=\"casetrain-instantedit\">"
				// + getFrameName(sec)
				// + getEditorIcon(sec)
				+ Utils.renderTools(sec, user)
				// + getLink(sec)
				+ "</div>");

		// Dont ask why I use this hack here!
		// DelegateRenderer should find the SubblockMarkupRender, but in fact
		// it does NOT! Johannes
		List<Section<?>> secsWithoutTitle = sec.getChildren()
				.stream().filter(section -> !section.get().getClass().equals(Title.class))
				.collect(Collectors.toList());
		for (Section<?> s : secsWithoutTitle) {
			s.get().getRenderer().render(s, user, string);
		}

		string.append("/%\r\n");

		string.appendHtml("</pre>");
	}

}

/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.casetrain.type.general;

import java.util.List;
import java.util.ResourceBundle;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;


/**
 * 
 * @author Johannes Dienst
 * @created 06.06.2011
 */
public class BlockMarkupContentRenderer extends KnowWEDomRenderer<BlockMarkupContent> {

	private static final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");;

	private static BlockMarkupContentRenderer unique;

	public static BlockMarkupContentRenderer getInstance() {
		if (unique == null)
			unique = new BlockMarkupContentRenderer();
		return unique;
	}

	private BlockMarkupContentRenderer() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void render(KnowWEArticle article, Section<BlockMarkupContent> sec,
			UserContext user, StringBuilder string) {

		string.append(KnowWEUtils.maskHTML("%%collapsebox-closed \r\n"));
		String tString = "";

		Section<Title> titl = Sections.findSuccessor(sec, Title.class);
		if (titl != null) {
			tString = titl.getOriginalText().trim();
		} else if(bundle.getString(sec.getFather().get().getName()) != null) {
			tString = bundle.getString(sec.getFather().get().getName());
		}

		string.append(
				KnowWEUtils.maskHTML("! "
						+ tString
						+ "\r\n")
		);

		// Dont ask why I use this hack here!
		// DelegateRenderer should find the SubblockMarkupRender, but in fact
		// it does NOT! Johannes
		Class<?>[] array = {Title.class};
		List<Section<?>> secsWithoutTitle = Sections.getChildrenExceptExactType(sec, array);
		for (Section<?> s :secsWithoutTitle) {
			s.get().getRenderer().render(article, s, user, string);
		}

		string.append(KnowWEUtils.maskHTML("/%\r\n"));
		return;
	}

}

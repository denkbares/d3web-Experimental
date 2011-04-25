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

package de.knowwe.caseTrain.type.general;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.type.AnonymousTypeInvisible;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.caseTrain.renderer.MouseOverTitleRenderer;

/**
 * 
 * A simple Type to capture a section of a page using a start/end-tag and a
 * keyword. Vontent-types can be added. Error messages are rendered as summary
 * at the top of the block.
 * 
 * TODO: generalize with BlockMarkupType?
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class SubblockMarkup extends AbstractType {

	private final String key;

	public static final String START_TAG = "<";
	public static final String END_TAG = ">";

	private final SubBlockMarkupContent content;

	public String getCSSClass() {
		return this.getClass().getSimpleName();
	}

	public SubblockMarkup(String key) {
		this.key = key;

		this.sectionFinder = new RegexSectionFinder("\\r?\\n(" + START_TAG
				+ key + ":" + "(.*?)" + END_TAG + ")\\r?\\n", Pattern.DOTALL, 1);

		this.setCustomRenderer(new KnowWEDomRenderer<SubblockMarkup>() {

			@Override
			public void render(KnowWEArticle article, Section<SubblockMarkup> sec, UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML("<div class='"
						+ sec.get().getCSSClass()
						+ "'>"));
				MouseOverTitleRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</div>"));

			}
		});

		content = new SubBlockMarkupContent();

		// TODO: reuse regex above
		String regex2 = START_TAG
		+ key + ":" + "(.*?)" + END_TAG;
		content.setSectionFinder(new RegexSectionFinder(regex2, Pattern.DOTALL, 1));
		this.addChildType(content);

		AnonymousTypeInvisible keytext = new AnonymousTypeInvisible("syntax");
		this.addChildType(keytext);
	}

	protected String getKey() {
		return this.key;
	}

	/**
	 * Types can be added to contentType of the SubblockMarkup
	 * for refining the KDOM.
	 * 
	 * @created 25.04.2011
	 * @param t
	 */
	public void addContentType(Type t) {
		content.addChildType(t);
	}

}

class SubBlockMarkupContent extends AbstractType {

}

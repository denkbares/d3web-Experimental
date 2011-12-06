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
package de.knowwe.wimvent.ruleTable;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class WimVentTableMarkup extends DefaultMarkupType {

	public static final String COLUMN_NUMBER_KEY = "actionColumns";

	public WimVentTableMarkup(DefaultMarkup markup) {
		super(markup);
	}

	@Override
	public KnowWEDomRenderer getRenderer() {
		return new KnowWEDomRenderer() {
			@Override
			public void render(KnowWEArticle article, Section sec, UserContext user, StringBuilder string) {
				// user.getParameters().put(TableRenderer.QUICK_EDIT_FLAG,
				// "false");
				DelegateRenderer.getInstance().render(article, sec, user, string);
			}
		};
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("ruleTable");
		m.addContentType(new WimVentTable());
		m.addAnnotation(COLUMN_NUMBER_KEY, true);
	}

	public WimVentTableMarkup() {
		super(m);

	}
}

/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.searchbox;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author jochenreutelshofer
 * @created 15.04.2013
 */
public class SearchBoxMarkup extends DefaultMarkupType {

	public SearchBoxMarkup(DefaultMarkup markup) {
		super(markup);
		setIgnorePackageCompile(true);
		this.setRenderer(new SearchBoxRenderer());
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("searchbox");

	}

	public SearchBoxMarkup() {
		super(m);
		setIgnorePackageCompile(true);
		this.setRenderer(new SearchBoxRenderer());
	}

	class SearchBoxRenderer implements Renderer {

		@Override
		public void render(Section<?> section, UserContext user, RenderResult string) {
			String contextPath = Environment.getInstance().getWikiConnector().getServletContext().getContextPath();

			string.appendHtml("<div class='searchbox'><form id='searchForm' class='wikiform' accept-charset='UTF-8' action='"
					+ contextPath
					+ "/Search.jsp'><div style='position:relative'><input id='query' type='text' style='color:black;font-weight:bold;' accesskey='f' size='20' name='query' value='Suche' onfocus='if( this.value == this.defaultValue ) { this.value = ''}; return true; ' onblur='if( this.value == '' ) { this.value = this.defaultValue }; return true; ' autocomplete='off'><button id='searchSubmit' title='Go!' value='Go!' name='searchSubmit' type='submit'></button></div><div id='searchboxMenu' style='visibility: hidden; opacity: 0; left: 600px; top: 39px;' visibility='visible'></form></div>");
		}
	}

}

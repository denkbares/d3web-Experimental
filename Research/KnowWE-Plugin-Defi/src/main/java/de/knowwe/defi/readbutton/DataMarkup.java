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
package de.knowwe.defi.readbutton;

import javax.servlet.ServletContext;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiSession;
import com.ecyrd.jspwiki.auth.WikiPrincipal;
import com.ecyrd.jspwiki.event.WikiEvent;
import com.ecyrd.jspwiki.event.WikiEventListener;
import com.ecyrd.jspwiki.event.WikiEventManager;
import com.ecyrd.jspwiki.event.WikiSecurityEvent;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;

/**
 * 
 * @author dupke
 * @created 23.03.2011
 */
public class DataMarkup extends DefaultMarkupType implements WikiEventListener {

	private static DefaultMarkup MARKUP = null;

	public static String HTMLID_READPAGES = "defi-readpages";

	static {
		MARKUP = new DefaultMarkup("data");
		MARKUP.addAnnotation("readpages", true);
	}

	/**
	 * @param markup
	 */
	public DataMarkup() {
		super(MARKUP);
		this.setCustomRenderer(this.getDefaultRenderer());
		this.setIgnorePackageCompile(true);

		ServletContext context = KnowWEEnvironment.getInstance().getContext();
		WikiEngine en = WikiEngine.getInstance(context, null);
		WikiEventManager.addWikiEventListener(en.getAuthenticationManager(), this);
	}

	@Override
	protected KnowWEDomRenderer<?> getDefaultRenderer() {
		return new DataRenderer<AbstractType>();
	}

	@Override
	public void actionPerformed(WikiEvent event) {

		if ((event instanceof WikiSecurityEvent)
				&& (event.getType() == WikiSecurityEvent.PRINCIPAL_ADD)) {

			WikiSecurityEvent e = (WikiSecurityEvent) event;
			WikiSession session = (WikiSession) e.getTarget();
			WikiPrincipal up = (WikiPrincipal) session.getUserPrincipal();
			WikiPrincipal lp = (WikiPrincipal) session.getLoginPrincipal();

			String fullName = "";
			String loginName = "";

			if (up.getType().equals(WikiPrincipal.FULL_NAME)) {
				fullName = up.getName();
			}
			if (lp.getType().equals(WikiPrincipal.LOGIN_NAME)) {
				loginName = lp.getName();
			}

			if (fullName != ""
					&& !KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(
							fullName + "_data")) {

				String pagePermissions = "[{ALLOW view All}]\r\n[{ALLOW delete "
						+ fullName + "}]\r\n\r\n";
				String content = pagePermissions + "%%data\r\n%\r\n";
				KnowWEEnvironment.getInstance().buildAndRegisterArticle(content,
						fullName + "_data", KnowWEEnvironment.DEFAULT_WEB);
				KnowWEEnvironment.getInstance().getWikiConnector()
						.createWikiPage(fullName + "_data", content, loginName);
			}
		}
	}

}

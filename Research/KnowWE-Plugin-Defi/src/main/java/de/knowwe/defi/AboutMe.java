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
package de.knowwe.defi;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiSession;
import com.ecyrd.jspwiki.auth.WikiPrincipal;
import com.ecyrd.jspwiki.event.WikiEvent;
import com.ecyrd.jspwiki.event.WikiEventListener;
import com.ecyrd.jspwiki.event.WikiEventManager;
import com.ecyrd.jspwiki.event.WikiSecurityEvent;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.utils.KnowWEUtils;

/**
 * The AboutMe markup inserts in an article named after a user's name a special
 * "About Me" box. This box contains information about the user and an avatar.
 * If the article name equals the currently authenticated user the user sees the
 * "About Me" box in edit mode, otherwise the view mode of the "About Me" box is
 * rendered.
 *
 * @author smark
 * @created 25.01.2011
 */
public class AboutMe extends DefaultMarkupType implements WikiEventListener {

	private static DefaultMarkup MARKUP = null;

	public static String HTMLID_AVATAR = "defi-avatar";
	public static String HTMLID_ABOUT = "defi-about";

	public static String LOGOUT_FILENAME = "defi.logout.txt";
	public static String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

	static {
		MARKUP = new DefaultMarkup("aboutme");
		MARKUP.addAnnotation("avatar", true);
		MARKUP.addAnnotation("about", true);
	}

	/**
	 * @param markup
	 */
	public AboutMe() {
		super(MARKUP);
		this.setCustomRenderer(this.getDefaultRenderer());
		this.setIgnorePackageCompile(true);

		ServletContext context = KnowWEEnvironment.getInstance().getContext();
		WikiEngine en = WikiEngine.getInstance(context, null);
		WikiEventManager.addWikiEventListener(en.getAuthenticationManager(), this);
	}

	@Override
	protected KnowWEDomRenderer<?> getDefaultRenderer() {
		return new AboutMeRenderer<KnowWEObjectType>();
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
					&& !KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(fullName)) {

				String pagePermissions = "[{ALLOW view All}]\r\n[{ALLOW delete "
						+ fullName + "}]\r\n\r\n";
				String content = pagePermissions + "%%aboutme\r\n%\r\n";
				KnowWEEnvironment.getInstance().getWikiConnector()
						.createWikiPage(fullName, content, loginName);
			}
		}
		else if ((event instanceof WikiSecurityEvent)
				&& (event.getType() == WikiSecurityEvent.LOGOUT)) {
			WikiSecurityEvent e = (WikiSecurityEvent) event;
			WikiPrincipal fp = (WikiPrincipal) e.getPrincipal();

			String filterUsername = ResourceBundle.getBundle("KnowWE_Defi_config").getString(
					"defi.last.login.user");

			if (fp.getName().equals(filterUsername)) {

				String logout_file = System.getProperty("java.io.tmpdir") + File.separatorChar
						+ LOGOUT_FILENAME;

				DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
				Date date = new Date();

				StringBuilder entry = new StringBuilder();
				entry.append(fp);
				entry.append(" - ");
				entry.append(dateFormat.format(date));
				entry.append("\n");

				KnowWEUtils.writeFile(logout_file, entry.toString());
			}
		}
	}
}

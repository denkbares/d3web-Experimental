/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.data;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.wiki.WikiEngine;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.core.wikiConnector.WikiConnector;
import de.knowwe.defi.logger.DefiCommentEventLogger;
import de.knowwe.defi.logger.DefiOtherEventsLogger;
import de.knowwe.defi.logger.DefiPageEventLogger;
import de.knowwe.defi.logger.DefiPageRateEventLogger;
import de.knowwe.defi.logger.DefiSessionEventLogger;
import de.knowwe.jspwiki.JSPWikiConnector;


/**
 * 
 * @author dupke
 * @created 17.08.2013
 */
public class DataTagHandler extends AbstractTagHandler {

	private final String LOG_HEADLINE = "<h5>Download der Logs</h5><hr />";
	private final String USERDATA_HEADLINE = "<h5>Benutzerdaten</h5><hr />";

	public DataTagHandler() {
		super("data");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		if (!userContext.userIsAdmin()) return;

		// logs
		addLogsToAttachment(section.getTitle());
		result.appendHtml(renderLogDownloadLinks(section.getTitle()));

		// render user data
		String user = userContext.getUserName();
		result.appendHtml(renderUserData(user));
	}

	private void addLogsToAttachment(String title) {
		WikiConnector wc = Environment.getInstance().getWikiConnector();
		File pagelog = new File(DefiPageEventLogger.getPath());
		File sessionlog = new File(DefiSessionEventLogger.getPath());
		File commentlog = new File(DefiCommentEventLogger.getPath());
		File extlog = new File(DefiOtherEventsLogger.getPath());
		File ratelog = new File(DefiPageRateEventLogger.getPath());
		
		try {
			boolean addPage = pagelog.exists();
			boolean addSession = sessionlog.exists();
			boolean addComment = commentlog.exists();
			boolean addExtLink = extlog.exists();
			boolean addRate = ratelog.exists();
			for (WikiAttachment wikiAttachment : wc.getAttachments(title)) {
				String attachmentName = wikiAttachment.getFileName();
				long attachmentSize = wikiAttachment.getSize();
				if (attachmentName.equals(pagelog.getName())) addPage = (attachmentSize != pagelog.length());
				else if (attachmentName.equals(sessionlog.getName())) addSession = (attachmentSize != sessionlog.length());
				else if (attachmentName.equals(commentlog.getName())) addComment = (attachmentSize != commentlog.length());
				else if (attachmentName.equals(extlog.getName())) addExtLink = (attachmentSize != extlog.length());
				else if (attachmentName.equals(ratelog.getName())) addRate = (attachmentSize != ratelog.length());
			}
			if (addPage) wc.storeAttachment(title, "Wiki", pagelog);
			if (addSession) wc.storeAttachment(title, "Wiki", sessionlog);
			if (addComment) wc.storeAttachment(title, "Wiki", commentlog);
			if (addExtLink) wc.storeAttachment(title, "Wiki", extlog);
			if (addRate) wc.storeAttachment(title, "Wiki", ratelog);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	private String renderLogDownloadLinks(String title) {
		StringBuilder loglinks = new StringBuilder();
		List<WikiAttachment> attachments = new LinkedList<WikiAttachment>();
		try {
			for (WikiAttachment att : Environment.getInstance().getWikiConnector().getAttachments(
					title)) {
				if (att.getFileName().startsWith("Defi") && att.getFileName().endsWith(".log")) attachments.add(att);
			}
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

		loglinks.append(LOG_HEADLINE);
		loglinks.append("<ul>");
		for (WikiAttachment wa : attachments) {
			loglinks.append("<li class='defi_log_dl'><a href='attach/" + wa.getPath() + "'>"
					+ wa.getFileName() + "</a></li>");
		}
		loglinks.append("</ul>");

		return loglinks.toString();
	}

	public String renderUserData(String user) {
		StringBuilder userData = new StringBuilder();

		userData.append(USERDATA_HEADLINE);
		userData.append(renderUserSelection());
		userData.append("<ul id='userDataLog'></ul>");

		return userData.toString();
	}

	private String renderUserSelection() {
		JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
				Environment.getInstance().getContext(), null));
		String[] users = wc.getAllUsers();
		StringBuilder us = new StringBuilder();

		us.append("<script type='text/javascript'>window.onload = function(){document.getElementById('userDataSelect').selectedIndex = -1;}</script>");
		us.append("<p>Benutzer auswählen: <select id='userDataSelect' name='users' onChange='displayPersonalData(this.options[this.selectedIndex].value)'>");
		us.append("<option value='' style='display:none'></option>");
		for (String user : users) {
			us.append("<option value='" + user + "'>" + user + "</option>");
		}
		us.append("</select></p>");
	
		return us.toString();
	}
}

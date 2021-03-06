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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.aboutMe.AboutMe;

/**
 * Stores the clicks in TabbedSection in a log file. Used to log which user
 * clicked on which tab.
 *
 * @author smark
 * @created 15.02.2011
 */
public class LogTabClicksAction extends AbstractAction {

	/**
	 * The name of the log file the informations is stored in.
	 */
	private final String LOGFILE_NAME = "defi.log.txt";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String username = context.getUserName();
		String clicked_tab = context.getParameter("tologtab");
		String page = context.getParameter("page");

		boolean isAuthenticated = context.userIsAsserted();
		if (!isAuthenticated) {
			username = context.getRequest().getRemoteAddr();
		}

		String log_file = System.getProperty("java.io.tmpdir") + File.separatorChar
				+ this.LOGFILE_NAME;

		DateFormat dateFormat = new SimpleDateFormat(AboutMe.DATE_FORMAT);
		Date date = new Date();

		StringBuilder entry = new StringBuilder();
		entry.append(page);
		entry.append("- ");
		entry.append(username);
		entry.append("- [");
		entry.append(dateFormat.format(date));
		entry.append("] - ");
		entry.append(clicked_tab);
		entry.append("\n");

		KnowWEUtils.appendToFile(log_file, entry.toString());
	}
}

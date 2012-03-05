/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.defi.logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.append.PageAppendHandler;
import de.knowwe.core.user.UserContext;

public class PageLoggerHandler implements PageAppendHandler {

	public boolean log = true;
	private final static String path = KnowWEEnvironment.getInstance().getWikiConnector().getSavePath()
			+ "/Pagelogger.log";

	@Override
	public String getDataToAppend(String topic, String web,
			UserContext user) {
		if (log) {
			try {
				BufferedWriter buffy = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(path, true), "UTF-8"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String uhrzeit = sdf.format(new Date(System.currentTimeMillis()));
				buffy.append(uhrzeit + ";" + user.getUserName() + ";" + user.getTopic() + "\n");
				buffy.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@Override
	public boolean isPre() {
		return false;
	}

	public static String getPath() {

		return path;
	}
}

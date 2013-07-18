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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import de.knowwe.core.Environment;
import de.knowwe.core.append.PageAppendHandler;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;

public class PageLoggerHandler implements PageAppendHandler {

	public boolean log = true;
	private final static String PATH = Environment.getInstance().getWikiConnector().getSavePath()
			+ "/Pagelogger.log";
	private final static String SEPARATOR = "###";

	@Override
	public void append(String web, String title,
			UserContext user, RenderResult result) {
		if (log) {
			try {
				BufferedWriter buffy = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(PATH, true), "UTF-8"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String uhrzeit = sdf.format(new Date(System.currentTimeMillis()));
				buffy.append(uhrzeit + "###" + user.getUserName() + "###" + user.getTitle() + "\n");
				buffy.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Suche im Log nach Zeilen des Users.
	 */
	public static HashMap<String, String> checkLogFor(String user) {
		HashMap<String, String> logPages = new HashMap<String, String>();
		String log = PageLoggerHandler.getPath();
		String line;

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(log),
					"UTF-8"));
			try {
				while ((line = br.readLine()) != null) {
					String date = line.split(PageLoggerHandler.getSeparator())[0];
					String name = line.split(PageLoggerHandler.getSeparator())[1];
					String title = line.split(PageLoggerHandler.getSeparator())[2];
					if (user.equals(name)) {
						logPages.put(title, date);
					}
				}
			}
			finally {
				br.close();
			}
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}

		return logPages;
	}

	@Override
	public boolean isPre() {
		return false;
	}

	public static String getPath() {

		return PATH;
	}

	public static String getSeparator() {

		return SEPARATOR;
	}
}

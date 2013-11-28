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
package de.knowwe.defi.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.Environment;

/**
 * 
 * @author dupke
 * @created 31.07.2013
 */
public class DefiSessionEventLogger {

	private final static String FILENAME = "DefiSessionLog.log";
	private final static String PATH = Environment.getInstance().getWikiConnector().getSavePath()
			+ "/" + FILENAME;
	/** Sepearator between entries in a logline **/
	private final static String SEPARATOR = DefiLoggerUtils.SEPARATOR;


	/**
	 * Write a new login entry into defis session log.
	 */
	public static void logLogin(DefiSessionLogLine line) {
		writeToSessionLog(Arrays.asList(line.toString()), true);
	}

	/**
	 * Write a new logout entry into defis session log.
	 */
	public static void logLogout(DefiSessionLogLine last) {
		updateLastLogLineForUser(last);
	}

	/**
	 * Find and return the last authentication action for user.
	 */
	public static DefiSessionLogLine findLastEntryForUser(String user) {
		DefiSessionLogLine lastEntry = null;

		for (DefiSessionLogLine logLine : getLogLines()) {
			if (logLine.getUser().equals(user)) lastEntry = logLine;
		}
	
		return (lastEntry == null) ? new DefiSessionLogLine() : lastEntry;
	}

	private static void writeToSessionLog(List<String> logLines, boolean append) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH, append),
					"ISO-8859-1"));
			for (String line : logLines) {
				out.write(line);
				out.newLine();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				out.close();
			}
			catch (IOException e) {
			}
		}
	}

	private static void updateLastLogLineForUser(DefiSessionLogLine last) {
		List<String> userlog = new LinkedList<String>();
		boolean added = false;

		for (DefiSessionLogLine logLine : getLogLines()) {
			if (!added && logLine.equalLogin(last)) {
				userlog.add(last.toString());
				added = true;
			}
			else {
				userlog.add(logLine.toString());
			}
		}

		writeToSessionLog(userlog, false);
	}

	public static String getPath() {
		return PATH;
	}

	public static String getSeparator() {
		return SEPARATOR;
	}

	public static List<DefiSessionLogLine> getLogLines() {
		LinkedList<DefiSessionLogLine> loglines = new LinkedList<DefiSessionLogLine>();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(
					(new FileInputStream(new File(PATH))), "ISO-8859-1"));
			while ((line = br.readLine()) != null) {
				loglines.add(new DefiSessionLogLine(line));
			}
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}
		finally {
			try {
				if (br != null) br.close();
			}
			catch (IOException e) {
			}
		}

		return loglines;
	}
}

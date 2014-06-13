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
public class DefiPageEventLogger {
	private final static String FILENAME = "DefiPageLog.log";
	private final static String PATH = Environment.getInstance().getWikiConnector().getSavePath()
			+ "/" + FILENAME;
	private final static String ENCODING = DefiLoggerUtils.getEncoding();
	/** Sepearator between entries in a logline **/
	private final static String SEPARATOR = DefiLoggerUtils.SEPARATOR;
	private static final String SEPARATOR_UNICODE = DefiLoggerUtils.SEPARATOR_UNICODE;

	/**
	 * Set end to last page entry and start to new page entry.
	 */
	public static void logEntry(DefiPageLogLine line) {
		// if last page visited equals current page -> return
		DefiPageLogLine lastEntry = findLastEntryForUser(line.getUser());
		if (lastEntry != null && lastEntry.getPage().equals(line.getPage())) return;
		// find last entry for user and fill end
		updateLastEntry(line);
		// write new entry
		writeToPageLog(Arrays.asList(line.toString()), true);
	}

	private static void writeToPageLog(List<String> logLines, boolean append) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH, append),
					ENCODING));

			if ((new File(PATH)).length() == 0) {
				out.write(DefiPageLogLine.getHeader());
				out.newLine();
			}

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

	private static void updateLastEntry(DefiPageLogLine newEntry) {
		DefiPageLogLine lastEntry = findLastEntryForUser(newEntry.getUser());
		if (lastEntry == null) return;
		List<String> userlog = new LinkedList<String>();
		boolean added = false;

		lastEntry.setEndDate(newEntry.getStartDate());
		lastEntry.setEndTime(newEntry.getstartTime());
		for (DefiPageLogLine logLines : getLogLines()) {
			if (!added && logLines.getUser().equals(lastEntry.getUser())
					&& logLines.getEndDate().equals(DefiPageLogLine.getNoDataString())) {
				userlog.add(lastEntry.toString());
				added = true;
			}
			else {
				userlog.add(logLines.toString());
			}
		}
		writeToPageLog(userlog, false);
	}

	public static void updateLastEntryOnLogout(String user, String date, String time) {
		DefiPageLogLine lastEntry = findLastEntryForUser(user);
		if (lastEntry == null) return;
		List<String> userlog = new LinkedList<String>();
		boolean added = false;

		lastEntry.setEndDate(date);
		lastEntry.setEndTime(time);
		for (DefiPageLogLine logLines : getLogLines()) {
			if (!added && logLines.getUser().equals(lastEntry.getUser())
					&& logLines.getEndDate().equals(DefiPageLogLine.getNoDataString())) {
				userlog.add(lastEntry.toString());
				added = true;
			}
			else {
				userlog.add(logLines.toString());
			}
		}
		writeToPageLog(userlog, false);
	}

	/**
	 * Find and return the last page entry for user.
	 */
	public static DefiPageLogLine findLastEntryForUser(String user) {
		if (!(new File(PATH)).exists()) return null;

		DefiPageLogLine lastEntry = null;
		for (DefiPageLogLine logLine : getLogLines()) {
			if (logLine.getUser().equals(user)) lastEntry = logLine;
		}

		return lastEntry;
	}

	public static String getPath() {
		return PATH;
	}

	public static String getSeparator() {
		return SEPARATOR;
	}

	public static String getSeparatorUnicode() {
		return SEPARATOR_UNICODE;
	}

	public static List<DefiPageLogLine> getLogLines() {
		LinkedList<DefiPageLogLine> loglines = new LinkedList<DefiPageLogLine>();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(
					(new FileInputStream(new File(PATH))), ENCODING));

			// skip header
			br.readLine();

			// read log
			while ((line = br.readLine()) != null) {
				loglines.add(new DefiPageLogLine(line));
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

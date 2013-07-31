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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
			+ "\\" + FILENAME;
	/** Sepearator between entries in a logline **/
	private final static String SEPARATOR = "___";

	/**
	 * Set end to last page entry and start to new page entry.
	 */
	public static void logEntry(DefiPageLogLine line) {

		// find last entry for user and fill end
		DefiPageLogLine lastEntry = findLastEntryForUser(line.getUser());
		if (lastEntry != null) {
			lastEntry.setEndDate(line.getStartDate());
			lastEntry.setEndTime(line.getstartTime());
			updateLastEntryForUser(lastEntry);
		}

		// write new entry
		writeToPageLog(Arrays.asList(line.toString()), true);
	}

	private static void writeToPageLog(List<String> logLines, boolean append) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(PATH, append));
			for (String line : logLines) {
				writer.append(line);
				writer.newLine();
			}

			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateLastEntryForUser(DefiPageLogLine lastEntry) {
		List<String> userlog = new LinkedList<String>();
		DefiPageLogLine tmp;
		boolean added = false;
		String line;
		BufferedReader reader = null;
	
		try {
			reader = new BufferedReader(new FileReader(PATH));
			while ((line = reader.readLine()) != null) {
				tmp = new DefiPageLogLine(line);
				if (!added && tmp.getUser().equals(lastEntry.getUser())
						&& tmp.getEndDate().equals(DefiPageLogLine.getNoDataString())) {
					userlog.add(lastEntry.toString());
					added = true;
				}
				else {
					userlog.add(line);
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			}
			catch (IOException e) {
			}
		}
	
		writeToPageLog(userlog, false);
	}

	/**
	 * Find and return the last page entry for user.
	 */
	public static DefiPageLogLine findLastEntryForUser(String user) {
		if (!(new File(PATH)).exists()) return null;

		DefiPageLogLine lastEntry = null, tmp;
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(PATH));
			String line;
			while ((line = reader.readLine()) != null) {
				tmp = new DefiPageLogLine(line);
				if (tmp.getUser().equals(user)) lastEntry = tmp;
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			}
			catch (IOException e) {
			}
		}

		return lastEntry;
	}

	public static String getPath() {
		return PATH;
	}

	public static String getSeparator() {
		return SEPARATOR;
	}

	public static List<DefiPageLogLine> getLogLines() {
		LinkedList<DefiPageLogLine> loglines = new LinkedList<DefiPageLogLine>();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(
					(new FileInputStream(new File(PATH))), "UTF-8"));
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
				br.close();
			}
			catch (IOException e) {
			}
		}
	
		return loglines;
	}
}

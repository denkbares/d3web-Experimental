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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.Environment;


/**
 * 
 * @author dupke
 * @created 21.08.2013
 */
public class DefiOtherEventsLogger {
	
	private final static String FILENAME = "DefiOtherEventsLog.log";
	private final static String PATH = Environment.getInstance().getWikiConnector().getSavePath()
			+ "\\" + FILENAME;
	/** Sepearator between entries in a logline **/
	public final static String SEPARATOR = "___";
	public final static String EXT_LINK_PREFIX = "Externer Link";
	public final static String FEEDBACK_PREFIX = "Feedback";

	public static void logExternalLinkEvent(String user, String link) {
		String datetime = DefiLoggerUtils.getCurrentDate() + " " + DefiLoggerUtils.getCurrentTime();
		logEvent(EXT_LINK_PREFIX + SEPARATOR + user + SEPARATOR + link + SEPARATOR + datetime);
	}

	public static void logFeedbackEvent(String user) {
		String datetime = DefiLoggerUtils.getCurrentDate() + " " + DefiLoggerUtils.getCurrentTime();
		logEvent(FEEDBACK_PREFIX + SEPARATOR + user + SEPARATOR + datetime);
	}

	private static void logEvent(String logline) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(PATH, true));
			writer.append(logline);
			writer.newLine();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch (IOException e) {
			}
		}
	}

	public static List<String> getLogLines(String prefix) {
		LinkedList<String> loglines = new LinkedList<String>();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(
					(new FileInputStream(new File(PATH))), "UTF-8"));
			while ((line = br.readLine()) != null) {
				if (line.startsWith(prefix)) loglines.add(line);
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

	public static String getPath() {
		return PATH;
	}

}

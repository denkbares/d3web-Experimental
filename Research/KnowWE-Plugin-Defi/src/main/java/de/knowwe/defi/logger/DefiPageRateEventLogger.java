/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.defi.event.DefiPageRateClosedEvent;
import de.knowwe.defi.event.DefiPageRatedEvent;

/**
 * 
 * @author dupke
 * @created 19.11.2013
 */
public class DefiPageRateEventLogger {

	private final static String FILENAME = "DefiRatedPages.log";
	private final static String PATH = Environment.getInstance().getWikiConnector().getSavePath()
			+ "/" + FILENAME;
	/** Sepearator between entries in a logline **/
	private final static String SEPARATOR = DefiLoggerUtils.SEPARATOR;

	public static void logEvent(DefiPageRatedEvent event) {
		DefiPageRateLogLine logline = new DefiPageRateLogLine(event);
		writePageRate(Arrays.asList(logline), true);
	}

	public static void closeLogline(DefiPageRateClosedEvent event) {
		List<DefiPageRateLogLine> logLines = getLogLines();
		ArrayList<DefiPageRateLogLine> newLogLines = new ArrayList<DefiPageRateLogLine>();
		String discussed = event.getDiscussed();

		for (DefiPageRateLogLine line : logLines) {

			if (event.getId().equals(line.getId())
					&& event.getUser().equals(line.getUser())) {

				line.setDiscussed(discussed.equals("Nein") ? line.getDiscussed() : "Ja");
				line.setClosed(discussed.equals("Nein") ? "Ja" : "Nein");
				newLogLines.add(line);
			}
			else {
				newLogLines.add(line);
			}
		}

		writePageRate(newLogLines, false);
	}

	public static List<DefiPageRateLogLine> getLogLines() {
		ArrayList<DefiPageRateLogLine> loglines = new ArrayList<DefiPageRateLogLine>();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(
					(new FileInputStream(new File(PATH))), "ISO-8859-1"));
			while ((line = br.readLine()) != null) {
				loglines.add(new DefiPageRateLogLine(line));
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

	private static void writePageRate(List<DefiPageRateLogLine> list, boolean append) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH, append),
					"ISO-8859-1"));
			for (DefiPageRateLogLine line : list) {
				out.write(line.toString());
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

	public static boolean checkRateLogFile() {
		File rateLog = new File(PATH);
		if (!rateLog.exists()) {
			try {
				rateLog.createNewFile();
			}
			catch (IOException e) {
				return false;
			}
		}
		return rateLog.exists();
	}

	public static String getSeparator() {
		return SEPARATOR;
	}
}

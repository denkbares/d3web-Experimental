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
import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.Environment;

/**
 * 
 * @author dupke
 * @created 31.07.2013
 */
public class DefiCommentEventLogger {

	private final static String FILENAME = "DefiForumLog.log";
	private final static String PATH = Environment.getInstance().getWikiConnector().getSavePath()
			+ "/" + FILENAME;
	/** Sepearator between entries in a logline **/
	private final static String SEPARATOR = DefiLoggerUtils.SEPARATOR;

	public static void logComment(DefiCommentLogLine commentLogLine) {
		writeToForumLog(commentLogLine.toString());
	}

	private static void writeToForumLog(String logLine) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH, true),
					"ISO-8859-1"));
				out.write(logLine);
				out.newLine();
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

	public static List<DefiCommentLogLine> getLogLines() {
		LinkedList<DefiCommentLogLine> loglines = new LinkedList<DefiCommentLogLine>();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(
					(new FileInputStream(new File(PATH))), "ISO-8859-1"));
			while ((line = br.readLine()) != null) {
				loglines.add(new DefiCommentLogLine(line));
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

	public static String getSeparator() {
		return SEPARATOR;
	}

	public static String getPath() {
		return PATH;
	}
}

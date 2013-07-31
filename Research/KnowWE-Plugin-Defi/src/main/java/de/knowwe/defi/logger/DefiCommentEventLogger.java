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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import de.knowwe.core.Environment;

/**
 * 
 * @author dupke
 * @created 31.07.2013
 */
public class DefiCommentEventLogger {

	private final static String FILENAME = "DefiForumLog.log";
	private final static String PATH = Environment.getInstance().getWikiConnector().getSavePath()
			+ "\\" + FILENAME;
	/** Sepearator between entries in a logline **/
	private final static String SEPARATOR = "___";

	public static void logComment(DefiCommentLogLine commentLogLine) {
		writeToForumLog(commentLogLine.toString());
	}

	private static void writeToForumLog(String logLine) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(PATH, true));
			writer.append(logLine);
			writer.newLine();
		}
		catch (IOException e) {
		}
		finally {
			try {
				writer.close();
			}
			catch (IOException e) {
			}
		}
	}

	public static String getSeparator() {
		return SEPARATOR;
	}

	public String getPath() {
		return PATH;
	}
}

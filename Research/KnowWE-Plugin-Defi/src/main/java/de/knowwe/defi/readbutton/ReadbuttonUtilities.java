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
package de.knowwe.defi.readbutton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.defi.logger.DefiLoggerUtils;

/**
 * @author dupke
 */
public class ReadbuttonUtilities {

	private final static String FILENAME = "DefiRatedPages.log";
	private final static String PATH = Environment.getInstance().getWikiConnector().getSavePath()
			+ "/" + FILENAME;
	/** Sepearator between entries in a logline **/
	private final static String SEPARATOR = DefiLoggerUtils.SEPARATOR;

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

	public static void logPageRate(String user, String title, Map<String, String> rateInfos) {
		String date = (new SimpleDateFormat("dd.MM.yyyy HH:mm")).format((new Date()));

		StringBuffer rate = new StringBuffer();
		rate.append(rateInfos.get("id") + SEPARATOR);
		rate.append(title + SEPARATOR);
		rate.append(user + SEPARATOR);
		rate.append(date + SEPARATOR);
		rate.append(rateInfos.get("realvalue") + SEPARATOR);
		rate.append(rateInfos.get("value") + SEPARATOR);
		rate.append(rateInfos.get("label") + SEPARATOR);
		rate.append(rateInfos.get("discussed") + SEPARATOR);
		rate.append(rateInfos.get("closed"));

		writePageRate(Arrays.asList(rate.toString()), true);
	}

	private static void writePageRate(List<String> list, boolean append) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(PATH, append));
			for (String line : list) {
				writer.append(line);
				writer.newLine();
			}
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getReadbutton(String id, String userName) {
		ArrayList<String> readbutton = new ArrayList<String>();
		String logline = "";

		for (String line : getLogLines()) {
			String rId = line.split(SEPARATOR)[0];
			String rUser = line.split(SEPARATOR)[2];

			if (id.equals(rId) && userName.equals(rUser)) {
				logline = line;
				break;
			}
		}

		for (String info : logline.split(SEPARATOR)) {
			readbutton.add(info);
		}

		return readbutton;
	}

	public static boolean checkID(String id, String title) {

		for (String line : getLogLines()) {
			String rId = line.split(SEPARATOR)[0];
			String rTitle = line.split(SEPARATOR)[1];

			if (id.equals(rId) && !title.equals(rTitle)) {
				return false;
			}
		}

		return true;
	}

	public static List<String> getLogLines() {
		ArrayList<String> loglines = new ArrayList<String>();
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(
					(new FileInputStream(new File(PATH))), "UTF-8"));
			while ((line = br.readLine()) != null) {
				loglines.add(line);
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

	public static void refreshLine(String id, String username, String discussed) {
		// disc = 7 close = 8
		List<String> logLines = getLogLines();
		ArrayList<String> newLogLines = new ArrayList<String>();

		for (String line : logLines) {
			String rId = line.split(SEPARATOR)[0];
			String rUser = line.split(SEPARATOR)[2];
			String rDisc = line.split(SEPARATOR)[7];

			if (id.equals(rId) && username.equals(rUser)) {
				StringBuilder newLogline = new StringBuilder();
				for (int i = 0; i < 7; i++) {
					newLogline.append(line.split(SEPARATOR)[i] + SEPARATOR);
				}
				newLogline.append(discussed.equals("Nein") ? rDisc : "Ja");
				newLogline.append(SEPARATOR);
				newLogline.append(discussed.equals("Nein") ? "Ja" : "Nein");
				newLogLines.add(newLogline.toString());
			}
			else {
				newLogLines.add(line);
			}
		}

		writePageRate(newLogLines, false);
	}

	public static boolean isPageRated(String id, String userName) {

		for (String line : getLogLines()) {
			String rId = line.split(SEPARATOR)[0];
			String rUser = line.split(SEPARATOR)[2];

			if (id.equals(rId) && userName.equals(rUser)) {
				return true;
			}
		}

		return false;
	}

	public static List<String> getRatedPages(String user) {
		List<String> ratedPages = new ArrayList<String>();
		for (String line : getLogLines()) {
			String title = line.split(SEPARATOR)[1];
			String rUser = line.split(SEPARATOR)[2];

			if (user.equals(rUser)) {
				ratedPages.add(title);
			}
		}

		return ratedPages;
	}
}

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

import java.util.ArrayList;
import java.util.List;

import de.knowwe.defi.logger.DefiPageRateEventLogger;
import de.knowwe.defi.logger.DefiPageRateLogLine;

/**
 * @author dupke
 */
public class ReadbuttonUtilities {

	/**
	 * Search for the readbutton with the given id and user.
	 */
	public static DefiPageRateLogLine getReadbutton(String id, String user) {
		DefiPageRateLogLine readbutton = null;

		for (DefiPageRateLogLine line : DefiPageRateEventLogger.getLogLines()) {
			if (id.equals(line.getId()) && user.equals(line.getUser())) {
				readbutton = line;
				break;
			}
		}

		return readbutton;
	}

	/**
	 * Check if there is a readbutton with the given id but other page.
	 */
	public static boolean checkID(String id, String title) {
		for (DefiPageRateLogLine line : DefiPageRateEventLogger.getLogLines()) {

			if (id.equals(line.getId()) && !title.equalsIgnoreCase(line.getPage())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Check if a readbutton is already rated.
	 */
	public static boolean isPageRated(String id, String user) {
		return getReadbutton(id, user) != null;
	}

	/**
	 * Returns a list of all rated pages.
	 */
	public static List<String> getRatedPages(String user) {
		List<String> ratedPages = new ArrayList<>();
		for (DefiPageRateLogLine line : DefiPageRateEventLogger.getLogLines()) {

			if (user.equals(line.getUser())) {
				ratedPages.add(line.getPage());
			}
		}

		return ratedPages;
	}
}

/**
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

package de.d3web.proket.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Personalized logging class.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class ProKEtLogger {
	private static Logger logger = null;
	private final static String LOGGER_NAME = "Logger";

	synchronized public static Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(LOGGER_NAME);

			final Level level = Level.ALL;

			try {
				SimpleFormatter simpleFormatter = new SimpleFormatter();

				ConsoleHandler ch = new ConsoleHandler();
				logger.addHandler(ch);
				logger.setLevel(level);
				ch.setFormatter(simpleFormatter);

			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		return Logger.getLogger(LOGGER_NAME);
	}
}

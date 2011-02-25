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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Util functions for working with files.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class FileUtils {

	/**
	 * Retrieve a file by file name and package name
	 * 
	 * @param fileName
	 * @param packageName
	 * @return The requested file if it exists, null otherwise.
	 */
	public static File getFile(String fileName, String packageName) {
		if (!packageName.endsWith("/")) {
			packageName += "/";
		}
		if (!packageName.startsWith("/")) {
			packageName = "/" + packageName;
		}
		packageName = packageName.replaceAll("\\.", "/");

		URL resourceUrl = FileUtils.class.getResource
				("/" + packageName + fileName);
		if (resourceUrl == null) {
			return null;
		}
		try {
			return new File(resourceUrl.toURI());
		} catch (URISyntaxException e) {
		}
		return null;
	}

	/**
	 * Get a resource file by path and name.
	 * 
	 * @param fileNameAndPath path and name of the file relative to the
	 *        src/main/resources directory.
	 * @return The specified file if it exists, exception otherwise.
	 * @throws FileNotFoundException If the specified file could not be found.
	 */
	public static File getResourceFile(String fileNameAndPath)
			throws FileNotFoundException {
		URL resourceUrl = null;
		resourceUrl = FileUtils.class.getResource(fileNameAndPath);

		try {
			return new File(resourceUrl.toURI());
		} catch (URISyntaxException e) {
			throw new FileNotFoundException(MessageFormat.format(
					"File {0} not found", fileNameAndPath));
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Return file contents as String.
	 * 
	 * @param file Source file to be read.
	 * @return The String containing the contents of the file given, null
	 *         otherwise.
	 */
	public static String getString(File file) {
		Logger log = ProKEtLogger.getLogger();
		StringBuilder result = new StringBuilder();
		if (file == null) {
			return null;
		}

		String line = null;
		BufferedReader f = null;
		try {
			f = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
			while ((line = f.readLine()) != null) {
				result.append(line).append("\n");
			}
		} catch (FileNotFoundException e) {
			log.severe(MessageFormat.format("Could not find file at \"{0}\"",
					file));
			e.printStackTrace();
		} catch (IOException e) {
			log.severe(MessageFormat.format(
					"Problems occurred while reading from file \"{0}\"", file));
			e.printStackTrace();
		}

		if (f != null) {
			try {
				f.close();
			} catch (IOException e) {
			}
		}

		return result.toString();
	}
}

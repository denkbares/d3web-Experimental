/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.visualization.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author jochenreutelshofer
 * @created 23.05.2013
 */
public class FileUtils {

	public static final String TMP_FOLDER = "tmp";
	public static final String KNOWWEEXTENSION_FOLDER = "KnowWEExtension";
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String TOMCAT_PATH_SEPARATOR = "/";

	/**
	 * 
	 * @created 20.08.2012
	 * @param dot
	 */
	public static void writeFile(File dot, String dotSource) {
		try {
			checkWriteable(dot);
			FileWriter writer;
			writer = new FileWriter(dot);
			writer.append(dotSource);
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the specified file can be read. If not an {@link IOException}
	 * is thrown.
	 * 
	 * @created 20.04.2011
	 * @param file the file to read from
	 * @throws IOException if the file cannot be read
	 */
	public static void checkReadable(File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(
					"file does not exist: " + file.getCanonicalPath());
		}
		if (!file.canRead()) {
			throw new IOException(
					"file cannot be read:" + file.getCanonicalPath());
		}
	}

	/**
	 * Check if the specified file and the required folder structure can be
	 * written. This prevents failures later on when the pdf will be created. If
	 * the file cannot be written an {@link IOException} is thrown.
	 * 
	 * @created 20.04.2011
	 * @param file the file to be written
	 * @throws IOException if the file cannot be written
	 */
	public static void checkWriteable(File file) throws IOException {
		// create/check target output folder
		File parent = file.getAbsoluteFile().getParentFile();
		parent.mkdirs();
		if (!parent.exists()) {
			throw new IOException(
					"failed to create non-existing parent folder: " + parent.getCanonicalPath());
		}
		// if there is already a file that cannot be overwritten,
		// throw an exception
		if (file.exists() && !file.canWrite()) {
			throw new IOException(
					"output file cannot be written: " + file.getCanonicalPath());
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			}
			catch (IOException io) {
				throw new IOException(
						"output file could not be created: " + file.getCanonicalPath());
			}
		}
	}

	public static void printStream(InputStream str) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(str));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
	}
}

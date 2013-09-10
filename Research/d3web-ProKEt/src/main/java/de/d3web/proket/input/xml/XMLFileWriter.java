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

package de.d3web.proket.input.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

/**
 * Functions for writing XML files.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class XMLFileWriter {
	private static final Logger logger = Logger.getLogger("MainLogger");

	private FileWriter fw;
	private int level;
	private File outputFile;

	/**
	 * Create the XMLFileWriter with a desired output file with setting
	 * indentation level to 0
	 * 
	 * @param outputFile
	 */
	public XMLFileWriter(File outputFile) {
		level = 0;
		this.outputFile = outputFile;

	}

	/**
	 * Try to close the written file correctly.
	 * 
	 * @created 10.10.2010
	 * @return
	 */
	public boolean close() {
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			logger.severe(String.format("File '%s' could not be closed.",
					outputFile.getName()));
			return false;
		}
		return true;
	}

	/**
	 * Close the XML element with name qName
	 * 
	 * @created 10.10.2010
	 * @param qName
	 */
	public void closeXMLElement(String qName) {
		level--; // leave one level of indentation
		indent(); // indent the closing tag
		write("</" + qName + ">\n"); // and write it
	}

	/**
	 * Indent elements by adding tabs up to a certain level
	 * 
	 * @created 10.10.2010
	 */
	private void indent() {
		// indent with tabs
		for (int i = 0; i < level; i++) {
			write("\t");
		}
	}

	/**
	 * Open XML File for writing
	 * 
	 * @created 10.10.2010
	 * @return
	 */
	public boolean open() {
		try {
			fw = new FileWriter(outputFile);
		} catch (IOException e) {
			logger.severe(String.format("File '%s' could not be closed.",
					outputFile.getName()));
			return false;
		}

		// ensure correct encoding
		String encoding = fw.getEncoding();
		if (encoding.equals("UTF8")) {
			encoding = "UTF-8";
		}

		// try to write the XML header into the file
		if (!write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n"))
			return false;
		return true;
	}

	/**
	 * Write an XML element with the name qName, and Attributes atts
	 * 
	 * @created 10.10.2010
	 * @param qName
	 * @param atts
	 */
	public void openXMLElement(String qName, Attributes atts) {

		indent(); // indent new element

		write("<" + qName); // write new element
		for (int i = 0; i < atts.getLength(); i++) {
			write(" " + atts.getQName(i) + "=\"" + escapeHTML(atts.getValue(i))
					+ "\"");
		}
		write(">\n");
		level++; // go one level deeper for next indentation
	}

	/**
	 * Writes the String str into an outputFile that was given when creating
	 * this FileWriter
	 * 
	 * @created 10.10.2010
	 * @param str
	 * @return
	 */
	private boolean write(String str) {
		try {
			fw.write(str);
		} catch (IOException e) {
			logger.severe(String.format("File '%s' could not be closed.",
					outputFile.getName()));
			return false;
		}
		return true;
	}

	/**
	 * Util-method for escaping HTML code.
	 * 
	 * @param s
	 * @return
	 */
	private static final String escapeHTML(String s) {
		StringBuffer sb = new StringBuffer();
		int n = s.length();
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '"':
				sb.append("&quot;");
				break;

			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

}

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
package de.knowwe.rdfs.vis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.rdfs.vis.RenderingCore.NODE_TYPE;
import de.knowwe.rdfs.vis.util.FileUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 29.04.2013
 */
public class DotRenderer {

	/**
	 * 
	 * @created 04.09.2012
	 * @param shape
	 */
	public static String buildLabel(RenderingStyle style) {
		return " shape=\"" + style.shape + "\" ";
	}

	/**
	 * 
	 * @created 04.09.2012
	 * @param arrowtail
	 */
	public static String buildRelation(String arrowtail, String color) {
		return " arrowtail=\"" + arrowtail + "\" " + " color=\"" + color + "\" ";
	}

	/**
	 * Given the label of the inner relation, the method returns the String of
	 * the appearance of the relation.
	 * 
	 * @created 06.09.2012
	 * @param label
	 */
	public static String innerRelation(String label, String relationColorCodes) {
		// Basic Relation Attributes
		String arrowtail = "normal";

		String color = getRelationColorCode(label, relationColorCodes);

		return "[ label = \"" + label
				+ "\"" + buildRelation(arrowtail, color) + " ];\n";
	}

	/**
	 * 
	 * @created 07.12.2012
	 * @param label
	 * @return
	 */
	private static String getRelationColorCode(String label, String relationColorCodes) {
		if (relationColorCodes != null) {
			String codeList = relationColorCodes;
			String[] assignments = codeList.split(";");
			for (String assignment : assignments) {
				String[] ass = assignment.split(":");
				String relationName = ass[0];
				String colorCode = ass[1];
				if (relationName.equals(label)) {
					return colorCode;
				}
			}
		}
		return "black";
	}

	public static RenderingStyle getStyle(NODE_TYPE type) {
		RenderingStyle style = new RenderingStyle();
		style.fontcolor = "black";

		if (type == NODE_TYPE.CLAAS) {
			style.shape = "box";
		}
		else if (type == NODE_TYPE.PROPERTY) {
			style.shape = "septagon";
		}
		else if (type == NODE_TYPE.INSTANCE) {
			style.shape = "egg";
		}
		else {
			style.shape = "box";
		}
		return style;
	}

	/**
	 * The sources from the maps are being written into the String-dotSource.
	 * 
	 * @created 18.08.2012
	 */
	public static String connectSources(String dotSource, Map<ConceptNode, String> dotSourceLabel, Map<Edge, String> dotSourceRelations) {
		// iterate over the labels and add them to the dotSource
		Iterator<ConceptNode> labelKeys = dotSourceLabel.keySet().iterator();
		while (labelKeys.hasNext()) {
			ConceptNode key = labelKeys.next();
			dotSource += "\"" + key.getName() + "\"" + dotSourceLabel.get(key);
		}

		// iterate over the relations and add them to the dotSource
		Iterator<Edge> relationsKeys = dotSourceRelations.keySet().iterator();
		while (relationsKeys.hasNext()) {
			Edge key = relationsKeys.next();
			dotSource += "\"" + key.getSubject() + "\"" + " -> " + "\"" + key.getObject() + "\" "
					+ dotSourceRelations.get(key);
		}

		dotSource += "}";

		return dotSource;
	}

	/**
	 * The dot, svg and png files are created and written.
	 * 
	 * @created 20.08.2012
	 */
	public static void createAndwriteDOTFiles(Section<?> section, String dotSource, String path, String user_app_path) {
		File dot = createFile("dot", path, section);
		File svg = createFile("svg", path, section);
		File png = createFile("png", path, section);

		// TODO all files are being deleted and still it happens that the old
		// files/graph is being displayed when the user chooses a different
		// concept
		dot.delete();
		svg.delete();
		png.delete();

		FileUtils.writeFile(dot, dotSource);
		// create svg
		String command = getDOTApp(user_app_path) + " " + dot.getAbsolutePath() +
				" -Tsvg -o " + svg.getAbsolutePath() + "";

		try {

			createFileOutOfDot(svg, dot, command);

			// create png
			command = getDOTApp(user_app_path) + " " + dot.getAbsolutePath() +
					" -Tpng -o " + png.getAbsolutePath() + "";
			createFileOutOfDot(png, dot, command);
			prepareSVG(svg);
		}
		catch (FileNotFoundException e) {
			Logger.getLogger(DotRenderer.class.getName()).log(Level.WARNING, e.toString());
		}
		catch (IOException e) {
			Logger.getLogger(DotRenderer.class.getName()).log(Level.WARNING, e.toString());
		}

	}

	public static String getDOTApp(String user_def_app) {
		ResourceBundle rb = ResourceBundle.getBundle("dotInstallation");
		String DOT_INSTALLATION = rb.getString("path");
		String app = DOT_INSTALLATION;
		if (user_def_app != null) {
			if (app.endsWith(FileUtils.FILE_SEPARATOR)) app += user_def_app;
			else {
				app = app.substring(0, app.lastIndexOf(FileUtils.FILE_SEPARATOR))
						+ FileUtils.FILE_SEPARATOR
						+ user_def_app;
			}
		}
		return app;
	}

	/**
	 * Method, that adds the target-tag to every URL in the svg-file and if
	 * requested changes the height and width of the graph.
	 * 
	 * @created 01.08.2012
	 * @param svg
	 */
	private static void prepareSVG(File svg) throws FileNotFoundException, IOException {
		FileInputStream fs = null;
		InputStreamReader in = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line;

		fs = new FileInputStream(svg);
		in = new InputStreamReader(fs);
		br = new BufferedReader(in);

		while (true) {
			line = br.readLine();
			if (line == null) break;
			line = checkLine(line);
			sb.append(line + "\n");
		}

		fs.close();
		in.close();
		br.close();

		FileWriter fstream = new FileWriter(svg);
		BufferedWriter outobj = new BufferedWriter(fstream);
		outobj.write(sb.toString());
		outobj.close();
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param line
	 */
	private static String checkLine(String line) {
		// adds target-tag to every URL
		if (line.matches("<a xlink:href=.*")) {
			line = line.substring(0, line.length() - 1) + " target=\"_top\">";
		}
		return line;
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param file
	 * @param dot
	 * @param command
	 */
	private static void createFileOutOfDot(File file, File dot, String command) throws IOException {
		FileUtils.checkWriteable(file);
		FileUtils.checkReadable(dot);
		try {
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			int exitValue = process.exitValue();
			if (exitValue != 0) {
				FileUtils.printStream(process.getErrorStream());
				throw new IOException("Command could not successfully be executed: " + command);
			}

		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @created 18.08.2012
	 * @param type
	 * @param path
	 */
	private static File createFile(String type, String path, Section<?> section) {
		String filename = path + "graph" + RenderingCore.getSectionID(section)
				+ "." + type;
		File f = new File(filename);
		return f;
	}

}

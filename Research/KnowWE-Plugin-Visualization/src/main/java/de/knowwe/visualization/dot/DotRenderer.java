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
package de.knowwe.visualization.dot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.knowwe.visualization.ConceptNode;
import de.knowwe.visualization.Edge;
import de.knowwe.visualization.GraphDataBuilder;
import de.knowwe.visualization.GraphDataBuilder.NODE_TYPE;
import de.knowwe.visualization.SubGraphData;
import de.knowwe.visualization.util.FileUtils;
import de.knowwe.visualization.util.SAXBuilderSingleton;
import de.knowwe.visualization.util.Utils;

/**
 * 
 * @author jochenreutelshofer
 * @created 29.04.2013
 */
public class DotRenderer {

	// appearance of outer node
	public static final String outerLabel = "[ shape=\"none\" fontsize=\"0\" fontcolor=\"white\" ];\n";

	/**
	 * 
	 * @created 04.09.2012
	 * @param shape
	 */
	private static String buildLabel(RenderingStyle style) {
		return " shape=\"" + style.shape + "\" ";
	}

	/**
	 * 
	 * @created 04.09.2012
	 * @param arrowtail
	 */
	private static String buildRelation(String arrowtail, String color) {
		return " arrowtail=\"" + arrowtail + "\" " + " color=\"" + color + "\" ";
	}

	/**
	 * Given the label of the inner relation, the method returns the String of
	 * the appearance of the relation.
	 * 
	 * @created 06.09.2012
	 * @param label
	 */
	private static String innerRelation(String label, String relationColorCodes) {
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

	private static RenderingStyle getStyle(NODE_TYPE type) {
		RenderingStyle style = new RenderingStyle();
		style.fontcolor = "black";

		if (type == NODE_TYPE.CLASS) {
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
	public static String createDotSources(SubGraphData data, Map<String, String> parameters) {
		String graphtitle = "Konzeptuebersicht";
		String dotSource = "";
		dotSource = "digraph " + graphtitle + " {\n";
		dotSource = insertPraefixed(dotSource, parameters);
		dotSource += DotRenderer.setSizeAndRankDir(parameters.get(GraphDataBuilder.RANK_DIRECTION),
				parameters.get(GraphDataBuilder.GRAPH_SIZE), data.getConceptDeclarations().size());

		// dotSource += generateGraphSource(dotSourceLabel, dotSourceRelations,
		// parameters);

		dotSource += generateGraphSource(data, parameters);

		dotSource += "}";

		return dotSource;
	}

	private static String generateGraphSource(SubGraphData data, Map<String, String> parameters) {
		Collection<ConceptNode> dotSourceLabel = data.getConceptDeclarations();
		Set<Edge> dotSourceRelations = data.getEdges();
		String dotSource = "";

		// iterate over the labels and add them to the dotSource
		Iterator<ConceptNode> conceptDeclarations = dotSourceLabel.iterator();
		while (conceptDeclarations.hasNext()) {
			ConceptNode key = conceptDeclarations.next();
			RenderingStyle style = DotRenderer.getStyle(key.getType());
			String label = "";

			if (key.isRoot()) {
				label = getRootLabel(key.getName(), key.getConceptLabel(), parameters,
						key.getType());
			}
			else if (key.isOuter()) {
				label = DotRenderer.outerLabel;
			}
			else {
				label = DotRenderer.createDotConceptLabel(style, key.getConceptUrl(),
						clearLabel(key.getConceptLabel()));
			}
			dotSource += "\"" + key.getName() + "\"" + label;

		}

		// iterate over the relations and add them to the dotSource
		Iterator<Edge> relationsKeys = dotSourceRelations.iterator();
		while (relationsKeys.hasNext()) {
			Edge key = relationsKeys.next();
			String label = DotRenderer.innerRelation(key.getPredicate(),
					parameters.get(GraphDataBuilder.RELATION_COLOR_CODES));
			if (key.isOuter()) {
				boolean arrowHead = key.getSubject().isOuter();
				label = DotRenderer.getOuterEdgeLabel(key.getPredicate(), arrowHead);
			}
			dotSource += "\"" + key.getSubject().getName() + "\"" + " -> " + "\""
					+ key.getObject().getName() + "\" "
					+ label;
		}
		return dotSource;
	}

	private static String clearLabel(String label) {
		String xsdStringAnnotation = "^^http://www.w3.org/2001/XMLSchema#string";
		if (label.endsWith(xsdStringAnnotation)) {
			label = label.substring(0, label.length() - xsdStringAnnotation.length());
		}
		return label;
	}

	/**
	 * 
	 * @created 27.05.2013
	 * @param concept
	 * @param style
	 * @param fillcolor
	 * @param shape
	 * @param url
	 * @param conceptLabel
	 * @return
	 */
	private static String getRootLabel(String concept, String conceptLabel, Map<String, String> parameters,
			NODE_TYPE type) {

		if (conceptLabel == null) {
			conceptLabel = concept;
		}

		// Main Concept Attributes
		String style = "filled";
		String fillcolor = "yellow";
		String shape = "ellipse";

		if (type == NODE_TYPE.CLASS) {
			shape = "rectangle";
		}
		else if (type == NODE_TYPE.PROPERTY) {
			shape = "diamond";
		}
		String url = GraphDataBuilder.createBaseURL();

		String conceptValue = "[ URL=\"" + url + "?page=" + parameters.get(GraphDataBuilder.TITLE)
				+ "&concept="
				+ concept + "\" style=\"" + style + "\" fillcolor=\"" + fillcolor + "\" " +
				// + "\" fontsize=\"" + fontsize + "\""
				" shape=\"" + shape + "\"" + " label=\""
				+ Utils.prepareLabel(conceptLabel) + "\"];\n";
		return conceptValue;
	}

	/**
	 * 
	 * @created 27.05.2013
	 * @param relation
	 * @param arrowhead
	 * @param color
	 * @param style
	 * @return
	 */
	private static String getOuterEdgeLabel(String relation, boolean showArrowHead) {
		// Relation Attributes
		String arrowhead = "arrowhead=\"none\" ";
		String arrowtail = "";
		if (showArrowHead) {
			arrowhead = "";
			arrowtail = "arrowtail = \"normal\" ";
		}
		String color = "#8b8989";
		String style = "dashed";

		String newLineRelationsValue = "[ label=\"" + relation
				+ "\" fontcolor=\"#8b8989\" " + arrowhead + arrowtail + " color=\"" + color
				+ "\" style=\"" + style + "\" ];\n";
		return newLineRelationsValue;
	}

	private static String insertPraefixed(String dotSource, Map<String, String> parameters) {
		String added = parameters.get(GraphDataBuilder.ADD_TO_DOT);
		if (added != null) dotSource += added;

		return dotSource;
	}

	/**
	 * 
	 * 
	 * @created 30.10.2012
	 */
	private static String setSizeAndRankDir(String rankDirSetting, String graphSize, int numberOfConcepts) {
		String source = "";
		String rankDir = "LR";

		if (rankDirSetting != null) {
			rankDir = rankDirSetting;
		}
		if (graphSize != null) {
			if (graphSize.matches("\\d+px")) {
				graphSize = graphSize.substring(0, graphSize.length() - 2);
				source += "graph [ rankdir=\"" + rankDir + "\"]\n";
			}
			if (graphSize.matches("\\d+")) {
				source += "graph [size=\""
						+ String.valueOf(Double.valueOf(graphSize) * 0.010415597) + "!\""
						+ " rankdir=\"" + rankDir + "\"]\n";
			}
			else {
				source += "graph [size=\""
						+ calculateAutomaticGraphSize(numberOfConcepts) + "!\""
						+ " rankdir=\"" + rankDir + "\"]\n";
			}
		}
		else {
			if (numberOfConcepts == 1 || numberOfConcepts == 2) {
				source += "graph [size=\""
						+ calculateAutomaticGraphSize(numberOfConcepts) + "!\""
						+ " rankdir=\"" + rankDir + "\"]\n";
			}
			else {
				source += "graph [ rankdir=\"" + rankDir + "\"]\n";
			}

		}
		return source;
	}

	/**
	 * 
	 * @created 08.12.2013
	 * @param numberOfConcepts
	 * @return
	 */
	private static String calculateAutomaticGraphSize(int numberOfConcepts) {
		if (numberOfConcepts == 1) return "1";
		if (numberOfConcepts == 2) return "3";
		return null;
	}

	/**
	 * The dot, svg and png files are created and written.
	 * 
	 * @created 20.08.2012
	 */
	public static void createAndwriteDOTFiles(String sectionID, String dotSource, String realPath, String user_app_path) {
		String tmpPath = FileUtils.KNOWWEEXTENSION_FOLDER + FileUtils.FILE_SEPARATOR
				+ FileUtils.TMP_FOLDER
				+ FileUtils.FILE_SEPARATOR;
		String path = realPath + FileUtils.FILE_SEPARATOR + tmpPath;

		File dot = createFile("dot", path, sectionID);
		File svg = createFile("svg", path, sectionID);
		File png = createFile("png", path, sectionID);

		dot.delete();
		svg.delete();
		png.delete();

		FileUtils.writeFile(dot, dotSource);
		// create svg

		String command = getDOTApp(user_app_path) + " " + dot.getAbsolutePath() +
				" -Tsvg -o " + svg.getAbsolutePath() + "";
		if (Utils.isWindows()) {
			command = getDOTApp(user_app_path) + " \"" + dot.getAbsolutePath() +
					"\" -Tsvg -o \"" + svg.getAbsolutePath() + "\"";
		}

		try {

			createFileOutOfDot(svg, dot, command);

			// create png
			command = getDOTApp(user_app_path) + " " + dot.getAbsolutePath() +
					" -Tpng -o " + png.getAbsolutePath() + "";
			if (Utils.isWindows()) {
				command = getDOTApp(user_app_path) + " \"" + dot.getAbsolutePath() +
						"\" -Tpng -o \"" + png.getAbsolutePath() + "\"";
			}

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

	/**
	 * 
	 * @created 27.05.2013
	 * @param style
	 * @param targetURL
	 * @param targetLabel
	 * @return
	 */
	private static String createDotConceptLabel(RenderingStyle style, String targetURL, String targetLabel) {
		String newLineLabelValue;
		newLineLabelValue = "[ URL=\"" + targetURL + "\""
				+ DotRenderer.buildLabel(style) + "label=\""
				+ Utils.prepareLabel(targetLabel) + "\" ];\n";
		return newLineLabelValue;
	}

	private static String getDOTApp(String user_def_app) {
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
	 * Adds the target-tag to every URL in the svg-file
	 * 
	 * @created 01.08.2012
	 * @param svg
	 */
	private static void prepareSVG(File svg) throws FileNotFoundException, IOException {
		try {
			Document doc = SAXBuilderSingleton.getInstance().build(svg);
			Element root = doc.getRootElement();
			if (root == null) return;

			findAElements(root);

			XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
			xmlOutputter.output(doc, new FileWriter(svg));
		}
		catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Iterates through all the children of root to find all a-tag elements.
	 * 
	 * @created 21.12.2013
	 * @param root
	 */
	private static void findAElements(Element root) {
		List<?> children = root.getChildren();
		Iterator<?> iter = children.iterator();
		while (iter.hasNext()) {
			Element childElement = (Element) iter.next();
			if (childElement.getName().equals("a")) {
				addTargetAttribute(childElement);
			}
			else {
				findAElements(childElement);
			}
		}
	}

	/**
	 * Adds the target-attribute to the element.
	 * 
	 * @created 21.12.2013
	 * @param element
	 */
	private static void addTargetAttribute(Element element) {
		Attribute target = new Attribute("target", "_top");
		element.setAttribute(target);
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
	private static File createFile(String type, String path, String sectionID) {
		String filename = path + "graph" + sectionID
				+ "." + type;
		File f = new File(filename);
		return f;
	}

	/**
	 * A data class for storing the style info of a node/property etc.
	 * 
	 * @author Joachim Baumeister (denkbares GmbH)
	 * @created 02.05.2013
	 */
	static class RenderingStyle {

		String shape = "box";
		String fontcolor = "black";
		String fontsize = "10";
	}
}

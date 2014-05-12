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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.d3web.strings.Strings;
import de.d3web.utils.Log;
import de.knowwe.visualization.ConceptNode;
import de.knowwe.visualization.Edge;
import de.knowwe.visualization.GraphDataBuilder;
import de.knowwe.visualization.GraphDataBuilder.NODE_TYPE;
import de.knowwe.visualization.SubGraphData;
import de.knowwe.visualization.util.FileUtils;
import de.knowwe.visualization.util.SAXBuilderSingleton;
import de.knowwe.visualization.util.Utils;

/**
 * @author jochenreutelshofer
 * @created 29.04.2013
 */
public class DOTRenderer {

	// appearance of outer node
	public static final String outerLabel = "[ shape=\"none\" fontsize=\"0\" fontcolor=\"white\" ];\n";

	private static String buildLabel(RenderingStyle style) {
		StringBuilder result = new StringBuilder();
		result.append(" shape=\"").append(style.getShape()).append("\" ");
		if (!Strings.isBlank(style.getStyle())) {
			result.append(" style=\"").append(style.getStyle()).append("\" ");
		}
		if (!Strings.isBlank(style.getFillcolor())) {
			result.append(" fillcolor=\"").append(style.getFillcolor()).append("\" ");
		}
		return result.toString();
	}

	private static String buildRelation(String arrowtail, String color) {
		return " arrowtail=\"" + arrowtail + "\" " + " color=\"" + color + "\" ";
	}

	/**
	 * Given the label of the inner relation, the method returns the String of the appearance of the relation.
	 *
	 * @created 06.09.2012
	 */
	private static String innerRelation(String label, String relationColorCodes) {
		// Basic Relation Attributes
		String arrowtail = "normal";

		String color = Utils.getColorCode(label, relationColorCodes);
		if (color == null) {
			// black is default
			color = "black";
		}
		return "[ label = \"" + label
				+ "\"" + buildRelation(arrowtail, color) + " ];\n";
	}

	/**
	 * The sources from the maps are being written into the String-dotSource.
	 *
	 * @created 18.08.2012
	 */
	public static String createDotSources(SubGraphData data, Map<String, String> parameters) {
		String graphtitle = "Konzeptuebersicht";
		String dotSource = "digraph " + graphtitle + " {\n";
		dotSource = insertPraefixed(dotSource, parameters);
		dotSource += DOTRenderer.setSizeAndRankDir(parameters.get(GraphDataBuilder.RANK_DIRECTION),
				parameters.get(GraphDataBuilder.GRAPH_SIZE), data.getConceptDeclarations().size());

		dotSource += generateGraphSource(data, parameters);

		dotSource += "}";

		return dotSource;
	}

	private static String generateGraphSource(SubGraphData data, Map<String, String> parameters) {
		Collection<ConceptNode> dotSourceLabel = data.getConceptDeclarations();
		Set<Edge> dotSourceRelations = data.getEdges();
		String dotSource = "";

		// iterate over the labels and add them to the dotSource
		for (ConceptNode key : dotSourceLabel) {

			RenderingStyle style = key.getStyle();

			// root is rendered highlighted
			if (key.isRoot()) {
				style.addStyle("bold");
			}

			String label;

			if (key.isOuter()) {
				label = DOTRenderer.outerLabel;
			}
			else {
				String nodeLabel = clearLabel(key.getConceptLabel());
				//nodeLabel = "<<B>"+nodeLabel+"</B>>";

				if ((!key.getType().equals(NODE_TYPE.LITERAL)) &&
						parameters.get(GraphDataBuilder.USE_LABELS) != null && parameters.get(GraphDataBuilder.USE_LABELS)
						.equals("false")) {
					// use of labels suppressed by the user -> show concept name, i.e. uri
					nodeLabel = key.getName();
				}
				label = DOTRenderer.createDotConceptLabel(style, key.getConceptUrl(), nodeLabel, true);
			}
			dotSource += "\"" + key.getName() + "\"" + label;

		}

		// iterate over the relations and add them to the dotSource
		for (Edge key : dotSourceRelations) {
			String label = DOTRenderer.innerRelation(key.getPredicate(),
					parameters.get(GraphDataBuilder.RELATION_COLOR_CODES));
			if (key.isOuter()) {
				boolean arrowHead = key.getSubject().isOuter();
				label = DOTRenderer.getOuterEdgeLabel(key.getPredicate(), arrowHead);
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

		return "[ label=\"" + relation
				+ "\" fontcolor=\"#8b8989\" " + arrowhead + arrowtail + " color=\"" + color
				+ "\" style=\"" + style + "\" ];\n";
	}

	private static String insertPraefixed(String dotSource, Map<String, String> parameters) {
		String added = parameters.get(GraphDataBuilder.ADD_TO_DOT);
		if (added != null) dotSource += added;

		return dotSource;
	}

	/**
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
			int timeout = 10000;
			prepareSVG(svg, timeout);
		}
		catch (FileNotFoundException e) {
			Log.warning(e.getMessage(), e);
		}
		catch (IOException e) {
			Log.warning(e.getMessage(), e);
		}

	}

	private static String createDotConceptLabel(RenderingStyle style, String targetURL, String targetLabel, boolean prepareLabel) {
		String newLineLabelValue;
		String url = "";
		if (targetURL != null) {
			url = "URL=\"" + Strings.encodeHtml(targetURL) + "\" ";
		}
		if (prepareLabel) {
			// prevents HTML rendering !
			targetLabel = "\"" + Utils.prepareLabel(targetLabel) + "\"";
		}

		newLineLabelValue = "[ " + url
				+ DOTRenderer.buildLabel(style) + "label="
				+ targetLabel + " ];\n";
		return newLineLabelValue;
	}

	private static String getDOTApp(String user_def_app) {
		ResourceBundle rb = ResourceBundle.getBundle("dotInstallation");
		String app = rb.getString("path");
		if (user_def_app != null) {
			if (app.endsWith(FileUtils.FILE_SEPARATOR)) {
				app += user_def_app;
			}
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
	 */
	private static void prepareSVG(final File svg, final int timeout) throws IOException {
		try {

			// check if svg file is closed, otherwise wait timeout second
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
			final Future<Boolean> handler = executor.submit(new Callable() {
				@Override
				public Boolean call() throws Exception {
					while (!Utils.isFileClosed(svg)) {
						// wait
					}
					return true;
				}
			});

			// cancel handler after timeout seconds
			executor.schedule(new Runnable(){
				@Override
				public void run(){
					handler.cancel(true);
				}
			}, timeout, TimeUnit.MILLISECONDS);

			// svg hasn't been closed yet, return
			if (!handler.get()) return;

			Document doc = SAXBuilderSingleton.getInstance().build(svg);
			Element root = doc.getRootElement();
			if (root == null) return;

			findAElements(root);

			XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
			xmlOutputter.output(doc, new FileWriter(svg));
		}
		catch (JDOMException e) {
			Log.warning(e.getMessage(), e);
		}
		catch (InterruptedException e) {
			Log.warning(e.getMessage(), e);
		}
		catch (ExecutionException e) {
			Log.warning(e.getMessage(), e);
		}
	}

	/**
	 * Iterates through all the children of root to find all a-tag elements.
	 *
	 * @created 21.12.2013
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
	 */
	private static void addTargetAttribute(Element element) {
		Attribute target = new Attribute("target", "_top");
		element.setAttribute(target);
	}

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
			Log.warning(e.getMessage(), e);
		}
	}

	private static File createFile(String type, String path, String sectionID) {
		String filename = path + "graph" + sectionID
				+ "." + type;
		return new File(filename);
	}

}

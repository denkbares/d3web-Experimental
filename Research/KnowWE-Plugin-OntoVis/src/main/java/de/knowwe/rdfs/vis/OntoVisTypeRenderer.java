package de.knowwe.rdfs.vis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.rdf2go.Rdf2GoCore;

public class OntoVisTypeRenderer extends DefaultMarkupRenderer {

	// path of the local dot-Installation
	private final static String dotInstallation = "D:\\Graphviz\\bin\\dot";

	// Annotations
	private List<String> excludedKnodes;
	private List<String> excludedRelations;
	private int requestedDepth;
	private int requestedHeight;
	private String graphWidth;
	private String graphHeight;

	private int depth;
	private int height;

	// paths
	private String realPath;
	private final String tmpPath = "KnowWEExtension\\tmp\\";
	private String path;

	// sources for the dot-file
	private String dotSource;
	private Map<String, String> dotSourceLabel;
	private Map<String, String> dotSourceRelations;

	@Override
	public void renderContents(Section<?> section, UserContext user, StringBuilder string) {
		String request = getRequest(user);
		String concept = getConcept(user, section);
		getAnnotations(user, section);

		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only
		realPath = servletContext.getRealPath("");
		path = realPath + "\\" + tmpPath;
		dotSource = "digraph finite_state_machine {\n";
		dotSourceLabel = new LinkedHashMap<String, String>();
		dotSourceRelations = new LinkedHashMap<String, String>();

		buildSources(concept, request, section);
		writeAndAppendFiles(section, string);
	}

	/**
	 * 
	 * @created 18.08.2012
	 * @param user
	 */
	private String getRequest(UserContext user) {
		if (user != null) {
			if (user.getRequest() != null) {
				if (user.getRequest().getRequestURL() != null) {
					return user.getRequest().getRequestURL().toString();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param section
	 */
	private void getAnnotations(UserContext user, Section<?> section) {
		getSuccessors(section);
		getPredecessors(section);
		graphWidth = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_WIDTH);
		graphHeight = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_HEIGHT);
		getExcludedKnodes(section);
		getExcludedRelations(section);
	}

	/**
	 * 
	 * @created 18.08.2012
	 * @return
	 */
	private String getConcept(UserContext user, Section<?> section) {
		String parameter = user.getParameter("concept");
		if (parameter != null) {
			return parameter;
		}
		return OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_CONCEPT);
	}

	/**
	 * 
	 * @created 18.08.2012
	 * @param section
	 */
	private void getPredecessors(Section<?> section) {
		if (!isValidInt(OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_PREDECESSORS))) {
			requestedHeight = 0;
		}
		else {
			requestedHeight = Integer.parseInt(OntoVisType.getAnnotation(section,
					OntoVisType.ANNOTATION_PREDECESSORS));
		}
	}

	/**
	 * 
	 * @created 18.08.2012
	 * @param section
	 */
	private void getSuccessors(Section<?> section) {
		if (!isValidInt(OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_SUCCESSORS))) {
			requestedDepth = 0;
		}
		else {
			requestedDepth = Integer.parseInt(OntoVisType.getAnnotation(section,
					OntoVisType.ANNOTATION_SUCCESSORS));
		}
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param section
	 */
	private void getExcludedRelations(Section<?> section) {
		excludedRelations = new ArrayList<String>();
		String exclude = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDERELATIONS);
		if (exclude != null) {
			String[] array = exclude.split(",");
			excludedRelations = Arrays.asList(array);
		}
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param section
	 */
	private void getExcludedKnodes(Section<?> section) {
		excludedKnodes = new ArrayList<String>();
		String exclude = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDEKNODES);
		if (exclude != null) {
			String[] array = exclude.split(",");
			excludedKnodes = Arrays.asList(array);
		}
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param concept
	 * @param request
	 * @param section
	 */
	private void buildSources(String concept, String request, Section<?> section) {
		// if requested, the predecessor are added to the source
		if (requestedHeight > 0) {
			height = 0;
			addPredecessors(concept, request, section);
		}
		insertMainConcept(concept, request, section);
		// if requested, the successors are added to the source
		if (requestedDepth > 0) {
			depth = 0;
			addSuccessors(concept, request, section);
		}
		// if the look of the main concept was changed during the process, it is
		// resettet
		insertMainConcept(concept, request, section);
		connectSources();
	}

	/**
	 * 
	 * @created 18.08.2012
	 * @param concept
	 * @param request
	 * @param section
	 */
	private void insertMainConcept(String concept, String request, Section<?> section) {
		// Main Concept Attributes
		String style = "filled";
		String fillcolor = "yellow";
		String fontsize = "14";

		String conceptKey = "\"" + concept + "\" ";
		String conceptValue = "[ URL=\"" + request + "?page=" + section.getTitle() + "&concept="
				+ concept + "\" style=\"" + style + "\" fillcolor=\"" + fillcolor
				+ "\" fontsize=\"" + fontsize + "\" ];\n";
		if (dotSourceLabel.get(conceptKey) != conceptValue) {
			dotSourceLabel.put(conceptKey, conceptValue);
		}
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param section
	 */
	private void writeAndAppendFiles(Section<?> section, StringBuilder string) {
		File dot = createFile("dot", path, section);
		File svg = createFile("svg", path, section);
		File png = createFile("png", path, section);

		// TODO all files are being deleted and still it happens that the old
		// files/graph is being displayed when the user chooses a different
		// concept
		dot.delete();
		svg.delete();
		png.delete();

		writeDot(dot);
		// create svg
		String command = dotInstallation + " \"" + dot.getAbsolutePath() + "\"" +
				" -Tsvg -o\"" + svg.getAbsolutePath() + "\"";
		createFileOutOfDot(svg, dot, command);

		// create png
		command = dotInstallation + " \"" + dot.getAbsolutePath() + "\"" +
				" -Tpng -o\"" + png.getAbsolutePath() + "\"";
		createFileOutOfDot(png, dot, command);

		prepareSVG(svg, section);
		string.append("<object data='" + tmpPath + "graph" + section.getID()
				+ ".svg' type=\"image/svg+xml\"><img alt='graph' src='" + tmpPath + "graph"
				+ section.getID() + ".png'></object>");
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param dot
	 */
	private void writeDot(File dot) {
		FileWriter writer;
		try {
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
	 * 
	 * @created 20.08.2012
	 * @param file
	 * @param dot
	 */
	private void createFileOutOfDot(File file, File dot, String command) {
		try {
			Runtime.getRuntime().exec(command);
			boolean fileFinished = file.exists();
			int time = 0;
			int timeout = 8000;

			// test if file is finished yet, if not sleep thread until the file
			// is finished or a timeout is reached
			loop: while (!fileFinished) {
				Thread.sleep(100);
				time += 100;
				fileFinished = file.exists();
				if (time == timeout) {
					break loop;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @created 18.08.2012
	 * @param string
	 */
	private File createFile(String type, String path, Section<?> section) {
		String filename = path + "graph" + section.getID()
				+ "." + type;
		File f = new File(filename);
		return f;
	}

	/**
	 * 
	 * @created 18.08.2012
	 */
	private void connectSources() {
		// iterate over the labels and add them to the dotSource
		Iterator<String> labelKeys = dotSourceLabel.keySet().iterator();
		while (labelKeys.hasNext()) {
			String key = labelKeys.next();
			dotSource += key + dotSourceLabel.get(key);
		}

		// iterate over the relations and add them to the dotSource
		Iterator<String> relationsKeys = dotSourceRelations.keySet().iterator();
		while (relationsKeys.hasNext()) {
			String key = relationsKeys.next();
			dotSource += key + dotSourceRelations.get(key);
		}

		dotSource += "}";
	}

	/**
	 * Method, that recursively adds all successors of the requested concept up
	 * to the chosen depth
	 * 
	 * @created 26.06.2012
	 * @param depth
	 */
	private void addSuccessors(String concept, String request, Section<?> section) {
		String query = "SELECT ?y ?z WHERE { lns:" + concept
				+ " ?y ?z.}";
		ClosableIterator<QueryRow> result =
				Rdf2GoCore.getInstance().sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			String yURI = row.getValue("y").toString();
			String y = clean(yURI.substring(yURI.indexOf("#") + 1));

			String zURI = row.getValue("z").toString();
			String z = clean(zURI.substring(zURI.indexOf("#") + 1));

			if (excludedRelation(y, section)) {
				continue;
			}
			if (excludedKnode(z, section)) {
				continue;
			}

			addConcept(z, concept, y, request, section);

			depth++;
			if (depth < requestedDepth) {
				addSuccessors(z, request, section);
			}
			if (depth == requestedDepth) {
				addOutgoingEdgesSuccessors(z, request, section);
			}
			depth--;
		}
	}

	/**
	 * 
	 * @created 03.08.2012
	 * @param concept
	 * @param request
	 * @param section
	 * @param dotSourceLabel
	 * @param dotSourceRelations
	 */
	private void addPredecessors(String concept, String request, Section<?> section) {
		String query = "SELECT ?x ?y WHERE { ?x ?y lns:" + concept + "}";
		ClosableIterator<QueryRow> result =
				Rdf2GoCore.getInstance().sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			String xURI = row.getValue("x").toString();
			String x = clean(xURI.substring(xURI.indexOf("#") + 1));

			String yURI = row.getValue("y").toString();
			String y = clean(yURI.substring(yURI.indexOf("#") + 1));

			if (excludedRelation(y, section)) {
				continue;
			}
			if (excludedKnode(x, section)) {
				continue;
			}

			height++;
			if (height < requestedHeight) {
				addPredecessors(x, request, section);
			}
			if (height == requestedHeight) {
				addOutgoingEdgesPredecessors(x, request, section);
			}
			height--;

			addConcept(x, concept, y, request, section);
		}
	}

	/**
	 * 
	 * 
	 * @created 26.06.2012
	 */
	private void addOutgoingEdgesSuccessors(String concept, String request, Section<?> section) {
		String query = "SELECT ?y ?z WHERE { lns:" + concept
				+ " ?y ?z.}";
		ClosableIterator<QueryRow> result =
				Rdf2GoCore.getInstance().sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			String yURI = row.getValue("y").toString();
			String y = clean(yURI.substring(yURI.indexOf("#") + 1));

			String zURI = row.getValue("z").toString();
			String z = clean(zURI.substring(zURI.indexOf("#") + 1));
			if (excludedRelation(y, section)) {
				continue;
			}
			if (excludedKnode(z, section)) {
				continue;
			}
			addOuterConcept(concept, z);
		}
	}

	private void addOutgoingEdgesPredecessors(String concept, String request, Section<?> section) {
		String query = "SELECT ?x ?y WHERE { ?x ?y lns:" + concept + "}";
		ClosableIterator<QueryRow> result =
				Rdf2GoCore.getInstance().sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			String xURI = row.getValue("x").toString();
			String x = clean(xURI.substring(xURI.indexOf("#") + 1));

			String yURI = row.getValue("y").toString();
			String y = clean(yURI.substring(yURI.indexOf("#") + 1));
			if (excludedRelation(y, section)) {
				continue;
			}
			if (excludedKnode(x, section)) {
				continue;
			}
			addOuterConcept(x, concept);
		}
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param from
	 * @param to
	 * @param relation between from --> to
	 * @param request
	 * @param section
	 */
	private void addConcept(String from, String to, String relation, String request, Section<?> section) {
		// Label Attributes
		String shape = "ellipse";
		String fontcolor = "black";
		String fontsizeL = "14";
	
		// Relation Attributes
		String arrowtail = "normal";
		String color = "black";
		String fontsizeR = "13";
	
		String newLineLabelKey = "\"" + from + "\" ";
		String newLineRelationsKey = "\"" + from + "\"->\"" + to + "\" ";
		String newLineLabelValue = "[ URL=\"" + request + "?page=" + section.getTitle()
				+ "&concept=" + from
				+ "\" fontcolor=\"" + fontcolor + "\" shape=\"" + shape + "\" fontsize=\""
				+ fontsizeL + "\" ];\n";
		String newLineRelationsValue = "[ label = \"" + relation
				+ "\" arrowtail=\"" + arrowtail + "\" color=\"" + color + "\" fontsize=\""
				+ fontsizeR + "\" ];\n";
		if (!dotSourceLabel.containsKey(newLineLabelKey)
				|| (dotSourceLabel.get(newLineLabelKey) != newLineLabelValue)) {
			dotSourceLabel.put(newLineLabelKey, newLineLabelValue);
		}
		if (!dotSourceRelations.containsKey(newLineRelationsKey)
				|| (dotSourceRelations.get(newLineRelationsKey)) != newLineRelationsValue) {
			dotSourceRelations.put(newLineRelationsKey, newLineRelationsValue);
		}
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param from
	 * @param to
	 */
	private void addOuterConcept(String from, String to) {
		// Relation Attributes
		String arrowtail = "none";
		String color = "#BEBEBE";
		String style = "dashed";

		String newLineLabelKey = "\"" + from + "\" ";
		String newLineRelationsKey = "\"" + from + "\"->\"" + to + "\" ";
		String newLineLabelValue = "[ shape=\"none\" fontsize=\"0\" fontcolor=\"white\" ];\n";
		String newLineRelationsValue = "[ arrowtail=\"" + arrowtail + "\" color=\"" + color
				+ "\" style=\"" + style + "\" ];\n";

		if (!dotSourceLabel.containsKey(newLineLabelKey)) {
			dotSourceLabel.put(newLineLabelKey, newLineLabelValue);
		}
		if (!dotSourceRelations.containsKey(newLineRelationsKey)) {
			dotSourceRelations.put(newLineRelationsKey, newLineRelationsValue);
		}
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param x
	 * @param section
	 * @return
	 */
	private boolean excludedKnode(String x, Section<?> section) {
		if (excludedKnodes != null) {
			Iterator<String> iterator = excludedKnodes.iterator();
			while (iterator.hasNext()) {
				String next = iterator.next().trim();
				if (x.matches(next)) return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param y
	 * @param section
	 * @return
	 */
	private boolean excludedRelation(String y, Section<?> section) {
		if (excludedRelations != null) {
			Iterator<String> iterator = excludedRelations.iterator();
			while (iterator.hasNext()) {
				String next = iterator.next().trim();
				if (y.matches(next)) return true;
			}
		}
		return false;
	}

	/**
	 * Method, that adds the target-tag to every URL in the svg-file
	 * 
	 * @created 01.08.2012
	 * @param svg
	 */
	private void prepareSVG(File svg, Section<?> section) {
		FileInputStream fs = null;
		InputStreamReader in = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line;

		try {
			fs = new FileInputStream(svg);
			in = new InputStreamReader(fs);
			br = new BufferedReader(in);

			while (true) {
				line = br.readLine();
				if (line == null) break;
				line = checkLine(line, section);
				sb.append(line + "\n");
			}

			fs.close();
			in.close();
			br.close();

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter fstream = new FileWriter(svg);
			BufferedWriter outobj = new BufferedWriter(fstream);
			outobj.write(sb.toString());
			outobj.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param readLine
	 * @return
	 */
	private String checkLine(String line, Section<?> section) {
		if (isValidHeightOrWidth(graphWidth) && isValidHeightOrWidth(graphHeight)) {
			if (line.matches("<svg width=.*")) {
				line = line.replaceAll("width=\"\\d+pt\"", "width=\"" + graphWidth + "\"");
				line = line.replaceAll("height=\"\\d+pt\"",
						"height=\"" + graphHeight + "\"");
			}
		}
		if (line.matches("<a xlink:href=.*")) {
			line = line.substring(0, line.length() - 1) + " target=\"_top\">";
		}
		return line;
	}

	/**
	 * tests if var is a valid width or height value
	 * 
	 * @created 08.08.2012
	 * @return
	 */
	private boolean isValidHeightOrWidth(String var) {
		if (var == null) {
			return false;
		}
		if (var.matches("\\d{2,4}pt") || var.matches("(100|[0-9]{1,2})%")) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @created 20.08.2012
	 * @param input
	 * @return
	 */
	private boolean isValidInt(String input) {
		try {
			Integer value = Integer.parseInt(input);
			if (value > 5 || value < 0) {
				return false;
			}
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * 
	 * @created 31.07.2012
	 * @return
	 */
	private String clean(String line) {
		if (line.matches("http:.*/?page=.*")) {
			line = line.substring(line.indexOf("page=") + 5);
		}
		return line;
	}

}

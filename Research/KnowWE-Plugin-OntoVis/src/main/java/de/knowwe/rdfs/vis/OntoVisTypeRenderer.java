package de.knowwe.rdfs.vis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.rdf2go.Rdf2GoCore;

public class OntoVisTypeRenderer extends DefaultMarkupRenderer {

	// path of the local dot-Installation
	private final static String dotInstallation = "/usr/local/bin/dot";
	// private final static String dotInstallation = "D:\\Graphviz\\bin\\dot";

	// section
	private Section<?> section;

	// Annotations
	private List<String> excludedNodes;
	private List<String> excludedRelations;
	private int requestedDepth;
	private int requestedHeight;
	private String graphWidth;
	private String graphHeight;
	private String format;
	private boolean showClasses;
	private boolean showProperties;

	private int depth;
	private int height;

	// paths
	private String realPath;
	private final String tmpPath;
	private static final String TMP_FOLDER = "tmp";
	private static final String KNOWWEEXTENSION_FOLDER = "KnowWEExtension";
	private String path;
	private static String FILE_SEPARATOR = System.getProperty("file.separator");

	// sources for the dot-file
	private String dotSource;
	private Map<String, String> dotSourceLabel;
	private Map<String, String> dotSourceRelations;

	// appearances
	private final String outerLabel = "[ shape=\"none\" fontsize=\"0\" fontcolor=\"white\" ];\n";

	public OntoVisTypeRenderer() {
		tmpPath = KNOWWEEXTENSION_FOLDER + FILE_SEPARATOR + TMP_FOLDER + FILE_SEPARATOR;
	}

	@Override
	public void renderContents(Section<?> section, UserContext user, StringBuilder string) {
		this.section = section;
		String request = getRequest(user);
		String concept = getConcept(user);
		getAnnotations(user);

		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only
		realPath = servletContext.getRealPath("");
		path = realPath + FILE_SEPARATOR + tmpPath;
		dotSource = "digraph finite_state_machine {\n";
		dotSourceLabel = new LinkedHashMap<String, String>();
		dotSourceRelations = new LinkedHashMap<String, String>();

		buildSources(concept, request);
		try {
			writeFiles();
		}
		catch (FileNotFoundException e) {
			// TODO: render proper message
			string.append("Warning:" + e.toString());
		}
		catch (IOException e) {
			// TODO: render proper message
			string.append("Warning:" + e.toString());
		}
		appendFiles(string);
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
	 * @param user
	 */
	private void getAnnotations(UserContext user) {
		getSuccessors();
		getPredecessors();
		graphWidth = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_WIDTH);
		graphHeight = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_HEIGHT);
		getExcludedNodes();
		getExcludedRelations();
		format = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
		}
		getShowAnnotations();
	}

	/**
	 * 
	 * @created 18.08.2012
	 */
	private String getConcept(UserContext user) {
		String parameter = user.getParameter("concept");
		if (parameter != null) {
			return parameter;
		}
		return OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_CONCEPT);
	}

	/**
	 * 
	 * @created 18.08.2012
	 */
	private void getPredecessors() {
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
	 */
	private void getSuccessors() {
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
	 */
	private void getExcludedRelations() {
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
	 */
	private void getExcludedNodes() {
		excludedNodes = new ArrayList<String>();
		String exclude = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDENODES);
		if (exclude != null) {
			String[] array = exclude.split(",");
			excludedNodes = Arrays.asList(array);
		}
	}

	/**
	 * 
	 * @created 13.09.2012
	 */
	private void getShowAnnotations() {
		String classes = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWCLASSES);
		if (classes == null) {
			showClasses = true;
		}
		else if (classes.equals("false")) {
			showClasses = false;
		}
		else {
			showClasses = true;
		}
		String properties = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWPROPERTIES);
		if (properties == null) {
			showProperties = true;
		}
		else if (properties.equals("false")) {
			showProperties = false;
		}
		else {
			showProperties = true;
		}
	}

	/**
	 * Adds all requested concepts and information to the dotSources (the maps).
	 * 
	 * @created 20.08.2012
	 * @param concept
	 * @param request
	 */
	private void buildSources(String concept, String request) {
		// if requested, the predecessor are added to the source
		if (requestedHeight > 0) {
			height = 0;
			addPredecessors(concept, request);
		}
		insertMainConcept(concept, request);
		// if requested, the successors are added to the source
		if (requestedDepth > 0) {
			depth = 0;
			addSuccessors(concept, request);
		}
		// if the appearance of the main concept was changed during the process,
		// it is reset
		insertMainConcept(concept, request);
		// check all relations again
		checkRelations();
		connectSources();
	}

	/**
	 * 
	 * @created 18.08.2012
	 * @param concept
	 * @param request
	 */
	private void insertMainConcept(String concept, String request) {
		// Main Concept Attributes
		String style = "filled";
		String fillcolor = "yellow";
		String fontsize = "14";
		String shape = "ellipse";

		String askClass = "ASK { " + createSparqlURI(concept) + " rdf:type owl:Class}";
		boolean isClass = Rdf2GoCore.getInstance().sparqlAsk(askClass);
		String askProperty = "ASK { " + createSparqlURI(concept) + " rdf:type owl:Property}";
		boolean isProperty = Rdf2GoCore.getInstance().sparqlAsk(askProperty);

		if (isClass) {
			shape = "rectangle";
		}
		else if (isProperty) {
			shape = "diamond";
		}

		String conceptKey = "\"" + concept + "\"";
		String conceptValue = "[ URL=\"" + request + "?page=" + section.getTitle() + "&concept="
				+ concept + "\" style=\"" + style + "\" fillcolor=\"" + fillcolor
				+ "\" fontsize=\"" + fontsize + "\" shape=\"" + shape + "\"];\n";
		// the main concept is inserted in the dotSource resp. reset if
		// the appearance was changed
		if (dotSourceLabel.get(conceptKey) != conceptValue) {
			dotSourceLabel.put(conceptKey, conceptValue);
		}
	}

	/**
	 * All inner relations (relations between two inner nodes) are checked if
	 * their appearance equals the appearance of the relation to an outer node.
	 * If that is the case it is corrected.
	 * 
	 * @created 04.09.2012
	 */
	private void checkRelations() {
		// First step: Iterate over all relations and check if it's an outer
		// relation (gray, dashed and no arrowhead). If it is, save it.
		List<String> keysWithGrayRelation = new LinkedList<String>();
		Iterator<String> keys = dotSourceRelations.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			if (dotSourceRelations.get(key).contains("fontcolor=\"white\"")) {
				keysWithGrayRelation.add(key);
			}
		}

		// Second step: Now check both concepts of those relations and see, if
		// they are both normal (inner) nodes. If they are, the relation
		// shouldnt be gray, so it will be changed.
		Iterator<String> relations = keysWithGrayRelation.iterator();
		while (relations.hasNext()) {
			String relation = relations.next();
			String[] concepts = relation.split("->");

			if (dotSourceLabel.get(concepts[0]) != outerLabel
					&& dotSourceLabel.get(concepts[1]) != outerLabel) {
				String temp = dotSourceRelations.get(relation);
				String labelOfRelation = temp.substring(9,
						temp.indexOf("fontcolor") - 2);
				dotSourceRelations.put(relation, innerRelation(labelOfRelation));
			}
		}
	}

	/**
	 * Given the label of the inner relation, the method returns the String of
	 * the appearance of the relation.
	 * 
	 * @created 06.09.2012
	 * @param label
	 */
	private String innerRelation(String label) {
		// Basic Relation Attributes
		String arrowtail = "normal";
		String color = "black";
		String fontsize = "13";

		return "[ label = \"" + label
				+ "\"" + buildRelation(arrowtail, color, fontsize) + " ];\n";
	}

	/**
	 * The sources from the maps are being written into the String-dotSource.
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
	 * The dot, svg and png files are created and written.
	 * 
	 * @created 20.08.2012
	 */
	private void writeFiles() throws FileNotFoundException, IOException {
		File dot = createFile("dot", path);
		File svg = createFile("svg", path);
		File png = createFile("png", path);

		// TODO all files are being deleted and still it happens that the old
		// files/graph is being displayed when the user chooses a different
		// concept
		dot.delete();
		svg.delete();
		png.delete();

		writeDot(dot);
		// create svg
		String command = dotInstallation + " " + dot.getAbsolutePath() +
				" -Tsvg -o " + svg.getAbsolutePath();
		createFileOutOfDot(svg, dot, command);

		// create png
		command = dotInstallation + " " + dot.getAbsolutePath() +
				" -Tpng -o " + png.getAbsolutePath();
		createFileOutOfDot(png, dot, command);
		prepareSVG(svg);
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param dot
	 */
	private void writeDot(File dot) {
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
	 * Check if the specified file and the required folder structure can be
	 * written. This prevents failures later on when the pdf will be created. If
	 * the file cannot be written an {@link IOException} is thrown.
	 * 
	 * @created 20.04.2011
	 * @param file the file to be written
	 * @throws IOException if the file cannot be written
	 */
	private static void checkWriteable(File file) throws IOException {
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

	/**
	 * 
	 * @created 20.08.2012
	 * @param file
	 * @param dot
	 * @param command
	 */
	private void createFileOutOfDot(File file, File dot, String command) throws IOException {
		checkWriteable(file);
		checkReadable(dot);
		try {
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			int exitValue = process.exitValue();
			if (exitValue != 0) {
				printStream(process.getErrorStream());
				throw new IOException("Command could not successfully be executed: " + command);
			}

		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void printStream(InputStream str) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(str));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
	}

	/**
	 * 
	 * @created 18.08.2012
	 * @param type
	 * @param path
	 */
	private File createFile(String type, String path) {
		String filename = path + "graph" + section.getID()
				+ "." + type;
		File f = new File(filename);
		return f;
	}

	/**
	 * Checks if the specified file can be read. If not an {@link IOException}
	 * is thrown.
	 * 
	 * @created 20.04.2011
	 * @param file the file to read from
	 * @throws IOException if the file cannot be read
	 */
	private static void checkReadable(File file) throws IOException {
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
	 * 
	 * @created 03.09.2012
	 * @param StringBuilder
	 */
	private void appendFiles(StringBuilder string) {
		String png_default = Strings.maskHTML("<img alt='graph' src='" + tmpPath + "graph"
				+ section.getID() + ".png'>");
		String svg = Strings.maskHTML("<object data='" + tmpPath + "graph" + section.getID()
				+ ".svg' type=\"image/svg+xml\">" + png_default + "</object>");
		if (format == null) {
			string.append(png_default);
		}
		else if (format.equals("svg")) {
			string.append(svg);
		}
		else {
			string.append(png_default);
		}
	}

	/**
	 * Method, that recursively adds all successors of the requested concept up
	 * to the chosen depth.
	 * 
	 * @created 26.06.2012
	 * @param concept
	 * @param request
	 */
	private void addSuccessors(String concept, String request) {
		String query = "SELECT ?y ?z WHERE { " + createSparqlURI(concept)
				+ " ?y ?z.}";
		ClosableIterator<QueryRow> result =
				Rdf2GoCore.getInstance().sparqlSelectIt(
						query);
		loop: while (result.hasNext()) {
			QueryRow row = result.next();
			String yURI = row.getValue("y").toString();
			String y = clean(yURI.substring(yURI.indexOf("#") + 1));

			String zURI = row.getValue("z").toString();
			String z = clean(zURI.substring(zURI.indexOf("#") + 1));

			if (excludedRelation(y)) {
				continue loop;
			}
			if (excludedNode(z)) {
				continue loop;
			}

			String askClass = "ASK { " + createSparqlURI(z) + " rdf:type owl:Class}";
			boolean isClass = Rdf2GoCore.getInstance().sparqlAsk(askClass);
			String askProperty = "ASK { " + createSparqlURI(y) + " rdf:type rdf:Property}";
			boolean isProperty = Rdf2GoCore.getInstance().sparqlAsk(askProperty);
			String type = "basic";

			if (isClass) {
				if (showClasses) {
					type = "class";
				}
				else {
					continue loop;
				}
			}
			else if (isProperty) {
				if (showProperties) {
					type = "property";
				}
				else {
					continue loop;
				}
			}
			addConcept(concept, z, y, request, false, type);

			depth++;
			if (depth < requestedDepth) {
				addSuccessors(z, request);
			}
			if (depth == requestedDepth) {
				addOutgoingEdgesSuccessors(z, request);
			}
			depth--;
		}
	}

	/**
	 * 
	 * @created 03.08.2012
	 * @param concept
	 * @param request
	 */
	private void addPredecessors(String concept, String request) {
		String query = "SELECT ?x ?y WHERE { ?x ?y " + createSparqlURI(concept) + ". }";
		ClosableIterator<QueryRow> result =
				Rdf2GoCore.getInstance().sparqlSelectIt(
						query);
		loop: while (result.hasNext()) {
			QueryRow row = result.next();
			String xURI = row.getValue("x").toString();
			String x = clean(xURI.substring(xURI.indexOf("#") + 1));

			String yURI = row.getValue("y").toString();
			String y = clean(yURI.substring(yURI.indexOf("#") + 1));

			if (excludedRelation(y)) {
				continue loop;
			}
			if (excludedNode(x)) {
				continue loop;
			}

			String askClass = "ASK { " + createSparqlURI(x) + " rdf:type owl:Class}";
			boolean isClass = Rdf2GoCore.getInstance().sparqlAsk(askClass);
			String askProperty = "ASK { " + createSparqlURI(y) + " rdf:type rdf:Property}";
			boolean isProperty = Rdf2GoCore.getInstance().sparqlAsk(askProperty);
			String type = "basic";

			if (isClass) {
				if (showClasses) {
					type = "class";
				}
				else {
					continue loop;
				}
			}
			else if (isProperty) {
				if (showProperties) {
					type = "property";
				}
				else {
					continue loop;
				}
			}

			height++;
			if (height < requestedHeight) {
				addPredecessors(x, request);
			}
			if (height == requestedHeight) {
				addOutgoingEdgesPredecessors(x, request);
			}
			height--;

			addConcept(x, concept, y, request, true, type);
		}
	}

	/**
	 * Adds (gray) relations to the last (successor) nodes of the graph, showing
	 * the nodes that still follow.
	 * 
	 * @created 26.06.2012
	 * @param concept
	 * @param request
	 */
	private void addOutgoingEdgesSuccessors(String concept, String request) {
		String query = "SELECT ?y ?z WHERE { " + createSparqlURI(concept)
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
			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(z)) {
				continue;
			}
			addOuterConcept(concept, z, y, false);
		}
	}

	/**
	 * Adds (gray) relations to the last (predecessor) nodes of the graph,
	 * showing the nodes that still follow.
	 * 
	 * @created 26.06.2012
	 * @param concept
	 * @param request
	 */
	private void addOutgoingEdgesPredecessors(String concept, String request) {
		String query = "SELECT ?x ?y WHERE { ?x ?y " + createSparqlURI(concept) + "}";
		ClosableIterator<QueryRow> result =
				Rdf2GoCore.getInstance().sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			String xURI = row.getValue("x").toString();
			String x = clean(xURI.substring(xURI.indexOf("#") + 1));

			String yURI = row.getValue("y").toString();
			String y = clean(yURI.substring(yURI.indexOf("#") + 1));
			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(x)) {
				continue;
			}
			addOuterConcept(x, concept, y, true);
		}
	}

	private static String createSparqlURI(String name) {
		name = name.replaceAll(" ", "+");
		if (name.contains("+") || name.contains(".")) {
			String localNamespace = Rdf2GoCore.getInstance().getLocalNamespace();

			return "<" + localNamespace + name + ">";
		}

		try {
			return "lns:" + URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String urlDecode(String name) {
		try {
			return URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param from
	 * @param to
	 * @param relation between from --> to
	 * @param boolean predecessor (if predecessor or successor is added to the
	 *        graph)
	 * @param type (basic, class, property)
	 */
	private void addConcept(String from, String to, String relation, String request, boolean predecessor, String type) {
		String shape;
		String fontcolor;
		String fontsize;

		from = urlDecode(from);
		to = urlDecode(to);
		relation = urlDecode(relation);

		if (type.equals("class")) {
			// Class Label Attributes
			shape = "rectangle";
			fontcolor = "black";
			fontsize = "14";
		}
		else if (type.equals("property")) {
			// Property Label Attributes
			shape = "diamond";
			fontcolor = "black";
			fontsize = "14";
		}
		else {
			// Basic Label Attributes
			shape = "ellipse";
			fontcolor = "black";
			fontsize = "14";
		}

		String newLineLabelKey;
		String newLineLabelValue;
		if (predecessor) {
			newLineLabelKey = "\"" + from + "\"";
			newLineLabelValue = "[ URL=\"" + request + "?page=" + section.getTitle()
					+ "&concept=" + from + "\"" + buildLabel(shape, fontcolor, fontsize) + " ];\n";
		}
		else {
			newLineLabelKey = "\"" + to + "\"";
			newLineLabelValue = "[ URL=\"" + request + "?page=" + section.getTitle()
					+ "&concept=" + to + "\"" + buildLabel(shape, fontcolor, fontsize) + " ];\n";
		}
		String newLineRelationsKey = "\"" + from + "\"->\"" + to + "\"";
		String newLineRelationsValue = innerRelation(relation);

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
	 * @created 04.09.2012
	 * @param shape
	 * @param fontcolor
	 * @param fontsizeL
	 */
	private String buildLabel(String shape, String fontcolor, String fontsizeL) {
		return " fontcolor=\"" + fontcolor + "\" shape=\"" + shape
				+ "\" fontsize=\"" + fontsizeL + "\"";
	}

	/**
	 * 
	 * @created 04.09.2012
	 * @param arrowtail
	 * @param color
	 * @param fontsizeR
	 */
	private String buildRelation(String arrowtail, String color, String fontsize) {
		return " arrowtail=\"" + arrowtail + "\" color=\"" + color + "\" fontsize=\""
				+ fontsize + "\"";
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param from
	 * @param to
	 * @param relation between from --> to
	 * @param boolean predecessor (if predecessor or successor is added to the
	 *        graph)
	 */
	private void addOuterConcept(String from, String to, String relation, boolean predecessor) {
		// Relation Attributes
		String arrowhead = "none";
		String color = "#8b8989";
		String style = "dashed";

		from = urlDecode(from);
		to = urlDecode(to);
		relation = urlDecode(relation);

		String newLineLabelKey;
		if (predecessor) {
			newLineLabelKey = "\"" + from + "\"";
		}
		else {
			newLineLabelKey = "\"" + to + "\"";
		}
		String newLineRelationsKey = "\"" + from + "\"->\"" + to + "\"";
		String newLineRelationsValue = "[ label=\"" + relation
				+ "\" fontcolor=\"white\" arrowhead=\""
				+ arrowhead + "\" color=\"" + color
				+ "\" style=\"" + style + "\" ];\n";

		if (!dotSourceLabel.containsKey(newLineLabelKey)) {
			dotSourceLabel.put(newLineLabelKey, outerLabel);
		}
		if (!dotSourceRelations.containsKey(newLineRelationsKey)) {
			dotSourceRelations.put(newLineRelationsKey, newLineRelationsValue);
		}
	}

	/**
	 * Tests if the given node x is being excluded in the annotations.
	 * 
	 * @created 20.08.2012
	 * @param x
	 */
	private boolean excludedNode(String x) {
		if (excludedNodes != null) {
			Iterator<String> iterator = excludedNodes.iterator();
			while (iterator.hasNext()) {
				String next = iterator.next().trim();
				if (x.matches(next)) return true;
			}
		}
		return false;
	}

	/**
	 * Test if the given relation y is being excluded in the annotations.
	 * 
	 * @created 20.08.2012
	 * @param y
	 */
	private boolean excludedRelation(String y) {
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
	 * Method, that adds the target-tag to every URL in the svg-file and if
	 * requested changes the height and width of the graph.
	 * 
	 * @created 01.08.2012
	 * @param svg
	 */
	private void prepareSVG(File svg) throws FileNotFoundException, IOException {
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
	private String checkLine(String line) {
		// changes width and height if requested
		if (isValidHeightOrWidth(graphWidth) && isValidHeightOrWidth(graphHeight)) {
			if (line.matches("<svg width=.*")) {
				line = line.replaceAll("width=\"\\d+pt\"", "width=\"" + graphWidth + "\"");
				line = line.replaceAll("height=\"\\d+pt\"",
						"height=\"" + graphHeight + "\"");
			}
		}
		// adds target-tag to every URL
		if (line.matches("<a xlink:href=.*")) {
			line = line.substring(0, line.length() - 1) + " target=\"_top\">";
		}
		return line;
	}

	/**
	 * Tests if var is a valid width or height value (valid if: var is a number
	 * with 2-4 digits or a correct percentage).
	 * 
	 * @created 08.08.2012
	 * @param var
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
	 * Tests if input is a valid int for the depth/height of the graph.
	 * 
	 * @created 20.08.2012
	 * @param input
	 */
	private boolean isValidInt(String input) {
		try {
			Integer value = Integer.parseInt(input);
			// final maximum depth/height for graph
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
	 * If line is an URL it is cut and only the String behind "page=" is
	 * returned.
	 * 
	 * @created 31.07.2012
	 */
	private String clean(String line) {
		if (line.matches("http:.*/?page=.*")) {
			line = line.substring(line.indexOf("page=") + 5);
		}
		return line;
	}

}

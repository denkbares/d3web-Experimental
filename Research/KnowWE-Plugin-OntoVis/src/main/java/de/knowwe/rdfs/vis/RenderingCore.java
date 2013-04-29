/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.LinkToTermDefinitionProvider;
import de.knowwe.rdf2go.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.rdfs.util.IncrementalCompilerLinkToTermDefinitionProvider;
import de.knowwe.rdfs.vis.util.Utils;

/**
 * 
 * @author jochenreutelshofer
 * @created 29.11.2012
 */
public class RenderingCore {

	public static final String FORMAT = "format";
	public static final String CONCEPT = "concept";
	public static final String MASTER = "master";
	public static final String REQUESTED_HEIGHT = "requested_height";
	public static final String REQUESTED_DEPTH = "requested_depth";

	public static final String EXCLUDED_NODES = "excluded_nodes";
	public static final String EXCLUDED_RELATIONS = "excluded_relations";

	public static final String SHOW_CLASSES = "show_classes";
	public static final String SHOW_PROPERTIES = "show_properties";
	public static final String SHOW_SCROLLBAR = "show_scrollbar";

	public static final String GRAPH_SIZE = "graph_size";
	public static final String RANK_DIRECTION = "rank_direction";
	public static final String LINK_MODE = "LINK_MODE";
	public static final String LINK_MODE_JUMP = "LINK_MODE_JUMP";
	public static final String LINK_MODE_BROWSE = "LINK_MODE_BROWSE";

	public static final String RELATION_COLOR_CODES = "relation_color_codes";

	public static final String SHOW_OUTGOING_EDGES = "SHOW_OUTGOING_EDGES";

	private int requestedDepth = 1;
	private int requestedHeight = 1;

	private int depth;
	private int height;

	boolean showClasses;
	boolean showProperties;

	private String graphSize;

	// path of the local dot-Installation
	private static String DOT_INSTALLATION;

	// paths
	private final String tmpPath;
	private static final String TMP_FOLDER = "tmp";
	private static final String KNOWWEEXTENSION_FOLDER = "KnowWEExtension";
	private final String path;
	private static String FILE_SEPARATOR = System.getProperty("file.separator");

	// sources for the dot-file
	private String dotSource;
	private final Map<String, String> dotSourceLabel;
	private final Map<Edge, String> dotSourceRelations;

	// Annotations
	private List<String> excludedNodes;
	private List<String> excludedRelations;

	private final Section<?> section;

	Map<String, String> parameters;

	private Rdf2GoCore rdfRepository = null;

	private LinkToTermDefinitionProvider uriProvider = null;

	private String master = null;

	// appearances
	private final String outerLabel = "[ shape=\"none\" fontsize=\"0\" fontcolor=\"white\" ];\n";

	/**
	 * Allows to create a new Rendering Core. For each rendering task a new one
	 * should be created.
	 * 
	 * @param realPath webapp path
	 * @param section a section that the graph is rendered for/at
	 * @param parameters the configuration, consider the constants of this class
	 */
	public RenderingCore(String realPath, Section<?> section, Map<String, String> parameters) {

		String master = parameters.get(MASTER);
		if (master != null) {
			rdfRepository = Rdf2GoCore.getInstance(Environment.DEFAULT_WEB, master);
			this.master = master;
			uriProvider = new PackageCompileLinkToTermDefinitionProvider();
		}
		else {
			rdfRepository = Rdf2GoCore.getInstance();
			uriProvider = new IncrementalCompilerLinkToTermDefinitionProvider();
		}

		String requestedHeightString = parameters.get(REQUESTED_HEIGHT);
		if (requestedHeightString != null) {
			try {
				this.requestedHeight = Integer.parseInt(requestedHeightString);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		String requestedDepthString = parameters.get(REQUESTED_DEPTH);
		if (requestedDepthString != null) {
			try {
				this.requestedDepth = Integer.parseInt(requestedDepthString);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.parameters = parameters;
		this.section = section;
		ResourceBundle rb = ResourceBundle.getBundle("dotInstallation");
		DOT_INSTALLATION = rb.getString("path");
		tmpPath = KNOWWEEXTENSION_FOLDER + FILE_SEPARATOR + TMP_FOLDER + FILE_SEPARATOR;
		String graphtitle = "Konzeptuebersicht"; // + parameters.get(CONCEPT);
		dotSource = "digraph " + graphtitle + " {\n";

		path = realPath + FILE_SEPARATOR + tmpPath;

		dotSourceLabel = new LinkedHashMap<String, String>();
		dotSourceRelations = new LinkedHashMap<Edge, String>();
	}

	/**
	 * Starts the actual rendering process. Generates doc file and images files.
	 * Adds corresponding html source to the passed StringBuilder.
	 * 
	 * @created 29.11.2012
	 * @param builder html source showing the generated images is added to this
	 *        builder
	 */
	public void render(RenderResult builder) {

		getAnnotations();

		setSizeAndRankDir();

		buildSources();
		try {
			writeFiles(section);
		}
		catch (FileNotFoundException e) {
			// TODO: render proper message
			builder.append("Warning:" + e.toString());
		}
		catch (IOException e) {
			// TODO: render proper message
			builder.append("Warning:" + e.toString());
		}

		// actually render HTML-content
		createHTMLOutput(builder);
	}

	/**
	 * Adds all requested concepts and information to the dotSources (the maps).
	 * 
	 * @created 20.08.2012
	 * @param concept
	 * @param request
	 */
	private void buildSources() {

		String concept = parameters.get(CONCEPT);
		String conceptNameEncoded = null;
		try {
			conceptNameEncoded = URLEncoder.encode(concept, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URI conceptURI = new URIImpl(rdfRepository.getLocalNamespace() + conceptNameEncoded);

		// if requested, the predecessor are added to the source
		if (requestedHeight > 0) {
			height = 0;
			addPredecessors(conceptURI);
		}
		insertMainConcept(conceptURI);
		// if requested, the successors are added to the source
		if (requestedDepth > 0) {
			depth = 0;
			addSuccessors(conceptURI);
		}
		// if the appearance of the main concept was changed during the process,
		// it is reset
		insertMainConcept(conceptURI);
		// check all relations again
		checkRelations();
		connectSources();
	}

	/**
	 * 
	 * @created 03.09.2012
	 * @param StringBuilder
	 */
	private void createHTMLOutput(RenderResult string) {
		String style = "max-height:1000px; ";
		if (parameters.get(SHOW_SCROLLBAR) != null
				&& parameters.get(SHOW_SCROLLBAR).equals("false")) {
			// no scroll-bars
		}
		else {
			style += "overflow:scroll";
		}
		String div_open = "<div style=\"" + style + "\">";
		String div_close = "</div>";
		String png_default = div_open + "<img alt='graph' src='"
				+ tmpPath + "graph" + section.getID() + ".png'>" + div_close;
		String svg = div_open + "<object data='" + tmpPath
				+ "graph" + section.getID() + ".svg' type=\"image/svg+xml\">" + png_default
				+ "</object>" + div_close;
		String format = parameters.get(FORMAT);
		if (format == null) {
			string.appendHtml(png_default);
		}
		else if (format.equals("svg")) {
			string.appendHtml(svg);
		}
		else {
			string.appendHtml(png_default);
		}
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
		// adds target-tag to every URL
		if (line.matches("<a xlink:href=.*")) {
			line = line.substring(0, line.length() - 1) + " target=\"_top\">";
		}
		return line;
	}

	/**
	 * The dot, svg and png files are created and written.
	 * 
	 * @created 20.08.2012
	 */
	public void writeFiles(Section<?> section) throws FileNotFoundException, IOException {
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
		String command = DOT_INSTALLATION + " " + dot.getAbsolutePath() +
				" -Tsvg -o " + svg.getAbsolutePath() + "";
		createFileOutOfDot(svg, dot, command);

		// create png
		command = DOT_INSTALLATION + " " + dot.getAbsolutePath() +
				" -Tpng -o " + png.getAbsolutePath() + "";
		createFileOutOfDot(png, dot, command);
		prepareSVG(svg);
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
	 * @created 20.08.2012
	 * @param user
	 */
	private void getAnnotations() {
		getSuccessors();
		getPredecessors();
		graphSize = parameters.get(GRAPH_SIZE);

		getExcludedNodes();
		getExcludedRelations();

		getShowAnnotations();
	}

	/**
	 * 
	 * 
	 * @created 30.10.2012
	 */
	private void setSizeAndRankDir() {
		String rankDir = "TB";
		String rankDirSetting = parameters.get(RANK_DIRECTION);
		if (rankDirSetting != null) {
			rankDir = rankDirSetting;
		}
		if (graphSize != null) {
			if (graphSize.matches("\\d+px")) {
				graphSize = graphSize.substring(0, graphSize.length() - 2);
				dotSource += "graph [ rankdir=\"" + rankDir + "\"]\n";
			}
			if (graphSize.matches("\\d+")) {
				dotSource += "graph [size=\""
						+ String.valueOf(Double.valueOf(graphSize) * 0.010415597) + "!\""
						+ " rankdir=\"" + rankDir + "\"]\n";
			}
		}

	}

	/**
	 * 
	 * @created 18.08.2012
	 */
	private void getPredecessors() {
		if (!isValidInt(parameters.get(REQUESTED_HEIGHT))) {
			requestedHeight = 1;
		}
		else {
			requestedHeight = Integer.parseInt(parameters.get(REQUESTED_HEIGHT));
		}
	}

	/**
	 * 
	 * @created 18.08.2012
	 */
	private void getSuccessors() {
		if (!isValidInt(parameters.get(REQUESTED_DEPTH))) {
			requestedDepth = 1;
		}
		else {
			requestedDepth = Integer.parseInt(parameters.get(REQUESTED_DEPTH));
		}
	}

	/**
	 * 
	 * @created 20.08.2012
	 */
	private void getExcludedRelations() {
		excludedRelations = new ArrayList<String>();
		String exclude = parameters.get(EXCLUDED_RELATIONS);
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
		String exclude = parameters.get(EXCLUDED_NODES);
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

		String classes = parameters.get(SHOW_CLASSES);
		if (classes == null) {
			showClasses = true;
		}
		else if (classes.equals("false")) {
			showClasses = false;
		}
		else {
			showClasses = true;
		}
		String properties = parameters.get(SHOW_PROPERTIES);
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
	 * 
	 * @created 18.08.2012
	 * @param concept
	 * @param request
	 */
	private void insertMainConcept(URI conceptURI) {
		String concept = getConceptName(conceptURI);
		// Main Concept Attributes
		String style = "filled";
		String fillcolor = "yellow";
		String fontsize = "14";
		String shape = "ellipse";

		// String askClass = "ASK { " + uriProvider.createSparqlURI(concept,
		// rdfRepository, master)
		// + " rdf:type owl:Class}";
		String askClass = "ASK { <" + conceptURI.toString()
				+ "> rdf:type owl:Class}";
		boolean isClass = rdfRepository.sparqlAsk(askClass);
		// String askProperty = "ASK { " + uriProvider.createSparqlURI(concept,
		// rdfRepository, master)
		// + " rdf:type owl:Property}";
		String askProperty = "ASK { <" + conceptURI.toString()
				+ "> rdf:type owl:Property}";
		boolean isProperty = rdfRepository.sparqlAsk(askProperty);

		if (isClass) {
			shape = "rectangle";
		}
		else if (isProperty) {
			shape = "diamond";
		}
		String url = createBaseURL();
		String conceptLabel = Utils.getRDFSLabel(conceptURI.asURI(), rdfRepository);
		if (conceptLabel == null) {
			conceptLabel = concept;
		}

		String conceptKey = "\"" + concept + "\"";
		String conceptValue = "[ URL=\"" + url + "?page=" + section.getTitle() + "&concept="
				+ concept + "\" style=\"" + style + "\" fillcolor=\"" + fillcolor
				+ "\" fontsize=\"" + fontsize + "\" shape=\"" + shape + "\"" + "label=\""
				+ Utils.prepareLabel(conceptLabel) + "\"];\n";
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
		List<Edge> keysWithGrayRelation = new LinkedList<Edge>();
		Iterator<Edge> keys = dotSourceRelations.keySet().iterator();
		while (keys.hasNext()) {
			Edge key = keys.next();
			if (dotSourceRelations.get(key).contains("fontcolor=\"white\"")) {
				keysWithGrayRelation.add(key);
			}
		}

		// Second step: Now check both concepts of those relations and see, if
		// they are both normal (inner) nodes. If they are, the relation
		// shouldnt be gray, so it will be changed.
		Iterator<Edge> relations = keysWithGrayRelation.iterator();
		while (relations.hasNext()) {
			Edge relation = relations.next();
			String[] concepts = new String[] {
					relation.getSubject(), relation.getObject() };

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
		String fontsize = "13";

		String color = getRelationColorCode(label);

		return "[ label = \"" + label
				+ "\"" + buildRelation(arrowtail, color, fontsize) + " ];\n";
	}

	/**
	 * 
	 * @created 07.12.2012
	 * @param label
	 * @return
	 */
	private String getRelationColorCode(String label) {
		if (parameters.get(RELATION_COLOR_CODES) != null) {
			String codeList = parameters.get(RELATION_COLOR_CODES);
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
		Iterator<Edge> relationsKeys = dotSourceRelations.keySet().iterator();
		while (relationsKeys.hasNext()) {
			Edge key = relationsKeys.next();
			dotSource += "\"" + key.getSubject() + "\"" + " -> " + "\"" + key.getObject() + "\" "
					+ dotSourceRelations.get(key);
		}

		dotSource += "}";
	}

	/**
	 * Method, that recursively adds all successors of the requested concept up
	 * to the chosen depth.
	 * 
	 * @created 26.06.2012
	 * @param concept
	 * @param request
	 */
	private void addSuccessors(Node conceptURI) {
		String concept = getConceptName(conceptURI);
		// String query = "SELECT ?y ?z WHERE { "
		// + uriProvider.createSparqlURI(concept, rdfRepository, master)
		// + " ?y ?z.}";
		String query = "SELECT ?y ?z WHERE { <"
				+ conceptURI.asURI().toString()
				+ "> ?y ?z.}";
		ClosableIterator<QueryRow> result =
				rdfRepository.sparqlSelectIt(
						query);
		loop: while (result.hasNext()) {
			QueryRow row = result.next();
			Node yURI = row.getValue("y");
			String y = getConceptName(yURI);

			Node zURI = row.getValue("z");
			String z = getConceptName(zURI);

			if (excludedRelation(y)) {
				continue loop;
			}
			if (excludedNode(z)) {
				continue loop;
			}
			if (isLiteral(zURI)) {
				continue loop;
			}
			String askClass = "ASK { <" + zURI.asURI().toString()
					+ "> rdf:type owl:Class}";
			boolean isClass = rdfRepository.sparqlAsk(askClass);
			String askProperty = "ASK { <" + zURI.asURI().toString() + "> rdf:type rdf:Property}";
			boolean isProperty = rdfRepository.sparqlAsk(askProperty);
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
			addConcept(conceptURI, zURI, yURI, false, type);

			depth++;
			if (depth < requestedDepth) {
				addSuccessors(zURI);
			}
			if (depth == requestedDepth) {
				addOutgoingEdgesSuccessors(zURI);
			}
			depth--;
		}
	}

	/**
	 * 
	 * @created 24.04.2013
	 * @param zURI
	 * @return
	 */
	private boolean isLiteral(Node zURI) {
		try {
			zURI.asLiteral();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @created 03.08.2012
	 * @param concept
	 * @param request
	 */
	private void addPredecessors(Node conceptURI) {
		String concept = getConceptName(conceptURI);
		// String query = "SELECT ?x ?y WHERE { ?x ?y "
		// + uriProvider.createSparqlURI(concept, rdfRepository, master) +
		// ". }";
		String query = "SELECT ?x ?y WHERE { ?x ?y <"
				+ conceptURI.asURI().toString() + "> . }";
		ClosableIterator<QueryRow> result =
				rdfRepository.sparqlSelectIt(
						query);
		loop: while (result.hasNext()) {
			QueryRow row = result.next();
			Node xURI = row.getValue("x");
			String x = getConceptName(xURI);

			Node yURI = row.getValue("y");
			String y = getConceptName(yURI);

			if (excludedRelation(y)) {
				continue loop;
			}
			if (excludedNode(x)) {
				continue loop;
			}

			String askClass = "ASK { <" + xURI.toString() + "> rdf:type owl:Class}";
			boolean isClass = rdfRepository.sparqlAsk(askClass);
			String askProperty = "ASK { <" + xURI.toString() + "> rdf:type rdf:Property}";
			boolean isProperty = rdfRepository.sparqlAsk(askProperty);
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
				addPredecessors(xURI);
			}
			if (height == requestedHeight) {

				addOutgoingEdgesPredecessors(xURI);
			}
			height--;

			addConcept(xURI, conceptURI, yURI, true, type);
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
	private void addOutgoingEdgesSuccessors(Node conceptURI) {
		if (isLiteral(conceptURI)) return;
		String concept = getConceptName(conceptURI);
		try {
			concept = URLDecoder.decode(concept, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String query = "SELECT ?y ?z WHERE { "
		// + uriProvider.createSparqlURI(concept, rdfRepository, master)
		// + " ?y ?z.}";
		String query = "SELECT ?y ?z WHERE { <"
				+ conceptURI.asURI().toString()
				+ "> ?y ?z.}";
		ClosableIterator<QueryRow> result =
				rdfRepository.sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			Node yURI = row.getValue("y");
			String y = getConceptName(yURI);

			Node zURI = row.getValue("z");
			String z = getConceptName(zURI);
			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(z)) {
				continue;
			}
			addOuterConcept(conceptURI, zURI, yURI, false);
		}
	}

	private static String getConceptName(Node uri) {
		try {
			uri.asURI();
			return urlDecode(clean(uri.asURI().toString().substring(uri.toString().indexOf("#") + 1)));

		}
		catch (ClassCastException e) {
			return null;
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
	private void addOutgoingEdgesPredecessors(Node conceptURI) {
		if (isLiteral(conceptURI)) return;
		String concept = getConceptName(conceptURI);
		String conceptDecoded = urlDecode(concept);
		// String query = "SELECT ?x ?y WHERE { ?x ?y "
		// + uriProvider.createSparqlURI(conceptDecoded, rdfRepository, master)
		// + "}";

		String query = "SELECT ?x ?y WHERE { ?x ?y <"
				+ conceptURI.asURI().toString()
				+ ">}";
		ClosableIterator<QueryRow> result =
				rdfRepository.sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			Node xURI = row.getValue("x");
			String x = getConceptName(xURI);

			Node yURI = row.getValue("y");
			String y = getConceptName(yURI);

			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(x)) {
				continue;
			}
			addOuterConcept(xURI, conceptURI, yURI, true);
		}
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
	private void addConcept(Node fromURI, Node toURI, Node relationURI, boolean predecessor, String type) {
		String from = getConceptName(fromURI);
		String to = getConceptName(toURI);
		String relation = getConceptName(relationURI);

		String shape;
		String fontcolor;
		String fontsize;

		from = urlDecode(from);
		to = urlDecode(to);
		relation = urlDecode(relation);

		if (type.equals("class")) {
			// Class Label Attributes
			shape = "box";
			fontcolor = "black";
			fontsize = "14";
		}
		else if (type.equals("property")) {
			// Property Label Attributes
			shape = "septagon";
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
			String sourceURL = createConceptURL(from);
			String sourceLabel = Utils.getRDFSLabel(fromURI.asURI(), rdfRepository);
			if (sourceLabel == null) {
				sourceLabel = from;
			}

			newLineLabelKey = "\"" + from + "\"";
			newLineLabelValue = "[ URL=\"" + sourceURL + "\""
					+ buildLabel(shape, fontcolor, fontsize)
					+ "label=\"" + Utils.prepareLabel(sourceLabel) + "\" ];\n";
		}
		else {
			String targetURL = createConceptURL(to);
			String targetLabel = Utils.getRDFSLabel(toURI.asURI(), rdfRepository);
			if (targetLabel == null) {
				targetLabel = to;
			}
			newLineLabelKey = "\"" + to + "\"";
			newLineLabelValue = "[ URL=\"" + targetURL + "\""
					+ buildLabel(shape, fontcolor, fontsize) + "label=\""
					+ Utils.prepareLabel(targetLabel) + "\" ];\n";
		}
		Edge newLineRelationsKey = new Edge(from, relation, to);
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

	private String createConceptURL(String to) {
		if (parameters.get(LINK_MODE) != null) {
			if (parameters.get(LINK_MODE).equals(LINK_MODE_BROWSE)) {
				return uriProvider.getLinkToTermDefinition(to, master);
			}
		}
		return createBaseURL() + "?page=" + section.getTitle()
				+ "&concept=" + to;
	}

	/**
	 * 
	 * @created 29.11.2012
	 * @return
	 */
	private String createBaseURL() {
		return Environment.getInstance().getWikiConnector().getBaseUrl() + "Wiki.jsp";
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
	private void addOuterConcept(Node fromURI, Node toURI, Node relationURI, boolean predecessor) {
		String from = getConceptName(fromURI);
		String to = getConceptName(toURI);
		String relation = getConceptName(relationURI);

		// TODO: implement rendering of literal nodes
		if (to == null) {
			return;
		}

		// Relation Attributes
		String arrowhead = "none";
		String color = "#8b8989";
		String style = "dashed";

		String newLineLabelKey;
		if (predecessor) {
			newLineLabelKey = "\"" + from + "\"";
		}
		else {
			newLineLabelKey = "\"" + to + "\"";
		}
		Edge newLineRelationsKey = new Edge(from, relation, to);
		String newLineRelationsValue = "[ label=\"" + relation
				+ "\" fontcolor=\"white\" arrowhead=\""
				+ arrowhead + "\" color=\"" + color
				+ "\" style=\"" + style + "\" ];\n";

		boolean arcIsNew = !dotSourceRelations.containsKey(newLineRelationsKey);
		boolean nodeIsNew = !dotSourceLabel.containsKey(newLineLabelKey);

		boolean showOutgoingEdges = true;
		if (parameters.get(SHOW_OUTGOING_EDGES) != null) {
			if (parameters.get(SHOW_OUTGOING_EDGES).equals("false")) {
				showOutgoingEdges = false;
			}
		}

		if (showOutgoingEdges) {
			if (nodeIsNew) {
				dotSourceLabel.put(newLineLabelKey, outerLabel);
			}
			if (arcIsNew) {
				dotSourceRelations.put(newLineRelationsKey, newLineRelationsValue);
			}
		}
		else {
			// do not show outgoing edges
			if (!nodeIsNew) {
				// but show if its node is internal one already
				dotSourceRelations.put(newLineRelationsKey, newLineRelationsValue);
			}
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
	private static String clean(String line) {
		if (line.matches("http:.*/?page=.*")) {
			line = line.substring(line.indexOf("page=") + 5);
		}
		return line;
	}

}

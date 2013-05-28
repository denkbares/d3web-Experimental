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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import de.knowwe.rdfs.vis.dot.DOTVisualizationRenderer;
import de.knowwe.rdfs.vis.util.Utils;

/**
 * 
 * @author jochenreutelshofer
 * @created 29.11.2012
 */
public class RenderingCore {

	public enum NODE_TYPE {
		CLAAS, PROPERTY, INSTANCE, UNDEFINED
	};

	public static final String FORMAT = "format";
	public static final String CONCEPT = "concept";
	public static final String MASTER = "master";
	public static final String LANGUAGE = "language";
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

	public static final String DOT_APP = "dot_app";
	public static final String ADD_TO_DOT = "add_to_dot";
	public static final String TITLE = "title";
	public static final String SECTION_ID = "section-id";
	public static final String REAL_PATH = "realpath";

	public static final String RELATION_COLOR_CODES = "relation_color_codes";

	public static final String SHOW_OUTGOING_EDGES = "SHOW_OUTGOING_EDGES";

	private int requestedDepth = 1;
	private int requestedHeight = 1;

	private int depth;
	private int height;

	boolean showClasses;
	boolean showProperties;
	boolean showOutgoingEdges = true;

	// stores the actual subset of the rdf-graph to rendered
	private final SubGraphData data;

	// concept and relation names which are black-listed
	private List<String> excludedNodes;
	private List<String> excludedRelations;

	private final Section<?> section;

	private final Map<String, String> parameters;

	private Rdf2GoCore rdfRepository = null;

	private LinkToTermDefinitionProvider uriProvider = null;

	private GraphVisualizationRenderer sourceRenderer = null;

	/**
	 * Allows to create a new Rendering Core. For each rendering task a new one
	 * should be created.
	 * 
	 * @param realPath webapp path
	 * @param section a section that the graph is rendered for/at
	 * @param parameters the configuration, consider the constants of this class
	 */
	public RenderingCore(String realPath, Section<?> section, Map<String, String> parameters, LinkToTermDefinitionProvider uriProvider, Rdf2GoCore rdfRepository) {

		this.rdfRepository = rdfRepository;
		this.uriProvider = uriProvider;

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

		if (parameters.get(SHOW_OUTGOING_EDGES) != null) {
			if (parameters.get(SHOW_OUTGOING_EDGES).equals("false")) {
				showOutgoingEdges = false;
			}
		}

		this.parameters = parameters;
		this.section = section;

		parameters.put(REAL_PATH, realPath);
		parameters.put(TITLE, getSectionTitle());
		parameters.put(SECTION_ID, getSectionID(section));

		data = new SubGraphData();

		// current default source renderer is DOT
		sourceRenderer = new DOTVisualizationRenderer(data, parameters);

		// set config values
		setConfigurationParameters();
	}

	private String getSectionTitle() {
		if (section != null) {
			return section.getTitle();
		}

		return "ForTestingOnly";
	}

	public static String getSectionID(Section<?> section) {
		if (section != null) {
			return section.getID();
		}

		return "ForTestingOnly";
	}

	public void createData() {

		// select the relevant sub-graph from the overall rdf-graph
		selectGraphData();

		// create the source representation using the configured source-renderer
		this.sourceRenderer.generateSource();

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
		createData();

		builder.appendHtml(sourceRenderer.getHTMLIncludeSnipplet());

	}

	/**
	 * Adds all requested concepts and information to the dotSources (the maps).
	 * 
	 * @created 20.08.2012
	 * @param concept
	 * @param request
	 */
	private void selectGraphData() {

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

	}

	public String getSource() {
		return this.sourceRenderer.getSource();
	}

	/**
	 * 
	 * @created 20.08.2012
	 * @param user
	 */
	private void setConfigurationParameters() {
		getSuccessors();
		getPredecessors();

		getExcludedNodes();
		getExcludedRelations();

		getShowAnnotations();
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
		if (classes != null && classes.equals("false")) {
			showClasses = false;
		}
		else {
			showClasses = true;
		}

		String properties = parameters.get(SHOW_PROPERTIES);
		if (properties != null && properties.equals("false")) {
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

		String conceptLabel = Utils.getRDFSLabel(conceptURI.asURI(), rdfRepository,
				parameters.get(LANGUAGE));
		if (conceptLabel == null) {
			conceptLabel = concept;
		}
		// the main concept is inserted
		ConceptNode conceptNode = new ConceptNode(concept,
				getConceptType(conceptURI),
				conceptURI.toString(), conceptLabel);
		conceptNode.setRoot(true);
		data.addConcept(conceptNode);
	}

	private NODE_TYPE getConceptType(URI conceptURI) {
		NODE_TYPE result = NODE_TYPE.UNDEFINED;

		String askClass = "ASK { <" + conceptURI.toString()
				+ "> rdf:type owl:Class}";
		if (rdfRepository.sparqlAsk(askClass)) return NODE_TYPE.CLAAS;

		String askProperty = "ASK { <" + conceptURI.toString()
				+ "> rdf:type owl:Property}";
		if (rdfRepository.sparqlAsk(askProperty)) return NODE_TYPE.PROPERTY;

		return result;
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

			if (y == null) {
				Logger.getLogger(RenderingCore.class.getName()).log(Level.SEVERE,
						"Variable y of query was null: " + query);
				continue;
			}

			if (z == null) {
				Logger.getLogger(RenderingCore.class.getName()).log(Level.SEVERE,
						"Variable z of query was null: " + query);
				continue;
			}

			if (excludedRelation(y)) {
				continue loop;
			}
			if (excludedNode(z)) {
				continue loop;
			}
			if (isLiteral(zURI)) {
				continue loop;
			}
			NODE_TYPE nodeType = getConceptType(zURI.asURI());
			if (nodeType == NODE_TYPE.CLAAS && !showClasses) {
				continue loop;
			}
			else if (nodeType == NODE_TYPE.PROPERTY && !showProperties) {
				continue loop;
			}
			addConcept(conceptURI, zURI, yURI, false, nodeType);

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
		String query = "SELECT ?x ?y WHERE { ?x ?y <"
				+ conceptURI.asURI().toString() + "> . }";
		ClosableIterator<QueryRow> result =
				rdfRepository.sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			Node xURI = row.getValue("x");
			String x = getConceptName(xURI);

			Node yURI = row.getValue("y");
			String y = getConceptName(yURI);

			if (y == null) {
				Logger.getLogger(RenderingCore.class.getName()).log(Level.SEVERE,
						"Variable y of query was null: " + query);
				continue;
			}

			if (x == null) {
				Logger.getLogger(RenderingCore.class.getName()).log(Level.SEVERE,
						"Variable x of query was null: " + query);
				continue;
			}

			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(x)) {
				continue;
			}

			NODE_TYPE nodeType = getConceptType(xURI.asURI());

			if (nodeType == NODE_TYPE.CLAAS && !showClasses) {
				continue;
			}
			else if (nodeType == NODE_TYPE.PROPERTY && !showProperties) {
				continue;
			}

			height++;
			if (height < requestedHeight) {
				addPredecessors(xURI);
			}
			if (height == requestedHeight) {

				addOutgoingEdgesPredecessors(xURI);
			}
			height--;

			addConcept(xURI, conceptURI, yURI, true, nodeType);
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
			e.printStackTrace();
		}

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

			if (y == null) {
				Logger.getLogger(RenderingCore.class.getName()).log(Level.SEVERE,
						"Variable y of query was null: " + query);
				continue;
			}

			if (z == null) {
				Logger.getLogger(RenderingCore.class.getName()).log(Level.SEVERE,
						"Variable zs of query was null: " + query);
				continue;
			}

			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(z)) {
				continue;
			}
			addOuterConcept(conceptURI, zURI, yURI, false);
		}
	}

	public static String getConceptName(Node uri) {
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

			if (y == null) {
				Logger.getLogger(RenderingCore.class.getName()).log(Level.SEVERE,
						"Variable y of query was null: " + query);
				continue;
			}

			if (x == null) {
				Logger.getLogger(RenderingCore.class.getName()).log(Level.SEVERE,
						"Variable x of query was null: " + query);
				continue;
			}

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
	 * @param type (instance, class, property, undefined)
	 */
	private void addConcept(Node fromURI, Node toURI, Node relationURI, boolean predecessor, NODE_TYPE type) {
		String from = getConceptName(fromURI);
		String to = getConceptName(toURI);
		String relation = getConceptName(relationURI);

		ConceptNode conceptNode = null;
		String currentConcept = null;
		Node currentURI = null;
		if (predecessor) {
			currentConcept = from;
			currentURI = fromURI;
		}
		else {
			currentConcept = to;
			currentURI = toURI;
		}
		String currentURL = createConceptURL(currentConcept);
		String currentLabel = Utils.getRDFSLabel(currentURI.asURI(), rdfRepository,
				parameters.get(LANGUAGE));
		if (currentLabel == null) {
			currentLabel = currentConcept;
		}
		conceptNode = new ConceptNode(currentConcept, type, currentURL, currentLabel);

		Edge newLineRelationsKey = new Edge(from, relation, to);

		conceptNode.setOuter(false);
		data.addConcept(conceptNode);
		data.addEdge(newLineRelationsKey);

	}

	private String createConceptURL(String to) {
		if (parameters.get(LINK_MODE) != null) {
			if (parameters.get(LINK_MODE).equals(LINK_MODE_BROWSE)) {
				return uriProvider.getLinkToTermDefinition(to, parameters.get(MASTER));
			}
		}
		return createBaseURL() + "?page=" + getSectionTitle()
				+ "&concept=" + to;
	}

	/**
	 * 
	 * @created 29.11.2012
	 * @return
	 */
	public static String createBaseURL() {
		if (Environment.getInstance() != null
				&& Environment.getInstance().getWikiConnector() != null) {
			return Environment.getInstance().getWikiConnector().getBaseUrl() + "Wiki.jsp";
		}
		else {
			// for tests only
			return "http://localhost:8080/KnowWE/Wiki.jsp";
		}
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

		String currentConcept = null;
		if (predecessor) {
			currentConcept = from;
		}
		else {
			currentConcept = to;
		}

		Edge edge = new Edge(from, relation, to);
		boolean edgeIsNew = !data.getEdges().contains(edge);
		boolean nodeIsNew = !data.getConceptDeclaration().contains(new ConceptNode(
				currentConcept));

		if (showOutgoingEdges) {
			if (nodeIsNew) {
				ConceptNode node = new ConceptNode(currentConcept);
				node.setOuter(true);
				data.addConcept(node);
			}
			if (edgeIsNew) {
				edge.setOuter(true);
				data.addEdge(edge);
			}
		}
		else {
			// do not show outgoing edges
			if (!nodeIsNew) {
				// but show if its node is internal one already
				edge.setOuter(false);
				data.addEdge(edge);
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

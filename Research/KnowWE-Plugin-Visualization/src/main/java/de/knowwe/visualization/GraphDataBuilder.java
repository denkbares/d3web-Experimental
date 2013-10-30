/*
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.visualization;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.d3web.strings.Identifier;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.visualization.d3.D3VisualizationRenderer;
import de.knowwe.visualization.dot.DOTVisualizationRenderer;

/**
 * @author Johanna Latt
 * @created 11.10.2013
 * @param <T> The type of data the graph is supposed to visualize
 */
public abstract class GraphDataBuilder<T extends Object> {

	public enum NODE_TYPE {
		CLASS, PROPERTY, INSTANCE, UNDEFINED, LITERAL
	};

	public enum Renderer {
		dot, d3
	};

	public static final String RENDERER = "renderer";

	public static final String VISUALIZATION = "visualization";

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

	public int requestedDepth = 1;
	public int requestedHeight = 1;

	private boolean showClasses;
	private boolean showProperties;
	private boolean showOutgoingEdges = true;

	// stores the actual subset of the rdf-graph to rendered
	public SubGraphData data;

	// concept and relation names which are black-listed
	private List<String> excludedNodes;
	private List<String> excludedRelations;

	private Section<?> section;

	private Map<String, String> parameters;

	private LinkToTermDefinitionProvider uriProvider = null;

	private GraphVisualizationRenderer sourceRenderer = null;
	
	public Map<String, String> getParameterMap() {
		return parameters;
	}
	
	public boolean showProperties() {
		return showProperties;
	}
	
	public boolean showOutgoingEdges() {
		return showOutgoingEdges;
	}
	
	public boolean showClasses() {
		return showClasses;
	}

	public void initialiseData(String realPath, Section<?> section, Map<String, String> parameters, LinkToTermDefinitionProvider uriProvider) {
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
		parameters.put(TITLE, getSectionTitle(section));
		parameters.put(SECTION_ID, getSectionID(section));

		data = new SubGraphData();

		// current default source renderer is DOT
		String renderer = parameters.get(GraphDataBuilder.RENDERER);
		if (renderer != null && renderer.equals(Renderer.d3.name())) {
			sourceRenderer = new D3VisualizationRenderer(data, parameters);
		}
		else sourceRenderer = new DOTVisualizationRenderer(data, parameters);

		// set config values
		setConfigurationParameters();
	}

	public static String getSectionTitle(Section<?> section) {
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
	public abstract void selectGraphData();
	
	public String getEncodedConceptName() {
		String concept = parameters.get(CONCEPT);
		String conceptNameEncoded = null;
		try {
			conceptNameEncoded = URLEncoder.encode(concept, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return conceptNameEncoded;
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
		if (isValidInt(parameters.get(REQUESTED_HEIGHT))) {
			requestedHeight = Integer.parseInt(parameters.get(REQUESTED_HEIGHT));
		}
	}

	/**
	 * 
	 * @created 18.08.2012
	 */
	private void getSuccessors() {
		if (isValidInt(parameters.get(REQUESTED_DEPTH))) {
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
	public abstract void insertMainConcept(T concept);

	/**
	 * Method, that recursively adds all successors of the requested concept up
	 * to the chosen depth.
	 * 
	 * @created 26.06.2012
	 * @param concept
	 * @param request
	 */
	public abstract void addSuccessors(T concept);

	/**
	 * 
	 * @created 03.08.2012
	 * @param concept
	 * @param request
	 */
	public abstract void addPredecessors(T concept);

	/**
	 * Adds (gray) relations to the last (successor) nodes of the graph, showing
	 * the nodes that still follow.
	 * 
	 * @created 26.06.2012
	 * @param concept
	 * @param request
	 */
	public abstract void addOutgoingEdgesSuccessors(T concept);

	/**
	 * Adds (gray) relations to the last (predecessor) nodes of the graph,
	 * showing the nodes that still follow.
	 * 
	 * @created 26.06.2012
	 * @param concept
	 * @param request
	 */
	public abstract void addOutgoingEdgesPredecessors(T concept);

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
	public abstract void addConcept(T from, T to, T relation, NODE_TYPE type);

	/**
	 * 
	 * @created 20.08.2012
	 * @param from
	 * @param to
	 * @param relation between from --> to
	 * @param boolean predecessor (if predecessor or successor is added to the
	 *        graph)
	 */
	public abstract void addOuterConcept(T from, T to, T relation, boolean predecessor);

	public static String urlDecode(String name) {
		try {
			return URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String createConceptURL(String to) {
		if (parameters.get(LINK_MODE) != null) {
			if (parameters.get(LINK_MODE).equals(LINK_MODE_BROWSE)) {
				return uriProvider.getLinkToTermDefinition(new Identifier(to),
						parameters.get(MASTER));
			}
		}
		return createBaseURL() + "?page=" + getSectionTitle(section)
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
	 * Tests if the given node x is being excluded in the annotations.
	 * 
	 * @created 20.08.2012
	 * @param x
	 */
	public boolean excludedNode(String x) {
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
	public boolean excludedRelation(String y) {
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

}

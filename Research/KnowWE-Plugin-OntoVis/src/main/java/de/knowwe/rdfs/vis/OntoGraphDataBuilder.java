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
package de.knowwe.rdfs.vis;

import de.d3web.strings.Strings;
import de.d3web.utils.Log;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.rdfs.vis.util.Utils;
import de.knowwe.visualization.ConceptNode;
import de.knowwe.visualization.Edge;
import de.knowwe.visualization.GraphDataBuilder;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Johanna Latt
 * @created 11.10.2013
 */
public class OntoGraphDataBuilder extends GraphDataBuilder<Node> {

	private Rdf2GoCore rdfRepository = null;

	private int depth = 0;
	private int height = 0;

	private Set<Node> expandedPredecessors = new HashSet<Node>();
	private Set<Node> expandedSuccessors = new HashSet<Node>();

    private List<String> queries = new ArrayList<String>();
    private int asks = 0;

    private String propertyFilterExpression = null;
    private String nodeFilterExpression = null;

	/**
	 * Allows to create a new Ontology Rendering Core. For each rendering task a new one should be created.
	 *
	 * @param realPath   webapp path
	 * @param section    a section that the graph is rendered for/at
	 * @param parameters the configuration, consider the constants of this class
	 */
	public OntoGraphDataBuilder(String realPath, Section<?> section, Map<String, String> parameters, LinkToTermDefinitionProvider uriProvider, Rdf2GoCore rdfRepository) {
		if (rdfRepository == null) {
			throw new NullPointerException("The RDF repository can't be null!");
		}
		this.rdfRepository = rdfRepository;
		initialiseData(realPath, section, parameters, uriProvider);
	}

	public String getConceptName(Node uri) {
		return Utils.getConceptName(uri, this.rdfRepository);
	}

	private NODE_TYPE getConceptType(Node conceptURI) {
		if (isLiteral(conceptURI)) {
			return NODE_TYPE.LITERAL;
		}
		if (isBlankNode(conceptURI)) {
			return NODE_TYPE.BLANKNODE;
		}

		NODE_TYPE result = NODE_TYPE.UNDEFINED;
        asks++;

        if (Rdf2GoUtils.isClass(rdfRepository, conceptURI.asURI())) return NODE_TYPE.CLASS;

        asks++;
        if (Rdf2GoUtils.isProperty(rdfRepository, conceptURI.asURI())) return NODE_TYPE.PROPERTY;

		return result;
	}

	/**
	 * @param zURI the original node
	 * @return literal representation of the specified node
	 * @created 24.04.2013
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

	private boolean isBlankNode(Node zURI) {
		try {
			zURI.asBlankNode();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public void selectGraphData() {
        final long l = System.currentTimeMillis();
        queries.clear();
        String concept = getParameterMap().get(CONCEPT);
		String conceptNameEncoded = null;

		String url;
		if (concept.contains(":")) {
			url = Rdf2GoUtils.expandNamespace(rdfRepository, concept);
		}
		else {
			try {
				conceptNameEncoded = URLEncoder.encode(concept, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			url = rdfRepository.getLocalNamespace() + conceptNameEncoded;
		}
		URI conceptURI = new URIImpl(url);

		// if requested, the predecessor are added to the source
		if (requestedHeight > 0) {
			addPredecessors(conceptURI);
		}
		insertMainConcept(conceptURI);
		// if requested, the successors are added to the source
		if (requestedDepth > 0) {
			addSuccessors(conceptURI);
		}

		SubpropertyEliminator.eliminateSubproperties(data, rdfRepository);
		data.clearIsolatedNodesFromDefaultLevel();

        System.out.println("Zeit: " + (System.currentTimeMillis() - l));

        for (String query : queries) {
            System.out.println(query);
        }
        System.out.println();
        System.out.println("ASKS: " + asks);
        System.out.println("Queries: " + queries.size());
    }

	@Override
	public void insertMainConcept(Node conceptURI) {
		String concept = getConceptName(conceptURI);

		String conceptLabel = Utils.getRDFSLabel(conceptURI.asURI(), rdfRepository,
				getParameterMap().get(LANGUAGE));
		if (conceptLabel == null) {
			conceptLabel = concept;
		}
		// the main concept is inserted
		// TODO: use Utils.createNode() to obtain correct coloring of root
		/*ConceptNode conceptNode = new ConceptNode(concept,
				getConceptType(conceptURI.asURI()),
				conceptURI.toString(), conceptLabel, Utils.getStyle(getConceptType(conceptURI)));
		*/
		ConceptNode conceptNode = Utils.createNode(getParameterMap(), rdfRepository, uriProvider, section, data, conceptURI, true);
		conceptNode.setRoot(true);
		data.addConcept(conceptNode);

	}

	@Override
	public void addSuccessors(Node conceptURI) {
		addSuccessors(conceptURI, false);
	}

	public void addSuccessors(Node conceptURI, boolean labelsOnly) {
		// literals cannot have successors
		if (isLiteral(conceptURI)) return;

		if (!labelsOnly) {
			if (expandedSuccessors.contains(conceptURI)) {
				// already expanded
				return;
			}
			expandedSuccessors.add(conceptURI);
		}

		String query = "SELECT ?y ?z WHERE { <"
				+ conceptURI.asURI().toString()
                + "> ?y ?z. " + predicateFilter() + nodeFilter("?z") + "}";
        queries.add(query);
        ClosableIterator<QueryRow> result =
				rdfRepository.sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			Node yURI = row.getValue("y");
			String y = getConceptName(yURI);

			Node zURI = row.getValue("z");
			String z = getConceptName(zURI);
			NODE_TYPE nodeType = getConceptType(zURI);
			if (checkTripleFilters(query, y, z, nodeType, labelsOnly)) continue;

			addConcept(conceptURI, zURI, yURI, nodeType);

			depth++;
			if (depth < requestedDepth) {
				addSuccessors(zURI);
			}
			if (depth == requestedDepth) {
				addOutgoingEdgesSuccessors(zURI);
				addSuccessors(zURI, true);
			}
			depth--;
		}
	}


    @Override
    public void addPredecessors(Node conceptURI) {
		if (expandedPredecessors.contains(conceptURI)) {
			// already expanded
			return;
		}
		expandedPredecessors.add(conceptURI);

		String query = "SELECT ?x ?y WHERE { ?x ?y <"
                + conceptURI.asURI().toString() + "> . " + predicateFilter() + nodeFilter("?x") + "}";
        queries.add(query);
        ClosableIterator<QueryRow> result =
				rdfRepository.sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			Node xURI = row.getValue("x");
			String x = getConceptName(xURI);

			Node yURI = row.getValue("y");
			String y = getConceptName(yURI);
			NODE_TYPE nodeType = getConceptType(xURI);

			if (checkTripleFilters(query, y, x, nodeType)) continue;

			height++;
			if (height < requestedHeight) {
				addPredecessors(xURI);
				addSuccessors(xURI, true);
			}

			if (height == requestedHeight) {
				addOutgoingEdgesPredecessors(xURI);
				addSuccessors(xURI, true);
			}
			height--;

			addConcept(xURI, conceptURI, yURI, nodeType);
		}
	}

	@Override
	public void addOutgoingEdgesSuccessors(Node conceptURI) {
		if (isLiteral(conceptURI)) return;

		String query = "SELECT ?y ?z WHERE { <"
				+ conceptURI.asURI().toString()
                + "> ?y ?z. " + predicateFilter() + "}";
        queries.add(query);
        ClosableIterator<QueryRow> result =
				rdfRepository.sparqlSelectIt(
						query);
		while (result.hasNext()) {
			QueryRow row = result.next();
			Node yURI = row.getValue("y");
			String y = getConceptName(yURI);

			Node zURI = row.getValue("z");
			String z = getConceptName(zURI);
			NODE_TYPE nodeType = getConceptType(zURI);

			if (checkTripleFilters(query, y, z, nodeType)) continue;

			addOuterConcept(conceptURI, zURI, yURI, false);
		}
	}

	private boolean checkTripleFilters(String query, String y, String z, NODE_TYPE nodeType) {
		return checkTripleFilters(query, y, z, nodeType, false);
	}

    private String predicateFilter() {
        if (propertyFilterExpression != null) {
            return propertyFilterExpression;
        }
        if (getExcludedRelations().size() == 0) {
            propertyFilterExpression = " FILTER (true)";
            return propertyFilterExpression;
        }
        StringBuffer filterExp = new StringBuffer();

        filterExp.append("FILTER (");

        Iterator<String> iter = getExcludedRelations().iterator();
        while (iter.hasNext()) {
            filterExp.append(" ?y != " + iter.next());
            if (iter.hasNext()) {
                filterExp.append(" && ");
            }
        }
        filterExp.append("). ");

        this.propertyFilterExpression = filterExp.toString();
        return propertyFilterExpression;
    }

    private String nodeFilter(String variable) {
        return " FILTER (true).";


/*
        if(nodeFilterExpression != null) {
            return nodeFilterExpression;
        }
        if(getExcludedNodes().size() == 0) {
            nodeFilterExpression = " FILTER (true).";
            return nodeFilterExpression;
        }
        StringBuffer filterExp = new StringBuffer();

        filterExp.append("FILTER (");

        Iterator<String> iter = getExcludedNodes().iterator();
        while(iter.hasNext()) {
            filterExp.append(" "+variable+" != "+iter.next());
            if(iter.hasNext()) {
                filterExp.append( " && ");
            }
        }
        filterExp.append("). ");

        this.nodeFilterExpression = filterExp.toString();
        return nodeFilterExpression;
*/
    }

	private boolean checkTripleFilters(String query, String y, String z, NODE_TYPE nodeType, boolean labelsOnly) {
		if (y == null) {
			Log.severe("Variable y of query was null: " + query);
			return true;
		}
		if (z == null) {
			Log.severe("Variable z of query was null: " + query);
			return true;
		}
		if (excludedRelation(y)) {
            // this filter is already contained in the sparql query
            return true;
		}
		if (excludedNode(z)) {
			return true;
		}
		if (!filteredClass(z) && !filteredRelation(y)) {
			return true;
		}
		if (nodeType == NODE_TYPE.CLASS && !showClasses()) {
			return true;
		}
		else if (nodeType == NODE_TYPE.PROPERTY && !showProperties()) {
			return true;
		}

		if (labelsOnly) {
			if (nodeType.equals(NODE_TYPE.LITERAL) || isTypeRelation(y)) {
				// only literals and type assertions are not filtered out
				return false;
			}
			else {
				return true;
			}
		}

		return false;
	}

	@Override
	public void addOutgoingEdgesPredecessors(Node conceptURI) {
		if (isLiteral(conceptURI)) return;

		String query = "SELECT ?x ?y WHERE { ?x ?y <"
				+ conceptURI.asURI().toString()
                + ">. " + predicateFilter() + "}";
        queries.add(query);
        QueryResultTable resultTable = rdfRepository.sparqlSelect(
				query);

		ClosableIterator<QueryRow> result = resultTable.iterator();

		while (result.hasNext()) {
			QueryRow row = result.next();
			Node xURI = row.getValue("x");
			String x = getConceptName(xURI);
			NODE_TYPE nodeType = getConceptType(xURI);

			Node yURI = row.getValue("y");
			String y = getConceptName(yURI);

			if (checkTripleFilters(query, y, x, nodeType)) continue;

			addOuterConcept(xURI, conceptURI, yURI, true);
		}
	}

	@Override
	public void addConcept(Node fromURI, Node toURI, Node relationURI, NODE_TYPE type) {
		String relation = getConceptName(relationURI);


/*
cluster change
 */
		String clazz = null;
		if (isTypeRelation(relation)) {
			clazz = getConceptName(toURI);
		}

		ConceptNode toNode;
		ConceptNode fromNode;

		toNode = Utils.createNode(this.getParameterMap(), this.rdfRepository, this.uriProvider, this.section, this.data, toURI, true);

		toNode.setOuter(false);

		fromNode = Utils.createNode(this.getParameterMap(), this.rdfRepository, this.uriProvider, this.section, this.data, fromURI, true, clazz);

		fromNode.setOuter(false);

		// look for label for the property
		String relationLabel = null;

		if (getParameterMap().get(OntoGraphDataBuilder.USE_LABELS) != null && !getParameterMap().get(OntoGraphDataBuilder.USE_LABELS)
				.equals("false")) {
			relationLabel = Utils.getRDFSLabel(
					relationURI.asURI(), rdfRepository,
					getParameterMap().get(LANGUAGE));
			if (relationLabel != null && relationLabel.charAt(relationLabel.length() - 3) == '@') {
				// do not show language tag of relation labels
				relationLabel = relationLabel.substring(0, relationLabel.length() - 3);
			}
		}

		if (relationLabel != null) {
			relation = relationLabel;
		}

		if (Strings.isBlank(clazz)) {
			// classes are rendered as cluster labels - so no extra edge is required
			Edge edge = new Edge(fromNode, relation, toNode);
			addEdge(edge);
		}

	}

	private boolean isTypeRelation(String relation) {
		if (relation.length() > 4 && relation.substring(relation.length() - 4).equalsIgnoreCase("type")) {
			return true;
		}
		return false;
	}

	@Override
	public void addOuterConcept(Node fromURI, Node toURI, Node relationURI, boolean predecessor) {
		String from = getConceptName(fromURI);
		String to = getConceptName(toURI);
		String relation = getConceptName(relationURI);

		// TODO: implement rendering of literal nodes
		if (to == null) {
			return;
		}

		/*
		cluster change
		 */
		String clazz = null;
		if (isTypeRelation(relation)) {
			clazz = getConceptName(toURI);
		}

		ConceptNode toNode = Utils.createNode(this.getParameterMap(), this.rdfRepository, this.uriProvider, this.section, this.data, toURI, false);

		ConceptNode fromNode = Utils.createNode(this.getParameterMap(), this.rdfRepository, this.uriProvider, this.section, this.data, fromURI, false, clazz);

		ConceptNode current = null;
		if (predecessor) {
			// from is current new one
			current = fromNode;
		}
		else {
			// to is current new one
			current = toNode;
		}

		boolean nodeIsNew = !data.getConceptDeclarations().contains(current);

		Edge edge = new Edge(fromNode, relation, toNode);

		boolean edgeIsNew = !data.getAllEdges().contains(edge);

		if (showOutgoingEdges()) {
			if (nodeIsNew) {
				if (predecessor) {
					// from is current new one
					fromNode.setOuter(true);
					data.addConcept(fromNode);
				}
				else {
					// to is current new one
					toNode.setOuter(true);
					data.addConcept(toNode);
				}

			}
			if (edgeIsNew) {

				addEdge(edge);

			}
		}
		else {
			// do not show outgoing edges
			if (!nodeIsNew) {
				// but show if its node is internal one already, i.e. node would exist even without this edge
				if (!isTypeRelation(relation)) { // cluster change
					addEdge(edge);
				}

			}
			else {
				// exception for labels:
				// labels are shown for rim concepts even if out of scope in principle
				if (isLiteralEdge(edge)) {
					final ConceptNode label = edge.getObject();
					data.addConcept(label);
					addEdge(edge);
				}
			}
		}
	}

	/**
	 * adds an edge to the graph data object in the following way: if the edge is a label it will be added to a cluster
	 * for the subject node. The edge will be added at default level (no cluster) otherwise
	 *
	 * @param edge
	 */
	private void addEdge(Edge edge) {
		if (isLiteralEdge(edge)) {
			// this is a label edge
			data.addEdgeToCluster(edge.getSubject(), edge);
		}
		else {
			data.addEdge(edge);
		}
	}

	private boolean isLiteralEdge(Edge edge) {
		return edge.getObject().getType().equals(NODE_TYPE.LITERAL);
	}

}

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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.rdfs.vis.util.Utils;
import de.knowwe.visualization.ConceptNode;
import de.knowwe.visualization.Edge;
import de.knowwe.visualization.GraphDataBuilder;

/**
 * @author Johanna Latt
 * @created 11.10.2013
 */
public class OntoGraphDataBuilder extends GraphDataBuilder<Node> {

	private Rdf2GoCore rdfRepository = null;

	private int depth = 0;
	private int height = 0;

	/**
	 * Allows to create a new Ontology Rendering Core. For each rendering task a
	 * new one should be created.
	 * 
	 * @param realPath webapp path
	 * @param section a section that the graph is rendered for/at
	 * @param parameters the configuration, consider the constants of this class
	 */
	public OntoGraphDataBuilder(String realPath, Section<?> section, Map<String, String> parameters, LinkToTermDefinitionProvider uriProvider, Rdf2GoCore rdfRepository) {
		this.rdfRepository = rdfRepository;
		initialiseData(realPath, section, parameters, uriProvider);
	}

	public String getConceptName(Node uri) {
		return getConceptName(uri, this.rdfRepository);
	}

	public static String getConceptName(Node uri, Rdf2GoCore repo) {
		try {
			String reducedNamespace = Rdf2GoUtils.reduceNamespace(repo,
					uri.asURI().toString());
			String[] splitURI = reducedNamespace.split(":");
			String namespace = splitURI[0];
			String name = splitURI[1];
			if (namespace.equals("lns")) {
				return urlDecode(name);
			}
			else {
				return namespace + ":" + urlDecode(name);
			}

		}
		catch (ClassCastException e) {
			return null;
		}
	}

	private NODE_TYPE getConceptType(URI conceptURI) {
		NODE_TYPE result = NODE_TYPE.UNDEFINED;

		String askClass = "ASK { <" + conceptURI.toString()
				+ "> rdf:type owl:Class}";
		if (rdfRepository.sparqlAsk(askClass)) return NODE_TYPE.CLASS;

		String askProperty = "ASK { <" + conceptURI.toString()
				+ "> rdf:type owl:Property}";
		if (rdfRepository.sparqlAsk(askProperty)) return NODE_TYPE.PROPERTY;

		return result;
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

	@Override
	public void selectGraphData() {

		String concept = getParameterMap().get(CONCEPT);
		String conceptNameEncoded = null;

		String url = null;
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
		ConceptNode conceptNode = new ConceptNode(concept,
				getConceptType(conceptURI.asURI()),
				conceptURI.toString(), conceptLabel);
		conceptNode.setRoot(true);
		data.addConcept(conceptNode);

	}

	@Override
	public void addSuccessors(Node conceptURI) {
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
				Logger.getLogger(OntoGraphDataBuilder.class.getName()).log(Level.SEVERE,
						"Variable y of query was null: " + query);
				continue;
			}

			if (z == null) {
				Logger.getLogger(OntoGraphDataBuilder.class.getName()).log(Level.SEVERE,
						"Variable z of query was null: " + query);
				continue;
			}

			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(z)) {
				continue;
			}
			if (!filteredClass(z) && !filteredRelation(y)) {
				continue;
			}
			if (isLiteral(zURI)) {
				continue;
			}
			NODE_TYPE nodeType = getConceptType(zURI.asURI());
			if (nodeType == NODE_TYPE.CLASS && !showClasses()) {
				continue;
			}
			else if (nodeType == NODE_TYPE.PROPERTY && !showProperties()) {
				continue;
			}
			addConcept(conceptURI, zURI, yURI, nodeType);

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

	@Override
	public void addPredecessors(Node conceptURI) {
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
				Logger.getLogger(OntoGraphDataBuilder.class.getName()).log(Level.SEVERE,
						"Variable y of query was null: " + query);
				continue;
			}

			if (x == null) {
				Logger.getLogger(OntoGraphDataBuilder.class.getName()).log(Level.SEVERE,
						"Variable x of query was null: " + query);
				continue;
			}

			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(x)) {
				continue;
			}
			if (!filteredClass(x) && !filteredRelation(y)) {
				continue;
			}

			NODE_TYPE nodeType = getConceptType(xURI.asURI());

			if (nodeType == NODE_TYPE.CLASS && !showClasses()) {
				continue;
			}
			else if (nodeType == NODE_TYPE.PROPERTY && !showProperties()) {
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

			addConcept(xURI, conceptURI, yURI, nodeType);
		}
	}

	@Override
	public void addOutgoingEdgesSuccessors(Node conceptURI) {
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
				Logger.getLogger(OntoGraphDataBuilder.class.getName()).log(Level.SEVERE,
						"Variable y of query was null: " + query);
				continue;
			}

			if (z == null) {
				Logger.getLogger(OntoGraphDataBuilder.class.getName()).log(Level.SEVERE,
						"Variable z of query was null: " + query);
				continue;
			}

			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(z)) {
				continue;
			}
			if (!filteredClass(z) && !filteredRelation(y)) {
				continue;
			}
			addOuterConcept(conceptURI, zURI, yURI, false);
		}
	}

	@Override
	public void addOutgoingEdgesPredecessors(Node conceptURI) {
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
				Logger.getLogger(OntoGraphDataBuilder.class.getName()).log(Level.SEVERE,
						"Variable y of query was null: " + query);
				continue;
			}

			if (x == null) {
				Logger.getLogger(OntoGraphDataBuilder.class.getName()).log(Level.SEVERE,
						"Variable x of query was null: " + query);
				continue;
			}

			if (excludedRelation(y)) {
				continue;
			}
			if (excludedNode(x)) {
				continue;
			}
			if (!filteredClass(x) && !filteredRelation(y)) {
				continue;
			}
			addOuterConcept(xURI, conceptURI, yURI, true);
		}
	}

	@Override
	public void addConcept(Node fromURI, Node toURI, Node relationURI, NODE_TYPE type) {
		String from = getConceptName(fromURI);
		String to = getConceptName(toURI);
		String relation = getConceptName(relationURI);

		ConceptNode toNode = null;
		ConceptNode fromNode = null;

		toNode = data.getConcept(to);
		if (toNode == null) {
			String label = Utils.getRDFSLabel(
					toURI.asURI(), rdfRepository,
					getParameterMap().get(LANGUAGE));
			if (label == null) {
				label = to;
			}
			toNode = new ConceptNode(to, type, createConceptURL(to), label);
			data.addConcept(toNode);
		}
		toNode.setOuter(false);
		fromNode = data.getConcept(from);
		if (fromNode == null) {
			String label = Utils.getRDFSLabel(
					fromURI.asURI(), rdfRepository,
					getParameterMap().get(LANGUAGE));
			if (label == null) {
				label = from;
			}
			fromNode = new ConceptNode(from, type, createConceptURL(from), label);
			data.addConcept(fromNode);
		}
		fromNode.setOuter(false);

		// look for label for the property
		String relationLabel = Utils.getRDFSLabel(
				relationURI.asURI(), rdfRepository,
				getParameterMap().get(LANGUAGE));
		if (relationLabel != null) {
			relation = relationLabel;
		}

		Edge newLineRelationsKey = new Edge(fromNode, relation, toNode);

		data.addEdge(newLineRelationsKey);

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

		String currentConcept = null;
		if (predecessor) {
			// from is current new one
			currentConcept = from;
		}
		else {
			// to is current new one
			currentConcept = to;
		}
		boolean nodeIsNew = !data.getConceptDeclarations().contains(new ConceptNode(
				currentConcept));

		ConceptNode toNode = data.getConcept(to);
		if (toNode == null) {
			String label = Utils.getRDFSLabel(
					toURI.asURI(), rdfRepository,
					getParameterMap().get(LANGUAGE));
			if (label == null) {
				label = to;
			}
			toNode = new ConceptNode(to, NODE_TYPE.UNDEFINED,
					createConceptURL(to),
					label);
		}
		ConceptNode fromNode = data.getConcept(from);
		if (fromNode == null) {
			String label = Utils.getRDFSLabel(
					fromURI.asURI(), rdfRepository,
					getParameterMap().get(LANGUAGE));
			if (label == null) {
				label = from;
			}
			fromNode = new ConceptNode(from, NODE_TYPE.UNDEFINED,
					createConceptURL(from),
					label);
		}

		Edge edge = new Edge(fromNode, relation, toNode);

		boolean edgeIsNew = !data.getEdges().contains(edge);

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
				data.addEdge(edge);
			}
		}
		else {
			// do not show outgoing edges
			if (!nodeIsNew) {
				// but show if its node is internal one already
				data.addEdge(edge);
			}
		}
	}

}

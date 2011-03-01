/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

/**
 * 
 */
package de.d3web.we.core.semantic.rdf2go;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.openrdf.repository.RepositoryException;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.Section;

/**
 * @author kazamatzuri
 * 
 */
public class OwlHelper {

	public static URI EQUAL;
	public static URI EXPLAINS;
	public static URI GREATER;
	public static URI GREATEREQUAL;
	public static URI INPUT;
	public static URI SMALLER;
	public static URI SMALLEREQUAL;
	public static URI SOLUTION;
	public static String IOO = "List<Statement>";
	public static URI ANNOTATION;
	public static URI HASTAG;
	public static URI HASTOPIC;
	public static URI HASTYPE;
	public static URI NARYPROPERTY;
	public static URI TEXTORIGIN;
	public static URI HASNODE;
	public static URI HASORIGIN;

	static {
		SOLUTION = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "Solution");
		INPUT = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "Input");
		SMALLER = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "Smaller");
		GREATER = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "Greater");
		GREATEREQUAL = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "GreaterEqual");
		SMALLEREQUAL = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "SmallerEqual");
		EQUAL = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "Equal");
		EXPLAINS = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "Explains");
		ANNOTATION = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "Annotation");
		HASTAG = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "hasTag");
		HASTOPIC = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "hasTopic");
		HASTYPE = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "hasType");
		NARYPROPERTY = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "NaryProperty");
		TEXTORIGIN = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "TextOrigin");
		HASNODE = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "hasNode");
		HASORIGIN = Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, "hasOrigin");
	}
	private static final HashMap<String, URI> comparatorcache = new HashMap<String, URI>();

	/**
	 * returns a matching comparator URI to the string
	 * 
	 * @param comp
	 * @return
	 */
	public static URI getComparator(String comp) {
		return comparatorcache.get(comp);
	}
	
	/**
	 * @param value
	 * @return
	 */
	private static String beautify(String value) {
		String temp = value;
		try {
			temp = URLDecoder.decode(value, "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
		}
		catch (IllegalArgumentException e) {

		}

		try {
			return URLEncoder.encode(temp, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "value";

	}

	/**
	 * attaches a TextOrigin Node to a Resource. It's your duty to make sure the
	 * Resource is of the right type if applicable (eg attachto RDF.TYPE
	 * RDF.STATEMENT)
	 * 
	 * @param attachto The Resource that will be annotated bei the TO-Node
	 * @param source The source section that should be used
	 * @param io the IntermediateOwlObject that should collect the statements
	 */
	public static void attachTextOrigin(Resource attachto, Section source,
			IntermediateOwlObject io) {
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		io.addAllStatements(createTextOrigin(source, to));
		io.addStatement(createStatement(attachto,
					RDFS.isDefinedBy, to));
	}
	
	public static void attachTextOrigin(Resource attachto, Section source,
			List<Statement> io) {
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		io.addAll(createTextOrigin(source, to));
		io.add(createStatement(attachto,
				RDFS.isDefinedBy, to));
	}

	private static List<Statement> createTextOrigin(
			Section<Type> source, Resource to) {
		ArrayList<Statement> io = new ArrayList<Statement>();
		io.add(createStatement(to, RDF.type, TEXTORIGIN));
		io.add(createStatement(to, HASNODE,
				createLiteral(source.getID())));
		io.add(createStatement(to, HASTOPIC,
				createlocalURI(source.getTitle())));
		return io;
	}

	/**
	 * 
	 * @param father
	 * @param child
	 * @throws RepositoryException
	 */
	public static URI createChildOf(URI father, URI child) {
		Statement s = Rdf2GoCore.getInstance().createStatement(child,
				RDF.type, father);
		Rdf2GoCore.getInstance().addStaticStatement(s);
		return child;
	}

	public static Literal createLiteral(String text) {
		return Rdf2GoCore.getInstance().createLiteral(text);
	}

	public static Literal createLiteral(String text, URI type) {
		return Rdf2GoCore.getInstance().createLiteral(text, type);
	}
	
	/**
	 * creates an URI within the local namespace of this wiki
	 * 
	 * @param value
	 * @return
	 * @throws RepositoryException
	 */
	public static URI createlocalURI(String value) {
		value = beautify(value);
		return Rdf2GoCore.getInstance().createURI(Rdf2GoCore.localns, value);

	}

	/**
	 * creates a statement (convenience)
	 * 
	 * @throws RepositoryException
	 * 
	 */
	public static Statement createStatement(Resource arg0, URI arg1, Node arg2) {
		return Rdf2GoCore.getInstance().createStatement(arg0, arg1,
				arg2);
	}

	/**
	 * creates an URI in the upperontologynamespace
	 * 
	 * @param value the name of the URI to be created
	 * @return the created URI
	 * @throws RepositoryException
	 */
	public static URI createBasensURI(String value) {
		if (value == null) return null;
		value = beautify(value);
		return Rdf2GoCore.getInstance().createURI(Rdf2GoCore.basens, value);
	}

	/**
	 * creates an URI in the specified namespace
	 * 
	 * @param ns the name of the namespace
	 * @param value the name of the URI to be created
	 * @return the created URI
	 * @throws RepositoryException
	 */
	public static URI createURI(String ns, String value) {
		value = beautify(value);
		return Rdf2GoCore.getInstance().createURI(ns, value);
	}

	public static boolean knownConcept(String op) {
		// TODO
		String querystring = "SELECT ?x  WHERE { ?x ?y ?z  FILTER regex( str(?x), \""
				+ op + "\", \"i\" )  }";
		QueryResultTable result = null;
		try {
			result = Rdf2GoCore.getInstance().sparqlSelect(querystring);
		}
		catch (ModelRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.iterator().hasNext();
	}

	/**
	 * @param cur
	 */
	public static List<Statement> createlocalProperty(String cur) {
		URI prop = Rdf2GoCore.getInstance().createLocalURI(cur);
		URI naryprop = NARYPROPERTY;
		List<Statement> io = new ArrayList<Statement>();
		if (!PropertyManager.getInstance().isValid(prop)) {
			io.add(createStatement(prop,
						RDFS.subClassOf, naryprop));
		}
		return io;

	}

	/**
	 * @param cur
	 */
	public static List<Statement> createProperty(String cur) {
		URI prop = createBasensURI(cur);
		URI naryprop = NARYPROPERTY;
		List<Statement> io = new ArrayList<Statement>();
		if (!PropertyManager.getInstance().isValid(prop)) {
			io.add(createStatement(prop,
						RDFS.subClassOf, naryprop));
		}
		return io;

	}

	public static IntermediateOwlObject createProperty(String subject,
			String property, String object, Section<Type> source) {

		URI suri = Rdf2GoCore.getInstance().createLocalURI(subject);
		URI puri = Rdf2GoCore.getInstance().createLocalURI(property);
		URI ouri = Rdf2GoCore.getInstance().createLocalURI(object);

		return createProperty(suri, puri, ouri, source);
	}

	/**
	 * @param soluri
	 * @param prop
	 * @param stringa
	 * @param id
	 * @return
	 */
	public static IntermediateOwlObject createProperty(URI suri, URI puri, URI ouri,
			Section source) {
		IntermediateOwlObject io = new IntermediateOwlObject();
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		URI nary = Rdf2GoCore.getInstance().createLocalURI(
					source.getTitle() + ".." + source.getID() + ".."
							+ Rdf2GoCore.getLocalName(suri) + Rdf2GoCore.getLocalName(puri)
							+ Rdf2GoCore.getLocalName(ouri));
		io.addAllStatements(createTextOrigin(source, to));
		io.addStatement(createStatement(nary, RDFS.isDefinedBy, to));
		io.addStatement(createStatement(nary, RDF.type,
					RDF.Statement));
		io.addStatement(createStatement(nary, RDF.predicate, puri));
		io.addStatement(createStatement(nary, RDF.object, ouri));
		io.addStatement(createStatement(nary, RDF.subject, suri));
		io.addLiteral(nary);

		return io;
	}

	/**
	 * @param soluri
	 * @param prop
	 * @param stringa
	 * @param id
	 * @return
	 */
	public static IntermediateOwlObject createAnnotationProperty(URI suri, URI puri,
			URI ouri, Section source) {
		IntermediateOwlObject io = new IntermediateOwlObject();
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		URI nary = Rdf2GoCore.getInstance().createLocalURI(
					source.getTitle() + ".." + source.getID() + ".."
							+ Rdf2GoCore.getLocalName(suri) + Rdf2GoCore.getLocalName(puri)
							+ Rdf2GoCore.getLocalName(ouri));
		io.addAllStatements(createTextOrigin(source, to));
		io.addStatement(createStatement(to, HASTYPE, ANNOTATION));
		io.addStatement(createStatement(nary, RDFS.isDefinedBy, to));
		io.addStatement(createStatement(nary, RDF.type,
					RDF.Statement));
		io.addStatement(createStatement(nary, RDF.predicate, puri));
		io.addStatement(createStatement(nary, RDF.object, ouri));
		io.addStatement(createStatement(nary, RDF.subject, suri));

		io.addLiteral(nary);
		return io;
	}

	public static void attachTextOrigin(Resource attachto,
			Section<AbstractType> source, IntermediateOwlObject io, URI type) {
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		io.addAllStatements(createTextOrigin(source, to, type));
		io.addStatement(Rdf2GoCore.getInstance().createStatement(attachto,
					RDFS.isDefinedBy, to));
	}
	
	public static void attachTextOrigin(Resource attachto,
			Section<AbstractType> source, List<Statement> io, URI type) {
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		io.addAll(createTextOrigin(source, to, type));
		io.add(Rdf2GoCore.getInstance().createStatement(attachto,
					RDFS.isDefinedBy, to));
	}

	private static List<Statement> createTextOrigin(
			Section<AbstractType> source, Resource to, URI type) {
		ArrayList<Statement> io = new ArrayList<Statement>();
		io.add(createStatement(to, RDF.type, TEXTORIGIN));
		io.add(createStatement(to, HASNODE,
				createLiteral(source.getID())));
		io.add(createStatement(to, HASTOPIC,
				createlocalURI(source.getTitle())));
		io.add(createStatement(to, HASTYPE, type));
		return io;
	}

	public static List<Statement> createStatementSrc(URI soluri, URI prop,
			URI object, Section s, URI type) {
		ArrayList<Statement> io = new ArrayList<Statement>();
		BlankNode bnode = Rdf2GoCore.getInstance().createBlankNode();
		io.add(createStatement(bnode, RDF.subject, soluri));
		io.add(createStatement(bnode, RDF.predicate, prop));
		io.add(createStatement(bnode, RDF.object, object));
		io.add(createStatement(bnode, RDF.type, RDF.Statement));
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		io.addAll(createTextOrigin(s, to, type));
		io.add(createStatement(bnode, RDFS.isDefinedBy, to));
		return io;
	}
	
	private static void initSTDURIs() {
		comparatorcache.put("=", EQUAL);
		comparatorcache.put("<", SMALLER);
		comparatorcache.put(">", GREATER);
		comparatorcache.put("<=", SMALLEREQUAL);
		comparatorcache.put(">=", GREATEREQUAL);
	}

}

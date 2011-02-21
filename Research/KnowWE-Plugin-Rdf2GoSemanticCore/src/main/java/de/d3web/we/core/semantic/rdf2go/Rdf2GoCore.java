/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.core.semantic.rdf2go;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.ReasoningNotSupportedException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.util.RDFTool;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.event.Event;
import de.d3web.we.event.EventListener;
import de.d3web.we.event.FullParseEvent;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;

/**
 * 
 * @author grotheer
 * @created 29.11.2010
 */
public class Rdf2GoCore implements EventListener {

	// Base Namespace

	public static final String basens = "http://ki.informatik.uni-wuerzburg.de/d3web/we/knowwe.owl#";
	public static final String localns = KnowWEEnvironment.getInstance().getWikiConnector().getBaseUrl()
			+ "OwlDownload.jsp#";

	private static final String JENA = "jena";
	private static final String BIGOWLIM = "bigowlim";
	public static final String SESAME = "sesame";

	public static final String select = "select";
	public static final String ask = "ask";

	// use JENA, BIGOWLIM or SESAME:
	public static String USE_MODEL = JENA;

	// use Reasoning.owl, Reasoning.rdfs, Reasoning.rdfsAndOwl or
	// Reasoning.none:
	// SESAME and Reasoning.owl/Reasoning.rdfsAndOwl uses SwiftOWLIM Sail!
	private static Reasoning USE_REASONING = Reasoning.rdfs;

	private static Rdf2GoCore me;
	private Model model;
	private HashMap<String, WeakHashMap<Section<? extends KnowWEObjectType>, List<Statement>>> statementcache;
	private HashMap<Statement, Integer> duplicateStatements;
	private HashMap<String, String> namespaces;

	public static void printStuff() {
		System.out.println(Rdf2GoCore.getInstance().duplicateStatements.size());
		System.out.println(Rdf2GoCore.getInstance().statementcache.size());
		System.out.println(me.model.size());
	}

	/**
	 * Initializes the model and its caches and namespaces
	 */
	public void init() {
		initModel();
		statementcache = new HashMap<String, WeakHashMap<Section<? extends KnowWEObjectType>, List<Statement>>>();
		duplicateStatements = new HashMap<Statement, Integer>();
		namespaces = new HashMap<String, String>();
		namespaces.putAll(model.getNamespaces());
		initDefaultNamespaces();
	}

	/**
	 * 
	 * @return me
	 */
	public static Rdf2GoCore getInstance() {
		if (me == null) {
			me = new Rdf2GoCore();
			me.init();
		}
		return me;
	}

	private void registerJenaModel() {
		System.out.print("Jena 2.6");
		RDF2Go.register(new org.ontoware.rdf2go.impl.jena26.ModelFactoryImpl());
	}

	private void registerBigOWLIMModel() {
		System.out.print("BigOWLIM");
		//RDF2Go.register(new com.ontotext.trree.rdf2go.OwlimModelFactory());
	}

	/**
	 * registers the sesame model, uses SwiftOwlim if owl-Reasoning is used
	 */
	private void registerSesameModel() {
		System.out.print("Sesame 2.3");
		if (USE_REASONING == Reasoning.owl || USE_REASONING == Reasoning.rdfsAndOwl) {
			System.out.print(" with SwiftOWLIM");
		}
		RDF2Go.register(new org.openrdf.rdf2go.RepositoryModelFactory());
	}

	/**
	 * registers and opens the specified model
	 * 
	 * @throws ModelRuntimeException
	 */
	public void initModel() throws ModelRuntimeException, ReasoningNotSupportedException {
		if (USE_MODEL == JENA) {
			registerJenaModel();
		}
		else if (USE_MODEL == BIGOWLIM) {
			registerBigOWLIMModel();
		}
		else if (USE_MODEL == SESAME) {
			registerSesameModel();
		}
		else {
			throw new ModelRuntimeException("Model not supported");
		}

		if (USE_REASONING != null) {
			model = RDF2Go.getModelFactory().createModel(USE_REASONING);
		}
		else {
			model = RDF2Go.getModelFactory().createModel();
		}

		model.open();

		System.out.println(" model initialized");

	}

	/**
	 * sets the default namespaces
	 */
	private void initDefaultNamespaces() {
		addNamespace("ns", basens);
		addNamespace("lns", localns);
		addNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		addNamespace("w", "http://www.umweltbundesamt.de/wisec#");
		addNamespace("owl", "http://www.w3.org/2002/07/owl#");
		addNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		addNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
	}

	public void readFrom(InputStream in) throws ModelRuntimeException, IOException {
		model.readFrom(in);
	}

	public void readFrom(Reader in) throws ModelRuntimeException, IOException {
		model.readFrom(in);
	}

	/**
	 * add a namespace to the model
	 * 
	 * @param sh prefix
	 * @param ns url
	 */
	public void addNamespace(String sh, String ns) {
		namespaces.put(sh, ns);
		model.setNamespace(sh, ns);
	}

	public void removeNamespace(String sh) {
		namespaces.remove(sh);
		model.removeNamespace(sh);
	}

	public HashMap<String, String> getNameSpaces() {
		return namespaces;
	}

	/**
	 * expands namespace from prefix to uri string
	 * 
	 * @created 04.01.2011
	 * @param s
	 * @return
	 */
	public String expandNamespace(String s) throws IllegalArgumentException {
		if (s.startsWith("http://")) {
			return s;
		}
		String[] array = s.split(":", 2);
		if (array.length == 2) {
			return expandNSPrefix(array[0]) + array[1];
		}
		throw new IllegalArgumentException("Not a valid (absolute) URI: " + s);

	}

	/**
	 * expands prefix to namespace
	 * 
	 * @created 06.12.2010
	 * @param ns
	 * @return
	 */
	public String expandNSPrefix(String ns) {
		for (Entry<String, String> cur : namespaces.entrySet()) {
			if (ns.equals(cur.getKey())) {
				ns = cur.getValue();
				break;
			}
		}
		return ns;
	}

	/**
	 * reduces namespace in uri string to prefix
	 * 
	 * @created 06.12.2010
	 * @param s
	 * @return
	 */
	public String reduceNamespace(String s) {
		for (Entry<String, String> cur : namespaces.entrySet()) {
			s = s.replaceAll(cur.getValue(), cur.getKey() + ":");
		}
		return s;

	}

	public String renderedSparqlSelect(String query) throws ModelRuntimeException, MalformedQueryException {
		return render(sparqlSelect(query));
	}

	public URI createURI(String str) {
		return model.createURI(expandNamespace(str));
	}

	public URI createURI(String ns, String str) {
		return model.createURI(expandNSPrefix(ns) + str);
	}

	public URI createLocalURI(String str) {
		return model.createURI(localns + str);
	}

	/**
	 * 
	 * @created 06.12.2010
	 * @param qrt
	 * @return html table with all results of qrt
	 */
	public String render(QueryResultTable qrt) {
		List<String> l = qrt.getVariables();
		ClosableIterator<QueryRow> i = qrt.iterator();
		String result = "<table>";
		for (String var : l) {
			result += "<th>" + var + "</th>";
		}
		while (i.hasNext()) {
			QueryRow s = i.next();
			result += "<tr>";
			for (String var : l) {
				result += "<td>" + reduceNamespace(s.getValue(var).toString()) + "</td>";
			}
			result += "</tr>";
		}
		result += "</table>";
		return result;
	}

	public boolean sparqlAsk(String query) throws ModelRuntimeException, MalformedQueryException {
		return model.sparqlAsk(query);
	}

	public QueryResultTable sparqlSelect(String query) throws ModelRuntimeException, MalformedQueryException {
		if (query.startsWith(getSparqlNamespaceShorts())) {
			return model.sparqlSelect(query);
		}
		return model.sparqlSelect(getSparqlNamespaceShorts() + query);
	}

	/**
	 * 
	 * @created 06.12.2010
	 * @param s
	 * @return statements of section s (with children)
	 */
	public List<Statement> getSectionStatementsRecursive(
			Section<? extends KnowWEObjectType> s) {
		List<Statement> allstatements = new ArrayList<Statement>();

		if (getStatementsofSingleSection(s) != null) {
			// add statements of this section
			allstatements.addAll(getStatementsofSingleSection(s));
		}

		// walk over all children
		for (Section<? extends KnowWEObjectType> current : s.getChildren()) {
			// collect statements of the the children's descendants
			allstatements.addAll(getSectionStatementsRecursive(current));
		}

		return allstatements;
	}

	/**
	 * removes all statements of section s
	 * 
	 * @created 06.12.2010
	 * @param s
	 */
	public void removeSectionStatementsRecursive(
			Section<? extends KnowWEObjectType> s) {

		removeStatementsofSingleSection(s);

		// walk over all children
		for (Section<? extends KnowWEObjectType> current : s.getChildren()) {
			removeSectionStatementsRecursive(current);
		}
	}

	/**
	 * 
	 * @param sec
	 * @created 06.12.2010
	 * @return statements of section sec (without children)
	 */
	private List<Statement> getStatementsofSingleSection(
			Section<? extends KnowWEObjectType> sec) {
		WeakHashMap<Section<? extends KnowWEObjectType>, List<Statement>> temp = statementcache.get(sec
				.getArticle().getTitle());
		if (temp != null) {
			return temp.get(sec);
		}
		return new ArrayList<Statement>();
	}

	/**
	 * removes statements from statementcache and rdf store
	 * 
	 * @created 06.12.2010
	 * @param sec
	 */
	private void removeStatementsofSingleSection(
			Section<? extends KnowWEObjectType> sec) {
		WeakHashMap<Section<? extends KnowWEObjectType>, List<Statement>> temp = statementcache.get(sec
					.getArticle().getTitle());
		
		if (temp != null) {
			if (temp.containsKey(sec)) {
				List<Statement> statementsOfSection = temp.get(sec);
				List<Statement> removedStatements = new ArrayList<Statement>();
				
				for (Statement s : statementsOfSection) {

					if (duplicateStatements.containsKey(s)) {
						if (duplicateStatements.get(s) != 1) {
							duplicateStatements.put(s, duplicateStatements.get(s) - 1);
						}
						else {
							duplicateStatements.remove(s);
						}
					}
					else {
						removedStatements.add(s);
//						model.removeStatement(s);
					}
				}
				temp.remove(sec);
				if (temp.isEmpty()) {
					statementcache.remove(sec.getArticle().getTitle());
				}
				model.removeAll(removedStatements.iterator());
			}
			else {
				// Not necessary because of full-pasre-listener being active
				// for (Section cur : temp.keySet()) {
				// if (cur.getID().equals(sec.getID())) {
				// removeStatementsofSingleSection(cur);
				// }
				// break;
				// }
			}
		}
	}

	/**
	 * adds statements to statementcache and rdf store and count duplicate
	 * statements
	 * 
	 * @created 06.12.2010
	 * @param allStatements
	 * @param sec
	 */
	public void addStatements(List<Statement> allStatements, Section<? extends KnowWEObjectType> sec) {
		Logger.getLogger(this.getClass().getName()).finer(
				"semantic core updating " + sec.getID() + "  "
						+ allStatements.size());

		WeakHashMap<Section<? extends KnowWEObjectType>, List<Statement>> temp = statementcache.get(sec.getTitle());
		boolean scContainsCurrentSection = false;
		if (temp != null) {
			if (temp.get(sec) != null) {
				scContainsCurrentSection = true;
			}
			else {
				// Not necessary because of full-pasre-listener being active
				// for (Section cur : temp.keySet()) {
				// if (cur.getID().equals(sec.getID())) {
				// scContainsCurrentSection = true;
				// }
				// break;
				// }
			}
		}
		if (!scContainsCurrentSection) {
			for (Statement s : allStatements) {
				if (model.contains(s)) {
					if (duplicateStatements.containsKey(s)) {
						duplicateStatements.put(s, duplicateStatements.get(s) + 1);
					}
					else {
						duplicateStatements.put(s, 1);
					}
				}
			}
		}
		addToStatementcache(sec, allStatements);

		// Maybe remove duplicates before adding to store, if performance is
		// better
		addStaticStatements(allStatements, sec);
	}

	public void addStatements(IntermediateOwlObject io, Section<? extends KnowWEObjectType> sec) {
		addStatements(io.getAllStatements(), sec);
	}

	/**
	 * adds statements to rdf store
	 * 
	 * @created 06.12.2010
	 * @param allStatements
	 * @param sec
	 */
	public void addStaticStatements(List<Statement> allStatements, Section<? extends KnowWEObjectType> sec) {
		Iterator<Statement> i = allStatements.iterator();
		model.addAll(i);

	}

	public void addStaticStatement(Statement statement) {
		model.addStatement(statement);
	}

	/**
	 * adds statements to statementcache
	 * 
	 * @created 06.12.2010
	 * @param sec
	 * @param allStatements
	 */
	private void addToStatementcache(Section<? extends KnowWEObjectType> sec, List<Statement> allStatements) {
		WeakHashMap<Section<? extends KnowWEObjectType>, List<Statement>> temp = statementcache.get(sec
				.getArticle().getTitle());
		if (temp == null) {
			temp = new WeakHashMap<Section<? extends KnowWEObjectType>, List<Statement>>();

		}
		temp.put(sec, allStatements);
		statementcache.put(sec.getArticle().getTitle(), temp);
	}

	public Statement createStatement(Resource subject, URI predicate, Node object) {
		return model.createStatement(subject, predicate, object);
	}

	public BlankNode createBlankNode(String internalID) {
		return model.createBlankNode(internalID);
	}

	public BlankNode createBlankNode() {
		return model.createBlankNode();
	}

	/**
	 * Dumps the whole content of the model via System.out
	 * 
	 * @created 05.01.2011
	 */
	public void dumpModel() {
		model.dump();
	}

	public void dumpDuplicates() {
		System.out.println("<duplicates>");
		for (Entry<Statement, Integer> e : duplicateStatements.entrySet()) {
			System.out.print(e.getKey() + " #");
			System.out.println(e.getValue());
		}
		System.out.println("</duplicates>");
	}

	public void dumpStatementcache() {
		System.out.println("<statementcache>");
		for (String s : statementcache.keySet()) {
			System.out.println(s + ":");
			for (Section<? extends KnowWEObjectType> sec : statementcache.get(s).keySet()) {
				System.out.println(sec.getID());
				for (Statement l : statementcache.get(s).get(sec)) {
					System.out.println("s:" + l.getSubject() + " p:" + l.getPredicate() + " o:"
							+ l.getObject());
				}
			}
		}
		System.out.println("</statementcache>");
	}

	public void dumpNamespaces() {
		System.out.println("<namespaces>");
		for (Entry<String, String> e : namespaces.entrySet()) {
			System.out.println("Prefix=" + e.getKey() + " URL=" + e.getValue());
		}
		System.out.println("</namespaces>");
	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(1);
		events.add(FullParseEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof FullParseEvent) {
			getInstance().removeArticleStatementsRecursive(((FullParseEvent) event).getArticle());
		}
	}

	public void removeArticleStatementsRecursive(KnowWEArticle art) {
		if (statementcache.get(art.getTitle()) != null) {
			Set<Section<? extends KnowWEObjectType>> temp = new HashSet<Section<? extends KnowWEObjectType>>();
			temp.addAll(statementcache.get(art.getTitle()).keySet());
			for (Section<? extends KnowWEObjectType> cur : temp) {
				removeStatementsofSingleSection(cur);
			}
		}
	}

	public void removeAllCachedStatements() {
		//get all statements of this wiki and remove them from the model
		ArrayList<Statement> allStatements = new ArrayList<Statement>();
		for (WeakHashMap<Section<? extends KnowWEObjectType>,List<Statement>> w : statementcache.values()) {
			for (List<Statement> l : w.values()) {
				allStatements.addAll(l);
			}
		}
		model.removeAll(allStatements.iterator());
		
		//clear statementcache and duplicateStatements
		statementcache.clear();
		duplicateStatements.clear();
	}
	
	public String getSparqlNamespaceShorts() {
		StringBuffer buffy = new StringBuffer();

		for (Entry<String, String> cur : namespaces.entrySet()) {
			buffy.append("PREFIX " + cur.getKey() + ": <" + cur.getValue()
					+ "> \n");
		}
		return buffy.toString();
	}

	public Object getUnderlyingModelImplementation() {
		return model.getUnderlyingModelImplementation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.we.core.ISemanticCore#simpleQueryToList(java.lang.String,
	 * java.lang.String)
	 */
	public ArrayList<String> simpleQueryToList(String inquery,
			String targetbinding) {

		ArrayList<String> resultlist = new ArrayList<String>();
		String querystring = getSparqlNamespaceShorts();
		querystring = querystring + inquery;

		QueryResultTable results = null;
		try {
			results = sparqlSelect(inquery);
		}
		catch (ModelRuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (results != null) {
			ClosableIterator<QueryRow> i = results.iterator();

			while (i.hasNext()) {
				QueryRow row = i.next();
				String tag = row.getValue(targetbinding).toString();
				if (tag.split("#").length == 2) tag = tag.split("#")[1];
				try {
					tag = URLDecoder.decode(tag, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (tag.contains("=")) {
					tag = tag.split("=")[1];
				}
				if (tag.startsWith("\"")) {
					tag = tag.substring(1);
				}
				if (tag.endsWith("\"")) {
					tag = tag.substring(0, tag.length() - 1);
				}
				resultlist.add(tag.trim());
			}
		}
		return resultlist;
	}

	public Literal createLiteral(String text) {
		return model.createPlainLiteral(text);
	}

	public Literal createLiteral(String literal, URI datatypeURI) {
		return model.createDatatypeLiteral(literal, datatypeURI);
	}

	public static String getLocalName(Node o) {
		return RDFTool.getLabel(o);
	}

	public List<Statement> getTopicStatements(String topic) {
		Section<? extends KnowWEObjectType> rootsection = KnowWEEnvironment
				.getInstance().getArticle(KnowWEEnvironment.DEFAULT_WEB, topic)
				.getSection();
		return getSectionStatementsRecursive(rootsection);
	}

	public File[] getImportList() {
		KnowWEEnvironment knowWEEnvironment = KnowWEEnvironment.getInstance();
		String p = knowWEEnvironment.getWikiConnector().getSavePath();
		String inpath = (p != null) ? p : (knowWEEnvironment
				.getKnowWEExtensionPath()
				+ File.separatorChar + "owlincludes");
		File includes = new File(inpath);
		if (includes.exists()) {
			File[] files = includes.listFiles(new FilenameFilter() {

				public boolean accept(File f, String s) {
					return s.endsWith(".owl");
				}
			});
			return files;
		}
		return null;
	}

	/**
	 * @param prop
	 * @return
	 */
	public URI getRDF(String prop) {
		return createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#", prop);
	}

	/**
	 * @param prop
	 * @return
	 */
	public URI getRDFS(String prop) {
		return createURI("http://www.w3.org/2000/01/rdf-schema#", prop);
	}

	public void addStatement(Statement s, Section<? extends KnowWEObjectType> sec) {
		List<Statement> l = new ArrayList<Statement>();
		l.add(s);
		addStatements(l, sec);
	}
}
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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
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
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.event.ArticleUpdatesFinishedEvent;
import de.d3web.we.event.Event;
import de.d3web.we.event.EventListener;
import de.d3web.we.event.EventManager;
import de.d3web.we.event.FullParseEvent;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;

/**
 *
 * @author grotheer
 * @created 29.11.2010
 */
public class Rdf2GoCore implements EventListener {

	public static final String basens = "http://ki.informatik.uni-wuerzburg.de/d3web/we/knowwe.owl#";
	public static final String localns = KnowWEEnvironment.getInstance().getWikiConnector().getBaseUrl()
			+ "OwlDownload.jsp#";

	public static final URI HASTAG = Rdf2GoCore.getInstance().createURI(basens, "hasTag");
	private static final URI HASTOPIC = Rdf2GoCore.getInstance().createURI(basens, "hasTopic");
	private static final URI NARYPROPERTY = Rdf2GoCore.getInstance().createURI(basens,
			"NaryProperty");
	private static final URI TEXTORIGIN = Rdf2GoCore.getInstance().createURI(basens, "TextOrigin");
	private static final URI HASNODE = Rdf2GoCore.getInstance().createURI(basens, "hasNode");

	private static final String JENA = "jena";
	private static final String BIGOWLIM = "bigowlim";
	public static final String SESAME = "sesame";
	private static final String SWIFTOWLIM = "swiftowlim";

	public static final String SELECT = "select";
	public static final String ASK = "ask";
	private static final String OWL_REASONING = "owl";
	private static final String RDFS_REASONING = "rdfs";

	private static Rdf2GoCore me;
	private Model model;
	private HashMap<String, WeakHashMap<Section<? extends Type>, List<Statement>>> statementcache;
	private HashMap<Statement, Integer> duplicateStatements;
	private HashMap<String, String> namespaces;
	private List<Statement> addCache;
	private List<Statement> removeCache;

	/**
	 * Initializes the model and its caches and namespaces
	 */
	public void init() {
		initModel();
		statementcache = new HashMap<String, WeakHashMap<Section<? extends Type>, List<Statement>>>();
		duplicateStatements = new HashMap<Statement, Integer>();

		addCache = new ArrayList<Statement>();
		removeCache = new ArrayList<Statement>();

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

	/**
	 * registers and opens the specified model
	 *
	 * @throws ModelRuntimeException
	 */
	public void initModel() throws ModelRuntimeException, ReasoningNotSupportedException {
		Properties properties = new Properties();
		String path = KnowWEEnvironment.getInstance().getKnowWEExtensionPath();
		try {
			properties.load(new FileInputStream(path + File.separatorChar
					+ "model.properties"));
		}
		catch (IOException e) {
			// this case happens on junit-tests
			properties.put("model", SESAME);
			properties.put("reasoning", RDFS_REASONING);
		}

		String useModel = properties.getProperty("model").toLowerCase();
		String useReasoning = properties.getProperty("reasoning").toLowerCase();

		if (useModel.equals(JENA)) {
			// Jena dependency currently commented out because of clashing
			// lucene version in jspwiki

			// RDF2Go.register(new
			// org.ontoware.rdf2go.impl.jena26.ModelFactoryImpl());
		}
		else if (useModel.equals(BIGOWLIM)) {
			// registers the customized model factory (in memory, owl-max)
			// RDF2Go.register(new
			// de.d3web.we.core.semantic.rdf2go.modelfactory.BigOwlimInMemoryModelFactory());

			// standard bigowlim model factory:
			// RDF2Go.register(new
			// com.ontotext.trree.rdf2go.OwlimModelFactory());
		}
		else if (useModel.equals(SESAME)) {
			RDF2Go.register(new org.openrdf.rdf2go.RepositoryModelFactory());
		}
		else if (useModel.equals(SWIFTOWLIM)) {
			RDF2Go.register(new de.d3web.we.core.semantic.rdf2go.modelfactory.SesameSwiftOwlimModelFactory());
		}
		else {
			throw new ModelRuntimeException("Model not supported");
		}

		if (useReasoning.equals(OWL_REASONING)) {
			model = RDF2Go.getModelFactory().createModel(Reasoning.owl);
		}
		else if (useReasoning.equals(RDFS_REASONING)) {
			model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		}
		else {
			model = RDF2Go.getModelFactory().createModel();
		}

		model.open();

		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"-> RDF2Go model '" + useModel + "' initialized");

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

	private void removeStatementsFromCache(List<Statement> list) {

		String key = "REMOVE: ";
		// logStatements(list, key);

		// model.removeAll(list.iterator());
		removeCache.addAll(list);
	}

	private void addStatementsToCache(List<Statement> list) {
		String key = "INSERT: ";
		// logStatements(list, key);

		// model.addAll(list.iterator());
		addCache.addAll(list);
	}

	private void logStatements(List<Statement> list, String key) {
		StringBuffer buffy = new StringBuffer();
		for (Statement statement : list) {
			buffy.append(statement.toString());
		}
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, key + buffy.toString());
	}

	public URI createURI(String value) {
		return model.createURI(expandNamespace(value));
	}

	public URI createURI(String ns, String value) {
		return createURI(expandNSPrefix(ns) + beautify(value));
	}

	public URI createlocalURI(String value) {
		return createURI(localns, value);
	}

	public URI createBasensURI(String value) {
		return createURI(basens, value);
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
				result += "<td>" + reduceNamespace(s.getValue(var).toString())
						+ "</td>";
			}
			result += "</tr>";
		}
		result += "</table>";
		return result;
	}

	public boolean sparqlAsk(String query) throws ModelRuntimeException, MalformedQueryException {
		if (query.startsWith(getSparqlNamespaceShorts())) {
			return model.sparqlAsk(query);
		}
		return model.sparqlAsk(getSparqlNamespaceShorts() + query);
	}

	public QueryResultTable sparqlSelect(String query) throws ModelRuntimeException, MalformedQueryException {
		if (query.startsWith(getSparqlNamespaceShorts())) {
			return model.sparqlSelect(query);
		}
		return model.sparqlSelect(getSparqlNamespaceShorts() + query);
	}

	public ClosableIterator<QueryRow> sparqlSelectIt(String query) throws ModelRuntimeException, MalformedQueryException {
		return sparqlSelect(query).iterator();
	}

	/**
	 *
	 * @created 06.12.2010
	 * @param s
	 * @return statements of section s (with children)
	 */
	public List<Statement> getSectionStatementsRecursive(Section<? extends Type> s) {
		List<Statement> allstatements = new ArrayList<Statement>();

		if (getStatementsofSingleSection(s) != null) {
			// add statements of this section
			allstatements.addAll(getStatementsofSingleSection(s));
		}

		// walk over all children
		for (Section<? extends Type> current : s.getChildren()) {
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
	public void removeSectionStatementsRecursive(Section<? extends Type> s) {

		removeStatementsofSingleSection(s);

		// walk over all children
		for (Section<? extends Type> current : s.getChildren()) {
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
			Section<? extends Type> sec) {
		WeakHashMap<Section<? extends Type>, List<Statement>> temp = statementcache.get(sec.getArticle().getTitle());
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
	private void removeStatementsofSingleSection(Section<? extends Type> sec) {
		WeakHashMap<Section<? extends Type>, List<Statement>> temp = statementcache.get(sec.getArticle().getTitle());

		if (temp != null) {
			if (temp.containsKey(sec)) {
				List<Statement> statementsOfSection = temp.get(sec);
				List<Statement> removedStatements = new ArrayList<Statement>();

				for (Statement s : statementsOfSection) {

					if (duplicateStatements.containsKey(s)) {
						if (duplicateStatements.get(s) != 1) {
							duplicateStatements.put(s,
									duplicateStatements.get(s) - 1);
						}
						else {
							duplicateStatements.remove(s);
						}
					}
					else {
						removedStatements.add(s);
						// model.removeStatement(s);
					}
				}
				temp.remove(sec);
				if (temp.isEmpty()) {
					statementcache.remove(sec.getArticle().getTitle());
				}
				removeStatementsFromCache(removedStatements);
				// model.removeAll(removedStatements.iterator());
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
	public void addStatements(List<Statement> allStatements,
			Section<? extends Type> sec) {
		Logger.getLogger(this.getClass().getName()).finer(
				"semantic core updating " + sec.getID() + "  " + allStatements.size());

		WeakHashMap<Section<? extends Type>, List<Statement>> temp = statementcache.get(sec.getTitle());
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
		addStatementsToCache(allStatements);
	}

	/**
	 * adds statements to statementcache
	 *
	 * @created 06.12.2010
	 * @param sec
	 * @param allStatements
	 */
	private void addToStatementcache(Section<? extends Type> sec, List<Statement> allStatements) {
		WeakHashMap<Section<? extends Type>, List<Statement>> temp = statementcache.get(sec.getArticle().getTitle());
		if (temp == null) {
			temp = new WeakHashMap<Section<? extends Type>, List<Statement>>();

		}
		temp.put(sec, allStatements);
		statementcache.put(sec.getArticle().getTitle(), temp);
	}

	public Statement createStatement(Resource subject, URI predicate,
			Node object) {
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

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(1);
		events.add(FullParseEvent.class);
		events.add(ArticleUpdatesFinishedEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof FullParseEvent) {
			getInstance().removeArticleStatementsRecursive(((FullParseEvent) event).getArticle());
		}
		if (event instanceof ArticleUpdatesFinishedEvent) {
			getInstance().commit();
		}
	}

	// commits the statements from writeCache and removeCache to the triplestore
	private void commit() {

		// hazard filter

		model.removeAll(removeCache.iterator());
		EventManager.getInstance().fireEvent(new RemoveStatementsEvent(removeCache));

		model.addAll(addCache.iterator());
		EventManager.getInstance().fireEvent(new InsertStatementsEvent(addCache));

		removeCache.clear();
		addCache.clear();
	}

	public void removeArticleStatementsRecursive(KnowWEArticle art) {
		if (statementcache.get(art.getTitle()) != null) {
			Set<Section<? extends Type>> temp = new HashSet<Section<? extends Type>>();
			temp.addAll(statementcache.get(art.getTitle()).keySet());
			for (Section<? extends Type> cur : temp) {
				removeStatementsofSingleSection(cur);
			}
		}
	}

	public void removeAllCachedStatements() {
		// get all statements of this wiki and remove them from the model
		ArrayList<Statement> allStatements = new ArrayList<Statement>();
		for (WeakHashMap<Section<? extends Type>, List<Statement>> w : statementcache.values()) {
			for (List<Statement> l : w.values()) {
				allStatements.addAll(l);
			}
		}
		removeStatementsFromCache(allStatements);

		// clear statementcache and duplicateStatements
		statementcache.clear();
		duplicateStatements.clear();
	}

	public String getSparqlNamespaceShorts() {
		StringBuffer buffy = new StringBuffer();

		for (Entry<String, String> cur : namespaces.entrySet()) {
			buffy.append("PREFIX " + cur.getKey() + ": <" + cur.getValue() + "> \n");
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
	public ArrayList<String> simpleQueryToList(String inquery, String targetbinding) {

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

	public Literal createDatatypeLiteral(String literal, URI datatype) {
		return model.createDatatypeLiteral(literal, datatype);
	}

	public Literal createDatatypeLiteral(String literal, String datatype) {
		return createDatatypeLiteral(literal, createURI(datatype));
	}

	public Literal createLiteral(String literal, URI datatypeURI) {
		return model.createDatatypeLiteral(literal, datatypeURI);
	}

	public static String getLocalName(Node o) {
		return RDFTool.getLabel(o);
	}

	public List<Statement> getTopicStatements(String topic) {
		Section<? extends Type> rootsection = KnowWEEnvironment.getInstance().getArticle(
				KnowWEEnvironment.DEFAULT_WEB, topic).getSection();
		return getSectionStatementsRecursive(rootsection);
	}

	public File[] getImportList() {
		KnowWEEnvironment knowWEEnvironment = KnowWEEnvironment.getInstance();
		String p = knowWEEnvironment.getWikiConnector().getSavePath();
		String inpath = (p != null) ? p : (knowWEEnvironment.getKnowWEExtensionPath()
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

	public void addStatement(Statement s, Section<? extends Type> sec) {
		List<Statement> l = new ArrayList<Statement>();
		l.add(s);
		addStatements(l, sec);
	}

	public void addStatement(Resource subject, URI predicate, Node object, Section<? extends Type> sec) {
		List<Statement> l = new ArrayList<Statement>();
		l.add(createStatement(subject, predicate, object));
		addStatements(l, sec);
	}

	/**
	 * @param cur
	 */
	public List<Statement> createlocalProperty(String cur) {
		URI prop = createlocalURI(cur);
		URI naryprop = NARYPROPERTY;
		List<Statement> io = new ArrayList<Statement>();
		if (!PropertyManager.getInstance().isValid(prop)) {
			io.add(createStatement(prop, RDFS.subClassOf, naryprop));
		}
		return io;
	}

	/**
	 * attaches a TextOrigin Node to a Resource. It's your duty to make sure the
	 * Resource is of the right type if applicable (eg attachto RDF.TYPE
	 * RDF.STATEMENT)
	 *
	 * @param attachto The Resource that will be annotated bei the TO-Node
	 * @param source The source section that should be used
	 * @param io the ex-IntermediateOwlObject (now List<Statements> that should
	 *        collect the statements
	 */
	public void attachTextOrigin(Resource attachto, Section source, List<Statement> io) {
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		io.addAll(createTextOrigin(source, to));
		io.add(createStatement(attachto, RDFS.isDefinedBy, to));
	}

	private List<Statement> createTextOrigin(Section<Type> source, Resource to) {
		ArrayList<Statement> io = new ArrayList<Statement>();
		io.add(createStatement(to, RDF.type, TEXTORIGIN));
		io.add(createStatement(to, HASNODE, createLiteral(source.getID())));
		io.add(createStatement(to, HASTOPIC, createlocalURI(source.getTitle())));
		return io;
	}
}
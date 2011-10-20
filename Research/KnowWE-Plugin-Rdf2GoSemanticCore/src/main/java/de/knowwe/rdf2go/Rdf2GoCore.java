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
package de.knowwe.rdf2go;

import java.io.File;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
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

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.event.ArticleUpdatesFinishedEvent;
import de.knowwe.event.FullParseEvent;

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
	private static final URI HASTOPIC = Rdf2GoCore.getInstance().createURI(basens,
			"hasTopic");
	private static final URI NARYPROPERTY = Rdf2GoCore.getInstance().createURI(basens,
			"NaryProperty");
	private static final URI TEXTORIGIN = Rdf2GoCore.getInstance().createURI(basens,
			"TextOrigin");
	private static final URI HASNODE = Rdf2GoCore.getInstance().createURI(basens,
			"hasNode");

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
	private Map<String, WeakHashMap<Section<? extends Type>, List<Statement>>> statementcache;
	private Map<Statement, Set<String>> duplicateStatements;
	private Map<String, String> namespaces;
	private List<Statement> addCache;
	private List<Statement> removeCache;
	ResourceBundle properties = ResourceBundle.getBundle("model");

	Map<String, WeakHashMap<Section<? extends Type>, List<Statement>>> getStatementCache() {
		return statementcache;
	}

	/**
	 * Initializes the model and its caches and namespaces
	 */
	public void init() {
		initModel();
		statementcache = new HashMap<String, WeakHashMap<Section<? extends Type>, List<Statement>>>();
		duplicateStatements = new HashMap<Statement, Set<String>>();

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

		String useModel = properties.getString("model").toLowerCase();
		String useReasoning = properties.getString("reasoning").toLowerCase();

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
			RDF2Go.register(new de.knowwe.rdf2go.modelfactory.SesameSwiftOwlimModelFactory());
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

	public Map<String, String> getNameSpaces() {
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

	public String renderedSparqlSelect(String query, boolean links) throws ModelRuntimeException, MalformedQueryException {
		return renderQueryResult(sparqlSelect(query), links);
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
		logStatements(list, key);

		// model.removeAll(list.iterator());
		removeCache.addAll(list);
	}

	private void addStatementsToCache(List<Statement> list) {
		String key = "INSERT: ";
		logStatements(list, key);

		// model.addAll(list.iterator());
		addCache.addAll(list);
	}

	private void logStatements(List<Statement> list, String key) {
		StringBuffer buffy = new StringBuffer();
		for (Iterator<Statement> statements = list.iterator(); statements.hasNext();) {
			buffy.append(statements.next().toString());
			if (statements.hasNext()) buffy.append("\n");
		}
		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				key + buffy.toString());
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
	public String renderQueryResult(QueryResultTable qrt, boolean links) {
		boolean tablemode = false;
		boolean empty = true;

		List<String> l = qrt.getVariables();
		ClosableIterator<QueryRow> i = qrt.iterator();
		String result = "";
		if (!tablemode) {
			tablemode = l.size() > 1;
		}
		if (tablemode) {
			result += KnowWEUtils.maskHTML("<table>");
			for (String var : l) {
				result += KnowWEUtils.maskHTML("<th>") + var
						+ KnowWEUtils.maskHTML("</th>");
			}
		}
		else {
			result += KnowWEUtils.maskHTML("<ul>");
		}

		while (i.hasNext()) {
			empty = false;

			if (!tablemode) {
				tablemode = qrt.getVariables().size() > 1;
			}
			QueryRow s = i.next();
			if (tablemode) {
				result += KnowWEUtils.maskHTML("<tr>");
			}
			for (String var : l) {
				String erg = reduceNamespace(s.getValue(var).toString());
				try {
					erg = URLDecoder.decode(erg, "UTF-8");
				}
				catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (links) {
					if (erg.startsWith("lns:")) {
						erg = erg.substring(4);
					}
					try {
						if (KnowWEEnvironment.getInstance()
								.getWikiConnector().doesPageExist(erg)
								|| KnowWEEnvironment.getInstance()
										.getWikiConnector().doesPageExist(
												URLDecoder.decode(erg,
														"UTF-8"))) {
							erg = KnowWEUtils.maskHTML("<a href=\"Wiki.jsp?page=")
											+ erg + KnowWEUtils.maskHTML("\">") + erg
									+ KnowWEUtils.maskHTML("</a>");
						}
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				if (tablemode) {
					result += KnowWEUtils.maskHTML("<td>") + erg
							+ KnowWEUtils.maskHTML("</td>\n");
				}
				else {
					result += KnowWEUtils.maskHTML("<li>") + erg
							+ KnowWEUtils.maskHTML("</li>\n");
				}
			}
			if (tablemode) {
				result += KnowWEUtils.maskHTML("</tr>");
			}
		}

		if (empty) {
			ResourceBundle rb = KnowWEEnvironment.getInstance().getKwikiBundle();
			result = rb.getString("KnowWE.owl.query.no_result");
		}
		else {
			if (tablemode) {
				result += KnowWEUtils.maskHTML("</table>");
			}
			else {
				result += KnowWEUtils.maskHTML("</ul>");
			}
		}
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
		WeakHashMap<Section<? extends Type>, List<Statement>> allStatmentSectionsOfArticle =
				statementcache.get(sec.getTitle());

		if (allStatmentSectionsOfArticle != null) {
			if (allStatmentSectionsOfArticle.containsKey(sec)) {
				List<Statement> statementsOfSection = allStatmentSectionsOfArticle.get(sec);
				List<Statement> removedStatements = new ArrayList<Statement>();

				for (Statement s : statementsOfSection) {
					Set<String> sectionIDsForStatement = duplicateStatements.get(s);
					boolean removed = false;
					if (sectionIDsForStatement != null) {
						removed = sectionIDsForStatement.remove(sec.getID());
					}
					if (removed && sectionIDsForStatement.isEmpty()) {
						removedStatements.add(s);
						duplicateStatements.remove(s);
					}
					else {
						Logger.getLogger(this.getClass().getName()).log(
								Level.INFO,
								"Tried to remove statement from Section '" + sec.get().getName()
										+ "', ' " + sec.getID() + "' that wasn't there:\n"
										+ s.toString());
					}
				}
				allStatmentSectionsOfArticle.remove(sec);
				if (allStatmentSectionsOfArticle.isEmpty()) {
					statementcache.remove(sec.getArticle().getTitle());
				}
				removeStatementsFromCache(removedStatements);
				// model.removeAll(removedStatements.iterator());
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

		for (Statement s : allStatements) {
			Set<String> registeredSectionIDsForStatements = duplicateStatements.get(s);
			if (registeredSectionIDsForStatements == null) {
				registeredSectionIDsForStatements = new HashSet<String>();
				duplicateStatements.put(s, registeredSectionIDsForStatements);
			}
			registeredSectionIDsForStatements.add(sec.getID());
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
	 * @param newStatements
	 */
	private void addToStatementcache(Section<? extends Type> sec, List<Statement> newStatements) {
		WeakHashMap<Section<? extends Type>, List<Statement>> temp = statementcache.get(sec.getArticle().getTitle());
		if (temp == null) {
			temp = new WeakHashMap<Section<? extends Type>, List<Statement>>();
		}
		List<Statement> allStatements = new ArrayList<Statement>();
		allStatements.addAll(newStatements);
		if (temp.containsKey(sec)) {
			allStatements.addAll(temp.get(sec));
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
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(
				2);
		events.add(FullParseEvent.class);
		events.add(ArticleUpdatesFinishedEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof FullParseEvent) {
			if (properties.containsKey("compile")
					&& properties.getString("compile").equals("ignoreFullParse")) {
				// do nothing on full-parse
			}
			else {
				getInstance().removeArticleStatementsRecursive(
						((FullParseEvent) event).getArticle());
			}
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
		WeakHashMap<Section<? extends Type>, List<Statement>> oldStatementsOfArticle =
				statementcache.get(art.getTitle());
		if (oldStatementsOfArticle != null) {
			Set<Section<? extends Type>> sectionsWithStatements =
					new HashSet<Section<? extends Type>>();
			sectionsWithStatements.addAll(oldStatementsOfArticle.keySet());
			for (Section<? extends Type> cur : sectionsWithStatements) {
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

				@Override
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
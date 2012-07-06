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
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterable;
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

import de.knowwe.core.Environment;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;
import de.knowwe.event.ArticleUpdatesFinishedEvent;
import de.knowwe.event.FullParseEvent;

/**
 * 
 * @author grotheer
 * @created 29.11.2010
 */
public class Rdf2GoCore implements EventListener {

	public static final String basens = "http://ki.informatik.uni-wuerzburg.de/d3web/we/knowwe.owl#";
	public static final String localns = Environment.getInstance().getWikiConnector().getBaseUrl()
			+ "OwlDownload.jsp#";

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

	/**
	 * @param value
	 * @return
	 */
	private static String beautify(String value) {
		String temp = value;
		try {
			temp = Strings.decodeURL(value);
		}
		catch (IllegalArgumentException e) {
		}
		return Strings.encodeURL(temp);
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

	public static String getLocalName(Node o) {
		return RDFTool.getLabel(o);
	}

	private static Set<Statement> modelToSet(Model m) {
		HashSet<Statement> result = new HashSet<Statement>();

		for (Statement s : m) {
			result.add(s);
		}

		return result;
	}

	private Model model;

	/**
	 * This statement cache is controlled by the incremental compiler.
	 */
	private Map<String, WeakHashMap<Section<? extends Type>, List<Statement>>> incrementalStatementCache;
	private Map<Statement, Set<String>> duplicateStatements;

	/**
	 * This statement cache gets cleaned with the full parse of an article. If a
	 * full parse on an article is performed, all old statements registered for
	 * this article are removed.
	 */
	private final Map<String, Set<Statement>> fullParseStatementCache = new HashMap<String, Set<Statement>>();

	private Map<String, String> namespaces;

	private TreeSet<Statement> insertCache;

	private TreeSet<Statement> removeCache;

	ResourceBundle properties = ResourceBundle.getBundle("model");

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

	/**
	 * Creates a {@link Statement} for the given objects and adds it to the
	 * triple store. The {@link Section} is used for caching. *
	 * <p/>
	 * You can remove the {@link Statement} using the method
	 * {@link Rdf2GoCore#removeStatementsOfSectionRecursively(Section)}.
	 * 
	 * @created 06.12.2010
	 * @param subject the subject of the statement/triple
	 * @param predicate the predicate of the statement/triple
	 * @param object the object of the statement/triple
	 * @param section the {@link Section} for which the {@link Statement}s are
	 *        added and cached
	 */
	public void addStatement(Resource subject, URI predicate, Node object, Section<? extends Type> sec) {
		addStatement(createStatement(subject, predicate, object), sec);
	}

	/**
	 * Adds the given {@link Statement} for the given {@link Section} to the
	 * triple store.
	 * <p/>
	 * You can remove the {@link Statement} using the method
	 * {@link Rdf2GoCore#removeStatementsOfSectionRecursively(Section)}.
	 * 
	 * @created 06.12.2010
	 * @param statement the {@link Statement}s to add
	 * @param section the {@link Section} for which the {@link Statement}s are
	 *        added and cached
	 */
	public void addStatement(Statement statement, Section<? extends Type> section) {
		List<Statement> l = new ArrayList<Statement>();
		l.add(statement);
		addStatements(l, section);
	}

	/**
	 * Adds the given {@link Statement}s for the given {@link Section} to the
	 * triple store.
	 * <p/>
	 * You can remove the {@link Statement}s using the method
	 * {@link Rdf2GoCore#removeStatementsOfSectionRecursively(Section)}.
	 * 
	 * @created 06.12.2010
	 * @param statements the {@link Statement}s to add
	 * @param section the {@link Section} for which the {@link Statement}s are
	 *        added and cached
	 */
	public void addStatements(Collection<Statement> statements,
			Section<? extends Type> section) {
		Logger.getLogger(this.getClass().getName()).finer(
				"semantic core updating " + section.getID() + "  " + statements.size());

		addStatementsToDuplicatedCache(statements, section.getID());

		addStatementToIncrementalCache(statements, section);

		// Maybe remove duplicates before adding to store, if performance is
		// better
		addStatementsToInsertCache(statements);
	}

	/**
	 * Adds the {@link Statement} for the given article to the triple store. If
	 * the given article is compiled again, all {@link Statement}s added for
	 * this article are removed before the new {@link Statement} are added
	 * again. This method works best when used in a {@link SubtreeHandler}.
	 * 
	 * @created 11.06.2012
	 * @param statement the statement to add to the triple store
	 * @param article the article for which the statement is added, cached and
	 *        for which it is removed at full parse
	 */
	public void addStatement(Statement statement, Article article) {
		ArrayList<Statement> statements = new ArrayList<Statement>(1);
		statements.add(statement);
		addStatements(statements, article);
	}

	/**
	 * Adds the Collection of {@link Statement}s for the given article. If the
	 * given article is compiled again, all {@link Statement}s added for this
	 * article are removed before the new {@link Statement}s are added again.
	 * You don't need to remove the {@link Statement}s yourself. This method
	 * works best when used in a {@link SubtreeHandler}.
	 * 
	 * @created 11.06.2012
	 * @param statements the statements to add to the triple store
	 * @param article the article for which the statements are added and for
	 *        which they are removed at full parse
	 */
	public void addStatements(Collection<Statement> statements, Article article) {
		Set<Statement> statementsOfArticle = fullParseStatementCache.get(article.getTitle());
		if (statementsOfArticle == null) {
			statementsOfArticle = new HashSet<Statement>();
			fullParseStatementCache.put(article.getTitle(), statementsOfArticle);
		}
		statementsOfArticle.addAll(statements);
		addStatementsToDuplicatedCache(statements, article.getTitle());
		addStatementsToInsertCache(statements);
	}

	/**
	 * Adds the given {@link Statement}s directly to the triple store.
	 * <p/>
	 * <b>Attention</b>: The added {@link Statement}s are not cached in the
	 * {@link Rdf2GoCore}, so you are yourself responsible to remove the right
	 * {@link Statement}s in case they are not longer valid. You can remove
	 * these {@link Statement}s with the method
	 * {@link Rdf2GoCore#removeStatements(Collection)}.
	 * 
	 * @created 13.06.2012
	 * @param statements the statements you want to add to the triple store
	 */
	public void addStatements(Collection<Statement> statements) {
		addStatementsToDuplicatedCache(statements, null);
		addStatementsToInsertCache(statements);
	}

	private void addStatementsToDuplicatedCache(Collection<Statement> allStatements, String source) {
		for (Statement s : allStatements) {
			Set<String> registeredSectionIDsForStatements = duplicateStatements.get(s);
			if (registeredSectionIDsForStatements == null) {
				registeredSectionIDsForStatements = new HashSet<String>();
				duplicateStatements.put(s, registeredSectionIDsForStatements);
			}
			registeredSectionIDsForStatements.add(source);
		}
	}

	private void addStatementsToInsertCache(Collection<Statement> list) {
		insertCache.addAll(list);
	}

	/**
	 * Adds statements to the incremental statementcache.
	 * 
	 * @created 06.12.2010
	 * @param newStatements
	 * @param sec
	 */
	private void addStatementToIncrementalCache(Collection<Statement> newStatements, Section<? extends Type> sec) {
		WeakHashMap<Section<? extends Type>, List<Statement>> temp = incrementalStatementCache.get(sec.getArticle().getTitle());
		if (temp == null) {
			temp = new WeakHashMap<Section<? extends Type>, List<Statement>>();
		}
		List<Statement> allStatements = new ArrayList<Statement>();
		allStatements.addAll(newStatements);
		if (temp.containsKey(sec)) {
			allStatements.addAll(temp.get(sec));
		}
		temp.put(sec, allStatements);
		incrementalStatementCache.put(sec.getArticle().getTitle(), temp);
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
	public void attachTextOrigin(Resource attachto, Section<?> source, List<Statement> io) {
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		io.addAll(createTextOrigin(source, to));
		io.add(createStatement(attachto, RDFS.isDefinedBy, to));
	}

	/**
	 * Commit is automatically called every time an article has finished
	 * compiling. When commit is called, all {@link Statement}s that were cached
	 * to be removed from the triple store are removed and all {@link Statement}
	 * s that were cached to be added to the triple store are added.
	 * 
	 * @created 12.06.2012
	 */
	public void commit() {

		// hazard filter

		long start = System.currentTimeMillis();
		model.removeAll(removeCache.iterator());
		EventManager.getInstance().fireEvent(new RemoveStatementsEvent(removeCache));
		logStatements(removeCache, start, "Removed statements:\n");

		start = System.currentTimeMillis();
		model.addAll(insertCache.iterator());
		EventManager.getInstance().fireEvent(new InsertStatementsEvent(insertCache));
		logStatements(insertCache, start, "Inserted statements:\n");

		removeCache = new TreeSet<Statement>();
		insertCache = new TreeSet<Statement>();
	}

	public URI createBasensURI(String value) {
		return createURI(basens, value);
	}

	public BlankNode createBlankNode() {
		return model.createBlankNode();
	}

	public BlankNode createBlankNode(String internalID) {
		return model.createBlankNode(internalID);
	}

	public Literal createDatatypeLiteral(String literal, String datatype) {
		return createDatatypeLiteral(literal, createURI(datatype));
	}

	public Literal createDatatypeLiteral(String literal, URI datatype) {
		return model.createDatatypeLiteral(literal, datatype);
	}

	public Literal createLiteral(String text) {
		return model.createPlainLiteral(text);
	}

	public Literal createLiteral(String literal, URI datatypeURI) {
		return model.createDatatypeLiteral(literal, datatypeURI);
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

	public URI createlocalURI(String value) {
		return createURI(localns, value);
	}

	public Statement createStatement(Resource subject, URI predicate,
			Node object) {
		return model.createStatement(subject, predicate, object);
	}

	private List<Statement> createTextOrigin(Section<?> source, Resource to) {
		ArrayList<Statement> io = new ArrayList<Statement>();
		io.add(createStatement(to, RDF.type, TEXTORIGIN));
		io.add(createStatement(to, HASNODE, createLiteral(source.getID())));
		io.add(createStatement(to, HASTOPIC, createlocalURI(source.getTitle())));
		return io;
	}

	public URI createURI(String value) {
		return model.createURI(expandNamespace(value));
	}

	public URI createURI(String ns, String value) {
		return createURI(expandNSPrefix(ns) + beautify(value));
	}

	/**
	 * Dumps the whole content of the model via System.out
	 * 
	 * @created 05.01.2011
	 */
	public void dumpModel() {
		model.dump();
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
	 * Calculates the Set-subtraction of the inference closure of the model with
	 * and without the statements created by the given section
	 * 
	 * @created 02.01.2012
	 * @param sec
	 * @return
	 * @throws ModelRuntimeException
	 * @throws MalformedQueryException
	 */
	public Collection<Statement> generateStatementDiffForSection(Section<?> sec) throws ModelRuntimeException, MalformedQueryException {

		Set<Statement> includingSection = modelToSet(model);

		// retrieve statements to be excluded
		WeakHashMap<Section<? extends Type>, List<Statement>> allStatmentSectionsOfArticle =
				incrementalStatementCache.get(sec.getTitle());
		List<Statement> statementsOfSection = allStatmentSectionsOfArticle.get(sec);

		// remove these statements
		if (statementsOfSection != null) {
			model.removeAll(statementsOfSection.iterator());
		}
		Set<Statement> excludingSection = modelToSet(model);
		includingSection.removeAll(excludingSection);

		// reinsert statements
		if (statementsOfSection != null) {
			model.addAll(statementsOfSection.iterator());
		}

		return includingSection;

	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(
				2);
		events.add(FullParseEvent.class);
		events.add(ArticleUpdatesFinishedEvent.class);
		return events;
	}

	public File[] getImportList() {
		String p = Environment.getInstance().getWikiConnector().getSavePath();
		String inpath = (p != null) ? p : (KnowWEUtils.getKnowWEExtensionPath()
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

	public Map<String, String> getNameSpaces() {
		return namespaces;
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

	public String getSparqlNamespaceShorts() {
		StringBuilder buffy = new StringBuilder();

		for (Entry<String, String> cur : namespaces.entrySet()) {
			buffy.append("PREFIX " + cur.getKey() + ": <" + cur.getValue() + "> \n");
		}
		return buffy.toString();
	}

	Map<String, WeakHashMap<Section<? extends Type>, List<Statement>>> getStatementCache() {
		return Collections.unmodifiableMap(incrementalStatementCache);
	}

	/**
	 * 
	 * @param sec
	 * @created 06.12.2010
	 * @return statements of section sec (without children)
	 */
	private List<Statement> getStatementsofSingleSection(
			Section<? extends Type> sec) {
		WeakHashMap<Section<? extends Type>, List<Statement>> temp = incrementalStatementCache.get(sec.getArticle().getTitle());
		if (temp != null) {
			return temp.get(sec);
		}
		return new ArrayList<Statement>();
	}

	public List<Statement> getTopicStatements(String topic) {
		Section<? extends Type> rootsection = Environment.getInstance().getArticle(
				Environment.DEFAULT_WEB, topic).getRootSection();
		return getSectionStatementsRecursive(rootsection);
	}

	public Object getUnderlyingModelImplementation() {
		return model.getUnderlyingModelImplementation();
	}

	/**
	 * Initializes the model and its caches and namespaces
	 */
	public void init() {
		initModel();
		incrementalStatementCache = new HashMap<String, WeakHashMap<Section<? extends Type>, List<Statement>>>();
		duplicateStatements = new HashMap<Statement, Set<String>>();

		insertCache = new TreeSet<Statement>();
		removeCache = new TreeSet<Statement>();

		namespaces = new HashMap<String, String>();
		namespaces.putAll(model.getNamespaces());
		initDefaultNamespaces();
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

	private void logStatements(Collection<Statement> collection, long start, String key) {
		if (collection.isEmpty()) return;
		StringBuffer buffy = new StringBuffer();
		for (Statement statement : collection) {
			buffy.append(verbalizeStatement(statement) + "\n");
		}
		buffy.append("Done after " + (System.currentTimeMillis() - start) + "ms");
		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				key + buffy.toString());
	}

	private String verbalizeStatement(Statement statement) {
		String statementVerbalization = reduceNamespace(statement.toString());
		try {
			statementVerbalization = URLDecoder.decode(statementVerbalization, "UTF-8");
		}
		catch (Exception e) {
			// may happen, just ignore...
		}
		return statementVerbalization;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof FullParseEvent) {
			getInstance().removeStatementsOfArticle(((FullParseEvent) event).getArticle());

		}
		if (event instanceof ArticleUpdatesFinishedEvent) {
			getInstance().commit();
		}
	}

	public void readFrom(InputStream in) throws ModelRuntimeException, IOException {
		model.readFrom(in);
	}

	public void readFrom(Reader in) throws ModelRuntimeException, IOException {
		model.readFrom(in);
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

	public void removeAllCachedStatements() {
		// get all statements of this wiki and remove them from the model
		ArrayList<Statement> allStatements = new ArrayList<Statement>();
		for (WeakHashMap<Section<? extends Type>, List<Statement>> w : incrementalStatementCache.values()) {
			for (List<Statement> l : w.values()) {
				allStatements.addAll(l);
			}
		}
		for (Set<Statement> statements : fullParseStatementCache.values()) {
			allStatements.addAll(statements);
		}
		addStatementsToRemoveCache(allStatements);

		// clear statementcache and duplicateStatements
		fullParseStatementCache.clear();
		incrementalStatementCache.clear();
		duplicateStatements.clear();
	}

	public void removeNamespace(String sh) {
		namespaces.remove(sh);
		model.removeNamespace(sh);
	}

	/**
	 * Removes
	 * 
	 * @created 13.06.2012
	 * @param statements
	 */
	public void removeStatements(Collection<Statement> statements) {
		if (statements == null) return;
		List<Statement> removedStatements = new ArrayList<Statement>();
		for (Statement statement : statements) {
			boolean removed = removeStatementFromDuplicateCache(statement, null);
			if (removed) {
				removedStatements.add(statement);
			}
		}
		addStatementsToRemoveCache(removedStatements);
	}

	/**
	 * Removes all {@link Statement}s that were added and cached for the given
	 * {@link Section}.
	 * <p/>
	 * <b>Attention</b>: This method only removes {@link Statement}s that were
	 * added (and cached) in connection with a {@link Section} using methods
	 * like {@link Rdf2GoCore#addStatements(Collection, Section)} or
	 * {@link Rdf2GoCore#addStatement(Statement, Section)}.
	 * 
	 * @created 06.12.2010
	 * @param section the {@link Section} for which the {@link Statement}s
	 *        should be removed
	 */
	public void removeStatementsOfSectionRecursively(Section<? extends Type> section) {

		removeStatementsOfSection(section);

		// walk over all children
		for (Section<? extends Type> current : section.getChildren()) {
			removeStatementsOfSectionRecursively(current);
		}
	}

	private void removeStatementListOfSection(Section<? extends Type> sec) {

		WeakHashMap<Section<? extends Type>, List<Statement>> allStatementSectionsOfArticle =
				incrementalStatementCache.get(sec.getTitle());

		List<Statement> statementsOfSection = allStatementSectionsOfArticle.get(sec);
		List<Statement> removedStatements = new ArrayList<Statement>();

		for (Statement statement : statementsOfSection) {
			boolean removed = removeStatementFromDuplicateCache(statement, sec.getID());
			if (removed) {
				removedStatements.add(statement);
			}
		}
		removeSectionFromIncrementalStatementCache(sec, allStatementSectionsOfArticle);
		addStatementsToRemoveCache(removedStatements);
	}

	private void removeSectionFromIncrementalStatementCache(Section<? extends Type> sec, WeakHashMap<Section<? extends Type>, List<Statement>> allStatementSectionsOfArticle) {
		allStatementSectionsOfArticle.remove(sec);
		if (allStatementSectionsOfArticle.isEmpty()) {
			incrementalStatementCache.remove(sec.getArticle().getTitle());
		}
	}

	private boolean removeStatementFromDuplicateCache(Statement statement, String key) {
		Set<String> sectionIDsForStatement = duplicateStatements.get(statement);
		boolean removed = false;
		if (sectionIDsForStatement != null) {
			removed = sectionIDsForStatement.remove(key);
		}
		else {
			Logger.getLogger(this.getClass().getName()).log(
					Level.WARNING,
					"Internal caching error. Expected statment to be cached with key '" + key
							+ "', but wasn't:\n"
							+ verbalizeStatement(statement));
		}
		if (removed && sectionIDsForStatement.isEmpty()) {
			duplicateStatements.remove(statement);
			return true;
		}
		return false;
	}

	private void addStatementsToRemoveCache(List<Statement> list) {
		removeCache.addAll(list);
	}

	/**
	 * removes statements from statementcache and rdf store
	 * 
	 * @created 06.12.2010
	 * @param sec
	 */
	private void removeStatementsOfSection(Section<? extends Type> sec) {
		WeakHashMap<Section<? extends Type>, List<Statement>> allStatementSectionsOfArticle =
				incrementalStatementCache.get(sec.getTitle());

		if (allStatementSectionsOfArticle != null) {
			if (allStatementSectionsOfArticle.containsKey(sec)) {
				removeStatementListOfSection(sec);
			}
			else {
				// fix: IncrementalCompiler not necessarily delivers the same
				// section object, maybe another with the same content
				Set<Section<? extends Type>> keySet = allStatementSectionsOfArticle.keySet();
				Iterator<Section<? extends Type>> iterator = keySet.iterator();
				while (iterator.hasNext()) {
					Section<? extends Type> section = iterator.next();
					if (section.getText().equals(sec.getText())) {
						removeStatementListOfSection(section);
						break;
					}
				}
			}
		}
	}

	/**
	 * Removes all {@link Statement}s that were added and cached for the given
	 * {@link Article}. This method is automatically called every time an
	 * article is parsed fully ({@link FullParseEvent} fired) so normally you
	 * shouldn't need to call this method yourself.
	 * <p/>
	 * <b>Attention</b>: This method only removes {@link Statement}s that were
	 * added (and cached) in connection with an {@link Article} using methods
	 * like {@link Rdf2GoCore#addStatements(Collection, Article)} or
	 * {@link Rdf2GoCore#addStatement(Statement, Article)}.
	 * 
	 * @created 13.06.2012
	 * @param article the article for which you want to remove all
	 *        {@link Statement}s
	 */
	public void removeStatementsOfArticle(Article article) {
		Set<Statement> statementsOfArticle = fullParseStatementCache.get(article.getTitle());
		if (statementsOfArticle == null) return;
		List<Statement> removedStatements = new ArrayList<Statement>();
		for (Statement statement : statementsOfArticle) {
			boolean removed = removeStatementFromDuplicateCache(statement, article.getTitle());
			if (removed) {
				removedStatements.add(statement);
			}
		}
		addStatementsToRemoveCache(removedStatements);
		fullParseStatementCache.remove(article.getTitle());
	}

	public String renderedSparqlSelect(String query, boolean links) throws ModelRuntimeException, MalformedQueryException {
		return renderQueryResult(sparqlSelect(query), links);
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
			result += Strings.maskHTML("<table>");
			for (String var : l) {
				result += Strings.maskHTML("<th>") + var
						+ Strings.maskHTML("</th>");
			}
		}
		else {
			result += Strings.maskHTML("<ul>");
		}

		while (i.hasNext()) {
			empty = false;

			if (!tablemode) {
				tablemode = qrt.getVariables().size() > 1;
			}
			QueryRow s = i.next();

			if (tablemode) {
				result += Strings.maskHTML("<tr>");
			}
			for (String var : l) {
				Node n = s.getValue(var);
				String erg = "";
				if (n != null) {
					erg = reduceNamespace(s.getValue(var).toString());
				}

				erg = Strings.decodeURL(erg);

				if (links) {
					if (erg.startsWith("lns:")) {
						erg = erg.substring(4);
					}
					if (Environment.getInstance()
							.getWikiConnector().doesArticleExist(erg)
							|| Environment.getInstance()
									.getWikiConnector().doesArticleExist(
											Strings.decodeURL(erg))) {
						erg = Strings.maskHTML("<a href=\"Wiki.jsp?page=")
								+ erg + Strings.maskHTML("\">") + erg
								+ Strings.maskHTML("</a>");
					}
				}

				if (tablemode) {
					result += Strings.maskHTML("<td>") + erg
							+ Strings.maskHTML("</td>\n");
				}
				else {
					result += Strings.maskHTML("<li>") + erg
							+ Strings.maskHTML("</li>\n");
				}

			}
			if (tablemode) {
				result += Strings.maskHTML("</tr>");
			}
		}

		if (empty) {
			result = Messages.getMessageBundle().getString("KnowWE.owl.query.no_result");
		}
		else {
			if (tablemode) {
				result += Strings.maskHTML("</table>");
			}
			else {
				result += Strings.maskHTML("</ul>");
			}
		}
		return result;
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
				tag = Strings.decodeURL(tag);
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

	public boolean sparqlAsk(String query) throws ModelRuntimeException, MalformedQueryException {
		String sparqlNamespaceShorts = getSparqlNamespaceShorts();
		if (query.startsWith(sparqlNamespaceShorts)) {
			return model.sparqlAsk(query);
		}
		return model.sparqlAsk(sparqlNamespaceShorts + query);
	}

	/**
	 * Asks a sparql query on the model as if a specific Section wouldnt be
	 * there. The statements of this section are removed from the model before
	 * the query is executed. Afterwards these statements are inserted again to
	 * provide a consistent model.
	 * 
	 * @created 14.12.2011
	 * @param query the query to be ask
	 * @param sec the section determining the statements to be excluded for the
	 *        query
	 * @return
	 * @throws ModelRuntimeException
	 * @throws MalformedQueryException
	 */
	public boolean sparqlAskExcludeStatementForSection(String query, Section<?> sec) throws ModelRuntimeException, MalformedQueryException {

		// retrieve statements to be excluded
		WeakHashMap<Section<? extends Type>, List<Statement>> allStatmentSectionsOfArticle =
				incrementalStatementCache.get(sec.getTitle());
		List<Statement> statementsOfSection = allStatmentSectionsOfArticle.get(sec);

		// remove these statements
		if (statementsOfSection != null) {
			model.removeAll(statementsOfSection.iterator());
		}

		boolean result;

		// ask query
		if (query.startsWith(getSparqlNamespaceShorts())) {
			result = model.sparqlAsk(query);
		}
		result = model.sparqlAsk(getSparqlNamespaceShorts() + query);

		// reinsert statements
		if (statementsOfSection != null) {
			model.addAll(statementsOfSection.iterator());
		}

		// return query result
		return result;
	}

	public ClosableIterable<Statement> sparqlConstruct(String query) throws ModelRuntimeException, MalformedQueryException {
		if (query.startsWith(getSparqlNamespaceShorts())) {
			return model.sparqlConstruct(query);
		}
		return model.sparqlConstruct(getSparqlNamespaceShorts() + query);
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
	 * Writes the current repository model to the given writer in RDF/XML
	 * format.
	 * 
	 * @created 03.02.2012
	 * @param out
	 * @throws ModelRuntimeException
	 * @throws IOException
	 */
	public void writeModel(Writer out) throws ModelRuntimeException, IOException {
		model.writeTo(out);
	}
}
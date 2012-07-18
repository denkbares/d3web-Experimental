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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.core.Environment;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.event.ArticleUpdatesFinishedEvent;
import de.knowwe.event.FullParseEvent;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
 * 
 * @author grotheer
 * @created 29.11.2010
 */
public class Rdf2GoCore implements EventListener {

	private final String bns;
	private final String lns;

	private static final String NARY_PROPERTY = "NaryProperty";

	public static final String JENA = "jena";
	public static final String BIGOWLIM = "bigowlim";
	public static final String SESAME = "sesame";
	public static final String SWIFTOWLIM = "swiftowlim";

	public static final String SELECT = "select";
	public static final String ASK = "ask";
	public static final String OWL_REASONING = "owl";
	public static final String RDFS_REASONING = "rdfs";

	private static Rdf2GoCore me;

	public static Rdf2GoCore getInstance() {
		if (me == null) {
			me = new Rdf2GoCore();
			me.init();
		}
		return me;
	}

	public Rdf2GoCore() {
		bns = "http://ki.informatik.uni-wuerzburg.de/d3web/we/knowwe.owl#";
		lns = Environment.getInstance().getWikiConnector().getBaseUrl()
				+ "Wiki.jsp?page=";
	}

	/**
	 * @created 15.07.2012
	 * @return all {@link Statement}s of the Rdf2GoCore.
	 */
	public static Set<Statement> getStatements() {
		HashSet<Statement> result = new HashSet<Statement>();

		for (Statement s : getInstance().model) {
			result.add(s);
		}
		return result;
	}

	private Model model;

	/**
	 * This statement cache is controlled by the incremental compiler.
	 */
	private Map<String, WeakHashMap<Section<?>, List<Statement>>> incrementalStatementCache;
	private Map<Statement, Set<String>> duplicateStatements;

	/**
	 * This statement cache gets cleaned with the full parse of an article. If a
	 * full parse on an article is performed, all old statements registered for
	 * this article are removed.
	 */
	private final Map<String, Set<Statement>> fullParseStatementCache = new HashMap<String, Set<Statement>>();

	/**
	 * All namespaces known to KnowWE. Key is the namespace abbreviation, value
	 * is the full namespace, e.g. rdf and
	 * http://www.w3.org/1999/02/22-rdf-syntax-ns#
	 */
	private Map<String, String> namespaces;

	private Set<Statement> insertCache;

	private Set<Statement> removeCache;

	ResourceBundle properties = ResourceBundle.getBundle("model");

	/**
	 * Add a namespace to the model.
	 * 
	 * @param abbreviation the short version of the namespace
	 * @param namespace the namespace (URL)
	 */
	public void addNamespace(String abbreviation, String namespace) {
		namespaces.put(abbreviation, namespace);
		model.setNamespace(abbreviation, namespace);
	}

	/**
	 * Creates a {@link Statement} for the given objects and adds it to the
	 * triple store. The {@link Section} is used for caching.
	 * <p/>
	 * You can remove the {@link Statement} using the method
	 * {@link Rdf2GoCore#removeStatementsForSection(Section)}.
	 * 
	 * @created 06.12.2010
	 * @param subject the subject of the statement/triple
	 * @param predicate the predicate of the statement/triple
	 * @param object the object of the statement/triple
	 * @param section the {@link Section} for which the {@link Statement}s are
	 *        added and cached
	 */
	public void addStatement(Section<?> sec, Resource subject, URI predicate, Node object) {
		addStatements(sec, createStatement(subject, predicate, object));
	}

	/**
	 * Adds the given {@link Statement}s for the given {@link Section} to the
	 * triple store.
	 * <p/>
	 * You can remove the {@link Statement}s using the method
	 * {@link Rdf2GoCore#removeStatementsForSection(Section)}.
	 * 
	 * @created 06.12.2010
	 * @param section the {@link Section} for which the {@link Statement}s are
	 *        added and cached
	 * @param statements the {@link Statement}s to add
	 */
	public void addStatements(Section<? extends Type> section,
			Statement... statements) {
		Logger.getLogger(this.getClass().getName()).finer(
				"semantic core updating " + section.getID() + "  " + statements.length);

		addStatementsToDuplicatedCache(section.getID(), statements);
		addStatementToIncrementalCache(section, statements);
		addStatementsToInsertCache(statements);
	}

	/**
	 * Adds the {@link Statement}s for the given article. If the given article
	 * is compiled again, all {@link Statement}s added for this article are
	 * removed before the new {@link Statement}s are added again. You don't need
	 * to remove the {@link Statement}s yourself. This method works best when
	 * used in a {@link SubtreeHandler}.
	 * 
	 * @created 11.06.2012
	 * @param article the article for which the statements are added and for
	 *        which they are removed at full parse
	 * @param statements the statements to add to the triple store
	 */
	public void addStatements(Article article, Statement... statements) {
		Set<Statement> statementsOfArticle = fullParseStatementCache.get(article.getTitle());
		if (statementsOfArticle == null) {
			statementsOfArticle = new HashSet<Statement>();
			fullParseStatementCache.put(article.getTitle(), statementsOfArticle);
		}
		statementsOfArticle.addAll(Arrays.asList(statements));
		addStatementsToDuplicatedCache(article.getTitle(), statements);
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
	public void addStatements(Statement... statements) {
		addStatementsToDuplicatedCache(null, statements);
		addStatementsToInsertCache(statements);
	}

	private void addStatementsToDuplicatedCache(String source, Statement... statements) {
		for (Statement statement : statements) {
			Set<String> registeredSourcesForStatements = duplicateStatements.get(statement);
			if (registeredSourcesForStatements == null) {
				registeredSourcesForStatements = new HashSet<String>();
				duplicateStatements.put(statement, registeredSourcesForStatements);
			}
			registeredSourcesForStatements.add(source);
		}
	}

	private void addStatementsToInsertCache(Statement... array) {
		insertCache.addAll(Arrays.asList(array));
	}

	/**
	 * Adds statements to the incremental statement cache.
	 */
	private void addStatementToIncrementalCache(Section<?> section, Statement... newStatements) {
		WeakHashMap<Section<?>, List<Statement>> sectionsWithStatements = incrementalStatementCache.get(section.getArticle().getTitle());
		if (sectionsWithStatements == null) {
			sectionsWithStatements = new WeakHashMap<Section<? extends Type>, List<Statement>>();
			incrementalStatementCache.put(section.getTitle(), sectionsWithStatements);
		}
		List<Statement> statementsOfSection = sectionsWithStatements.get(section);
		if (statementsOfSection == null) {
			statementsOfSection = new ArrayList<Statement>();
			sectionsWithStatements.put(section, statementsOfSection);
		}
		statementsOfSection.addAll(Arrays.asList(newStatements));
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
		boolean log = true;

		// For logging...
		TreeSet<Statement> sortedRemoveCache = new TreeSet<Statement>(removeCache);

		// Hazard Filter:
		// Since removing statements is expansive, we do not remove statements
		// that are inserted again anyway.
		// Since inserting a statement is cheap and the fact that a statement in
		// the remove cache has not necessarily been committed to the model
		// before (e.g. compiling the same sections multiple times before the
		// first commit), we do not remove statements from the insert cache.
		// Duplicate statements are ignored by the model anyway.
		removeCache.removeAll(insertCache);

		long start = System.currentTimeMillis();
		model.removeAll(removeCache.iterator());
		EventManager.getInstance().fireEvent(new RemoveStatementsEvent(removeCache));
		if (log) logStatements(sortedRemoveCache, start, "Removed statements:\n");

		start = System.currentTimeMillis();
		model.addAll(insertCache.iterator());
		EventManager.getInstance().fireEvent(new InsertStatementsEvent(insertCache));
		if (log) logStatements(new TreeSet<Statement>(insertCache), start, "Inserted statements:\n");

		removeCache = new HashSet<Statement>();
		insertCache = new HashSet<Statement>();
	}

	public URI createBasensURI(String value) {
		return createURI(bns, value);
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
		List<Statement> io = new ArrayList<Statement>();
		if (!PropertyManager.getInstance().isValid(prop)) {
			io.add(createStatement(prop, RDFS.subClassOf, createURI(getBaseNamespace(),
					NARY_PROPERTY)));
		}
		return io;
	}

	public URI createlocalURI(String value) {
		return createURI(lns, value);
	}

	public Statement createStatement(Resource subject, URI predicate,
			Node object) {
		return model.createStatement(subject, predicate, object);
	}

	public URI createURI(String value) {
		return model.createURI(Rdf2GoUtils.expandNamespace(value));
	}

	public URI createURI(String ns, String value) {
		// in case ns is just the abbreviation
		String fullNs = getNameSpaces().get(ns);

		return createURI((fullNs == null ? ns : fullNs) + Rdf2GoUtils.cleanUp(value));
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
	 * Calculates the Set-subtraction of the inference closure of the model with
	 * and without the statements created by the given section
	 * 
	 * @deprecated this method has some serious flaws since it operates directly
	 *             on the model, removing and adding statements. Example:
	 *             queries to the model performed simultaneously might not
	 *             contain all the results it would contain normally.
	 * @created 02.01.2012
	 * @param sec
	 * @return
	 * @throws ModelRuntimeException
	 * @throws MalformedQueryException
	 */
	@Deprecated
	public Collection<Statement> generateStatementDiffForSection(Section<?> sec) throws ModelRuntimeException, MalformedQueryException {

		Set<Statement> includingSection = getStatements();

		// retrieve statements to be excluded
		WeakHashMap<Section<? extends Type>, List<Statement>> allStatmentSectionsOfArticle =
				incrementalStatementCache.get(sec.getTitle());
		List<Statement> statementsOfSection = allStatmentSectionsOfArticle.get(sec);

		// remove these statements
		if (statementsOfSection != null) {
			model.removeAll(statementsOfSection.iterator());
		}
		Set<Statement> excludingSection = getStatements();
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

	public Map<String, String> getNameSpaces() {
		return namespaces;
	}

	public URI getRDF(String prop) {
		return createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#", prop);
	}

	public URI getRDFS(String prop) {
		return createURI("http://www.w3.org/2000/01/rdf-schema#", prop);
	}

	/**
	 * 
	 * @created 06.12.2010
	 * @param s
	 * @return statements of section s (with children)
	 */
	public List<Statement> getSectionStatementsRecursively(Section<? extends Type> s) {
		List<Statement> allstatements = new ArrayList<Statement>();

		if (getStatementsofSingleSection(s) != null) {
			// add statements of this section
			allstatements.addAll(getStatementsofSingleSection(s));
		}

		// walk over all children
		for (Section<? extends Type> current : s.getChildren()) {
			// collect statements of the the children's descendants
			allstatements.addAll(getSectionStatementsRecursively(current));
		}

		return allstatements;
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

	public Object getUnderlyingModelImplementation() {
		return model.getUnderlyingModelImplementation();
	}

	public String getLocalNamespace() {
		return this.lns;
	}

	public String getBaseNamespace() {
		return bns;
	}

	/**
	 * Initializes the model and its caches and namespaces
	 */
	private void init() {
		initModel();
		incrementalStatementCache = new HashMap<String, WeakHashMap<Section<? extends Type>, List<Statement>>>();
		duplicateStatements = new HashMap<Statement, Set<String>>();

		insertCache = new HashSet<Statement>();
		removeCache = new HashSet<Statement>();

		namespaces = new HashMap<String, String>();
		namespaces.putAll(model.getNamespaces());
		initDefaultNamespaces();
	}

	/**
	 * sets the default namespaces
	 */
	private void initDefaultNamespaces() {
		addNamespace("ns", bns);
		addNamespace("lns", lns);
		addNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		addNamespace("w", "http://www.umweltbundesamt.de/wisec#");
		addNamespace("owl", "http://www.w3.org/2002/07/owl#");
		addNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		addNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
	}

	/**
	 * Registers and opens the specified model.
	 * 
	 * @throws ModelRuntimeException
	 */
	private void initModel() throws ModelRuntimeException, ReasoningNotSupportedException {

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

	private void logStatements(TreeSet<Statement> statements, long start, String key) {
		// sort statements at this point using tree map
		if (statements.isEmpty()) return;
		StringBuffer buffy = new StringBuffer();
		for (Statement statement : statements) {
			buffy.append(verbalizeStatement(statement) + "\n");
		}
		buffy.append("Done after " + (System.currentTimeMillis() - start) + "ms");
		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				key + buffy.toString());
	}

	private String verbalizeStatement(Statement statement) {
		String statementVerbalization = Rdf2GoUtils.reduceNamespace(statement.toString());
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
	 * like {@link Rdf2GoCore#addStatements(Section, Collection)} or
	 * {@link Rdf2GoCore#addStatement(Statement, Section)}.
	 * 
	 * @created 06.12.2010
	 * @param section the {@link Section} for which the {@link Statement}s
	 *        should be removed
	 */
	public void removeStatementsForSection(Section<? extends Type> sec) {

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
	 * Removes all {@link Statement}s that were added and cached for the given
	 * {@link Article}. This method is automatically called every time an
	 * article is parsed fully ({@link FullParseEvent} fired) so normally you
	 * shouldn't need to call this method yourself.
	 * <p/>
	 * <b>Attention</b>: This method only removes {@link Statement}s that were
	 * added (and cached) in connection with an {@link Article} using methods
	 * like {@link Rdf2GoCore#addStatements(Article, Collection)} or
	 * {@link Rdf2GoCore#addStatement(Article, Statement)}.
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

	public boolean sparqlAsk(String query) throws ModelRuntimeException, MalformedQueryException {
		String sparqlNamespaceShorts = Rdf2GoUtils.getSparqlNamespaceShorts();
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
		if (query.startsWith(Rdf2GoUtils.getSparqlNamespaceShorts())) {
			result = model.sparqlAsk(query);
		}
		result = model.sparqlAsk(Rdf2GoUtils.getSparqlNamespaceShorts() + query);

		// reinsert statements
		if (statementsOfSection != null) {
			model.addAll(statementsOfSection.iterator());
		}

		// return query result
		return result;
	}

	public ClosableIterable<Statement> sparqlConstruct(String query) throws ModelRuntimeException, MalformedQueryException {
		if (query.startsWith(Rdf2GoUtils.getSparqlNamespaceShorts())) {
			return model.sparqlConstruct(query);
		}
		return model.sparqlConstruct(Rdf2GoUtils.getSparqlNamespaceShorts() + query);
	}

	public QueryResultTable sparqlSelect(String query) throws ModelRuntimeException, MalformedQueryException {
		String completeQuery;
		if (!query.startsWith(Rdf2GoUtils.getSparqlNamespaceShorts())) {
			completeQuery = Rdf2GoUtils.getSparqlNamespaceShorts() + query;
		}
		else {
			completeQuery = query;
		}
		// long start = System.currentTimeMillis();
		QueryResultTable resultTable = model.sparqlSelect(completeQuery);
		// long time = System.currentTimeMillis() - start;
		// if (time > 5) {
		// Logger.getLogger(this.getClass().getName()).warning(
		// "Slow SPARQ query (" + time + "ms):\n" + query);
		// }
		return resultTable;
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
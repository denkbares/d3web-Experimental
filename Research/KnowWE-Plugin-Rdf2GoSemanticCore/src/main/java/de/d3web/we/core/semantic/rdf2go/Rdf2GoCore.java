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
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.exception.ReasoningNotSupportedException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

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
	private static final String JENA = "jena";
	private static final String BIGOWLIM = "bigowlim";
	private static final String SESAME = "sesame";

	// use JENA, BIGOWLIM or SESAME:
	private static String USE_MODEL = JENA;
	
	// use Reasoning.owl, Reasoning.rdfs, Reasoning.rdfsAndOwl or Reasoning.none:
	private static Reasoning USE_REASONING = Reasoning.none;

	private static Rdf2GoCore me;
	private Model model;
	private HashMap<String, WeakHashMap<Section, List<Statement>>> statementcache;
	private HashMap<Statement, Integer> duplicateStatements;

	/**
	 * Initializes the model and its caches and namespaces
	 */
	public void init() {
		initModel();
		statementcache = new HashMap<String, WeakHashMap<Section, List<Statement>>>();
		duplicateStatements = new HashMap<Statement, Integer>();
		initNamespaces();
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
	 * registers the sesame model, it has no owl-reasoning support
	 * @throws ReasoningNotSupportedException 
	 */
	private void registerSesameModel() throws ReasoningNotSupportedException {
		System.out.print("Sesame 2.3");
		if (USE_REASONING == Reasoning.owl) {
			throw new ReasoningNotSupportedException();
		}
		RDF2Go.register(new org.openrdf.rdf2go.RepositoryModelFactory());
	}

	/**
	 * registers and opens the specified model
	 * @throws ModelRuntimeException 
	 */
	public void initModel() throws ModelRuntimeException {
		if (USE_MODEL == JENA) {
			registerJenaModel();
		}
		else if (USE_MODEL == BIGOWLIM) {
			registerBigOWLIMModel();
		}
		else if (USE_MODEL == SESAME){
			registerSesameModel();
		} else {
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
	private void initNamespaces() {
		// TODO
		model.setNamespace("lns", "http://localhost/rdf.xml#");
	}

	/**
	 * add a namespace to the model
	 * @param sh prefix
	 * @param ns url
	 */
	public void addNamespace(String sh, String ns) {
		model.setNamespace(sh, ns);
	}

	public URI createURI(String str) {
		return model.createURI(expandNamespace(str));
	}

	/**
	 * expands prefix to namespace
	 * 
	 * @created 06.12.2010
	 * @param ns
	 * @return
	 */
	public String expandNSPrefix(String ns) {
		for (Entry<String, String> cur : model.getNamespaces().entrySet()) {
			if (ns.equals(cur.getKey())) {
				ns = cur.getValue();
				break;
			}
		}
		return ns;
	}

	/**
	 * expands namespace in uri string to prefix
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
	 * reduces namespace in uri string to prefix
	 * 
	 * @created 06.12.2010
	 * @param s
	 * @return
	 */
	public String reduceNamespace(String s) {
		for (Entry<String, String> cur : model.getNamespaces().entrySet()) {
			s = s.replaceAll(cur.getValue(), cur.getKey() + ":");
		}
		return s;

	}

	public String renderedSparqlSelect(String query) {
		return render(sparqlSelect(query));
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

	public boolean sparqlAsk(String query) {
		return model.sparqlAsk(query);
	}

	public QueryResultTable sparqlSelect(String query) {
		return model.sparqlSelect(query);
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
		WeakHashMap<Section, List<Statement>> temp = statementcache.get(sec
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
		WeakHashMap<Section, List<Statement>> temp = statementcache.get(sec
					.getArticle().getTitle());
		System.out.println("removing statements of section " + sec.getID());
		if (temp != null) {
			if (temp.containsKey(sec)) {
				List<Statement> statementsOfSection = temp.get(sec);
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
						model.removeStatement(s);
					}
				}
				temp.remove(sec);
				if (temp.isEmpty()) {
					statementcache.remove(sec.getArticle().getTitle());
				}
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
	public void addStatements(List<Statement> allStatements, Section sec) {
		Logger.getLogger(this.getClass().getName()).finer(
				"semantic core updating " + sec.getID() + "  "
						+ allStatements.size());

		WeakHashMap<Section, List<Statement>> temp = statementcache.get(sec.getTitle());
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

	/**
	 * adds statements to rdf store
	 * 
	 * @created 06.12.2010
	 * @param allStatements
	 * @param sec
	 */
	public void addStaticStatements(List<Statement> allStatements, Section sec) {
		Iterator i = allStatements.iterator();
		model.addAll(i);
	}

	/**
	 * adds statements to statementcache
	 * 
	 * @created 06.12.2010
	 * @param sec
	 * @param allStatements
	 */
	private void addToStatementcache(Section sec, List<Statement> allStatements) {
		WeakHashMap<Section, List<Statement>> temp = statementcache.get(sec
				.getArticle().getTitle());
		if (temp == null) {
			temp = new WeakHashMap<Section, List<Statement>>();

		}
		temp.put(sec, allStatements);
		statementcache.put(sec.getArticle().getTitle(), temp);
	}

	public Statement createStatement(Resource subject, URI predicate, Node object) {
		return model.createStatement(subject, predicate, object);
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
		for (Entry e : duplicateStatements.entrySet()) {
			System.out.print(e.getKey() + " #");
			System.out.println(e.getValue());
		}
		System.out.println("</duplicates>");
	}

	public void dumpStatementcache() {
		System.out.println("<statementcache>");
		for (String s : statementcache.keySet()) {
			System.out.println(s + ":");
			for (Section sec : statementcache.get(s).keySet()) {
				System.out.println(sec.getID());
				for (Statement l : statementcache.get(s).get(sec)) {
					System.out.println("s:" + l.getSubject() + " p:" + l.getPredicate() + " o:"
							+ l.getObject());
				}
			}
		}
		System.out.println("</statementcache>");
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
			if (statementcache != null) {
				getInstance().removeArticleStatementsRecursive(
						((FullParseEvent) event).getArticle());
			}
		}
	}

	public void removeArticleStatementsRecursive(KnowWEArticle art) {
		if (statementcache.get(art.getTitle()) != null) {
			Set<Section> temp = new HashSet<Section>();
			temp.addAll(statementcache.get(art.getTitle()).keySet());
			for (Section cur : temp) {
				removeStatementsofSingleSection(cur);
			}
		}
	}
}
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;

/**
 * 
 * @author grotheer
 * @created 29.11.2010
 */
public class Rdf2GoCore {

	private static String USE_MODEL = "";
	private static Reasoning USE_REASONING = Reasoning.owl;

	private static Rdf2GoCore me;
	private Model model;
	private HashMap<String, WeakHashMap<Section, List<Statement>>> statementcache;
	private HashMap<Statement, Integer> duplicateStatements;

	public static void duplicatesOut() {
		System.out.println("Duplicates:");
		for (Entry e : me.duplicateStatements.entrySet()) {
			System.out.print(e.getKey() + " #");
			System.out.println(e.getValue());
		}
	}

	public void init() {
		if (me == null) {
			me = this;

			me.initModel();
			me.statementcache = new HashMap<String, WeakHashMap<Section, List<Statement>>>();
			me.duplicateStatements = new HashMap<Statement, Integer>();
			initNamespaces();
		}
	}

	public static Rdf2GoCore getInstance() {
		return me;
	}

	/**
	 * 
	 * should not be needed, all needed functions should be implemented in
	 * Rdf2GoCore.
	 * 
	 * @return
	 */
	@Deprecated
	public Model getModel() {
		KnowWEEnvironment.maskHTML("");
		return model;
	}

	private void registerJenaModel() {
		System.out.print("Jena 2.6");
		RDF2Go.register(new org.ontoware.rdf2go.impl.jena26.ModelFactoryImpl());
	}

	private void registerOwlimModel() {
		System.out.print("Owlim");
		RDF2Go.register(new com.ontotext.trree.rdf2go.OwlimModelFactory());
	}

	private void registerSesameModel() {
		System.out.print("Sesame 2.3");
		RDF2Go.register(new org.openrdf.rdf2go.RepositoryModelFactory());
	}

	public void initModel() {
		if (USE_MODEL == "jena") {
			registerJenaModel();
		}
		else if (USE_MODEL == "owlim") {
			registerOwlimModel();
		}
		else {
			registerSesameModel();
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

	private void initNamespaces() {
		model.setNamespace("lns", "http://localhost/rdf.xml#");
	}

	public void addNamespace(String sh, String ns) {
		model.setNamespace(sh, ns);
	}

	@Deprecated
	public void addStatement(Resource sub, URI pred, String obj) {
		model.addStatement(sub, pred, obj);
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
		return renderedSparqlSelect(sparqlSelect(query));
	}

	/**
	 * 
	 * @created 06.12.2010
	 * @param qrt
	 * @return html table with all results of qrt
	 */
	public String renderedSparqlSelect(QueryResultTable qrt) {
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
		if (temp != null) {
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

				model.removeStatement(s);
			}
			temp.remove(sec);
			if (temp.isEmpty()) {
				statementcache.remove(sec.getArticle().getTitle());
			}
		}
	}

	/**
	 * adds statements to statementcache and rdf store
	 * 
	 * @created 06.12.2010
	 * @param allStatements
	 * @param sec
	 */
	public void addStatements(List<Statement> allStatements, Section sec) {
		// List<Statement> allStatements = inputio.getAllStatements();
		// clearContext(sec);
		addToStatementcache(sec, allStatements);

		Logger.getLogger(this.getClass().getName()).finer(
				"semantic core updating " + sec.getID() + "  "
						+ allStatements.size());

		List<Statement> currentDuplicates = new ArrayList<Statement>();

		for (Statement s : allStatements) {

			if (model.contains(s)) {
				if (duplicateStatements.containsKey(s)) {
					duplicateStatements.put(s, duplicateStatements.get(s) + 1);
				}
				else {
					duplicateStatements.put(s, 1);
				}
				currentDuplicates.add(s);
			}
		}
		allStatements.removeAll(currentDuplicates);
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
}

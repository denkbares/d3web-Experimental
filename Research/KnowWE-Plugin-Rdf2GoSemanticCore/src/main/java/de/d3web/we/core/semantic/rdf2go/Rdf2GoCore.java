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
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;

/**
 * 
 * @author grotheer
 * @created 29.11.2010
 */
public class Rdf2GoCore {

	private static Rdf2GoCore me;
	private Model model;
	private static HashMap<String, WeakHashMap<Section, List<Statement>>> statementcache;
	private static HashMap<Statement,Integer> duplicateStatements;

	public void init() {
		if (me == null) {
			me = this;

			initModel();
			statementcache = new HashMap<String, WeakHashMap<Section, List<Statement>>>();
			// initNamespaces();
			System.out.println("model initialized");
			System.out.println(model.getUnderlyingModelImplementation().toString());
		}
	}

	public static Rdf2GoCore getInstance() {
		return me;
	}

	public Model getModel() {
		return model;
	}

	public void initModel() {
		// Uncomment one of these lines to activate the specified triple store adapter.
		// By default the sesame adapter is used
		
		//Jena
		//RDF2Go.register(new org.ontoware.rdf2go.impl.jena26.ModelFactoryImpl());
		
		//Owlim
		//RDF2Go.register(new com.ontotext.trree.rdf2go.OwlimModelFactory());
		
		model = RDF2Go.getModelFactory().createModel();
		model.open();
		initNamespaces();
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
		return model.createURI(str);
	}
	
	/**
	 * expands prefix to namespace
	 * @created 06.12.2010
	 * @param ns
	 * @return 
	 */
	public String expandNamespace(String ns) {
		for (Entry<String, String> cur : model.getNamespaces().entrySet()) {
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
	public static String renderedSparqlSelect(QueryResultTable qrt) {
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
				result += "<td>" + s.getValue(var) + "</td>";
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
						duplicateStatements.put(s, duplicateStatements.get(s)-1);
					} else {
						duplicateStatements.remove(s);
					}
				} else {
					model.removeStatement(s);
				}
			}
			temp.remove(sec);

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

		for (Statement s : allStatements) {
			if (model.contains(s)) {
				if (duplicateStatements.containsKey(s)) {
					duplicateStatements.put(s, duplicateStatements.get(s)+1);
				} else {
					duplicateStatements.put(s, 1);
				}
				allStatements.remove(s);
				
			}
		}
		
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
}

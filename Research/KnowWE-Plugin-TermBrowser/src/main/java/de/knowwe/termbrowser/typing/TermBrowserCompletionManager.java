/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.knowwe.termbrowser.typing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.jetbrains.annotations.NotNull;
import org.openrdf.repository.RepositoryException;

import com.denkbares.semantictyping.CompletionResult;
import com.denkbares.semantictyping.LuceneCompleter;
import com.denkbares.semantictyping.SparqlCompletionProvider;
import com.denkbares.util.lucene.LuceneUtils;
import de.d3web.utils.Log;
import de.d3web.utils.Stopwatch;
import de.knowwe.core.compile.CompilerRemovedEvent;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.ontology.compile.OntologyCompilerFinishedEvent;
import de.knowwe.semanticcore.SemanticCoreWrapper;

/**
 * Manages the completions for the different completion slots in KnowWE.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 19.05.15
 */
public class TermBrowserCompletionManager implements EventListener {

	private OntologyCompiler compiler;

	private LuceneCompleter completer = null;

	private boolean initializationRunning = false;


	private static final Map<OntologyCompiler, TermBrowserCompletionManager> instances = new ConcurrentHashMap<>();

	private TermBrowserCompletionManager(OntologyCompiler compiler) {
		this.compiler = compiler;
		EventManager.getInstance().registerListener(this);
	}

	private String[] getAllQueries() {
		return new String[] {
				getClassesQuery(),
				getClassesSynonymsQuery(),
				getInstancesTypesQuery(),
				getInstancesSynonyms(),
				getInstancesSynonymsQuery(),
				getPropertiesQuery(),
				getPropertiesSynonymsQuery(),
		};
	}

	private LuceneCompleter getCompleter() {
		if (completer == null) init();
		return completer;
	}

	@NotNull
	public CompletionResult getCompletions(String phrase, int maxResults) throws RepositoryException, IOException, InterruptedException {
		Locale[] locales = { Locale.ENGLISH, Locale.GERMAN };
		long start = System.currentTimeMillis();
		CompletionResult completionResult = getCompleter().complete(phrase, locales).limit(maxResults);
		Log.info("Fetched completions for phrase '" + phrase + "' in " + (System.currentTimeMillis() - start) + "ms");
		return completionResult;
	}

	public static TermBrowserCompletionManager getInstance(OntologyCompiler compiler) {
		return instances.computeIfAbsent(compiler, TermBrowserCompletionManager::new);
	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		return Arrays.asList(OntologyCompilerFinishedEvent.class, CompilerRemovedEvent.class);
	}

	@Override
	public void notify(Event event) {
		if (event instanceof OntologyCompilerFinishedEvent) {
			if (((OntologyCompilerFinishedEvent) event).getCompiler() == compiler) {

				init();

			}
		}
		else if (event instanceof CompilerRemovedEvent) {
			if (((CompilerRemovedEvent) event).getCompiler() == compiler) {
				instances.remove(compiler);
				EventManager.getInstance().unregister(this);
			}
		}
	}

	private synchronized void init() {
		this.initializationRunning = true;
		try {
			SemanticCoreWrapper core = SemanticCoreWrapper.get(compiler);
			Stopwatch watch = new Stopwatch();
			completer = createLuceneCompleter(core);
			watch.show("Autocompletion init took");
		}
		catch (RepositoryException | IOException | InterruptedException e) {
			Log.severe("Exception while initializing EDB completions");
		}
		this.initializationRunning = false;
	}

	private String getClassesQuery() {
		return "  SELECT ?uri ?name ?typeURI ?typeName ?synonym ?parent\n" +
				"  WHERE {\n" +
				"    ?uri rdf:type|<http://denkbares.com/SemanticServiceCore#type> rdfs:Class .\n" +
				"    BIND  ( rdfs:Class AS ?typeURI ) .\n" +
				"    BIND  ( \"Class\" AS ?typeName ) .\n" +
				"  OPTIONAL {\n" +
				"    ?uri skos:altLabel|rdfs:label|skos:prefLabel   ?label .\n" +
				"	}\n" +
				"    BIND ( IF (BOUND(?label), ?label, str(?uri) )  as ?name  ) .\n " +
				"    BIND (?name AS ?synonym) . \n" +
				"    OPTIONAL { ?uri rdfs:subClassOf ?parent .  FILTER (?uri != ?parent ) .}\n" +
				"  }";
	}

	private String getClassesSynonymsQuery() {
		return "  SELECT ?uri ?synonym \n" +
				"  WHERE {\n" +
				"    ?uri rdf:type|<http://denkbares.com/SemanticServiceCore#type> rdfs:Class .\n" +
				"    ?uri rdfs:label ?synonym .\n" +
				"  }";
	}

	private String getPropertiesQuery() {
		return "  SELECT ?uri ?name ?typeURI ?typeName ?synonym ?parent ?priority\n" +
				"  WHERE {\n" +
				"    ?uri rdf:type rdf:Property .\n" +
				"    BIND  ( rdf:Property AS ?typeURI ) .\n" +
				"    BIND  ( \"Property\" AS ?typeName ) .\n" +
				"  OPTIONAL {\n" +
				"    ?uri skos:altLabel|rdfs:label|skos:prefLabel   ?label .\n" +
				"	}\n" +
				"    BIND ( IF (BOUND (?label), ?label, str(?uri) )  as ?name  ) .\n " +
				"    BIND (?name AS ?synonym) . \n" +
				"    OPTIONAL { ?uri rdfs:  ?parent .  FILTER (?uri != ?parent ) .}\n" +
				"  }";
	}

	private String getPropertiesSynonymsQuery() {
		return "  SELECT ?uri ?synonym \n" +
				"  WHERE {\n" +
				"    ?uri rdf:type rdf:Property .\n" +
				"    ?uri rdfs:label ?synonym .\n" +
				"  }";
	}

	private String getInstancesTypesQuery() {
		return "SELECT ?uri ?typeURI ?typeName ?parent ?priority\n" +
				"  WHERE {\n" +
				"    ?uri rdf:type|<http://denkbares.com/SemanticServiceCore#type>  \t  ?typeURI .\n" +
				"    FILTER ( ?typeURI != rdf:Resource ) .\n" +
				"	FILTER NOT EXISTS { ?uri rdf:type rdf:Property . } \n" +
				"	FILTER NOT EXISTS { ?uri rdf:type rdfs:Class . } \n" +
				"	FILTER (!isBlank(?uri)). \n" +
				"  OPTIONAL {\n" +
				"    ?typeURI skos:altLabel|rdfs:label|skos:prefLabel   ?typeLabel .\n" +
				"	}\n" +
				"    BIND ( IF (BOUND (?typeLabel), ?typeLabel, str(?typeURI) )  as ?typeName  ) .\n " +
				"    OPTIONAL { ?uri skos:broader   ?parent .  FILTER (?uri != ?parent ) .}\n" +
				"  }";
	}

	private String getInstancesSynonyms() {
		return "SELECT ?uri ?name ?synonym\n" +
				"  WHERE {\n" +
				"    ?uri rdf:type|<http://denkbares.com/SemanticServiceCore#type>  \t  ?typeURI .\n" +
				"    FILTER ( ?typeURI != rdf:Resource ) .\n" +
				"	FILTER NOT EXISTS { ?uri rdf:type rdf:Property . } \n" +
				"	FILTER NOT EXISTS { ?uri rdf:type rdfs:Class . } \n" +
				"	FILTER (!isBlank(?uri)). \n" +
				"  OPTIONAL {\n" +
				"    ?uri skos:altLabel|rdfs:label|skos:prefLabel   ?label .\n" +
				"	}\n" +
				"    BIND ( IF (BOUND (?label), ?label, str(?uri) )  as ?name  ) .\n " +
				"    BIND (?name AS ?synonym) . \n" +
				"  }";
	}

	private String getInstancesSynonymsQuery() {
		return "SELECT ?uri ?typeName ?synonym\n" +
				"  WHERE {\n" +
				"    ?uri rdf:type|<http://denkbares.com/SemanticServiceCore#type>  \t  ?typeURI .\n" +
				"    ?uri rdfs:label ?synonym .\n" +
				"    ?typeURI	rdfs:label ?typeName .\n" +
				"    FILTER ( ?typeURI != rdf:Resource ) .\n" +
				"  }";
	}

	private LuceneCompleter createLuceneCompleter(SemanticCoreWrapper core) throws IOException, InterruptedException {
		boolean addURIasSynonym = true;
		boolean filterOutTypeConceptsForCompletion = false;
		SparqlCompletionProvider provider = new SparqlCompletionProvider(core.getSparqlEndpoint(), getAllQueries(), addURIasSynonym, filterOutTypeConceptsForCompletion);
		long start = System.currentTimeMillis();
		Directory dir = new RAMDirectory();
		LuceneCompleter.createIndex(dir, LuceneUtils.TermAnalyzer.standard, provider);
		Log.info("Created TermBrowser Completion lucene index in " + (System.currentTimeMillis() - start) + "ms");
		return new LuceneCompleter(DirectoryReader.open(dir), LuceneCompleter.SearchMode.INFIX_OPTIMIZED);
	}

	public boolean isInitializationRunning() {
		return initializationRunning;
	}
}

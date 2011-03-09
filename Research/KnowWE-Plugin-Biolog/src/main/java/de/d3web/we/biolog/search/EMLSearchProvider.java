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

package de.d3web.we.biolog.search;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.biolog.freemap.SubmethodExpander;
import de.d3web.we.search.GenericSearchResult;
import de.d3web.we.search.KnowWESearchProvider;
import de.d3web.we.search.SearchTerm;
import de.d3web.we.user.UserContext;
import de.knowwe.semantic.sparql.SPARQLUtil;

/**
 *
 * SearchProvider to search the EML-data that has been formalized by the
 * EMLXMLType (@see EMLXMLType).
 *
 * @author Jochen
 * @created 16.09.2010
 */
public class EMLSearchProvider implements KnowWESearchProvider {

	private static EMLSearchProvider instance = null;

	public static EMLSearchProvider getInstance() {
		if (instance == null) {
			instance = new EMLSearchProvider();

		}
		return instance;
	}

	@Override
	public String getID() {
		return "EMLSearch";
	}

	@Override
	public String getVerbalization(Locale local) {
		return "BIOLOG Methoden";
	}

	@Override
	public String renderResults(Collection<GenericSearchResult> localResults, String queryString) {
		StringBuffer resultBuffy = new StringBuffer();

		for (GenericSearchResult genericSearchResult : localResults) {
			if (genericSearchResult.getContexts().length > 0) {
				String topic = genericSearchResult.getPagename();
				try {
					topic = URLDecoder.decode(topic, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				resultBuffy
						.append("<strong><a target='_blank' href='Wiki.jsp?page="
								+ topic + "' >" + StringEscapeUtils.escapeHtml(topic)
								+ ": </a> </strong>");
				resultBuffy.append(StringEscapeUtils.escapeHtml(genericSearchResult.getContexts()[0]));

			}
			else {
				resultBuffy.append("<no context>");
			}
			resultBuffy.append("<br />");
		}

		return resultBuffy.toString();
	}

	private static final String KEYWORD_SPARQL = "SELECT ?eml WHERE { ?eml rdf:type <http://swrc.ontoware.org/ontology#ResearchProject> .  }";

	// a query matching a keyword or the abstract with the searchword
	private static final String KEYWORD_SPARQL3 = "SELECT ?eml ?abstract WHERE { ?eml rdf:type <http://swrc.ontoware.org/ontology#ResearchProject> . ?eml <http://swrc.ontoware.org/ontology#abstract> ?abstract . ?eml <http://swrc.ontoware.org/ontology#creator> ?creator .?eml <http://swrc.ontoware.org/ontology#keywords> ?keyword . FILTER (regex(?keyword, \"SEARCHWORD\", \"i\" ) || regex(?abstract, \"SEARCHWORD\", \"i\") || regex(?creator, \"SEARCHWORD\", \"i\" )) }";

	private static final String KEYWORD_SPARQL2 = "SELECT ?keyword WHERE { <EML_URI> <http://swrc.ontoware.org/ontology#keywords> ?keyword . }";

	private static final String METHOD_SPARQL = "SELECT ?eml ?abstract WHERE { ?eml rdf:type <http://swrc.ontoware.org/ontology#ResearchProject> . ?eml <http://swrc.ontoware.org/ontology#abstract> ?abstract . ?eml <http://swrc.ontoware.org/ontology#description> ?description   .  FILTER regex(?description, \"SEARCHWORD\", \"i\" )}";
	private static final String EML_TOPIC = "SELECT  ?topic ?to ?kdomid WHERE {  EML_URI rdfs:isDefinedBy ?to . ?to ns:hasTopic ?topic . ?to ns:hasNode ?kdomid . }";

	@Override
	public Collection<GenericSearchResult> search(Collection<SearchTerm> words,
			UserContext context) {
		Set<GenericSearchResult> results = new HashSet<GenericSearchResult>();

		// looking for keywords and abstracts and creators
		for (SearchTerm searchTerm : words) {
			TupleQueryResult executeTupleQuery = null;
			executeTupleQuery = SPARQLUtil
					.executeTupleQuery(KEYWORD_SPARQL3.replaceAll("SEARCHWORD",
							searchTerm.getTerm()));

			if (executeTupleQuery != null) {
				try {
					while (executeTupleQuery.hasNext()) {
						BindingSet next = executeTupleQuery.next();
						Binding b = next.getBinding("eml");
						String emlURI = b.getValue().stringValue();
						Binding abstractB = next.getBinding("abstract");
						String abstracT = abstractB.getValue().stringValue();

						TupleQueryResult executeTupleQuery3 = null;
						executeTupleQuery3 = SPARQLUtil
								.executeTupleQuery(EML_TOPIC.replaceAll(
										"EML_URI", "<" + emlURI + ">"));
						while (executeTupleQuery3.hasNext()) {
							BindingSet nextTopic = executeTupleQuery3.next();
							Binding bTopic = nextTopic.getBinding("topic");

							String topic = bTopic.getValue().stringValue();
							Binding kdomidB = nextTopic.getBinding("kdomid");
							String kdomid = kdomidB.getValue().stringValue();
							results.add(new GenericSearchResult(topic
									.substring(topic.indexOf('#') + 1),
									new String[] {
											abstracT, kdomid }, 1));
						}

					}
				}
				catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// looking for methods
		for (SearchTerm searchTerm : words) {
			TupleQueryResult executeTupleQuery = SPARQLUtil
					.executeTupleQuery(METHOD_SPARQL.replaceAll("SEARCHWORD",
							searchTerm.getTerm()));
			if (executeTupleQuery != null) {
				try {
					while (executeTupleQuery.hasNext()) {
						BindingSet next = executeTupleQuery.next();
						Binding b = next.getBinding("eml");
						String emlURI = b.getValue().stringValue();
						Binding titleB = next.getBinding("abstract");
						String title = titleB.getValue().stringValue();

						TupleQueryResult executeTupleQuery3 = null;
						executeTupleQuery3 = SPARQLUtil
								.executeTupleQuery(EML_TOPIC.replaceAll(
										"EML_URI", "<" + emlURI + ">"));
						while (executeTupleQuery3.hasNext()) {
							BindingSet nextTopic = executeTupleQuery3.next();
							Binding bTopic = nextTopic.getBinding("topic");
							String topic = bTopic.getValue().stringValue();
							Binding kdomidB = nextTopic.getBinding("kdomid");
							String kdomid = kdomidB.getValue().stringValue();
							results.add(new GenericSearchResult(topic
									.substring(topic.indexOf('#') + 1),
									new String[] {
											title, kdomid }, 1));
						}

					}
				}
				catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return results;
	}

	@Override
	public Collection<SearchTerm> getAllTerms() {

		SearchTerm root = new SearchTerm("Methoden");

		Collection<SearchTerm> resultTerms = new HashSet<SearchTerm>();

		resultTerms.addAll(SubmethodExpander.getInstance().expandSearchTerm(
				root, Integer.MAX_VALUE));
		resultTerms.add(root);

		// looking for keywords
		TupleQueryResult executeTupleQuery = SPARQLUtil
				.executeTupleQuery(KEYWORD_SPARQL);
		if (executeTupleQuery != null) {
			try {
				while (executeTupleQuery.hasNext()) {
					BindingSet next = executeTupleQuery.next();
					Binding b = next.getBinding("eml");
					String emlURI = b.getValue().stringValue();

					TupleQueryResult executeTupleQuery3 = null;
					executeTupleQuery3 = SPARQLUtil
							.executeTupleQuery(KEYWORD_SPARQL2.replaceAll(
									"EML_URI", emlURI));
					while (executeTupleQuery3.hasNext()) {
						BindingSet next3 = executeTupleQuery3.next();
						Binding keyB = next3.getBinding("keyword");
						String key = keyB.getValue().stringValue();

						try {
							key = URLDecoder.decode(key, "UTF-8");

						}
						catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						resultTerms.add(new SearchTerm(key));

					}

				}
			}
			catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultTerms;
	}

	@Override
	public Collection<SearchTerm> expandTermForRecommendation(SearchTerm t) {

		// add children of search term (i.e. method)
		Collection<SearchTerm> expandSearchTerm = SubmethodExpander
				.getInstance().expandSearchTerm(t, 1);

		// get and add father of search term(i.e. method) (usually single
		// father)
		Collection<SearchTerm> fatherMethod = SubmethodExpander.getInstance()
				.expandFatherMethods(t, 0.5);
		expandSearchTerm.addAll(fatherMethod);

		// foreach father (usually only ONE) add his children (i.e. sibblings of
		// search term)
		for (SearchTerm fatherTerm : fatherMethod) {
			Collection<SearchTerm> sibblings = SubmethodExpander.getInstance()
					.expandSearchTerm(fatherTerm, 1);
			expandSearchTerm.addAll(sibblings);
		}

		return expandSearchTerm;
	}

	@Override
	public Collection<SearchTerm> expandTermForSearch(SearchTerm t) {

		// add all submethods
		Collection<SearchTerm> expandSearchTerm = SubmethodExpander
				.getInstance().expandSearchTerm(t, Integer.MAX_VALUE);

		return expandSearchTerm;
	}

}

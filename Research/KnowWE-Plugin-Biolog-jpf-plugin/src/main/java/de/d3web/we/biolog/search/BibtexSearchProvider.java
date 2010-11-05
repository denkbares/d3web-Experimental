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
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.biolog.utils.BiologUtils;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.core.semantic.SPARQLUtil;
import de.d3web.we.search.GenericSearchResult;
import de.d3web.we.search.KnowWESearchProvider;
import de.d3web.we.search.SearchTerm;

/**
 * SearchProvider searching in data formalized by the BibTex-type
 * 
 * @see BibTex
 * 
 * @author Jochen
 * 
 */
public class BibtexSearchProvider implements KnowWESearchProvider {

	private static BibtexSearchProvider instance = null;

	public static BibtexSearchProvider getInstance() {
		if (instance == null) {
			instance = new BibtexSearchProvider();

		}
		return instance;
	}

	@Override
	public String getID() {
		return "BIBTEX-Search";
	}

	@Override
	public String getVerbalization(Locale local) {
		return "BIOLOG Literatur";
	}

	@Override
	public String renderResults(Collection<GenericSearchResult> localResults, String queryString) {

		StringBuffer resultBuffy = new StringBuffer();

		for (GenericSearchResult genericSearchResult : localResults) {
			if (genericSearchResult.getContexts().length > 0) {
				String title = BiologUtils.replaceBibTeX(genericSearchResult.getContexts()[0]);
				resultBuffy.append("<strong>"
								+ StringEscapeUtils.escapeHtml(title)
								+ ": </strong>");

				String author = BiologUtils.replaceBibTeX(genericSearchResult.
										getContexts()[1]).replaceAll("\\{", "")
														.replaceAll("\\}", "");

				String topic = genericSearchResult.getPagename();

				resultBuffy.append("<i>" + StringEscapeUtils.escapeHtml(author) + " </i>");

				resultBuffy.append(
						" <a target='_blank' href='Wiki.jsp?page="
								+ topic
								+ "#"
								+ StringEscapeUtils.escapeHtml(genericSearchResult.getContexts()[3])
								+ "' >"
								+ topic
								+ "</a>");

			}
			else {
				resultBuffy.append("<no context>");
			}
			resultBuffy.append("<br />");
		}

		return BiologUtils.replaceBibTeX(resultBuffy.toString());
	}

	private static final String KEYWORD_SPARQL = "SELECT ?pub ?title ?author WHERE { ?pub <http://swrc.ontoware.org/ontology#keywords> \"SEARCHWORD\" . ?pub <http://swrc.ontoware.org/ontology#title> ?title . ?pub <http://swrc.ontoware.org/ontology#author> ?author . }";

	private static final String AUTHOR_SPARQL = "SELECT ?pub ?title ?author WHERE { ?pub <http://swrc.ontoware.org/ontology#title> ?title . ?pub <http://swrc.ontoware.org/ontology#author> ?author FILTER regex(?author, \"SEARCHWORD\", \"i\") . }";

	private static final String TITLE_SPARQL = "SELECT ?pub ?title ?author WHERE { ?pub <http://swrc.ontoware.org/ontology#author> ?author . ?pub <http://swrc.ontoware.org/ontology#title> ?title FILTER regex(?title, \"SEARCHWORD\", \"i\") . }";

	private static final String ABSTRACT_SPARQL = "SELECT ?pub ?abstract ?title ?author WHERE { ?pub <http://swrc.ontoware.org/ontology#author> ?author . ?pub <http://swrc.ontoware.org/ontology#abstract> ?abstract . ?pub <http://swrc.ontoware.org/ontology#title> ?title FILTER regex(?abstract, \"SEARCHWORD\", \"i\") . }";

	private static final String BIBTEX_TOPIC = "SELECT  ?topic ?to ?kdomid ?bibtexkey WHERE { PUB_URI <http://swrc.ontoware.org/ontology#bibtexkey> ?bibtexkey . PUB_URI rdfs:isDefinedBy ?to . ?to ns:hasTopic ?topic . ?to ns:hasNode ?kdomid . }";

	/*
	 * SELECT ?title WHERE { _:book :title ?title . FILTER (regex(str(?title),
	 * "SPARQL")) . }
	 */

	/* SELECT ?v WHERE { ?v ?p "cat" } */

	@Override
	public Collection<GenericSearchResult> search(Collection<SearchTerm> words,
			KnowWEParameterMap map) {
		Set<GenericSearchResult> results = new HashSet<GenericSearchResult>();
		for (SearchTerm searchTerm : words) {

			// SEARCH KEYWORDS
			TupleQueryResult executeTupleQueryKeywords = null;
			executeTupleQueryKeywords = SPARQLUtil
						.executeTupleQuery(KEYWORD_SPARQL.replaceAll("SEARCHWORD",
								searchTerm.getTerm().toLowerCase()));

			if (executeTupleQueryKeywords != null) {
				try {
					while (executeTupleQueryKeywords.hasNext()) {
						BindingSet next = executeTupleQueryKeywords.next();
						Binding b = next.getBinding("pub");
						String pubURI = b.getValue().stringValue();
						Binding titleB = next.getBinding("title");
						String title = titleB.getValue().stringValue();
						Binding authorB = next.getBinding("author");
						String author = authorB.getValue().stringValue();
						try {
							title = URLDecoder.decode(title, "UTF-8");
							author = URLDecoder.decode(author, "UTF-8");

						}
						catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						TupleQueryResult executeTupleQuery3 = null;
						executeTupleQuery3 = SPARQLUtil
								.executeTupleQuery(BIBTEX_TOPIC.replaceAll("PUB_URI", "<"
										+ pubURI + ">"));
						while (executeTupleQuery3.hasNext()) {
							BindingSet nextTopic = executeTupleQuery3.next();
							Binding bTopic = nextTopic.getBinding("topic");
							String topic = bTopic.getValue().stringValue();
							Binding kdomidB = nextTopic.getBinding("kdomid");
							String kdomid = kdomidB.getValue().stringValue();
							Binding bibtexKeyB = nextTopic.getBinding("bibtexkey");
							String bibtexKey = bibtexKeyB.getValue().stringValue();

							results.add(new GenericSearchResult(
									topic.substring(topic.indexOf('#') + 1),
									new String[] {
											title, author, kdomid, bibtexKey }, 1));
						}

					}
				}
				catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}

			// SEARCH AUTHOR
			TupleQueryResult executeTupleQueryAuthor = null;
			try {
				executeTupleQueryAuthor = SPARQLUtil
						.executeTupleQuery(AUTHOR_SPARQL.replaceAll("SEARCHWORD",
								URLEncoder.encode(searchTerm.getTerm(), "UTF-8")));
			}
			catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (executeTupleQueryAuthor != null) {
				try {
					while (executeTupleQueryAuthor.hasNext()) {
						BindingSet next = executeTupleQueryAuthor.next();
						Binding b = next.getBinding("pub");
						String pubURI = b.getValue().stringValue();
						Binding titleB = next.getBinding("title");
						String title = titleB.getValue().stringValue();
						Binding authorB = next.getBinding("author");
						String author = authorB.getValue().stringValue();
						try {
							title = URLDecoder.decode(title, "UTF-8");
							author = URLDecoder.decode(author, "UTF-8");

						}
						catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						TupleQueryResult executeTupleQuery3 = null;
						executeTupleQuery3 = SPARQLUtil
								.executeTupleQuery(BIBTEX_TOPIC.replaceAll("PUB_URI", "<"
										+ pubURI + ">"));
						while (executeTupleQuery3.hasNext()) {
							BindingSet nextTopic = executeTupleQuery3.next();
							Binding bTopic = nextTopic.getBinding("topic");
							String topic = bTopic.getValue().stringValue();
							Binding kdomidB = nextTopic.getBinding("kdomid");
							String kdomid = kdomidB.getValue().stringValue();
							Binding bibtexKeyB = nextTopic.getBinding("bibtexkey");
							String bibtexKey = bibtexKeyB.getValue().stringValue();

							results.add(new GenericSearchResult(
									topic.substring(topic.indexOf('#') + 1),
									new String[] {
											title, author, kdomid, bibtexKey }, 1));
						}

					}
				}
				catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (java.util.regex.PatternSyntaxException ex) {
					// TODO: escape regex use correctly!
					ex.printStackTrace();
				}
			}

			// SEARCH TITLE
			TupleQueryResult executeTupleQueryTitle = null;
			try {
				executeTupleQueryTitle = SPARQLUtil
						.executeTupleQuery(TITLE_SPARQL.replaceAll("SEARCHWORD",
								URLEncoder.encode(searchTerm.getTerm(), "UTF-8")));
			}
			catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (executeTupleQueryTitle != null) {
				try {
					while (executeTupleQueryTitle.hasNext()) {
						BindingSet next = executeTupleQueryTitle.next();
						Binding b = next.getBinding("pub");
						String pubURI = b.getValue().stringValue();
						Binding titleB = next.getBinding("title");
						String title = titleB.getValue().stringValue();
						Binding authorB = next.getBinding("author");
						String author = authorB.getValue().stringValue();
						try {
							title = URLDecoder.decode(title, "UTF-8");
							author = URLDecoder.decode(author, "UTF-8");

						}
						catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						TupleQueryResult executeTupleQuery3 = null;
						executeTupleQuery3 = SPARQLUtil
								.executeTupleQuery(BIBTEX_TOPIC.replaceAll("PUB_URI", "<"
										+ pubURI + ">"));
						while (executeTupleQuery3.hasNext()) {
							BindingSet nextTopic = executeTupleQuery3.next();
							Binding bTopic = nextTopic.getBinding("topic");
							String topic = bTopic.getValue().stringValue();
							Binding kdomidB = nextTopic.getBinding("kdomid");
							String kdomid = kdomidB.getValue().stringValue();
							Binding bibtexKeyB = nextTopic.getBinding("bibtexkey");
							String bibtexKey = bibtexKeyB.getValue().stringValue();

							results.add(new GenericSearchResult(
									topic.substring(topic.indexOf('#') + 1),
									new String[] {
											title, author, kdomid, bibtexKey }, 1));
						}

					}
				}
				catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// SEARCH ABSTRACT
			TupleQueryResult executeTupleQueryAbstract = null;
			executeTupleQueryAbstract = SPARQLUtil
							.executeTupleQuery(ABSTRACT_SPARQL.replaceAll("SEARCHWORD",
									searchTerm.getTerm()));
			if (executeTupleQueryAbstract != null) {
				try {
					while (executeTupleQueryAbstract.hasNext()) {
						BindingSet next = executeTupleQueryAbstract.next();
						Binding b = next.getBinding("pub");
						String pubURI = b.getValue().stringValue();
						Binding titleB = next.getBinding("title");
						String title = titleB.getValue().stringValue();
						Binding authorB = next.getBinding("author");
						String author = authorB.getValue().stringValue();
						try {
							title = URLDecoder.decode(title, "UTF-8");
							author = URLDecoder.decode(author, "UTF-8");

						}
						catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						TupleQueryResult executeTupleQuery3 = null;
						executeTupleQuery3 = SPARQLUtil
								.executeTupleQuery(BIBTEX_TOPIC.replaceAll("PUB_URI", "<"
										+ pubURI + ">"));
						while (executeTupleQuery3.hasNext()) {
							BindingSet nextTopic = executeTupleQuery3.next();
							Binding bTopic = nextTopic.getBinding("topic");
							String topic = bTopic.getValue().stringValue();
							Binding kdomidB = nextTopic.getBinding("kdomid");
							String kdomid = kdomidB.getValue().stringValue();
							Binding bibtexKeyB = nextTopic.getBinding("bibtexkey");
							String bibtexKey = bibtexKeyB.getValue().stringValue();

							results.add(new GenericSearchResult(
									topic.substring(topic.indexOf('#') + 1),
										new String[] {
												title, author, kdomid, bibtexKey }, 1));
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

	private static final String BIBTEX_SPARQL = "SELECT  ?t WHERE {  ?t rdf:type <http://swrc.ontoware.org/ontology#Publication> . ?t <http://swrc.ontoware.org/ontology#keywords> ?keys }";
	private static final String KEYWORDS_SPARQL = "SELECT ?keywords WHERE { PUB_URI <http://swrc.ontoware.org/ontology#keywords> ?keywords }";

	@Override
	public Collection<SearchTerm> getAllTerms() {
		Set<SearchTerm> result = new HashSet<SearchTerm>();
		TupleQueryResult executeTupleQuery = SPARQLUtil
				.executeTupleQuery(BIBTEX_SPARQL);
		if (executeTupleQuery != null) {
			try {
				while (executeTupleQuery.hasNext()) {
					BindingSet next = executeTupleQuery.next();
					Binding b = next.getBinding("t");
					String pubURI = b.getValue().stringValue();

					TupleQueryResult executeTupleQuery2 = SPARQLUtil
							.executeTupleQuery(KEYWORDS_SPARQL.replaceAll("PUB_URI", "<"
									+ pubURI + ">"));
					if (executeTupleQuery2 != null) {
						while (executeTupleQuery2.hasNext()) {
							BindingSet next2 = executeTupleQuery2.next();
							Binding b2 = next2.getBinding("keywords");
							String key = b2.getValue().stringValue();
							try {
								key = URLDecoder.decode(key, "UTF-8");

							}
							catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							result.add(new SearchTerm(key));

						}
					}

				}
			}
			catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public Collection<SearchTerm> expandTermForRecommendation(SearchTerm t) {
		return null;
	}

	@Override
	public Collection<SearchTerm> expandTermForSearch(SearchTerm t) {
		return null;
	}
}

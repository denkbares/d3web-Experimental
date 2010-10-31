/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.we.biolog.search;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.core.semantic.SPARQLUtil;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.search.GenericSearchResult;
import de.d3web.we.search.KnowWESearchProvider;
import de.d3web.we.search.SearchTerm;

/**
 * SearchProvider for KnowWE-Annotations
 * 
 * @author Jochen
 * 
 */
public class AnnotationsProvider implements KnowWESearchProvider {

	private static AnnotationsProvider instance = null;

	public static AnnotationsProvider getInstance() {
		if (instance == null) {
			instance = new AnnotationsProvider();

		}

		return instance;
	}

	@Override
	public String getID() {
		return "Annotationen";
	}

	@Override
	public String getVerbalization(Locale local) {
		return getID();
	}

	@Override
	public String renderResults(Collection<GenericSearchResult> results) {
		StringBuffer resultBuffy = new StringBuffer();
		for (GenericSearchResult genericSearchResult : results) {
			if (genericSearchResult.getContexts().length > 0) {

				String topic = genericSearchResult.getPagename();
				String node = genericSearchResult.getContexts()[4];
				String sub= genericSearchResult.getContexts()[1];
				String prop= genericSearchResult.getContexts()[2];
				String obj= genericSearchResult.getContexts()[3];
				resultBuffy.append(" <a target='_blank' href='Wiki.jsp?page="
						+ topic + "#" + node + "' >" + topic + "</a>: "+sub.substring(sub.indexOf('#') + 1)+" "+prop.substring(prop.indexOf('#') + 1)+" "+obj.substring(obj.indexOf('#') + 1));

			} else {
				resultBuffy.append("<no context>");
			}
			resultBuffy.append("<br />");
		}

		return resultBuffy.toString();
	}

	private static final String REPORTS_SPARQL = "SELECT ?sub ?prop ?searcht ?node ?title WHERE { " +
			"?anno rdfs:isDefinedBy ?texto ." +
			"?anno rdf:type rdf:Statement ." +
			"?anno rdf:object ?searcht ." +
			"?anno rdf:predicate ?prop ." +
			"?anno rdf:subject ?sub ." +
			"?texto ns:hasTopic ?title ." +
			"?texto ns:hasNode ?node ." +
			"?texto ns:hasType ns:Annotation ." +
			"FILTER regex(STR(?searcht), \"SEARCHWORD\", \"i\")" +
			"}";

	@Override
	public Collection<GenericSearchResult> search(Collection<SearchTerm> words,
			KnowWEParameterMap map) {
		List<GenericSearchResult> results = new ArrayList<GenericSearchResult>();
		String lns = SemanticCoreDelegator.getInstance(KnowWEEnvironment.getInstance()).getUpper().getLocaleNS();
		// looking for annotations
		for (SearchTerm searchTerm : words) {
			TupleQueryResult executeTupleQuery = null;
			try {
				// TODO: escape meta-characters (regex) possibly occurring in searchTerm
				String coinedQuery = REPORTS_SPARQL						.replaceAll(
								"SEARCHWORD",
								URLEncoder
										.encode(searchTerm.getTerm().trim(), "UTF-8"));
				executeTupleQuery = SPARQLUtil.executeTupleQuery(coinedQuery);
				// } catch (UnsupportedEncodingException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
			} catch (Exception m) {
				// TODO Auto-generated catch block
				m.printStackTrace();
			}
			if (executeTupleQuery != null) {
				try {
					while (executeTupleQuery.hasNext()) {
						BindingSet next = executeTupleQuery.next();
						Binding b = next.getBinding("sub");
						String sub = b.getValue().stringValue();
						Binding titleB = next.getBinding("title");
						String title = titleB.getValue().stringValue();
						Binding nodeB = next.getBinding("node");
						String node = nodeB.getValue().stringValue();
						b=next.getBinding("prop");
						String prop=b.getValue().stringValue();
						b=next.getBinding("searcht");
						String obj=b.getValue().stringValue();

						try {
							title = URLDecoder.decode(title, "UTF-8");

						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						results.add(new GenericSearchResult(title
								.substring(title.indexOf('#') + 1),
								new String[] { title, sub, prop, obj,node }, 1));

					}
				} catch (QueryEvaluationException e) {
					e.printStackTrace();
				}
			}
		}

		return results;
	}

	@Override
	public Collection<SearchTerm> getAllTerms() {
		return null;
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

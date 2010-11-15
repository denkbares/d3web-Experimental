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

package de.d3web.we.biolog.freemap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.semantic.SPARQLUtil;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.search.SearchTerm;
import de.d3web.we.search.SearchTermExpander;

/**
 * This class implements the SearchTerm expansion on the Method-hierarchy
 * defined by the FreeNode-files
 * 
 * 
 * @author Jochen
 * @created 16.09.2010
 */
public class SubmethodExpander implements SearchTermExpander {

	public  static final String SUBMETHOD_OF = "submethodOf";
	
	private static SubmethodExpander instance;

	public static SubmethodExpander getInstance() {
		if (instance == null)
			instance = new SubmethodExpander();
		return instance;
	}
	
	
  
	@Override
	public Collection<SearchTerm> expandSearchTerm(SearchTerm t) {
		return expandSearchTerm(t, 1);

	}

	@Override
	public Collection<SearchTerm> expandSearchTerm(SearchTerm t, int level) {

		if (level < 1)
			return null;

		
		// throw together terms of all levels
		Set<SearchTerm> allResults = new HashSet<SearchTerm>();
		
		// adds SearchTerms for each found subclass of the class for t recursively until level level
		expand(t, allResults, 1, level);


		return allResults;
	}

	private void expand(SearchTerm t, Collection<SearchTerm> allTerms,
			int curLevel, int maxLevel) {
		if (curLevel > maxLevel)
			return;

		Collection<SearchTerm> newExpansion = expandSubmethods(t, 0.9);

		for (SearchTerm searchTerm : newExpansion) {
			if(!allTerms.contains(searchTerm)) {
				allTerms.add(searchTerm);
				expand(searchTerm, allTerms, ++curLevel, maxLevel);
			}
			
		}

	}
	
	public Collection<SearchTerm> expandFatherMethods(SearchTerm t, double discountFactor) {

		String name = t.getTerm();
		URI termURI = UpperOntology.getInstance().getHelper().createlocalURI(
				name);

		Set<SearchTerm> result = new HashSet<SearchTerm>();
		TupleQueryResult findSubClasses = SPARQLUtil.findObjects(termURI, UpperOntology.getInstance().getHelper().createlocalURI(SUBMETHOD_OF));
		if (findSubClasses != null) {
			try {
				while (findSubClasses.hasNext()) {
					BindingSet set = findSubClasses.next();
					String subClassName = set.getBinding("x").getValue()
							.stringValue();

					subClassName = URLDecoder.decode(subClassName, "UTF-8");

					SearchTerm searchTerm = new SearchTerm(subClassName
							.substring(subClassName.indexOf("#") + 1), t
							.getImportance()
							* discountFactor);
					result.add(searchTerm);
				}
			} catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public Collection<SearchTerm> expandSubmethods(SearchTerm t, double discountFactor) {

		String name = t.getTerm();
		URI termURI = UpperOntology.getInstance().getHelper().createlocalURI(
				name);

		Set<SearchTerm> result = new HashSet<SearchTerm>();
		TupleQueryResult findSubClasses = SPARQLUtil.findSubjects(termURI, UpperOntology.getInstance().getHelper().createlocalURI(SUBMETHOD_OF));
		if (findSubClasses != null) {
			try {
				while (findSubClasses.hasNext()) {
					BindingSet set = findSubClasses.next();
					String subClassName = set.getBinding("x").getValue()
							.stringValue();

					subClassName = URLDecoder.decode(subClassName, "UTF-8");

					SearchTerm searchTerm = new SearchTerm(subClassName
							.substring(subClassName.indexOf("#") + 1), t
							.getImportance()
							* discountFactor);
					result.add(searchTerm);
				}
			} catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
}

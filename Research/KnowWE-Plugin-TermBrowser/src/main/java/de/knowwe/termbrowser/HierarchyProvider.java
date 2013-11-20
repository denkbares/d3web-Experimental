/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.termbrowser;

import java.util.Collection;
import java.util.List;

import de.d3web.strings.Identifier;

/**
 * 
 * @author Jochen Reutelshoefer
 * @created 03.06.2013
 */
public interface HierarchyProvider {

	public static final String EXTENSION_POINT_HIERARCHY_PROVIDER = "HierarchyProvider";
	
	/**
	 * Sets a list of term categories that will be used to filter the terms that
	 * occur in the termbrowser
	 * 
	 * (categories for instance can be specified by the user in markup)
	 * 
	 * @created 20.11.2013
	 * @param categories
	 */
	void setCategories(List<String> categories);

	/**
	 * Filters the set of terms according to the filter categories set in
	 * setCategories().
	 * 
	 * Filtering is not necessary, i.e., also the full collection can be
	 * returned agains
	 * 
	 * @created 20.11.2013
	 * @param terms
	 * @return
	 */
	Collection<Identifier> filterInterestingTerms(Collection<Identifier> terms);

	/**
	 * Allows to specify the hierarchical relations that should be used by the
	 * termbrowser. This is not necessarily required if the HierarchyProvider
	 * uses hard coded hierarchy relations internally.
	 * 
	 * (relations for instance can be specified by the user in markup)
	 * 
	 * @created 20.11.2013
	 * @param relations
	 */
	void setAdditionalHierarchyRelations(List<String> relations);

	/**
	 * Set the master article of the knowledge base that should be used by the
	 * HierarchyProvider
	 * 
	 * @created 20.11.2013
	 * @param master
	 */
	void setMaster(String master);

	/**
	 * Returns all children terms for the specified terms.
	 * 
	 * @created 20.11.2013
	 * @param term
	 * @return
	 */
	List<Identifier> getChildren(Identifier term);

	/**
	 * Returns all parent terms for the specified terms.
	 * 
	 * @created 20.11.2013
	 * @param term
	 * @return
	 */
	List<Identifier> getParents(Identifier term);

	/**
	 * Determines whether term1 is a child of term2 according to the hierarchy
	 * represented by this HierarchyProvider.
	 * 
	 * @created 20.11.2013
	 * @param term1
	 * @param term2
	 * @return
	 */
	boolean isSubNodeOf(Identifier term1, Identifier term2);

	/**
	 * Returns all terms that are valid for this termbrowser. They are filtered
	 * according to the categories set by setCategories().
	 * 
	 * @created 20.11.2013
	 * @return
	 */
	Collection<Identifier> getAllTerms();
	
	/**
	 * Returns a set of terms that should be present in the termbrowser after
	 * system startup.
	 * 
	 * @created 20.11.2013
	 * @return
	 */
	Collection<Identifier> getStartupTerms();
	
}

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

import de.d3web.collections.PartialHierarchy;
import de.d3web.strings.Identifier;
import de.knowwe.core.user.UserContext;

/**
 * 
 * @author Jochen Reutelshoefer
 * @created 03.06.2013
 */
public interface HierarchyProvider<T> extends PartialHierarchy<T> {

	public static final String EXTENSION_POINT_HIERARCHY_PROVIDER = "HierarchyProvider";

	void updateSettings(UserContext user);


	Collection<BrowserTerm> filterInterestingTerms(Collection<BrowserTerm> terms);

	/**
	 * Returns all children terms for the specified terms.
	 *
	 * @created 20.11.2013
	 * @param term
	 * @return
	 */
	List<BrowserTerm> getChildren(BrowserTerm term);

	/**
	 * Returns all parent terms for the specified terms.
	 *
	 * @created 20.11.2013
	 * @param term
	 * @return
	 */
	List<BrowserTerm> getParents(BrowserTerm term);

	/**
	 * Returns all terms that are valid for this termbrowser. They are filtered
	 * according to the categories set by setCategories().
	 *
	 * @created 20.11.2013
	 * @return
	 */
	Collection<BrowserTerm> getAllTerms(UserContext user);

	/**
	 * Returns a set of terms that should be present in the termbrowser after
	 * system startup.
	 *
	 * @created 20.11.2013
	 * @return
	 */
	Collection<BrowserTerm> getStartupTerms(UserContext user);

}

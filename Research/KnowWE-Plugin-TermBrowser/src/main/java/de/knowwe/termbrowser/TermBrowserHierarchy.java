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

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.d3web.strings.Identifier;
import de.d3web.utils.Log;
import de.knowwe.core.user.UserContext;

/**
 * @author Jochen Reutelshöfer
 * @created 01.10.2013
 */
public class TermBrowserHierarchy implements HierarchyProvider<RatedTerm> {

	private final HierarchyProvider<Identifier> hierarchyProvider;
	private final UserContext user;

	/**
	 *
	 */
	public TermBrowserHierarchy(UserContext user) {

		this.user = user;
		String provider = TermBrowserMarkup.getCurrentTermbrowserMarkupHierarchyProvider(user);
		hierarchyProvider = TermBrowserHierarchy.getPluggedHierarchyProvider(provider);
		hierarchyProvider.updateSettings(user);
	}

	/**
	 * @return
	 * @created 05.06.2013
	 */
	@SuppressWarnings("unchecked")
	public static HierarchyProvider<Identifier> getPluggedHierarchyProvider(String provider) {
		Extension[] extensions = PluginManager.getInstance().getExtensions(
				"KnowWE-Plugin-TermBrowser", HierarchyProvider.EXTENSION_POINT_HIERARCHY_PROVIDER);
		for (Extension extension : extensions) {
			Object newInstance = extension.getSingleton();
			if (newInstance instanceof HierarchyProvider) {
				if (provider == null || provider.length() == 0) {
					return (HierarchyProvider<Identifier>) newInstance;
				}
				else {
					if (newInstance.getClass().getName().endsWith(provider)) {
						return (HierarchyProvider<Identifier>) newInstance;
					}
				}
			}
		}
		Log.warning("No HierarchyProvider found to populate TermBrowser: Either none is loaded by the Plugin-Framework or the @provider annotation in the TermBrowser-Markup is set incorrectly.");
		return null;
	}

	@Override
	public List<Identifier> getChildren(Identifier term) {
		this.hierarchyProvider.updateSettings(user);
		return hierarchyProvider.getChildren(term);
	}

	@Override
	public List<Identifier> getParents(Identifier term) {
		this.hierarchyProvider.updateSettings(user);
		return hierarchyProvider.getParents(term);
	}

	private boolean isSubNodeOf(Identifier term1, Identifier term2) {
		this.hierarchyProvider.updateSettings(user);
		return hierarchyProvider.isSuccessorOf(term1, term2);
	}

	@Override
	public Collection<Identifier> getAllTerms() {
		this.hierarchyProvider.updateSettings(user);
		Collection<Identifier> result = hierarchyProvider.getAllTerms();
		return result;
	}

	@Override
	public Collection<Identifier> getStartupTerms() {
		this.hierarchyProvider.updateSettings(user);
		return hierarchyProvider.getStartupTerms();
	}

	@Override
	public Collection<Identifier> filterInterestingTerms(Collection<Identifier> terms) {
		return hierarchyProvider.filterInterestingTerms(terms);
	}

	@Override
	public boolean isSuccessorOf(RatedTerm node1, RatedTerm node2) {
		return isSubNodeOf(node1.getTerm(), node2.getTerm());
	}

	@Override
	public void updateSettings(UserContext user) {
		this.hierarchyProvider.updateSettings(user);
	}

}

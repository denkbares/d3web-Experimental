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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.d3web.strings.Identifier;

/**
 * 
 * @author jochenreutelshofer
 * @created 01.10.2013
 */
public class TermBrowserHierarchy implements HierarchyProvider {

	private List<String> hierarchyRelations = new ArrayList<String>();
	private List<String> hierarchyCategories = new ArrayList<String>();
	private String master = null;
	private final HierarchyProvider hierarchyProvider;

	/**
	 * 
	 */
	public TermBrowserHierarchy(String master, List<String> relations, List<String> categories) {
		if(relations != null){
			hierarchyRelations.addAll(relations);
		}
		this.master = master;
		this.hierarchyCategories = categories;
		hierarchyProvider = TermBrowserHierarchy.getPluggedHierarchyProvider();
	}

	public List<String> getHierarchyRelations() {
		return hierarchyRelations;
	}

	public String getMaster() {
		return master;
	}

	/**
	 * 
	 * @created 05.06.2013
	 * @return
	 */
	public static HierarchyProvider getPluggedHierarchyProvider() {
		Extension[] extensions = PluginManager.getInstance().getExtensions(
				"KnowWE-Plugin-TermBrowser", HierarchyProvider.EXTENSION_POINT_HIERARCHY_PROVIDER);
		for (Extension extension : extensions) {
			Object newInstance = extension.getSingleton();
			if (newInstance instanceof HierarchyProvider) {
				return (HierarchyProvider) newInstance;
			}
		}
		return null;
	}

	@Override
	public List<Identifier> getChildren(Identifier term) {
		hierarchyProvider.setAdditionalHierarchyRelations(hierarchyRelations);
		hierarchyProvider.setCategories(hierarchyCategories);
		hierarchyProvider.setMaster(master);
		return hierarchyProvider.getChildren(term);
	}

	@Override
	public List<Identifier> getParents(Identifier term) {
		hierarchyProvider.setAdditionalHierarchyRelations(hierarchyRelations);
		hierarchyProvider.setCategories(hierarchyCategories);
		hierarchyProvider.setMaster(master);
		return hierarchyProvider.getParents(term);
	}

	@Override
	public boolean isSubNodeOf(Identifier term1, Identifier term2) {
		hierarchyProvider.setAdditionalHierarchyRelations(hierarchyRelations);
		hierarchyProvider.setCategories(hierarchyCategories);
		hierarchyProvider.setMaster(master);
		return hierarchyProvider.isSubNodeOf(term1, term2);
	}

	@Override
	public void setAdditionalHierarchyRelations(List<String> relations) {
		hierarchyRelations = relations;

	}

	@Override
	public void setMaster(String master) {
		this.master = master;

	}

	@Override
	public Collection<Identifier> getAllTerms() {
		hierarchyProvider.setAdditionalHierarchyRelations(hierarchyRelations);
		hierarchyProvider.setCategories(hierarchyCategories);
		hierarchyProvider.setMaster(master);
		Collection<Identifier> result = hierarchyProvider.getAllTerms();
		return result;
	}

	@Override
	public Collection<Identifier> getStartupTerms() {
		return hierarchyProvider.getStartupTerms();
	}

	@Override
	public void setCategories(List<String> categories) {
		this.hierarchyProvider.setCategories(categories);

	}

}

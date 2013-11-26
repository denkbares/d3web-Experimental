package de.knowwe.termbrowser;

import de.d3web.strings.Identifier;

public interface SubnodeRelationProvider {

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

}

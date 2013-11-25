package de.knowwe.termbrowser;

import de.d3web.strings.Identifier;
import de.knowwe.termbrowser.util.SubnodeRelationProvider;


public class RootSubNodeHierarchyProvider implements SubnodeRelationProvider {

	@Override
	public boolean isSubNodeOf(Identifier term1, Identifier term2) {
		if (term2.equals(RatedTerm.ROOT)) return true;
		return false;
	}

}

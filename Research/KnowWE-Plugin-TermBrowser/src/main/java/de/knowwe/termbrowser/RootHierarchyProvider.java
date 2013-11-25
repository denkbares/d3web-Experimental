package de.knowwe.termbrowser;

import java.util.Collection;
import java.util.List;

import de.d3web.strings.Identifier;


public class RootHierarchyProvider implements HierarchyProvider {

	@Override
	public void setCategories(List<String> categories) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Identifier> filterInterestingTerms(Collection<Identifier> terms) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdditionalHierarchyRelations(List<String> relations) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMaster(String master) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Identifier> getChildren(Identifier term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Identifier> getParents(Identifier term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSubNodeOf(Identifier term1, Identifier term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Identifier> getAllTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Identifier> getStartupTerms() {
		// TODO Auto-generated method stub
		return null;
	}

}

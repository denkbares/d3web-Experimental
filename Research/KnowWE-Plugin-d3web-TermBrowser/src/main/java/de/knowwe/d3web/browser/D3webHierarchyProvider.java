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
package de.knowwe.d3web.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.d3web.we.object.D3webTermDefinition;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.termbrowser.HierarchyProvider;

/**
 * 
 * @author jochenreutelshofer
 * @created 02.10.2013
 */
public class D3webHierarchyProvider implements HierarchyProvider {

	private List<String> relations = null;
	private String master = null;

	@Override
	public void setAdditionalHierarchyRelations(List<String> relations) {
		this.relations = relations;
	}

	@Override
	public void setMaster(String master) {
		this.master = master;

	}

	@Override
	public List<Identifier> getChildren(Identifier term) {
		List<Identifier> childrenList = new ArrayList<Identifier>();
		TerminologyManager terminologyManager = Environment.getInstance().getTerminologyManager(
				Environment.DEFAULT_WEB, master);

		Section<?> definingSection = terminologyManager.getTermDefiningSection(term);
		if (definingSection == null || !(definingSection.get() instanceof D3webTermDefinition)) return childrenList;
		KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(Environment.DEFAULT_WEB, master);
		de.d3web.core.knowledge.TerminologyManager manager = knowledgeBase.getManager();
		NamedObject namedObject = manager.search(term.toExternalForm());
		if (namedObject == null) {
			Section<D3webTermDefinition> def = Sections.cast(definingSection,
					D3webTermDefinition.class);
			if (def != null) {

				namedObject = def.get().getTermObject(
						Environment.getInstance().getArticle(Environment.DEFAULT_WEB, master), def);
			}
			if (namedObject == null) {
				return childrenList;
			}
		}
		List<NamedObject> childrenD3web = getChildrenD3web(namedObject);
		for (NamedObject childD3web : childrenD3web) {
			if (childD3web instanceof Choice) {
				childrenList.add(new Identifier(new String[] {
						((Choice) childD3web).getQuestion().getName(), childD3web.getName() }));
			}
			else {
				childrenList.add(new Identifier(childD3web.getName()));
			}
		}
		return childrenList;
	}

	private List<NamedObject> getChildrenD3web(NamedObject namedObject) {
		List<NamedObject> childrenList = new ArrayList<NamedObject>();
		if (namedObject instanceof TerminologyObject) {
			TerminologyObject kbObject = ((TerminologyObject) namedObject);
			TerminologyObject[] children = kbObject.getChildren();
			for (TerminologyObject terminologyObject : children) {
				childrenList.add(terminologyObject);
			}
		}

		if (namedObject instanceof QuestionChoice) {
			QuestionChoice qChoice = ((QuestionChoice) namedObject);
			List<Choice> answers = qChoice.getAllAlternatives();
			for (Choice answer : answers) {
				childrenList.add(answer);
			}
		}
		return childrenList;

	}

	private List<NamedObject> getParentsD3web(NamedObject namedObject) {
		List<NamedObject> parentList = new ArrayList<NamedObject>();
		if (namedObject instanceof TerminologyObject) {
			TerminologyObject kbObject = ((TerminologyObject) namedObject);
			TerminologyObject[] parents = kbObject.getParents();
			for (TerminologyObject terminologyObject : parents) {
				parentList.add(terminologyObject);
			}
		}

		if (namedObject instanceof Choice) {
			Choice choice = ((Choice) namedObject);
			parentList.add(choice.getQuestion());
		}
		return parentList;
	}

	@Override
	public List<Identifier> getParents(Identifier term) {
		List<Identifier> parentList = new ArrayList<Identifier>();
		TerminologyManager terminologyManager = Environment.getInstance().getTerminologyManager(
				Environment.DEFAULT_WEB, master);
		Section<?> definingSection = terminologyManager.getTermDefiningSection(term);
		if (definingSection == null || !(definingSection.get() instanceof D3webTermDefinition)) return parentList;
		KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(Environment.DEFAULT_WEB, master);
		de.d3web.core.knowledge.TerminologyManager manager = knowledgeBase.getManager();
		NamedObject namedObject = manager.search(term.toExternalForm());
		if (namedObject == null) {
			Section<D3webTermDefinition> def = Sections.cast(definingSection,
					D3webTermDefinition.class);
			if (def != null) {

				namedObject = def.get().getTermObject(
						Environment.getInstance().getArticle(Environment.DEFAULT_WEB, master), def);
			}
			if (namedObject == null) {
				return parentList;
			}
		}

		List<NamedObject> parentsD3web = getParentsD3web(namedObject);
		for (NamedObject parentD3web : parentsD3web) {
			parentList.add(new Identifier(parentD3web.getName()));
		}

		return parentList;
	}

	@Override
	public boolean isSubNodeOf(Identifier term1, Identifier term2) {

		NamedObject object1 = findTermForName(term1.toExternalForm());

		NamedObject object2 = findTermForName(term2.toExternalForm());
		return isSubObjectTrans(object1, object2);
	}

	/**
	 * 
	 * @created 02.10.2013
	 * @param term1
	 * @return
	 */
	private NamedObject findTermForName(String term) {
		KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(Environment.DEFAULT_WEB, master);
		de.d3web.core.knowledge.TerminologyManager manager = knowledgeBase.getManager();
		if (term.contains("#")) {
			String[] split = term.split("#");
			if (split.length == 2) {
				TerminologyObject question = manager.search(Strings.unquote(split[0]));
				if (question instanceof QuestionChoice) {
					List<Choice> children = ((QuestionChoice) question).getAllAlternatives();
					for (Choice terminologyObject : children) {
						if (terminologyObject.getName().equals(Strings.unquote(split[1]))) {
							return terminologyObject;
						}
					}
				}
			}
		}
		else {
			return manager.search(Strings.unquote(term));
		}
		return null;
	}

	private boolean isSubObjectTrans(NamedObject object1, NamedObject object2) {
		List<NamedObject> parentsD3web = getParentsD3web(object1);
		if (parentsD3web.contains(object2)) return true;
		for (NamedObject namedObject : parentsD3web) {
			boolean is = isSubObjectTrans(namedObject, object2);
			if (is) return true;
		}
		return false;
	}

	@Override
	public Collection<Identifier> getAllTerms() {
		KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(Environment.DEFAULT_WEB, master);
		de.d3web.core.knowledge.TerminologyManager manager = knowledgeBase.getManager();
		Set<Identifier> result = new HashSet<Identifier>();
		Collection<TerminologyObject> allTerminologyObjects = manager.getAllTerminologyObjects();
		for (TerminologyObject terminologyObject : allTerminologyObjects) {
			result.add(new Identifier(terminologyObject.toString()));
		}
		return result;
	}

	@Override
	public Collection<Identifier> getStartupTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCategories(List<String> categories) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Identifier> filterInterestingTerms(Collection<Identifier> terms) {
		// we do not filter until now
		return terms;
	}

}

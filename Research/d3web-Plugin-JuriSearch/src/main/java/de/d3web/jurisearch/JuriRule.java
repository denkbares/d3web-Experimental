/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.jurisearch;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;

/**
 * 
 * @author grotheer
 * @created 04.05.2012
 */
public class JuriRule implements KnowledgeSlice, Comparable<JuriRule> {

	public final static KnowledgeKind<JuriRule> KNOWLEDGE_KIND = new KnowledgeKind<JuriRule>(
			"JuriRule", JuriRule.class);

	public static final Choice YES = new Choice("ja");
	public static final Choice NO = new Choice("nein");
	public static final Choice MAYBE = new Choice("vielleicht");

	public static final ChoiceValue YES_VALUE = new ChoiceValue(YES);
	public static final ChoiceValue NO_VALUE = new ChoiceValue(NO);
	public static final ChoiceValue MAYBE_VALUE = new ChoiceValue(MAYBE);

	private QuestionOC father;
	private HashMap<QuestionOC, List<ChoiceValue>> children;

	private boolean disjunctive; // default type is conjunction

	public JuriRule(QuestionOC father) {
		this(father, new HashMap<QuestionOC, List<ChoiceValue>>());
	}

	public JuriRule(QuestionOC father, List<QuestionOC> children) {
		this(father, new HashMap<QuestionOC, List<ChoiceValue>>());
		addChildren(children);
	}

	public JuriRule(QuestionOC father, HashMap<QuestionOC, List<ChoiceValue>> children) {
		this.father = father;
		this.children = children;

		disjunctive = false;
	}

	/**
	 * Getter for the father
	 * 
	 * @created 04.05.2012
	 * @return
	 */
	public QuestionOC getFather() {
		return father;
	}

	/**
	 * Setter for the father
	 * 
	 * @created 04.05.2012
	 * @param father
	 */
	public void setFather(QuestionOC father) {
		this.father = father;
	}

	/**
	 * getter for the children
	 * 
	 * @created 04.05.2012
	 * @return
	 */
	public HashMap<QuestionOC, List<ChoiceValue>> getChildren() {
		return children;
	}

	/**
	 * Setter for the children
	 * 
	 * @created 04.05.2012
	 * @param children
	 */
	public void setChildren(HashMap<QuestionOC, List<ChoiceValue>> children) {
		this.children = children;
	}

	/**
	 * add a child to the rule with the default confirming value YES
	 * 
	 * @created 30.03.2012
	 * @param q
	 */
	public void addChild(QuestionOC q) {
		addChild(q, YES_VALUE);
	}

	/**
	 * add a child to the rule with the single confirming value
	 * 
	 * @created 30.03.2012
	 * @param q
	 * @param confirmingValue
	 */
	public void addChild(QuestionOC q, ChoiceValue confirmingValue) {
		List<ChoiceValue> list = new LinkedList<ChoiceValue>();
		list.add(confirmingValue);
		children.put(q, list);
	}

	/**
	 * add a child to the rule with multiple confirming values
	 * 
	 * @created 30.03.2012
	 * @param q
	 * @param confirmingValues
	 */
	public void addChild(QuestionOC q, List<ChoiceValue> confirmingValues) {
		children.put(q, confirmingValues);
	}

	/**
	 * add multiple children with the default confirming value
	 * 
	 * @created 30.03.2012
	 * @param c
	 */
	public void addChildren(Collection<QuestionOC> c) {
		for (QuestionOC q : c) {
			addChild(q);
		}
	}

	/**
	 * add multiple children with multiple confirming values
	 * 
	 * @created 30.03.2012
	 * @param m
	 */
	public void addChildren(HashMap<QuestionOC, List<ChoiceValue>> m) {
		children.putAll(m);
	}

	/**
	 * Remove a single child
	 * 
	 * @created 04.05.2012
	 * @param q
	 */
	public void removeChild(QuestionOC q) {
		children.remove(q);
	}

	/**
	 * 
	 * @created 04.05.2012
	 * @return true if rule is disjunctive, else false
	 */
	public boolean isDisjunctive() {
		return disjunctive;
	}

	/**
	 * Define if the rule is disjunctive or not
	 * 
	 * @created 04.05.2012
	 * @param disjunctive
	 */
	public void setDisjunctive(boolean disjunctive) {
		this.disjunctive = disjunctive;
	}

	/**
	 * fire a fact, if the evaluation of the rule is positive, else null
	 * 
	 * @created 04.05.2012
	 * @param session
	 * @return derived fact
	 */
	public Fact fire(Session session) {
		boolean maybe = false;

		// run over all children of the rule.
		for (Entry<QuestionOC, List<ChoiceValue>> child : children.entrySet()) {
			ChoiceValue value = null;
			// get the value for this child from the blackboard
			if (session.getBlackboard().getAnsweredQuestions().contains(child.getKey())) {
				value = (ChoiceValue) session.getBlackboard().getValue(child.getKey());
			}

			if (value != null) {
				// find out, if the value equals one of the specified confirming
				// values
				boolean oneOfConfirmingValues = false;
				for (ChoiceValue confirmingValue : child.getValue()) {
					if (value.equals(confirmingValue)) {
						oneOfConfirmingValues = true;
					}
				}

				if (!disjunctive) {
					// directly set father to "no" if the rule is not
					// disjunctive and has at least one child, which is not set
					// to a confirming value.
					if (!oneOfConfirmingValues) {
						return createFact(session, NO_VALUE);
					}

				}
				else {
					// directly set father to "yes" if the rule is disjunctive
					// and has at least one child, which is set to a confirming
					// value.
					if (oneOfConfirmingValues) {
						return createFact(session, YES_VALUE);
					}
				}

				// set the maybe flag, if one child is set to the maybe value.
				if (value.equals(MAYBE_VALUE)) {
					maybe = true;
				}
			}
			else {
				if (!disjunctive) {
					// if a child question is not jet answered and the rule is
					// not disjunctive, no new fact is returned (return null)
					return null;
				}
			}
		}

		// if the for-loop is passed, there are four cases:
		if (maybe) {
			// matches two cases:
			// 1. the rule is not disjunctive and all child question values are
			// "maybe" or "yes"
			// 2. the rule is disjunctive and all child question values are
			// "maybe" or "no"
			return createFact(session, MAYBE_VALUE);
		}
		if (!disjunctive) {
			// the rule is not disjunctive and all child question values are
			// "yes"
			return createFact(session, YES_VALUE);
		}
		else {
			// the rule is disjunctive and all child question values are "no"
			return createFact(session, NO_VALUE);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + (disjunctive ? 1231 : 1237);
		result = prime * result + ((father == null) ? 0 : father.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		JuriRule other = (JuriRule) obj;
		if (children == null) {
			if (other.children != null) return false;
		}
		else if (!children.equals(other.children)) return false;
		if (disjunctive != other.disjunctive) return false;
		if (father == null) {
			if (other.father != null) return false;
		}
		else if (!father.equals(other.father)) return false;
		return true;
	}

	private Fact createFact(Session session, Value value) {
		return FactFactory.createFact(session, father, value, this,
				session.getPSMethodInstance(PSMethodJuri.class));
	}

	@Override
	public String toString() {
		return "JuriRule [father=" + father + ", children=" + children + ", disjunctive="
				+ disjunctive + "]";
	}

	@Override
	public int compareTo(JuriRule o) {
		// TODO Auto-generated method stub
		return hashCode() - o.hashCode();
	}
}






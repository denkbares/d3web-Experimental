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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PropagationEntry;
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
 * @created 06.03.2012
 */
public class JuriRule implements KnowledgeSlice {

	public final static KnowledgeKind<JuriRule> KNOWLEDGE_KIND = new KnowledgeKind<JuriRule>(
			"JuriRule", JuriRule.class);

	public static final Choice YES = new Choice("yes");
	public static final Choice NO = new Choice("no");
	public static final Choice MAYBE = new Choice("maybe");

	public static final ChoiceValue YES_VALUE = new ChoiceValue(YES);
	public static final ChoiceValue NO_VALUE = new ChoiceValue(NO);
	public static final ChoiceValue MAYBE_VALUE = new ChoiceValue(MAYBE);

	private QuestionOC father;
	private List<QuestionOC> children;
	private boolean disjunctive; // default type is conjunction

	public JuriRule() {
		children = new ArrayList<QuestionOC>();
		disjunctive = false;
	}

	public JuriRule(QuestionOC father, List<QuestionOC> children) {
		this.father = father;
		this.children = children;

		disjunctive = false;
	}

	public QuestionOC getFather() {
		return father;
	}

	public void setFather(QuestionOC father) {
		this.father = father;
	}

	public List<QuestionOC> getChildren() {
		return children;
	}

	public void setChildren(List<QuestionOC> children) {
		this.children = children;
	}

	public void addChild(QuestionOC q) {
		children.add(q);
	}

	public void removeChild(QuestionOC q) {
		children.remove(q);
	}

	public boolean isDisjunctive() {
		return disjunctive;
	}

	public void setDisjunctive(boolean disjunctive) {
		this.disjunctive = disjunctive;
	}

	public Fact fire(Session session, List<PropagationEntry> changes) {
		HashMap<QuestionOC, ChoiceValue> changedQuestions = new HashMap<QuestionOC, ChoiceValue>();
		for (PropagationEntry change : changes) {
			changedQuestions.put((QuestionOC) change.getObject(),
					(ChoiceValue) change.getNewValue());
		}
		boolean maybe = false;
		for (QuestionOC child : children) {
			ChoiceValue value = null;
			if (changedQuestions.keySet().contains(child)) {
				value = changedQuestions.get(child);
			}
			else {
				if (session.getBlackboard().getAnsweredQuestions().contains(child)) {
					value = (ChoiceValue) session.getBlackboard().getValue(child);
				}
			}

			if (value != null) {
				if (!disjunctive) {
					if (value.equals(NO_VALUE)) {
						return createFact(session, NO_VALUE);
					}
				}
				else {
					if (value.equals(YES)) {
						return createFact(session, YES_VALUE);
					}
				}
				if (value.equals(MAYBE_VALUE)) {
					maybe = true;
				}
			}
			else {
				if (!disjunctive) {
					return null;
				}
			}
		}
		if (maybe) {
			return createFact(session, MAYBE_VALUE);
		}
		if (!disjunctive) {
			return createFact(session, YES_VALUE);
		}
		else {
			return createFact(session, NO_VALUE);
		}
	}

	private Fact createFact(Session session, Value value) {
		return FactFactory.createFact(session, father, value, this,
				session.getPSMethodInstance(PSMethodJuri.class));
	}
}
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
import java.util.List;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.Session;

/**
 * 
 * @author grotheer
 * @created 06.03.2012
 */
public class JuriRule implements KnowledgeSlice {

	public final static KnowledgeKind<JuriRule> KNOWLEDGE_KIND = new KnowledgeKind<JuriRule>(
			"JuriRule", JuriRule.class);
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String MAYBE = "maybe";

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

		for (QuestionOC child : this.children) {
			child = setAlternatives(child);
		}
		this.father = setAlternatives(this.father);
		disjunctive = false;
	}

	private QuestionOC setAlternatives(QuestionOC q) {
		// for (Choice a : q.getAlternatives()) {
		// q.removeAlternative(a);
		// }
		q.addAlternative(new Choice(YES));
		q.addAlternative(new Choice(NO));
		q.addAlternative(new Choice(MAYBE));
		return q;
	}

	public QuestionOC getFather() {
		return father;
	}

	public void setFather(QuestionOC father) {
		this.father = father;
		this.father = setAlternatives(father);
	}

	public List<QuestionOC> getChildren() {
		return children;
	}

	public void setChildren(List<QuestionOC> children) {
		this.children = children;
		for (QuestionOC child : this.children) {
			child = setAlternatives(child);
		}
	}

	public void addChild(QuestionOC q) {
		q = setAlternatives(q);
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

	public void fire(Session session, List<PropagationEntry> entries) {
		ArrayList<QuestionOC> affirmendQuestions = new ArrayList<QuestionOC>();
		ArrayList<QuestionOC> negatedQuestions = new ArrayList<QuestionOC>();
		ArrayList<QuestionOC> unansweredQuestions = new ArrayList<QuestionOC>();
		for (PropagationEntry e : entries) {
			TerminologyObject o = e.getObject();
			if (o instanceof QuestionOC) {
				QuestionOC q = (QuestionOC) o;
				if (children.contains(q)) {
					if (e.hasNewValue()) {
						if (e.getNewValue().equals(YES)) {
							affirmendQuestions.add(q);
						}
						else if (e.getNewValue().equals(NO)) {
							negatedQuestions.add(q);
						}
					}
					else {
						unansweredQuestions.add(q);
					}
				}
				// TODO
				affirmendQuestions.add(q);
			}
		}

		if (!disjunctive) {
			boolean maybe = false;
			for (QuestionOC child : children) {
				if (!affirmendQuestions.contains(child)) {
					if (negatedQuestions.contains(child)) {
						// fire no
					}
					else {
						maybe = true;
					}
				}
			}
			if (maybe) {
				// fire maybe
			}
			// fire yes
		}
		else {
			boolean maybe = false;
			for (QuestionOC child : children) {
				if (affirmendQuestions.contains(child)) {
					// fire yes
				}
				else {
					if (!negatedQuestions.contains(child)) {
						maybe = true;
					}
				}
			}
			if (maybe) {
				// fire maybe
			}
			// fire no
		}
	}
}

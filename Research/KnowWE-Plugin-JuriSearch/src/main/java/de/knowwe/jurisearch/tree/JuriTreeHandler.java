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
package de.knowwe.jurisearch.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.jurisearch.JuriModel;
import de.d3web.jurisearch.JuriRule;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.event.ArticleCreatedEvent;
import de.knowwe.jurisearch.tree.JuriTreeExpression.BracketContent;
import de.knowwe.jurisearch.tree.JuriTreeExpression.NegationFlag;
import de.knowwe.jurisearch.tree.JuriTreeExpression.Operator;

/**
 * 
 * @author boehler
 * @created 19.01.2012
 */
public class JuriTreeHandler extends D3webSubtreeHandler<JuriTreeExpression> implements EventListener {

	public JuriTreeHandler() {
		EventManager.getInstance().registerListener(this);
	}

	@Override
	public Collection<Message> create(Article article, Section<JuriTreeExpression> section) {
		if (!section.hasErrorInSubtree(article)) {
			KnowledgeBase kb = getKB(article);
			JuriRule rule = createJuriRule(kb, section);
			if (rule != null) {
				JuriModel model = getModelOrCreate(kb);
				model.addRule(rule);
				kb.getKnowledgeStore().addKnowledge(JuriModel.KNOWLEDGE_KIND, model);
			}
		}
		return new ArrayList<Message>(0);
	}

	public Class<? extends Event> getEvent() {
		return ArticleCreatedEvent.class;
	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		LinkedList<Class<? extends Event>> l = new LinkedList<Class<? extends Event>>();
		l.add(ArticleCreatedEvent.class);
		// l.add(Event.class);
		return l;
	}

	@Override
	public void notify(Event event) {
		ArticleCreatedEvent e = (ArticleCreatedEvent) event;
		Article article = e.getArticle();
		Section<Article> section = article.getRootSection();
		// JuriTreeXmlGenerator jtxg = new JuriTreeXmlGenerator(section);
	}

	/**
	 * Creates a JuriRule for the specified section.
	 * 
	 * @created 12.03.2012
	 * @param kb
	 * @param section
	 */
	private JuriRule createJuriRule(KnowledgeBase kb, Section<JuriTreeExpression> s) {
		Section<QuestionIdentifier> section = Sections.findSuccessor(s, QuestionIdentifier.class);
		List<Section<QuestionIdentifier>> children = section.get().getChildrenQuestion(section);
		List<QuestionOC> childrenQuestion = new LinkedList<QuestionOC>();
		List<QuestionOC> negatedChildrenQuestion = new LinkedList<QuestionOC>();

		// Get all children questions
		for (Section<QuestionIdentifier> child : children) {
			QuestionOC question = (QuestionOC) kb.getManager().search(child.getText());
			if (question == null) {
				// if a child question is not defined, return null
				return null;
			}
			childrenQuestion.add(question);

			/*
			 * get the content of the negation flag and mark child as negative
			 * if required.
			 */
			Section<JuriTreeExpression> jte = Sections.findAncestorOfType(child,
					JuriTreeExpression.class);
			Section<NegationFlag> negation = Sections.findSuccessor(jte,
					JuriTreeExpression.NegationFlag.class);
			if (negation != null) {
				Section<BracketContent> negation_content =
						Sections.findSuccessor(negation,
								JuriTreeExpression.BracketContent.class);
				String negation_str = negation_content.getText().toLowerCase();
				if (negation_str.equals(JuriTreeExpression.NOT)) {
					negatedChildrenQuestion.add(question);
				}
			}
		}
		if (children.isEmpty()) {
			// if father is a leaf, return null
			return null;
		}

		JuriRule rule;
		QuestionOC father = (QuestionOC) kb.getManager().search(section.getText());
		rule = new JuriRule(father);

		for (QuestionOC childQuestion : childrenQuestion) {
			// remove children questions from RootQASet
			father.getKnowledgeBase().getRootQASet().removeChild(childQuestion);

			// add children questions to father question
			father.addChild(childQuestion);

		}

		// add children to rule
		childrenQuestion.removeAll(negatedChildrenQuestion);
		rule.addChildren(childrenQuestion);
		rule.addNegatedChildren(negatedChildrenQuestion);

		/*
		 * get the content of operator flag and mark rule as disjunctive if
		 * required. default is conjunctive, disjunctive = false
		 */
		Section<Operator> operator = Sections.findSuccessor(s, JuriTreeExpression.Operator.class);
		if (operator != null) {
			Section<BracketContent> operator_content = Sections.findSuccessor(operator,
					JuriTreeExpression.BracketContent.class);
			String operator_str = operator_content.getText().toLowerCase();

			if (operator_str.equals(JuriTreeExpression.OR)) {
				rule.setDisjunctive(true);
			}
			else if (operator_str.equals(JuriTreeExpression.SCORE)) {
				// TODO
			}
		}

		// /*
		// * get the content of the negation flag and mark rule as negative if
		// * required.
		// */
		// Section<NegationFlag> negation = Sections.findSuccessor(s,
		// JuriTreeExpression.NegationFlag.class);
		// if (negation != null) {
		//
		// Section<BracketContent> negation_content =
		// Sections.findSuccessor(negation,
		// JuriTreeExpression.BracketContent.class);
		// String negation_str = negation_content.getText().toLowerCase();
		// if (negation_str.equals(JuriTreeExpression.NOT)) {
		// rule.setNegative(true);
		// }
		// }

		// System.out.println(rule.toString());
		return rule;
	}

	/**
	 * Tries to retrieve an JuriModel that is contained in the knowledge base.
	 * Otherwise create a new empty JuriModel.
	 * 
	 * @created 13.03.2012
	 * @param kb
	 * @return
	 */
	private JuriModel getModelOrCreate(KnowledgeBase kb) {
		Collection<JuriModel> models = kb.getAllKnowledgeSlicesFor(JuriModel.KNOWLEDGE_KIND);
		JuriModel model;
		if (models.isEmpty()) {
			model = new JuriModel();
		}
		else {
			model = models.iterator().next();
		}
		return model;
	}
}

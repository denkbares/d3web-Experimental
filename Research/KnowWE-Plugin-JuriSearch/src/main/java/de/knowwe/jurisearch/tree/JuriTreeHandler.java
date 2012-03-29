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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.jurisearch.JuriModel;
import de.d3web.jurisearch.JuriRule;
import de.d3web.we.object.AnswerReference;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.event.ArticleCreatedEvent;
import de.knowwe.jurisearch.EmbracedContent;
import de.knowwe.jurisearch.tree.DummyExpression.NegationFlag;
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
		ArrayList<Message> messages = new ArrayList<Message>();
		if (!section.hasErrorInSubtree(article)) {
			KnowledgeBase kb = getKB(article);
			JuriRule rule = createJuriRule(article, kb, section, messages);
			if (rule != null) {
				JuriModel model = getModelOrCreate(kb);
				model.addRule(rule);
				kb.getKnowledgeStore().addKnowledge(JuriModel.KNOWLEDGE_KIND, model);
			}
		}
		return messages;
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
	private JuriRule createJuriRule(Article article, KnowledgeBase kb, Section<JuriTreeExpression> s, ArrayList<Message> messages) {
		Section<QuestionIdentifier> section = Sections.findSuccessor(s, QuestionIdentifier.class);
		List<Section<QuestionIdentifier>> children = section.get().getChildrenQuestion(section);
		HashMap<QuestionOC, ChoiceValue> childrenQuestion = new HashMap<QuestionOC, ChoiceValue>();

		// Get all children questions
		for (Section<QuestionIdentifier> child : children) {
			QuestionOC question = (QuestionOC) kb.getManager().search(child.getText());
			if (question == null) {
				// if a child question is not defined, return null
				messages.add(new Message(Message.Type.ERROR, "Child question " + child.getText()
						+ " of " + section.getText() + " not defined."));
				return null;
			}

			/*
			 * get the content of the answer identifier flag, otherwise YES is
			 * the confirming answer
			 */
			Section<JuriTreeExpression> jte = Sections.findAncestorOfType(child,
					JuriTreeExpression.class);
			Section<AnswerReference> answer = Sections.findSuccessor(jte, AnswerReference.class);
			ChoiceValue value;
			if (answer != null) {
				Choice c = answer.get().getTermObject(article, answer);
				value = new ChoiceValue(c);
			}
			else {
				Section<NegationFlag> dummyNegation = Sections.findSuccessor(jte,
						NegationFlag.class);
				if (dummyNegation != null) {
					String name = dummyNegation.get().getName();
					Choice c = KnowledgeBaseUtils.findChoice(question, name);
					value = new ChoiceValue(c);
				}
				else {
					value = JuriRule.YES_VALUE;
				}
			}
			childrenQuestion.put(question, value);

		}
		if (children.isEmpty()) {
			// if father is a leaf, return null
			messages.add(new Message(Message.Type.INFO, "Question " + section.getText()
					+ " is a leaf."));
			return null;
		}

		JuriRule rule;
		QuestionOC father = (QuestionOC) kb.getManager().search(section.getText());

		// father nodes MUST have the answers yes, no and maybe
		List<Choice> alloweredAnswers = father.getAlternatives();
		if (alloweredAnswers.contains(JuriRule.YES) & alloweredAnswers.contains(JuriRule.NO)
				& alloweredAnswers.contains(JuriRule.MAYBE)) {

			rule = new JuriRule(father, childrenQuestion);
			for (QuestionOC childQuestion : childrenQuestion.keySet()) {
				// remove children questions from RootQASet
				father.getKnowledgeBase().getRootQASet().removeChild(childQuestion);

				// add children questions to father question
				father.addChild(childQuestion);
			}

			/*
			 * get the content of operator flag and mark rule as disjunctive if
			 * required. default is conjunctive, disjunctive = false
			 */
			Section<Operator> operator = Sections.findSuccessor(s,
					JuriTreeExpression.Operator.class);
			if (operator != null) {
				Section<EmbracedContent> operator_content = Sections.findSuccessor(operator,
						EmbracedContent.class);
				String operator_str = operator_content.getText().toLowerCase();

				if (operator_str.toLowerCase().equals(JuriTreeExpression.OR)) {
					rule.setDisjunctive(true);
				}
				else if (operator_str.toLowerCase().equals(JuriTreeExpression.SCORE)) {
					messages.add(new Message(Message.Type.ERROR, section.getText()
							+ ": Scoring not implemented yet."));
				}
			}
			return rule;
		}
		messages.add(Messages.objectCreationError(
				"Inner nodes must have the answer alternatives yes, no and maybe."));
		return null;
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

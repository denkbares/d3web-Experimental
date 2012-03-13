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
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.event.ArticleCreatedEvent;
import de.knowwe.jurisearch.tree.JuriTreeExpression.RoundBracketExp;
import de.knowwe.jurisearch.tree.JuriTreeExpression.RoundExpBracketExpContent;

/**
 * 
 * @author boehler
 * @created 19.01.2012
 */
public class JuriTreeHandler extends D3webSubtreeHandler<JuriTreeExpression> implements EventListener {

	private static final String OR = "oder";

	public JuriTreeHandler() {
		EventManager.getInstance().registerListener(this);
	}

	@Override
	public Collection<Message> create(Article article, Section<JuriTreeExpression> section) {
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(article.getWeb(), article.getTitle());
		JuriModel model = getModelOrCreate(kb);
		model.addRule(createJuriRule(kb, section));
		kb.getKnowledgeStore().addKnowledge(JuriModel.KNOWLEDGE_KIND, model);

		// Section<QuestionIdentifier> question =
		// Sections.findSuccessor(section, QuestionIdentifier.class);
		//
		// Section<QuestionIdentifier> fatherQuestion =
		// question.get().getFatherQuestion(question);
		// List<Section<QuestionIdentifier>> childrenQuestion =
		// question.get().getChildrenQuestion(question);
		//
		// System.out.println("Frage: "+question);
		// System.out.println("- Vater: "+fatherQuestion); for
		// (Section<QuestionIdentifier> child : childrenQuestion) {
		// System.out.println("- Kind: "+child); } System.out.println("");

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
		Section<Article> section = article.getSection();
		JuriTreeXmlGenerator jtxg = new JuriTreeXmlGenerator(section);
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
		JuriRule rule = new JuriRule();
		QuestionOC father = getQuestionOrCreate(kb, section.getText());
		rule.setFather(father);
		List<Section<QuestionIdentifier>> children = section.get().getChildrenQuestion(section);
		for (Section<QuestionIdentifier> child : children) {
			QuestionOC question = getQuestionOrCreate(kb, child.getText());
			rule.addChild(question);
		}

		/*
		 * get the content of round bracket exp and mark rule as disjunctive if
		 * required. default is conjunctive, disjunctive = false
		 */
		Section<RoundBracketExp> rbe = Sections.findChildOfType(s,
				RoundBracketExp.class);
		if (rbe != null) {
			Section<RoundExpBracketExpContent> rebec = Sections.findChildOfType(rbe,
					JuriTreeExpression.RoundExpBracketExpContent.class);
			String expr = rebec.getText().toLowerCase();

			if (expr.equals(OR)) {
				rule.setDisjunctive(true);
			}
		}
		// System.out.println(rule.toString());
		return rule;
	}

	/**
	 * Tries to retrieve an QuestionOC with the specified name, that is
	 * contained in the knowledge base. Otherwise create the question with the
	 * value alternatives yes, no and maybe.
	 * 
	 * @created 12.03.2012
	 * @param kb
	 * @param name
	 * @return
	 */
	private QuestionOC getQuestionOrCreate(KnowledgeBase kb, String name) {
		QuestionOC question = (QuestionOC) kb.getManager().search(name);
		if (question == null) {
			question = new QuestionOC(kb, name);
			question.addAlternative(JuriRule.YES);
			question.addAlternative(JuriRule.NO);
			question.addAlternative(JuriRule.MAYBE);
		}
		return question;
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

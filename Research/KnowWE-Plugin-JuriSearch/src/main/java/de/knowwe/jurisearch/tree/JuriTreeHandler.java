/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.jurisearch.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.event.ArticleCreatedEvent;
import de.knowwe.event.KDOMCreatedEvent;


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
		/*
		 * In the moment, do nothing.
		 *
		Section<QuestionIdentifier> question = Sections.findSuccessor(section, QuestionIdentifier.class);

		Section<QuestionIdentifier> fatherQuestion = question.get().getFatherQuestion(question);
		List<Section<QuestionIdentifier>> childrenQuestion = question.get().getChildrenQuestion(question);

		System.out.println("Frage: "+question);
		System.out.println("- Vater: "+fatherQuestion);
		for (Section<QuestionIdentifier> child : childrenQuestion) {
			System.out.println("- Kind: "+child);
		}
		System.out.println("");
		*/
		//System.out.println("zuuu");
		return new ArrayList<Message>(0);
	}


	public Class<? extends Event> getEvent() {
		return ArticleCreatedEvent.class;
	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		LinkedList<Class<? extends Event>> l = new LinkedList<Class<? extends Event>>();
		l.add(ArticleCreatedEvent.class);
		//l.add(Event.class);
		return l;
	}

	@Override
	public void notify(Event event) {
		ArticleCreatedEvent e = (ArticleCreatedEvent)event;
		Article article = e.getArticle();
		Section<Article> section = article.getSection();
		JuriTreeXmlGenerator jtxg = new JuriTreeXmlGenerator(section);
	}

}

/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.wisskont.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.event.PageRenderedEvent;

/**
 * 
 * @author jochenreutelshofer
 * @created 05.12.2012
 */
public class TermRecommender implements EventListener {

	private static final double WEIGHT_REFERENCE = 0.5;
	private static final double WEIGHT_DEFINITION = 1.0;
	private static final double WEIGHT_SEARCHED = 2.0;

	private static TermRecommender instance;

	public static TermRecommender getInstance() {
		if (instance == null) {
			instance = new TermRecommender();
		}
		return instance;
	}

	private final Map<String, RecommendationSet> data = new HashMap<String, RecommendationSet>();

	/**
	 * 
	 */
	private TermRecommender() {
		EventManager.getInstance().registerListener(this);
	}

	public List<String> getRankedTermList(UserContext user) {
		String username = user.getUserName();
		if (!data.containsKey(username)) {
			return new ArrayList<String>(0);
		}
		else {
			RecommendationSet recommendationSet = data.get(username);
			Collection<String> rankedTermList = recommendationSet.getRankedTermList();
			List<String> result = new ArrayList<String>();
			result.addAll(rankedTermList);
			return result;
		}
	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		Collection<Class<? extends Event>> result = new HashSet<Class<? extends Event>>();
		result.add(PageRenderedEvent.class);
		return result;
	}

	public void termSearched(UserContext user, String term) {
		RecommendationSet set = null;
		if (data.containsKey(user.getUserName())) {
			set = data.get(user.getUserName());
			set.discount(0.8);
		}
		else {
			set = new RecommendationSet();
			data.put(user.getUserName(), set);
		}
		set.addValue(term, WEIGHT_SEARCHED);
	}

	@Override
	public void notify(Event event) {
		if (event instanceof PageRenderedEvent) {
			String title = ((PageRenderedEvent) event).getTitle();
			UserContext user = ((PageRenderedEvent) event).getUser();
			Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB, title);

			RecommendationSet set = null;
			if (data.containsKey(user.getUserName())) {
				set = data.get(user.getUserName());
				set.discount(0.8);
			}
			else {
				set = new RecommendationSet();
				data.put(user.getUserName(), set);
			}

			List<Section<SimpleDefinition>> definitions = Sections.findSuccessorsOfType(
					article.getRootSection(), SimpleDefinition.class);
			for (Section<SimpleDefinition> def : definitions) {
				String termname = def.get().getTermName(def);
				set.addValue(termname, WEIGHT_DEFINITION);
			}

			List<Section<SimpleReference>> references = Sections.findSuccessorsOfType(
					article.getRootSection(), SimpleReference.class);
			for (Section<SimpleReference> ref : references) {
				String termname = ref.get().getTermName(ref);
				set.addValue(termname, WEIGHT_REFERENCE);
			}

		}

	}

	/**
	 * 
	 * @created 11.12.2012
	 * @param context
	 * @param term
	 */
	public void clearTerm(UserActionContext context, String term) {
		RecommendationSet recommendationSet = data.get(context.getUserName());
		if (recommendationSet != null) {
			recommendationSet.clearValue(term);
		}

	}

	/**
	 * 
	 * @created 11.12.2012
	 * @param context
	 * @param term
	 */
	public void expandTerm(UserActionContext context, String term) {
		// TODO Auto-generated method stub

	}

}

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

package de.knowwe.termbrowser.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.d3web.strings.Identifier;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.jspwiki.types.LinkType;
import de.knowwe.termbrowser.BrowserTerm;
import de.knowwe.termbrowser.HierarchyProvider;

/**
 * @author Jochen Reutelshöfer
 * @created 21.03.2014
 */
public class WikiPageHierarchyProvider implements HierarchyProvider<BrowserTerm> {

	@Override
	public void updateSettings(UserContext user) {
		// nothing to do
	}

	@Override
	public Collection<BrowserTerm> filterInterestingTerms(Collection<BrowserTerm> terms) {
		return Collections.emptyList();
	}

	@Override
	public List<BrowserTerm> getChildren(BrowserTerm term) {
		List<BrowserTerm> result = new ArrayList<BrowserTerm>();
		ArticleManager articleManager = Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB);
		String pageName = term.getIdentifier().getLastPathElement();
		Article article = articleManager.getArticle(pageName);
		if (article == null) {
			return result;
		}
		Section<RootType> rootSection = article.getRootSection();
		List<Section<LinkType>> successorsOfType = Sections.successors(rootSection, LinkType.class);
		for (Section<LinkType> linkSection : successorsOfType) {
			String targetPage = linkSection.get().getLink(linkSection);
			result.add(new BrowserTerm(new Identifier(targetPage)));
		}

		return result;
	}

	@Override
	public List<BrowserTerm> getParents(BrowserTerm term) {
		ArticleManager articleManager = Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB);
		Iterator<Article> articleIterator = articleManager.getArticles().iterator();
		while (articleIterator.hasNext()) {
			Article next = articleIterator.next();
			List<Section<LinkType>> successorsOfType = Sections.successors(next.getRootSection(), LinkType.class);
			for (Section<LinkType> linkSection : successorsOfType) {
				String targetPage = linkSection.get().getLink(linkSection);
				if (targetPage.equals(term.getIdentifier().getLastPathElement())) {
					List<BrowserTerm> result = new ArrayList<BrowserTerm>();
					result.add(new BrowserTerm(new Identifier(next.getTitle())));
					return result;
				}

			}
		}
		return Collections.emptyList();
	}

	@Override
	public Collection<BrowserTerm> getAllTerms() {
		ArticleManager articleManager = Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB);
		Iterator<Article> articleIterator = articleManager.getArticles().iterator();
		List<BrowserTerm> result = new ArrayList<BrowserTerm>();
		while (articleIterator.hasNext()) {
			result.add(new BrowserTerm(new Identifier(articleIterator.next().getTitle())));
		}
		return result;
	}

	@Override
	public Collection<BrowserTerm> getStartupTerms() {
		List<BrowserTerm> result = new ArrayList<BrowserTerm>();
		result.add(new BrowserTerm(new Identifier("Main")));
		return result;
	}

	@Override
	public boolean isSuccessorOf(BrowserTerm node1, BrowserTerm node2) {
		return isSubPageOf(node1, node2, new HashSet<BrowserTerm>());
	}

	private boolean isSubPageOf(BrowserTerm node1, BrowserTerm target, Set<BrowserTerm> terms) {
		terms.add(node1);

		if (getChildren(target).contains(node1)) {
			return true;
		}

		List<BrowserTerm> parents = getParents(node1);
		for (BrowserTerm parent : parents) {
			if (parent.equals(target)) {
				return true;
			}
			else {
				// beware of circles in the hierarchy network
				if (!terms.contains(parent)) {
					return isSubPageOf(parent, target, terms);
				}
			}
		}
		return false;
	}
}

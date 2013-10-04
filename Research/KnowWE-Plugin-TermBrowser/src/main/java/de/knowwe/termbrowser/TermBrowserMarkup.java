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
package de.knowwe.termbrowser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author jochenreutelshofer
 * @created 29.11.2012
 */
public class TermBrowserMarkup extends DefaultMarkupType {

	private static final String HIERARCHY = "hierarchy";
	private static final String SEARCH_SLOT = "searchslot";
	private static final String PREFIX_ABBREVIATION = "abbreviation";
	private static final String TITLE = "title";
	private static final DefaultMarkup MARKUP;

	public TermBrowserMarkup(DefaultMarkup markup) {
		super(markup);
		setIgnorePackageCompile(true);
		this.setRenderer(new TermBrowserMarkupRenderer());
	}

	static {
		MARKUP = new DefaultMarkup("termbrowser");
		MARKUP.addAnnotation(PackageManager.ANNOTATION_MASTER, false);
		MARKUP.addAnnotation(HIERARCHY, false);
		MARKUP.addAnnotation(TITLE, false);
		MARKUP.addAnnotation(SEARCH_SLOT, false, new String[] {
				"true", "false" });
		MARKUP.addAnnotation(PREFIX_ABBREVIATION, false, new String[] {
				"true", "false" });
	}

	public TermBrowserMarkup() {
		super(MARKUP);
		setIgnorePackageCompile(true);
		this.setRenderer(new TermBrowserMarkupRenderer());
	}

	public static String getCurrentTermbrowserMarkupMaster(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			return DefaultMarkupType.getAnnotation(termBrowser,
					PackageManager.ANNOTATION_MASTER);
		}
		return null;
	}

	public static String getCurrentTermbrowserMarkupTitle(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			return DefaultMarkupType.getAnnotation(termBrowser,
					TITLE);
		}
		return null;
	}

	public static boolean getCurrentTermbrowserMarkupSearchSlotFlag(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			String s = DefaultMarkupType.getAnnotation(termBrowser,
					SEARCH_SLOT);
			if (s == null) return false;
			if (s.equals("false")) return false;
			if (s.equals("true")) return true;
		}
		return false;
	}

	public static boolean getCurrentTermbrowserMarkupPrefixAbbreviationFlag(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			String s = DefaultMarkupType.getAnnotation(termBrowser, PREFIX_ABBREVIATION);
			if (s == null) return false;
			if (s.equals("false")) return false;
			if (s.equals("true")) return true;
		}
		return false;
	}

	public static List<String> getCurrentTermbrowserMarkupHierarchyRelations(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			String hierarchyData = DefaultMarkupType.getAnnotation(termBrowser,
					HIERARCHY);
			if (hierarchyData == null) return new ArrayList<String>();
			String[] split = hierarchyData.split(",");
			List<String> list = Arrays.asList(split);
			return list;
		}
		return null;
	}

	/**
	 * 
	 * @created 01.10.2013
	 * @param user
	 * @return
	 */
	private static Section<TermBrowserMarkup> getTermBrowserMarkup(UserContext user) {
		Article article = Environment.getInstance().getArticleManager(user.getWeb()).getArticle(
				user.getTitle());
		Section<TermBrowserMarkup> termBrowser = null;
		if (article != null) {
			termBrowser = Sections.findSuccessor(article.getRootSection(),
					TermBrowserMarkup.class);
		}
		if (termBrowser == null) {
			Article leftMenu = Environment.getInstance().getArticleManager(user.getWeb()).getArticle(
					"LeftMenu");
			termBrowser = Sections.findSuccessor(leftMenu.getRootSection(),
					TermBrowserMarkup.class);
		}
		return termBrowser;
	}

	class TermBrowserMarkupRenderer implements Renderer {

		@Override
		public void render(Section<?> section, UserContext user, RenderResult string) {
			String master = DefaultMarkupType.getAnnotation(section,
					PackageManager.ANNOTATION_MASTER);
			LinkToTermDefinitionProvider linkProvider = null;
			if (master == null) {
				linkProvider = new de.knowwe.compile.utils.IncrementalCompilerLinkToTermDefinitionProvider();
			}
			else {
				linkProvider = new PackageCompileLinkToTermDefinitionProvider();
			}

			boolean abbreviationFlag = TermBrowserMarkup.getCurrentTermbrowserMarkupPrefixAbbreviationFlag(user);

			TermBrowserRender renderer = new TermBrowserRender(user,
					linkProvider,
					master, abbreviationFlag);
			string.append(renderer.renderTermBrowser());
		}
	}

}

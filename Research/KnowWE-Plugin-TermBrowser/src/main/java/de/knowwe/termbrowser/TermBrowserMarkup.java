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
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.*;
import de.knowwe.core.compile.Compiler;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.compile.terminology.TermCompiler;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * @author Jochen Reutelshoefer
 * @created 29.11.2012
 */
public class TermBrowserMarkup extends DefaultMarkupType {

	private static final String IGNORE = "ignore";
	private static final String HIERARCHY = "hierarchy";
	private static final String CATEGORIES = "categories";
	private static final String SEARCH_SLOT = "searchslot";
	private static final String START_CONCEPT = "startConcept";
	private static final String CLEAR_ON_LOAD = "clearOnLoad";
	private static final String PREFIX_ABBREVIATION = "abbreviation";
	private static final String HIERARCHY_PROVIDER = "provider";
	private static final String AUTOMATED_TERM_COLLECTION = "autoCollect";
	private static final String TITLE = "title";
	private static final String SIZE = "size";
	private static final DefaultMarkup MARKUP;

	public TermBrowserMarkup(DefaultMarkup markup) {
		super(markup);
		this.setRenderer(new TermBrowserMarkupRenderer());
	}

	static {
		MARKUP = new DefaultMarkup("termbrowser");
		MARKUP.addAnnotation(PackageManager.MASTER_ATTRIBUTE_NAME, false);
		MARKUP.addAnnotation(HIERARCHY, false);
		MARKUP.addAnnotation(SIZE, false, Pattern.compile("[0-9]+"));
		MARKUP.addAnnotation(CATEGORIES, false);
		MARKUP.addAnnotation(IGNORE, false);
		MARKUP.addAnnotation(START_CONCEPT, false);
		MARKUP.addAnnotation(TITLE, false);
		MARKUP.addAnnotation(HIERARCHY_PROVIDER, false);
		MARKUP.addAnnotation(SEARCH_SLOT, false, "true", "false");
		MARKUP.addAnnotation(CLEAR_ON_LOAD, false, "true", "false");
		MARKUP.addAnnotation(PREFIX_ABBREVIATION, false, "true", "false");
		MARKUP.addAnnotation(AUTOMATED_TERM_COLLECTION, false, "true", "false");
	}

	public TermBrowserMarkup() {
		super(MARKUP);
		this.setRenderer(new TermBrowserMarkupRenderer());
	}

	public static int getCurrentTermBrowserMarkupSize(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			String sizeString = DefaultMarkupType.getAnnotation(termBrowser,
					SIZE);
			try {
				return Integer.parseInt(sizeString);
			}
			catch (NumberFormatException e) {
				// ignore
			}
		}
		return 0;
	}

	public static String getCurrentTermbrowserMarkupMaster(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			return DefaultMarkupType.getAnnotation(termBrowser,
					PackageManager.MASTER_ATTRIBUTE_NAME);
		}
		return null;
	}

	public static String getCurrentTermbrowserMarkupHierarchyProvider(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			return DefaultMarkupType.getAnnotation(termBrowser,
					HIERARCHY_PROVIDER);
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

	public static boolean getCurrentTermbrowserMarkupAutoCollectFlag(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			String s = DefaultMarkupType.getAnnotation(termBrowser,
					AUTOMATED_TERM_COLLECTION);
			if (s == null) return false;
			if (s.equals("false")) return false;
			if (s.equals("true")) return true;
		}
		return false;
	}

	public static boolean getCurrentTermbrowserMarkupClearOnLoadFlag(UserContext user) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			String s = DefaultMarkupType.getAnnotation(termBrowser,
					CLEAR_ON_LOAD);
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

	private static List<String> getCommaSeparatedAnnotationList(UserContext user, String annotationName) {
		Section<TermBrowserMarkup> termBrowser = getTermBrowserMarkup(user);
		if (termBrowser != null) {
			String hierarchyData = DefaultMarkupType.getAnnotation(termBrowser,
					annotationName);
			if (hierarchyData == null) return new ArrayList<String>();
			String[] split = hierarchyData.split(",");
			List<String> list = Arrays.asList(split);
			return Strings.trim(list);
		}
		return new ArrayList<String>(0);
	}

	public static List<String> getCurrentTermbrowserMarkupHierarchyRelations(UserContext user) {
		return getCommaSeparatedAnnotationList(user, HIERARCHY);
	}

	public static Collection<TerminologyManager> getTerminologyManager(UserContext user) {
		List<TerminologyManager> managers = new ArrayList<>();
		final Section<TermBrowserMarkup> termBrowserMarkup = getTermBrowserMarkup(user);
		if(termBrowserMarkup != null) {
			List<de.knowwe.core.compile.Compiler> allCompilers = termBrowserMarkup.getArticleManager()
					.getCompilerManager()
					.getCompilers();
			managers.addAll(allCompilers.stream().filter(compiler -> compiler instanceof TermCompiler).map(compiler -> ((TermCompiler) compiler).getTerminologyManager()).collect(Collectors.toList()));
		}
		return managers;
	}

	public static List<String> getCurrentTermbrowserMarkupHierarchyCategories(UserContext user) {
		return getCommaSeparatedAnnotationList(user, CATEGORIES);
	}

	public static List<String> getCurrentTermbrowserIgnoredTerms(UserContext user) {
		return getCommaSeparatedAnnotationList(user, IGNORE);
	}

	public static List<String> getCurrentTermbrowserMarkupStartConcept(UserContext user) {
		return getCommaSeparatedAnnotationList(user, START_CONCEPT);
	}

	public static Section<TermBrowserMarkup> getTermBrowserMarkup(UserContext user) {
		Article article = KnowWEUtils.getArticleManager(user.getWeb()).getArticle(
				user.getTitle());
		Section<TermBrowserMarkup> termBrowser = null;
		if (article != null) {
			termBrowser = Sections.successor(article.getRootSection(),
					TermBrowserMarkup.class);
		}
		if (termBrowser == null) {
			Article leftMenu = KnowWEUtils.getArticleManager(user.getWeb()).getArticle(
					"LeftMenu");
			termBrowser = Sections.successor(leftMenu.getRootSection(),
					TermBrowserMarkup.class);
		}
		return termBrowser;
	}

	class TermBrowserMarkupRenderer implements Renderer {

		@Override
		public void render(Section<?> section, UserContext user, RenderResult string) {
			String master = DefaultMarkupType.getAnnotation(section,
					PackageManager.MASTER_ATTRIBUTE_NAME);
			LinkToTermDefinitionProvider linkProvider;
			if (master == null) {
				// TODO: completely remove dependency to IncrementalCompiler
				try {
					linkProvider = (LinkToTermDefinitionProvider) Class.forName(
							"de.knowwe.compile.utils.IncrementalCompilerLinkToTermDefinitionProvider")
							.newInstance();
				}
				catch (Exception e) {
					linkProvider = new LinkToTermDefinitionProvider() {
						@Override
						public String getLinkToTermDefinition(Identifier name, String masterArticle) {
							return null;
						}
					};
				}
			}
			else {
				linkProvider = new PackageCompileLinkToTermDefinitionProvider();
			}

			boolean abbreviationFlag = TermBrowserMarkup.getCurrentTermbrowserMarkupPrefixAbbreviationFlag(user);

			// check whether term set should be cleared before rendering
			boolean clearData = TermBrowserMarkup.getCurrentTermbrowserMarkupClearOnLoadFlag(user);
			if (clearData) {
				TermSetManager.getInstance().clearList(user);
			}

/*			// insert static start term
			List<String> startConcept = TermBrowserMarkup.getCurrentTermbrowserMarkupStartConcept(user);
			if (startConcept != null && startConcept.trim().length() > 0) {
				TermSet recommendationSet = TermSetManager.getInstance().getRecommendationSet(
						user);
				recommendationSet.addValue(new Identifier(startConcept.split("#")), 1.0);
			}*/

			TermBrowserRenderer renderer = new TermBrowserRenderer(user,
					linkProvider,
					master, abbreviationFlag);
			string.append(renderer.renderTermBrowser());
		}
	}

}

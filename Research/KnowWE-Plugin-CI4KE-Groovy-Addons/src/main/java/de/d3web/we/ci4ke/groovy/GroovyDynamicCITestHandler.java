/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.we.ci4ke.groovy;

import groovy.lang.GroovyShell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.groovy.control.CompilerConfiguration;

import de.d3web.we.ci4ke.testing.CITest;
import de.d3web.we.ci4ke.testing.DynamicCITestHandler;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Implements the {@link DynamicCITestHandler} interface for Groovy CITests
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 22.11.2010
 */
public final class GroovyDynamicCITestHandler implements DynamicCITestHandler {
	
	public static final GroovyDynamicCITestHandler INSTANCE = new GroovyDynamicCITestHandler();

	private GroovyDynamicCITestHandler() {
	}

	@Override
	public Class<? extends CITest> getCITestClass(String testName) {
		Section<GroovyCITestType> testSection = getAllGroovyCITestSections().get(testName);
		return parseGroovyCITest(DefaultMarkupType.getContent(testSection));
	}

	/**
	 * Creates a Map containing all GroovyCITestSections
	 * 
	 * @created 22.11.2010
	 * @return
	 */
	@Override
	public Map<String, Class<? extends CITest>> getAllCITestClasses() {
		// return map
		Map<String, Class<? extends CITest>> classesMap =
				new HashMap<String, Class<? extends CITest>>();
		for (Map.Entry<String, Section<GroovyCITestType>> testSectionEntry : getAllGroovyCITestSections().entrySet()) {
			String testName = testSectionEntry.getKey();
			Section<GroovyCITestType> testSection = testSectionEntry.getValue();
			classesMap.put(testName, parseGroovyCITest(DefaultMarkupType.getContent(testSection)));
		}
		return classesMap;
	}

	public static Map<String, Section<GroovyCITestType>> getAllGroovyCITestSections() {
		// return map
		Map<String, Section<GroovyCITestType>> sectionsMap =
				new HashMap<String, Section<GroovyCITestType>>();
		for (Section<GroovyCITestType> section : getAllGroovyCITestSectionsByList()) {
			// a GroovyCITest is uniquely identified by its name-annotation
			String testName = DefaultMarkupType.getAnnotation(section, "name");
			if (sectionsMap.containsKey(testName)) {
				String warning = "Duplicate CITest with name '" + testName + "found! "
						+ "The value of the @name annotation for CITests has to be unique!";
				Logger.getLogger(GroovyDynamicCITestHandler.class.getName()).
						log(Level.WARNING, warning);
			}
			else {
				sectionsMap.put(testName, section);
			}
		}
		return sectionsMap;
	}

	public static List<Section<GroovyCITestType>> getAllGroovyCITestSectionsByList() {
		// our return list
		List<Section<GroovyCITestType>> sectionsList = new
				ArrayList<Section<GroovyCITestType>>();
		// a collection containing all wiki-articles
		Collection<Article> allWikiArticles = Environment.getInstance().
				getArticleManager(Environment.DEFAULT_WEB).getArticles();
		// iterate over all articles
		for (Article article : allWikiArticles) {
			// find all GroovyCITestType sections on this article...
			Sections.findSuccessorsOfType(article.getSection(),
					GroovyCITestType.class, sectionsList);
		}
		return sectionsList;
	}

	public static Class<? extends CITest> parseGroovyCITest(String groovyCodeOfCITestSection) {

		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass(GroovyCITestScript.class.getName());
		GroovyShell shell = new GroovyShell(cc);

		String groovycode = GroovyCITestSubtreeHandler.PREPEND + groovyCodeOfCITestSection;

		@SuppressWarnings("unchecked")
		Class<? extends CITest> clazz =
				(Class<? extends CITest>) shell.parse(groovycode).getClass();

		return clazz;
	}
}

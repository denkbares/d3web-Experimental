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

import org.codehaus.groovy.control.CompilerConfiguration;

import de.d3web.we.ci4ke.handling.CITest;
import de.d3web.we.ci4ke.handling.DynamicCITestHandler;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;

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
		Map<String, Section<GroovyCITestType>> groovyTests = getAllGroovyCITestSections();
		if (groovyTests.containsKey(testName)) {
			Section<GroovyCITestType> testSection = groovyTests.get(testName);

			CompilerConfiguration cc = new CompilerConfiguration();
			cc.setScriptBaseClass(GroovyCITestScript.class.getName());
			GroovyShell shell = new GroovyShell(cc);

			String groovycode = GroovyCITestSubtreeHandler.PREPEND
					+ DefaultMarkupType.getContent(testSection);

			@SuppressWarnings("unchecked")
			Class<? extends CITest> clazz =
					(Class<? extends CITest>) shell.parse(groovycode).getClass();

			return clazz;
		}
		return null;
	}

	/**
	 * Creates a Map containing all GroovyCITestSections
	 * 
	 * @created 22.11.2010
	 * @return
	 */
	public static Map<String, Section<GroovyCITestType>>
			getAllGroovyCITestSections() {
		// return map
		Map<String, Section<GroovyCITestType>> sectionsMap = new HashMap<String,
				Section<GroovyCITestType>>();
		// a collection containing all wiki-articles
		Collection<KnowWEArticle> allWikiArticles = KnowWEEnvironment.getInstance().
				getArticleManager(KnowWEEnvironment.DEFAULT_WEB).getArticles();
		// iterate over all articles
		for (KnowWEArticle article : allWikiArticles) {
			List<Section<GroovyCITestType>> sectionsList = new
					ArrayList<Section<GroovyCITestType>>();
			// find all GroovyCITestType sections on this article...
			article.getSection().findSuccessorsOfType(GroovyCITestType.class,
					sectionsList);
			// ...and add them to our Map
			for (Section<GroovyCITestType> section : sectionsList) {
				// a GroovyCITest is uniquely identified by its name-annotation
				String testName = DefaultMarkupType.getAnnotation(section, "name");
				sectionsMap.put(testName, section);
			}
		}
		return sectionsMap;
	}
}

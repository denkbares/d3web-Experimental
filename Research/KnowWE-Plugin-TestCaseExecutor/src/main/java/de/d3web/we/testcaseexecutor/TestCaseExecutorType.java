/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.testcaseexecutor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.KnowWERessourceLoader;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.wikiConnector.KnowWEWikiConnector;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author Reinhard Hatko
 * @created 10.12.2010
 */
public class TestCaseExecutorType extends DefaultMarkupType {

	public static final String ANNOTATION_MASTER = "master";
	public static final String ANNOTATION_FILE = "file";
	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("TestCaseExecutor");
		MARKUP.addAnnotation(ANNOTATION_MASTER, false);
		MARKUP.addAnnotation(ANNOTATION_FILE, false);

		KnowWERessourceLoader.getInstance().add("testcaseexecutor.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);
		KnowWERessourceLoader.getInstance().add("testcaseexecutor.css",
				KnowWERessourceLoader.RESOURCE_STYLESHEET);
	}

	public TestCaseExecutorType() {
		super(MARKUP);
	}

	@Override
	public KnowWEDomRenderer<TestCaseExecutorType> getRenderer() {
		return new TestCaseExecutorRender();
	}

	public static String getMaster(Section<TestCaseExecutorType> section) {
		String master = DefaultMarkupType.getAnnotation(section, ANNOTATION_MASTER);
		return master != null ? master : section.getArticle().getTitle();
	}

	/**
	 * 
	 * @created 13.09.2011
	 * @param section
	 */
	public static void execute(Section<TestCaseExecutorType> section, TestCaseAnalysis analysis) {
		String files = DefaultMarkupType.getAnnotation(section, ANNOTATION_FILE);
		if (files == null) return;

		Pattern pattern = Pattern.compile(files, Pattern.CASE_INSENSITIVE);
		
		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();
		String title = section.getArticle().getTitle();
		List<String> page = connector.getAttachmentFilenamesForPage(title);
		String master = getMaster(section);
		KnowledgeBase kb = D3webUtils.getKB(section.getWeb(), master);
		for (String file : page) {
			if (pattern.matcher(file).matches()) {
				try {
					String path = title + "/" + file;
					System.out.println(path);
					InputStream stream = connector.getAttachment(path).getInputStream();
					List<SequentialTestCase> cases = TestPersistence.getInstance().loadCases(
							stream, kb);
					TestCase suite = new TestCase();
					suite.setRepository(cases);
					suite.setKb(kb);
					analysis.runAndAnalyze(suite);

				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				catch (FactoryConfigurationError e) {
					e.printStackTrace();
				}
				catch (XMLStreamException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}

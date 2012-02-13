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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.KnowWERessourceLoader;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
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
	public static final String TEST_RESULT_KEY = "testcaseresult";
	public static final String TESTCASE_KEY = "testcase";

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
		addSubtreeHandler(new SubtreeHandler<TestCaseExecutorType>() {

			@Override
			public Collection<Message> create(KnowWEArticle article, Section<TestCaseExecutorType> section) {
				List<Message> errors = new LinkedList<Message>();

				String[] files = DefaultMarkupType.getAnnotations(section, ANNOTATION_FILE);
				KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();
				List<String> attachments = connector.getAttachmentFilenamesForPage(section.getArticle().getTitle());

				for (String file : files) {
					if (!attachments.contains(file)) errors.add(Messages.noSuchObjectError("File",
							file));

				}

				return errors;
			}
		});
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
	 * @return
	 */
	public static int execute(Section<TestCaseExecutorType> section, TestCaseAnalysis analysis) {
		String[] files = DefaultMarkupType.getAnnotations(section, ANNOTATION_FILE);
		if (files == null) return 0;

		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();
		String title = section.getArticle().getTitle();
		List<String> attachments = connector.getAttachmentFilenamesForPage(title);
		String master = getMaster(section);
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(section.getWeb(), master);
		List<SequentialTestCase> cases = new LinkedList<SequentialTestCase>();
		attachments.retainAll(Arrays.asList(files));

		for (String file : attachments) {
			try {
				String path = title + "/" + file;
				InputStream stream = connector.getAttachment(path).getInputStream();
				cases.addAll(TestPersistence.getInstance().loadCases(
						stream, kb));

			}
			catch (Exception e) {
				Logger.getLogger(TestCaseExecutorType.class.getName()).log(Level.FINE,
						"could not execute testcase", e);
			}

			TestCase suite = new TestCase();
			suite.setRepository(cases);
			suite.setKb(kb);
			TestCaseAnalysisReport report = analysis.runAndAnalyze(suite);
			section.getSectionStore().storeObject(TEST_RESULT_KEY, report);
			section.getSectionStore().storeObject(TESTCASE_KEY, suite);
		}

		return cases.size();
	}

}

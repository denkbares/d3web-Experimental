/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.testcase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.plugin.test.InitPluginManager;
import de.knowwe.core.utils.Strings;
import de.knowwe.testcases.TimeStampType;

/**
 * This class takes a sequential test case XML and creates markup for a
 * corresponding testcasetable for execution in KnowWE.
 * 
 * Ignores ExpectedFindings and Solutions
 * 
 * @author Reinhard Hatko
 * @created 20.01.2011
 */
public class STCToTestcaseTableConverter {

	private static final String LINESEP = "\r\n";

	public static String convert(List<SequentialTestCase> cases, String master) {
		StringBuilder markup = new StringBuilder();
		for (SequentialTestCase stc : cases) {
			markup.append(convert(stc, master));
			markup.append(LINESEP);
		}

		return markup.toString();
	}

	/**
	 * 
	 * @created 20.01.2011
	 * @param stc
	 * @return
	 */
	public static String convert(SequentialTestCase stc, String master) {
		List<RatedTestCase> cases = stc.getCases();

		if (cases.isEmpty()) return null;

		StringBuilder builder = new StringBuilder("%%TestcaseTable" + LINESEP);

		long startTime = cases.get(0).getTimeStamp().getTime();

		List<Question> questions = getQuestions(stc);

		builder.append(buildHeaderLine(questions)).append(LINESEP);

		for (RatedTestCase cas : stc.getCases()) {
			builder.append(buildTestLine(questions, cas, startTime));
		}

		builder.append(LINESEP);

		if (!Strings.isBlank(master)) builder.append("@master " + master);
		builder.append(LINESEP);
		builder.append("%");
		builder.append(LINESEP);

		return builder.toString();
	}

	/**
	 * 
	 * @created 20.01.2011
	 * @param stc
	 * @return
	 */
	private static List<Question> getQuestions(SequentialTestCase stc) {
		List<Question> questions = new ArrayList<Question>();

		for (RatedTestCase cas : stc.getCases()) {

			for (Finding finding : cas.getFindings()) {
				Question question = finding.getQuestion();
				if (!questions.contains(question)) questions.add(question);
			}

		}
		return questions;
	}

	/**
	 * 
	 * @created 20.01.2011
	 * @param questions
	 * @param cas
	 * @param startTime
	 * @return
	 */
	private static String buildTestLine(List<Question> questions, RatedTestCase cas, long startTime) {
		StringBuilder builder = new StringBuilder();

		String time = createTimeStamp(cas, startTime);

		builder.append("|").append(cas.getName());
		builder.append("|").append(time);

		nextQ: for (Question question : questions) {
			for (Finding finding : cas.getFindings()) {
				if (finding.getQuestion().equals(question)) {
					builder.append("|").append(finding.getValue());
					continue nextQ;
				}

			}
			builder.append("|").append(UnchangedType.UNCHANGED_VALUE_STRING);

		}

		builder.append(LINESEP);

		return builder.toString();
	}

	private static String createTimeStamp(RatedTestCase cas, long startTime) {
		return TimeStampType.createTimeAsTimeStamp(cas.getTimeStamp().getTime() - startTime);
	}

	/**
	 * 
	 * @created 20.01.2011
	 * @param questions
	 * @return
	 */
	private static String buildHeaderLine(List<Question> questions) {
		StringBuilder builder = new StringBuilder("||Name||Time");

		for (Question question : questions) {
			builder.append("||" + question.getName());
		}

		return builder.toString();
	}

	static String workspace = "d:/WiMVent/Faelle/";
	static String knowledgebase_fn = "knowledgebase.d3web";
	static String journal_fn = "20110714133256-Service.xml";
	static String stc_input = "d3web_" + journal_fn;
	static String tct_output = "tct_" + stc_input + ".txt";

	public static void main(String[] args) throws Exception {

		InitPluginManager.init();
		File kbFile = new File(workspace + knowledgebase_fn);
		File inputFile = new File(workspace + stc_input);
		File outputFile = new File(workspace + tct_output);
		KnowledgeBase kb = PersistenceManager.getInstance().load(kbFile);
		List<SequentialTestCase> cases = TestPersistence.getInstance().loadCases(
				inputFile.toURI().toURL(), kb);

		String markup = convert(cases, "Dashboard");
		System.out.println(markup);

		Writer writer = new BufferedWriter(new FileWriter(outputFile));
		try {
			writer.write(markup.toString());
		}
		finally {
			writer.close();
		}

	}

}

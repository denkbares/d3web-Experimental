/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.knowledgeExporter.tests;

import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import de.d3web.KnOfficeParser.SingleKBMIDObjectManager;
import de.d3web.KnOfficeParser.dashtree.QuestionnaireBuilder;
import de.d3web.KnOfficeParser.dashtree.SolutionsBuilder;
import de.d3web.KnOfficeParser.decisiontree.D3DTBuilder;
import de.d3web.KnOfficeParser.rule.D3ruleBuilder;
import de.d3web.KnOfficeParser.xcl.XCLd3webBuilder;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.knowledgeExporter.KnowledgeManager;

public abstract class KnowledgeExporterTest extends TestCase {

	protected KnowledgeBase kb;
	protected KnowledgeManager manager;

	// private InputStream getStream(String ressource) {
	// InputStream stream;
	// try {
	// stream = new ByteArrayInputStream(ressource.getBytes("UTF-8"));
	// } catch (UnsupportedEncodingException e1) {
	// e1.printStackTrace();
	// stream = null;
	// }
	//
	// return stream;
	// }

	protected void setUpKB(String diagnosis, String initQuestion,
			String decisionTree, String rules, String xcl) {

		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
		kb = kbm.getKnowledgeBase();

		if (initQuestion != null) {
			List<de.d3web.report.Message> messages = QuestionnaireBuilder
					.parse(new StringReader(initQuestion), new SingleKBMIDObjectManager(kbm));
			System.out.println(messages);
		}

		if (diagnosis != null) {
			List<de.d3web.report.Message> messages = SolutionsBuilder
					.parse(new StringReader(diagnosis), kbm, new SingleKBMIDObjectManager(kbm));
			System.out.println(messages);
		}

		if (decisionTree != null) {
			List<de.d3web.report.Message> messages = D3DTBuilder.parse(new StringReader(
					decisionTree), new SingleKBMIDObjectManager(kbm));
			System.out.println(messages);
		}

		if (rules != null) {
			D3ruleBuilder builder = new D3ruleBuilder("", false,
					new SingleKBMIDObjectManager(kbm));

			List<de.d3web.report.Message> messages = builder.addKnowledge(new StringReader(rules),
					new SingleKBMIDObjectManager(kbm), null);
			System.out.println(messages);
		}

		if (xcl != null) {
			XCLd3webBuilder builder = new XCLd3webBuilder("", new SingleKBMIDObjectManager(kbm));
			List<de.d3web.report.Message> messages = builder.addKnowledge(new StringReader(xcl),
					new SingleKBMIDObjectManager(kbm), null);
			System.out.println(messages);
		}

		manager = new KnowledgeManager(kb);
		setUpWriter();

	}

	protected void setUpKB2(String[] diagnosis, String[] initQuestion,
			String[] decisionTreeFormatted) {
		// TODO Auto-generated method stub

	}

	protected abstract void setUpWriter();

}

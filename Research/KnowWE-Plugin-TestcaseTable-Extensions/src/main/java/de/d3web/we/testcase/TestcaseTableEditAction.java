/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.testcases.table.TestcaseTableType;

/**
 * 
 * @author Alexander Strehler
 * @created 29.01.2012
 */
public class TestcaseTableEditAction extends AbstractAction {

	/**
	 * Beispielstring [ {'name': 'FrageNum','type': 'num'}, {'name':
	 * 'FrageOC','type': 'oc','alternatives': ['a1','a2','a3','a4','a5']},
	 * {'name': 'FrageYN','type': 'oc','alternatives': ['Yes','No']} ]
	 */
	@Override
	public void execute(UserActionContext context) throws IOException {

		// KnowledgeBase kb = D3webUtils.getKnowledgeBase(context.getWeb(),
		// context.getTitle());
		// List<Question> questions = new
		// ArrayList<Question>(kb.getManager().getQuestions());

		String sectionID = context.getParameter("kdomid");
		Section<?> tablecontent = Sections.getSection(sectionID);

		Section<TestcaseTableType> section = Sections.findAncestorOfType(tablecontent,
				TestcaseTableType.class);

		List<Question> questions = getQuestions(section, context);

		Collections.sort(questions, new Comparator<Question>() {

			@Override
			public int compare(Question o1, Question o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});

		StringBuilder bui = new StringBuilder();
		bui.append("[");
		for (Iterator<Question> it = questions.iterator(); it.hasNext(); it.next()) {

			Question q = it.next();
			if (q.getName().toLowerCase().equals("start")
					|| q.getName().toLowerCase().equals("now")) {
				continue;
			}

			bui.append("{");
			bui.append("'name': " + "'" + q.getName() + "'");
			bui.append(",");
			bui.append("'type': ");

			if (q instanceof QuestionNum) {
				bui.append("'num'");
			}
			else if (q instanceof QuestionOC) {
				bui.append("'oc'");
			}
			else if (q instanceof QuestionMC) {
				bui.append("'mc'");
			}
			else if (q instanceof QuestionDate) {
				bui.append("'date'");
			}
			else if (q instanceof QuestionText) {
				bui.append("'text'");
			}

			if (q instanceof QuestionChoice) {
				bui.append(",");
				bui.append("'alternatives': ");
				bui.append("[");
				List<Choice> answers = ((QuestionChoice) q).getAllAlternatives();
				for (Choice choice : answers) {
					bui.append("'" + choice.getName() + "'");
					bui.append(",");
				}
				bui.replace(bui.length() - 1, bui.length(), "");
				bui.append("]");
			}

			bui.append("}");
			// if (it.hasNext())
			bui.append(",");
		}
		bui.replace(bui.length() - 1, bui.length(), "");

		bui.append("]");

		context.setContentType("application/json");
		context.getWriter().write(bui.toString());
	}

	private static List<Question> getQuestions(Section<TestcaseTableType> section, UserContext user) {
		Collection<Question> questions = new HashSet<Question>();
		String[] kbpackages = getPackages(section);
		KnowWEEnvironment env = KnowWEEnvironment.getInstance();
		KnowWEPackageManager packageManager = env.getPackageManager(section.getWeb());
		KnowWEArticleManager articleManager = env.getArticleManager(user.getWeb());
		for (String kbpackage : kbpackages) {
			Set<String> articlesReferringTo = packageManager.getCompilingArticles(kbpackage);
			for (String masterTitle : articlesReferringTo) {
				KnowWEArticle masterArticle = articleManager.getArticle(masterTitle);
				KnowledgeBase kb = D3webUtils.getKnowledgeBase(masterArticle.getWeb(),
						masterArticle.getTitle());
				questions.addAll(kb.getManager().getQuestions());
			}
		}
		return new ArrayList<Question>(questions);
	}

	private static String[] getPackages(Section<TestcaseTableType> section) {
		String[] kbpackages = DefaultMarkupType.getAnnotations(section,
				KnowWEPackageManager.PACKAGE_ATTRIBUTE_NAME);
		if (kbpackages.length == 0) {
			kbpackages = new String[] { KnowWEPackageManager.DEFAULT_PACKAGE };
		}
		return kbpackages;
	}

}

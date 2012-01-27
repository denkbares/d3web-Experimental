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
package de.knowwe.sessiondebugger;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.utilities.Pair;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.ContentType;

/**
 * Renderer for TestCasePlayerType
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 19.01.2012
 */
public class TestCasePlayerRenderer extends KnowWEDomRenderer<ContentType> {

	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
	public static String SELECTOR_KEY = "selector";

	@Override
	public void render(KnowWEArticle article, Section<ContentType> section, UserContext user, StringBuilder string) {
		if (user == null || user.getSession() == null) {
			return;
		}
		KnowWEPackageManager packageManager = KnowWEEnvironment.getInstance().getPackageManager(
				article.getWeb());
		List<Section> sectionsInPackage = new LinkedList<Section>();
		for (String s : section.getPackageNames()) {
			sectionsInPackage.addAll(packageManager.getSectionsOfPackage(s));
		}
		List<Pair<TestCaseProvider, Section>> providers = new LinkedList<Pair<TestCaseProvider, Section>>();
		for (Section s : sectionsInPackage) {
			TestCaseProvider testCaseProvider = (TestCaseProvider) s.getSectionStore().getObject(
					TestCaseProvider.KEY);
			if (testCaseProvider != null) {
				providers.add(new Pair<TestCaseProvider, Section>(testCaseProvider, s));
			}
		}
		string.append(KnowWEUtils.maskHTML("<span id='" + section.getID() + "'>"));

		if (providers.size() == 0) {
			string.append("No TestCaseProvider found in the packages: " + section.getPackageNames());
		}
		else {
			String selectedID = (String) user.getSession().getAttribute(
					SELECTOR_KEY + "_" + section.getID());
			StringBuffer selectsb = new StringBuffer();
			// if no pair is selected, use the first
			Pair<TestCaseProvider, Section> selectedPair = providers.get(0);
			selectsb.append("Select TestCase: <select id=selector" +
					section.getID()
					+ " onchange=\"SessionDebugger.change('" + section.getID() +
					"', this.options[this.selectedIndex].value);\">");
			for (Pair<TestCaseProvider, Section> pair : providers) {
				String id = pair.getB().getID();
				if (id.equals(selectedID)) {
					selectsb.append("<option value='" + id + "' selected='selected'>"
							+ pair.getA().getName() + "</option>");
					selectedPair = pair;
				}
				else {
					selectsb.append("<option value='" + id + "'>"
							+ pair.getA().getName() + "</option>");
				}
			}
			selectsb.append("</select>");
			TestCaseProvider provider = selectedPair.getA();
			string.append(KnowWEUtils.maskHTML(selectsb.toString()));
			Session session = provider.getActualSession(user.getUserName());

			if (session == null) {
				string.append("No knowledge base found.\n");
			}
			else {
				TestCase testCase = provider.getTestCase();
				SessionDebugStatus status = provider.getDebugStatus(user.getUserName());

				if (status.getSession() != session) {
					status.setSession(session);
				}

				if (testCase != null) {
					Collection<Question> usedQuestions = TestCaseUtils.getUsedQuestions(testCase);
					string.append("\n|| ||Date");
					for (Question q : usedQuestions) {
						string.append("||" + q.getName());
					}
					string.append("||Checks");
					string.append("\n");
					for (Date date : testCase.chronology()) {
						string.append("|");
						String dateString = dateFormat.format(date);
						if (status.getLastExecuted() == null
									|| status.getLastExecuted().before(date)) {
							StringBuffer sb = new StringBuffer();
							String js = "SessionDebugger.send("
										+ "'"
										+ selectedPair.getB().getID()
										+ "', '" + dateString + "');";
							sb.append("<a href=\"javascript:" + js + ";undefined;\">");
							sb.append("Play");
							sb.append("</a>");
							string.append(KnowWEUtils.maskHTML(sb.toString()));
						}
						else {
							string.append("%%(color:silver;)Play%%");
						}
						string.append("|" + dateString);
						for (Question q : usedQuestions) {
							Finding finding = testCase.getFinding(date, q);
							if (finding != null) {
								string.append("|" + finding.getValue());
							}
							else {
								string.append("| ");
							}
						}
						Collection<Pair<Check, Boolean>> checkResults = status.getCheckResults(date);
						if (checkResults == null) {
							if (testCase.getChecks(date).isEmpty()) {
								string.append("| ");
							}
							else {
								string.append("|");
								for (Check c : testCase.getChecks(date)) {
									string.append(c.getCondition() + "\\\\");
								}
								string.replace(string.lastIndexOf("\\\\"), string.length(), "");
							}
						}
						else {
							if (checkResults.isEmpty()) {
								string.append("| ");
							}
							else {
								string.append("|");
								StringBuffer sb = new StringBuffer();
								for (Pair<Check, Boolean> p : checkResults) {
									if (p.getB()) {
										sb.append("%%(background-color:lime;)"
													+ p.getA().getCondition() + "%%\\\\");
									}
									else {
										sb.append("%%(background-color:red;)"
													+ p.getA().getCondition()
													+ "%%\\\\");
									}
								}
								sb.replace(sb.lastIndexOf("\\\\"), sb.length(), "");
								string.append(sb.toString());
							}
						}
						string.append("\n");
					}
				}
			}
		}
		string.append(KnowWEUtils.maskHTML("</span>"));
	}

}

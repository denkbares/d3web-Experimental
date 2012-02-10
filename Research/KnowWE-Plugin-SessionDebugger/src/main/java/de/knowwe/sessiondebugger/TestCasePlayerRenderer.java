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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;

import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.utilities.Pair;
import de.d3web.core.utilities.Triple;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;
import de.d3web.we.testcase.kdom.TimeStampType;
import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.ContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * Renderer for TestCasePlayerType
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 19.01.2012
 */
public class TestCasePlayerRenderer extends KnowWEDomRenderer<ContentType> {

	private static final String QUESTIONS_SEPARATOR = "#####";
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
	private static String SELECTOR_KEY = "selector";
	private static String QUESTION_SELECTOR_KEY = "question_selector";

	@Override
	public void render(KnowWEArticle article, Section<ContentType> section, UserContext user, StringBuilder result) {
		StringBuilder string = new StringBuilder();
		if (user == null || user.getSession() == null) {
			return;
		}
		KnowWEPackageManager packageManager = KnowWEEnvironment.getInstance().getPackageManager(
				article.getWeb());
		String[] kbpackages = DefaultMarkupType.getAnnotations(section.getFather(), "uses");
		List<Triple<TestCaseProvider, Section<?>, KnowWEArticle>> providers = new LinkedList<Triple<TestCaseProvider, Section<?>, KnowWEArticle>>();
		for (String kbpackage : kbpackages) {
			List<Section<?>> sectionsInPackage = packageManager.getSectionsOfPackage(kbpackage);
			Set<String> articlesReferringTo = packageManager.getArticlesReferringTo(kbpackage);
			KnowWEArticleManager articleManager = KnowWEEnvironment.getInstance().getArticleManager(
					user.getWeb());
			for (String masterTitle : articlesReferringTo) {
				KnowWEArticle masterarticle = articleManager.getArticle(masterTitle);
				for (Section<?> packagesections : sectionsInPackage) {
					TestCaseProviderStorage testCaseProviderStorage = (TestCaseProviderStorage) packagesections.getSectionStore().getObject(
							masterarticle,
							TestCaseProviderStorage.KEY);
					if (testCaseProviderStorage != null) {
						for (TestCaseProvider testCaseProvider : testCaseProviderStorage.getTestCaseProviders()) {
							providers.add(new Triple<TestCaseProvider, Section<?>, KnowWEArticle>(
									testCaseProvider,
									packagesections, masterarticle));
						}
					}
				}
			}
		}
		string.append(KnowWEUtils.maskHTML("<div id='" + section.getID() + "'>"));

		if (providers.size() == 0) {
			string.append("No TestCaseProvider found in the packages: " + section.getPackageNames());
		}
		else {
			Triple<TestCaseProvider, Section<?>, KnowWEArticle> selectedTriple = renderTestCaseSelection(
					section,
					user, string, providers);
			TestCaseProvider provider = selectedTriple.getA();
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
					string.append(" Start: " + dateFormat.format(testCase.getStartDate()));

					StringBuilder html = new StringBuilder();
					html.append(" <a onclick='SessionDebugger.reset();'><img src='KnowWEExtension/testcaseplayer/icon/stop.gif'></a>");
					string.append(KnowWEUtils.maskHTML(html.toString()));
					// get Question from cookie
					String additionalQuestions = null;
					String cookiename = "additionalQuestions" + article.getTitle();
					for (Cookie cookie : user.getRequest().getCookies()) {
						try {
							if (URLDecoder.decode(cookie.getName(), "UTF-8").equals(cookiename)) {
								additionalQuestions = URLDecoder.decode(cookie.getValue(), "UTF-8");
								break;
							}
						}
						catch (UnsupportedEncodingException e) {
							additionalQuestions = cookie.getValue();
							Logger.getLogger(getClass()).error(
										"Could not decode the value of the cookie " + cookiename
												+ ":" + cookie.getValue());
						}
					}
					String[] questionStrings = new String[0];
					if (additionalQuestions != null && !additionalQuestions.isEmpty()) {
						questionStrings = additionalQuestions.split(QUESTIONS_SEPARATOR);
					}
					Collection<Question> usedQuestions = TestCaseUtils.getUsedQuestions(testCase);
					string.append("\n|| ||Time");
					for (Question q : usedQuestions) {
						string.append("||" + q.getName());
					}
					string.append("||Checks");
					TerminologyManager manager = session.getKnowledgeBase().getManager();
					renderObservationQuestionsHeader(string, status, additionalQuestions,
							questionStrings, manager);
					Question selectedQuestion = renderObservationQuestionAdder(section, user,
							string, questionStrings, manager,
							additionalQuestions);
					string.append("\n");
					for (Date date : testCase.chronology()) {
						String dateString = dateFormat.format(date);
						renderRunTo(string, selectedTriple, status, date, dateString);
						// render date cell
						string.append("|"
								+ TimeStampType.createTimeAsTimeStamp(date.getTime()
										- testCase.getStartDate().getTime()));
						// render values of questions
						for (Question q : usedQuestions) {
							Finding finding = testCase.getFinding(date, q);
							if (finding != null) {
								string.append("|" + finding.getValue());
							}
							else {
								string.append("| ");
							}
						}
						renderCheckResults(string, testCase, status, date);
						// render observations
						for (String s : questionStrings) {
							Question question = manager.searchQuestion(s);
							if (question == null) {
								string.append("| ");
							}
							else {
								appendValueCell(string, status, question, date);
							}
						}
						if (selectedQuestion != null) {
							appendValueCell(string, status, selectedQuestion, date);
						}
						string.append("\n");
					}
					// string.append("/%\n");
				}
			}
		}
		string.append(KnowWEUtils.maskHTML("</div>"));
		result.append(string.toString());
	}

	private void appendValueCell(StringBuilder string, SessionDebugStatus status, Question question, Date date) {
		Value value = status.getValue(question, date);
		if (value != null) {
			string.append("|" + value);
		}
		else {
			string.append("| ");
		}
	}

	private void renderObservationQuestionsHeader(StringBuilder string, SessionDebugStatus status, String additionalQuestions, String[] questionStrings, TerminologyManager manager) {
		for (String s : questionStrings) {
			Question question = manager.searchQuestion(s);
			if (question != null) {
				string.append("||" + s);
			}
			else {
				string.append("||%%(color:silver;)" + s + "%%");
			}
			String newQuestionsString = additionalQuestions;
			newQuestionsString = newQuestionsString.replace(s, "");
			newQuestionsString = newQuestionsString.replace(QUESTIONS_SEPARATOR
					+ QUESTIONS_SEPARATOR,
					QUESTIONS_SEPARATOR);
			if (newQuestionsString.startsWith(QUESTIONS_SEPARATOR)) {
				newQuestionsString = newQuestionsString.replaceFirst(
						QUESTIONS_SEPARATOR, "");
			}
			if (newQuestionsString.endsWith(QUESTIONS_SEPARATOR)) {
				newQuestionsString = newQuestionsString.substring(0,
						newQuestionsString.length() - QUESTIONS_SEPARATOR.length());
			}
			string.append(KnowWEUtils.maskHTML(" <input type=\"button\" value=\"-\" onclick=\"SessionDebugger.addCookie(&quot;"
					+ newQuestionsString
					+ "&quot;);\">"));
		}
	}

	private void renderRunTo(StringBuilder string, Triple<TestCaseProvider, Section<?>, KnowWEArticle> selectedTriple, SessionDebugStatus status, Date date, String dateString) {
		string.append("|");
		if (status.getLastExecuted() == null
					|| status.getLastExecuted().before(date)) {
			StringBuffer sb = new StringBuffer();
			String js = "SessionDebugger.send("
						+ "'"
						+ selectedTriple.getB().getID()
						+ "', '" + dateString
						+ "', '" + selectedTriple.getA().getName()
						+ "', '" + selectedTriple.getC().getTitle() + "');";
			sb.append("<a href=\"javascript:" + js + ";undefined;\">");
			sb.append("<img src='KnowWEExtension/testcaseplayer/icon/runto.png'>");
			sb.append("</a>");
			string.append(KnowWEUtils.maskHTML(sb.toString()));
		}
		else {
			Collection<Pair<Check, Boolean>> checkResults = status.getCheckResults(date);
			boolean ok = true;
			if (checkResults != null) {
				for (Pair<Check, Boolean> pair : checkResults) {
					ok &= pair.getB();
				}
			}
			if (ok) {
				string.append(KnowWEUtils.maskHTML("<img src='KnowWEExtension/testcaseplayer/icon/done.png'>"));
			}
			else {
				string.append(KnowWEUtils.maskHTML("<img src='KnowWEExtension/testcaseplayer/icon/error.png'>"));
			}
		}
	}

	private void renderCheckResults(StringBuilder string, TestCase testCase, SessionDebugStatus status, Date date) {
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
						sb.append("%%(background-color:" + StyleRenderer.CONDITION_FULLFILLED
								+ ";)"
									+ p.getA().getCondition() + "%%\\\\");
					}
					else {
						sb.append("%%(background-color:" + StyleRenderer.CONDITION_FALSE + ";)"
									+ p.getA().getCondition()
									+ "%%\\\\");
					}
				}
				sb.replace(sb.lastIndexOf("\\\\"), sb.length(), "");
				string.append(sb.toString());
			}
		}
	}

	private Question renderObservationQuestionAdder(Section<ContentType> section, UserContext user, StringBuilder string, String[] questionStrings, TerminologyManager manager, String questionString) {
		String key = QUESTION_SELECTOR_KEY + "_" + section.getID();
		String selectedQuestion = (String) user.getSession().getAttribute(
				key);
		Question question = null;
		StringBuffer selectsb2 = new StringBuffer();
		selectsb2.append("||<form><select name=\"toAdd\" id=adder"
				+ section.getID()
				+ " onchange=\"SessionDebugger.change('"
				+ key
							+ "', this.options[this.selectedIndex].value);\">");
		HashSet<String> alreadyAddedQuestions = new HashSet<String>(Arrays.asList(questionStrings));
		selectsb2.append("<option value='--'>--</option>");
		boolean foundone = false;
		for (Question q : manager.getQuestions()) {
			if (!alreadyAddedQuestions.contains(q.getName())) {
				if (q.getName().equals(selectedQuestion)) {
					selectsb2.append("<option selected='selected' value='" + q.getName() + "'>"
							+ q.getName() + "</option>");
					question = q;
				}
				else {
					selectsb2.append("<option value='" + q.getName() + "'>" + q.getName()
							+ "</option>");
				}
				foundone = true;
			}
		}
		selectsb2.append("</select>");
		if (question != null && !question.getName().equals(selectedQuestion)) {
			user.getSession().setAttribute(key, question.getName());
		}
		if (questionString != null && !questionString.isEmpty()) {
			selectsb2.append("<input " +
					(question == null ? "disabled='disabled'" : "")
					+ " type=\"button\" value=\"+\" onclick=\"SessionDebugger.addCookie(&quot;"
					+ questionString
					+ QUESTIONS_SEPARATOR
					+ "&quot;+this.form.toAdd.options[toAdd.selectedIndex].value);\"></form>");
		}
		else {
			selectsb2.append("<input type=\"button\" value=\"+\" onclick=\"SessionDebugger.addCookie(this.form.toAdd.options[toAdd.selectedIndex].value);\"></form>");
		}
		if (foundone) {
			string.append(KnowWEUtils.maskHTML(selectsb2.toString()));
		}
		return question;
	}

	private Triple<TestCaseProvider, Section<?>, KnowWEArticle> renderTestCaseSelection(Section<ContentType> section, UserContext user, StringBuilder string, List<Triple<TestCaseProvider, Section<?>, KnowWEArticle>> providers) {
		String key = SELECTOR_KEY + "_" + section.getID();
		String selectedID = (String) user.getSession().getAttribute(
				key);
		StringBuffer selectsb = new StringBuffer();
		// if no pair is selected, use the first
		Triple<TestCaseProvider, Section<?>, KnowWEArticle> selectedPair = providers.get(0);
		selectsb.append("Select TestCase: <select id=selector" + section.getID()
				+ " onchange=\"SessionDebugger.change('" + key
				+ "', this.options[this.selectedIndex].value);\">");
		for (Triple<TestCaseProvider, Section<?>, KnowWEArticle> triple : providers) {
			String id = triple.getC().getTitle() + "/" + triple.getA().getName();
			if (id.equals(selectedID)) {
				selectsb.append("<option value='" + id + "' selected='selected'>"
						+ triple.getA().getName() + "</option>");
				selectedPair = triple;
			}
			else {
				selectsb.append("<option value='" + id + "'>"
						+ triple.getA().getName() + "</option>");
			}
		}
		selectsb.append("</select>");
		string.append(KnowWEUtils.maskHTML(selectsb.toString()));
		return selectedPair;
	}
}

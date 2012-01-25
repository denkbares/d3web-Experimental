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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.utilities.Pair;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;
import de.d3web.testcase.stc.STCWrapper;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.ConnectorAttachment;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Renderer for SessionDebuggerType
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 19.01.2012
 */
public class SessionDebuggerRenderer extends DefaultMarkupRenderer<SessionDebuggerType> {

	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");

	public SessionDebuggerRenderer() {
		super(false);
	}

	@Override
	public void renderContents(KnowWEArticle article, Section<SessionDebuggerType> section, UserContext user, StringBuilder string) {
		if (user == null || user.getSession() == null) {
			return;
		}
		string.append(KnowWEUtils.maskHTML("<span id='" + section.getID() + "'>"));

		String masterArticleName = DefaultMarkupType.getAnnotation(section,
				SessionDebuggerType.ANNOTATION_MASTER);
		Session session = D3webUtils.getSession(masterArticleName, user, article.getWeb());
		if (session == null) {
			string.append("No knowledge base for: " + masterArticleName + "\n");
		}
		else {
			String stc_file_string = DefaultMarkupType.getAnnotation(section,
					SessionDebuggerType.STC);
			Collection<ConnectorAttachment> attachments = KnowWEEnvironment.getInstance().getWikiConnector().getAttachments();
			ConnectorAttachment stcfile = null;
			for (ConnectorAttachment attachment : attachments) {
				if (attachment.getFileName().equals(stc_file_string)
						&& attachment.getParentName().equals(article.getTitle())) {
					stcfile = attachment;
				}
			}
			if (stcfile == null) {
				string.append("STC file " + session.getKnowledgeBase().getName()
						+ " cannot be found attached to this article.\n");
			}
			else {
				SessionDebugStatus status = (SessionDebugStatus) user.getSession().getAttribute(
						section.getID());
				if (status == null) {
					status = new SessionDebugStatus(stcfile.getDate(), session);
					user.getSession().setAttribute(section.getID(), status);
				}
				else if (status.getSession() != session) {
					status.setSession(session);
				}
				TestCase testCase = status.getTestCase();
				if (testCase == null || status.getStcFileDate().before(stcfile.getDate())) {
					testCase = new STCWrapper(loadSTC(string, session, stc_file_string, stcfile));
					status.setTestCase(testCase);
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
									+ section.getID()
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

	private SequentialTestCase loadSTC(StringBuilder string, Session session, String stc_file_string, ConnectorAttachment stcfile) {
		try {
			List<SequentialTestCase> cases = TestPersistence.getInstance().loadCases(
						stcfile.getInputStream(), session.getKnowledgeBase());
			if (cases.size() != 1) {
				string.append("The attached SequentialTestCase file " + stc_file_string
							+ " has " + cases.size()
							+ " cases. Only files with exactly one case are allowed.\n");
				return null;
			}
			else {
				return cases.get(0);
			}
		}
		catch (XMLStreamException e) {
			string.append("File " + stc_file_string + " does not contain correct xml markup.");
			return null;
		}
		catch (IOException e) {
			string.append("File " + stc_file_string + " is not accessible.");
			return null;
		}
	}

}

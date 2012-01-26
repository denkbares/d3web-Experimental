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
package de.knowwe.sessiondebugger.stc;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.testcase.model.TestCase;
import de.d3web.testcase.stc.STCWrapper;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.wikiConnector.ConnectorAttachment;
import de.knowwe.sessiondebugger.SessionDebugStatus;
import de.knowwe.sessiondebugger.TestCaseProvider;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.01.2012
 */
public class STCTestCaseProvider implements TestCaseProvider {

	private TestCase testCase;
	private ConnectorAttachment attachment;
	private List<Message> messages = new LinkedList<Message>();
	private final String master;
	private final String web;
	private final Map<String, SessionDebugStatus> statusPerUser = new HashMap<String, SessionDebugStatus>();
	private final String title;
	private final String fileName;

	public STCTestCaseProvider(String master, String web, String fileName, String title) {
		super();
		this.fileName = fileName;
		this.title = title;
		this.master = master;
		this.web = web;
	}

	@Override
	public TestCase getTestCase() {
		Collection<ConnectorAttachment> attachments = KnowWEEnvironment.getInstance().getWikiConnector().getAttachments();
		ConnectorAttachment actualAttachment = null;
		for (ConnectorAttachment attachment : attachments) {
			if (attachment.getFileName().equals(fileName)
					&& attachment.getParentName().equals(title)) {
				actualAttachment = attachment;
				break;
			}
		}
		if (actualAttachment == null) {
			messages.add(Messages.error("STC file " + fileName
					+ " cannot be found attached to this article.\n"));
			return null;
		}
		if (attachment == null || attachment.getDate() != actualAttachment.getDate()) {
			attachment = actualAttachment;
			parse(master);
		}
		return testCase;
	}

	public void parse(String master) {
		messages.clear();
		statusPerUser.clear();
		KnowledgeBase kb = D3webModule.getKnowledgeBase(web, master);
		if (kb == null) {
			messages.add(Messages.error("Kb not found."));
			return;
		}
		try {
			List<SequentialTestCase> cases = TestPersistence.getInstance().loadCases(
					attachment.getInputStream(), kb);
			if (cases.size() != 1) {
				messages.add(Messages.error("The attached SequentialTestCase file "
						+ attachment.getFileName()
							+ " has " + cases.size()
							+ " cases. Only files with exactly one case are allowed."));
				return;
			}
			else {
				testCase = new STCWrapper(cases.get(0));
			}
		}
		catch (XMLStreamException e) {
			messages.add(Messages.error("File " + attachment.getFileName()
					+ " does not contain correct xml markup."));
		}
		catch (IOException e) {
			messages.add(Messages.error("File " + attachment.getFileName() + " is not accessible."));
		}
	}

	public List<Message> getMessages() {
		return Collections.unmodifiableList(messages);
	}

	@Override
	public Session getActualSession(String user) {
		return D3webUtils.getSession(master, user, web);
	}

	@Override
	public void storeSession(Session session, String user) {
		String sessionId = KnowWEEnvironment.generateDefaultID(master);
		D3webModule.getBroker(user, web).addSession(sessionId, session);
		getDebugStatus(user).setSession(session);
	}

	@Override
	public SessionDebugStatus getDebugStatus(String user) {
		SessionDebugStatus status = statusPerUser.get(user);
		if (status == null) {
			status = new SessionDebugStatus(getActualSession(user));
			statusPerUser.put(user, status);
		}
		return status;
	}

}

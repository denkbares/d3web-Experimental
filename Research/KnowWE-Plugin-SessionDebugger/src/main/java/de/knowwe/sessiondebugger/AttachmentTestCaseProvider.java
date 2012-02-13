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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.session.Session;
import de.d3web.testcase.model.TestCase;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.ConnectorAttachment;

/**
 * Abstract class providing all methods to create a {@link TestCaseProvider}
 * based on an Attachment
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.01.2012
 */
public abstract class AttachmentTestCaseProvider implements TestCaseProvider {

	protected TestCase testCase;
	protected ConnectorAttachment attachment;
	protected List<Message> messages = new LinkedList<Message>();
	protected final KnowWEArticle article;
	private final Map<String, SessionDebugStatus> statusPerUser = new HashMap<String, SessionDebugStatus>();

	public AttachmentTestCaseProvider(KnowWEArticle article, ConnectorAttachment attachment) {
		super();
		this.article = article;
		this.attachment = attachment;
		parse();
	}

	@Override
	public TestCase getTestCase() {
		ConnectorAttachment actualAttachment = KnowWEUtils.getAttachment(
				attachment.getParentName(),
				attachment.getFileName());
		if (actualAttachment == null) {
			messages.clear();
			statusPerUser.clear();
			messages.add(Messages.error("File " + attachment.getFileName()
					+ " cannot be found attached to this article.\n"));
			return null;
		}
		if (attachment == null || attachment.getDate() != actualAttachment.getDate()) {
			attachment = actualAttachment;
			messages.clear();
			statusPerUser.clear();
			parse();
		}
		return testCase;
	}

	protected abstract void parse();

	@Override
	public List<Message> getMessages() {
		return Collections.unmodifiableList(messages);
	}

	@Override
	public Session getActualSession(String user) {
		return D3webUtils.getSession(article.getTitle(), user, article.getWeb());
	}

	@Override
	public void storeSession(Session session, String user) {
		String sessionId = KnowWEEnvironment.generateDefaultID(article.getTitle());
		D3webUtils.getBroker(user, article.getWeb()).addSession(sessionId, session);
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

	@Override
	public String getName() {
		return attachment.getFullName();
	}

}
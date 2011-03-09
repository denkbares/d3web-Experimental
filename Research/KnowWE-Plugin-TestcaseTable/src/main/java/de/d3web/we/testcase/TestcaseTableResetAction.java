/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.testcase;

import java.io.IOException;
import java.util.Map;

import de.d3web.core.session.Session;
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.basic.WikiEnvironment;
import de.d3web.we.basic.WikiEnvironmentManager;
import de.d3web.we.utils.D3webUtils;

/**
 *
 * @author Florian Ziegler
 * @created 14.02.2011
 */
public class TestcaseTableResetAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		SessionBroker broker = D3webModule.getBroker(context.getParameters());
		broker.clear();

		String user = context.getUserName();
		String topic = context.getTopic();
		String web = context.getWeb();
		Session session = D3webUtils.getSession(topic, user, web);
		WikiEnvironment wiki = WikiEnvironmentManager.getInstance().getEnvironments(web);
		Map<String, Object> sessionInfoStore = wiki.getSessionInfoStore(session);
		sessionInfoStore.clear();
	}

}

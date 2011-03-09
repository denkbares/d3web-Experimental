/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import de.d3web.core.session.Session;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.core.KnowWEAttributes;

public class RequestDialogRenderer extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {

		prepareDialog(context);

		StringBuffer sb = new StringBuffer();
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"de\">");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
		sb.append("<meta http-equiv=\"REFRESH\" content=\"0; url=faces/Controller?knowwe=true&id="
				+ context.getSession().getId() + "\">");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	public void prepareDialog(UserActionContext context) {
		String id = context.getParameter(KnowWEAttributes.SESSION_ID);
		SessionBroker broker = D3webModule.getBroker(context.getParameters());
		Session serviceSession = broker.getServiceSession(id);
		String namespace;
		namespace = context.getParameter(KnowWEAttributes.TARGET);
		if (namespace == null) {
			namespace = context.getParameter(KnowWEAttributes.NAMESPACE);
		}
		// add the case to a map and save it in application scope
		Map<String, Session> sessionToCaseMap = (Map) context.getSession().getServletContext().getAttribute(
					"sessionToCaseMap");
		if (sessionToCaseMap == null) {
			sessionToCaseMap = new HashMap<String, Session>();
		}
		HttpSession s = context.getSession();
		String sID = s.getId();
		sessionToCaseMap.put(sID, serviceSession);
		context.getSession().getServletContext().setAttribute("sessionToCaseMap",
					sessionToCaseMap);
	}

}

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

package de.d3web.we.drools.kdom.rendering;

import de.knowwe.core.report.Message;
import de.knowwe.core.report.MessageRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class DroolsRuleWarningRenderer implements MessageRenderer {

	private static DroolsRuleWarningRenderer instance = null;

	public static DroolsRuleWarningRenderer getInstance() {
		if (instance == null) {
			instance = new DroolsRuleWarningRenderer();
		}

		return instance;
	}

	@Override
	public String postRenderMessage(Message m, UserContext user, String source) {
		StringBuffer buffy = new StringBuffer();

		buffy.append("<span style=\"border-bottom: 1px solid #999; padding: 2px; margin-top: 5px; display: block; color: #a40000;\">");
		buffy.append(m.getVerbalization());
		buffy.append("</span>");

		return KnowWEUtils.maskHTML(buffy.toString());
	}

	@Override
	public String preRenderMessage(Message m, UserContext user, String source) {
		return "";
	}

}

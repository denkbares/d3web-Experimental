/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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

import java.util.Collection;
import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.table.TableCellContentRenderer;

/**
 * 
 * @author Florian Ziegler / Sebastian Furth
 * @created 10.08.2010
 */
public class TestcaseTableCellContentRenderer extends TableCellContentRenderer {

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {

		Map<String, Collection<Message>> errors = Messages.getMessagesFromSubtree(sec,
				Message.Type.ERROR);


		StringBuilder html = new StringBuilder();
		if (!errors.isEmpty()) {
			html.append("<td class='error'>");
		}
		else {
			html.append("<td>");
		}

		html.append(sec.getText());

		html.append("</td>");
		string.append(KnowWEUtils.maskHTML(html.toString()));

	}

}

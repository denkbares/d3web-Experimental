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
package de.knowwe.defi.readbutton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.defi.user.UserUtilities;
import de.knowwe.defi.utils.ReplaceSectionUtils;

/**
 * 
 * @author dupke
 */
public class ReadbuttonSubmitAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		Map<String, String> nodesMap = new HashMap<String, String>();
		Section<?> dataSection = UserUtilities.getDataPage(context.getUserName()).getRootSection();
		nodesMap.put(dataSection.getID(), dataSection.getText() + "\n"
				+ createNewMarkupString(context));

		// submit change
		ReplaceSectionUtils.replaceSections(context, nodesMap);
	}

	private String createNewMarkupString(UserActionContext context) {
		String id = context.getParameter("id");
		String realValue = context.getParameter("realvalue");
		String value = context.getParameter("value");
		String label = context.getParameter("label");
		String discussed = context.getParameter("discussed");
		String closed = context.getParameter("closed");
		String page = context.getTitle();
		String date = (new SimpleDateFormat("dd.MM.yyyy HH:mm")).format((new Date()));

		if (label.equals("")) label = "--";

		StringBuffer newContent = new StringBuffer();
		newContent.append("%%readbutton\n");
		newContent.append("@id:" + id + "\n");
		newContent.append("@realvalue:" + realValue + "\n");
		newContent.append("@value:" + value + "\n");
		newContent.append("@label:" + label + "\n");
		newContent.append("@discussed:" + discussed + "\n");
		newContent.append("@closed:" + closed + "\n");
		newContent.append("@page:" + page + "\n");
		newContent.append("@date:" + date + "\n");
		newContent.append("%\n");

		return newContent.toString();
	}

}

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

import javax.servlet.http.HttpSession;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

/**
 * Stores which TestCase is actually selected in the {@link HttpSession}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.01.2012
 */
public class TestCaseSelectorAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String sectionid = context.getParameter("id");
		context.getSession().setAttribute(TestCasePlayerRenderer.SELECTOR_KEY + "_" + sectionid,
				context.getParameter("value"));
	}

}

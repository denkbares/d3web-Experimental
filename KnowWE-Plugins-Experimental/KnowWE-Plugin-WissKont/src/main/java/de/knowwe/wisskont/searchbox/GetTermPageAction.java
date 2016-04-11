/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.searchbox;

import java.io.IOException;
import java.util.Collection;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 30.09.2013
 */
public class GetTermPageAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String term = context.getParameter("term");
		Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				new Identifier(term));
		String page = null;
		if (termDefinitions.size() > 0) {
			Section<? extends SimpleDefinition> next = termDefinitions.iterator().next();
			page = next.getTitle();
		}
		context.setContentType("text/plain; charset=UTF-8");
		context.getWriter().write(page);
	}

}

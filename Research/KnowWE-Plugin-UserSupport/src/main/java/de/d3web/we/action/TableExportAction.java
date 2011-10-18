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
package de.d3web.we.action;

import java.io.IOException;

import de.d3web.we.tables.ITable;
import de.d3web.we.tables.TableUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;


/**
 * 
 * @author Johannes Dienst
 * @created 18.10.2011
 */
public class TableExportAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String tableId = context.getParameter("tableId");
		String title = context.getTitle();
		String web = context.getWeb();

		// Get the table to be exported
		KnowWEArticle article =
				KnowWEEnvironment.getInstance().getArticle(context.getWeb(), context.getTitle());
		Section<ITable> table = TableUtils.getTableWithId(article, tableId);

		// Export the Table


	}

}

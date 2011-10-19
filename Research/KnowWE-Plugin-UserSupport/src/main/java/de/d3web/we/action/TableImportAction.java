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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import de.d3web.we.poi.PoiUtils;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;


/**
 * TODO Perhaps enable the user to upload a file that is not.
 * TODO Load the file from cookie.
 * 
 * 
 * @author Johannes Dienst
 * @created 18.10.2011
 */
public class TableImportAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) {

		try {
			String tableId = context.getParameter("tableId");
			String title = context.getTitle();
			String web = context.getWeb();
			String user = context.getUserName();

			// TODO load the file from cookie
			FileInputStream in = new FileInputStream("Bla");
			PoiUtils.importTableFromFile(in);

			Writer writer = context.getWriter();
			writer.append("Import successful");
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).warning(
					"Import of xls failed");
		}

	}

}

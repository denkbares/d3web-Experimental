/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.medicine.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.medicine.DbConnection;
import de.knowwe.medicine.Medicine;
import de.knowwe.rdf2go.Rdf2GoCore;

public class TableExistCheckAction extends AbstractAction {

	private Medicine med;
	private Rdf2GoCore core;

	private Connection connect = null;
	private Statement statement = null;

	@Override
	public void execute(UserActionContext context) throws IOException {
		med = new Medicine();
		String result;
		if (med.getExportSetting(5).equals("true")) {
			result = perform(context);
		}
		else {
			WriteDB o = new WriteDB();
			result = o.getStatements();
		}
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {
		DbConnection con = new DbConnection(med.getExportSetting(0), med.getExportSetting(1),
				med.getExportSetting(2), med.getExportSetting(3));
		String name = med.getExportSetting(4);
		try {
			con.readDatabase("select * from `" + name + "`");
		}
		catch (Exception e) {
			WriteDB write = new WriteDB();
			try {
				write.execute(context);
				return "";
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally {
			con.close();
		}

		return "<div class='info'>Table "
				+ name
				+ " already exists. <input type='button' value='Overwrite' title='' "
				+ "onclick='generateSqlFile();'/><input type='button' value='Cancel' title='' onclick='cancelExport();'/>";
	}
}
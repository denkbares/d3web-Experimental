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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.medicine.DbConnection;
import de.knowwe.medicine.Medicine;
import de.knowwe.medicine.MedicineObject;
import de.knowwe.medicine.MedicineParameter;
import de.knowwe.rdf2go.Rdf2GoCore;

public class WriteDB extends AbstractAction {

	private Medicine med;
	private Rdf2GoCore core;

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {
		readFromStore();
		String drop = "DROP TABLE `" + med.getExportSetting(4) + "`;";
		String createTable = createTable();
		String insertStatement = insertStatement();
		DbConnection con = new DbConnection(med.getExportSetting(0), med.getExportSetting(1),
				med.getExportSetting(2), med.getExportSetting(3));
		try {
			con.write(drop);
			con.write(createTable);
			con.write(insertStatement);
		}
		catch (SQLException e) {
			return "<div class='error'>" + e.getMessage() + "</div>";
		}
		finally {
			con.close();
		}

		return "<div class='error' style='background-image:url(KnowWEExtension/images/msg_checkmark.png)'>Successfully exported</div>";
	}

	public String getStatements() {
		readFromStore();
		return createTable() + "\n \n" + insertStatement();
	}

	private String createTable() {
		String s = "CREATE TABLE IF NOT EXISTS `" + med.getExportSetting(4) + "` (\n";
		s += "  `" + med.getExportSetting(6) + "` bigint(20) NOT NULL,\n";
		s += "  `" + med.getExportSetting(9) + "` varchar(500) NOT NULL,\n";
		for (MedicineParameter p : med.getParams()) {
			s += "  `" + p.getName() + "` ";
			if (p.getTyp().equals(MedicineParameter.STRING)) {
				s += "varchar(5000)";
			}
			else if (p.getTyp().equals(MedicineParameter.INTEGER)) {
				s += "int(11)";
			}
			else if (p.getTyp().equals(MedicineParameter.BOOLEAN)) {
				s += "boolean";
			}
			s += " DEFAULT NULL,\n";

		}
		s += "  `" + med.getExportSetting(8) + "` bigint(20) NOT NULL,\n";

		s += "  PRIMARY KEY (`" + med.getExportSetting(6) + "`)\n);";
		return s;
	}

	private String insertStatement() {
		String s = "INSERT INTO `" + med.getExportSetting(4) + "` (`" + med.getExportSetting(6)
				+ "`, `" + med.getExportSetting(9) + "`, ";
		for (MedicineParameter p : med.getParams()) {
			s += "`" + p.getName() + "`, ";
		}
		s += "`" + med.getExportSetting(8) + "`) VALUES\n";

		for (MedicineObject e : med.getObjects().values()) {
			s += "(" + e.getId() + ", '" + decode(e.getSubject()) + "'";
			for (MedicineParameter p : med.getParams()) {
				Object o = e.get(p);
				if (p.getTyp().equals(MedicineParameter.STRING)) {
					s += ", '";
					if (o != null) {
						o = decode((String) o);
					}
				}
				else {
					s += ", ";
				}
				s += o;
				if (p.getTyp().equals(MedicineParameter.STRING)) {
					s += "'";
				}
			}
			s += ", " + e.getParent() + "),\n";

		}
		s = s.substring(0, s.length() - 2) + ";\n";

		return s;
	}

	private void readFromStore() {
		med = new Medicine();
		core = Rdf2GoCore.getInstance();

		HashMap<Integer, MedicineObject> objects = new HashMap<Integer, MedicineObject>();

		String select = "select ?subject ?id ";
		String where = "where { ?subject lns:" + med.getExportSetting(7) + " ?id . ";
		ArrayList<MedicineParameter> splitted = new ArrayList<MedicineParameter>();
		for (MedicineParameter p : med.getParams()) {
			if (!p.isSplitted()) {
				select += "?" + p.getName() + " ";
				where += "OPTIONAL { ?subject lns:" + p.getTag() + " ?" + p.getName() + " . } ";
			}
			else {
				splitted.add(p);
			}
		}

		where += "}";
		select += where;

		ClosableIterator<QueryRow> it = core.sparqlSelectIt(select);
		while (it.hasNext()) {
			QueryRow row = it.next();
			int id = Integer.parseInt(row.getLiteralValue("id"));
			String subject = Rdf2GoCore.getLocalName(row.getValue("subject"));

			MedicineObject o = new MedicineObject();
			o.setId(id);
			o.setSubject(subject);

			for (MedicineParameter p : med.getParams()) {
				String s = Rdf2GoCore.getLocalName(row.getValue(p.getName()));
				if (s == null) {
					s = "";
				}
				if (p.getTyp().equals(MedicineParameter.STRING)) {
					o.addValue(p, s);
				}
				else if (p.getTyp().equals(MedicineParameter.INTEGER)) {
					o.addValue(p, Integer.parseInt(s));
				}
				else if (p.getTyp().equals(MedicineParameter.BOOLEAN)) {
					o.addValue(p, Boolean.parseBoolean(s));
				}
			}

			// Read splitted properties
			for (MedicineParameter p : splitted) {
				String union = "";
				String query = "select ?item {?x lns:" + med.getExportSetting(7) + " '" + id
						+ "' . ?x lns:" + p.getTag() + " ?item . }";
				ClosableIterator<QueryRow> cit = core.sparqlSelectIt(query);
				while (cit.hasNext()) {
					if (!union.equals("")) {
						union += p.getSplitStr();
					}
					String item = cit.next().getLiteralValue("item");
					union += item;
				}
				o.addValue(p, union);
			}

			// Read parent id
			String query = "select ?parentid { ?x lns:directSubclassOf ?y . " +
					"?y lns:" + med.getExportSetting(7) + " ?parentid . " +
					"?x lns:" + med.getExportSetting(7) + " '" + id + "' . }";
			ClosableIterator<QueryRow> itp = core.sparqlSelectIt(query);
			while (itp.hasNext()) {
				o.setParent(Integer.parseInt(itp.next().getLiteralValue("parentid")));
			}

			objects.put(id, o);
		}
		med.setObjects(objects);
	}

	private String decode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
		}
		catch (IllegalArgumentException e) {
		}

		return "value";

	}
}
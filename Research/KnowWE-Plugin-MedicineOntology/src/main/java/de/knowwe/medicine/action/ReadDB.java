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
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.medicine.DbConnection;
import de.knowwe.medicine.Medicine;
import de.knowwe.medicine.MedicineObject;
import de.knowwe.medicine.MedicineParameter;

public class ReadDB extends AbstractAction {

	private static final String PRE = "ttl: ";
	private static final String MID = ":: ";
	private static final String N3BLOCK = "n3block";
	private static final String N3SINGLE = "n3single";

	private Medicine med;

	private String perform(UserActionContext context) {
		med = new Medicine();
		DbConnection con = new DbConnection(med.getImportSetting(0), med.getImportSetting(1),
				med.getImportSetting(2), med.getImportSetting(3));
		// read from database and create objects with params and values
		try {
			String table = med.getImportSetting(4);
			ResultSet resultSet = con.readDatabase("select * from " + table);
			createEKGObjects(resultSet);

		}
		catch (Exception e) {
			return "<div class='error'>" + e.getMessage() + "</div>";
		}
		finally {
			con.close();
		}

		generateParents();
		generateFiles(context);

		return "<div class='error' style='background-image:url(KnowWEExtension/images/msg_checkmark.png)'>Sucessfully imported</div>";
	}

	private void createEKGObjects(ResultSet resultSet)
			throws SQLException {
		HashMap<Integer, MedicineObject> objects = med.getObjects();
		while (resultSet.next()) {
			MedicineObject o = new MedicineObject();

			for (MedicineParameter p : med.getParams()) {
				if (p.getTyp().equals(MedicineParameter.STRING)) {
					o.addValue(p, resultSet.getString(p.getName()).replaceAll("%", "Prozent"));
				}
				else if (p.getTyp().equals(MedicineParameter.INTEGER)) {
					o.addValue(p, resultSet.getInt(p.getName()));
				}
				else if (p.getTyp().equals(MedicineParameter.BOOLEAN)) {
					o.addValue(p, resultSet.getBoolean(p.getName()));
				}
			}

			int id = resultSet.getInt(med.getImportSetting(6));
			o.setId(id);
			o.setParent(resultSet.getInt(med.getImportSetting(8)));
			o.setSubject(resultSet.getString(med.getImportSetting(9)).replaceAll("%", "Prozent").replaceAll(
					":", " "));

			objects.put(id, o);
		}
		med.setObjects(objects);
	}

	private void generateParents() {
		HashMap<Integer, MedicineObject> objects = med.getObjects();
		HashMap<Integer, List<Integer>> parents = med.getParents();

		HashMap<Integer, List<Integer>> temp = new HashMap<Integer, List<Integer>>();
		for (MedicineObject x : objects.values()) {
			if (!temp.containsKey(x.getId())) {
				temp.put((Integer) x.getId(), new ArrayList<Integer>());
			}
			List<Integer> list = new ArrayList<Integer>();
			if (temp.containsKey(x.getParent())) {
				list = temp.get(x.getParent());
			}
			list.add((Integer) x.getId());
			temp.put((Integer) x.getParent(), list);
		}

		// clear parents without children
		for (Entry<Integer, List<Integer>> e : temp.entrySet()) {
			if (!e.getValue().isEmpty()) {
				parents.put(e.getKey(), e.getValue());
			}
		}

		List<Integer> tempList = parents.get(0);
		tempList.remove(0);
		parents.put(0, tempList);
		med.setParents(parents);
	}

	private void generateFiles(UserActionContext context) {
		// Generate and write other pages
		for (MedicineObject x : med.getObjects().values()) {
			String title = x.getSubject();

			String page = createPageString(x);

			Environment.getInstance().getWikiConnector().createWikiPage(title, page,
					context.getUserName());
			Environment.getInstance().buildAndRegisterArticle(page, title, context.getWeb());
		}

		// Generate and write Main page
		Environment.getInstance().getWikiConnector().createWikiPage("Main", generateMain(),
				context.getUserName());

	}

	private String createPageString(MedicineObject x) {
		if (med.getImportSetting(5).toLowerCase().equals(N3BLOCK)) {
			return getN3BlockMarkup(x);
		}
		else if (med.getImportSetting(5).toLowerCase().equals(N3SINGLE)) {
			return getN3SingleMarkup(x);
		}
		else {
			return "no markup specified";
		}
	}

	private String getN3BlockMarkup(MedicineObject x) {
		String result = "def " + x.getSubject() + "\n";
		result += PRE + x.getSubject() + " " + med.getImportSetting(7) + MID + "'" + x.getId()
				+ "'" + ".\n";
		result += PRE + x.getSubject() + "\n";
		for (MedicineParameter p : x.getMap().keySet()) {
			Object value = x.get(p);
			if (p.isSplitted()) {
				String[] items = ((String) value).split(p.getSplitPattern());
				String firstLine = "    " + p.getTag() + MID;
				result += firstLine + "\n";
				String einzug = "";
				for (int i = 0; i < firstLine.length(); i++) {
					einzug += " ";
				}
				for (String item : items) {
					result += einzug + "'" + item + "' ,\n";
				}
				result = result.substring(0, result.length() - 2) + ";\n";
			}
			else {
				result += "    " + p.getTag() + MID + "'"
						+ value + "' ;\n";
			}

		}
		result = result.substring(0, result.length() - 2) + ".\n";
		return result;
	}

	private String getN3SingleMarkup(MedicineObject x) {
		String result = "def " + x.getSubject() + "\n";
		result += PRE + x.getSubject() + " " + med.getImportSetting(7) + MID + "'" + x.getId()
				+ "'" + ".\n";
		for (MedicineParameter p : x.getMap().keySet()) {
			Object value = x.get(p);
			if (p.isSplitted()) {
				String[] items = ((String) value).split(p.getSplitPattern());
				String firstLine = PRE + x.getSubject() + " " + p.getTag() + MID;
				result += firstLine + "\n";
				String einzug = "";
				for (int i = 0; i < firstLine.length(); i++) {
					einzug += " ";
				}
				for (String item : items) {
					result += einzug + "'" + item + "' ,\n";
				}
				result = result.substring(0, result.length() - 2) + ".\n";
			}
			else {
				result += PRE + x.getSubject() + " " + p.getTag() + MID + "'" + value
						+ "' .\n";
			}

		}
		return result;
	}

	private String generateMain() {
		return "%%subclass\n" + "EKG\n"
				+ generateTree("-", false, 0, med.getParents().get(0), false)
				+ "%\n\n";
	}

	private String generateTree(String bullet, boolean links, int ebene,
			List<Integer> list, boolean beautify) {
		HashMap<Integer, MedicineObject> objects = med.getObjects();

		String pre = "";
		for (int i = 0; i <= ebene; i++) {
			pre += bullet;
		}
		pre += " ";

		String result = "";
		for (Integer i : list) {
			String pre2 = pre;
			if (links) {
				pre2 = pre + "[" + (String) objects.get(i).getSubject() + "|";
			}

			result += pre2;
			if (beautify) {
				result += beautify((String) objects.get(i).getSubject());
			}
			else {
				result += (String) objects.get(i).getSubject();
			}
			if (links) {
				result += "]";
			}
			result += "\n";
			if (med.getParents().containsKey(i)) {
				result += generateTree(bullet, links, ebene + 1, med.getParents().get(i), beautify);
			}
		}

		return result;
	}

	private String beautify(String value) {
		String temp = value;
		try {
			temp = URLDecoder.decode(value, "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
		}
		catch (IllegalArgumentException e) {
		}

		try {
			return URLEncoder.encode(temp, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "value";
	}

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}
}

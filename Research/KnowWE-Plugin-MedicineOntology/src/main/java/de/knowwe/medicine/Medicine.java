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

package de.knowwe.medicine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.rdf2go.Rdf2GoCore;

public class Medicine {

	private List<MedicineParameter> params;
	private HashMap<Integer, MedicineObject> objects;
	private HashMap<Integer, List<Integer>> parents;

	// 0 = SqlIp
	// 1 = SqlDb
	// 2 = SqlUser
	// 3 = SqlPw
	// 4 = SqlTable
	// 5 = Import: useMarkup / Export: exportToDB
	// 6 = id
	// 7 = idTag
	// 8 = parent
	// 9 = subject
	private String[] importSettings;
	private String[] exportSettings;

	private static final String queryParams = "select ?title ?tag ?typ ?split ?sp"
			+ "{ ?title rdf:type lns:MedicineParameter. ?title lns:tag ?tag. "
			+ "?title lns:dataType ?typ. OPTIONAL {?title lns:split ?split . } OPTIONAL {?title lns:splitPattern ?sp. } }";
	private static final String queryImportSettings = "select ?SqlIp ?SqlDb ?SqlUser ?SqlPw ?SqlTable ?useMarkup ?id ?idTag ?parent ?subject { lns:ImportSettings lns:SqlId ?id. lns:ImportSettings lns:idTag ?idTag. lns:ImportSettings lns:subject ?subject. lns:ImportSettings lns:SqlIp ?SqlIp. lns:ImportSettings lns:SqlDb ?SqlDb. lns:ImportSettings lns:SqlUser ?SqlUser.lns:ImportSettings lns:SqlTable ?SqlTable. lns:ImportSettings lns:useMarkup ?useMarkup. lns:ImportSettings lns:parent ?parent. OPTIONAL { lns:ImportSettings lns:SqlPw ?SqlPw. } }";
	private static final String queryExportSettings = "select ?exportToDB ?SqlIp ?SqlDb ?SqlUser ?SqlPw ?SqlTable ?id ?idTag ?parent ?subject { lns:ExportSettings lns:SqlId ?id. lns:ExportSettings lns:idTag ?idTag. lns:ExportSettings lns:subject ?subject. lns:ExportSettings lns:SqlIp ?SqlIp. lns:ExportSettings lns:SqlDb ?SqlDb. lns:ExportSettings lns:SqlUser ?SqlUser. lns:ExportSettings lns:SqlTable ?SqlTable. lns:ExportSettings lns:exportToDB ?exportToDB. lns:ExportSettings lns:parent ?parent. OPTIONAL { lns:ExportSettings lns:SqlPw ?SqlPw. } }";

	public Medicine() {
		params = new ArrayList<MedicineParameter>();
		objects = new HashMap<Integer, MedicineObject>();
		parents = new HashMap<Integer, List<Integer>>();
		readParamsAndSettings();
	}

	public HashMap<Integer, List<Integer>> getParents() {
		return parents;
	}

	public void setParents(HashMap<Integer, List<Integer>> parents) {
		this.parents = parents;
	}

	private void readParamsAndSettings() {
		ClosableIterator<QueryRow> it = Rdf2GoCore.getInstance().sparqlSelectIt(
				queryParams);
		while (it.hasNext()) {
			QueryRow row = it.next();
			String title = Rdf2GoCore.getLocalName(row.getValue("title"));
			String tag = Rdf2GoCore.getLocalName(row.getValue("tag"));
			String typ = Rdf2GoCore.getLocalName(row.getValue("typ"));
			Node n = row.getValue("split");
			if (n == null) {
				params.add(new MedicineParameter(title, tag, typ));
			}
			else {
				String split = Rdf2GoCore.getLocalName(n);
				String sp = Rdf2GoCore.getLocalName(row.getValue("sp"));
				params.add(new MedicineParameter(title, tag, typ, split, sp));
			}
		}

		// ImportSettings
		QueryRow row = Rdf2GoCore.getInstance().sparqlSelectIt(
				queryImportSettings).next();
		importSettings = new String[10];
		importSettings[0] = row.getLiteralValue("SqlIp");
		importSettings[1] = row.getLiteralValue("SqlDb");
		importSettings[2] = row.getLiteralValue("SqlUser");

		if (row.getValue("SqlPw") != null) {
			importSettings[3] = row.getLiteralValue("SqlPw");
		}
		else {
			importSettings[3] = "";
		}

		importSettings[4] = row.getLiteralValue("SqlTable");
		importSettings[5] = row.getLiteralValue("useMarkup");
		importSettings[6] = row.getLiteralValue("id");
		importSettings[7] = Rdf2GoCore.getLocalName(row.getValue("idTag"));
		importSettings[8] = Rdf2GoCore.getLocalName(row.getValue("parent"));
		importSettings[9] = row.getLiteralValue("subject");

		// ExportSettings
		row = Rdf2GoCore.getInstance().sparqlSelectIt(
				queryExportSettings).next();
		exportSettings = new String[10];
		exportSettings[0] = row.getLiteralValue("SqlIp");
		exportSettings[1] = row.getLiteralValue("SqlDb");
		exportSettings[2] = row.getLiteralValue("SqlUser");

		if (row.getValue("SqlPw") != null) {
			exportSettings[3] = row.getLiteralValue("SqlPw");
		}
		else {
			exportSettings[3] = "";
		}

		exportSettings[4] = row.getLiteralValue("SqlTable");
		exportSettings[5] = row.getLiteralValue("exportToDB");
		exportSettings[6] = row.getLiteralValue("id");
		exportSettings[7] = Rdf2GoCore.getLocalName(row.getValue("idTag"));
		exportSettings[8] = Rdf2GoCore.getLocalName(row.getValue("parent"));
		exportSettings[9] = row.getLiteralValue("subject");

	}

	public List<MedicineParameter> getParams() {
		return params;
	}

	public void setParams(List<MedicineParameter> params) {
		this.params = params;
	}

	public HashMap<Integer, MedicineObject> getObjects() {
		return objects;
	}

	public void setObjects(HashMap<Integer, MedicineObject> objects) {
		this.objects = objects;
	}

	public String getImportSetting(int i) {
		return importSettings[i];
	}

	public String getExportSetting(int i) {
		return exportSettings[i];
	}

	public String[] getImportSettings() {
		return importSettings;
	}

	public String[] getExportSettings() {
		return exportSettings;
	}
}

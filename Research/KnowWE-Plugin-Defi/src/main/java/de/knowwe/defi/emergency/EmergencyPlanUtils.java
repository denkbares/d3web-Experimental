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
package de.knowwe.defi.emergency;

import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.defi.table.TableEntryType;


/**
 * 
 * @author smark
 * @created 11.10.2011
 */
public class EmergencyPlanUtils {

	private static final String NAME = "name";
	private static final String EMERGENCY_NUMBER = "emtel";
	private static final String GENERAL = "general";
	private static final String MEDIC = "med";
	private static final int NUMBER_OF_MEDICS = 5;

	/**
	 * tableid: name INPUT0: name of patient
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getPatientName(String user) {
		String entry = getTableEntry(NAME, user);
		String name = getEntryLine(entry, "INPUT0");

		return name;
	}

	/**
	 * tableid: emtel INPUT1: Cardiologist INPUT2: Tele
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getCardiologist(String user) {
		String entry = getTableEntry(EMERGENCY_NUMBER, user);
		String doctor = getEntryLine(entry, "INPUT1");
		String phonenumber = getEntryLine(entry, "INPUT2");
		

		return doctorTemplate(doctor, phonenumber);
	}

	/**
	 * tableid: emtel INPUT3: Physician INPUT4: Tele
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getPhysician(String user) {
		String entry = getTableEntry(EMERGENCY_NUMBER, user);
		String doctor = getEntryLine(entry, "INPUT3");
		String phonenumber = getEntryLine(entry, "INPUT4");

		return doctorTemplate(doctor, phonenumber);
	}

	/**
	 * tableid: emtel INPUT5: EmergencyPerson INPUT6: Tele
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getEmergencyPerson(String user) {
		String entry = getTableEntry(EMERGENCY_NUMBER, user);
		String doctor = getEntryLine(entry, "INPUT5");
		String phonenumber = getEntryLine(entry, "INPUT6");

		return doctorTemplate(doctor, phonenumber);
	}

	/**
	 * tableid: emtel INPUT0: EmergencyNumber
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getEmergencyNumber(String user) {
		String entry = getTableEntry(EMERGENCY_NUMBER, user);

		return getEntryLine(entry, "INPUT0");
	}

	/**
	 * tableid: med INPUT0: drug INPUT1: dose
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getMedics(String user) {
		String entry, drug, dose;
		StringBuilder html = new StringBuilder();

		for (int i = 0; i < NUMBER_OF_MEDICS; i++) {
			entry = getTableEntry(MEDIC, user);
			drug = getEntryLine(entry, "INPUT" + (i * 2));
			dose = getEntryLine(entry, "INPUT" + (i * 2 + 1));

			html.append(medTemplate(drug, dose));

		}

		return html.toString();
	}

	/**
	 * tableid: general INPUT3: blood group
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getBloodType(String user) {
		String entry = getTableEntry(GENERAL, user);

		return getEntryLine(entry, "INPUT3");
	}

	/**
	 * tableid: general INPUT2: diagnosis
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getDiagnosis(String user) {
		String entry = getTableEntry(GENERAL, user);

		return getEntryLine(entry, "INPUT2");
	}

	/**
	 * tableid: general INPUT0: title
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getICDModelTitle(String user) {
		String entry = getTableEntry(GENERAL, user);

		return getEntryLine(entry, "INPUT0");
	}

	/**
	 * tableid: general INPUT1: type
	 * 
	 * @created 25.10.2011
	 * @param user
	 * @return
	 */
	public static String getICDModelID(String user) {
		String entry = getTableEntry(GENERAL, user);

		return getEntryLine(entry, "INPUT1");
	}

	private static String medTemplate(String med, String dosis) {
		return "<tr>"
				+ "<th width=\"200px\" height=\"20px\"><span class=\"inhalt\">"
				+ med + "</span></th>"
				+ "<th class=\"inner2\" width=\"20px\"></th>"
				+ "<th width=\"120px\" height=\"20px\"><span class=\"inhalt\"> " + dosis
				+ " </span></th>"
				+ "</tr>";
	}

	private static String doctorTemplate(String doctor, String phone) {
		return "<table class=\"other\" border=\"0\">"
				+ "<tr>"
				+ "	<th width=\"160px\" height=\"20px\">"
				+ "<span class=\"inhalt\"> " + doctor + " </span>"
				+ "</th>"
				+ "<th class=\"inner2\" width=\"20px\"></th>"
				+ "<th width=\"160px\" height=\"20px\">"
				+ "<span class=\"inhalt\"> " + phone + " </span>"
				+ "</th>"
				+ "</tr>"
				+ "</table>";
	}

	private static String getTableEntry(String id, String user) {
		List<Section<TableEntryType>> tableEntries;

		try {
			Article article = Environment.getInstance().getArticle(
					Environment.DEFAULT_WEB, user + "_data");
			tableEntries = Sections.findSuccessorsOfType(article.getRootSection(), TableEntryType.class);
			for (Section<TableEntryType> sec : tableEntries) {
				if (TableEntryType.getAnnotation(sec, "tableid").equals(id)) return sec.getText();
			}
		}
		catch (NullPointerException e) {
			// e.printStackTrace();
		}

		return "";
	}

	private static String getEntryLine(String entry, String identifier) {
		String[] lines = entry.split("\n");
		identifier += ":";

		for (String line : lines) {
			if (line.startsWith(identifier)) return line.split(identifier)[1];
		}

		return "";
	}
}

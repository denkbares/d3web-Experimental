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


/**
 * 
 * @author smark
 * @created 11.10.2011
 */
public class EmergencyPlanUtils {

	public static String getCardiologist(String user) {
		String doctor = "Dr. Oetker ";
		String phonenumber = "0190/1234567";
		return doctorTemplate(doctor, phonenumber);
	}

	public static String getPhysician(String user) {
		String doctor = "Dr. Best";
		String phonenumber = "0666/111111";
		return doctorTemplate(doctor, phonenumber);
	}

	public static String getEmergencyPerson(String user) {
		String doctor = "Daisy Duck";
		String phonenumber = "0999/9876543";
		return doctorTemplate(doctor, phonenumber);
	}

	public static String getMedics(String user) {

		StringBuilder html = new StringBuilder();

		html.append(medTemplate("Benazepril", "XX mg"));
		html.append(medTemplate("Adenosin", "XX mg"));
		html.append(medTemplate("Retard", "XX mg"));

		return html.toString();
	}

	public static String getBloodType(String user) {
		return "A+";
	}

	public static String getDiagnosis(String user) {
		return "Linksherzinsuffizienz";
	}

	public static String getICDModelTitle(String user) {
		return "Biotronik";
	}

	public static String getICDModelID(String user) {
		return "XYZ 1234";
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
}

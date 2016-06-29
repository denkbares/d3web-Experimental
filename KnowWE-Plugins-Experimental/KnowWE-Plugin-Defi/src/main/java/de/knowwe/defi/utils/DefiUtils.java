/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.defi.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.defi.aboutMe.AboutMe;

/**
 *
 *
 * @author smark
 * @created 17.02.2011
 */
public class DefiUtils {

	public static String lastLogin() {
		String logout_file = System.getProperty("java.io.tmpdir") + File.separatorChar
				+ AboutMe.LOGOUT_FILENAME;

		File file = new File(logout_file);
		try {
			// name - name - date
			String file_content = DefiUtils.readFile(file);
			String[] token = file_content.split(" - ");

			DateFormat dateFormat = new SimpleDateFormat(AboutMe.DATE_FORMAT);
			Date date = dateFormat.parse(token[1].trim());

			DateFormat d = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat t = new SimpleDateFormat("HH.mm");

			StringBuilder html = new StringBuilder();
			html.append("Zuletzt online <br /> am ");
			html.append(d.format(date));
			html.append("<br /> um ");
			html.append(t.format(date));
			html.append(" Uhr");

			return html.toString();
		}
		catch (Exception e) {
			return "Zuletzt online: <br /> nicht bekannt";
		}
	}

	public static String readFile(File file) throws IOException {
		StringBuilder buffy = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),
				"UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			buffy.append(str).append("\n");
		}
		in.close();
		return buffy.toString();
	}

	public static List<String> getAllDefiArticleNames() {
		List<String> articles = new LinkedList<>();

		articles.addAll(Environment.getInstance().getWikiConnector().getAllArticles(
				Environment.DEFAULT_WEB).keySet());
		articles.add("EmergencyPlan.jsp");
		articles.add("Zusatzmodul");
		return articles;
	}
}

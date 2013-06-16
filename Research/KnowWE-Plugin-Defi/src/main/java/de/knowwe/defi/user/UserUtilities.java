/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.user;

import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.auth.user.UserProfile;

import de.knowwe.core.Environment;
import de.knowwe.jspwiki.JSPWikiConnector;

/**
 * 
 * @author dupke
 * @created 12.02.2013
 */
public class UserUtilities {

	public static Date getCreated(UserProfile user) {
		Date created = user.getCreated();
		if (created != null) return created;

		JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
				Environment.getInstance().getContext(), null));
		String file = wc.getWikiProperty("jspwiki.xmlUserDatabaseFile");
		String createdAttr = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new FileInputStream(file));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("user");

			Element knoten;
			for (int i = 0; i < nodes.getLength(); i++) {
				knoten = (Element) nodes.item(i);
				if (knoten.getAttribute("fullName").equals(user.getFullname()))
					createdAttr = knoten.getAttribute("created");
			}
		}
		catch (Exception e) {
		}

		if (createdAttr != null) {
			Locale defaultLocale = Locale.getDefault();
			try {
				if (createdAttr.endsWith("CET") || createdAttr.endsWith("CEST")) Locale.setDefault(Locale.ENGLISH);
				if (createdAttr.endsWith("MEZ") || createdAttr.endsWith("MESZ")) Locale.setDefault(Locale.GERMAN);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' hh:mm:ss:SSS z");
				created = sdf.parse(createdAttr);
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
			finally {
				Locale.setDefault(defaultLocale);
			}
		}

		return created;
	}

}

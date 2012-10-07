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
package de.knowwe.defi.links;

import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;

/**
 * Renders a html-link with "target='_blank'"
 * 
 * 
 * @author Jochen
 * @created 28.03.2011
 */
public class ExternalLinkTaghandler extends AbstractTagHandler {

	public ExternalLinkTaghandler() {
		super("link");
	}

	@Override
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {
		if (parameters.containsKey("url")) {
			String url = parameters.get("url");
			if (url == "") {
				String[] params = section.getText().split(" , ");
				for (String param : params) {
					if (param.startsWith("url=")) url = param.substring(4);
				}
				if (url.endsWith("}]")) url = url.substring(0, url.length() - 2);
			}
			String text = url;
			if (parameters.containsKey("title")) {
				text = parameters.get("title");
			}
			
			String externalLinkHintText = "Sie verlassen nun die Webseite von ICD-Forum und folgen einem externen Link. Wir haben keinen Einfluss auf den Inhalt der folgenden Webseite und sind nicht für den Inhalt verantwortlich.";

			StringBuffer buffy = new StringBuffer();
			buffy.append(Strings.maskHTML("<a href='#' onclick='linkAlertCalled(\""
					+ externalLinkHintText + "\", \"" + url + "\")'>"));
			buffy.append(text);
			buffy.append(Strings.maskHTML("</a>"));
			return buffy.toString();
		}
		return "no url defined";
	}

}

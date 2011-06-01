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
package de.knowwe.casetrain.taghandler;

import java.util.Map;
import java.util.ResourceBundle;

import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.user.UserContext;


/**
 * 
 * @author Johannes Dienst
 * @created 30.05.2011
 */
public class XMLButtonTagHandler extends AbstractHTMLTagHandler {

	private final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	public XMLButtonTagHandler() {
		super("xmlbuttonhandler");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String,
			String> parameters, String web) {
		StringBuilder buildi = new StringBuilder();
		buildi.append("<div id=\"xmlcasetraindiv\">" +
				"<input type=\"button\" name=\"xmlctbutton\"" +
				" id=\"xmlctbutton\" value=\"" +bundle.getString("PARSE_BUTTON")+"\">" +
		"</div>");
		buildi.append("<div id=\"casetrainparseresult\"></div>");
		return buildi.toString();
	}

}

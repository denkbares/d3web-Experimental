/**
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

package de.d3web.proket.output.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webXMLParser.LoginMode;

/**
 * Container for JavaScript code.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class JSCodeContainer implements ICodeContainer {

	private boolean d3web = false;
	private final Map<Integer, StringBuilder> data = new HashMap<Integer, StringBuilder>();
	private boolean dateAnswer = false;
	private final boolean debug = false;
	private boolean imageQuestions = false;
	private boolean usrdatLogin = false;
	private boolean dbLogin = false;
	private boolean logging = false;

	@Override
	public void add(String data) {
		add(data, 0);
	}

	public void add(String newData, int order) {

		// get what is already stored at that order level
		StringBuilder sb = data.get(order);
		if (sb == null) {
			sb = new StringBuilder(); // new sb
			data.put(order, sb); // add at that order level
		}
		sb.append(newData).append("\n"); // add to existing sb
	}

	/**
	 * Some setters for enabling specific settings: d3web, dateanswers,
	 * imagequestions --- for eased usage in the Renderers
	 */
	public void enableD3Web() {
		d3web = true;
	}

	public void enableDateAnswer() {
		dateAnswer = true;
	}

	public void enableImagequestions() {
		imageQuestions = true;
	}

	public void setLoginMode(LoginMode mode) {
		if (mode == LoginMode.usrdat) usrdatLogin = true;
		else if (mode == LoginMode.db) dbLogin = true;
	}

	public void enableClickLogging() {
		logging = true;
	}

	/**
	 * Putting together the complete JS-defining and -linking String
	 */
	@Override
	public String generateOutput() {
		StringBuffer output = new StringBuffer();
		Vector<String> linkedBibs = new Vector<String>();
		Vector<String> ownBibs = new Vector<String>();

		// FIRST define all necessary bibs
		linkedBibs.add("jquery/jquery-1.6.1.min.js");
		// linkedBibs.add("jquery/jquery.min.js");
		// linkedBibs.add("jquery/jquery-ui-position.min.js");
		linkedBibs.add("jquery/jquery.cookie.js");
		linkedBibs.add("jquery/jquery.jstree.js");
		linkedBibs.add("jquery/jquery.typing.min.js");
		linkedBibs.add("jquery/jquery.numeric.js");
		linkedBibs.add("jquery/jquery.customFileInput.js");
		linkedBibs.add("jquery/jquery.print.js");
		linkedBibs.add("jqueryUI/jqueryUi-1.8.10.all.min.js");

		// manage language mechanism
		String lang = D3webConnector.getInstance().getLanguage();
		String langString = "var language = \"" + lang + "\";";
		add(langString, 0);

		if (dateAnswer) {
			linkedBibs.add("jquery/jquery.datepick.pack.js");
			linkedBibs.add("jquery/jquery.datepick-de.js");
			add("var dateanswer = true;", 0);
		}
		else {
			add("var dateanswer = false;", 0);
		}
		if (imageQuestions) {
			linkedBibs.add("jquery/jquery.maphilight.min.js");
		}

		if (usrdatLogin) {
			ownBibs.add("login.js");
			ownBibs.add("encrypt.js");
			// flag that enables login mechanism in d3web.js
			add("var usrdatLogin = true;", 0);
		}
		else {
			// flag that disables login mechanism in d3web.js
			add("var usrdatLogin = false;", 0);
		}

		if (dbLogin) {
			// flag that enables login mechanism in d3web.js
			add("var dbLogin = true;", 0);
		}
		else {
			// flag that disables login mechanism in d3web.js
			add("var dbLogin = false;", 0);
		}

		if (d3web) {

			ownBibs.add("d3webBasic.js");
			ownBibs.add("d3web.js");

			linkedBibs.add("jquery/jquery.object.js");
			add("var d3web = true;", 0);
		}
		else {
			ownBibs.add("code.js");
			add("var d3web = false;", 0);
		}

		if (logging) {
			add("var logging = true;", 0);

		}
		else {
			add("var logging = false;", 0);
		}
		// SECOND assemble bibs and singular js data to a string
		// assemble all defined bibs to one String
		for (String filename : linkedBibs) {
			output.append(
					"<script language=\"javascript\" type=\"text/javascript\" src=\"libsExternal/")
					.append(filename).append("\"></script>\n");
		}

		for (String filename : ownBibs) {
			output.append(
					"<script language=\"javascript\" type=\"text/javascript\" src=\"js/")
					.append(filename).append("\"></script>\n");
		}

		// append the JS code that is written directly into the output HTML file
		output.append("<script language=\"javascript\" type=\"text/javascript\">\n");
		// add data ordered, supporting up to 32 order levels
		for (int i = 0; i < 32; i++) {
			if (data.get(i) != null) {
				output.append(data.get(i));
			}
		}
		output.append("\n</script>\n");

		// THIRD remove debug lines for Firebug
		if (!debug) {
			String x = output.toString().replaceAll("console\\.", "//console.");
			return x;
		}

		return output.toString();
	}
}

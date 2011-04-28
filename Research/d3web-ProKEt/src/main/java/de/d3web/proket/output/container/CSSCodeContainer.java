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

import java.util.Vector;

/**
 * Container for css code.
 *
 * @author Martina Freiberg, Johannes Mitlmeier
 *
 */
public class CSSCodeContainer implements ICodeContainer {

	// stores the css string
	StringBuilder data = new StringBuilder();
	private boolean dateAnswer = false;


	@Override
	public void add(String data) {
		this.data.append(data);
	}

	/**
	 * Adds a new style to a defined css element
	 *
	 * @created 09.10.2010
	 * @param string the style to be added
	 * @param cssElement the css element that is extended
	 */
	public void addStyle(String string, String cssElement) {
		if (string == null || string.length() == 0) {
			return;
		}

		StringBuilder stringBuilder = new StringBuilder("\n" + cssElement
				+ " {\n");

		stringBuilder.append("\t" + string + "\n}");
		add(stringBuilder.toString());
	}

	/**
	 * Enable date answers with the color picker
	 *
	 * @created 09.10.2010
	 */
	public void enableDateAnswer() {
		dateAnswer = true;
	}


	@Override
	public String generateOutput() {
		StringBuffer output = new StringBuffer();
		Vector<String> linkedStyles = new Vector<String>();

		// in case of dateAnswers link jquery datepicker style
		// if (dateAnswer) {
		// linkedStyles.add("jquery.datepick.css");
		// }
		linkedStyles.add("jqueryUI/jquery-ui-1.8.12.customMediastinitis.css");

		// link invariant js files to use browser caching
		for (String filename : linkedStyles) {
			output.append(
					"<link rel=\"stylesheet\" type=\"text/css\" href=\"libsExternal/")
					.append(filename).append("\"/>\n");
		}

		// add "normal" css styles to output css string
		output.append("<style type=\"text/css\">").append(data)
				.append("</style>");
		return output.toString();
	}
}

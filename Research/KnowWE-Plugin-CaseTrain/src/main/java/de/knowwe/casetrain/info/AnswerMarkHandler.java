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
package de.knowwe.casetrain.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.casetrain.info.AnswerLine.AnswerMark;
import de.knowwe.casetrain.message.InvalidArgumentError;


/**
 * Simply checks, if the string t inside {t} has the allowed
 * symbols. +/-/number
 * 
 * @author Johannes Dienst
 * @created 08.05.2011
 */
public class AnswerMarkHandler extends GeneralSubtreeHandler<AnswerMark> {

	ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	String[] symbols = {"+", "-"};

	private static AnswerMarkHandler uniqueInstance;

	private AnswerMarkHandler(){
		// Nothing
	}

	public static AnswerMarkHandler getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new AnswerMarkHandler();
		return uniqueInstance;
	}

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<AnswerMark> s) {

		List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>();
		String content = s.getOriginalText().substring(1, s.getOriginalText().length()-1).trim();

		for (String string : symbols) {
			if (content.equals(string)) {
				return messages;
			}
		}

		double d1 = 0.0;
		double d2 = -1.0;
		try {
			String[] doubles = content.split("[ ]+");
			if (doubles.length > 2)
				throw new IllegalArgumentException();
			d1 = Double.parseDouble(doubles[0]);
			if (doubles.length == 2)
				d2 = Double.parseDouble(doubles[1]);
		}
		catch (Exception e) {
			messages.add(
					new InvalidArgumentError(
							bundle.getString("WRONG_ANSWER_MARK")));
			return messages;
		}

		if ( d1 < 0 ) {
			messages.add(
					new InvalidArgumentError(
							bundle.getString("GREATER_ZERO")));
		}

		if (d2 >= 0) {
			messages.add(
					new InvalidArgumentError(
							bundle.getString("LOWER_ZERO")));
		}
		return messages;
	}
}

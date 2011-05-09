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
package de.knowwe.caseTrain.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.caseTrain.message.InvalidArgumentError;
import de.knowwe.caseTrain.type.Antworten.Antwort.AntwortMarkierung;


/**
 * Simply checks, if the string t inside {t} has the allowed
 * symbols. +/-/number
 * 
 * @author Johannes Dienst
 * @created 08.05.2011
 */
public class AntwortMarkierungHandler extends GeneralSubtreeHandler<AntwortMarkierung> {

	String[] symbols = {"+", "-"};

	private static AntwortMarkierungHandler uniqueInstance;

	private AntwortMarkierungHandler(){
		// Nothing
	}

	public static AntwortMarkierungHandler getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new AntwortMarkierungHandler();
		return uniqueInstance;
	}

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<AntwortMarkierung> s) {

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
			messages.add(new InvalidArgumentError("Nur '+' oder '-' oder Zahlen größer 0"));
			return messages;
		}

		if ( d1 < 0 ) {
			messages.add(new InvalidArgumentError("Nur Zahlen größer 0"));
		}

		if (d2 >= 0) {
			messages.add(new InvalidArgumentError("Nur Zahlen kleiner 0"));
		}
		return messages;
	}
}

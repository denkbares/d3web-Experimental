/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.revisions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;

/**
 * 
 * @author grotheer
 * @created 29.03.2013
 */
public class DateType extends AbstractType {

	private static final String DATE = "\\d{4}(-\\d{2}){2}\\s\\d{2}(:\\d{2}){2}";
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private static final Pattern DATE_PATTERN = Pattern.compile(DATE);

	public DateType() {
		sectionFinder = new AllTextFinderTrimmed();
		addSubtreeHandler(new DateSubtreeHandler());
	}

	public static boolean isValid(String sectionText) {
		return DATE_PATTERN.matcher(sectionText).matches();
	}

	public static String createTimeAsTimeStamp(long time) {
		return DATE_FORMAT.format(new Date(time));
	}

	public static long getTimeInMillis(Section<DateType> sec) throws ParseException {
		return getTimeInMillis(sec.getText());
	}

	public static long getTimeInMillis(String dateString) throws ParseException {
		return DATE_FORMAT.parse(dateString).getTime();
	}

	class DateSubtreeHandler extends SubtreeHandler<DateType> {

		@Override
		public Collection<Message> create(Article article, Section<DateType> s) {
			System.out.println("running");
			if (DateType.isValid(s.getText())) {
				return Collections.emptyList();
			}
			else {
				LinkedList<Message> list = new LinkedList<Message>();
				list.add(Messages.syntaxError("Invalid date: '" + s.getText() + "'"));

				return list;

			}

		}

	}
}

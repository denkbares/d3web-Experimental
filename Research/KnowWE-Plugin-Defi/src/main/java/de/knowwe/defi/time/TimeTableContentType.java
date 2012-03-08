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
package de.knowwe.defi.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;

public class TimeTableContentType extends AbstractType {

	public TimeTableContentType() {
		this.setSectionFinder(new AllTextSectionFinder());

		this.addChildType(new TimeTableLine());
	}

	class TimeTableLine extends AbstractType {

		TimeTableLine() {
			this.setSectionFinder(new LineSectionFinder());
			this.addChildType(new DateT());
		}
	}
}

class DateT extends AbstractType {

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd.MM.yyyy");

	public DateT() {
		this.setSectionFinder(new AllTextSectionFinder());
		this.addSubtreeHandler(new DateChecker());
	}

	class DateChecker extends GeneralSubtreeHandler<DateT> {

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<DateT> s) {
			String dateText = s.getText().trim();
			Date d = null;
			try {
				d = dateFormat.parse(dateText);
			}
			catch (ParseException e) {
				List<Message> messages = new ArrayList<Message>(1);
				messages.add(Messages.error("Invalid Date: " + dateText));
				return messages;
			}
			return new ArrayList<Message>(0);
		}

	}
}

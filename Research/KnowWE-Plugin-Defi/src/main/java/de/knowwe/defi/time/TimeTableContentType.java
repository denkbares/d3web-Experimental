package de.knowwe.defi.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;

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
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<DateT> s) {
			String dateText = s.getOriginalText().trim();
			Date d = null;
			try {
				d = dateFormat.parse(dateText);
			}
			catch (ParseException e) {
				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(1);
				messages.add(new InvalidDateError(dateText));
				return messages;
			}
			return new ArrayList<KDOMReportMessage>(0);
		}

	}
}

class InvalidDateError extends KDOMError {

	private final String messageText;

	public InvalidDateError(String s) {
		this.messageText = s;
	}

	@Override
	public String getVerbalization() {
		return "Invalid Date: " + messageText;
	}

}

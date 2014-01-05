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

import java.text.ParseException;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.NothingRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class RevisionType extends DefaultMarkupType {

	public static final String NAME_KEY = "name";
	public static final String DATE_KEY = "date";
	public static final String COMMENT_KEY = "comment";

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("Revision");
		m.addAnnotation(NAME_KEY, true);
		m.addAnnotation(DATE_KEY, true);
		m.addAnnotationContentType(DATE_KEY, new DateType());
		m.addAnnotation(COMMENT_KEY, false);
	}

	public RevisionType() {
		super(m);
		this.setRenderer(NothingRenderer.getInstance());
	}

	public static String getRevisionName(Section<RevisionType> section) {
		return DefaultMarkupType.getAnnotation(section, NAME_KEY);
	}

	public static String getRevisionDate(Section<RevisionType> section) {
		return DefaultMarkupType.getAnnotation(section, DATE_KEY);
	}

	public static String getRevisionComment(Section<RevisionType> section) {
		return DefaultMarkupType.getAnnotation(section, COMMENT_KEY);
	}

	/**
	 * create json data element
	 * 
	 * @created 21.04.2013
	 * @param section
	 * @return a json data element as string
	 */
	public static String toTimelineString(Section<RevisionType> section) {
		Section<DateType> dateSection = Sections.findSuccessor(section, DateType.class);

		long time = 0;
		try {
			time = DateType.getTimeInMillis(dateSection);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		String result = "";
		if (DateType.isValid(dateSection.getText())) {
			result += "{\n" +
					"\'start\': new Date(" + time + "),\n" +
					"\'content\': \'" + getRevisionName(section) + "\',\n" +
					// "\'comment\': \'" + getRevisionComment(section) + "\'\n"
					// +
					// "\'group\': \'Wiki\',\n" +
					"\'id\': \'" + section.getID() + "\'\n" +
					"},\n";
		}
		else {
			System.out.println("invalid revision ignored");
		}
		// TODO Auto-generated method stub
		return result;
	}
}

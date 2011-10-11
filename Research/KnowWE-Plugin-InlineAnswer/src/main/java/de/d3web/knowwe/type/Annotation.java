package de.d3web.knowwe.type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.knowwe.renderer.AnnotationInlineAnswerRenderer;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.kdom.AnonymousType;


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

/**
 * 
 * @author Johannes Dienst
 * @created 09.06.2011
 */
public class Annotation extends AbstractType {

	private static String ANNOTATIONBEGIN = "\\{\\{";
	private static String ANNOTATIONEND = "\\}\\}";

	@Override
	public void init() {
		AnonymousType at1 = new AnonymousType("InlineAnnotationBegin");
		at1.setSectionFinder(new RegexSectionFinder(ANNOTATIONBEGIN));
		AnonymousType at2 = new AnonymousType("InlineAnnotationEnd");
		at2.setSectionFinder(new RegexSectionFinder(ANNOTATIONEND));
		this.childrenTypes.add(at1);
		this.childrenTypes.add(at2);
		this.childrenTypes.add(new AnnotationContent());
		this.sectionFinder = new AnnotationSectionFinder();
		this.setCustomRenderer(new AnnotationInlineAnswerRenderer());
	}

	public class AnnotationSectionFinder implements SectionFinder {

		private final String PATTERN = ANNOTATIONBEGIN + "[\\w\\W]*?" + ANNOTATIONEND;

		@Override
		public List<SectionFinderResult> lookForSections(String text,
				Section<?> father, Type type) {
			ArrayList<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
			Pattern p = Pattern.compile(PATTERN);
			Matcher m = p.matcher(text);
			while (m.find()) {
				String found = m.group();
				if (found.contains("::")) result.add(new SectionFinderResult(m.start(), m.end()));
			}
			return result;

		}

	}
}

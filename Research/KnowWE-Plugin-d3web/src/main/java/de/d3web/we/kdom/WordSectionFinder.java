package de.d3web.we.kdom;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;

public class WordSectionFinder implements SectionFinder {

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section father, Type type) {

		if (!text.equals(" ") && !text.equals("\"")
				&& !text.contains("(") && !text.contains(")")) {

			int start = 0;
			int end = text.length();
			while (text.charAt(start) == ' ' || text.charAt(start) == '"') {
				start++;
				if (start >= end) return null;
			}
			while (text.charAt(end - 1) == ' ' || text.charAt(end - 1) == '"') {
				end--;
				if (start >= end) return null;
			}

			List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
			result.add(new SectionFinderResult(start, end));
			return result;
		}
		return null;
	}

}

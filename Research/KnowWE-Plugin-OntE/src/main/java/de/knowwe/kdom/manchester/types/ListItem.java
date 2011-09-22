package de.knowwe.kdom.manchester.types;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

public class ListItem extends NonTerminalCondition {

	public static final String PATTERN = "(" +
			"[^\"]" + // everything not in quotes
			"|" +
			"\"[^\"]*\"" + // if in quotes everything that is not a quote
			")*?" +
			"(,|\\z)"; // till comma or line end

	public static final char KOMMA = ',';

	@Override
	protected void init() {
		this.sectionFinder = new ContentFinder();
	}

	class ContentFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

			String trimmed = text.trim();

			if (text.contains(Character.toString(KOMMA))) {
				List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();

				char[] chars = text.toCharArray();

				int currentEnd = 0;
				int currentStart = text.indexOf(trimmed);
				boolean quoted = false;

				for (int i = 0; i < chars.length; i++) {
					if (Character.valueOf('\u002c').equals(chars[i]) && !quoted) {
						currentEnd = i;
						results.add(new SectionFinderResult(currentStart, currentEnd));
						currentStart = i + 1;
					} else if(i + 1 == text.length()) {
						currentEnd = text.length();
						results.add(new SectionFinderResult(currentStart, currentEnd));
					}
					else if (Character.valueOf('\u0022').equals(chars[i])) {
						quoted = !quoted;
					}
				}
				return results;
			}
			return null;
		}
	}
}

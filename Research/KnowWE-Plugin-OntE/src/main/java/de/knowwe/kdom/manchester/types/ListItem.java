package de.knowwe.kdom.manchester.types;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

public class ListItem extends NonTerminalCondition {

	/**
	 * OLD regular expression for matching comma separated lists.
	 */
	public static final String PATTERN = "(" +
			"[^\"]" + // everything not in quotes
			"|" +
			"\"[^\"]*\"" + // if in quotes everything that is not a quote
			")*?" +
			"(,|\\z)"; // till comma or line end

	public static final char COMMA = '\u002c';
	public static final char QUOTE = '\u0022';

	@Override
	protected void init() {
		this.sectionFinder = new ContentFinder();
	}

	class ContentFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

			String trimmed = text.trim();

			if (text.contains(Character.toString(COMMA))) {
				List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();

				// Comma should be marked as PlainText
				if (trimmed.length() == 1 && trimmed.equals(Character.valueOf(COMMA))) {
					return null;
				}

				char[] chars = text.toCharArray();

				int currentEnd = 0;
				int currentStart = text.indexOf(trimmed);
				boolean quoted = false;

				for (int i = 0; i < chars.length; i++) {
					if (Character.valueOf(COMMA).equals(chars[i]) && !quoted) {
						currentEnd = i;
						results.add(new SectionFinderResult(currentStart, currentEnd));
						currentStart = i + 1;
					} else if(i + 1 == text.length()) {
						currentEnd = text.length();
						results.add(new SectionFinderResult(currentStart, currentEnd));
					}
					else if (Character.valueOf(QUOTE).equals(chars[i])) {
						quoted = !quoted;
					}
				}
				return results;
			}
			return null;
		}
	}
}

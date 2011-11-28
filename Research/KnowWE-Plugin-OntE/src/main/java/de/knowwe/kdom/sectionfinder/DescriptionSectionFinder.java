package de.knowwe.kdom.sectionfinder;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;

public class DescriptionSectionFinder implements SectionFinder {

	public static final char COLON = '\u003A';

	private String keyword = "";

	public DescriptionSectionFinder(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
		String trimmed = text.trim();

		if (text.contains(Character.toString(COLON))) {

			boolean keywordMatch = false;

			List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();

			char[] chars = text.toCharArray();

			int currentEnd = 0;
			int currentStart = text.indexOf(trimmed);
			int lastSpaceLinebreak = 0;

			for (int i = 0, l = chars.length; i < l; i++) {

				char ch = chars[i];

				if (Character.isWhitespace(ch)) {
					lastSpaceLinebreak = i;
				}

				switch (chars[i]) {

				case COLON:
					if (!keywordMatch) { // no possible match found until now
						String possibleKeyword = text.substring(lastSpaceLinebreak + 1, i);

						if (possibleKeyword.equalsIgnoreCase(keyword)) {
							keywordMatch = true;
							currentStart = lastSpaceLinebreak;
						}
					}
					else {
						// another colon found;
						// followed by space?
						if (i + 1 < l) {
							if (Character.isWhitespace(chars[i + 1])) {
								// found another keyword
								currentEnd = lastSpaceLinebreak;
								results.add(new SectionFinderResult(currentStart, currentEnd));
								currentStart = i + 1;
								return results;
							}
						}
					}
					break;

				default:
					break;
				}
			}
			if (keywordMatch) {
				results.add(new SectionFinderResult(currentStart, text.length()));
				return results;
			}
			return null;
		}
		return null;
	}
}

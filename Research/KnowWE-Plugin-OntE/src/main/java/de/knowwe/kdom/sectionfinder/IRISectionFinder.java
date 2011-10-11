package de.knowwe.kdom.sectionfinder;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;

/**
 * Finds the IRI in a prefix definition
 *
 * @author Stefan Mark
 * @created 29.09.2011
 */
public class IRISectionFinder implements SectionFinder {

	public static final char LESS_THAN = '\u003c';
	public static final char GREATER_THAN = '\u003e';

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section father, Type type) {

		String trimmed = text.trim();

		if (text.contains(Character.toString(LESS_THAN))) {
			List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();

			// Less and greater than should be marked as PlainText
			if (trimmed.length() == 1
					&& (trimmed.equals(Character.valueOf(LESS_THAN)) || trimmed.equals(Character.valueOf(GREATER_THAN)))) {
				return null;
			}

			char[] chars = text.toCharArray();

			int currentEnd = 0;
			int currentStart = text.indexOf(trimmed);

			for (int i = 0; i < chars.length; i++) {
				if (Character.valueOf(LESS_THAN).equals(chars[i])) {
					currentStart = i;
				}
				if (Character.valueOf(GREATER_THAN).equals(chars[i])) {
					// found greater than -> create result
					currentEnd = i;
					results.add(new SectionFinderResult(currentStart, currentEnd + 1));
					currentStart = i + 1;
				}
			}
			return results; // return found results
		}
		return null;
	}
}

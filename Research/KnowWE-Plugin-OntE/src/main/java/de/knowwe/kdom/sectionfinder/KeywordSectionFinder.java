package de.knowwe.kdom.sectionfinder;

import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

/**
 * Simply the section finder for the {@link MiscIndividuals} frame of an
 * ontology.
 *
 * @author Stefan Mark
 * @created 21.09.2011
 */
public class KeywordSectionFinder implements SectionFinder {

	private String KEYWORD = "";

	public KeywordSectionFinder(String keyword) {
		if (keyword != null) {
			this.KEYWORD = keyword;
		}
	}

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

		Pattern p = Pattern.compile(KEYWORD, Pattern.CASE_INSENSITIVE);

		if (p.matcher(text).find()) {
			String trimmed = text.trim();
			int leadingSpaces = text.indexOf(trimmed);
			return SectionFinderResult.createSingleItemList(new SectionFinderResult(
						leadingSpaces, text.length()));
		}
		return null;
	}
}

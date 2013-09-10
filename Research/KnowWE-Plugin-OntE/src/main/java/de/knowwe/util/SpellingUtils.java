package de.knowwe.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.wcohen.ss.Levenstein;

import de.knowwe.core.correction.CorrectionProvider;
import de.knowwe.core.kdom.parsing.Section;


public class SpellingUtils {

	/**
	 * Assume misspelling and calculate possible corrections for a unknown
	 * keyword for a description of a Manchester OWl syntax frame.
	 *
	 * @created 11.11.2011
	 * @param section
	 */
	public static boolean hasCorrections(Section<?> section, String keyword) {
		return !getCorrections(section, keyword).isEmpty();
	}

	/**
	 * Assume misspelling and calculate possible corrections for a unknown
	 * keyword for a description of a Manchester OWl syntax frame.
	 *
	 * @created 11.11.2011
	 * @param section
	 */
	public static List<CorrectionProvider.Suggestion> getCorrections(Section<?> section, String keyword) {

		Set<String> possibleMatches = new HashSet<String>();
		List<CorrectionProvider.Suggestion> suggestions = new LinkedList<CorrectionProvider.Suggestion>();

		for (ManchesterSyntaxKeywords k : ManchesterSyntaxKeywords.values()) {
			Class<?> cls = section.get().getClass();
			if (k.inContext(cls)) {
				possibleMatches.add(k.getKeyword());
			}
		}

		Levenstein l = new Levenstein();
		for (String match : possibleMatches) {
			double score = l.score(keyword, match);
			if (score >= -2) {
				suggestions.add(new CorrectionProvider.Suggestion(match, (int) score));
			}
		}
		return suggestions;
	}

}

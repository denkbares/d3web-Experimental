/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.wcohen.ss.Levenstein;

import de.knowwe.core.correction.CorrectionProvider;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.manchester.frame.DefaultFrame;
import de.knowwe.kdom.manchester.types.MisspelledSyntaxConstruct;

/**
 * A Correction Provider for keywords in the Manchester OWL syntax frames.
 *
 * @author Stefan Mark
 * @created 10.11.2011
 */
public class KeywordCorrectionProvider implements CorrectionProvider {

	@Override
	public List<CorrectionProvider.Suggestion> getSuggestions(Article article, Section<?> section, int threshold) {
		if (!(section.get() instanceof MisspelledSyntaxConstruct)) {
			return null;
		}

		Section<DefaultFrame> defaultFrame = Sections.findAncestorOfType(section,
				DefaultFrame.class);

		List<CorrectionProvider.Suggestion> suggestions = new LinkedList<CorrectionProvider.Suggestion>();
		Levenstein l = new Levenstein();

		if(defaultFrame != null) {

			String originalText = section.getText();
			Set<String> possibleMatches = new HashSet<String>();

			for (ManchesterSyntaxKeywords k : ManchesterSyntaxKeywords.values()) {
				if (k.inContext(defaultFrame.get().getClass())) {
					possibleMatches.add(k.getKeyword());
				}
			}

			for (String match : possibleMatches) {
				double score = l.score(originalText, match);
				if (score >= -threshold) {
					suggestions.add(new CorrectionProvider.Suggestion(match, (int)score));
				}
			}
		}
		return suggestions;
	}
}

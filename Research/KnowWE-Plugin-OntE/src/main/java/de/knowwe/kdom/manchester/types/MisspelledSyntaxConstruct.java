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
package de.knowwe.kdom.manchester.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.knowwe.core.correction.CorrectionProvider;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.report.message.UnexpectedSequence;
import de.knowwe.tools.ToolMenuDecoratingRenderer;
import de.knowwe.util.SpellingUtils;

public class MisspelledSyntaxConstruct extends AbstractType {

	public final static char COLON = '\u003A';

	@SuppressWarnings("unchecked")
	public MisspelledSyntaxConstruct() {
		this.setSectionFinder(new MisspelledFrameKeywordFinder());
		this.setCustomRenderer(new ToolMenuDecoratingRenderer<MisspelledSyntaxConstruct>(
				new StyleRenderer(
						"color:rgb(255, 0, 0)")));

		this.addSubtreeHandler(new GeneralSubtreeHandler<MisspelledSyntaxConstruct>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<MisspelledSyntaxConstruct> s) {

				String messageText = "";
				StringBuilder corrections = new StringBuilder();
				for(CorrectionProvider.Suggestion sug : SpellingUtils.getCorrections(s.getFather(),
						s.getOriginalText())) {
					corrections.append(sug.getSuggestion());
				}

				return Arrays.asList((KDOMReportMessage) new UnexpectedSequence(
						messageText + s.getOriginalText() + "; Did you mean: " + corrections));
			}
		});

	}

	/**
	 * 
	 *
	 * @author Stefan Mark
	 * @created 29.09.2011
	 */
	class MisspelledFrameKeywordFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

			String trimmed = text.trim();

			if (text.contains(Character.toString(COLON))) {
				// check for misspelled keyword
				char[] chars = trimmed.toCharArray();

				for (int i = 0; i < chars.length; i++) {
					char current = chars[i];

					if (!Character.isLetterOrDigit(current)) {
						int startPos = text.indexOf(trimmed);
						String possibleKeyword = text.substring(startPos, startPos + i);

						if (SpellingUtils.hasCorrections(father, possibleKeyword)) {
							List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();
							results.add(new SectionFinderResult(startPos, startPos + i));
							return results;
						}
						return null;
					}
				}
			}
			return null;
		}


	}
}
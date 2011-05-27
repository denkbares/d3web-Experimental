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
package de.knowwe.casetrain.info;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.casetrain.renderer.DivStyleClassRenderer;
import de.knowwe.casetrain.renderer.MouseOverTitleRenderer;

/**
 * Antwort is a Antwort Line.
 * Contains a AntwortContent which has the following syntax:
 * {Markierung}Antwort{Antwortspezifische Erklärung}
 * 
 * 
 * @author Jochen
 * @created
 */
public class AnswerLine extends AbstractType {

	public AnswerLine() {
		this.setSectionFinder(new AnswerSectionFinder());
		this.setCustomRenderer(new DivStyleClassRenderer("Antwort"));
		this.addChildType(new AnswerMark());
		this.addChildType(new AnswerTextArgument());
		this.addChildType(new AnswerText());
		this.addChildType(new AnswerExplanation());
	}

	/**
	 * 
	 * @author Johannes Dienst
	 * @created 12.05.2011
	 */
	class AnswerSectionFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
			List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();

			Pattern p = Pattern.compile("\\{[0-9]+\\}");
			Matcher m = p.matcher(text);
			if (m.matches()) return results;
			if (text.startsWith(AnswerValidator.PRAEFIX)
					|| text.startsWith(AnswerValidator.POSTFIX)
					|| text.startsWith(AnswerValidator.UEBERSCHRIFT)) {
				return results;
			}
			results.add(new SectionFinderResult(0, text.length()));
			return results;
		}

	}

	/**
	 * NumAnswerText can be an Interval. This method
	 * returns this Interval or null if it is not an
	 * Interval.
	 * 
	 * @created 18.05.2011
	 * @param antwortText
	 * @return
	 */
	public static String[] getInterval(String answerText) {
		String[] i = answerText.split("[ ]+");
		if (i.length == 2) {
			try {
				new BigDecimal(i[0]);
				new BigDecimal(i[1]);
			} catch(NumberFormatException e) {
				return null;
			}
			return i;
		}
		return null;
	}

	/**
	 * Returns the PosFactor of a given AntwortMarkierung-Section.
	 * If none is specified then it returns 1.
	 * 
	 * @created 13.05.2011
	 * @param sec
	 * @return
	 */
	public static String getPosFactor(Section<AnswerLine> sec) {
		Section<AnswerMark> mark = Sections.findSuccessor(sec, AnswerMark.class);
		if (mark == null) return "1";
		String markText = mark.getOriginalText().trim();
		markText = markText.substring(1, markText.length()-1);
		markText = AnswerLine.replaceFactorWithNumber(markText);
		String[] factors = markText.trim().split("[ ]+");
		return factors[0];
	}

	/**
	 * 
	 * Returns the NegFactor of a given AntwortMarkierung-Section.
	 * If none is specified then it returns null;
	 * 
	 * @created 13.05.2011
	 * @param sec
	 * @return
	 */
	public static String getNegFactor(Section<AnswerLine> sec) {
		Section<AnswerMark> mark = Sections.findSuccessor(sec, AnswerMark.class);
		if (mark == null) return null;
		String markText = mark.getOriginalText().trim();
		markText = markText.substring(1, markText.length()-1);
		markText = AnswerLine.replaceFactorWithNumber(markText);
		String[] factors = markText.trim().split("[ ]+");
		if (factors.length < 2) return null;
		return factors[1];
	}

	private static String replaceFactorWithNumber(String factor) {
		factor = factor.replaceAll("\\+", "1");
		factor = factor.replaceAll("\\-", "0");
		return factor;
	}

	/**
	 * {@link AnswerValidator}
	 * 
	 * @author Johannes Dienst
	 * @created 08.05.2011
	 */
	public class AnswerMark extends AbstractType {

		String regex = "\\{(.*?)\\}";

		public AnswerMark() {
			this.setCustomRenderer(new StyleRenderer("font-weight:bold;"));
			ConstraintSectionFinder csf = new ConstraintSectionFinder(
					new RegexSectionFinder(regex));
			csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
			this.addSubtreeHandler(AnswerMarkHandler.getInstance());
		}

	}

	/**
	 * Represents {r/f/Number};
	 * 
	 * @author Johannes Dienst
	 * @created 18.05.2011
	 */
	public class AnswerTextArgument extends AbstractType {

		public AnswerTextArgument() {
			ConstraintSectionFinder csf = new ConstraintSectionFinder(
					new RegexSectionFinder("\\{.*?\\}"));
			csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
		}

	}

	/**
	 * 
	 * @author Johannes Dienst
	 * @created 08.05.2011
	 */
	public class AnswerText extends AbstractType {

		// TODO Regex only recognizes {r}word
		//      not regex in full.
		//			String regex = "(\\{.*?\\})?([\\w]{1}[äüöÄÜÖß]?[ 0-9]*)+";

		public AnswerText() {
			this.setCustomRenderer(MouseOverTitleRenderer.getInstance());
			//				ConstraintSectionFinder csf = new ConstraintSectionFinder(
			//						new RegexSectionFinder(regex));
			//				csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
			//				this.setSectionFinder(csf);
			this.setSectionFinder(new AntwortTextSectionFinder());
		}

		class AntwortTextSectionFinder implements SectionFinder {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

				List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();
				int start = 0;
				int end = text.length();

				Pattern p = Pattern.compile("\\{.*?\\}");
				Matcher m = p.matcher(text);
				while (m.find()) {
					if (m.end() == text.length()) {
						end = m.start();
						break;
					}
				}

				results.add(new SectionFinderResult(start, end));

				return results;
			}

		}

	}

	/**
	 * 
	 * @author Johannes Dienst
	 * @created 08.05.2011
	 */
	public class AnswerExplanation extends AbstractType {

		String regex = "\\{.*?\\}";

		public AnswerExplanation() {
			this.setCustomRenderer(MouseOverTitleRenderer.getInstance());
			ConstraintSectionFinder csf = new ConstraintSectionFinder(
					new RegexSectionFinder(regex));
			csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
		}
	}
}
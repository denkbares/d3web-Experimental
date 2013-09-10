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

import de.knowwe.casetrain.info.AnswersBlock.Heading;
import de.knowwe.casetrain.info.AnswersBlock.Postfix;
import de.knowwe.casetrain.info.AnswersBlock.Praefix;
import de.knowwe.casetrain.renderer.DivStyleClassRenderer;
import de.knowwe.casetrain.renderer.MouseOverTitleRenderer;
import de.knowwe.casetrain.renderer.SpanClassRenderer;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.ExactlyOneFindingConstraint;

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
		this.setRenderer(new DivStyleClassRenderer("Antwort", null));
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

			Pattern p = Pattern.compile("\\s*\\{[0-9]+\\}");
			Matcher m = p.matcher(text);
			if (m.matches()) return results;

			p = Pattern.compile("\\s*"+Praefix.NAME+":.*");
			m = p.matcher(text);
			if (m.matches()) return results;

			p = Pattern.compile("\\s*"+Postfix.NAME+":.*");
			m = p.matcher(text);
			if (m.matches()) return results;

			p = Pattern.compile("\\s*"+Heading.NAME+":.*");
			m = p.matcher(text);
			if (m.matches()) return results;

			p = Pattern.compile("\\s*");
			m = p.matcher(text);
			if (m.matches()) return results;

			m.reset();
			int i = 0;
			if (m.find()) {
				i = m.end();
			}

			results.add(new SectionFinderResult(i, text.length()));
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
		String markText = mark.getText().trim();
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
		String markText = mark.getText().trim();
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
	 * {@link AnswersBlockValidator}
	 * 
	 * @author Johannes Dienst
	 * @created 08.05.2011
	 */
	public class AnswerMark extends AbstractType {

		String regex = "\\{(.*?)\\}";

		public AnswerMark() {
			this.setRenderer(new SpanClassRenderer(SpanClassRenderer.META_KEY));
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
			this.setRenderer(new SpanClassRenderer(SpanClassRenderer.META_KEY));
			ConstraintSectionFinder csf = new ConstraintSectionFinder(
					new RegexSectionFinder("\\{[rf1-9]}"));
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
			this.setRenderer(MouseOverTitleRenderer.getInstance());
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
			this.setRenderer(MouseOverTitleRenderer.getInstance());
			ConstraintSectionFinder csf = new ConstraintSectionFinder(
					new RegexSectionFinder(regex));
			csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
		}
	}
}

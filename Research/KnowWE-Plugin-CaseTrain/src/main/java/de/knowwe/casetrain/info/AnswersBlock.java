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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.kdom.type.AnonymousType;
import de.knowwe.casetrain.message.MissingComponentWarning;
import de.knowwe.casetrain.renderer.DivStyleClassRenderer;
import de.knowwe.casetrain.renderer.SpanClassRenderer;
import de.knowwe.casetrain.type.general.SubblockMarkup;


/**
 * Part of Info: Contains Antwort-Lines.
 * 
 * @author Jochen Reutelshoefer
 * @created 28.04.2011
 */
public class AnswersBlock extends SubblockMarkup {

	public AnswersBlock() {
		super("Antworten");
		AnonymousType at = new AnonymousType("LineBreak");
		at.setSectionFinder(new RegexSectionFinder("\\r?\\n"));
		this.addContentType(at);
		this.addContentType(new AnswerLine());
		this.addContentType(new Praefix());
		this.addContentType(new Postfix());
		this.addContentType(new Heading());
		this.addContentType(new AnswersBlockWeightMark());

		this.addSubtreeHandler(new GeneralSubtreeHandler<Question>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Question> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);
				List<Section<AnswerLine>> found = new ArrayList<Section<AnswerLine>>();
				Sections.findSuccessorsOfType(s, AnswerLine.class, found);

				if (found.isEmpty()) {
					messages.add(new MissingComponentWarning(AnswerLine.class.getSimpleName()));
				}

				return messages;
			}
		});
	}

	/**
	 * 
	 * @author Johannes Dienst
	 * @created 09.05.2011
	 */
	public class Praefix extends AbstractType {

		public static final String NAME = "Präfix";

		public Praefix() {
			this.setSectionFinder(
					new RegexSectionFinder(NAME + ":.*"));
			this.setCustomRenderer(
					new DivStyleClassRenderer("praefix",
							new SpanClassRenderer(SpanClassRenderer.META_KEY)));
		}
	}

	/**
	 * 
	 * @author Johannes Dienst
	 * @created 09.05.2011
	 */
	public class Postfix extends AbstractType {

		public static final String NAME = "Postfix";

		public Postfix() {
			this.setSectionFinder(
					new RegexSectionFinder(NAME + ":.*"));
			this.setCustomRenderer(
					new DivStyleClassRenderer("postfix",
							new SpanClassRenderer(SpanClassRenderer.META_KEY)));
		}
	}

	/**
	 * 
	 * @author Johannes Dienst
	 * @created 09.05.2011
	 */
	public class Heading extends AbstractType {

		public static final String NAME = "Überschrift";

		public Heading() {
			this.setSectionFinder(
					new RegexSectionFinder(NAME + ":.*"));
			this.setCustomRenderer(
					new DivStyleClassRenderer(
							"ueberschrift", new SpanClassRenderer(SpanClassRenderer.META_KEY)));
		}
	}

	/**
	 * In MN/OMW the {@link Antwortenblock} can have a block-weight mark.
	 * 
	 * @author Johannes Dienst
	 * @created 20.05.2011
	 */
	public class AnswersBlockWeightMark extends AbstractType {
		public AnswersBlockWeightMark() {
			this.setSectionFinder(
					new RegexSectionFinder("\\{[0-9]+\\}"));
			this.setCustomRenderer(new SpanClassRenderer(SpanClassRenderer.META_KEY));
		}

	}

	/**
	 * Extracts the Weight.
	 * 
	 * @created 03.06.2011
	 * @param weight
	 * @return
	 */
	public static String getWeight(String weight) {
		return weight.replaceAll("[\\{\\}]", "");
	}

	/**
	 * 
	 * @created 18.05.2011
	 * @param t
	 * @return
	 */
	public static Long getEditDistance(String t) {
		try {
			return new Long(t.substring(1, t.length()-1).trim());
		} catch(NumberFormatException e) {
			// do nothing here is right!
		} catch(NullPointerException e1) {
			// do nothing here is right!
		}
		return Long.valueOf("0");
	}

	/**
	 * 
	 * @created 18.05.2011
	 * @param t
	 * @return
	 */
	public static Boolean getIsRegularExpression(String t) {
		if (t == null) return false;
		return t.substring(1, t.length()-1).trim().equals("r");
	}
}

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
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;

/**
 *
 * @author smark
 * @created 13.08.2011
 */
public class Annotations extends AbstractType {

	public final static String KEYWORD = "Annotations[:]?";

	public final static String KEYWORDS = "("
			+ RDFSLabel.KEYWORD + "|"
			+ RDFSComment.KEYWORD
			+ "|\\z)";

	public static final StyleRenderer LABEL_RENDERER = new StyleRenderer("color:rgb(181, 159, 19)");
	public static final StyleRenderer TERM_RENDERER = new StyleRenderer("color:rgb(67, 85, 190)");
	public static final StyleRenderer TAG_RENDERER = new StyleRenderer("color:rgb(255, 83, 13)");

	/**
	 *
	 * @param String frame The keywords that appear in the current frame
	 */
	public Annotations(String frame) {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(frame, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		this.addChildType(new AnnotationsContentType());
	}

	/**
	 *
	 * @author smark
	 * @created 18.05.2011
	 */
	class AnnotationsContentType extends AbstractType {

		protected AnnotationsContentType() {
			this.setSectionFinder(new AllTextFinderTrimmed());

			Keyword key = new Keyword(KEYWORD);
			this.addChildType(key);

			List<Type> t = new ArrayList<Type>();
			t.add(new RDFSLabel());
			t.add(new RDFSComment());

			CommaSeparatedList csl = new CommaSeparatedList(t);
			this.addChildType(csl);
		}
	}

	/**
	 *
	 *
	 * @author smark
	 * @created 13.08.2011
	 */
	public static class RDFSLabel extends AbstractType {

		public static final String KEYWORD = "rdfs:label";

		public RDFSLabel() {
			Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(Annotations.KEYWORDS, KEYWORD);
			this.setSectionFinder(new RegexSectionFinder(p));

			Keyword key = new Keyword(KEYWORD);
			key.setCustomRenderer(Annotations.LABEL_RENDERER);
			this.addChildType(key);
			this.addChildType(new AnnotationTerm());
			this.addChildType(new AnnotationLanguageTag());
			this.addChildType(new AnnotationDatatypeTag());
		}
	}

	/**
	 *
	 *
	 * @author smark
	 * @created 13.08.2011
	 */
	public static class RDFSComment extends AbstractType {

		public static final String KEYWORD = "rdfs:comment";

		public RDFSComment() {
			Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(Annotations.KEYWORDS, KEYWORD);
			this.setSectionFinder(new RegexSectionFinder(p));

			Keyword key = new Keyword(KEYWORD);
			key.setCustomRenderer(Annotations.LABEL_RENDERER);
			this.addChildType(key);
			this.addChildType(new AnnotationTerm());
			this.addChildType(new AnnotationLanguageTag());
			this.addChildType(new AnnotationDatatypeTag());
		}
	}

	public static class AnnotationTerm extends AbstractType {

		public AnnotationTerm() {
			this.setSectionFinder(new RegexSectionFinder("\".*\""));
			this.setCustomRenderer(Annotations.TERM_RENDERER);
		}
	}

	public static class AnnotationLanguageTag extends AbstractType {

		private final String LANG_REGEX = "@([a-z]{2})";

		public AnnotationLanguageTag() {
			Pattern p = Pattern.compile(LANG_REGEX);
			this.setSectionFinder(new RegexSectionFinder(p, 1));
			this.setCustomRenderer(Annotations.TAG_RENDERER);
		}
	}

	public static class AnnotationDatatypeTag extends AbstractType {

		private final String LANG_REGEX = "\\^\\^([a-zA-Z:]+)";

		public AnnotationDatatypeTag() {
			Pattern p = Pattern.compile(LANG_REGEX);
			this.setSectionFinder(new RegexSectionFinder(p, 1));
			this.setCustomRenderer(Annotations.TAG_RENDERER);
		}
	}
}

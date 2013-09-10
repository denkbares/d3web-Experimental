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

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * <p>
 * Simple {@link AbstractType} for a {@link Annotation} of an element in a
 * ontology. For example: An annotation could be a RDFSLabel or RDFSComment
 * element.
 * </p>
 * <p>
 * The possible properties for an {@link Annotation} are defined by the W3C, see
 * http://www.w3.org/TR/owl-ref/#Header for further information.
 * </p>
 *
 * @author Stefan Mark
 * @created 21.09.2011
 */
public class Annotation extends AbstractType {

	public static final String KEYWORD_COMMENT = "comment";
	public static final String KEYWORD_LABEL = "label";
	public static final String KEYWORD_VERSION = "owl:versionInfo";
	public static final String KEYWORD_SEEALSO = "rdfs:seeAlso";
	public static final String KEYWORD_ISDEFINEDBY = "rdfs:isDefinedBy";

	public static final String PATTERN_LANG = "@([a-z]{2})";
	public static final String PATTERN_DATATYPE = "\\^\\^([a-zA-Z:]+)";

	public static final StyleRenderer LABEL_RENDERER = new StyleRenderer("color:rgb(181, 159, 19)");
	public static final StyleRenderer TERM_RENDERER = new StyleRenderer("color:rgb(67, 85, 190)");
	public static final StyleRenderer TAG_RENDERER = new StyleRenderer("color:rgb(255, 83, 13)");

	/**
	 * Constructor for the {@link Annotation} {@link AbstractType} type.
	 */
	public Annotation() {
		this.setSectionFinder(new AllTextFinderTrimmed());

		Keyword k = new Keyword(KEYWORD_COMMENT + "|" + KEYWORD_LABEL + "|" + KEYWORD_VERSION
				+ KEYWORD_ISDEFINEDBY + "|" + KEYWORD_SEEALSO);
		k.setRenderer(LABEL_RENDERER);

		this.addChildType(k);
		this.addChildType(new AnnotationTerm());
		this.addChildType(new AnnotationLanguageTag());
		this.addChildType(new AnnotationDatatypeTag());
		this.addChildType(new PlainText());
	}

	/**
	 * Check whether the current {@link Annotation} is a rdfs:label adding
	 * additional language information to a element in the ontology.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return TRUE if RDFSLabel, FALSE otherwise
	 */
	public boolean isLabel(Section<Annotation> a) {
		return checkAnnotationType(a, KEYWORD_LABEL);
	}

	/**
	 * Check whether the current {@link Annotation} is a rdfs:comment adding
	 * additional information to a element in the ontology.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return TRUE if RDFSComment, FALSE otherwise
	 */
	public boolean isComment(Section<Annotation> a) {
		return checkAnnotationType(a, KEYWORD_COMMENT);
	}

	/**
	 * Check whether the current {@link Annotation} is a owl:version adding
	 * additional information to a element in the ontology.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return TRUE if RDFSComment, FALSE otherwise
	 */
	public boolean isVersion(Section<Annotation> a) {
		return checkAnnotationType(a, KEYWORD_VERSION);
	}

	/**
	 * Check whether the current {@link Annotation} is a rdfs:seeAlso adding
	 * additional information to a element in the ontology.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return TRUE if RDFSComment, FALSE otherwise
	 */
	public boolean isSeeAlso(Section<Annotation> a) {
		return checkAnnotationType(a, KEYWORD_SEEALSO);
	}

	/**
	 * Check whether the current {@link Annotation} is a rdfs:isDefinedBy adding
	 * additional information to a element in the ontology.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return TRUE if RDFSComment, FALSE otherwise
	 */
	public boolean isVDefinedBy(Section<Annotation> a) {
		return checkAnnotationType(a, KEYWORD_ISDEFINEDBY);
	}

	private boolean checkAnnotationType(Section<Annotation> section, String key) {
		Section<Keyword> keywordSection = Sections.findSuccessor(section, Keyword.class);
		if (keywordSection != null) {
			String keywordTerm = keywordSection.get().getKeyword(keywordSection);
			if (keywordTerm.equals(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the current {@link Annotation} has a
	 * {@link AnnotationDatatypeTag}.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean hasDatatypeTag(Section<Annotation> a) {
		if (Sections.findSuccessor(a, AnnotationDatatypeTag.class) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether the current {@link Annotation} has a
	 * {@link AnnotationLanguageTag}.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean hasLanguageTag(Section<Annotation> a) {
		if (Sections.findSuccessor(a, AnnotationLanguageTag.class) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Retrieves the {@link AnnotationTerm} of the current {@link Annotation}
	 * and returns the result for further handling.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return The found {@link AnnotationTerm} section
	 */
	public Section<?> getTerm(Section<Annotation> a) {
		return Sections.findSuccessor(a, AnnotationTerm.class);
	}

	/**
	 * Retrieves the {@link AnnotationDatatypeTag} of the current
	 * {@link Annotation} and returns the result for further handling.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return The found {@link AnnotationDatatypeTag} section
	 */
	public Section<?> getDatatype(Section<Annotation> a) {
		return Sections.findSuccessor(a, AnnotationDatatypeTag.class);
	}

	/**
	 * Retrieves the {@link AnnotationLanguageTag} of the current
	 * {@link Annotation} and returns the result for further handling.
	 *
	 * @param Section<Annotation> a A {@link Annotation} section
	 * @return The found {@link AnnotationLanguageTag} section
	 */
	public Section<?> getLanguage(Section<Annotation> a) {
		return Sections.findSuccessor(a, AnnotationLanguageTag.class);
	}
}

/**
 * Simple {@link AbstractType} for the actual {@link Annotation} information.
 * This could be e.g. a label or a comment.
 *
 * @author Stefan Mark
 * @created 21.09.2011
 */
class AnnotationTerm extends AbstractType {

	public AnnotationTerm() {
		// StringLiteral
		this.setSectionFinder(new RegexSectionFinder("\".*\""));
		this.setRenderer(Annotation.TERM_RENDERER);
	}
}

/**
 * Simple {@link AbstractType} for the language information in a
 * {@link Annotation}.
 *
 * @author Stefan Mark
 * @created 21.09.2011
 */
class AnnotationLanguageTag extends AbstractType {

	public AnnotationLanguageTag() {
		Pattern p = Pattern.compile(Annotation.PATTERN_LANG);
		this.setSectionFinder(new RegexSectionFinder(p, 1));
		this.setRenderer(Annotation.TAG_RENDERER);
	}
}

/**
 * Simple {@link AbstractType} for the data type information in a
 * {@link Annotation}.
 *
 * @author Stefan Mark
 * @created 21.09.2011
 */
class AnnotationDatatypeTag extends AbstractType {

	public AnnotationDatatypeTag() {
		Pattern p = Pattern.compile(Annotation.PATTERN_DATATYPE);
		this.setSectionFinder(new RegexSectionFinder(p, 1));
		this.setRenderer(Annotation.TAG_RENDERER);
	}
}

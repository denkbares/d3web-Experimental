/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.kdom.defaultMarkup;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

/**
 * This class represents a section of the top-level default markup. That markup
 * always starts with "%%" followed by an alpha-numerical string. After that an
 * optional ":" is allowed. This is followed by either a one-line declaration or
 * a multiple-line-block terminated by an "/%" denoted in a line with no other
 * content. Because of backward-compatibility reasons, the multiple-line-block
 * can also be terminated by an single "%".
 * <p>
 * Within the block declaration you may use java-style end-line comments. Within
 * the single-line declaration you may also use this comments at the end of the
 * line. Note: there are no block comments ("/ * ... * /") allowed.
 * <p>
 * It is also allowed to define multiple additional annotations. An annotation
 * is denoted as "@", followed by its name without spacing. This
 * annotation-header may optionally followed by a ":" or "=". The content of the
 * parameter goes until a new parameter is defined or the markup block is
 * terminated.
 * <p>
 * <b>Examples:</b>
 * 
 * <pre>
 * %%rule &lt;condition&gt; --> &lt;action&gt;
 * 
 * %%rule // define 2 rules in one block
 *   &lt;condition&gt; --> &lt;action&gt;
 *   &lt;condition&gt; --> &lt;action&gt;
 * /%
 * 
 * %%rule // use annotations
 *   &lt;condition&gt; --> &lt;action&gt;
 *   &lt;condition&gt; --> &lt;action&gt;
 *   &#64;lazy: create 
 * /%
 * </pre>
 * 
 * <p>
 * The default mark-up forms a KDOM of the following structure. Please not that
 * there might be any PlainText section in between at any level:
 * 
 * <pre>
 * Section&lt;DefaultMarkupType&gt; // %%rule
 * |
 * +--Section&lt;ContentType&gt;
 * |  |
 * |  +--Rule-Block..1  // &lt;condition&gt; --> &lt;action&gt;
 * |  |
 * |  +--[Comment Text]
 * |  |
 * |  +--Rule-Block..n // &lt;condition&gt; --> &lt;action&gt;
 * |  |
 * |  +--...
 * |
 * +--Section&lt;AnnotationType&gt; // &#64;lazy: true
 * |
 * +--Section&lt;AnnotationType&gt; // &#64;...
 * |
 * +--...
 * </pre>
 * 
 * @author Volker Belli
 * 
 */
public class DefaultMarkupType extends DefaultAbstractKnowWEObjectType {

	private final static int FLAGS =
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL;

	private final static String SECTION_REGEXP =
				// Declaration
				"^\\p{Blank}*%%$NAME$\\p{Blank}*" +
						"(?:" +
						// multi-line content with termination
						// starts with an empty rest of the line (only comment
						// is allowed)
						// and followed by any text terminated by "/%" or a
						// single "%" in
						// a line with no other content
						"(?:[:=]?\\p{Blank}*(?://[^$]*?)?$" + // only comment
						// allowed
						// before
						// end-of-line
						"(.*?)" + // CONTENT --> anything in multiple lines
						// (reluctant
						// match)
						"^\\p{Blank}*/?%\\p{Blank}*$" + // "/%" or "%" in a line
						")" +
						// or single-line content with termination
						"|(?:" +
						// at least one non-whitespace character followed by any
						// non-line-break item
						"[:=\\p{Blank}]\\p{Blank}*([^/][^$]*?)$" + // CONTENT
						// -->
						// anything
						// in a single
						// line
						// (reluctant match)
						"))";

	private final DefaultMarkup markup;

	public DefaultMarkupType(DefaultMarkup markup) {
		this.markup = markup;
		Pattern pattern = getPattern(markup.getName());
		this.setSectionFinder(new RegexSectionFinder(pattern, 0));
		// add children
		this.childrenTypes.add(new ContentType(markup));
		for (DefaultMarkup.Annotation parameter : markup.getAnnotations()) {
			// update KDOM structure for the annotations
			this.childrenTypes.add(new AnnotationType(parameter));
		}
		this.childrenTypes.add(new UnknownAnnotationType());
		this.addSubtreeHandler(Priority.PRECOMPILE_HIGH, new DefaultMarkupSubtreeHandler(markup));
		this.addSubtreeHandler(new DefaultMarkupOwlHandler(this));
	}

	@Override
	protected KnowWEDomRenderer<?> getDefaultRenderer() {
		return new DefaultMarkupRenderer<DefaultMarkupType>();
	}

	// TODO: already exists in parent class! Is this a problem?
	@Override
	public String getName() {
		return this.markup.getName();
	}

	public DefaultMarkup getMarkup() {
		return this.markup;
	}

	/**
	 * Returns the contents of the default content block of the specified
	 * section. If the section is not of type "DefaultMarkup" an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param section the section to take the content block from
	 * @return the contents of the content block, if the section is not null. An
	 *         empty string otherwise.
	 * @throws IllegalArgumentException if the specified section is not of
	 *         {@link DefaultMarkupType}
	 */
	public static String getContent(Section<?> section) {
		Section<? extends ContentType> contentSection = getContentSection(section);
		if (contentSection != null) return contentSection.getOriginalText();
		else return "";
	}

	/**
	 * Returns the contents section of the default content block of the
	 * specified section. If the section is not of type "DefaultMarkup" an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param section the section to take the content section from
	 * @return the content section
	 * @throws IllegalArgumentException if the specified section is not of
	 *         {@link DefaultMarkupType}
	 */
	public static Section<? extends ContentType> getContentSection(Section<?> section) {
		if (!DefaultMarkupType.class.isAssignableFrom(section.getObjectType().getClass())) {
			throw new IllegalArgumentException("section not of type DefaultMarkupType");
		}
		return section.findChildOfType(ContentType.class);
	}

	/**
	 * Returns the content of the first annotation section of the specified
	 * name. If the section is not of type "DefaultMarkup" an
	 * IllegalArgumentException is thrown. If there is no annotation section
	 * with the specified name, null is returned.
	 * 
	 * @param section the section to be searched
	 * @param name the name of the annotation
	 * @return the content string of the annotation
	 * @throws IllegalArgumentException if the specified section is not of
	 *         {@link DefaultMarkupType}
	 */
	public static String getAnnotation(Section<?> section, String name) {
		Section<?> annotationSection = getAnnotationSection(section, name);
		if (annotationSection == null) return null;
		return annotationSection.getOriginalText();
	}

	/**
	 * Returns the first annotation section of the specified name. If the
	 * section is not of type "DefaultMarkup" an IllegalArgumentException is
	 * thrown. If there is no annotation section with the specified name, null
	 * is returned.
	 * 
	 * @param section the section to be searched
	 * @param name the name of the annotation
	 * @return the annotation section
	 * @throws IllegalArgumentException if the specified section is not of
	 *         {@link DefaultMarkupType}
	 */
	public static Section<? extends AnnotationType> getAnnotationSection(Section<?> section, String name) {
		if (!DefaultMarkupType.class.isAssignableFrom(section.getObjectType().getClass())) {
			throw new IllegalArgumentException("section not of type DefaultMarkupType");
		}
		List<Section<AnnotationType>> children = section.findChildrenOfType(AnnotationType.class);
		for (Section<AnnotationType> child : children) {
			String childName = child.getObjectType().getName();
			if (childName.equalsIgnoreCase(name)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Returns all annotations section of the specified name. If the section is
	 * not of type "DefaultMarkup" an IllegalArgumentException is thrown. If
	 * there is no annotation section with the specified name, an empty list is
	 * returned.
	 * 
	 * @param section the section to be searched
	 * @param name the name of the annotation
	 * @return the list of annotation sections
	 * @throws IllegalArgumentException if the specified section is not of
	 *         {@link DefaultMarkupType}
	 */
	public static List<Section<? extends AnnotationType>> getAnnotationSections(Section<?> section, String name) {
		if (!DefaultMarkupType.class.isAssignableFrom(section.getObjectType().getClass())) {
			throw new IllegalArgumentException("section not of type DefaultMarkupType");
		}
		List<Section<AnnotationType>> children = section.findChildrenOfType(AnnotationType.class);
		List<Section<? extends AnnotationType>> results = new ArrayList<Section<? extends AnnotationType>>();
		for (Section<AnnotationType> child : children) {
			String childName = child.getObjectType().getName();
			if (childName.equalsIgnoreCase(name)) {
				results.add(child);
			}
		}
		return results;
	}

	/**
	 * Returns the pattern to match a default mark-up section of a specified
	 * name.
	 * 
	 * @param name the name of the section ("%%&lt;name&gt;")
	 * @return the pattern to match the complete section
	 */
	public static Pattern getPattern(String name) {
		String regexp = SECTION_REGEXP.replace("$NAME$", name);
		return Pattern.compile(regexp, FLAGS);
	}

}

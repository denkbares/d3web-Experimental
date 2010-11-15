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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;

public class DefaultMarkup {

	public class Annotation {

		private final String name;
		private final boolean mandatory;
		private final Pattern pattern; // optional

		private final Collection<KnowWEObjectType> types = new LinkedList<KnowWEObjectType>();

		private Annotation(String name, boolean mandatory, Pattern pattern) {
			super();
			this.name = name;
			this.mandatory = mandatory;
			this.pattern = pattern;
		}

		/**
		 * Returns the name of the annotation. The name is the text after the
		 * &#64; that uniquely identify the annotation within a default mark-up.
		 * 
		 * @return the name of the annotation
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Returns whether the annotation is mandatory for the mark-up.
		 * 
		 * @return whether the annotation is mandatory
		 */
		public boolean isMandatory() {
			return this.mandatory;
		}

		/**
		 * Checks if the content of an annotation matches the annotations
		 * pattern.
		 * 
		 * @param annotationContent the content string to be checked
		 * @return whether the annotations pattern is matched
		 */
		public boolean matches(String annotationContent) {
			if (pattern == null) return true;
			if (annotationContent == null) return false;
			return pattern.matcher(annotationContent).matches();
		}

		/**
		 * Return all {@link KnowWEObjectType}s that may be accepted as the
		 * content text of the annotation. These types will be used to
		 * sectionize, parse and render the annotations content text, if there
		 * is no other renderer/parser defined in the parent's
		 * {@link DefaultMarkupType}.
		 * <p>
		 * The annotation may also contain any other text. It will be recognized
		 * as {@link PlainText}, such in any other section or wiki-page. It is
		 * in responsibility of the {@link SubtreeHandler} of the
		 * {@link DefaultMarkupType} instance to check for non-allowed content.
		 * 
		 * @return the KnowWEObjectTypes of this annotation
		 */
		public KnowWEObjectType[] getTypes() {
			return this.types.toArray(new KnowWEObjectType[this.types.size()]);
		}

		public Pattern getPattern() {
			return pattern;
		}
	}

	private final String name;
	private final Collection<KnowWEObjectType> types = new LinkedList<KnowWEObjectType>();
	private final Map<String, Annotation> annotations = new HashMap<String, Annotation>();

	public DefaultMarkup(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Adds a new annotation to the markup with.
	 * 
	 * @param name the name of the annotation to be added
	 * @param mandatory if the annotation is required for the markup
	 */
	public void addAnnotation(String name, boolean mandatory) {
		this.addAnnotation(name, mandatory, (Pattern) null);
	}

	/**
	 * Adds a new annotation to the markup with a fixed list of possible values
	 * (enumeration).
	 * 
	 * @param name the name of the annotation to be added
	 * @param mandatory if the annotation is required for the markup
	 * @param enumValues the allowed values for the annotation
	 */
	public void addAnnotation(String name, boolean mandatory,
			String... enumValues) {
		String regex = "^(" + de.d3web.we.utils.Strings.concat("|", enumValues)
				+ ")$";
		int flags = Pattern.CASE_INSENSITIVE;
		addAnnotation(name, mandatory, Pattern.compile(regex, flags));
	}

	/**
	 * Adds a new annotation to the markup with a fixed list of possible values
	 * (enumeration).
	 * 
	 * @param name the name of the annotation to be added
	 * @param mandatory if the annotation is required for the markup
	 * @param enumValues the allowed values for the annotation
	 */
	public void addAnnotation(String name, boolean mandatory,
			Enum<?>... enumValues) {
		String regex = "^(" + de.d3web.we.utils.Strings.concat("|", enumValues)
				+ ")$";
		int flags = Pattern.CASE_INSENSITIVE;
		addAnnotation(name, mandatory, Pattern.compile(regex, flags));
	}

	/**
	 * Adds a new annotation to the markup with a pattern to specify the values
	 * allowed for this annotation.
	 * 
	 * @param name the name of the annotation to be added
	 * @param mandatory if the annotation is required for the markup
	 * @param pattern a regular expression to check the allowed values
	 */
	public void addAnnotation(String name, boolean mandatory, Pattern pattern) {
		// do not allow duplicates
		String key = name.toLowerCase();
		if (annotations.containsKey(key)) {
			throw new IllegalArgumentException("annotation " + name
					+ " already added");
		}
		// add new parameter
		Annotation annotation = new Annotation(name, mandatory, pattern);
		this.annotations.put(key, annotation);
	}

	public Annotation getAnnotation(String name) {
		String key = name.toLowerCase();
		return this.annotations.get(key);
	}

	/**
	 * Returns an array of all annotations of a specific markup. If the markup
	 * has no annotations defined, an empty array is returned.
	 * 
	 * @return the annotations of the markup
	 */
	public Annotation[] getAnnotations() {
		return this.annotations.values().toArray(
				new Annotation[this.annotations.size()]);
	}

	public void addAnnotationType(String name, KnowWEObjectType type) {
		Annotation annotation = getAnnotation(name);
		if (annotation == null) {
			throw new IllegalArgumentException("no such annotation defined: "
					+ name);
		}
		annotation.types.add(type);
	}

	public void addContentType(KnowWEObjectType type) {
		this.types.add(type);
	}

	/**
	 * Return all {@link KnowWEObjectType}s that may be accepted as the content
	 * text of the mark-up. These types will be used to sectionize, parse and
	 * render the mark-up's content text, if there is no other renderer/parser
	 * defined in the parent's {@link DefaultMarkupType}.
	 * <p>
	 * The mark-up may also contain any other text. It will be recognized as
	 * {@link PlainText}, such in any other section or wiki-page. It is in
	 * responsibility of the {@link SubtreeHandler} of the
	 * {@link DefaultMarkupType} instance to check for non-allowed content.
	 * 
	 * @return the KnowWEObjectTypes of this mark-up
	 */
	public KnowWEObjectType[] getTypes() {
		return this.types.toArray(new KnowWEObjectType[this.types.size()]);
	}

}

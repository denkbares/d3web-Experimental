/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.interview;

import de.d3web.we.object.QuestionnaireReference;
import de.d3web.we.solutionpanel.ShowSolutionsType;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 07.08.2013
 */
public class WisskontSolutionPanel extends ShowSolutionsType {

	private static final String ANNOTATION_ABSTRACTIONS = "show_abstractions";
	private static final String ANNOTATION_SUGGESTED = "show_suggested";
	private static final String ANNOTATION_ESTABLISHED = "show_established";
	private static final String ANNOTATION_EXCLUDED = "show_excluded";
	private static final String ONLY_DERIVATIONS = "only_derivations";
	private static final String EXCEPT_DERIVATIONS = "except_derivations";
	private static final String SHOW_DIGITS = "show_digits";
	private static final String END_USER_MODE = "end_user_mode";

	public enum BoolValue {
		TRUE, FALSE
	};

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("WisskontSolutions");
		MARKUP.addAnnotation(PackageManager.MASTER_ATTRIBUTE_NAME, true);
		MARKUP.addAnnotation(ANNOTATION_ESTABLISHED, false, BoolValue.values());
		MARKUP.addAnnotation(ANNOTATION_SUGGESTED, false, BoolValue.values());
		MARKUP.addAnnotation(ANNOTATION_EXCLUDED, false, BoolValue.values());
		MARKUP.addAnnotation(ANNOTATION_ABSTRACTIONS, false, BoolValue.values());
		MARKUP.addAnnotation(ONLY_DERIVATIONS, false);
		MARKUP.addAnnotation(EXCEPT_DERIVATIONS, false);
		MARKUP.addAnnotation(SHOW_DIGITS, false);
		MARKUP.addAnnotation(END_USER_MODE, false, BoolValue.values());

		QuestionnaireReference qc = new QuestionnaireReference();
		qc.setSectionFinder(new AllTextFinderTrimmed());
		MARKUP.addAnnotationContentType(ONLY_DERIVATIONS, qc);
	}

	/**
	 * 
	 */
	public WisskontSolutionPanel() {
		super();
		this.clearCompileScripts();
	}
}

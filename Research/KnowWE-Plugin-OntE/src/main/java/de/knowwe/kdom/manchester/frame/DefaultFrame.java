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
package de.knowwe.kdom.manchester.frame;

import java.util.List;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.kdom.manchester.ManchesterSyntaxFrameRenderer;
import de.knowwe.kdom.manchester.types.MisspelledSyntaxConstruct;

/**
 * The {@link DefaultFrame} is the root class for all frames used in the
 * Manchester OWL syntax. If you want to add a new frame, please extend the
 * {@link DefaultFrame}.
 *
 * Sections which are unknown or cannot be matched by any children are labeled
 * as "unrecognized-frame-description".
 *
 * @author Stefan Mark
 * @created 23.09.2011
 */
public class DefaultFrame extends AbstractType {

	private static final String UNRECOGNIZED_TYPE = "UnrecognizedFrameDescription";

	/**
	 * Constructor for the {@link DefaultFrame}.
	 */
	public DefaultFrame() {
		this(UNRECOGNIZED_TYPE);
		this.setRenderer(new ManchesterSyntaxFrameRenderer());
	}

	/**
	 * Constructor for the {@link DefaultFrame}. Initializes the children types
	 * and some other stuff. Note. Adds an UnrecognizedTypeE
	 *
	 * @param typename
	 * @param messageText
	 */
	public DefaultFrame(String typename) {

		// check for misspelled identifiers ...
		this.addChildType(new MisspelledSyntaxConstruct());

		// ... or check for misplaced identifiers ...
		// this.addChildType(new MissplacedSyntaxConstruct());

		// anything left over that matches xx: is an unrecognized frame
		// description and throws an error
		// AnonymousType unrecognized = new AnonymousType(typename);
		// unrecognized.setSectionFinder(new AllTextFinderTrimmed());
		// unrecognized.addSubtreeHandler(new
		// GeneralSubtreeHandler<DefaultFrame>() {
		//
		// @Override
		// public Collection<KDOMReportMessage> create(KnowWEArticle article,
		// Section<DefaultFrame> s) {
		//
		// if (s.getOriginalText().startsWith("\u003A")) {
		// return new ArrayList<KDOMReportMessage>(0);
		// }
		//
		// String messageText =
		// "If this errors occurs together with a misspelled one, just ignore this one.";
		// return Arrays.asList((KDOMReportMessage) new UnexpectedSequence(
		// messageText + s.getOriginalText()));
		// }
		// });
		// this.addChildType(unrecognized);
	}

	public void setKnownDescriptions(List<Type> types) {
		this.childrenTypes.addAll(0, types);
	}
}


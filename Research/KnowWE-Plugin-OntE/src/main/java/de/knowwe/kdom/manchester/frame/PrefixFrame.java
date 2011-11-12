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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.ExactlyOneFindingConstraint;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.renderer.IRITypeRenderer;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionfinder.IRISectionFinder;

/**
 * <p>
 * Simple {@link AbstractType} for prefixes in the Manchester OWL syntax.
 * </p>
 *
 * @author Stefan Mark
 * @created 22.09.2011
 */
public class PrefixFrame extends DefaultFrame {

	public static final String KEYWORD = "Prefix[:]?";

	/**
	 * Constructor for the {@link PrefixFrame}. Adds the necessary
	 * {@link AbstractType}s needed for correct mapping in the KDOM of KnowWE.
	 */
	public PrefixFrame() {

		Pattern p = Pattern.compile(KEYWORD + ".*>");
		this.setSectionFinder(new RegexSectionFinder(p));

		List<Type> types = new ArrayList<Type>();

		types.add(new Keyword(KEYWORD));
		types.add(new Prefix());
		types.add(new PrefixIRI());
		this.setKnownDescriptions(types);
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 22.09.2011
 */
class Prefix extends AbstractType {

	public static final StyleRenderer PREFIX_RENDERER = new StyleRenderer("color:rgb(232,44,12)");

	public Prefix() {
		ConstraintSectionFinder csf = new ConstraintSectionFinder(new RegexSectionFinder(
				"([a-z0-9]*:)"));
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());

		this.setSectionFinder(csf);
		this.setCustomRenderer(PREFIX_RENDERER);
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 22.09.2011
 */
class PrefixIRI extends AbstractType {

	public PrefixIRI() {
		ConstraintSectionFinder csf = new ConstraintSectionFinder(new IRISectionFinder());
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());

		this.setSectionFinder(csf);
		this.setCustomRenderer(new IRITypeRenderer<PrefixIRI>());
	}
}
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
package de.knowwe.wisskont.rule;

import de.knowwe.compile.object.renderer.CompositeRenderer;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.termbrowser.DroppableTargetSurroundingRenderer;

/**
 * 
 * @author jochenreutelshofer
 * @created 08.08.2013
 */
public class RuleActionKeyType extends AbstractType {

	/**
	 * 
	 */
	public RuleActionKeyType() {
		CompositeRenderer renderer = new CompositeRenderer(new StyleRenderer("font-weight:bold;"),
				new DroppableTargetSurroundingRenderer());
		this.setRenderer(renderer);
		RegexSectionFinder finder = new RegexSectionFinder("(?i)DANN");
		ConstraintSectionFinder csf = new ConstraintSectionFinder(finder,
				AtMostOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);
	}
}

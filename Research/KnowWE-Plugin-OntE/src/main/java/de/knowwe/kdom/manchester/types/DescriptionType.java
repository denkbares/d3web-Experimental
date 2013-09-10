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
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;

/**
 * 
 * @author Jochen
 * @created 03.03.2012
 */
public abstract class DescriptionType extends AbstractType {


	public DescriptionType(String description, String keyword) {
		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(description, keyword);
		RegexSectionFinder regexFinder = new RegexSectionFinder(p, 1);
		ConstraintSectionFinder csf = new ConstraintSectionFinder(regexFinder);
		csf.addConstraint(SingleChildConstraint.getInstance());
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);

		Keyword key = new Keyword(keyword);
		this.addChildType(key);
	}
}

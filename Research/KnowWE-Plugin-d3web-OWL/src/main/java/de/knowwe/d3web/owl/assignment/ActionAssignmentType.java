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
package de.knowwe.d3web.owl.assignment;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.rules.action.RuleAction;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

/**
 *
 * @author Sebastian Furth
 * @created Mar 30, 2011
 */
public class ActionAssignmentType extends AbstractType implements AssignmentRegEx {

	private final String REGEX = "\\s*" + EXISTS + "\\s*=>\\s*.+";

	public ActionAssignmentType() {
		this.sectionFinder = new RegexSectionFinder(REGEX, Pattern.CASE_INSENSITIVE, 0);
		this.addChildType(new ComplexOWLClassType());
		this.addChildType(new QuantifierType());
		RuleAction action = new RuleAction();
		action.setSectionFinder(new RegexSectionFinder("=>\\s*(.+)", Pattern.CASE_INSENSITIVE, 1));
		this.addChildType(action);
		this.addChildType(new RightArrowType());
	}

}

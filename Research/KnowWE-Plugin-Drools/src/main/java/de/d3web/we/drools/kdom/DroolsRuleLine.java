/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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
package de.d3web.we.drools.kdom;

import java.util.regex.Pattern;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.utils.Patterns;

/**
 * A simple line in the rule body.
 * 
 * @author Alex Legler
 */
public class DroolsRuleLine extends DefaultAbstractKnowWEObjectType {

	public DroolsRuleLine() {
		setSectionFinder(new RegexSectionFinder("^(?<!//).*?(" + Patterns.LINEBREAK + "|\\z)", Pattern.MULTILINE));
		addChildType(new DroolsRuleVariable());
		addChildType(new DroolsRuleLiteral());
		addChildType(new DroolsRuleKeyword());
		addChildType(new PlainText());
	}
}

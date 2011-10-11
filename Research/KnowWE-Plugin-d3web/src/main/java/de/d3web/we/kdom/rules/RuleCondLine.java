/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.kdom.rules;

import java.util.regex.Pattern;

import de.d3web.we.kdom.kopic.renderer.RuleConditionHighlightingRenderer;
import de.d3web.we.kdom.rule.If;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.basicType.LineBreak;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

public class RuleCondLine extends AbstractType {

	@Override
	protected void init() {
		sectionFinder = new RegexSectionFinder("(IF|WENN).+", Pattern.DOTALL);
		setCustomRenderer(RuleConditionHighlightingRenderer.getInstance());
		childrenTypes.add(new LineBreak());
		childrenTypes.add(new If());
		childrenTypes.add(new RuleCondition());
	}

}

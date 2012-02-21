/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.jurisearch.tree;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.jurisearch.Error;
import de.knowwe.kdom.dashtree.DashTreeElementContent;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.EmbracedContentFinder;
import de.knowwe.kdom.sectionFinder.OneOfStringEnumFinder;

/**
 * 
 * @author boehler
 * @created 18.01.2012
 */
public class JuriTreeExpression extends DashTreeElementContent {

	public static final char BRACKET_OPEN = '(';
	public static final char BRACKET_CLOSE = ')';

	public JuriTreeExpression() {
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(new RoundBracketExp());
		this.addChildType(new QuestionIdentifier());
		this.addChildType(new Error());
		this.addSubtreeHandler(new JuriTreeHandler());
	}

	
	class RoundBracketExp extends AbstractType {
		RoundBracketExp() {
			this.setSectionFinder(new EmbracedContentFinder(BRACKET_OPEN, BRACKET_CLOSE));
			this.addChildType(new RoundExpBracketExpContent());
		}
	}

	
	
	class RoundExpBracketExpContent extends AbstractType {
		RoundExpBracketExpContent() {
			this.setSectionFinder(new EmbracedContentFinder(BRACKET_OPEN, BRACKET_CLOSE, true));
			this.addChildType(new Operator());
			this.addChildType(new Error());
		}
	}

	
	
	class Operator extends AbstractType {
		Operator() {
			this.sectionFinder = new OneOfStringEnumFinder(new String[] {
					"oder", "und", "score" });
			this.setRenderer(new StyleRenderer("font-weight:bold"));
		}
	}

}

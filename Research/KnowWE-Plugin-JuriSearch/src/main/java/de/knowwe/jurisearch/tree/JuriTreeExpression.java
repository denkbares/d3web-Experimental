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
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.jurisearch.BracketRenderer;
import de.knowwe.jurisearch.Error;
import de.knowwe.kdom.dashtree.DashTreeElementContent;

/**
 * 
 * @author boehler
 * @created 18.01.2012
 */
public class JuriTreeExpression extends DashTreeElementContent {

	public JuriTreeExpression() {
		this.setSectionFinder(new AllTextFinderTrimmed());

		this.setOrderSensitive(true);
		this.addChildType(new DummyExpression());
		this.addChildType(new Operator());
		this.addChildType(new AnswerBracket());

		this.addChildType(new QuestionIdentifier());

		this.addChildType(new Error());
		this.addSubtreeHandler(new JuriTreeHandler());
	}

	class AnswerBracket extends AbstractType {

		AnswerBracket() {
			SectionFinder sf = new
					RegexSectionFinder("\\[[^\\[\\]]+\\]");

			this.setSectionFinder(sf);
			this.addChildType(new AnswerIdentifier());
			this.setRenderer(new BracketRenderer());
		}
	}
}

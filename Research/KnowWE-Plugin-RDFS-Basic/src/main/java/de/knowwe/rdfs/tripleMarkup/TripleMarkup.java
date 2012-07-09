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
package de.knowwe.rdfs.tripleMarkup;

import java.util.regex.Pattern;

import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.kdom.basicType.EndLineComment;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.rdfs.rendering.PreEnvRenderer;

public class TripleMarkup extends AbstractKnowledgeUnitType<TripleMarkup> implements
		KnowledgeUnit {

	static final String TRIPLE_REGEX = "^>(.*?::.*?)$";

	public TripleMarkup() {
		this.setCompileScript(new TripleCompileScript());
		this.setSectionFinder(new RegexSectionFinder(TRIPLE_REGEX,
				Pattern.DOTALL | Pattern.MULTILINE, 0));

		this.addChildType(new EndLineComment());
		this.addChildType(new TripleMarkupContent());
		this.setRenderer(new PreEnvRenderer());
	}

}

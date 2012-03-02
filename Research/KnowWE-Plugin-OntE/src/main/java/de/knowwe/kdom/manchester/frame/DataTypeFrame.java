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

import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.compile.DatatypeCompileScript;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.EquivalentTo;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.termObject.DatatypePropertyIRIDefinition;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
public class DataTypeFrame extends DefaultFrame implements KnowledgeUnit<DataTypeFrame> {

	public static final String KEYWORD;
	public static final String KEYWORDS;

	static {
		KEYWORD = ManchesterSyntaxUtil.getFrameKeywordPattern(ManchesterSyntaxKeywords.DATATYPE);

		// add all children's keywords so they can be handled accordingly
		KEYWORDS = "("
				+ Annotations.KEYWORD + "|"
				+ EquivalentTo.KEYWORD
				+ "|\\z)";
	}

	public DataTypeFrame() {

		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 3));

		List<Type> types = new ArrayList<Type>();

		types.add(new DatatypeDefinition());
		types.add(new Annotations(KEYWORDS));

		EquivalentTo to = new EquivalentTo(KEYWORDS);
		to.addChildType(ManchesterSyntaxUtil.getDataRangeExpression());
		types.add(to);

		this.setKnownDescriptions(types);

	}

	/**
	 * Returns the {@link Datatype} section containing the name of the to define
	 * OWLDatatype.
	 *
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section
	 * @return The found section
	 */
	public boolean hasDefinition(Section<? extends DefaultFrame> section) {
		return Sections.findSuccessor(section, Datatype.class) != null;
	}

	/**
	 * Returns the {@link Datatype} section containing the name of the to define
	 * OWLDatatype.
	 *
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getDefinition(Section<? extends DefaultFrame> section) {
		return Sections.findSuccessor(section, Datatype.class);
	}

	/**
	 * Returns the {@link EquivalentTo} section
	 *
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section
	 * @return The found section
	 */
	public boolean hasEquivalentTo(Section<? extends DefaultFrame> section) {
		return Sections.findSuccessor(section, EquivalentTo.class) != null;
	}

	/**
	 * Returns the {@link EquivalentTo} section
	 *
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getEquivalentTo(Section<? extends DefaultFrame> section) {
		return Sections.findSuccessor(section, EquivalentTo.class);
	}

	@Override
	public KnowledgeUnitCompileScript<DataTypeFrame> getCompileScript() {
		return new DatatypeCompileScript();
	}
}

/**
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class DatatypeDefinition extends AbstractType {

	public static String PATTERN = DataTypeFrame.KEYWORD + "\\p{Blank}+(.+)";

	public DatatypeDefinition() {

		Pattern p = Pattern.compile(PATTERN);
		this.setSectionFinder(new RegexSectionFinder(p, 0));

		Keyword key = new Keyword(DataTypeFrame.KEYWORD);
		this.addChildType(key);

		Datatype type = new Datatype();
		this.addChildType(type);
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 06.06.2011
 */
class Datatype extends DatatypePropertyIRIDefinition {

	public Datatype() {
		this.setSectionFinder(new AllTextFinderTrimmed());
	}
}
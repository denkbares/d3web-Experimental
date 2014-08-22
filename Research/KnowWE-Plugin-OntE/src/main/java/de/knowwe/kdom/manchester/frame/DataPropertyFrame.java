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
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.compile.DataPropertyCompileScript;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.Characteristics;
import de.knowwe.kdom.manchester.types.DisjointWith;
import de.knowwe.kdom.manchester.types.Domain;
import de.knowwe.kdom.manchester.types.EquivalentTo;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.manchester.types.Range;
import de.knowwe.kdom.manchester.types.SubPropertyOf;
import de.knowwe.termObject.DatatypePropertyIRIDefinition;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 * 
 * 
 * @author Stefan Mark
 * @created 06.09.2011
 */

public class DataPropertyFrame extends DefaultFrame<DataPropertyFrame> implements KnowledgeUnit {

	public static final String KEYWORD = ManchesterSyntaxUtil.getFrameKeywordPattern(ManchesterSyntaxKeywords.DATA_PROPERTY);

	public static final String KEYWORDS = "("
			+ EquivalentTo.KEYWORD + "|"
			+ Annotations.KEYWORD + "|"
			+ SubPropertyOf.KEYWORD + "|"
			+ DisjointWith.KEYWORD + "|"
			+ Characteristics.KEYWORD + "|"
			+ Domain.KEYWORD + "|"
			+ Range.KEYWORD
			+ "|\\z)";

	public DataPropertyFrame() {

		super(ManchesterSyntaxKeywords.DATA_PROPERTY.getKeyword());
		this.setCompileScript(new DataPropertyCompileScript());
		List<Type> types = new ArrayList<Type>();

		types.add(DataPropertyDefinition.getInstance());
		types.add(new Annotations(KEYWORDS));

		EquivalentTo to = new EquivalentTo(KEYWORDS);
		to.addChildType(ManchesterSyntaxUtil.getMCE());
		types.add(to);

		SubPropertyOf sub = new SubPropertyOf(KEYWORDS);
		sub.addChildType(ManchesterSyntaxUtil.getMCE());
		types.add(sub);

		DisjointWith dis = new DisjointWith(KEYWORDS);
		dis.addChildType(ManchesterSyntaxUtil.getMCE());
		types.add(dis);

		types.add(new Characteristics(false, KEYWORDS));
		types.add(new Domain(KEYWORDS));

		Range r = new Range(KEYWORDS);
		r.addChildType(ManchesterSyntaxUtil.getDataRangeExpression());
		types.add(r);

		this.setKnownDescriptions(types);
	}

	/**
	 * Returns the {@link DataProperty} section containing the name of the to
	 * define OWLDataProperty.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasDataPropertyDefinition(Section<DataPropertyFrame> section) {
		return Sections.successor(section, DataProperty.class) != null;
	}

	/**
	 * Returns the {@link DataProperty} section containing the name of the to
	 * define OWLDataProperty.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getDataPropertyDefinition(Section<DataPropertyFrame> section) {
		return Sections.successor(section, DataProperty.class);
	}

	/**
	 * Returns if the current class definition has a {@link Domain} description.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasDomain(Section<DataPropertyFrame> section) {
		return Sections.successor(section, Domain.class) != null;
	}

	/**
	 * Returns the {@link Domain} sections of the current
	 * {@link ObjectPropertyFrame}.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public Section<Domain> getDomain(Section<DataPropertyFrame> section) {
		return Sections.successor(section, Domain.class);
	}

	/**
	 * Returns if the current class definition has a {@link Domain} description.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasRange(Section<DataPropertyFrame> section) {
		return Sections.successor(section, Range.class) != null;
	}

	/**
	 * Returns the {@link Domain} sections of the current
	 * {@link ObjectPropertyFrame}.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public Section<Range> getRange(Section<DataPropertyFrame> section) {
		return Sections.successor(section, Range.class);
	}

	/**
	 * Returns if the current class definition has a {@link SubPropertyOf}
	 * description.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasSubPropertyOf(Section<DataPropertyFrame> section) {
		return Sections.successor(section, SubPropertyOf.class) != null;
	}

	/**
	 * Returns the {@link SubPropertyOf} sections of the current
	 * {@link DataPropertyFrame}.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public Section<SubPropertyOf> getSubPropertyOf(Section<DataPropertyFrame> section) {
		return Sections.successor(section, SubPropertyOf.class);
	}

	/**
	 * Returns if the current class definition has a {@link EquivalentTo}
	 * description.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasEquivalentTo(Section<DataPropertyFrame> section) {
		return Sections.successor(section, EquivalentTo.class) != null;
	}

	/**
	 * Returns the {@link EquivalentTo} sections of the current
	 * {@link DataPropertyFrame}.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public Section<EquivalentTo> getEquivalentTo(Section<DataPropertyFrame> section) {
		return Sections.successor(section, EquivalentTo.class);
	}

	/**
	 * Returns if the current class definition has a {@link DisjointWith}
	 * description.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasDisjointWith(Section<DataPropertyFrame> section) {
		return Sections.successor(section, DisjointWith.class) != null;
	}

	/**
	 * Returns the {@link DisjointWith} sections of the current
	 * {@link DataPropertyFrame}.
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public Section<DisjointWith> getDisjointWith(Section<DataPropertyFrame> section) {
		return Sections.successor(section, DisjointWith.class);
	}

	/**
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasCharacteristics(Section<DataPropertyFrame> section) {
		return Sections.successor(section, Characteristics.class) != null;
	}

	/**
	 * 
	 * @created 27.09.2011
	 * @param Section<DataPropertyFrame> section
	 * @return The found section
	 */
	public Section<Characteristics> getCharacteristics(Section<DataPropertyFrame> section) {
		return Sections.successor(section, Characteristics.class);
	}

}

/**
 * 
 * @author Stefan Mark
 * @created 06.09.2011
 */
class DataPropertyDefinition extends AbstractType {

	public static String PATTERN = DataPropertyFrame.KEYWORD + "\\p{Blank}+(.+)";

	private static DataPropertyDefinition instance = null;

	public DataPropertyDefinition() {

		Pattern p = Pattern.compile(PATTERN);
		RegexSectionFinder finder = new RegexSectionFinder(p, 0);
		ConstraintSectionFinder csf = new ConstraintSectionFinder(finder);
		csf.addConstraint(SingleChildConstraint.getInstance());
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);

		Keyword key = new Keyword(DataPropertyFrame.KEYWORD);
		this.addChildType(key);

		DataProperty prop = new DataProperty();
		prop.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(prop);
	}

	public static synchronized DataPropertyDefinition getInstance() {
		if (instance == null) {
			instance = new DataPropertyDefinition();
		}
		return instance;
	}

}

/**
 * 
 * 
 * @author Stefan Mark
 * @created 06.09.2011
 */
class DataProperty extends DatatypePropertyIRIDefinition {

	public DataProperty() {

	}
}

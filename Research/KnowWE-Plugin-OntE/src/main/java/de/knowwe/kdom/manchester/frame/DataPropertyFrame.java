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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.Characteristics;
import de.knowwe.kdom.manchester.types.DisjointWith;
import de.knowwe.kdom.manchester.types.Domain;
import de.knowwe.kdom.manchester.types.EquivalentTo;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.manchester.types.Range;
import de.knowwe.kdom.manchester.types.SubPropertyOf;
import de.knowwe.termObject.DatatypePropertyIRIDefinition;

/**
 *
 *
 * @author Stefan Mark
 * @created 06.09.2011
 */

public class DataPropertyFrame extends DefaultFrame {

	public static final String KEYWORD = "DataProperty[:]?";

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

		// this.addSubtreeHandler(new ObjectPropertySubtreeHandler());

		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p));

		List<Type> types = new ArrayList<Type>();

		types.add(new DataPropertyDefinition());
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
		return Sections.findSuccessor(section, DataProperty.class) != null;
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
		return Sections.findSuccessor(section, DataProperty.class);
	}

}

/**
 *
 * @author Stefan Mark
 * @created 06.09.2011
 */
class DataPropertyDefinition extends AbstractType {

	public static String PATTERN = DataPropertyFrame.KEYWORD + "\\p{Blank}+(.+)";

	public DataPropertyDefinition() {

		Pattern p = Pattern.compile(PATTERN);
		SectionFinder sf = new RegexSectionFinder(p, 0);
		this.setSectionFinder(sf);

		Keyword key = new Keyword(DataPropertyFrame.KEYWORD);
		this.addChildType(key);

		DataProperty prop = new DataProperty();
		prop.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(prop);
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

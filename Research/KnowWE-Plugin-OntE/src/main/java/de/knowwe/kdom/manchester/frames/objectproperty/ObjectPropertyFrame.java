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
package de.knowwe.kdom.manchester.frames.objectproperty;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.frame.DefaultFrame;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.DisjointWith;
import de.knowwe.kdom.manchester.types.Domain;
import de.knowwe.kdom.manchester.types.EquivalentTo;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.manchester.types.Range;
import de.knowwe.kdom.manchester.types.SubPropertyOf;
import de.knowwe.kdom.subtreehandler.ObjectPropertySubtreeHandler;
import de.knowwe.termObject.ObjectPropertyIRIDefinition;

/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
public class ObjectPropertyFrame extends DefaultFrame {

	public static final String KEYWORD = "ObjectProperty[:]?";

	public static final String KEYWORDS = "("
			+ SubPropertyOf.KEYWORD + "|"
			+ Characteristics.KEYWORD + "|"
			+ Range.KEYWORD + "|"
			+ Domain.KEYWORD + "|"
			+ InverseOf.KEYWORD
			+ "|\\z)";

	public ObjectPropertyFrame() {

		this.addSubtreeHandler(new ObjectPropertySubtreeHandler());

		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p));

		ObjectPropertyDefinition dt = new ObjectPropertyDefinition();
		this.addChildType(dt);

		this.addChildType(new Annotations(KEYWORDS));

		SubPropertyOf sub = new SubPropertyOf(KEYWORDS);
		sub.addChildType(ManchesterSyntaxUtil.getMCE());
		this.addChildType(sub);

		EquivalentTo to = new EquivalentTo(KEYWORDS);
		to.addChildType(ManchesterSyntaxUtil.getMCE());
		this.addChildType(to);

		DisjointWith dis = new DisjointWith(KEYWORDS);
		dis.addChildType(ManchesterSyntaxUtil.getMCE());
		this.addChildType(dis);

		this.addChildType(new Characteristics(true, KEYWORDS));
		this.addChildType(new Domain(KEYWORDS));

		Range r = new Range(KEYWORDS);
		r.addChildType(ManchesterSyntaxUtil.getMCE());
		this.addChildType(r);

		this.addChildType(new InverseOf());
	}

	/**
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasCharacteristics(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, Characteristics.class) != null;
	}

	/**
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public Section<Characteristics> getCharacteristics(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, Characteristics.class);
	}

	/**
	 * Returns the {@link ObjectProperty} section containing the name of the to
	 * define OWLObjectProperty.
	 *
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section
	 * @return The found section
	 */
	public boolean hasObjectPropertyDefinition(Section<? extends DefaultFrame> section) {
		return Sections.findSuccessor(section, ObjectProperty.class) != null;
	}

	/**
	 * Returns the {@link ObjectProperty} section containing the name of the to
	 * define OWLObjectProperty.
	 *
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getObjectPropertyDefinition(Section<? extends DefaultFrame> section) {
		return Sections.findSuccessor(section, ObjectProperty.class);
	}

	/**
	 * Returns if the current class definition has a {@link Domain} description.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasDomain(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, Domain.class) != null;
	}

	/**
	 * Returns the {@link Domain} sections of the current
	 * {@link ObjectPropertyFrame}.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public Section<Domain> getDomain(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, Domain.class);
	}

	/**
	 * Returns if the current class definition has a {@link Domain} description.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasRange(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, Range.class) != null;
	}

	/**
	 * Returns the {@link Domain} sections of the current
	 * {@link ObjectPropertyFrame}.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public Section<Range> getRange(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, Range.class);
	}

	/**
	 * Returns if the current class definition has a {@link InverseOf}
	 * description.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasInverseOf(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, InverseOf.class) != null;
	}

	/**
	 * Returns the {@link InverseOf} sections of the current
	 * {@link ObjectPropertyFrame}.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public Section<InverseOf> getInverseOf(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, InverseOf.class);
	}

	/**
	 * Returns if the current class definition has a {@link SubPropertyOf}
	 * description.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasSubPropertyOf(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, SubPropertyOf.class) != null;
	}

	/**
	 * Returns the {@link SubPropertyOf} sections of the current
	 * {@link ObjectPropertyFrame}.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public Section<SubPropertyOf> getSubPropertyOf(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, SubPropertyOf.class);
	}

	/**
	 * Returns if the current class definition has a {@link EquivalentTo}
	 * description.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasEquivalentTo(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, EquivalentTo.class) != null;
	}

	/**
	 * Returns the {@link EquivalentTo} sections of the current
	 * {@link ObjectPropertyFrame}.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public Section<EquivalentTo> getEquivalentTo(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, EquivalentTo.class);
	}

	/**
	 * Returns if the current class definition has a {@link DisjointWith}
	 * description.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasDisjointWith(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, DisjointWith.class) != null;
	}

	/**
	 * Returns the {@link DisjointWith} sections of the current
	 * {@link ObjectPropertyFrame}.
	 *
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public Section<DisjointWith> getDisjointWith(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, DisjointWith.class);
	}
}

/**
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class ObjectPropertyDefinition extends AbstractType {

	public static String PATTERN = ObjectPropertyFrame.KEYWORD + "\\p{Blank}+(.+)";

	public ObjectPropertyDefinition() {

		Pattern p = Pattern.compile(PATTERN);
		SectionFinder sf = new RegexSectionFinder(p, 0);
		this.setSectionFinder(sf);

		Keyword key = new Keyword(ObjectPropertyFrame.KEYWORD);
		this.addChildType(key);

		ObjectProperty owl = new ObjectProperty();
		owl.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(owl);
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 06.06.2011
 */
class ObjectProperty extends ObjectPropertyIRIDefinition {

	public ObjectProperty() {

	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class InverseOf extends AbstractType {

	public static final String KEYWORD = "InverseOf[:]?";

	public InverseOf() {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(ObjectPropertyFrame.KEYWORDS,
				KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);

		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}
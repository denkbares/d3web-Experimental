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
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.compile.ObjectPropertyCompileScript;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.Characteristics;
import de.knowwe.kdom.manchester.types.DescriptionType;
import de.knowwe.kdom.manchester.types.DisjointWith;
import de.knowwe.kdom.manchester.types.Domain;
import de.knowwe.kdom.manchester.types.EquivalentTo;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;
import de.knowwe.kdom.manchester.types.Range;
import de.knowwe.kdom.manchester.types.SubPropertyOf;
import de.knowwe.termObject.ObjectPropertyIRIDefinition;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 * Simple {@link AbstractType} for an Object Property in the OWL 2
 * Specification. (Object properties connect pairs of individuals.)
 * 
 * @author Stefan Mark
 * @created 24.05.2011
 */
public class ObjectPropertyFrame extends DefaultFrame implements KnowledgeUnit {

	public static final String KEYWORD;
	public static final String KEYWORDS;

	static {
		KEYWORD = ManchesterSyntaxUtil.getFrameKeywordPattern(ManchesterSyntaxKeywords.OBJECT_PROPERTY);

		// add all children's keywords so they can be handled accordingly
		KEYWORDS = "("
				+ Annotations.KEYWORD + "|"
				+ SubPropertyOf.KEYWORD + "|"
				+ SubPropertyChain.KEYWORD + "|"
				+ EquivalentTo.KEYWORD + "|"
				+ DisjointWith.KEYWORD + "|"
				+ Characteristics.KEYWORD + "|"
				+ Range.KEYWORD + "|"
				+ Domain.KEYWORD + "|"
				+ InverseOf.KEYWORD
				+ "|\\z)";
	}

	/**
	 * Constructor. Add here all possible children of the
	 * {@link ObjectPropertyFrame}.
	 */
	public ObjectPropertyFrame() {

		super(ManchesterSyntaxKeywords.OBJECT_PROPERTY.getKeyword());

		List<Type> types = new ArrayList<Type>();

		types.add(ObjectPropertyDefinition.getInstance());
		types.add(new Annotations(KEYWORDS));

		SubPropertyOf sub = new SubPropertyOf(KEYWORDS);
		sub.addChildType(ManchesterSyntaxUtil.getMCE());
		types.add(sub);

		EquivalentTo to = new EquivalentTo(KEYWORDS);
		to.addChildType(ManchesterSyntaxUtil.getMCE());
		types.add(to);

		DisjointWith dis = new DisjointWith(KEYWORDS);
		dis.addChildType(ManchesterSyntaxUtil.getMCE());
		types.add(dis);

		types.add(new Characteristics(true, KEYWORDS));
		types.add(new Domain(KEYWORDS));

		Range r = new Range(KEYWORDS);
		r.addChildType(ManchesterSyntaxUtil.getMCE());
		types.add(r);

		types.add(new InverseOf());
		types.add(new SubPropertyChain());

		this.setKnownDescriptions(types);
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

	/**
	 * Returns if the current class definition has a {@link SubPropertyChain}
	 * description.
	 * 
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public boolean hasSubPropertyChain(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, SubPropertyChain.class) != null;
	}

	/**
	 * Returns the {@link SubPropertyChain} sections of the current
	 * {@link ObjectPropertyFrame}.
	 * 
	 * @created 27.09.2011
	 * @param Section<ObjectPropertyFrame> section
	 * @return The found section
	 */
	public Section<SubPropertyChain> getSubPropertyChain(Section<ObjectPropertyFrame> section) {
		return Sections.findSuccessor(section, SubPropertyChain.class);
	}

	@Override
	public KnowledgeUnitCompileScript<ObjectPropertyFrame> getCompileScript() {
		return new ObjectPropertyCompileScript();
	}
}

/**
 * 
 * @author Stefan Mark
 * @created 24.05.2011
 */
class ObjectPropertyDefinition extends AbstractType {

	// public static String PATTERN = ObjectPropertyFrame.KEYWORD +
	// "\\p{Blank}+(.+)";

	private static ObjectPropertyDefinition instance = null;

	private ObjectPropertyDefinition() {

		Pattern p = Pattern.compile(ObjectPropertyFrame.KEYWORD
				+ ManchesterSyntaxUtil.getTillKeywordPattern(ObjectPropertyFrame.KEYWORDS),
				Pattern.DOTALL);

		// there can be only one
		RegexSectionFinder finder = new RegexSectionFinder(p, 0);
		ConstraintSectionFinder csf = new ConstraintSectionFinder(finder);
		csf.addConstraint(SingleChildConstraint.getInstance());
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);

		Keyword key = new Keyword(ObjectPropertyFrame.KEYWORD);
		this.addChildType(key);

		ObjectProperty owl = new ObjectProperty();
		owl.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(owl);
	}

	public static synchronized ObjectPropertyDefinition getInstance() {
		if (instance == null) {
			instance = new ObjectPropertyDefinition();
		}
		return instance;
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
class InverseOf extends DescriptionType {

	public static final String KEYWORD = ManchesterSyntaxUtil.getFrameKeywordPattern(ManchesterSyntaxKeywords.INVERSE_OF);

	public InverseOf() {

		super(ObjectPropertyFrame.KEYWORDS,
				KEYWORD);
		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}

/**
 * 
 * 
 * @author Stefan Mark
 * @created 25.10.2011
 */
class SubPropertyChain extends DescriptionType {

	public static final String KEYWORD = ManchesterSyntaxUtil.getFrameKeywordPattern(ManchesterSyntaxKeywords.SUB_PROPERTY_CHAIN);

	public SubPropertyChain() {
		super(ObjectPropertyFrame.KEYWORDS,
				KEYWORD);

		// objectPropertyExpression 'o' objectPropertyExpression { 'o'
		// objectPropertyExpression }

		this.addChildType(new Keyword("\\s+o\\s+"));
		this.addChildType(new ObjectPropertyExpression());
	}
}

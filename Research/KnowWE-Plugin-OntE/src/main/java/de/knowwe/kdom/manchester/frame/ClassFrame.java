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

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.subtreehandler.ClassFrameSubtreeHandler;
import de.knowwe.termObject.ClassIRIDefinition;

/**
 * An {@link AbstractType} for the Manchester OWL syntax ClassFrame. Specifies
 * the allowed children and some helper methods used in the
 * {@link SubtreeHandler}.
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
public class ClassFrame extends DefaultFrame {

	public static final String KEYWORD = "Class[:]?";

	// add all children's keywords so they can be handled accordingly
	public static final String KEYWORDS = "("
			+ Annotations.KEYWORD + "|"
			+ DisjointWith.KEYWORD + "|"
			+ DisjointUnionOf.KEYWORD + "|"
			+ SubClassOf.KEYWORD + "|"
			+ EquivalentTo.KEYWORD
			+ "|\\z)";

	public ClassFrame() {

		this.addSubtreeHandler(new ClassFrameSubtreeHandler());

		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p));

		OWLClassDefinition c = new OWLClassDefinition();
		this.addChildType(c);

		Annotations a = new Annotations(KEYWORDS);
		this.addChildType(a);

		EquivalentTo to = new EquivalentTo();
		this.addChildType(to);

		SubClassOf sc = new SubClassOf();
		this.addChildType(sc);

		DisjointWith dis = new DisjointWith();
		this.addChildType(dis);

		DisjointUnionOf disUnion = new DisjointUnionOf();
		this.addChildType(disUnion);
	}

	/**
	 * Returns the {@link OWLClass} section containing the name of the to define
	 * OWLClass.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasClassDefinition(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, OWLClass.class) != null;
	}

	/**
	 * Returns the {@link OWLClass} section containing the name of the to define
	 * OWLClass.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getClassDefinition(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, OWLClass.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasSubClassOf(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, SubClassOf.class) != null;
	}

	/**
	 * Returns the {@link SubClassOf} section containing a SubClassOf
	 * description for the current class.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getSubClassOf(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, SubClassOf.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasEquivalentTo(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, EquivalentTo.class) != null;
	}

	/**
	 * Returns the {@link SubClassOf} section containing a SubClassOf
	 * description for the current class.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getEquivalentTo(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, EquivalentTo.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasDisjointWith(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, DisjointWith.class) != null;
	}

	/**
	 * Returns the {@link SubClassOf} section containing a SubClassOf
	 * description for the current class.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getDisjointWith(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, DisjointWith.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasDisjointUnionOf(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, DisjointUnionOf.class) != null;
	}

	/**
	 * Returns the {@link SubClassOf} section containing a SubClassOf
	 * description for the current class.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getDisjointUnionOf(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, DisjointUnionOf.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasAnnotations(Section<ClassFrame> section) {
		return Sections.findSuccessor(section, Annotations.class) != null;
	}

	/**
	 * Returns the {@link SubClassOf} section containing a SubClassOf
	 * description for the current class.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public List<Section<Annotation>> getAnnotations(Section<ClassFrame> section) {
		Section<Annotations> a = Sections.findSuccessor(section, Annotations.class);
		if (a != null) {
			return Sections.findSuccessorsOfType(a, Annotation.class);
		}
		return new ArrayList<Section<Annotation>>();
	}
}

/**
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class OWLClassDefinition extends AbstractType {

	public static String PATTERN = ClassFrame.KEYWORD + "\\p{Blank}+(.+)";

	public OWLClassDefinition() {

		Pattern p = Pattern.compile(PATTERN);
		SectionFinder sf = new RegexSectionFinder(p, 0);
		this.setSectionFinder(sf);

		Keyword key = new Keyword(ClassFrame.KEYWORD);
		this.addChildType(key);

		OWLClass owl = new OWLClass();
		owl.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(owl);
	}
}

/**
 * An {@link AbstractType} for the name of an OWLClass.
 *
 * @author Stefan Mark
 * @created 06.06.2011
 */
class OWLClass extends ClassIRIDefinition {

	public OWLClass() {

	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class SubClassOf extends AbstractType {

	public static final String KEYWORD = "SubClassOf[:]?";

	public SubClassOf() {
		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(ClassFrame.KEYWORDS, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		this.addChildType(new Keyword(SubClassOf.KEYWORD));
		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 28.09.2011
 */
class EquivalentTo extends AbstractType {

	public static final String KEYWORD = "EquivalentTo[:]?";

	public EquivalentTo() {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(ClassFrame.KEYWORDS, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		this.addChildType(new Keyword(KEYWORD));
		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class DisjointWith extends AbstractType {

	public static final String KEYWORD = "DisjointWith[:]?";

	public DisjointWith() {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(ClassFrame.KEYWORDS, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		this.addChildType(new Keyword(KEYWORD));
		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class DisjointUnionOf extends AbstractType {

	public static final String KEYWORD = "DisjointUnionOf[:]?";

	public DisjointUnionOf() {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(ClassFrame.KEYWORDS, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		this.addChildType(new Keyword(KEYWORD));
		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}
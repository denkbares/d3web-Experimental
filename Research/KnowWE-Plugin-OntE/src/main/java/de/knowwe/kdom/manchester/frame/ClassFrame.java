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
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.compile.ClassFrameCompileScript;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.EquivalentTo;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.termObject.ClassIRIDefinition;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 * An {@link AbstractType} for the Manchester OWL syntax ClassFrame. Specifies
 * the allowed children and some helper methods used in the
 * {@link SubtreeHandler}.
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
public class ClassFrame extends DefaultFrame implements KnowledgeUnit<ClassFrame> {

	public static final String KEYWORD = ManchesterSyntaxUtil.getFrameKeywordPattern(ManchesterSyntaxKeywords.CLASS);

	// add all children's keywords so they can be handled accordingly
	public static final String KEYWORDS = "("
			+ Annotations.KEYWORD + "|"
			+ DisjointWith.KEYWORD + "|"
			+ DisjointUnionOf.KEYWORD + "|"
			+ SubClassOf.KEYWORD + "|"
			+ EquivalentTo.KEYWORD
			+ "|\\z)";

	public ClassFrame() {

		// this.addSubtreeHandler(new ClassFrameSubtreeHandler());

		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 3));

		List<Type> types = new ArrayList<Type>();

		types.add(OWLClassDefinition.getInstance());
		types.add(new Annotations(KEYWORDS));

		EquivalentTo to = new EquivalentTo(ClassFrame.KEYWORDS);
		to.addChildType(ManchesterSyntaxUtil.getMCE());
		types.add(to);

		types.add(new SubClassOf());
		types.add(new DisjointWith());
		types.add(new DisjointUnionOf());

		this.setKnownDescriptions(types);
	}

	/**
	 * Returns the {@link OWLClass} section containing the name of the to define
	 * OWLClass.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasClassDefinition(Section<? extends ClassFrame> section) {
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
	public Section<? extends Type> getClassDefinition(Section<? extends ClassFrame> section) {
		return Sections.findSuccessor(section, OWLClass.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasSubClassOf(Section<?> section) {
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
	public Section<? extends Type> getSubClassOf(Section<?> section) {
		return Sections.findSuccessor(section, SubClassOf.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasEquivalentTo(Section<?> section) {
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
	public Section<? extends Type> getEquivalentTo(Section<?> section) {
		return Sections.findSuccessor(section, EquivalentTo.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasDisjointWith(Section<?> section) {
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
	public Section<? extends Type> getDisjointWith(Section<?> section) {
		return Sections.findSuccessor(section, DisjointWith.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasDisjointUnionOf(Section<?> section) {
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
	public Section<? extends Type> getDisjointUnionOf(Section<?> section) {
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

	@Override
	public KnowledgeUnitCompileScript<ClassFrame> getCompileScript() {
		return new ClassFrameCompileScript();
	}
}

/**
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class OWLClassDefinition extends AbstractType {

	public static String PATTERN = ClassFrame.KEYWORD + "\\p{Blank}+(.+)";

	private static OWLClassDefinition instance = null;

	private OWLClassDefinition() {

		Pattern p = Pattern.compile(PATTERN);
		SectionFinder sf = new RegexSectionFinder(p, 0);
		this.setSectionFinder(sf);

		Keyword key = new Keyword(ClassFrame.KEYWORD);
		this.addChildType(key);

		OWLClass owl = new OWLClass();
		owl.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(owl);
	}

	public static synchronized OWLClassDefinition getInstance() {
		if (instance == null) {
			instance = new OWLClassDefinition();
		}
		return instance;
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
class SubClassOf extends DefaultDescription {

	public static final String KEYWORD = ManchesterSyntaxUtil.getFrameKeywordPattern(ManchesterSyntaxKeywords.SUBCLASS_OF);

	public SubClassOf() {
		super(ClassFrame.KEYWORDS, KEYWORD);
		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}
/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class DisjointWith extends DefaultDescription {

	public static final String KEYWORD = ManchesterSyntaxUtil.getFrameKeywordPattern(ManchesterSyntaxKeywords.DISJOINT_WITH);

	public DisjointWith() {
		super(ClassFrame.KEYWORDS, KEYWORD);
		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
class DisjointUnionOf extends DefaultDescription {

	public static final String KEYWORD = ManchesterSyntaxUtil.getFrameKeywordPattern(ManchesterSyntaxKeywords.DISJOINT_UNION_OF);

	public DisjointUnionOf() {

		super(ClassFrame.KEYWORDS, KEYWORD);
		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}
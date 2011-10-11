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

import org.semanticweb.owlapi.model.OWLIndividual;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.Fact;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.kdom.subtreehandler.IndividualFrameSubtreeHandler;
import de.knowwe.termObject.NamedIndividualIRIDefinition;

/**
 * <p>
 * Simple {@link AbstractType} for the {@link IndividualFrame} Manchester OWL
 * syntax frame. It follows an excerpt from the Manchester OWL syntax definition
 * from the W3C.
 * </p>
 *
 *<code>
 * individualFrame ::= 'Individual:' individual<br />
 * { 'Annotations:' annotationAnnotatedList<br />
 * | 'Types:' descriptionAnnotatedList<br />
 * | 'Facts:' factAnnotatedList<br />
 * | 'SameAs:' individualAnnotatedList<br />
 * | 'DifferentFrom:' individualAnnotatedList }
 *</code>
 *
 * @author Stefan Mark
 * @created 24.06.2011
 */
public class IndividualFrame extends DefaultFrame {

	public static final String KEYWORD = "Individual[:]?";

	public static final String KEYWORDS = "("
			+ Types.KEYWORD + "|"
			+ SameAs.KEYWORD + "|"
			+ DifferentFrom.KEYWORD + "|"
			+ Facts.KEYWORD + "|"
			+ Annotations.KEYWORD
			+ "|\\z)";

	public IndividualFrame() {

		this.addSubtreeHandler(new IndividualFrameSubtreeHandler());

		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p));

		this.addChildType(new IndividualDefinition());
		this.addChildType(new Types());
		this.addChildType(new SameAs());
		this.addChildType(new DifferentFrom());
		this.addChildType(new Facts());
		this.addChildType(new Annotations(KEYWORDS));
	}

	/**
	 * Checks if the current {@link IndividualFrame} has a {@link Individual}
	 * section.
	 *
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section
	 * @return TRUE if such an section exists, FALSE otherwise
	 */
	public boolean hasIndividualDefinition(Section<? extends DefaultFrame> section) {
		return !Sections.findSuccessor(section, Individual.class).isEmpty();
	}

	/**
	 * Returns the {@link Individual} section containing the name of the to
	 * define {@link OWLIndividual}.
	 *
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getIndividualDefinition(Section<? extends DefaultFrame> section) {
		return Sections.findSuccessor(section, Individual.class);
	}

	/**
	 * Returns if the current class definition has a {@link Facts} description.
	 *
	 * @created 27.09.2011
	 * @param Section<IndividualFrame> section
	 * @return The found section
	 */
	public boolean hasFacts(Section<IndividualFrame> section) {
		return Sections.findChildOfType(section, Facts.class) != null;
	}
	/**
	 * Returns the {@link FactsItem} sections of the current
	 * {@link IndividualFrame}.
	 *
	 * @created 27.09.2011
	 * @param Section<IndividualFrame> section
	 * @return The found section
	 */
	public List<Section<? extends Type>> getFacts(Section<IndividualFrame> section) {
		List<Section<? extends Type>> nodes = new ArrayList<Section<? extends Type>>();
		List<Section<Fact>> items = Sections.findSuccessorsOfType(section, Fact.class);
		nodes.addAll(items);
		return nodes;
	}

	/**
	 * Returns if the current class definition has a {@link Facts} description.
	 *
	 * @created 27.09.2011
	 * @param Section<IndividualFrame> section
	 * @return The found section
	 */
	public boolean hasSameAs(Section<IndividualFrame> section) {
		return Sections.findChildOfType(section, SameAs.class) != null;
	}

	/**
	 * Returns the {@link SameAs} sections of the current
	 * {@link IndividualFrame}.
	 *
	 * @created 27.09.2011
	 * @param Section<IndividualFrame> section
	 * @return The found section
	 */
	public List<Section<OWLTermReferenceManchester>> getSameAs(Section<IndividualFrame> section) {
		Section<SameAs> sameAs = Sections.findChildOfType(section, SameAs.class);
		if (sameAs != null) {
			return Sections.findSuccessorsOfType(sameAs, OWLTermReferenceManchester.class);
		}
		return new ArrayList<Section<OWLTermReferenceManchester>>();
	}

	/**
	 * Returns if the current class definition has a {@link DifferentFrom}
	 * description.
	 *
	 * @created 27.09.2011
	 * @param Section<IndividualFrame> section
	 * @return The found section
	 */
	public boolean hasDifferentFrom(Section<IndividualFrame> section) {
		return Sections.findChildOfType(section, DifferentFrom.class) != null;
	}

	/**
	 * Returns the {@link DifferentFrom} sections of the current
	 * {@link IndividualFrame}.
	 *
	 * @created 27.09.2011
	 * @param Section<IndividualFrame> section
	 * @return The found section
	 */
	public List<Section<OWLTermReferenceManchester>> getDifferentFrom(Section<IndividualFrame> section) {
		Section<DifferentFrom> sameAs = Sections.findChildOfType(section, DifferentFrom.class);
		if (sameAs != null) {
			return Sections.findSuccessorsOfType(sameAs, OWLTermReferenceManchester.class);
		}
		return new ArrayList<Section<OWLTermReferenceManchester>>();
	}

	/**
	 * Returns if the current class definition has a {@link Types} description.
	 *
	 * @created 27.09.2011
	 * @param Section<IndividualFrame> section
	 * @return The found section
	 */
	public boolean hasTypes(Section<IndividualFrame> section) {
		return Sections.findChildOfType(section, Types.class) != null;
	}

	/**
	 * Returns the {@link Types} sections of the current {@link IndividualFrame}
	 * .
	 *
	 * @created 27.09.2011
	 * @param Section<IndividualFrame> section
	 * @return The found section
	 */
	public Section<Types> getTypes(Section<IndividualFrame> section) {
		return Sections.findChildOfType(section, Types.class);
	}

	/**
	 * Returns if the current class definition has a SubClassOf description.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public boolean hasAnnotations(Section<IndividualFrame> section) {
		return Sections.findChildOfType(section, Annotations.class) != null;
	}

	/**
	 * Returns the {@link SubClassOf} section containing a SubClassOf
	 * description for the current class.
	 *
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	public List<Section<Annotation>> getAnnotations(Section<IndividualFrame> section) {
		Section<Annotations> a = Sections.findChildOfType(section, Annotations.class);
		if (a != null) {
			Sections.findSuccessorsOfType(a, Annotation.class);
		}
		return new ArrayList<Section<Annotation>>();
	}
}
/**
 *
 * @author Stefan Mark
 * @created 24.06.2011
 */
class IndividualDefinition extends AbstractType {

	public static String PATTERN = IndividualFrame.KEYWORD + "\\p{Blank}+(.+)";

	public IndividualDefinition() {

		Pattern p = Pattern.compile(PATTERN);
		SectionFinder sf = new RegexSectionFinder(p, 0);
		this.setSectionFinder(sf);

		Keyword key = new Keyword(IndividualFrame.KEYWORD);
		this.addChildType(key);

		Individual individual = new Individual();
		individual.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(individual);
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 24.06.2011
 */
class Individual extends NamedIndividualIRIDefinition {

	public Individual() {

	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 28.09.2011
 */
class SameAs extends AbstractType {

	public static final String KEYWORD = "SameAs[:]?";

	public SameAs() {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(IndividualFrame.KEYWORDS, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);

		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 24.06.2011
 */
class Types extends AbstractType {

	public static final String KEYWORD = "Types[:]?";

	public Types() {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(IndividualFrame.KEYWORDS, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);
		this.addChildType(new Annotations());

		// List<Type> types = new ArrayList<Type>();
		// types.add(ManchesterSyntaxUtil.getMCE());

		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 28.09.2011
 */
class DifferentFrom extends AbstractType {

	public static final String KEYWORD = "DifferentFrom[:]?";

	public DifferentFrom() {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(IndividualFrame.KEYWORDS, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);

		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 28.09.2011
 */
class Facts extends AbstractType {

	public static final String KEYWORD = "Facts[:]?";

	public Facts() {

		Pattern p = ManchesterSyntaxUtil.getDescriptionPattern(IndividualFrame.KEYWORDS, KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p, 1));

		Keyword key = new Keyword(KEYWORD);
		this.addChildType(key);

		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}
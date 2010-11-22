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

package de.d3web.we.kdom.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.condition.helper.BracedCondition;
import de.d3web.we.kdom.condition.helper.BracedConditionContent;
import de.d3web.we.kdom.condition.helper.CompCondLineEndComment;
import de.d3web.we.kdom.condition.helper.ConjunctSectionFinder;
import de.d3web.we.kdom.constraint.AtMostOneFindingConstraint;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.ISectionFinder;
import de.d3web.we.kdom.sectionFinder.OneOfStringEnumFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.kdom.type.AnonymousType;
import de.d3web.we.utils.SplitUtility;

/**
 * This class defines a KDOM-Schema to parse composite conditions as known from
 * proposition logics, using 'AND', 'OR', 'NOT' as keywords and brackets '(' and
 * ')' for to express association boundaries
 * 
 * 
 * @author Jochen
 * 
 */
public class CompositeCondition extends DefaultAbstractKnowWEObjectType {

	private final TerminalCondition terminalCondition = new TerminalCondition();

	public static char BRACE_OPEN = '(';
	public static char BRACE_CLOSED = ')';

	public CompositeCondition() {

		// this composite takes everything it gets => needs suitable wrapper
		// type as father
		this.sectionFinder = new AllTextFinderTrimmed();
		// this.setCustomRenderer(new
		// de.d3web.we.kdom.renderer.KDOMDepthFontSizeRenderer());

		// a composite condition may either be a BracedCondition,...
		BracedCondition braced = new BracedCondition(); // contains the brackets
		// and the
		// endline-comments
		this.childrenTypes.add(braced);
		BracedConditionContent bracedContent = new BracedConditionContent(); // without
		// brackets
		// and
		// comments
		braced.addChildType(bracedContent);
		braced.addChildType(new CompCondLineEndComment()); // explicit nodes for
		// the
		// endline-comments
		bracedContent.addChildType(this);

		// ... a negated expression,...
		NegatedExpression negatedExpression = new NegatedExpression();
		this.childrenTypes.add(negatedExpression);
		negatedExpression.addChildType(this); // a NegatedExpression again
		// allows for a
		// CompositeCondition

		// ...a conjuctive expression,...
		Conjunct conj = new Conjunct();
		this.addChildType(conj);
		conj.addChildType(this); // Conjuncts again allow for a
		// CompositeCondition

		// ... a disjuctive expression,...
		Disjunct disj = new Disjunct();
		this.addChildType(disj);
		disj.addChildType(this); // Disjuncts again allow for a
		// CompositeCondition

		// ... or finally a TerminalCondition which stops the recursive descent
		this.addChildType(terminalCondition);
	}

	/**
	 * Sets the set of terminalConditions for this CompositeCondition
	 * 
	 * Any terminal that is not accepted by one of these will be marked by an
	 * UnrecognizedTerminalCondition causing an error
	 * 
	 * @param types
	 */
	public void setAllowedTerminalConditions(List<KnowWEObjectType> types) {
		terminalCondition.setAllowedTerminalConditions(types);
	}

	/**
	 * tells whether a CompositeCondition is a disjunction
	 * 
	 * @param c
	 * @return
	 */
	public boolean isDisjunction(Section<CompositeCondition> c) {
		return getDisjuncts(c).size() > 0;
	}

	/**
	 * returns the disjuncts of a disjunction
	 * 
	 * @param c
	 * @return
	 */
	public List<Section<? extends NonTerminalCondition>> getDisjuncts(Section<CompositeCondition> c) {

		List<Section<? extends NonTerminalCondition>> result = new ArrayList<Section<? extends NonTerminalCondition>>();
		List<Section<Disjunct>> childrenOfType = c.findChildrenOfType(Disjunct.class);

		result.addAll(childrenOfType);
		return result;
	}

	/**
	 * tells whether a CompositeCondition is a Conjunction
	 * 
	 * @param c
	 * @return
	 */
	public boolean isConjunction(Section<CompositeCondition> c) {
		return getConjuncts(c).size() > 0;
	}

	/**
	 * returns the conjunts of a conjunction
	 * 
	 * @param c
	 * @return
	 */
	public List<Section<? extends NonTerminalCondition>> getConjuncts(Section<CompositeCondition> c) {

		List<Section<? extends NonTerminalCondition>> result = new ArrayList<Section<? extends NonTerminalCondition>>();
		List<Section<Conjunct>> childrenOfType = c.findChildrenOfType(Conjunct.class);

		result.addAll(childrenOfType);
		return result;
	}

	/**
	 * tells whether a CompositeCondition is a bracedexpression
	 * 
	 * @param c
	 * @return
	 */
	public boolean isBraced(Section<CompositeCondition> c) {
		return getBraced(c) != null;
	}

	/**
	 * returns the BracedCondition
	 * 
	 * @param c
	 * @return
	 */
	public Section<? extends NonTerminalCondition> getBraced(Section<CompositeCondition> c) {

		Section<? extends BracedCondition> childrenOfType = c.findChildOfType(BracedCondition.class);
		return childrenOfType;
	}

	/**
	 * tells whether a CompositeCondition is a NegatedExpression
	 * 
	 * @param c
	 * @return
	 */
	public boolean isNegation(Section<CompositeCondition> c) {
		return getNegation(c) != null;
	}

	/**
	 * returns the NegatedExpression of a Negation
	 * 
	 * @param c
	 * @return
	 */
	public Section<? extends NonTerminalCondition> getNegation(Section<CompositeCondition> c) {

		List<Section<? extends NonTerminalCondition>> result = new ArrayList<Section<? extends NonTerminalCondition>>();
		Section<? extends NonTerminalCondition> negEx = c.findChildOfType(NegatedExpression.class);

		return negEx;
	}

	/**
	 * tells whether this CompositeCondition is a TerminalCondition
	 * 
	 * @param c
	 * @return
	 */
	public boolean isTerminal(Section<CompositeCondition> c) {
		return getTerminal(c) != null;
	}

	/**
	 * returns the TerminalCondition of a (terminal-)CompositeCondition
	 * 
	 * @param c
	 * @return
	 */
	public Section<? extends TerminalCondition> getTerminal(Section<CompositeCondition> c) {

		List<Section<? extends TerminalCondition>> result = new ArrayList<Section<? extends TerminalCondition>>();
		Section<? extends TerminalCondition> terminal = c.findChildOfType(TerminalCondition.class);

		return terminal;
	}

	/**
	 * 
	 * @created 03.08.2010
	 * @param trimmed
	 * @return
	 */
	public static boolean hasLineBreakAfterComment(String text) {
		int start = SplitUtility.lastIndexOfUnquoted(text, "//");
		if (start != -1) {
			Pattern pattern = Pattern.compile("\\r?\\n");
			Matcher matcher = pattern.matcher(text);
			int lineBreak = -1;
			while (matcher.find()) {
				// attempts to find the last line break
				lineBreak = matcher.start();
			}
			if (lineBreak > start) {
				return true;
			}
		}
		return false;
	}

}

/**
 * @author Jochen
 * 
 *         Type for a disjunct element in the CompositeCondition
 * 
 *         example: 'a OR b' here 'a' and 'b' are nodes of type disjunct
 * 
 */
class Disjunct extends NonTerminalCondition implements de.d3web.we.kdom.ExclusiveType {

	@Override
	protected void init() {

		this.sectionFinder = ConjunctSectionFinder.createConjunctFinder(new String[] {
				"OR", "ODER", "|" });
	}
}

/**
 * @author Jochen
 * 
 *         Type for a conjunct element in the CompositeCondition
 * 
 *         example: 'a AND b' here 'a' and 'b' are nodes of type conjunct
 * 
 */
class Conjunct extends NonTerminalCondition implements de.d3web.we.kdom.ExclusiveType {

	static String[] CONJ_SIGNS = {
			"AND", "UND", "&" };

	@Override
	protected void init() {
		this.setSectionFinder(ConjunctSectionFinder.createConjunctFinder(CONJ_SIGNS));
	}

}

/**
 * @author Jochen
 * 
 *         Type for a negated element in the CompositeCondition
 * 
 *         example: 'NOT b' here 'b' is not nodes of type NegatedExpression
 * 
 */
class NegatedExpression extends NonTerminalCondition {

	static String[] NEG_SIGNS = {
			"NOT", "NICHT", "!" };

	@Override
	protected void init() {
		AnonymousType negationSign = new AnonymousType("NegationSign");
		ConstraintSectionFinder finder = new ConstraintSectionFinder(
				new OneOfStringEnumFinder(NEG_SIGNS),
				AtMostOneFindingConstraint.getInstance());
		negationSign.setSectionFinder(finder);
		this.addChildType(negationSign);

		this.sectionFinder = new ISectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section father, KnowWEObjectType type) {
				String trimmed = text.trim();
				for (String sign : NEG_SIGNS) {
					if (trimmed.startsWith(sign)) {
						return new AllTextFinderTrimmed().lookForSections(text,
								father, type);
					}
				}
				return null;
			}
		};
	}

}











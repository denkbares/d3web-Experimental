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
package de.knowwe.kdom.manchester;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.TerminalCondition;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.kdom.manchester.types.ListItem;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.kdom.manchester.types.OneOfBracedCondition;
import de.knowwe.kdom.manchester.types.OneOfBracedConditionContent;
import de.knowwe.kdom.manchester.types.Restriction;

/**
 * Allows different markups to use the manchester syntax expressions so one can
 * use with or without the default markup, own markup etc.
 *
 * @author Jochen, smark
 * @created 24.05.2011
 */
public class ManchesterClassExpression extends CompositeCondition {

	/**
	 * Initializes the {@link TerminalCondition}s.
	 *
	 * @created 21.09.2011
	 */
	public void initRestrictionTypes() {

		// include oneof on the same level as the other brackets
		OneOfBracedCondition oneOf = new OneOfBracedCondition();
		OneOfBracedConditionContent oneOfContent = new OneOfBracedConditionContent();
		oneOfContent.addChildType(this);
		oneOf.addChildType(oneOfContent);
		this.childrenTypes.add(0, oneOf);

		// include list to the children
		ListItem list = new ListItem();
		list.addChildType(this);
		this.childrenTypes.add(getAllowedChildrenTypes().size() - 1, list);

		// set terminal conditions (only restrictions are terminal!)
		List<Type> types = new ArrayList<Type>();
		types.add(Restriction.getInstance());
		this.setAllowedTerminalConditions(types);
	}

	/**
	 * Check whether the current {@link ManchesterClassExpression} has a
	 * {@link OneOfBracedCondition} section as child.
	 *
	 * @param Section<ManchesterClassExpression> a A
	 *        {@link ManchesterClassExpression} section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean hasOneOf(Section<ManchesterClassExpression> section) {
		if (Sections.findSuccessor(section, OneOfBracedCondition.class) != null) {
			return true;
		}
		return false;
	}
	/**
	 * Retrieves each fragment of the OneOfList and the returns a list for
	 * further handling.
	 *
	 * @param Section<ManchesterClassExpression> a A
	 *        {@link ManchesterClassExpression} section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public List<Section<OWLTermReferenceManchester>> getOneOfs(Section<ManchesterClassExpression> section) {
		return Sections.findSuccessorsOfType(section, OWLTermReferenceManchester.class);
	}

	/**
	 * Bundle the content within the Default Markup in a separate content type.
	 *
	 * @author smark
	 * @created 18.05.2011
	 */
	public static class OWLClassContentType extends AbstractType {

		private static OWLClassContentType instance = null;

		private static ManchesterClassExpression cc = new ManchesterClassExpression();

		protected OWLClassContentType() {
			this.setSectionFinder(new AllTextFinderTrimmed());
			cc.initRestrictionTypes();
			this.addChildType(cc);

		}

		public static synchronized OWLClassContentType getInstance() {
			if (instance == null) {
				instance = new OWLClassContentType();
			}
			return instance;
		}

		/**
		 * Returns the CompositeCondition since it is possible to nest OWL
		 * descriptions.
		 *
		 * @created 24.05.2011
		 * @return {@link CompositeCondition}
		 */
		public static CompositeCondition getCompositeCondition() {
			return cc;
		}
	}
}

package de.knowwe.kdom.manchester.types;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.TerminalCondition;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;

/**
 *
 *
 * @author Stefan Mark
 * @created 18.09.2011
 */
public class DataRangeExpression extends CompositeCondition {

	/**
	 * Initializes the {@link TerminalCondition}s.
	 *
	 * @created 18.09.2011
	 */
	public void initRestrictionTypes() {

		// get count of the CompositeConditions children
		int ccChildren = getAllowedChildrenTypes().size();

		// add new NonTerminalChildren ...
		// ... like a NonTerminalList ...^
		// ... caused error with facets
		// NonTerminalList list = new NonTerminalList();
		// NonTerminalListContent listContent = new NonTerminalListContent();
		// listContent.addChildType(this);
		// list.addChildType(listContent);
		// this.childrenTypes.add(ccChildren - 1, list);

		// ... or a OneOfBracedList
		OneOfBracedCondition oneOf = new OneOfBracedCondition();
		OneOfBracedConditionContent oneOfContent = new OneOfBracedConditionContent();
		oneOfContent.addChildType(this);
		oneOf.addChildType(oneOfContent);
		this.childrenTypes.add(ccChildren - 1, oneOf);

		// ... or finally a TerminalCondition which stops the recursive descent
		// DataType, literal list, dataTyperestriction datarange
		List<Type> types = new ArrayList<Type>();
		types.add(DatatypeRestriction.getInstance());
		this.setAllowedTerminalConditions(types);
	}

	/**
	 * Check whether the current {@link DataRangeExpression} has a
	 * {@link OneOfBracedCondition} section as child.
	 *
	 * @param Section<DataRangeExpression> a A {@link DataRangeExpression}
	 *        section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isOneOfCurlyBracket(Section<DataRangeExpression> section) {
		return Sections.findChildOfType(section, OneOfBracedCondition.class) != null;
	}

	/**
	 * Retrieves each fragment of the OneOfList and the returns a list for
	 * further handling.
	 *
	 * @param Section<DataRangeExpression> a A {@link DataRangeExpression}
	 *        section
	 * @return The found {@link OWLTermReferenceManchester} sections
	 */
	public Section<OneOfBracedCondition> getOneOfCurlyBracket(Section<DataRangeExpression> section) {
		return Sections.findChildOfType(section, OneOfBracedCondition.class);
	}

	/**
	 * Check whether the current {@link DataRangeExpression} has a
	 * {@link OneOfBracedCondition} section as child.
	 *
	 * @param Section<DataRangeExpression> a A {@link DataRangeExpression}
	 *        section
	 * @return TRUE if found, FALSE otherwise
	 */
	public boolean isNonTerminalList(Section<DataRangeExpression> section) {
		return Sections.findChildOfType(section, NonTerminalList.class) != null;
	}

	/**
	 * Retrieves each fragment of the OneOfList and the returns a list for
	 * further handling.
	 *
	 * @param Section<DataRangeExpression> a A {@link DataRangeExpression}
	 *        section
	 * @return The found {@link NonTerminalListContent} sections
	 */
	public List<Section<NonTerminalList>> getNonTerminalListElements(Section<DataRangeExpression> section) {
		return Sections.findChildrenOfType(section, NonTerminalList.class);
	}

	/**
	 * Wrap the content of the DataRangeExpression to make sure, that the
	 * correct text snippet is further processed.
	 *
	 * @author Stefan Mark
	 * @created 18.09.2011
	 */
	public static class DataRangeContentType extends AbstractType {

		private static DataRangeContentType instance = null;

		private static DataRangeExpression cc;

		static {
			cc = new DataRangeExpression();
			cc.initRestrictionTypes();
		}

		protected DataRangeContentType() {
			this.setSectionFinder(new AllTextFinderTrimmed());
			cc.initRestrictionTypes();
			this.addChildType(cc);

		}

		public static synchronized DataRangeContentType getInstance() {
			if (instance == null) {
				instance = new DataRangeContentType();
			}
			return instance;
		}

		/**
		 * Returns the {@link DataRangeExpression} since its possible to nest
		 * the expressions
		 *
		 * @created 18.09.2011
		 * @return {@link CompositeCondition}
		 */
		public CompositeCondition getCompositeCondition() {
			return cc;
		}
	}
}

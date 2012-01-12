package de.knowwe.kdom.manchester.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;


/**
 *
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
public class DatatypeRestriction extends AbstractType {

	private static DatatypeRestriction instance = null;

	public DatatypeRestriction() {

		this.setSectionFinder(new AllTextFinderTrimmed());

		this.addChildType(new PredefinedOWLDatatype());

		BraceElement brace = new BraceElement('\u005B', '\u005D'); // '[' | ']'
		BraceElementContent braceContent = new BraceElementContent('\u005B', '\u005D');

		NonTerminalList list = new NonTerminalList();
		NonTerminalListContent listContent = new NonTerminalListContent();
		listContent.addChildType(new Delimiter());
		listContent.addChildType(new FacetRestriction());
		list.addChildType(listContent);

		braceContent.addChildType(list);
		braceContent.addChildType(new FacetRestriction());
		brace.addChildType(braceContent);
		this.addChildType(brace);

		this.addChildType(new Delimiter());

		this.addChildType(new FacetRestriction());
		this.addChildType(new Literal());
	}

	public static synchronized DatatypeRestriction getInstance() {
		if (instance == null) {
			instance = new DatatypeRestriction();
		}
		return instance;
	}

	public boolean isPredefinedDataType(Section<DatatypeRestriction> section) {
		return Sections.findSuccessor(section, PredefinedOWLDatatype.class) != null;
	}

	// integer|int|double|boolean|float|decimal|string

	public boolean isIntegerDataType(Section<DatatypeRestriction> section) {
		Section<?> predef = getPredefinedDataType(section);
		if (predef.getOriginalText().equals("integer") || predef.getOriginalText().equals("int")) {
			return true;
		}
		return false;
	}

	public boolean isDoubleDataType(Section<DatatypeRestriction> section) {
		Section<?> predef = getPredefinedDataType(section);
		if (predef.getOriginalText().equals("double")) {
			return true;
		}
		return false;
	}

	public boolean isBooleanDataType(Section<DatatypeRestriction> section) {
		Section<?> predef = getPredefinedDataType(section);
		if (predef.getOriginalText().equals("boolean")) {
			return true;
		}
		return false;
	}

	public boolean isFloatDataType(Section<DatatypeRestriction> section) {
		Section<?> predef = getPredefinedDataType(section);
		if (predef.getOriginalText().equals("float")) {
			return true;
		}
		return false;
	}

	public boolean isDecimalDataType(Section<DatatypeRestriction> section) {
		Section<?> predef = getPredefinedDataType(section);
		if (predef.getOriginalText().equals("decimal")) {
			return true;
		}
		return false;
	}

	public boolean isStringDataType(Section<DatatypeRestriction> section) {
		Section<?> predef = getPredefinedDataType(section);
		if (predef.getOriginalText().equals("string")) {
			return true;
		}
		return false;
	}

	/**
	 * Retrieves a {@link PredefinedOWLDatatype} section
	 *
	 * @param Section<Restriction> a A {@link DatatypeRestriction} section
	 * @return The found {@link PredefinedOWLDatatype} sections
	 */
	public Section<?> getPredefinedDataType(Section<DatatypeRestriction> section) {
		return Sections.findChildOfType(section, PredefinedOWLDatatype.class);
	}

	public boolean isLiteral(Section<DatatypeRestriction> section) {
		return Sections.findSuccessor(section, Literal.class) != null;
	}

	/**
	 * Retrieves a {@link Literal} section
	 *
	 * @param Section<Restriction> a A {@link DatatypeRestriction} section
	 * @return The found {@link Literal} sections
	 */
	public Section<Literal> getLiteral(Section<DatatypeRestriction> section) {
		return Sections.findChildOfType(section, Literal.class);
	}

	public boolean isFacet(Section<DatatypeRestriction> section) {
		return Sections.findSuccessor(section, FacetRestriction.class) != null;
	}

	public Map<Section<? extends AbstractType>, Section<Literal>> getFacets(Section<DatatypeRestriction> section) {
		List<Section<FacetRestriction>> restrictions = Sections.findSuccessorsOfType(section,
				FacetRestriction.class);

		Map<Section<? extends AbstractType>, Section<Literal>> facets = new HashMap<Section<? extends AbstractType>, Section<Literal>>();

		for (Section<FacetRestriction> restriction : restrictions) {
			FacetRestriction type = restriction.get();
			if (type.hasFacet(restriction)) {
				facets.put(type.getFacet(restriction), type.getLiteral(restriction));
			}
		}
		if (!facets.isEmpty()) {
			return facets;
		}
		return Collections.emptyMap();
	}
}
/**
 *
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
class PredefinedOWLDatatype extends AbstractType {

	public static final String PATTERN = "integer|int|double|boolean|float|decimal|string";

	public PredefinedOWLDatatype() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
		this.setCustomRenderer(new ToolMenuDecoratingRenderer<PredefinedOWLDatatype>(
				new StyleRenderer("font-weight:bold;font-color:black")));
	}
}
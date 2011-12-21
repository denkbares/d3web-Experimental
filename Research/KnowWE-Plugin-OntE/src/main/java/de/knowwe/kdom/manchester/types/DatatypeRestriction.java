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
		listContent.addChildType(new FacetRestriction());
		list.addChildType(listContent);

		braceContent.addChildType(list);
		braceContent.addChildType(new FacetRestriction());
		brace.addChildType(braceContent);
		this.addChildType(brace);
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

	public boolean isLiteral(Section<DatatypeRestriction> section) {
		return Sections.findSuccessor(section, Literal.class) != null;
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

class FacetRestriction extends AbstractType {

	public FacetRestriction() {
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(new Facet());
		this.addChildType(new Literal());
	}

	public boolean hasLiteral(Section<FacetRestriction> section) {
		return Sections.findSuccessor(section, Literal.class) != null;
	}

	public Section<Literal> getLiteral(Section<FacetRestriction> section) {
		return Sections.findSuccessor(section, Literal.class);
	}

	public boolean hasFacet(Section<FacetRestriction> section) {
		return Sections.findSuccessor(section, Facet.class) != null;
	}

	public Section<? extends AbstractType> getFacet(Section<FacetRestriction> section) {
		return Sections.findSuccessor(section, Facet.class);
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
class Facet extends AbstractType {

	public static final String PATTERN = "length|minLength|maxLength|pattern|langRange|<=|<|>=|>";

	public Facet() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
	}
}

/**
 *
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
class PredefinedOWLDatatype extends AbstractType {

	public static final String PATTERN = "integer|int|double|boolean|float|decimal";

	public PredefinedOWLDatatype() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
		this.setCustomRenderer(new ToolMenuDecoratingRenderer<PredefinedOWLDatatype>(
				new StyleRenderer("font-weight:bold;font-color:black")));
	}
}
package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;


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

		// DataType = datatypeIRI | 'integer' | 'decimal' | 'float' | 'string'
		// facet restrictionValue -> literal
		// facet = length, minlength, maxLength, pattern,langRange <= < >= >
		this.addChildType(new Facet());
		this.addChildType(new Literal());

		// Datatype
		// { literalList }
		// dataTypeRestriction -> DataType [ facet restrictionValue ] ....
		// ---> facet = length, minlength, maxLength, pattern,langRange <= < >=
		// >
		// ( dataRange )


	}

	public static synchronized DatatypeRestriction getInstance() {
		if (instance == null) {
			instance = new DatatypeRestriction();
		}
		return instance;
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
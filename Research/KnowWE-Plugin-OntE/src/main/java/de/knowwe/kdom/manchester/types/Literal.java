package de.knowwe.kdom.manchester.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

/**
 * Simple {@link AbstractType} for {@link Literal}s in the Manchester OWL
 * syntax.
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
public class Literal extends AbstractType{

	public Literal() {
		this.setSectionFinder(new AllTextFinderTrimmed());

		// typed literals
		this.addChildType(new TypedLiteral());

		// string literals
		this.addChildType(new StringLiteral());
		this.addChildType(new StringLiteralLanguage());

		// number literals
		this.addChildType(new FloatingPointLiteral());
		this.addChildType(new DecimalLiteral());
		this.addChildType(new IntegerLiteral());
	}
}

/**
 * Simple {@link AbstractType} for a FloatingPoint in a {@link Literal} in the
 * Manchester OWL syntax.
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
class FloatingPointLiteral extends AbstractType {

	//floatingPointLiteral ::= [ '+' | '-'] ( digits ['.'digits] [exponent] | '.' digits[exponent]) ( 'f' | 'F' )
	// exponent ::= ('e' | 'E') ['+' | '-'] digits
	public static final String EXPONENT = "(e|E)[+-]\\d*";
	public static final String PATTERN = "[+-](\\d+[\\.\\d*]\\s+[" + EXPONENT + "]|\\.\\d*["
			+ EXPONENT + "])(f|F)";

	public FloatingPointLiteral() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
	}
}

/**
 * Simple {@link AbstractType} for a Decimal in a {@link Literal} in the
 * Manchester OWL syntax.
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
class DecimalLiteral extends AbstractType {

	// decimalLiteral ::= ['+' | '-'] digits '.' digits
	public static final String PATTERN = "[+-]\\d+\\.\\d+";

	public DecimalLiteral() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
	}
}

/**
 * Simple {@link AbstractType} for a Integer in a {@link Literal} in the
 * Manchester OWL syntax.
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
class IntegerLiteral extends AbstractType {

	// integerLiteral ::= ['+' | '-'] digits
	public static final String PATTERN = "[+-]\\d+";

	public IntegerLiteral() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
	}
}

/**
 * Simple {@link AbstractType} for a string in the Manchester OWL Syntax in a
 * {@link Literal}.
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
class StringLiteral extends AbstractType {

	public StringLiteral() {
		this.setSectionFinder(new RegexSectionFinder("\".*\""));
	}
}

/**
 * Simple {@link AbstractType} for the language information in a {@link Literal}
 * in the Manchester OWl syntax. A language tag is a @ (U+40) followed a
 * nonempty sequence of characters matching the langtag production from
 * http://www.rfc-editor.org/rfc/bcp/bcp47.txt.
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
class StringLiteralLanguage extends AbstractType {

	public static final String PATTERN = "@([a-z]{2})";

	public StringLiteralLanguage() {
		Pattern p = Pattern.compile(PATTERN);
		this.setSectionFinder(new RegexSectionFinder(p, 1));
	}
}

/**
 * Simple {@link AbstractType} for a TypedLiteral in a {@link Literal} in the
 * Manchester OWL syntax.
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
class TypedLiteral extends AbstractType {

	public TypedLiteral() {

		this.setSectionFinder(new RegexSectionFinder(
				"\".*\"^^([A-Za-z0-9]+|integer|decimal|float|string)"));
		this.addChildType(new StringLiteral());
	}
}
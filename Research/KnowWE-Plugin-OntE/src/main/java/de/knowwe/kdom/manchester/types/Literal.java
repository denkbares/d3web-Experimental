package de.knowwe.kdom.manchester.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

/**
 * Simple {@link AbstractType} for {@link Literal}s in the Manchester OWL
 * syntax.
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
public class Literal extends AbstractType {

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

	public boolean isFloatingPoint(Section<Literal> section) {
		return Sections.findSuccessor(section, FloatingPointLiteral.class) != null;
	}

	public boolean isInteger(Section<Literal> section) {
		return Sections.findSuccessor(section, IntegerLiteral.class) != null;
	}

	public boolean isDecimal(Section<Literal> section) {
		return Sections.findSuccessor(section, DecimalLiteral.class) != null;
	}

	public boolean isString(Section<Literal> section) {
		return Sections.findSuccessor(section, StringLiteral.class) != null;
	}

	public boolean isLanguage(Section<Literal> section) {
		return Sections.findSuccessor(section, StringLiteralLanguage.class) != null;
	}

	public boolean isTyped(Section<Literal> section) {
		return Sections.findSuccessor(section, TypedLiteral.class) != null;
	}

	public Section<FloatingPointLiteral> getFloatingPoint(Section<Restriction> section) {
		return Sections.findChildOfType(section, FloatingPointLiteral.class);
	}

	public Section<IntegerLiteral> getInteger(Section<Literal> section) {
		return Sections.findSuccessor(section, IntegerLiteral.class);
	}

	public Section<DecimalLiteral> getDecimal(Section<Literal> section) {
		return Sections.findSuccessor(section, DecimalLiteral.class);
	}

	public Section<StringLiteral> getString(Section<Literal> section) {
		return Sections.findSuccessor(section, StringLiteral.class);
	}

	public Section<StringLiteralLanguage> getLanguage(Section<Literal> section) {
		return Sections.findSuccessor(section, StringLiteralLanguage.class);
	}

	public Section<TypedLiteral> getTyped(Section<Literal> section) {
		return Sections.findSuccessor(section, TypedLiteral.class);
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
	public static final String EXPONENT = "(e|E)[+-]?\\d*";
	public static final String PATTERN = "[+-]?(\\d+[\\.\\d*]\\s+[" + EXPONENT + "]|\\.\\d*["
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
	public static final String PATTERN = "[+-]?\\d+\\.\\d+";

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
	public static final String PATTERN = "[+-]?\\d+";

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
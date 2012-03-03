package de.knowwe.kdom.manchester.types;


/**
 *
 *
 * @author Stefan Mark
 * @created 28.09.2011
 */
public class SubPropertyOf extends DescriptionType {

	public static final String KEYWORD = "SubPropertyOf[:]?";

	public SubPropertyOf(String description) {
		super(description, KEYWORD);
	}
}

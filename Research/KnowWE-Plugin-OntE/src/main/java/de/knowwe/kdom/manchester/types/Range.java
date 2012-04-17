package de.knowwe.kdom.manchester.types;

/**
 * 
 * 
 * @author Stefan Mark
 * @created 24.05.2011
 */
public class Range extends DescriptionType {

	public static final String KEYWORD = "\\sRange[:]?";

	public Range(String description) {
		super(description, KEYWORD);

	}
}

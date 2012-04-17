package de.knowwe.kdom.manchester.types;

import de.knowwe.kdom.manchester.frame.DefaultDescription;

/**
 * 
 * 
 * @author Stefan Mark
 * @created 28.09.2011
 */
public class EquivalentTo extends DefaultDescription {

	public static final String KEYWORD = "\\sEquivalentTo[:]?";

	public EquivalentTo(String description) {
		super(description, KEYWORD);
	}
}

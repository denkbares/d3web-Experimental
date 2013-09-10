package de.knowwe.kdom.manchester.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

/**
 *
 *
 * @author Stefan Mark
 * @created 30.11.2011
 */
public class HiddenComment extends AbstractType {


	public static final String OPEN_TAG = "<!--";
	public static final String CLOSE_TAG = "-->";

	public HiddenComment() {
		this.setSectionFinder(new RegexSectionFinder(OPEN_TAG + ".+" + CLOSE_TAG,
				Pattern.MULTILINE | Pattern.DOTALL));
	}
}

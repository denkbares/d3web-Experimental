package de.knowwe.kdom.manchester.types;

import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;

/**
 *
 *
 * @author Stefan Mark
 * @created 05.10.2011
 */
public class Domain extends DescriptionType {

	public static final String KEYWORD = "Domain[:]?";

	public Domain(String description) {
		super(description, KEYWORD);

		this.addChildType(ManchesterSyntaxUtil.getMCE());
	}
}

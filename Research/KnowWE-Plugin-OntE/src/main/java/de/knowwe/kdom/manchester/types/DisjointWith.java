package de.knowwe.kdom.manchester.types;


/**
 *
 *
 * @author Stefan Mark
 * @created 24.05.2011
 */
public class DisjointWith extends DescriptionType {

	public static final String KEYWORD = "DisjointWith[:]?";

	public DisjointWith(String description) {
		super(description, KEYWORD);

		if (description == null || description.isEmpty()) {
			throw new IllegalArgumentException(
					"Empty keywords are not allowed for the DisjointWith description!");
		}


	}
}

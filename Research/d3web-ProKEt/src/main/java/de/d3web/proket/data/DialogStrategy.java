package de.d3web.proket.data;

/**
 * Definition of different dialog strategies. Are mapped in the specification
 * XMLs and by d3web-FormStrategies
 * 
 * @author Martina Freiberg
 * @created 13.10.2010
 */
public enum DialogStrategy {

	/* Always get the next not-answered form */
	NEXTFORM,

	/* Get only the next unanswered question next */
	NEXTQUESTION,

	/* Default value */
	DEFAULT;
}

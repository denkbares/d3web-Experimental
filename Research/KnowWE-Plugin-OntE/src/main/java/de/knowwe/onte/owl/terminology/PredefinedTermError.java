package de.knowwe.onte.owl.terminology;

import de.d3web.we.kdom.report.KDOMError;

public class PredefinedTermError extends KDOMError {

	private final String s;

	public PredefinedTermError(String s) {
		this.s = s;
	}

	@Override
	public String getVerbalization() {
		return "This term is already predefined: " + s;
	}

}

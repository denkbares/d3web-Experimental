package de.knowwe.caseTrain;

import de.d3web.we.kdom.report.KDOMError;

public class InvalidArgumentError extends KDOMError {

	private final String text;

	public InvalidArgumentError(String t) {
		this.text = t;
	}

	@Override
	public String getVerbalization() {
		return "Invalid Argument: " + text;
	}
}

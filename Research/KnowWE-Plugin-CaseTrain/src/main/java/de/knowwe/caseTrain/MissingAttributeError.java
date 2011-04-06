package de.knowwe.caseTrain;

import de.d3web.we.kdom.report.KDOMError;

public class MissingAttributeError extends KDOMError {

	private final String text;

	public MissingAttributeError(String t) {
		this.text = t;
	}

	@Override
	public String getVerbalization() {
		return "Fehlendes Attribut: " + text;
	}
}

package de.knowwe.caseTrain;

import de.d3web.we.kdom.report.KDOMWarning;

public class MissingComponentWarning extends KDOMWarning {

	private final String text;

	public MissingComponentWarning(String t) {
		this.text = t;
	}

	@Override
	public String getVerbalization() {
		return "Fehlende Komponente: " + text;
	}
}

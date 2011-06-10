package de.knowwe.compile.object;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.report.KDOMError;

public class ConcurrentDefinitionsError extends KDOMError {

	private final String text;
	private Section<? extends TermDefinition<?>> definition = null;

	public ConcurrentDefinitionsError(String text) {
		this.text = text;
	}

	public ConcurrentDefinitionsError(String text, Section<? extends TermDefinition<?>> s) {
		this(text);
		definition = s;
	}

	@Override
	public String getVerbalization() {
		String result = "Object has concurrent definitions: " + text;
		if (definition != null) {
			result += " in: " + definition.getTitle();
		}
		return result;
	}

}

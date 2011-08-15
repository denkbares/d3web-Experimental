package de.knowwe.compile.object;

import java.util.Map;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.TermDefinition;

public interface TypedTermDefinition {
	
	public Map<String, ? extends Object> getTypedTermInformation(Section<? extends TermDefinition> s);

}

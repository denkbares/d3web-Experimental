package de.knowwe.compile.object;

import java.util.Map;

import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;

public interface TypedTermDefinition {
	
	public Map<String, ? extends Object> getTypedTermInformation(Section<? extends TermDefinition> s);

}

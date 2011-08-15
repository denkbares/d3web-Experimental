package de.knowwe.compile.object;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.objects.TermReference;

public interface ComplexDefinitionWithTypeConstraints<T extends Type> extends ComplexDefinition<Type>{

	public boolean checkTypeConstraints(Section<? extends ComplexDefinition<T>> def, Section<? extends TermReference> ref);
	
	public String getProblemMessageForConstraintViolation(Section<? extends ComplexDefinition<T>> def, Section<? extends TermReference> ref);
	
}

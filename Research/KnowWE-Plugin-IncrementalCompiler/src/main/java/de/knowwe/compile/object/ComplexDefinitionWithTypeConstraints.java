package de.knowwe.compile.object;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;

public interface ComplexDefinitionWithTypeConstraints<T extends Type> extends ComplexDefinition<Type>{

	public boolean checkTypeConstraints(Section<? extends ComplexDefinition<T>> def, Section<? extends TermReference> ref);
	
	public String getProblemMessageForConstraintViolation(Section<? extends ComplexDefinition<T>> def, Section<? extends TermReference> ref);
	
}

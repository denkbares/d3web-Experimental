package de.knowwe.compile.object;

import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;

public interface ComplexDefinitionWithTypeConstraints extends ComplexDefinition {

	public boolean checkTypeConstraints(Section<? extends ComplexDefinition> def, Section<? extends SimpleReference> ref);

	public String getProblemMessageForConstraintViolation(Section<? extends ComplexDefinition> def, Section<? extends SimpleReference> ref);

}

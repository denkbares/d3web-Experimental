package de.knowwe.compile.object;

import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;

public interface ComplexDefinitionWithTypeConstraints extends ComplexDefinition {

	boolean checkTypeConstraints(Section<? extends ComplexDefinition> def, Section<? extends SimpleReference> ref);

	String getProblemMessageForConstraintViolation(Section<? extends ComplexDefinition> def, Section<? extends SimpleReference> ref);

}

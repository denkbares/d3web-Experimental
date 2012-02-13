package de.knowwe.compile.object;

import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;

public interface TypeRestrictedReference extends SimpleTerm {

	public boolean checkTypeConstraints(Section<? extends SimpleTerm> s);

	public String getMessageForConstraintViolation(Section<? extends SimpleTerm> s);
}

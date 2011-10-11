package de.knowwe.compile.object;

import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;

public interface TypeRestrictedReference {

	public boolean checkTypeConstraints(Section<? extends TermReference> s);
	
	public String getMessageForConstraintViolation(Section<? extends TermReference> s);
}

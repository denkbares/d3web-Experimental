package de.knowwe.compile.object;

import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;

public interface TypeRestrictedReference extends TermReference {

	public boolean checkTypeConstraints(Section<? extends Term> s);

	public String getMessageForConstraintViolation(Section<? extends Term> s);
}

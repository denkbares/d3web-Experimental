package de.knowwe.compile.object;

import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;

public interface TypeRestrictedReference extends TermReference {

	boolean checkTypeConstraints(Section<? extends Term> s);

	String getMessageForConstraintViolation(Section<? extends Term> s);
}

package de.knowwe.compile.object;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.TermReference;

public interface TypeRestrictedReference {

	public boolean checkTypeConstraints(Section<? extends TermReference> s);
	
	public String getMessageForConstraintViolation(Section<? extends TermReference> s);
}

package de.knowwe.compile.test;

import de.knowwe.core.compile.terminology.TerminologyExtension;


public class SubclassOfTerminologyExtension implements TerminologyExtension {

	@Override
	public String[] getTermNames() {
		return new String[]{"subclassOf"};
	}

}

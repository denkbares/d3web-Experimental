package de.d3web.we.kdom.error;

import de.d3web.we.wikiConnector.KnowWEUserContext;

public class ObjectAlreadyDefinedError extends KDOMError {
	
	private String text;
	
	public ObjectAlreadyDefinedError(String text) {
			this.text = text;
	}

	@Override
	public String getVerbalization(KnowWEUserContext usercontext) {
		return "Object already defined: "+text;
	}

}

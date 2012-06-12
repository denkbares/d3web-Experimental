package de.knowwe.rdfs.d3web;

import de.d3web.core.session.SessionFactory;
import de.knowwe.plugin.Instantiation;

public class D3webRdf2GoInstantiation implements Instantiation {

	@Override
	public void init() {
		SessionFactory.addPropagationListener(new Rdf2GoPropagationListener());
	}

}

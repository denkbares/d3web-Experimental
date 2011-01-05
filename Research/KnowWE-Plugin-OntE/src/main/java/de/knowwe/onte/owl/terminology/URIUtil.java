package de.knowwe.onte.owl.terminology;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.knowwe.termObject.OWLTermReference;

public class URIUtil {

	public static URI createURI(Section<? extends KnowWEObjectType> s) {
		
		if(s.get() instanceof OWLTermReference) {
			//(OWLTermReference(s.get())). 
		}
		
		return null;
		
	}
	
}

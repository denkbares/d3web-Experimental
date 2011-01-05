package de.knowwe.onte.owl.terminology;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.knowwe.termObject.BasicVocabularyReference;

public class URIUtil {
	
	public static final String CLASS = "Class";
	
	public static final String OBJECTPROPERTY = "ObjectProperty";
	
	public static final String THING = "Thing";
	
	public static final String TYPE = "type";
	
	public static final String SUBCLASSOF = "subClassOf";
	
	public static final String SUBPROPERTYOF = "subPropertyOf";
	
	public static final String DOMAIN = "domain";
	public static final String RANGE = "range";
	
	
	public static final String[] OBJECT_VOCABULARY = new String[] {
		CLASS, OBJECTPROPERTY, THING };
	
	public static final String[] PREDICATE_VOCABULARY = new String[] {
			TYPE, SUBCLASSOF, DOMAIN, RANGE, SUBPROPERTYOF };

	public static URI getURI(Section<? extends BasicVocabularyReference> s) {
		
		String text = s.getOriginalText();
		
		if(s.get() instanceof KnowWETerm) {
			// TODO: find solution for call
			//text = ((KnowWETerm<?>)(s.get())).getTermName((Section<? extends KnowWETerm<?>>)(s));
		}
		
		// hack TODO remove
		if (text.endsWith("::")) {
			text = text.substring(0, text.length() - 2);
		}
		
		if(text.equals(OBJECTPROPERTY)) {
			return OWL.ObjectProperty;
		} else if(text.equals(CLASS)) {
			return OWL.Class;
		} else if(text.equals(THING)) {
			return OWL.Thing;
		} else if(text.equals(TYPE)) {
			return RDF.type;
		} else if(text.equals(SUBCLASSOF)) {
			return RDFS.subClassOf;
		} else if(text.equals(DOMAIN)) {
			return RDFS.domain;
		} else if(text.equals(RANGE)) {
			return RDFS.range;
		} else if(text.equals(SUBPROPERTYOF)) {
			return RDFS.subPropertyOf;
		} 
		
		return null;
		
	}
	
}
